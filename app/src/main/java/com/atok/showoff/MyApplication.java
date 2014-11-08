package com.atok.showoff;


import android.app.Application;

import com.atok.showoff.flickr.FlickrSearchService;
import com.atok.showoff.utils.OptionalTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import timber.log.Timber;

public class MyApplication extends Application {

    public FlickrSearchService flickrSearchService;

    @Override
    public void onCreate() {
        super.onCreate();

        configureLogging();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY)
                .serializeNulls()
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.flickr.com/services/rest")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();

        flickrSearchService = restAdapter.create(FlickrSearchService.class);
    }

    private void configureLogging() {
        if (BuildConfig.DEBUG) {
            Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);
            Picasso.with(getApplicationContext()).setLoggingEnabled(false);

            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.HollowTree());  //placeholder for error reporting
        }
    }
}
