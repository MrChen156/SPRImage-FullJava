package com.example.sprimage;

import androidx.appcompat.app.AppCompatActivity;

// import android.nfc.Tag;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Matrix;

import org.opencv.android.OpenCVLoader;

import java.io.FileNotFoundException;

// import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int PICK_CURRENT = 12;
    private final int PICK_REFERENCE = 13;
    ImageView referenceView;
    ImageView currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 监听按钮和图片元素
        Button button_ref = (Button) findViewById(R.id.buttonRef);
        Button button_cur = (Button) findViewById(R.id.buttonCur);
        referenceView = findViewById(R.id.ReferenceImage);
        currentView = findViewById(R.id.CurrentImage);


        // 加载Opencv库
        String message = iniLoadOpenCV();//加载OpenCV的本地库
        TextView textView = findViewById(R.id.text_view);
        textView.setText(message);

        // 设置按钮方法
        button_ref.setOnClickListener(this);
        button_cur.setOnClickListener(this);
    }

    // 设置按钮反应
    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.buttonCur) {
            selectCurrentImage();
        } else if (id == R.id.buttonRef) {
            selectRefImage();
        }
    }

    private String iniLoadOpenCV(){
        boolean success = OpenCVLoader.initDebug();
        if(success){
            String CV_TAG = "OpenCVLibrary";
            Log.i(CV_TAG,"OpenCV Libraries loaded...");
            return "Opencv is OK";
        }else{
            Toast.makeText(this.getApplicationContext(), "WARNING: Could not load OpenCV Libraries!", Toast.LENGTH_LONG).show();
            return "Opencv is done";
        }
    }

    // 选择目标图片
    private void selectCurrentImage(){
        applyAndOpenAlbum(PICK_CURRENT);
    }

    // 选择基准图片
    private void selectRefImage(){
        applyAndOpenAlbum(PICK_REFERENCE);
    }

    // 访问相册
    private void applyAndOpenAlbum(int Flag){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Flag);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // compare the resultCode with the
                // SELECT_PICTURE constant
//                if (requestCode == PICK_CURRENT) {
//                    // update the preview image in the layout
////                    currentView.setImageURI(selectedImageUri);
//                    CurImagePath = selectedImageUri;
//                } else if (requestCode == PICK_REFERENCE) {
//                    // update the preview image in the layout
////                    referenceView.setImageURI(selectedImageUri);
//                    RefImagePath = selectedImageUri;
//                }
                // 读取文件
                try {
                    rotateAndShowImage(requestCode, selectedImageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 旋转并展示图片
    private void rotateAndShowImage(int Flag, Uri imagePath) throws FileNotFoundException {
        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath));
        int ImageH = image.getHeight();
        int ImageW = image.getWidth();
        int scaleFactor = ImageH / ImageW;
        if (scaleFactor > 1.0) { // 如果高小于宽
            Bitmap rotatedImage = rotateImage(image);
            if (Flag == PICK_CURRENT) {
                currentView.setImageBitmap(rotatedImage);
            } else if (Flag == PICK_REFERENCE) {
                referenceView.setImageBitmap(rotatedImage);
            }
        } else {
            if (Flag == PICK_CURRENT) {
                currentView.setImageBitmap(image);
            } else if (Flag == PICK_REFERENCE) {
                referenceView.setImageBitmap(image);
            }
        }
    }

    // 旋转图片
    private Bitmap rotateImage(Bitmap src){
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // 旋转90度
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

}









