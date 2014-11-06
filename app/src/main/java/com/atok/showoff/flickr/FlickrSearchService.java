package com.atok.showoff.flickr;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface FlickrSearchService {
    @GET("/?method=flickr.photos.search&format=json&nojsoncallback=1")
    void findPhotos(@Query("api_key") String apiKey, @Query("tags") String tags, Callback<FlickrResponse> callback);
}
