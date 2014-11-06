package com.atok.showoff;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
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
        private final CardView root;

        private final TextView subtitleTextView;
        private final  TextView titleTextView;
        private final ImageView imageView;
        private final LinearLayout titleTextBackground;
        private final int parentWidth;

        public ViewHolder(ViewGroup v, int parentWidth) {
            super(v);
            this.parentWidth = parentWidth;
            root = ((CardView)v.findViewById(R.id.menuItem_card));
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
        int width = parent.getMeasuredWidth();
        ViewHolder vh = new ViewHolder(v, width);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MainMenuItem item = items.get(position);

        int bigImageHeight = 500;
        int smallImageHeight = 200;

//        int imageWidth;
//        if(holder.imageView.getWidth() != 0) {
        int imageWidth = holder.root.getMeasuredWidth();
        if(imageWidth == 0) {
            imageWidth = 300;
        }
//        } else {
//            imageWidth = holder.parentWidth;
//            e("parent");
//        }
//        int imageWidth = holder.parentWidth;


        int imageHeight;

        if(item.expanded) {
            imageHeight = bigImageHeight;
        } else {
            imageHeight = smallImageHeight;
        }

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(holder.root.getLayoutParams());
        params.height = imageHeight;
        holder.root.setLayoutParams(params);

        holder.titleTextView.setText(item.title);
        holder.subtitleTextView.setText(item.subtitle);

        holder.subtitleTextView.setVisibility(View.GONE);

        Context context = holder.imageView.getContext();

        Picasso.with(context).load(item.imageUrl)
                .resize(imageWidth, imageHeight)
                .centerCrop()
                .transform(new RoundedTransformation(50, 0))
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


        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.expanded = !item.expanded;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}