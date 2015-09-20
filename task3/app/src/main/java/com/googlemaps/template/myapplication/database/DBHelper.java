package com.googlemaps.template.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by ilya on 20.09.15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;

    public static final String DB_NAME = "myDB";

    public static final String TABLE_POINTS = "points";
    public static final String TABLE_DIRECTIONS = "directions";

    public static final String FIELD_ID = "_id";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_SHORT_DESCRIPTION = "short_desc";
    public static final String FIELD_LONG_DESCRIPTION = "long_desc";

    public static final String CREATE_TABLE_POINTS = "create table '" + TABLE_POINTS +
            "' (" + FIELD_ID + " integer primary key autoincrement, " + FIELD_LATITUDE +
            " double, " + FIELD_LONGITUDE + " double, " + FIELD_SHORT_DESCRIPTION +
            " text, " + FIELD_LONG_DESCRIPTION + " text);";

    public static final String CREATE_TABLE_DIRECTIONS = "create table '" + TABLE_DIRECTIONS +
            "' (" + FIELD_ID + " integer primary key autoincrement, " + FIELD_LATITUDE +
            " double, " + FIELD_LONGITUDE + " double);";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(CREATE_TABLE_POINTS);
            db.execSQL(CREATE_TABLE_DIRECTIONS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
