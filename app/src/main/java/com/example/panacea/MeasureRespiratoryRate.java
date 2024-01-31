package com.example.panacea;


import androidx.annotation.Nullable;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MeasureRespiratoryRate extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int numberOfSamples = 128;
    List<Float> accelerometerX = new ArrayList<Float>();
    List<Float> accelerometerY = new ArrayList<Float>();
    List<Float> accelerometerZ = new ArrayList<Float>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Info", "Measure Respiratory Rate Called!!!");
        Toast.makeText(this, "Measuring respiratory rate", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Lie on you back and place the phone on your chest for 30 secs", Toast.LENGTH_LONG).show();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 100000);
        final SensorEventListener listener = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sensorManager.unregisterListener(listener);
                measureRespiratoryRate();
            }
        }, 30000);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerX.add(sensorEvent.values[0]);
            accelerometerY.add(sensorEvent.values[1]);
            accelerometerZ.add(sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void measureRespiratoryRate() {
        List<Double> squaredAcceleration = new ArrayList<>();
        List<Integer> peaks = new ArrayList<>();
        List<Double> intervals = new ArrayList<>();

//        Log.i("ACCELEROMETER INDICES", String.valueOf(accelerometerX.size()) + " " + String.valueOf(accelerometerY.size()) + " "+ String.valueOf(accelerometerZ.size()));
        for (int i = 0; i < accelerometerZ.size(); i++) {
//            Log.i("ACCELEROMETER X", String.valueOf(accelerometerX.get(i)));
//            Log.i("ACCELEROMETER Y", String.valueOf(accelerometerY.get(i)));
//            Log.i("ACCELEROMETER Z", String.valueOf(accelerometerZ.get(i)));
            squaredAcceleration.add(Math.pow(accelerometerZ.get(i), 2) + Math.pow(accelerometerY.get(i), 2) + Math.pow(accelerometerX.get(i), 2));
        }

        for (int i = 1; i < squaredAcceleration.size() - 1; i++) {
//            Log.i("SQUARED ACCELERATION", String.valueOf(squaredAcceleration.get(i)));
            if (squaredAcceleration.get(i) > squaredAcceleration.get(i - 1) && squaredAcceleration.get(i) > squaredAcceleration.get(i + 1) && squaredAcceleration.get(i) > 1) {
                peaks.add(i);
            }
        }

//        Log.i("PEAKS", String.valueOf(peaks.size()));
        for (int i = 1; i < peaks.size(); i++) {
            int previous = peaks.get(i - 1);
            int current = peaks.get(i);
//            Log.i("PEAKS", String.valueOf(previous) + " " + String.valueOf(current));
            double interval = (current - previous); // Divide with frequency
            intervals.add(interval);
        }

//        Log.i("INTERVALS", String.valueOf(intervals.size()));
        double sumOfIntervals = 0;
        for (int i = 0; i < intervals.size(); i++) {
            sumOfIntervals += intervals.get(i);
        }

        double avgIntervalTime = sumOfIntervals / intervals.size();

//        Log.i("INTERVAL INFO", String.valueOf(sumOfIntervals) + " " + String.valueOf(avgIntervalTime));

        if (avgIntervalTime != 0) {
            sendMessageToActivity((float) (45.0 / avgIntervalTime));
        } else {
            sendMessageToActivity((float) 0);
        }

        Toast.makeText(getApplicationContext(), "Respiratory Rate measurement completed", Toast.LENGTH_LONG).show();
        stopSelf();

    }

    private void sendMessageToActivity(float reading) {
        Intent intent = new Intent("");
        intent.putExtra("value", reading);
        sendBroadcast(intent);
    }

}
