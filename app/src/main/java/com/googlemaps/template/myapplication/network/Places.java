package com.googlemaps.template.myapplication.network;

import android.content.ClipData;

import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class Places {
    public Response response;

    static class Response {
        public GeoObjectCollection GeoObjectCollection;
    }

    static class GeoObjectCollection {
        public List<Item> featureMember;
    }

    static class Item {
        public GeoObject GeoObject;
    }

    static class GeoObject {
        public String name;
        public Point Point;
    }

    static class Point {
        public String pos;
    }

}
