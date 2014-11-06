package com.atok.showoff;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atok.showoff.flickr.FlickrPhoto;
import com.atok.showoff.picasso.PaletteTransformation;
import com.atok.showoff.picasso.RoundedTransformation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thedeanda.lorem.Lorem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static timber.log.Timber.*;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<MainMenuItem> items = new ArrayList<MainMenuItem>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView subtitleTextView;
        private final  TextView titleTextView;
        private final ImageView imageView;
        private final LinearLayout titleTextBackground;

        public ViewHolder(ViewGroup v) {
            super(v);
            titleTextView = ((TextView) v.findViewById(R.id.menuItem_title));
            subtitleTextView = ((TextView) v.findViewById(R.id.menuItem_subtitle));
            imageView = ((ImageView) v.findViewById(R.id.menuItem_image));
            titleTextBackground = ((LinearLayout)v.findViewById(R.id.menuItem_textBg));
        }
    }

    public MyAdapter() {
        items = new ArrayList<MainMenuItem>();
    }

    public void addMenuItem(MainMenuItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addPhotos(List<FlickrPhoto> photos) {
        for (int i = 0; i < items.size() && i < photos.size(); i++) {
            items.get(i).imageUrl = photos.get(i).getImageUrl();
        }
        notifyDataSetChanged();
    }

    public void addMenuItems(MainMenuItem... addedItems) {
        items.addAll(Arrays.asList(addedItems));
        notifyDataSetChanged();
    }

    public void fillWithLorem() {
        for(int i = 0; i < 20; i++) {
            items.add(new MainMenuItem(Lorem.getTitle(2, 4), Lorem.getWords(10, 15), "https://i.imgur.com/zlKZE1v.jpg", new Intent()));
        }
        notifyDataSetChanged();
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = ((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_item, parent, false));
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MainMenuItem item = items.get(position);

        holder.titleTextView.setText(item.title);
        holder.subtitleTextView.setText(item.subtitle);

        holder.subtitleTextView.setVisibility(View.GONE);

        Context context = holder.imageView.getContext();

        Picasso.with(context).load(item.imageUrl)
                .fit()
                .centerCrop()
                .transform(new RoundedTransformation(5, 0))
                .transform(PaletteTransformation.instance())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) holder.imageView.getDrawable()).getBitmap();
                        Palette palette = PaletteTransformation.getPalette(bitmap);
                        int textColor = palette.getLightVibrantColor(Color.WHITE);

                        holder.titleTextView.setTextColor(textColor);
                        holder.subtitleTextView.setTextColor(textColor);

                        int bgColor = palette.getDarkMutedColor(Color.parseColor("#55000000"));
                        int bgColorAlpha = Color.argb(200, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{bgColorAlpha, Color.TRANSPARENT});
                        holder.titleTextBackground.setBackground(gradientDrawable);
                    }

                    @Override
                    public void onError() {
                        e("Error downloading image");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}