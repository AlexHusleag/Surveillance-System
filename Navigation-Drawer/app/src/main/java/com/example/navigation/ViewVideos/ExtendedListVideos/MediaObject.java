package com.example.navigation.ViewVideos.ExtendedListVideos;

public class MediaObject {

    private String title;
    private String media_url;

    public MediaObject() {
    }

    public MediaObject(String title, String media_url) {
        this.title = title;
        this.media_url = media_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

}
