package com.atok.showoff.picasso;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {

    final RenderScript rs;

    public BlurTransformation(Context context) {
        rs = RenderScript.create(context);
    }

    @Override
    public Bitmap transform(final Bitmap source) {

        float myBlurRadius = 5f;

        final Allocation inputAllocation = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation outputAllocation = Allocation.createTyped(rs, inputAllocation.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4(rs) );
        script.setRadius(myBlurRadius /* e.g. 3.f */);
        script.setInput( inputAllocation );
        script.forEach( outputAllocation );

        Bitmap bluredImage = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        outputAllocation.copyTo(bluredImage);

        BitmapShader sourceBitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        BitmapShader bluredBitmapShader = new BitmapShader(bluredImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Bitmap output = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(),
                source.getConfig());

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setShader(bluredBitmapShader);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight() / 2, paint);
        paint.setShader(sourceBitmapShader);
        canvas.drawRect(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight(), paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "blur";
    }
}
