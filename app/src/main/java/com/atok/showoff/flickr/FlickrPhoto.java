package com.atok.showoff.flickr;

public class FlickrPhoto {
    public String id;
    public String owner;
    public String secret;
    public String server;
    public int farm;
    public String title;

    public String getImageUrl() {
        // https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + "_b.jpg";
    }
}
