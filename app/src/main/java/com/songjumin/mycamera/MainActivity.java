package com.songjumin.mycamera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btnCamera;
    Button btnGallery;
    ImageView imageView;

    File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        imageView = findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.CAMERA);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            1000);
                    Toast.makeText(MainActivity.this, "카메라 권한 필요합니다", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(i.resolveActivity(MainActivity.this.getPackageManager()) != null ){

                        // 사진의 파일명을 만들기
                        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        photoFile = getPhotoFile(fileName);

                        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this,
                                "com.songjumin.mycamera.fileprovider", photoFile);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                        startActivityForResult(i, 100);


                    }else {
                        Toast.makeText(MainActivity.this, "이 폰에는 카메라 앱이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23){
                    if (checkPermission()){
                        displayFileChoose();
                    }else {
                        requestPermission();
                    }
                }
            }
        });

    }

    private void displayFileChoose() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "SELECT IMAGE"), 300);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "권한 수락이 필요합니다.", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 500);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_DENIED){
            return true;
        }else {
            return false;
        }
    }

    private File getPhotoFile(String fileName) {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            return File.createTempFile(fileName, ".jpg", storageDirectory);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000 : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "권한 허가 되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "아직 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 500: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "권한 허가 되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "아직 승인하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 100 && resultCode == RESULT_OK){
            Bitmap photo = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            imageView.setImageBitmap(photo);
        }else if (requestCode == 300 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imgPath = data.getData();
            imageView.setImageURI(imgPath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}