package com.atok.showoff;


import android.content.Context;
import android.content.Intent;

public class MainMenuItem {
    final String title;
    final String subtitle;
    final Intent intent;
    String imageUrl;

    public MainMenuItem(String title, String subtitle, String url, Intent intent) {
        this.title = title;
        this.subtitle = subtitle;
        this.intent = intent;
        this.imageUrl = url;
    }

    public MainMenuItem(Context context, int titleRes, int subtitleRes, String url, Intent intent) {
        title = context.getResources().getString(titleRes);
        subtitle = context.getResources().getString(subtitleRes);
        imageUrl = url;
        this.intent = intent;
    }
}
