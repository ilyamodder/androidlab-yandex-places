package com.googlemaps.template.myapplication.network;

import android.app.Application;

import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import org.apache.commons.lang3.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

/**
 * Created by ilya on 15.09.15.
 */
public class SpiceService extends RetrofitGsonSpiceService {

    RestAdapter.Builder mRestAdapterBuilder;
    private Map<Class<?>, Object> mRetrofitInterfaceToServiceMap = new HashMap<Class<?>, Object>();
    private Map<Class<?>, String> mRetrofitInterfaceToUrl = new HashMap<Class<?>, String>() {{
        put(PlacesApi.class, "https://mGeocode-maps.yandex.ru/1.x");
        put(DirectionsApi.class, "https://maps.googleapis.com/maps/api/directions");
    }};

    @Override
    public void onCreate() {
        super.onCreate();
        mRestAdapterBuilder = new RestAdapter.Builder().setConverter(getConverter());
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
        T service = (T) mRetrofitInterfaceToServiceMap.get(serviceClass);
        if (service == null) {
            service = mRestAdapterBuilder.setEndpoint(mRetrofitInterfaceToUrl.get(serviceClass)).build().create(serviceClass);
            mRetrofitInterfaceToServiceMap.put(serviceClass, service);
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
