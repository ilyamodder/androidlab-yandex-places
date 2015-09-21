package com.googlemaps.template.myapplication.network;

import com.google.android.gms.maps.model.LatLng;
import com.googlemaps.template.myapplication.BuildConfig;
import com.googlemaps.template.myapplication.Utils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class DirectionsRequest extends RetrofitSpiceRequest<DrawingPoints, DirectionsApi> {
    String mWaypoints;
    String mOrigin;
    String mDestination;

    public DirectionsRequest(LatLng origin, PlacePoints placePoints) {
        super(DrawingPoints.class, DirectionsApi.class);

        List<PlacePoints.Point> waypoints = new ArrayList<>(placePoints.points);

        LatLng destination = waypoints.remove(waypoints.size() - 1).position;

        this.mOrigin = origin.latitude + "," + origin.longitude;
        this.mDestination = destination.latitude + "," + destination.longitude;

        StringBuffer stringBuffer = new StringBuffer("optimize:true|");
        for (PlacePoints.Point waypoint : waypoints) {
            stringBuffer.append(waypoint.position.latitude);
            stringBuffer.append(",");
            stringBuffer.append(waypoint.position.longitude);
            stringBuffer.append("|");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        this.mWaypoints = stringBuffer.toString();
    }

    @Override
    public DrawingPoints loadDataFromNetwork() throws Exception {

        List<LatLng> points = new ArrayList<>();

        Directions directions =  getService().getDirections(mWaypoints, mOrigin, mDestination, BuildConfig.DIRECTIONS_APP_KEY);
        for (Directions.Route route : directions.routes) {
            for (Directions.Leg leg : route.legs) {
                for (Directions.Step step : leg.steps) {
                    points.addAll(Utils.decodePoly(step.polyline.points));
                }
            }
        }
        return new DrawingPoints(points);
    }
}
