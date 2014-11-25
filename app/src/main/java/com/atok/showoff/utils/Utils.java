package com.atok.showoff.utils;


import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {

    public static int dpToPixels(Resources r, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
