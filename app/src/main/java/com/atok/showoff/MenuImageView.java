package com.atok.showoff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.atok.showoff.picasso.BlurStripeTransformation;
import com.atok.showoff.picasso.VerticalGrowingPicassoDrawable;
import com.atok.showoff.picasso.PaletteTransformation;
import com.atok.showoff.picasso.RoundedTransformation;
import com.atok.showoff.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;


public class MenuImageView extends ImageView implements Target {

    String url;
    PaletteCallback paletteCallback = null;
    boolean loadAfterMeasure = false;

    int requestedImageHeight = 0;

    public MenuImageView(Context context) {
        super(context);
    }

    public MenuImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(loadAfterMeasure) {
            loadImage(requestedImageHeight);
        }
    }

    public void setImageUrl(@Nullable String url, final PaletteCallback paletteCallback) {
        this.url = url;
        this.paletteCallback = paletteCallback;
    }

    public void loadImage(final int height) {
        final int width = getMeasuredWidth();
        if(width == 0) {
            requestedImageHeight = height;
            loadAfterMeasure = true;
            return;
        }

        float blurStartInPixels = Utils.dpToPixels(getResources(), 15);
        float blurEndInPixels = Utils.dpToPixels(getResources(), 45);

        setScaleType(ScaleType.CENTER);
        RequestCreator transform = Picasso.with(getContext()).load(url)
                .resize(width, height)
                .centerCrop()
//                .transform(new TextBackgroundGradientTransformation(50))
                .transform(new BlurStripeTransformation(getContext(), blurStartInPixels, blurEndInPixels))
                .transform(new RoundedTransformation(8, 0))
                .transform(PaletteTransformation.instance());

        transform.into((Target)this);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        VerticalGrowingPicassoDrawable.setBitmap(this, getContext(), bitmap, from, false, false);
        Palette palette = PaletteTransformation.getPalette(bitmap);
        paletteCallback.onPalette(palette);
        loadAfterMeasure = false;
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }


    public interface PaletteCallback {
        void onPalette(Palette palette);
    }

}
