package com.example.imagematrix;

import android.util.Log;

public class ImageMatrix {
    private final static String IM_TAG = "ImageMatrixLog";
    private int Height;
    private int Width;
    private int ChannelNum;
    private double[][][] Values;

    // 以下为了两个类的构造函数
    public ImageMatrix(int H, int W, int C) {
        if (H > 0 && W > 0 && C > 0){
            this.Height = H;
            this.Width = W;
            this.ChannelNum = C;
            this.Values = new double[Height][Width][ChannelNum];
        } else {
            Log.e(IM_TAG, "不能构造负数矩阵");
        }
    }

    public ImageMatrix(){
        this.Height = 1;
        this.Width = 1;
        this.ChannelNum = 1;
        this.Values = new double[Height][Width][ChannelNum];
    }

    public ImageMatrix(ImageMatrix src){
        this.Height = src.getHeight();
        this.Width = src.getWidth();
        this.ChannelNum = src.getChannelNum();
        this.Values = new double[Height][Width][ChannelNum];
    }

    // 以下为私有成员访问函数
    public int getChannelNum() {
        return ChannelNum - 1;
    }

    public int getHeight() {
        return Height - 1;
    }

    public int getWidth() {
        return Width - 1;
    }

    // 矩阵值的访问函数
    public double getValue(int XX, int YY, int CC){
        if (XX < Height && YY < Width && CC < ChannelNum){
            return Values[XX][YY][CC];
        }
        else {
            Log.e(IM_TAG, "访问越界");
            return Values[0][0][0];
        }
    }

    // 矩阵值的放置函数
    public void putValue(int XX, int YY, int CC, double VV){
        if (XX < Height && YY < Width && CC < ChannelNum){
            Values[XX][YY][CC] = VV;
        }
        else {
            Log.e(IM_TAG, "存储越界");
        }
    }

    // 两个矩阵的对比函数
    public boolean isMatricsEqual(ImageMatrix src1, ImageMatrix src2){
        return (src1.getHeight() == src2.getHeight() && src1.getWidth() == src2.getWidth()
                && src1.getChannelNum() == src2.getChannelNum());
    }

    //两个矩阵的运算
    public ImageMatrix addImageMatrix(ImageMatrix src1, ImageMatrix src2){
        ImageMatrix dst = new ImageMatrix(src1);
        if (isMatricsEqual(src1, src2)){
            int X_max = src1.getHeight();
            int Y_max = src1.getWidth();
            int C_max = src2.getChannelNum();
            for(int i = 0;i <= X_max;i++){
                for(int j = 0; j <= Y_max; j++){
                    for (int cc = 0; cc <= C_max; cc++){
                        dst.putValue(i, j, cc, src1.getValue(i, j, cc) + src2.getValue(i, j, cc));
                    }
                }
            }
        } else {
            Log.e(IM_TAG, "大小不同的矩阵相加不能完成");
        }
        return dst;
    }

    public ImageMatrix minusImageMatrix(ImageMatrix src1, ImageMatrix src2){
        ImageMatrix dst = new ImageMatrix(src1);
        if (isMatricsEqual(src1, src2)){
            int X_max = src1.getHeight();
            int Y_max = src1.getWidth();
            int C_max = src2.getChannelNum();
            for(int i = 0;i <= X_max;i++){
                for(int j = 0; j <= Y_max; j++){
                    for (int cc = 0; cc <= C_max; cc++){
                        dst.putValue(i, j, cc, src1.getValue(i, j, cc) - src2.getValue(i, j, cc));
                    }
                }
            }
        } else {
            Log.e(IM_TAG, "大小不同的矩阵相减不能完成");
        }
        return dst;
    }

    // 单矩阵运算
    // 求一个channel的和
    public ImageMatrix sumElement(ImageMatrix src){
        ImageMatrix dst = new ImageMatrix(src.getHeight(), src.getWidth(), 1);
        int X_max = src.getHeight();
        int Y_max = src.getWidth();
        int C_max = src.getChannelNum();
        for(int i = 0;i <= X_max;i++){
            for(int j = 0; j <= Y_max; j++){
                double theValue = 0.0;
                for (int cc = 0; cc <= C_max; cc++){
                    theValue += src.getValue(i, j, cc);
                }
                dst.putValue(i, j, 0, theValue);
            }
        }
        return dst;
    }

    // 按channel分割Matrix
    public ImageMatrix spiltMatrix(ImageMatrix src, int N){
        if (src.getChannelNum() == 0){
            return src;
        } else if (src.getChannelNum() >= N){
            ImageMatrix dst = new ImageMatrix(src.getHeight(), src.getWidth(), N);
            return dst;
        } else {
            Log.i(IM_TAG,"维度过大已经返回最大维度");
            return spiltMatrix(src, src.getChannelNum()); // 递归调用
        }
    }

    // 做除法
    public ImageMatrix subtractMatrics(ImageMatrix src1, ImageMatrix src2){
        ImageMatrix dst = new ImageMatrix(src1);
        if (isMatricsEqual(src1, src2)){
            int X_max = src1.getHeight();
            int Y_max = src1.getWidth();
            int C_max = src2.getChannelNum();
            for(int i = 0;i <= X_max;i++){
                for(int j = 0; j <= Y_max; j++){
                    for (int cc = 0; cc <= C_max; cc++){
                        if (src2.getValue(i, j, cc)!=0)
                            dst.putValue(i, j, cc, src1.getValue(i, j, cc) / src2.getValue(i, j, cc));
                        else {
                            dst.putValue(i, j, cc, src1.getValue(i, j, cc));
                            String message = "src2位置为0已经用1计算 (" + i + ","
                                    + j + ")";
                            Log.i(IM_TAG, message);
                        }
                    }
                }
            }
        } else {
            Log.e(IM_TAG, "大小不同的矩阵相除不能完成");
        }
        return dst;
    }
}
