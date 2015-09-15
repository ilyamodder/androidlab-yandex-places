package com.googlemaps.template.myapplication.network;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ilya on 15.09.15.
 */
public interface Api {
    @GET("/?format=json&kind=house")
    Places getPlaces(@Query("geocode") String geocode);
}
