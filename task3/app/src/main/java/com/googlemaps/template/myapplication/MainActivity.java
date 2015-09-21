package com.googlemaps.template.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

    SupportMapFragment mMapFragment;
    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    PlacePoints mPlacePoints;
    GoogleMap mGoogleMap;
    DrawingPoints mDrawingPoints;
    boolean mPlacesUpdatedFromNetwork = false;
    boolean mDirectionsUpdatedFromNetwork = false;

    Map<LatLng, PlacePoints.Point> mPositionToPoints;

    private SpiceManager mSpiceManager = new SpiceManager(SpiceService.class);

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        if (savedInstanceState == null) {
            if (mGoogleApiClient != null) mGoogleApiClient.connect();
        } else {
            mLocation = savedInstanceState.getParcelable("mLocation");
            mDrawingPoints = savedInstanceState.getParcelable("mDrawingPoints");
            mPlacePoints = savedInstanceState.getParcelable("mPlacePoints");
            final CameraPosition cameraPosition = savedInstanceState.getParcelable("cameraPosition");
            mPlacesUpdatedFromNetwork = savedInstanceState.getBoolean("mPlacesUpdatedFromNetwork");
            mDirectionsUpdatedFromNetwork = savedInstanceState.getBoolean("mDirectionsUpdatedFromNetwork");
            if (cameraPosition != null) {
                mMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MainActivity.this.mGoogleMap = googleMap;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));
                        drawPlaces();
                        drawPath();
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mLocation", mLocation);
        outState.putParcelable("mDrawingPoints", mDrawingPoints);
        outState.putParcelable("mPlacePoints", mPlacePoints);
        if (mGoogleMap != null) outState.putParcelable("cameraPosition", mGoogleMap.getCameraPosition());
        outState.putBoolean("mDirectionsUpdatedFromNetwork", mDirectionsUpdatedFromNetwork);
        outState.putBoolean("mPlacesUpdatedFromNetwork", mPlacesUpdatedFromNetwork);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLocation != null) {
            processLocation();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, new LocationRequest().setInterval(5000), this);
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
                MainActivity.this.mPlacePoints = placePoints;
                mMapFragment.getMapAsync(MainActivity.this);
            }
        });


    }

    private void loadPointsFromNetwork() {
        mPlacesUpdatedFromNetwork = false;
        mDirectionsUpdatedFromNetwork = false;
        GeocoderPlacesRequest request = new GeocoderPlacesRequest(mLocation.getLongitude(), mLocation.getLatitude());
        getSpiceManager().execute(request, PlacePoints.class, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<PlacePoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                if (MainActivity.this.mPlacePoints == null) showErrorDialog();
            }

            @Override
            public void onRequestSuccess(PlacePoints placePoints) {
                mPlacesUpdatedFromNetwork = true;
                MainActivity.this.mPlacePoints = placePoints;
                mMapFragment.getMapAsync(MainActivity.this);
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;
        processLocation();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.clear();

        this.mGoogleMap = googleMap;

        drawPlaces();

        if (!mPlacesUpdatedFromNetwork) {
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
                MainActivity.this.mDrawingPoints = drawingPoints;
                drawPath();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 11));
                if (!mDirectionsUpdatedFromNetwork) loadPointsFromNetwork();
            }
        });
    }

    private void loadDirectionsFromNetwork() {
        DirectionsRequest request = new DirectionsRequest(new LatLng(mLocation.getLatitude(),
                mLocation.getLongitude()), mPlacePoints);
        getSpiceManager().execute(request, DrawingPoints.class, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<DrawingPoints>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showErrorDialog();
            }

            @Override
            public void onRequestSuccess(DrawingPoints drawingPoints) {
                MainActivity.this.mDrawingPoints = drawingPoints;
                drawPath();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), 11));
                if (!mDirectionsUpdatedFromNetwork) {
                    mDirectionsUpdatedFromNetwork = true;
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
                if (mPlacesUpdatedFromNetwork) loadDirectionsFromNetwork();
                else loadPointsFromNetwork();
            }
        }).show();
    }

    private void drawPath() {
        PolylineOptions polylineOptions = new PolylineOptions().color(Color.BLUE);
        for (LatLng point : mDrawingPoints.points) {
            polylineOptions.add(point);
        }
        mGoogleMap.addPolyline(polylineOptions);
    }

    private void drawPlaces() {

        mPositionToPoints = new HashMap<>();

        //adding places around us
        for (PlacePoints.Point item : mPlacePoints.mPoints) {
            String name = item.mName;

            LatLng position = item.mPosition;

            mPositionToPoints.put(position, item);

            mGoogleMap.addMarker(new MarkerOptions()
                            .title(name)
                            .position(position)
            );

        }

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PlacePoints.Point point = mPositionToPoints.get(marker.getPosition());
                showDescriptionActivity(point);
            }
        });

        //adding current mLocation
        mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
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
                mPositionToPoints.get(point.mPosition).mDescription = point.mDescription;
                mGoogleMap.clear();
                drawPlaces();
                drawPath();
            } else if (resultCode == PlaceDescriptionActivity.RESULT_ITEM_REMOVED) {
                PlacePoints.Point point =
                        data.getParcelableExtra(PlaceDescriptionActivity.EXTRA_POINT);
                mPlacePoints.mPoints.remove(mPlacePoints.mPoints.indexOf(point));
                mGoogleMap.clear();
                drawPlaces();
                loadDirectionsFromNetwork();
            }
        }
    }
}