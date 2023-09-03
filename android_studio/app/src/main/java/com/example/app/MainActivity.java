package com.example.app;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnSend;
    private Button btnUploadImg;
    private Button btnCamera;
    private Uri uri;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState); // 保存Activity的状态
        setContentView(R.layout.activity_main); //引用定义的布局
        imageView = findViewById(R.id.imageView);
        btnSend = findViewById(R.id.send);
        btnUploadImg = findViewById(R.id.uploadImg);
        btnCamera = findViewById(R.id.camera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = null;
                bitmap = null;
                Intent imgCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureForResult.launch(imgCapture);
            }
        });

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri = null;
                bitmap = null;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                pickForResult.launch(Intent.createChooser(intent, "Select Picture"));
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity2.class); //Intent实现活动跳转
                if (uri != null) {
                    intent.putExtra("Upload_uri", uri); //Intent实现数据传递
                    startActivity(intent);
                }
                else {
                    intent.putExtra("Camera_bitmap", bitmap); //Intent实现数据传递
                    startActivity(intent);
                }
            }
        });

    }
    ActivityResultLauncher<Intent> captureForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result != null) {
                        Bundle extras = result.getData().getExtras();
                        bitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
    ActivityResultLauncher<Intent> pickForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) { return; }
                        uri = data.getData();
                        imageView.setImageURI(uri);
                    }
                }
            });
}
