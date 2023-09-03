import os
from flask import Flask, render_template, request, jsonify
from predictor import imagePredict

app = Flask(__name__)

predictor = imagePredict(model_path='models')

ALLOWED_EXT = set(['jpg', 'jpeg', 'png', "JPG", "JPEG", "PNG"])
def allowed_file(filename):
    return '.' in filename and \
            filename.rsplit('.', 1)[1] in ALLOWED_EXT

# 通过python装饰器的方法定义路由地址
@app.route('/')
# 用jinjia2引擎来渲染页面，并返回一个index.html页面
def home():
    return render_template("index.html")

# app的路由地址"/success"即为定义的url地址，采用POST方法均可提交
@app.route('/success', methods=['POST'])
def success():
    if request.method == 'POST':
        file = request.files['file'] # <FileStorage: '' ('image/jpeg')>
        if file and allowed_file(file.filename):
            img_name = file.filename
            target_folder = os.path.join(os.getcwd(), 'static/images')
            img_path = os.path.join(target_folder, img_name)
            file.save(img_path) # Save file to static/images folder
            img = predictor.browser_IMGprocessing(img_path) # File -> multi-dimensional array
            results = predictor.imgPredict(img)
            predictions = { "class_name":results[0], "prob":results[1], "baike_link":results[2] }
            return render_template('success.html', img=img_name, predictions=predictions)
        else:
            error = "请仅上传“jpg” 、“ jpeg”和“png”格式的照片"
            return render_template('index.html', error=error)
    else:
        return render_template('index.html')

# app的路由地址"/getImage"即为定义的url地址，采用POST方法均可提交
@app.route('/getImage', methods=['POST'])
def getimage():
    if request.method == 'POST':
        image = request.form.get('file') # String base64
        img = predictor.android_IMGprocessing(image) # base64 -> multi-dimensional array
        results = predictor.imgPredict(img)
        return jsonify({'class_name': results[0], 'prob': results[1], 'baike_link': results[2]}) # 返回值均为json格式
    return ('', 204)

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True)
