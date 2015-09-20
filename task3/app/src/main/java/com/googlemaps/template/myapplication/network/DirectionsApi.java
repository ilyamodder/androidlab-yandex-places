package com.googlemaps.template.myapplication.network;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ilya on 15.09.15.
 */
public interface DirectionsApi {
    @GET("/json?sensor=false")
    Directions getDirections(@Query("waypoints") String waypoints, @Query("origin") String origin,
                             @Query("destination") String destination, @Query("key") String appKey);
}
