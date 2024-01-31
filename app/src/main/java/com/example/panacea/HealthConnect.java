package com.example.panacea;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.google.android.gms.fitness.data.Session;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HealthConnect {

    private static final String TAG = "GoogleFitHealthMetrics";

    private GoogleApiClient googleApiClient;
    private Context context;

    public HealthConnect(Context context, GoogleApiClient googleApiClient) {
        this.context = context;
        this.googleApiClient = googleApiClient;
    }

    public void retrieveHealthMetrics() {
        new RetrieveDataTask().execute();
    }

    private class RetrieveDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            retrieveHeartRate();
            retrieveRespiratoryRate();
            retrieveBloodOxygen();
            retrieveSleepMetrics();
            return null;
        }
    }

    private void retrieveHeartRate() {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(googleApiClient, readRequest);

        result.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getStatus().isSuccess()) {
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                Value value = dp.getValue(field);
                                Log.d(TAG, "Heart Rate: " + value.asFloat());
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve heart rate data: " + dataReadResult.getStatus());
                }
            }
        });
    }

    private void retrieveRespiratoryRate() {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEART_POINTS)
                .setTimeRange(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(googleApiClient, readRequest);

        result.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getStatus().isSuccess()) {
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                Value value = dp.getValue(field);
                                Log.d(TAG, "Respiratory Rate: " + value.asFloat());
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve respiratory rate data: " + dataReadResult.getStatus());
                }
            }
        });
    }

    private void retrieveBloodOxygen() {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_BASAL_METABOLIC_RATE)
                .setTimeRange(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> result = Fitness.HistoryApi.readData(googleApiClient, readRequest);

        result.setResultCallback(new ResultCallback<DataReadResult>() {
            @Override
            public void onResult(DataReadResult dataReadResult) {
                if (dataReadResult.getStatus().isSuccess()) {
                    for (DataSet dataSet : dataReadResult.getDataSets()) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                Value value = dp.getValue(field);
                                Log.d(TAG, "Blood Oxygen: " + value.asFloat());
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve blood oxygen data: " + dataReadResult.getStatus());
                }
            }
        });
    }

    private void retrieveSleepMetrics() {
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                .read(DataType.TYPE_SLEEP_SEGMENT)
                .setTimeInterval(getStartTime(), getEndTime(), TimeUnit.MILLISECONDS)
                .build();

        PendingResult<SessionReadResult> result = Fitness.SessionsApi.readSession(googleApiClient, readRequest);

        result.setResultCallback(new ResultCallback<SessionReadResult>() {
            @Override
            public void onResult(SessionReadResult sessionReadResult) {
                if (sessionReadResult.getStatus().isSuccess()) {
                    for (Session session : sessionReadResult.getSessions()) {
                        Log.d(TAG, "Sleep Metrics: " + session.getName());
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve sleep metrics data: " + sessionReadResult.getStatus());
                }
            }
        });
    }

    private long getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return calendar.getTimeInMillis();
    }

    private long getEndTime() {
        return System.currentTimeMillis();
    }
}
