package com.atok.showoff;


import android.content.Context;
import android.content.Intent;

import com.google.common.base.Optional;

public class MainMenuItem {
    final String title;
    final String subtitle;

    final Optional<Intent> intent;
    Optional<String> imageUrl;

    boolean expanded = false;

    public MainMenuItem(String title, String subtitle, String url, Intent intent) {
        this.title = title;
        this.subtitle = subtitle;
        this.intent = Optional.fromNullable(intent);
        this.imageUrl = Optional.fromNullable(url);
    }

    public MainMenuItem(Context context, int titleRes, int subtitleRes, String url, Intent intent) {
        this(context.getResources().getString(titleRes), context.getResources().getString(subtitleRes), url, intent);
    }
}
