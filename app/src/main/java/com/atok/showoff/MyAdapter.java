package com.atok.showoff;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.common.base.Optional;
import com.thedeanda.lorem.Lorem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    final int bigCardHeight = 500;
    final int smallCardHeight = 200;

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

        final int cardHeight;
        if(item.expanded) {
            cardHeight = bigCardHeight;
        } else {
            cardHeight = smallCardHeight;
        }

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(holder.root.getLayoutParams());
        params.height = cardHeight;
        holder.root.setLayoutParams(params);

        holder.titleTextView.setText(item.title);
        holder.subtitleTextView.setText(item.subtitle);
        holder.subtitleTextView.setVisibility(View.GONE);

        loadImage(holder, item, cardHeight);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                animateCard(view, item, (MenuImageView)holder.imageView, !item.expanded);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addMenuItem(MainMenuItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addPhotos(List<FlickrPhoto> photos) {
        for (int i = 0; i < items.size() && i < photos.size(); i++) {
            items.get(i).imageUrl = Optional.of(photos.get(i).getImageUrl());
        }
        notifyDataSetChanged();
    }

    public void addMenuItems(MainMenuItem... addedItems) {
        items.addAll(Arrays.asList(addedItems));
        notifyDataSetChanged();
    }

    public void fillWithLorem() {
        for(int i = 0; i < 20; i++) {
            items.add(new MainMenuItem(Lorem.getTitle(2, 4), Lorem.getWords(10, 15), null, new Intent()));
        }
        notifyDataSetChanged();
    }

    private void loadImage(final ViewHolder holder, MainMenuItem item, final int cardHeight) {
        final MenuImageView imageView = (MenuImageView)holder.imageView;

        if(item.imageUrl.isPresent()) {
            imageView.setImageUrl(item.imageUrl.get(), new MenuImageView.PaletteCallback() {
                @Override
                public void onPalette(Palette palette) {
                    int textColor = palette.getLightVibrantColor(Color.WHITE);

                    holder.titleTextView.setTextColor(textColor);
                    holder.subtitleTextView.setTextColor(textColor);

                    int bgColor = palette.getDarkMutedColor(Color.TRANSPARENT);
                    int bgColorAlpha = Color.argb(200, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));

//                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{bgColorAlpha, Color.TRANSPARENT});
//                    holder.titleTextBackground.setBackground(gradientDrawable);
                }
            });
            imageView.loadImage(cardHeight - holder.root.getPaddingTop() - holder.root.getPaddingBottom());
        } else {
            imageView.setImageDrawable(new ColorDrawable(Color.WHITE));
        }


    }

    private void animateCard(final View containerCardView, final MainMenuItem item, final MenuImageView imageView, boolean toBeExpanded) {
        final int endHeight;
        if(toBeExpanded) {
            endHeight = bigCardHeight;
        } else {
            endHeight = smallCardHeight;
        }
        item.expanded = toBeExpanded;

        ValueAnimator sizeAnimator = ValueAnimator.ofInt(containerCardView.getMeasuredHeight(), endHeight);
        sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer)animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = containerCardView.getLayoutParams();
                layoutParams.height = val;
                containerCardView.setLayoutParams(layoutParams);
            }
        });
        sizeAnimator.setDuration(300);
        sizeAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(item.expanded) {
                    imageView.loadImage(endHeight - containerCardView.getPaddingTop() - containerCardView.getPaddingBottom());
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!item.expanded) {
                    imageView.loadImage(endHeight - containerCardView.getPaddingTop() - containerCardView.getPaddingBottom());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        sizeAnimator.start();
    }


}