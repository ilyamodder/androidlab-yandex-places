package com.googlemaps.template.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.googlemaps.template.myapplication.network.DirectionsRequest;
import com.googlemaps.template.myapplication.network.DrawingPoints;
import com.googlemaps.template.myapplication.network.GeocoderPlacesRequest;
import com.googlemaps.template.myapplication.network.PlacePoints;
import com.googlemaps.template.myapplication.network.SpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, OnMapReadyCallback {

    SupportMapFragment mapFragment;
    GoogleApiClient googleApiClient;
    Location location;
    PlacePoints placePoints;
    GoogleMap googleMap;
    DrawingPoints drawingPoints;
    boolean placesUpdatedFromNetwork = false;
    boolean directionsUpdatedFromNetwork = false;

    Map<LatLng, PlacePoints.Point> positionToPoints;

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
        getSpiceManager().getFromCache(PlacePoints.class, PlacePoints.class, DurationInMillis.ALWAYS_RETURNED, new RequestListener<PlacePoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                loadPointsFromNetwork();
            }

            @Override
            public void onRequestSuccess(PlacePoints placePoints) {
                MainActivity.this.placePoints = placePoints;
                mapFragment.getMapAsync(MainActivity.this);
            }
        });


    }

    private void loadPointsFromNetwork() {
        placesUpdatedFromNetwork = false;
        directionsUpdatedFromNetwork = false;
        GeocoderPlacesRequest request = new GeocoderPlacesRequest(location.getLongitude(), location.getLatitude());
        getSpiceManager().execute(request, PlacePoints.class, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<PlacePoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (MainActivity.this.placePoints == null) showErrorDialog();
            }

            @Override
            public void onRequestSuccess(PlacePoints placePoints) {
                placesUpdatedFromNetwork = true;
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

        if (!placesUpdatedFromNetwork) {
            loadDirectionsFromCache();
        } else {
            loadDirectionsFromNetwork();
        }


    }

    private void loadDirectionsFromCache() {
        getSpiceManager().getFromCache(DrawingPoints.class, DrawingPoints.class, DurationInMillis.ALWAYS_RETURNED, new RequestListener<DrawingPoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                loadDirectionsFromNetwork();
            }

            @Override
            public void onRequestSuccess(DrawingPoints drawingPoints) {
                MainActivity.this.drawingPoints = drawingPoints;
                drawPath();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 11));
                if (!directionsUpdatedFromNetwork) loadPointsFromNetwork();
            }
        });
    }

    private void loadDirectionsFromNetwork() {
        DirectionsRequest request = new DirectionsRequest(new LatLng(location.getLatitude(),
                location.getLongitude()), placePoints);
        getSpiceManager().execute(request, DrawingPoints.class, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<DrawingPoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showErrorDialog();
            }

            @Override
            public void onRequestSuccess(DrawingPoints drawingPoints) {
                MainActivity.this.drawingPoints = drawingPoints;
                drawPath();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 11));
                if (!directionsUpdatedFromNetwork) {
                    directionsUpdatedFromNetwork = true;
                    showDataUpdatedDialog();
                }

            }
        });
    }

    private void showDataUpdatedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.main_dialog_data_updated_title)
                .setMessage(R.string.main_dialog_data_updated_message)
                .setPositiveButton(R.string.main_dialog_data_updated_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.main_dialog_error_title))
                .setMessage(getString(R.string.main_dialog_error_message))
                .setPositiveButton(getString(R.string.main_dialog_error_ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNeutralButton(getString(R.string.main_dialog_error_retry_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (placesUpdatedFromNetwork) loadDirectionsFromNetwork();
                else loadPointsFromNetwork();
            }
        }).show();
    }

    private void drawPath() {
        PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE);
        for (LatLng point : drawingPoints.points) {
            polylineOptions.add(point);
        }
        googleMap.addPolyline(polylineOptions);
    }

    private void drawPlaces() {

        positionToPoints = new HashMap<>();

        //adding places around us
        for (PlacePoints.Point item : placePoints.points) {
            String name = item.name;

            LatLng position = item.position;

            positionToPoints.put(position, item);

            googleMap.addMarker(new MarkerOptions()
                            .title(name)
                            .position(position)
            );

        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PlacePoints.Point point = positionToPoints.get(marker.getPosition());
                showDescriptionActivity(point);
            }
        });

        //adding current location
        googleMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .fillColor(Color.BLUE)
                .radius(100));
    }

    private void showDescriptionActivity(PlacePoints.Point point) {
        Intent intent = new Intent(this, PlaceDescriptionActivity.class);
        intent.putExtra(PlaceDescriptionActivity.EXTRA_POINT, point);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == PlaceDescriptionActivity.RESULT_CHANGED_DESCRIPTION) {
                PlacePoints.Point point =
                        data.getParcelableExtra(PlaceDescriptionActivity.EXTRA_POINT);
                positionToPoints.get(point.position).description = point.description;
                googleMap.clear();
                drawPlaces();
                drawPath();
            } else if (resultCode == PlaceDescriptionActivity.RESULT_ITEM_REMOVED) {
                PlacePoints.Point point =
                        data.getParcelableExtra(PlaceDescriptionActivity.EXTRA_POINT);
                placePoints.points.remove(placePoints.points.indexOf(point));
                googleMap.clear();
                drawPlaces();
                loadDirectionsFromNetwork();
            }
        }
    }
}