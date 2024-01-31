package com.example.panacea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HeartRateDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HeartRate.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "HeartRate";
    private static final String COLUMN_ID = "ID";
    private static final String HEART_RATE = "HeartRate";

    private static final String CREATE_TABLE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            HEART_RATE + " INTEGER DEFAULT 0 " + ")";

    public HeartRateDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
            Log.i("DATABASE", "CREATED SUCCESSFULLY");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ERROR", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertRecords(HeartRate heartRate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HEART_RATE, heartRate.heartRate);
        long rowID = sqLiteDatabase.insert(TABLE_NAME, null, values);
        sqLiteDatabase.close();
        return rowID == -1;
    }

    public boolean updateRecords(HeartRate heartRate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HEART_RATE, heartRate.heartRate);
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = {String.valueOf(1)};
        int numberOfRecordsUpdated = sqLiteDatabase.update(TABLE_NAME, values, whereClause, whereArgs);
        sqLiteDatabase.close();
        return numberOfRecordsUpdated == -1;
    }

    public List<HeartRate> retrieveRecords() {
        List<HeartRate> heartRates = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] columns = {HEART_RATE};
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int heart_rate = cursor.getInt(cursor.getColumnIndex(HEART_RATE));
                HeartRate getHeartRate = new HeartRate(heart_rate);
                heartRates.add(getHeartRate);
            }
            cursor.close();
        }
        sqLiteDatabase.close();
        return heartRates;
    }

}


