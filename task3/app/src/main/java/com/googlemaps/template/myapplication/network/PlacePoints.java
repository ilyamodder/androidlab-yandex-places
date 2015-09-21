package com.googlemaps.template.myapplication.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by ilya on 16.09.15.
 */
public class PlacePoints implements Parcelable {

    public List<Point> mPoints;

    public PlacePoints(List<Point> points) {
        this.mPoints = points;
    }

    public static class Point implements Parcelable {
        public Point(String name, LatLng position) {
            this.mName = name;
            this.mPosition = position;
        }

        public int mId;
        public String mName;
        public LatLng mPosition;
        public String mDescription;

        public Point(String name, LatLng position, String description, int id) {
            this.mName = name;
            this.mPosition = position;
            this.mDescription = description;
            this.mId = id;
        }

        protected Point(Parcel in) {
            mId = in.readInt();
            mName = in.readString();
            mPosition = in.readParcelable(LatLng.class.getClassLoader());
            mDescription = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mId);
            dest.writeString(mName);
            dest.writeParcelable(mPosition, flags);
            dest.writeString(mDescription);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;

            Point point = (Point) o;

            return mId == point.mId;

        }

        @Override
        public int hashCode() {
            return mId;
        }
    }

    protected PlacePoints(Parcel in) {
        mPoints = in.createTypedArrayList(Point.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPoints);
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
