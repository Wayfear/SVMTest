package com.example.kanxuan.svm3;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class CpuLoadService extends Service {

    String filename;
    private static final String TAG = "CPULoadService";

    public CpuLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.e(TAG, "Start Service");

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss_SSS");
        filename = sDateFormat.format(new java.util.Date());

        openFile();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BatteryManager mBatteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                while (true) {

                    Intent batteryInfoIntent = getApplicationContext()
                            .registerReceiver( null ,
                                    new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) ) ;
                    int intentvoltage = batteryInfoIntent.getIntExtra( "voltage" , 0 );
                    int avg_current = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                    int now_current = mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    int voltage = mBatteryManager.getIntProperty(BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE);

                    StringBuilder  s = new StringBuilder();
                    s.append(System.currentTimeMillis()).append(",").append(getProcessCpuRate()).append(",").append(avg_current).append(",")
                            .append(now_current).append(",").append(intentvoltage).append(",").append(voltage).append("\n");

                    try {
                        writer.write(s.toString());
                        Log.i(TAG, s.toString());
                    }
                    catch (Exception e){

                    }


                }
            }
        }).start();



        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        closeFile();
        super.onDestroy();
    }

    OutputStreamWriter writer;

    private void openFile() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "record_cpu_load/" + filename);
            Log.e("TAG", Environment.DIRECTORY_DOWNLOADS + "record_cpu_load/" + filename);
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

    public static float getProcessCpuRate()
    {

        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try
        {
            Thread.sleep(120);
        }
        catch (Exception e) {

        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();

        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);

        return cpuRate;
    }

    public static long getTotalCpuTime()
    { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long totalCpu = Long.parseLong(cpuInfos[2])
                + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        return totalCpu;
    }

    public static long getAppCpuTime()
    { // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try
        {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }

}
