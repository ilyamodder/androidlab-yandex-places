package com.googlemaps.template.myapplication.network;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

/**
 * Created by ilya on 15.09.15.
 */
public class GeocoderPlacesRequest extends RetrofitSpiceRequest<Places, PlacesApi> {

    String geocode;

    public GeocoderPlacesRequest(double longitude, double latitude) {
        super(Places.class, PlacesApi.class);
        geocode = String.valueOf(longitude) + "," + latitude;
    }

    @Override
    public Places loadDataFromNetwork() throws Exception {
        return getService().getPlaces(geocode);
    }
}
