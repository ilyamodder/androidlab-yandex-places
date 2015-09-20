package com.googlemaps.template.myapplication.network;

import android.app.Application;

import com.google.android.gms.location.places.Place;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.retrofit.JacksonRetrofitObjectPersisterFactory;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by ilya on 15.09.15.
 */
public class SpiceService extends RetrofitGsonSpiceService {

    RestAdapter.Builder restAdapterBuilder;
    private Map<Class<?>, Object> retrofitInterfaceToServiceMap = new HashMap<Class<?>, Object>();
    private Map<Class<?>, String> retrofitInterfaceToUrl = new HashMap<Class<?>, String>() {{
        put(PlacesApi.class, "https://geocode-maps.yandex.ru/1.x");
        put(DirectionsApi.class, "https://maps.googleapis.com/maps/api/directions");
    }};

    @Override
    public void onCreate() {
        super.onCreate();
        restAdapterBuilder = new RestAdapter.Builder().setConverter(getConverter());
    }

    @Override
    protected String getServerUrl() {
        throw new NotImplementedException("Method should not be used");
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        return new RestAdapter.Builder().setConverter(getConverter()).setEndpoint("http://google.com");
    }

    @Override
    protected <T> T getRetrofitService(Class<T> serviceClass) {
        T service = (T) retrofitInterfaceToServiceMap.get(serviceClass);
        if (service == null) {
            service = restAdapterBuilder.setEndpoint(retrofitInterfaceToUrl.get(serviceClass)).build().create(serviceClass);
            retrofitInterfaceToServiceMap.put(serviceClass, service);
        }
        return service;
    }

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager manager = new CacheManager();
        manager.addPersister(new MyInDatabaseObjectPersister<>(application, PlacePoints.class));
        manager.addPersister(new MyInDatabaseObjectPersister<>(application, DrawingPoints.class));
        return manager;
    }
}
