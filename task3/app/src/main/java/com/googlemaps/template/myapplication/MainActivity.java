package com.googlemaps.template.myapplication;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.googlemaps.template.myapplication.network.Directions;
import com.googlemaps.template.myapplication.network.DirectionsRequest;
import com.googlemaps.template.myapplication.network.DrawingPoints;
import com.googlemaps.template.myapplication.network.GeocoderPlacesRequest;
import com.googlemaps.template.myapplication.network.PlacePoints;
import com.googlemaps.template.myapplication.network.Places;
import com.googlemaps.template.myapplication.network.SpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleApiClient googleApiClient;
    Location location;
    PlacePoints placePoints;
    GoogleMap googleMap;
    DrawingPoints drawingPoints;

    private SpiceManager spiceManager = new SpiceManager(SpiceService.class);

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        if (savedInstanceState == null) {
            if (googleApiClient != null) googleApiClient.connect();
        } else {
            location = savedInstanceState.getParcelable("location");
            drawingPoints = savedInstanceState.getParcelable("drawingPoints");
            placePoints = savedInstanceState.getParcelable("placePoints");
            final CameraPosition cameraPosition = savedInstanceState.getParcelable("cameraPosition");
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MainActivity.this.googleMap = googleMap;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
                    drawPlaces();
                    drawPath();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("location", location);
        outState.putParcelable("drawingPoints", drawingPoints);
        outState.putParcelable("placePoints", placePoints);
        outState.putParcelable("cameraPosition", googleMap.getCameraPosition());
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            processLocation();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, new LocationRequest().setInterval(5000), this);
        }
    }

    public void processLocation() {
        GeocoderPlacesRequest request = new GeocoderPlacesRequest(location.getLongitude(), location.getLatitude());
        getSpiceManager().execute(request, new RequestListener<PlacePoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(PlacePoints placePoints) {
                MainActivity.this.placePoints = placePoints;
                mapFragment.getMapAsync(MainActivity.this);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        processLocation();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.clear();

        this.googleMap = googleMap;

        drawPlaces();

        //sending request for directions
        DirectionsRequest request = new DirectionsRequest(new LatLng(location.getLatitude(),
                location.getLongitude()), placePoints);
        getSpiceManager().execute(request, new RequestListener<DrawingPoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println(spiceException);
            }

            @Override
            public void onRequestSuccess(DrawingPoints drawingPoints) {
                MainActivity.this.drawingPoints = drawingPoints;
                drawPath();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 11));
            }
        });
    }

    private void drawPath() {
        PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE);
        for (LatLng point : drawingPoints.points) {
            polylineOptions.add(point);
        }
        googleMap.addPolyline(polylineOptions);
    }

    private void drawPlaces() {
        //adding places around us
        for (PlacePoints.Point item : placePoints.points) {
            String name = item.name;

            LatLng position = item.position;

            googleMap.addMarker(new MarkerOptions()
                            .title(name)
                            .position(position)
            );

        }

        //adding current location
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .fillColor(Color.BLUE)
                .radius(100));
    }
}