package com.atok.showoff;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.atok.showoff.flickr.FlickrResponse;
import com.google.common.util.concurrent.SettableFuture;

import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FlickrSearchTest extends ApplicationTestCase<Application> {

    MyApplication application;

    public FlickrSearchTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        application = (MyApplication)getApplication();
    }

    public void fetchImages() throws ExecutionException, InterruptedException {
        assertTrue(false);

        final SettableFuture<FlickrResponse> future = SettableFuture.create();
        application.flickrSearchService.findPhotos(StaticConfig.FLICKR_API_KEY, "cat", new Callback<FlickrResponse>() {
            @Override
            public void success(FlickrResponse flickrResponse, Response response) {
                future.set(flickrResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                future.setException(error);
            }
        });

        FlickrResponse flickrResponse = future.get();
        assertTrue(flickrResponse.photos.isPresent());
        assertTrue(flickrResponse.photos.get().photo.size() > 0);

        System.out.println(flickrResponse.photos.get().photo.get(0).getImageUrl());
    }
}
