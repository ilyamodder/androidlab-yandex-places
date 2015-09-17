package com.googlemaps.template.myapplication.network;

import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class Directions {
    List<Route> routes;

    public static class Route {
        List<Leg> legs;
    }

    public static class Leg {
        List<Step> steps;
    }

    public static class Step {
        Polyline polyline;
    }

    public static class Polyline {
        String points;
    }
}
