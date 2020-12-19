package com.retrytech.vilo.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.retrytech.vilo.R;


public final class BindingAdapters {

    @BindingAdapter({"app:profile_url"})
    public static void loadProfileImage(ImageView view, String profileUrl) {
        new GlideLoader(view.getContext()).loadWithCircleCrop(Const.ITEM_BASE_URL + profileUrl, view);
    }

    @BindingAdapter({"app:image_url"})
    public static void loadImage(ImageView view, String profileUrl) {
        String url = Const.ITEM_BASE_URL + profileUrl;
        new GlideLoader(view.getContext()).loadImage(url, view);
    }

    @BindingAdapter({"app:notification_type"})
    public static void loadNotificationImage(ImageView view, String type) {
        Drawable drawable = view.getContext().getResources().getDrawable(R.drawable.bubbles_small);
        switch (type) {
            case "liked_video":
                drawable = view.getContext().getResources().getDrawable(R.drawable.ic_noti_like);
                break;
            case "comment_video":
                drawable = view.getContext().getResources().getDrawable(R.drawable.ic_noti_comment);
                break;
            case "following":
                drawable = view.getContext().getResources().getDrawable(R.drawable.ic_noti_follow);
                break;
            case "send_coin":
                drawable = view.getContext().getResources().getDrawable(R.drawable.bubbles_small);
                break;
            default:
                break;

        }
        new GlideLoader(view.getContext()).loadNotificationImage(drawable, view);
    }

    @BindingAdapter({"app:media_image", "app:is_round"})
    public static void loadMediaImage(ImageView view, String profileUrl, boolean isRound) {
        if (isRound) {
            new GlideLoader(view.getContext()).loadMediaRoundImage(profileUrl, view);
        } else {
            new GlideLoader(view.getContext()).loadMediaImage(profileUrl, view);
        }

    }

    @BindingAdapter({"app:blur_image"})
    public static void loadBlurImage(ImageView view, String imageUrl) {
        String url = Const.ITEM_BASE_URL + imageUrl;
        new GlideLoader(view.getContext()).loadBlurImage(url, view);
    }

    @BindingAdapter({"app:blur_image"})
    public static void loadMediaRoundBitmap(ImageView view, Bitmap bitmap) {
        new GlideLoader(view.getContext()).loadMediaRoundBitmap(bitmap, view);
    }

    @BindingAdapter({"app:ads_icon"})
    public static void loadRoundDrawable(ImageView view, Drawable bitmap) {
        new GlideLoader(view.getContext()).loadRoundDrawable(bitmap, view);
    }

}


