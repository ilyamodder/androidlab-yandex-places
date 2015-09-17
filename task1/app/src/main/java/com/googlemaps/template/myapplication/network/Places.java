package com.googlemaps.template.myapplication.network;

import android.content.ClipData;

import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class Places {
    public Response response;

    public static class Response {
        public GeoObjectCollection GeoObjectCollection;
    }

    public static class GeoObjectCollection {
        public List<Item> featureMember;
    }

    public static class Item {
        public GeoObject GeoObject;
    }

    public static class GeoObject {
        public String name;
        public Point Point;
    }

    public static class Point {
        public String pos;
    }

}
