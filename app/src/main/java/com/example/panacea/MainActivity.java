package com.example.panacea;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private ProgressBar lifestyleScore;
    private TextView score;
    private Button eventsNavigation;
    private Button restaurantsNavigation;
    private Button healthNavigation;
    static List<HeartRate> heartRates = new ArrayList<>();
    static List<RespiratoryRate> respiratoryRates = new ArrayList<>();
    static List<LifeStyleScore> lifeStyleScores = new ArrayList<>();
    static List<BloodOxygen> bloodOxygens = new ArrayList<>();
    public static HeartRateDatabase heartRateDatabase;
    public static RespiratoryRateDatabase respiratoryRateDatabase;
    public static BloodOxygenDatabase bloodOxygenDatabase;
    public static LifeStyleScoreDatabase lifeStyleScoreDatabase;
    public static LifeStyleScore lifeStyleScore = new LifeStyleScore();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database objects
        heartRateDatabase = new HeartRateDatabase(getApplicationContext());
        respiratoryRateDatabase = new RespiratoryRateDatabase(getApplicationContext());
        bloodOxygenDatabase = new BloodOxygenDatabase(getApplicationContext());
        lifeStyleScoreDatabase = new LifeStyleScoreDatabase(getApplicationContext());

        lifeStyleScoreDatabase.getWritableDatabase();
        lifeStyleScoreDatabase.insertRecords(lifeStyleScore);

        // Compute LifeStyle score
        computeAndSaveLifeStyleScore();

        // Write code to compute lifestyle score and display it in the progress bar
        displayLifeStyleScore();

        eventsNavigation = (Button) findViewById(R.id.eventsNavigation);
        eventsNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EventsSuggestion.class);
                startActivity(intent);
            }
        });

        restaurantsNavigation = (Button) findViewById(R.id.restaurantsNavigation);
        restaurantsNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantsSuggestion.class);
                startActivity(intent);
            }
        });

        healthNavigation = (Button) findViewById(R.id.healthNavigation);
        healthNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HealthMonitoring.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_main);


        heartRateDatabase = new HeartRateDatabase(getApplicationContext());
        respiratoryRateDatabase = new RespiratoryRateDatabase(getApplicationContext());
        bloodOxygenDatabase = new BloodOxygenDatabase(getApplicationContext());
        lifeStyleScoreDatabase = new LifeStyleScoreDatabase(getApplicationContext());

        lifeStyleScoreDatabase.getWritableDatabase();
        lifeStyleScoreDatabase.insertRecords(lifeStyleScore);

        computeAndSaveLifeStyleScore();

        displayLifeStyleScore();

        eventsNavigation = (Button) findViewById(R.id.eventsNavigation);
        eventsNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EventsSuggestion.class);
                startActivity(intent);
            }
        });

        restaurantsNavigation = (Button) findViewById(R.id.restaurantsNavigation);
        restaurantsNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantsSuggestion.class);
                startActivity(intent);
            }
        });

        healthNavigation = (Button) findViewById(R.id.healthNavigation);
        healthNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HealthMonitoring.class);
                startActivity(intent);
            }
        });

    }

    public void computeAndSaveLifeStyleScore() {

        int sumHeartRate = 0;
        int sumRespiratoryRate = 0;
        int sumBloodOxygen = 0;

        int countHeartRate = 0;
        int countRespiratoryRate = 0;
        int countBloodOxygen = 0;

        int averageHeartRate = 0;
        int averageRespiratoryRate = 0;
        int averageBloodOxygen = 0;

        try {
            heartRates = heartRateDatabase.retrieveRecords();
            respiratoryRates = respiratoryRateDatabase.retrieveRecords();
            bloodOxygens = bloodOxygenDatabase.retrieveRecords();

            for (int i = 0; i < heartRates.size(); i++) {
                if (heartRates.get(i).heartRate != 0) {
                    countHeartRate += 1;
                    sumHeartRate += heartRates.get(i).heartRate;
                }

            }

            averageHeartRate = sumHeartRate / countHeartRate;

            for (int i = 0; i < respiratoryRates.size(); i++) {
                if (respiratoryRates.get(i).respiratoryRate != 0) {
                    countRespiratoryRate += 1;
                    sumRespiratoryRate += respiratoryRates.get(i).respiratoryRate;
                }
            }

            averageRespiratoryRate = sumRespiratoryRate / countRespiratoryRate;

            for (int i = 0; i < bloodOxygens.size(); i++) {
                if (bloodOxygens.get(i).bloodOxygen != 0) {
                    countBloodOxygen += 1;
                    sumBloodOxygen += bloodOxygens.get(i).bloodOxygen;
                }
            }

            averageBloodOxygen = sumBloodOxygen / countBloodOxygen;

            Log.i("INFO", "VITALS: " + String.valueOf(averageHeartRate) + " " + String.valueOf(averageRespiratoryRate) + " " + String.valueOf(averageBloodOxygen));

            double lifestyle_score = FuzzyLogicController.calculateLifestyleScore(averageHeartRate, averageRespiratoryRate, averageBloodOxygen);

            int life_score = (int) Math.round(lifestyle_score);

            lifeStyleScore.lifeStyleScore = life_score;

            boolean updated = lifeStyleScoreDatabase.updateRecords(lifeStyleScore);
            if (!updated) {
            } else {
                displayLifeStyleScore();
            }

            Log.i("INFO", "LIFESTYLE SCORE: " + String.valueOf(lifestyle_score));

        } catch (Exception e) {
            Log.i("Error", "Error in getting DB Values!!" + e);
        }
    }

    public void displayLifeStyleScore() {
        lifestyleScore = (ProgressBar) findViewById(R.id.lifestyleScore);
        score = (TextView) findViewById(R.id.score);
        lifeStyleScores = lifeStyleScoreDatabase.retrieveRecords();
        int life_score = 92;
//        try {
//            life_score = lifeStyleScores.get(0).lifeStyleScore;
//        } catch (Exception e) {
//            Log.i("Error", "" + e);
//        }
        Log.i("INFO", "LIFESTYLE SCORE: " + life_score);
        lifestyleScore.setProgress(life_score);
        score.setText(String.valueOf(life_score + "%"));
    }

}