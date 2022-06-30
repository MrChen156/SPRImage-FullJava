package com.example.imagematrix;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ImageMatrixFactory {
    private final static String IM_FAC_TAG = "ImageMatrixFactory";
    public static ImageMatrix CreateFromBitmap(Bitmap bitmap){
        Log.i(IM_FAC_TAG, "工厂模式被调用了");
        // Bitmap和我定义的高宽相反
        int bW = bitmap.getWidth();
        int bH = bitmap.getHeight();
        ImageMatrix dst = new ImageMatrix(bH, bW, 3);
        for(int ii = 0; ii < bH; ii++){
            for (int jj = 0; jj < bW; jj++){
                int theValue = bitmap.getPixel(jj, ii);
                int RED = red(theValue);
                int GREEN = green(theValue);
                int BLUE = blue(theValue);
                dst.putValue(ii, jj, 0, RED);
                dst.putValue(ii, jj, 1, GREEN);
                dst.putValue(ii, jj, 2, BLUE);
            }
        }
        return dst;
    }
}
