package com.atok.showoff.picasso;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurStripeTransformation implements Transformation {

    final RenderScript rs;
    final float blurHeightStart;
    final float blurHeightEnd;

    public BlurStripeTransformation(Context context, float blurHeightStart, float blurHeightEnd) {
        this.blurHeightEnd = blurHeightEnd;
        this.blurHeightStart = blurHeightStart;
        rs = RenderScript.create(context);
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        float blurRadius = 10f;
//        float blurHeight = source.getHeight() / 2;

        final Allocation inputAllocation = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation outputAllocation = Allocation.createTyped(rs, inputAllocation.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4(rs) );
        script.setRadius(blurRadius /* e.g. 3.f */);
        script.setInput( inputAllocation );
        script.forEach( outputAllocation );

        Bitmap bluredImage = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        outputAllocation.copyTo(bluredImage);

        BitmapShader sourceBitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        BitmapShader bluredBitmapShader = new BitmapShader(bluredImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float heights[] = new float[4]; // FIXME derive from blurHeight
        heights[0] = 0f;
        heights[1] = blurHeightStart / blurHeightEnd;
        heights[2] = 0.8f;
        heights[3] = 1f;    // blurHeightEnd

        LinearGradient linearGradientShader = new LinearGradient(
                0,
                0,
                0,
                blurHeightEnd,
                new int[] {Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.TRANSPARENT},
                heights,
                Shader.TileMode.CLAMP);

        ComposeShader bluredWithGradientShader = new ComposeShader(linearGradientShader, bluredBitmapShader, PorterDuff.Mode.SRC_IN);   //SRC_IN
        ComposeShader finalShader = new ComposeShader(sourceBitmapShader, bluredWithGradientShader, PorterDuff.Mode.SRC_ATOP);

        Bitmap output = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setShader(finalShader);
        canvas.drawPaint(paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "blur" + blurHeightStart + ":" + blurHeightEnd;
    }
}
