package com.googlemaps.template.myapplication.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class DrawingPoints implements Parcelable {
    public List<LatLng> points;

    public DrawingPoints(List<LatLng> points) {
        this.points = points;
    }

    protected DrawingPoints(Parcel in) {
        points = in.createTypedArrayList(LatLng.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(points);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrawingPoints> CREATOR = new Creator<DrawingPoints>() {
        @Override
        public DrawingPoints createFromParcel(Parcel in) {
            return new DrawingPoints(in);
        }

        @Override
        public DrawingPoints[] newArray(int size) {
            return new DrawingPoints[size];
        }
    };
}
