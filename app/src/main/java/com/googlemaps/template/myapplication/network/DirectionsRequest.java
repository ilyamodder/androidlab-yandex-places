package com.googlemaps.template.myapplication.network;

import com.google.android.gms.maps.model.LatLng;
import com.googlemaps.template.myapplication.Utils;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilya on 15.09.15.
 */
public class DirectionsRequest extends RetrofitSpiceRequest<DrawingPoints, DirectionsApi> {
    String waypoints;
    String origin;
    String destination;
    String appKey = "AIzaSyD3GCtchyQ3zJ33efR9iB39LLZtXSRqSOQ";

    public DirectionsRequest(LatLng origin, List<LatLng> waypoints) {
        super(DrawingPoints.class, DirectionsApi.class);

        LatLng destination = waypoints.remove(waypoints.size() - 1);

        this.origin = origin.latitude + "," + origin.longitude;
        this.destination = destination.latitude + "," + destination.longitude;

        StringBuffer stringBuffer = new StringBuffer("optimize:true|");
        for (LatLng waypoint : waypoints) {
            stringBuffer.append(waypoint.latitude);
            stringBuffer.append(",");
            stringBuffer.append(waypoint.longitude);
            stringBuffer.append("|");
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        this.waypoints = stringBuffer.toString();
    }

    @Override
    public DrawingPoints loadDataFromNetwork() throws Exception {

        List<LatLng> points = new ArrayList<>();

        Directions directions =  getService().getDirections(waypoints, origin, destination, appKey);
        for (Directions.Route route : directions.routes) {
            for (Directions.Leg leg : route.legs) {
                for (Directions.Step step : leg.steps) {
                    points.addAll(Utils.decodePoly(step.polyline.points));
                }
            }
        }
        DrawingPoints drawingPoints = new DrawingPoints();
        drawingPoints.points = points;
        return drawingPoints;
    }
}
