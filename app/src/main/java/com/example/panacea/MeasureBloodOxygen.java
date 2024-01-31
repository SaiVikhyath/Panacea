package com.example.panacea;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeasureBloodOxygen {

    Context context;
    private TextView oxygenLevel;
    private TextView oxygenTime;
    private CountDownTimer countDown;
    private final int timePeriod = 35000;
    private final int timeInterval = 50;
    private final int initialWaitTime = 5000;
    private int timerValue;
    private int count = 0;
    private int detectedDips = 0;
    private final CopyOnWriteArrayList<Long> valleys = new CopyOnWriteArrayList<>();
    private ComputePixels computePixels;

    public MeasureBloodOxygen(Context context, TextView displayOxygenLevel, TextView oxygenTime) {
        this.context = context;
        this.oxygenLevel = displayOxygenLevel;
        this.oxygenTime = oxygenTime;
    }

    private boolean detectDip() {
        // Add your logic to detect dips
        // This is a simplified example and may need adjustment based on data characteristics
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

    void measureBloodOxygen(TextureView textureView, VideoInSurface videoInSurface) {
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
                        oxygenTime.setText("Place your finger on the flash. Recording starts in  " + String.valueOf((int) ((initialWaitTime - count * timeInterval) / 1000)) + " secs");
                    }
                    return;
                }
                if ((count * timeInterval) % 1000 == 0) {
                    oxygenTime.setText("Remaining : " + String.valueOf(timerValue--) + " secs");
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
                if (valleys.size() == 0) {
                    System.out.println("Place the finger properly");
                    Toast.makeText(context, "Finger not placed properly. Unable to measure blood oxygen level", Toast.LENGTH_LONG).show();
                    return;
                }
                float oxygen = 60f * (detectedDips - 1) / (Math.max(1, (valleys.get(valleys.size() - 1) - valleys.get(0)) / 1000f));
                int oxygenLevelReading = Math.round(oxygen);
                if (oxygenLevelReading > 100) {
                    oxygenLevelReading = 100 - (oxygenLevelReading - 100);
                }
                if(oxygenLevelReading < 85) {
                    Toast.makeText(context.getApplicationContext(), "Abnormal Blood Oxygen Detected!! Calling emergency services and sending SMS to emergency contacts.", Toast.LENGTH_SHORT).show();
                    String emergencyNumber = "6232009633";
                    String message = "Abnormal blood oxygen detected for Vikhy. Please reach out to him immediately!!";
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
                HealthMonitoring.bloodOxygen.bloodOxygen = oxygenLevelReading;
                boolean updated = HealthMonitoring.bloodOxygenDatabase.updateRecords(HealthMonitoring.bloodOxygen);
                if (!updated) {
                    Toast.makeText(context.getApplicationContext(), "Recorded Blood Oxygen to database", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Unable to record Blood Oxygen to database", Toast.LENGTH_LONG).show();
                }
                oxygenLevel.setText("Blood Oxygen Level:" + Integer.toString(Integer.parseInt(decimalFormat.format(oxygenLevelReading))));
                oxygenTime.setText("");
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
