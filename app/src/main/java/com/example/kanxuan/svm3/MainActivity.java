package com.example.kanxuan.svm3;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {

    private TextView mTextView;

//
//    private native void testLog(String logThis);
//   private native double predict(double[][] arr);

    public native double predict(double[][] sensor_data, String model_path);

    public static final String LOG_TAG = "AndroidLibSvm";

    String appFolderPath;
    String systemPath;

    String filename;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent cpuIntent = new Intent(this, CpuLoadService.class);
        systemPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        appFolderPath = systemPath+"libsvm/";
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                Button button = (Button)findViewById(R.id.button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try
                        {
                            writer.write(String.valueOf(System.currentTimeMillis())+"\n");
                        }
                        catch (Exception e){

                        }

                        double[][] d = {{1, 1, 1}, {2, 2, 2}};
                        MotionPredict predict = new MotionPredict();
                        String path = systemPath + "libsvm/model";
                        double a = predict(d, path);







                        try
                        {
                            writer.write(String.valueOf(System.currentTimeMillis())+"\n");
                        }
                        catch (Exception e){

                        }


                    }
                });

                Button button1 = (Button)findViewById(R.id.button1);

                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startService(cpuIntent);
                        openFile();

                    }
                });

                Button button2 = (Button)findViewById(R.id.button2);

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopService(cpuIntent);
                        closeFile();

                    }
                });
            }
        });


        // 1. create necessary folder to save model files
        CreateAppFolderIfNeed();
        copyAssetsDataIfNeed();

        // 2. assign model/output paths


//        testLog("Test");

        // 3. make SVM train

    }

    private void CreateAppFolderIfNeed(){
        // 1. create app folder if necessary
        File folder = new File(appFolderPath);

        if (!folder.exists()) {
            folder.mkdir();
            Log.d(LOG_TAG,"Appfolder is not existed, create one");
        } else {
            Log.w(LOG_TAG,"WARN: Appfolder has not been deleted");
        }


    }

    private void copyAssetsDataIfNeed(){
        String assetsToCopy[] = {"heart_scale_predict","heart_scale_train","heart_scale"};
        for(int i=0; i<assetsToCopy.length; i++){
            String from = assetsToCopy[i];
            String to = appFolderPath+from;

            // 1. check if file exist
            File file = new File(to);
            if(file.exists()){
                Log.d(LOG_TAG, "copyAssetsDataIfNeed: file exist, no need to copy:"+from);
            } else {
                // do copy
                boolean copyResult = copyAsset(getAssets(), from, to);
                Log.d(LOG_TAG, "copyAssetsDataIfNeed: copy result = "+copyResult+" of file = "+from);
            }
        }
    }

    private boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "[ERROR]: copyAsset: unable to copy file = "+fromAssetPath);
            return false;
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    OutputStreamWriter writer;

    private void openFile() {
        try {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS");
            filename = sDateFormat.format(new java.util.Date());
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "record_cpu_time/" + filename);
            file.getParentFile().mkdirs();
            writer = new OutputStreamWriter(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeFile() {
        if (writer==null)
            return;
        try {
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        writer = null;
        filename=null;
    }





    static {
        System.loadLibrary("motion");
    }

}
