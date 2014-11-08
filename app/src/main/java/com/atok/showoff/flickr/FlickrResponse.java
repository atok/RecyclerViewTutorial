package com.atok.showoff.flickr;

import com.google.common.base.Optional;

public class FlickrResponse {
    public Optional<FlickrPhotos> photos = Optional.absent();
    public Optional<String> stat = Optional.absent();
}
