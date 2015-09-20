package com.googlemaps.template.myapplication.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by ilya on 16.09.15.
 */
public class PlacePoints implements Parcelable {

    public List<Point> points;

    public PlacePoints(List<Point> points) {
        this.points = points;
    }

    public static class Point implements Parcelable {
        public Point(String name, LatLng position) {
            this.name = name;
            this.position = position;
        }

        public int id;
        public String name;
        public LatLng position;
        public String description;

        public Point(String name, LatLng position, String description, int id) {
            this.name = name;
            this.position = position;
            this.description = description;
            this.id = id;
        }

        protected Point(Parcel in) {
            name = in.readString();
            position = in.readParcelable(LatLng.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeParcelable(position, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Point> CREATOR = new Creator<Point>() {
            @Override
            public Point createFromParcel(Parcel in) {
                return new Point(in);
            }

            @Override
            public Point[] newArray(int size) {
                return new Point[size];
            }
        };
    }

    protected PlacePoints(Parcel in) {
        points = in.createTypedArrayList(Point.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(points);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlacePoints> CREATOR = new Creator<PlacePoints>() {
        @Override
        public PlacePoints createFromParcel(Parcel in) {
            return new PlacePoints(in);
        }

        @Override
        public PlacePoints[] newArray(int size) {
            return new PlacePoints[size];
        }
    };
}
