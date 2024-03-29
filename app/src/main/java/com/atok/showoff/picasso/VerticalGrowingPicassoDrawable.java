package com.atok.showoff.picasso;

/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static com.squareup.picasso.Picasso.LoadedFrom.MEMORY;


public class VerticalGrowingPicassoDrawable extends BitmapDrawable {

    // Only accessed from main thread.
    private static final Paint DEBUG_PAINT = new Paint();
    private static final float FADE_DURATION = 200f; //ms

    /**
     * Create or update the drawable on the target {@link android.widget.ImageView} to display the supplied bitmap
     * image.
     */
    public static void setBitmap(ImageView target, Context context, Bitmap bitmap,
                          Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        Drawable placeholder = target.getDrawable();
        if (placeholder instanceof AnimationDrawable) {
            ((AnimationDrawable) placeholder).stop();
        }
        VerticalGrowingPicassoDrawable drawable = new VerticalGrowingPicassoDrawable(context, bitmap, placeholder, loadedFrom, noFade, debugging);
        target.setImageDrawable(drawable);
    }

    /**
     * Create or update the drawable on the target {@link ImageView} to display the supplied
     * placeholder image.
     */
    static void setPlaceholder(ImageView target, int placeholderResId, Drawable placeholderDrawable) {
        if (placeholderResId != 0) {
            target.setImageResource(placeholderResId);
        } else {
            target.setImageDrawable(placeholderDrawable);
        }
        if (target.getDrawable() instanceof AnimationDrawable) {
            ((AnimationDrawable) target.getDrawable()).start();
        }
    }

    private final boolean debugging;
    private final float density;
    private final Picasso.LoadedFrom loadedFrom;

    Drawable placeholder;

    long startTimeMillis;
    boolean animating;
    int alpha = 0xFF;

    VerticalGrowingPicassoDrawable(Context context, Bitmap bitmap, Drawable placeholder,
                                   Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        super(context.getResources(), bitmap);

        this.debugging = debugging;
        this.density = context.getResources().getDisplayMetrics().density;

        this.loadedFrom = loadedFrom;

        boolean fade = loadedFrom != MEMORY && !noFade;
        if (fade) {
            this.placeholder = placeholder;
            animating = true;
            startTimeMillis = SystemClock.uptimeMillis();
        }
    }

    @Override public void draw(Canvas canvas) {
        if (!animating) {
            super.draw(canvas);
        } else {
            float normalized = (SystemClock.uptimeMillis() - startTimeMillis) / FADE_DURATION;
            if (normalized >= 1f) {
                animating = false;
                placeholder = null;
                super.draw(canvas);
            } else {
                if (placeholder != null) {

                    int placeholderHeight = placeholder.getIntrinsicHeight();
                    int canvasHeight = canvas.getHeight();
                    float yScale = placeholder.getIntrinsicHeight() / (float)canvasHeight;

                    int yOffset = Math.round((canvasHeight - placeholderHeight)/2f);

                    if(yOffset > 0) {
                        canvas.save();
                        canvas.translate(0, yOffset);
                        canvas.scale(1, yScale);
                        placeholder.draw(canvas);
                        canvas.restore();
                    } else if(yOffset < 0) {

                        placeholder.draw(canvas);
                    } else {
                        placeholder.draw(canvas);
                    }
                }

                int partialAlpha = (int) (alpha * normalized);
                super.setAlpha(partialAlpha);
                super.draw(canvas);
                super.setAlpha(alpha);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    invalidateSelf();
                }
            }
        }
    }

    @Override public void setAlpha(int alpha) {
        this.alpha = alpha;
        if (placeholder != null) {
            placeholder.setAlpha(alpha);
        }
        super.setAlpha(alpha);
    }

    @Override public void setColorFilter(ColorFilter cf) {
        if (placeholder != null) {
            placeholder.setColorFilter(cf);
        }
        super.setColorFilter(cf);
    }

    @Override protected void onBoundsChange(Rect bounds) {
        if (placeholder != null) {
            placeholder.setBounds(bounds);
        }
        super.onBoundsChange(bounds);
    }

    private static Path getTrianglePath(Point p1, int width) {
        Point p2 = new Point(p1.x + width, p1.y);
        Point p3 = new Point(p1.x, p1.y + width);

        Path path = new Path();
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);

        return path;
    }
}
