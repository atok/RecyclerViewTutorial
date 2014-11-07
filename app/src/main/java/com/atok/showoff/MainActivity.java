package com.atok.showoff;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.atok.showoff.flickr.FlickrPhotos;
import com.atok.showoff.flickr.FlickrResponse;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static timber.log.Timber.*;

public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter();
        mAdapter.addMenuItems(
                new MainMenuItem("Dirty dirty hacks", "Stuff I can do but I decide not to", "https://i.imgur.com/Iw9Nc.jpg", new Intent())
        );
        mAdapter.fillWithLorem();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
                e("animateChange");

                return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndShowPhotos();
    }

    private void fetchAndShowPhotos() {
        MyApplication app = (MyApplication) getApplication();
        app.flickrSearchService.findPhotos("f00ab05b096ee36531f6dfd0e6c149fa", "city+landscape", new Callback<FlickrResponse>() {
            @Override
            public void success(FlickrResponse flickrResponse, Response response) {
                showPhotos(flickrResponse.photos);
            }

            @Override
            public void failure(RetrofitError error) {
                e("Error retrieving flickr images: %s", error);
            }
        });
    }

    private void showPhotos(FlickrPhotos flickrPhotos) {
        mAdapter.addPhotos(flickrPhotos.photo);
    }
}
