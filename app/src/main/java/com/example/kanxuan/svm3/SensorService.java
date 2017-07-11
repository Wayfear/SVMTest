package com.example.kanxuan.svm3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.example.kanxuan.svm3.libsvm.svm;
import com.example.kanxuan.svm3.libsvm.svm_model;
import com.example.kanxuan.svm3.libsvm.svm_node;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SensorService extends Service implements SensorEventListener{

    SensorManager sensorManager;

    private static final String TAG = "SC: SensorService";

    public native double[] get_feature(double[][] arr);
    public native double predict(double[] arr, String model_path);
//    public native double predict(double[][] arr, String model_path);


    private String act;

    private svm_model model;

    String appFolderPath;
    public SensorService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        data = new double[3][200];
        tempData = new ArrayList<>();
        Log.e(TAG,"Start service");
        appFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() +"/libsvm/";

    }

    double[][] data;

    ArrayList<double[]> tempData;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            double[] temp = {event.values[0], event.values[1], event.values[2]};
            tempData.add(temp);
        }

        if(tempData.size()==200) {
            for(int i=0;i<3;i++) {
                for(int j=0;j<200;j++)
                {
                    data[i][j] = tempData.get(j)[i];
                }
            }
//            Log.i(TAG, "1   "+String.valueOf(System.currentTimeMillis()));
            String path = appFolderPath + "model";
//            Log.i(TAG, "1   "+String.valueOf(System.currentTimeMillis()));
            double[] res = get_feature(data);
            StringBuilder sb = new StringBuilder();
//            for(int i=0;i<15;i++){
//                sb.append(res[i]).append(",");
//            }
//            System.out.println(res + "||" + path);
//             double[] res = new double[18];
//            for (int i = 0;i < 18;i++) {
//                res[i] = 1;
//            }
//            double a = predict(res, path);

//            try {
//                model = svm.svm_load_model(path);
//;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            svm_node[] data = new svm_node[19];
//            for (int i = 0;i < 18;i++) {
//                data[i] = new svm_node();
//                data[i].index = i+1;
//                data[i].value = 1.0;
//            }
//            data[18] = new svm_node();
//            data[18].index = -1;
//            svm.svm_predict(model, data);
//            Log.e(TAG, "==========");
//            double a = predict(res, path);
//            Log.i(TAG, "2   "+sb.toString());


            int n = 200;
            while (n--!=0) {
                tempData.remove(0);
            }


        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"bind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int re = super.onStartCommand(intent, flags, startId);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 5000);
        return re;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);

    }

    static {
        System.loadLibrary("test");
//        System.loadLibrary("motion");
    }
}
