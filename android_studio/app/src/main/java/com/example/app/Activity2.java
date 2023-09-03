package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.api.ApiServer;
import com.example.app.model.Info;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;





public class Activity2 extends AppCompatActivity {
    private ImageView receiveImage;
    private Intent intent;
    private TextView tvClass;
    private TextView tvProb;
    private Button btnLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);
        tvClass = findViewById(R.id.tv_class);
        tvProb = findViewById(R.id.tv_prob);
        btnLink = findViewById(R.id.btn_link);
        receiveImage = findViewById(R.id.receivedImg);
        intent = getIntent();

        Bitmap Camera_imageBitmap = (Bitmap) intent.getParcelableExtra("Camera_bitmap");
        Uri Upload_imageUri = (Uri) intent.getParcelableExtra("Upload_uri");

        if (Camera_imageBitmap != null){
            String Camera_imageBase64 = BitmapToBase64(Camera_imageBitmap); // Bitmap to Base64
            imgPredict(Camera_imageBase64); // 识别图像然后返回结果
            receiveImage.setImageBitmap(Camera_imageBitmap);
        }
        else {
            Bitmap Upload_imageBitmap = uriToBitmap(Upload_imageUri); // Uri to Bitmap
            String Upload_imageBase64 = BitmapToBase64(Upload_imageBitmap); // Bitmap to Base64
            imgPredict(Upload_imageBase64);
            receiveImage.setImageBitmap(Upload_imageBitmap);
        }

    }

    public Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) { e.printStackTrace(); }
        return  null;
    }
    public static String BitmapToBase64(Bitmap imageBitmap) {
        // Bitmap to Bytes
        System.out.println("width" + imageBitmap.getWidth() + "height" + imageBitmap.getHeight());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes =  stream.toByteArray();
        // Bytes to Base64
        final String imageBase64 = Base64.encodeToString(imageBytes, 0);
        return imageBase64;
    }
    private void imgPredict(String imageBase64) {
        ApiServer.apiServer.getImage(imageBase64).enqueue(new Callback<Info>() {
            @Override
            public void onResponse(Call<Info> call, Response<Info> response) {
                Toast.makeText(Activity2.this, "识别成功", Toast.LENGTH_SHORT).show();
                Info info = response.body();
                tvClass.setText(info.getClassName());
                tvProb.setText(info.getProb());
                btnLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = info.getLink();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i); }
                });
            }
            @Override
            public void onFailure(Call<Info> call, Throwable t) {
                Toast.makeText(Activity2.this, "识别失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

}



