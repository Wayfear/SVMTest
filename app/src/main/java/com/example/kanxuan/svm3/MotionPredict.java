package com.example.kanxuan.svm3;

/**
 * Created by kanxuan on 2017/4/30.
 */

public class MotionPredict {

    //    public native double predict(double[][] sensor_data);
    static {
        System.loadLibrary("Motion");
    }
}
