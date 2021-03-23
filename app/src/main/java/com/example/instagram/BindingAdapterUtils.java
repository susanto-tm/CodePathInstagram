package com.example.instagram;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

public class BindingAdapterUtils {
    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, ParseFile image) {
        if (image != null) {
            Glide.with(view.getContext())
                    .load(image.getUrl())
                    .into(view);
        }
    }
}
