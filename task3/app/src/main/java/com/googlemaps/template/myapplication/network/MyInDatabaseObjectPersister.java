package com.googlemaps.template.myapplication.network;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.googlemaps.template.myapplication.database.DBHelper;
import com.googlemaps.template.myapplication.database.MyContentProvider;
import com.octo.android.robospice.persistence.ObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 20.09.15.
 */
public class MyInDatabaseObjectPersister<T> extends ObjectPersister<T> {

    private Context mContext;

    private static Map<Class<?>, Uri> classToUri;

    Class<T> mCacheClass;

    static {
        classToUri = new HashMap<>();
        classToUri.put(DrawingPoints.class, Uri.parse("content://" +
                MyContentProvider.AUTHORITY + "/" + MyContentProvider.DIRECTIONS_PATH));
        classToUri.put(PlacePoints.class, Uri.parse("content://" +
                MyContentProvider.AUTHORITY + "/" + MyContentProvider.PLACES_PATH));
    }

    public MyInDatabaseObjectPersister(Application application, Class<T> clazz) {
        super(application, clazz);
        mContext = application;
        mCacheClass = clazz;
    }

    @Override
    public T loadDataFromCache(Object cacheKey, long maxTimeInCache) throws CacheLoadingException {
        Uri uri = classToUri.get(mCacheClass);

        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        if (mCacheClass == DrawingPoints.class) {
            List<LatLng> pointsList = new ArrayList<>();

            while (cursor.moveToNext()) {
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.FIELD_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.FIELD_LONGITUDE));
                pointsList.add(new LatLng(latitude, longitude));
            }

            cursor.close();

            return (T) new DrawingPoints(pointsList);
        } else if (mCacheClass == PlacePoints.class) {
            List<PlacePoints.Point> pointsList = new ArrayList<>();

            while (cursor.moveToNext()) {
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.FIELD_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.FIELD_LONGITUDE));
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.FIELD_ID));

                String shortDescription =
                        cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_SHORT_DESCRIPTION));
                String longDescription =
                        cursor.getString(cursor.getColumnIndex(DBHelper.FIELD_LONG_DESCRIPTION));

                PlacePoints.Point point = new PlacePoints.Point(shortDescription,
                        new LatLng(latitude, longitude), longDescription, id);
                pointsList.add(point);
            }

            cursor.close();

            return (T) new PlacePoints(pointsList);
        }

        return null;
    }

    @Override
    public List<T> loadAllDataFromCache() throws CacheLoadingException {
        return new ArrayList<T>() {
            {
                add(loadDataFromCache(mCacheClass.getSimpleName(), 0));
            }
        };
    }

    @Override
    public List<Object> getAllCacheKeys() {
        return new ArrayList<Object>() {
            {
                add(mCacheClass.getSimpleName());
            }
        };
    }

    @Override
    public T saveDataToCacheAndReturnData(T data, Object cacheKey) throws CacheSavingException {

        Uri uri = classToUri.get(mCacheClass);

        removeDataFromCache(cacheKey);

        if (mCacheClass == DrawingPoints.class) {
            DrawingPoints drawingPoints = (DrawingPoints) data;

            ContentValues[] cvArr = new ContentValues[drawingPoints.points.size()];
            int i = 0;

            for (LatLng point : drawingPoints.points) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.FIELD_LATITUDE, point.latitude);
                contentValues.put(DBHelper.FIELD_LONGITUDE, point.longitude);
                cvArr[i] = contentValues;
                i++;
            }

            mContext.getContentResolver().bulkInsert(uri, cvArr);
        } else if (mCacheClass == PlacePoints.class) {
            PlacePoints placePoints = (PlacePoints) data;

            ContentValues contentValues = new ContentValues();

            for (PlacePoints.Point point : placePoints.mPoints) {
                contentValues.clear();
                contentValues.put(DBHelper.FIELD_LATITUDE, point.mPosition.latitude);
                contentValues.put(DBHelper.FIELD_LONGITUDE, point.mPosition.longitude);
                contentValues.put(DBHelper.FIELD_SHORT_DESCRIPTION, point.mName);
                contentValues.put(DBHelper.FIELD_LONG_DESCRIPTION, point.mDescription);
                Uri inserted = mContext.getContentResolver().insert(uri, contentValues);
                point.mId = Integer.parseInt(inserted.getLastPathSegment());
            }
        }

        return data;
    }

    @Override
    public boolean removeDataFromCache(Object cacheKey) {
        removeAllDataFromCache();
        return false;
    }

    @Override
    public void removeAllDataFromCache() {
        Uri uri = classToUri.get(mCacheClass);
        mContext.getContentResolver().delete(uri, null, null);
    }

    @Override
    public long getCreationDateInCache(Object cacheKey) throws CacheLoadingException {
        return 0;
    }

    @Override
    public boolean isDataInCache(Object cacheKey, long maxTimeInCacheBeforeExpiry) {
        Uri uri = classToUri.get(mCacheClass);
        Cursor cursor = mContext.getContentResolver().query(uri, new String[]{"count(*) AS count"}, null, null, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }


}
