package com.googlemaps.template.myapplication.network;

import com.google.android.gms.maps.model.LatLng;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class GeocoderPlacesRequest extends RetrofitSpiceRequest<PlacePoints, PlacesApi> {

    String geocode;

    public GeocoderPlacesRequest(double longitude, double latitude) {
        super(PlacePoints.class, PlacesApi.class);
        geocode = String.valueOf(longitude) + "," + latitude;
    }

    @Override
    public PlacePoints loadDataFromNetwork() throws Exception {
        Places places =  getService().getPlaces(geocode);

        List<PlacePoints.Point> points = new ArrayList<>();

        for (Places.Item item : places.response.GeoObjectCollection.featureMember) {
            String name = item.GeoObject.name;
            String[] pos = item.GeoObject.Point.pos.split(" ");

            LatLng position = new LatLng(Double.valueOf(pos[1]), Double.valueOf(pos[0]));
            points.add(new PlacePoints.Point(name, position));
        }

        return new PlacePoints(points);
    }
}
