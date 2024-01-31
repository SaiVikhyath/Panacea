package com.example.panacea;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class HealthMonitoring extends AppCompatActivity {
    static final int REQUEST_CAMERA_PERMISSION = 1;
    private String heartRateValue = "";
    private String respiratoryRateValue = "";
    private String bloodOxygenValue = "";
    private Button measureHeartRate;
    private Button measureRespiratoryRate;
    private Button measureBloodOxygen;
    private Button measureSleep;
    private TextView displayReading;
    private TextView timer;
    public static HeartRateDatabase heartRateDatabase;
    public static RespiratoryRateDatabase respiratoryRateDatabase;
    public static BloodOxygenDatabase bloodOxygenDatabase;
    public static SleepDatabase sleepDatabase;
    public static HeartRate heartRate = new HeartRate();
    public static RespiratoryRate respiratoryRate = new RespiratoryRate();
    public static BloodOxygen bloodOxygen = new BloodOxygen();
    public static Sleep sleep = new Sleep();
    public static MeasureHeartRate heartRateMeasurement;
    public static MeasureBloodOxygen bloodOxygenMeasurement;
    private MyBroadcastReceiver broadcastReceiver = new MyBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_monitoring);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }

        heartRateDatabase = new HeartRateDatabase(this);
        heartRateDatabase.getWritableDatabase();
        heartRateDatabase.insertRecords(heartRate);

        respiratoryRateDatabase = new RespiratoryRateDatabase(this);
        respiratoryRateDatabase.getWritableDatabase();
        respiratoryRateDatabase.insertRecords(respiratoryRate);

        bloodOxygenDatabase = new BloodOxygenDatabase(this);
        bloodOxygenDatabase.getWritableDatabase();
        bloodOxygenDatabase.insertRecords(bloodOxygen);

        sleepDatabase = new SleepDatabase(this);
        sleepDatabase.getWritableDatabase();
        sleepDatabase.insertRecords(sleep);

        displayReading = (TextView) findViewById(R.id.displayReading);

        timer = (TextView) findViewById(R.id.timer);

        measureHeartRate = (Button) findViewById(R.id.meaureHeartRate);
        measureHeartRate.setOnClickListener((v) -> {

            heartRateMeasurement = new MeasureHeartRate(HealthMonitoring.this, displayReading, timer);

            TextureView textureView = findViewById(R.id.textureView);
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();

            if (surfaceTexture != null) {
                Surface surface = new Surface(surfaceTexture);
                if (!HealthMonitoring.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Toast.makeText(HealthMonitoring.this, "Flash not turned on. Please turn the flash on", Toast.LENGTH_LONG).show();
                }
                VideoInSurface videoInSurface = new VideoInSurface(HealthMonitoring.this);
                videoInSurface.startVideo(surface);
                heartRateMeasurement.measureHeartRate(textureView, videoInSurface);
            }
        });
        registerReceiver(broadcastReceiver, new IntentFilter(heartRateValue));

        measureRespiratoryRate = (Button) findViewById(R.id.measureRespiratoryRate);
        measureRespiratoryRate.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), MeasureRespiratoryRate.class);
            startService(intent);
        });
        registerReceiver(broadcastReceiver, new IntentFilter(respiratoryRateValue));

        measureBloodOxygen = (Button) findViewById(R.id.measureBloodOxygen);
        measureBloodOxygen.setOnClickListener((v) -> {
            bloodOxygenMeasurement = new MeasureBloodOxygen(HealthMonitoring.this, displayReading, timer);

            TextureView textureView = findViewById(R.id.textureView);
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();

            if (surfaceTexture != null) {
                Surface surface = new Surface(surfaceTexture);
                if (!HealthMonitoring.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Toast.makeText(HealthMonitoring.this, "Flash not turned on. Please turn the flash on", Toast.LENGTH_LONG).show();
                }
                VideoInSurface videoInSurface = new VideoInSurface(HealthMonitoring.this);
                videoInSurface.startVideo(surface);
                bloodOxygenMeasurement.measureBloodOxygen(textureView, videoInSurface);
            }
        });
        registerReceiver(broadcastReceiver, new IntentFilter(bloodOxygenValue));

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            if(intent.getAction().equals(respiratoryRateValue)){
                float reading = intent.getFloatExtra("value",0);
                if(reading >= 25 || reading <= 12) {
                    Toast.makeText(getApplicationContext(), "Abnormal Respiratory Rate Detected!! Calling emergency services and sending SMS to emergency contacts.", Toast.LENGTH_SHORT).show();
                    String emergencyNumber = "6232009633";
                    String message = "Irregular respiratory rate detected for Vikhy. Please reach out to him immediately!!";
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emergencyNumber, null, message, null, null);
//                        Toast.makeText(getApplicationContext(),"Message sent to emergency contacts!!", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.d("SMS Error", "" + e);
                    }
                    try {
                        Intent phoneCall = new Intent(Intent.ACTION_CALL);
                        phoneCall.setData(Uri.parse("tel:" + emergencyNumber));
                        startActivity(phoneCall);
                    } catch (Exception e) {
                        Log.d("Call Error", "" + e);
                    }
                }
                int respiratoryRateReading = Math.round(reading);
                TextView respiratoryResult = (TextView) findViewById(R.id.displayReading);
                respiratoryResult.setText("Respiratory Rate:" + decimalFormat.format(respiratoryRateReading));
                respiratoryRate.respiratoryRate = respiratoryRateReading;
                boolean updated = respiratoryRateDatabase.updateRecords(respiratoryRate);
                if (!updated) {
                    Toast.makeText(getApplicationContext(), "Recorded Respiratory Rate to database", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to record Respiratory Rate to database", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}