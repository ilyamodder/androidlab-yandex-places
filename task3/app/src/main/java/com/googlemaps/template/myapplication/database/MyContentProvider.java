package com.googlemaps.template.myapplication.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by ilya on 20.09.15.
 */
public class MyContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.googlemaps.template.myapplication.cachedata";
    public static final String DIRECTIONS_PATH = "directions";
    public static final String PLACES_PATH = "places";

    public static final int URI_DIRECTIONS = 0;
    public static final int URI_PLACES = 1;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, DIRECTIONS_PATH, URI_DIRECTIONS);
        uriMatcher.addURI(AUTHORITY, PLACES_PATH, URI_PLACES);
    }

    static final String DIRECTIONS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + DIRECTIONS_PATH;

    static final String PLACES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PLACES_PATH;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        mDBHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mDb = mDBHelper.getReadableDatabase();

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_DIRECTIONS:
                table = DBHelper.TABLE_DIRECTIONS;
                break;
            case URI_PLACES:
                table = DBHelper.TABLE_POINTS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri.toString());

        }

        Cursor cursor = mDb.query(table, projection, selection, selectionArgs, null,
                null, sortOrder);

        mDb.close();

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_DIRECTIONS:
                return DIRECTIONS_CONTENT_TYPE;
            case URI_PLACES:
                return PLACES_CONTENT_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mDBHelper.getWritableDatabase();

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_DIRECTIONS:
                table = DBHelper.TABLE_DIRECTIONS;
                break;
            case URI_PLACES:
                table = DBHelper.TABLE_POINTS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri.toString());

        }

        long rowId = mDb.insert(table, null, values);

        mDb.close();

        return ContentUris.withAppendedId(uri, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        mDb = mDBHelper.getWritableDatabase();

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_DIRECTIONS:
                table = DBHelper.TABLE_DIRECTIONS;
                break;
            case URI_PLACES:
                table = DBHelper.TABLE_POINTS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri.toString());

        }

        int count =  mDb.delete(table, selection, selectionArgs);
        mDb.close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        mDb = mDBHelper.getWritableDatabase();

        String table;

        switch (uriMatcher.match(uri)) {
            case URI_DIRECTIONS:
                table = DBHelper.TABLE_DIRECTIONS;
                break;
            case URI_PLACES:
                table = DBHelper.TABLE_POINTS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri.toString());

        }

        mDb = mDBHelper.getWritableDatabase();

        int count = mDb.update(table, values, selection, selectionArgs);
        mDb.close();
        return count;
    }
}
