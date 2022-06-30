package com.example.imagematrix;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

import android.graphics.Bitmap;

public class ImageMatrixFactory {

    public static ImageMatrix CreateFromBitmap(Bitmap bitmap){
        int bH = bitmap.getHeight();
        int bW = bitmap.getWidth();
        ImageMatrix dst = new ImageMatrix(bH, bW, 3);
        for(int ii = 0; ii <= bH; ii++){
            for (int jj = 0; jj <= bW; jj++){
                int theValue = bitmap.getPixel(ii, jj);
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
