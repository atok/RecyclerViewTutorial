package com.atok.showoff.picasso;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

public class TextBackgroundGradientTransformation implements Transformation {

    private final String key;
    private final float height;

    public TextBackgroundGradientTransformation(float height) {
        this.height = height;
        key = "gradient-" + height;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap output = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        int color0 = Color.BLACK;
        int color1 = Color.TRANSPARENT;

        BitmapShader bitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        LinearGradient linearGradient = new LinearGradient(
                0,
                0,
                0,
                height,
                color0,
                color1,
                Shader.TileMode.CLAMP);
        ComposeShader composeShader = new ComposeShader(bitmapShader, linearGradient, PorterDuff.Mode.DARKEN);


        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(composeShader);

        Canvas canvas = new Canvas(output);
        canvas.drawPaint(paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return key;
    }
}
