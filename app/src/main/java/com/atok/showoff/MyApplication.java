package com.atok.showoff;


import android.app.Application;

import com.atok.showoff.flickr.FlickrSearchService;
import com.squareup.picasso.Picasso;

import retrofit.RestAdapter;
import timber.log.Timber;

public class MyApplication extends Application {

    public FlickrSearchService flickrSearchService;

    @Override
    public void onCreate() {
        super.onCreate();

        configureLogging();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.flickr.com/services/rest")
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();

        flickrSearchService = restAdapter.create(FlickrSearchService.class);
    }

    private void configureLogging() {
        if (BuildConfig.DEBUG) {
            Picasso.with(getApplicationContext()).setIndicatorsEnabled(true);
            Picasso.with(getApplicationContext()).setLoggingEnabled(true);

            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.HollowTree());  //placeholder for error reporting
        }
    }
}
