package com.googlemaps.template.myapplication.network;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

/**
 * Created by ilya on 15.09.15.
 */
public class SpiceService extends RetrofitGsonSpiceService {
    @Override
    protected String getServerUrl() {
        return "https://geocode-maps.yandex.ru/1.x";
    }
}
