package com.example.panacea;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeasureHeartRate {

    Context context;
    private TextView heartRate;
    private TextView heartRateTime;
    private CountDownTimer countDown;
    private final int timePeriod = 35000;
    private final int timeInterval = 50;
    private final int initialWaitTime = 5000;
    private int timerValue;
    private int count = 0;
    private int detectedDips = 0;
    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();
    private ComputePixels computePixels;

    public MeasureHeartRate(Context context, TextView displayHeartRate, TextView heartRateTime) {
        this.context = context;
        this.heartRate = displayHeartRate;
        this.heartRateTime = heartRateTime;
    }

    private boolean detectDip() {
        final int windowSize = 15;
        CopyOnWriteArrayList<Pixels<Integer>> subList = computePixels.getFinalStandardDeviations(windowSize);
        if (subList.size() < windowSize) {
            return false;
        } else {
            Integer reference = subList.get((int) Math.ceil(windowSize / 2f)).reading;
            for (Pixels<Integer> measurement : subList) {
                if (measurement.reading < reference) return false;
            }
            return (!subList.get((int) Math.ceil(windowSize / 2f)).reading.equals(subList.get((int) Math.ceil(windowSize / 2f) - 1).reading));
        }
    }

    void measureHeartRate(TextureView textureView, VideoInSurface videoInSurface) {

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.SEND_SMS}, 1);
        }

        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        computePixels = new ComputePixels();
        detectedDips = 0;
        timerValue = (int) (timePeriod - initialWaitTime) / 1000;
        count = 0;
        countDown = new CountDownTimer(timePeriod, timeInterval) {
            @Override
            public void onTick(long l) {
                count++;
                if (initialWaitTime > count * timeInterval) {
                    if ((count * timeInterval) % 1000 == 0) {
                        heartRateTime.setText("Place your finger on the flash. Recording starts in  " + String.valueOf((int) ((initialWaitTime - count * timeInterval) / 1000)) + " secs");
                    }
                    return;
                }
                if ((count * timeInterval) % 1000 == 0) {
                    heartRateTime.setText("Remaining : " + String.valueOf(timerValue--) + " secs");
                }
                Thread thread = new Thread(() -> {
                    Bitmap bitMap = textureView.getBitmap();
                    int bitMapWidth = textureView.getWidth();
                    int bitMapHeight = textureView.getHeight();
                    int numberOfPixels = bitMapWidth * bitMapHeight;
                    int reading = 0;
                    int[] pixels = new int[numberOfPixels];
                    bitMap.getPixels(pixels, 0, bitMapWidth, 0, 0, bitMapWidth, bitMapHeight);
                    for (int i = 0; i < numberOfPixels; i++) {
                        reading += (pixels[i] >> 16) & 0xff;
                    }
                    computePixels.add(reading);
                    if (detectDip()) {
                        detectedDips = detectedDips + 1;
                        valleys.add(computePixels.getLastTimestamp().getTime());
                    }
                });
                thread.start();
            }

            @Override
            public void onFinish() {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                CopyOnWriteArrayList<Pixels<Float>> standardDeviations = computePixels.getStandardDeviations();
                if (valleys.size() == 0) {
                    System.out.println("Place the finger properly");
                    Toast.makeText(context, "Finger not placed properly. Unable to measure the heart rate", Toast.LENGTH_LONG).show();
                    return;
                }
                float pulse = 60f * (detectedDips - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f));
                if(pulse <= 60 || pulse >= 100) {
                    Toast.makeText(context.getApplicationContext(), "Abnormal Heart Rate Detected!! Calling emergency services and sending SMS to emergency contacts.", Toast.LENGTH_SHORT).show();
                    String emergencyNumber = "6232009633";
                    String message = "Irregular heart rate detected for Vikhy. Please reach out to him immediately!!";
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emergencyNumber, null, message, null, null);
                        Toast.makeText(context.getApplicationContext(),"Message sent to emergency contacts!!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("SMS Error", "" + e);
                    }
                    try {
                        Intent phoneCall = new Intent(Intent.ACTION_CALL);
                        phoneCall.setData(Uri.parse("tel:" + emergencyNumber));
                        context.startActivity(phoneCall);
                    } catch (Exception e) {
                        Log.d("Call Error", "" + e);
                    }
                }
                int pulseReading = Math.round(pulse);
                HealthMonitoring.heartRate.heartRate = pulseReading;
                boolean updated = HealthMonitoring.heartRateDatabase.updateRecords(HealthMonitoring.heartRate);
                if (!updated) {
                    Toast.makeText(context.getApplicationContext(), "Recorded Heart Rate to database", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Unable to record Heart Rate to database", Toast.LENGTH_LONG).show();
                }
                videoInSurface.stopCamera();
                HealthMonitoring.heartRateMeasurement.stopMeasuring();
                heartRate.setText("Heart Rate:"+Integer.toString(Integer.parseInt(decimalFormat.format(pulseReading))));
                heartRateTime.setText("");
            }
        };
        countDown.start();
    }

    void stopMeasuring() {
        if (countDown != null) {
            countDown.cancel();
        }
    }
}


