import os

import base64
import io
import imageio
import numpy as np
import tensorflow as tf

from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing.image import load_img, img_to_array
from tensorflow.keras.applications import vgg16

class imagePredict(object):
    def __init__(self, model_path, width = 160, height = 160, channels = 3):
        self.channels = channels
        self.width = width
        self.height = height
        self.model = load_model(os.path.join(model_path,'./vgg16_acc_91.h5'),compile=False)
        with open('label_list_zh.txt', 'r', encoding='utf-8') as f:
            lines = f.readlines()
            self.labels = [line.replace('\n', '') for line in lines]
        print(f'模型初始化完成!!!!')

    def browser_IMGprocessing(self, img_path):
        img = load_img(img_path, target_size=(self.height, self.width))  # Changed img_size required by model
        img = img_to_array(img, dtype = "float32") # PIL.Image to array
        img = np.expand_dims(img, axis=0) # Model required
        return img

    def android_IMGprocessing(self, b64_img):
        image_string = base64.b64decode(b64_img) # Base64-decode a String
        img = imageio.v3.imread(io.BytesIO(image_string)) # String to array
        img = tf.cast(img, dtype="float32")
        img = tf.image.resize(img, size=(self.height, self.width), method='nearest') # Changed img_size required by model
        img = tf.expand_dims(img, axis=0) # Model required
        return img

    def imgPredict(self, img):
        img = vgg16.preprocess_input(img) # Pretrained preprocessing
        pred = self.model.predict(img) # return a matrix shape(1,num_class)
        pred = pred[0] # Model used softmax -> Sum of pred == 1
        index = np.argmax(pred) # Finding the class with the largest predicted probability
        score = (pred[index] * 100.0)
        print("准确度: ", score)
        results = []
        if score >= 70: #threshold
            label = self.labels[index]
            url = 'https://baike.baidu.com/item/'
            link = url + label
            results.append(label), results.append(str(round(score, 2))+"%"), results.append(link)
        else:
            label = "不能分类"
            score = 0
            link = ''
            results.append(label), results.append(str(round(score, 2))+"%"), results.append(link)
        return results



