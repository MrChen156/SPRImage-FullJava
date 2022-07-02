package com.example.sprimage;

import androidx.appcompat.app.AppCompatActivity;

import com.example.imagematrix.ImageMatrix;
import com.example.imagematrix.ImageMatrixFactory;

// import android.nfc.Tag;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
// import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Matrix;

import org.opencv.android.OpenCVLoader;
//import org.opencv.android.Utils;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;

import java.io.FileNotFoundException;

// import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int PICK_CURRENT = 12;
    private final int PICK_REFERENCE = 13;
    // 下面两个布尔变量用来判断有没有选好图像以防止错误计算
    private boolean CURRENT_IMAGE_UNPICK = true;
    private boolean REFERENCE_IMAGE_UNPICK = true;
    // 下面两个Bitmap用来存储文件
    private Bitmap currentImage;
    private Bitmap referenceImage;
    // 用来报错的Tag
    public final static String TAG = "SPR-Image";
    ImageView referenceView;
    ImageView currentView;
    TextView textView;
    String initMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 监听按钮和图片元素
        Button button_ref = (Button) findViewById(R.id.buttonRef);
        Button button_cur = (Button) findViewById(R.id.buttonCur);
        Button button_compute = (Button) findViewById(R.id.buttonCompute);
        referenceView = findViewById(R.id.ReferenceImage);
        currentView = findViewById(R.id.CurrentImage);


        // 加载Opencv库
        initMessage = iniLoadOpenCV();//加载OpenCV的本地库
        textView = findViewById(R.id.text_view);
        textView.setText(initMessage);

        // 设置按钮方法
        button_ref.setOnClickListener(this);
        button_cur.setOnClickListener(this);
        button_compute.setOnClickListener(this);
    }

    // 设置按钮反应
    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.buttonCur) {
            selectCurrentImage();
        } else if (id == R.id.buttonRef) {
            selectRefImage();
        } else if (id == R.id.buttonCompute) {
            computeOutput();
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
            } else { // 当取图片失败之后防止错误计算
                if (requestCode == PICK_CURRENT) CURRENT_IMAGE_UNPICK = true;
                if (requestCode == PICK_REFERENCE) REFERENCE_IMAGE_UNPICK = true;
            }
        }
    }

    // 旋转并展示图片
    private void rotateAndShowImage(int Flag, Uri imagePath) throws FileNotFoundException {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath),null,option);
        int ImageH = image.getHeight();
        int ImageW = image.getWidth();
        int scaleFactor = ImageH / ImageW;
        if (scaleFactor > 1.0) { // 如果高小于宽
            Bitmap rotatedImage = rotateImage(image);
            if (Flag == PICK_CURRENT) {
                currentView.setImageBitmap(rotatedImage);
                currentImage = rotatedImage;
                CURRENT_IMAGE_UNPICK = false; // 图片不为空，可以计算
            } else if (Flag == PICK_REFERENCE) {
                referenceView.setImageBitmap(rotatedImage);
                referenceImage = rotatedImage;
                REFERENCE_IMAGE_UNPICK = false;
            }
        } else {
            if (Flag == PICK_CURRENT) {
                currentView.setImageBitmap(image);
                currentImage = image;
                CURRENT_IMAGE_UNPICK = false;
            } else if (Flag == PICK_REFERENCE) {
                referenceView.setImageBitmap(image);
                referenceImage = image;
                REFERENCE_IMAGE_UNPICK = false;
            }
        }
    }

    // 旋转图片
    private Bitmap rotateImage(Bitmap src){
        Matrix matrix = new Matrix();
        matrix.postRotate(90); // 旋转90度
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    // 进行计算并把结果放到textView里面
    private void computeOutput() {
        if (CURRENT_IMAGE_UNPICK && REFERENCE_IMAGE_UNPICK) { // 只有两个同时为false才表明计算可以继续
            String message =  "Images Unpicked!";
            textView.setText(message);
        } else if (isBitmapSame()) {
            Log.i(TAG,"准备计算了");
            ImageMatrix curMat = ImageMatrixFactory.CreateFromBitmap(currentImage);
            ImageMatrix refMat = ImageMatrixFactory.CreateFromBitmap(referenceImage);
            Log.i(TAG,"完成计算准备了");
            double outputNum = computeFunc(curMat, refMat);
            String outputMessage = Double.toString(outputNum);
            textView.setText(outputMessage);
        } else if (!isBitmapSame()) {
            String unMatchMessage = "sizes unmatched";
            textView.setText(unMatchMessage);
            CURRENT_IMAGE_UNPICK = true;
            REFERENCE_IMAGE_UNPICK = true;
        }
    }

    // 具体计算函数
    private double computeFunc(ImageMatrix current, ImageMatrix reference){
        Log.i(TAG,"马上调用计算了");
        ImageMatrix dst = new ImageMatrix(current);
//        ImageMatrix dst_red = new ImageMatrix(current.getHeight(), current.getWidth(),1);
//        ImageMatrix dst_sum = new ImageMatrix(current.getHeight(), current.getWidth(),1);
        dst = current.minusImageMatrix(reference);
        ImageMatrix dst_red = dst.spiltMatrix(0); // get Red Channel
        ImageMatrix dst_sum = dst.sumElement();
        ImageMatrix dst_final = dst_red.subtractMatrics(dst_sum);
        return dst_final.MatrixSum();
    }

    // 把求差结果每个像素之和相加
//    private Mat getSumFromDST(Mat dst){
//        Log.i(TAG, "getSumFromDST is going to be used.");
//        Mat sum = new Mat(dst.size(), CvType.CV_32FC1);
//        return sum;
//    }

//    // 测试用的函数：展示相减图
//    private void showDST_test(Mat dst_test) {
//        String message = "showDST准备调用";
//        textView.setText(message);
//        Bitmap image_test = currentImage;
//        Utils.matToBitmap(dst_test, image_test);
////        currentView.setImageBitmap(image_test);
//    }

    // 比较两个图片的大小
    private boolean isBitmapSame() {
       boolean result = currentImage.getHeight() == referenceImage.getHeight() &&
        currentImage.getWidth() == referenceImage.getWidth();
       if (result) { // 如果两个图片一样大就可以重新继续了
           CURRENT_IMAGE_UNPICK = false;
           REFERENCE_IMAGE_UNPICK = false;
       }
       return result;
    }
}









