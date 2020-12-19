package com.retrytech.vilo.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemAdsLayBinding;
import com.retrytech.vilo.databinding.ItemVideoListBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.GlideLoader;
import com.retrytech.vilo.utils.Global;

import java.util.ArrayList;
import java.util.List;

public class VideoFullAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int AD_TYPE = 1;
    private static final int AD_DISPLAY_FREQUENCY = 10;
    private static final int POST_TYPE = 2;
    private ArrayList<Video.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;
    private int itemToPlay = 0;
    private String postId = "";
    private UnifiedNativeAd unifiedNativeAd;
    private NativeAd facebookNativeAd;
    private NativeAdLayout nativeAdLayout;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ads_lay, parent, false);
            return new AdsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
            return new VideoFullViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof VideoFullViewHolder) {
            VideoFullViewHolder holder = (VideoFullViewHolder) viewHolder;
            int index = position - ((position + 4) / AD_DISPLAY_FREQUENCY);
            holder.setModel(index);
        } else {
            if (viewHolder instanceof AdsViewHolder) {
                AdsViewHolder holder = (AdsViewHolder) viewHolder;
                if (unifiedNativeAd != null) {
                    holder.binding.frame.setVisibility(View.VISIBLE);
                    LinearLayout frameLayout = holder.binding.frame;
                    UnifiedNativeAdView
                            adView = (UnifiedNativeAdView) LayoutInflater.from(holder.binding.getRoot().getContext())
                            .inflate(R.layout.admob_native, null, false);
                    populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                    holder.binding.unbind();
                } else if (facebookNativeAd != null) {
                    inflateAd(facebookNativeAd, holder.binding);
                } else {
                    holder.binding.selfAd.getRoot().setVisibility(View.VISIBLE);
                    holder.binding.selfAd.btnBrand.setOnClickListener(v -> holder.binding.getRoot().getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.retrytech.ledgeapp"))));
                }
            }
        }
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd unifiedNativeAd, UnifiedNativeAdView adView) {

        adView.setMediaView(adView.findViewById(R.id.ad_media));
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());


        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (unifiedNativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);

        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(unifiedNativeAd.getBody());
        }

        if (unifiedNativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }

        if (unifiedNativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            new GlideLoader(adView.getContext()).loadRoundDrawable(unifiedNativeAd.getIcon().getDrawable(), (ImageView) adView.getIconView());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (unifiedNativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(unifiedNativeAd.getPrice());
        }

        if (unifiedNativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(unifiedNativeAd.getStore());
        }

        if (unifiedNativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(unifiedNativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (unifiedNativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(unifiedNativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.


        // Updates the UI to say whether or not this ad has a video asset.

    }

    private void inflateAd(NativeAd nativeAd, ItemAdsLayBinding binding) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = binding.fbNative;
        binding.fbNative.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(binding.getRoot().getContext());
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        RelativeLayout adView = (RelativeLayout) inflater.inflate(R.layout.fb_native_full, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(adView.getContext(), nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);


        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    public UnifiedNativeAd getUnifiedNativeAd() {
        return unifiedNativeAd;
    }

    public void setUnifiedNativeAd(UnifiedNativeAd unifiedNativeAd) {
        this.unifiedNativeAd = unifiedNativeAd;
    }

    public NativeAd getFacebookNativeAd() {
        return facebookNativeAd;
    }

    public void setFacebookNativeAd(NativeAd facebookNativeAd) {
        this.facebookNativeAd = facebookNativeAd;
    }

    public NativeAdLayout getNativeAdLayout() {
        return nativeAdLayout;
    }

    public void setNativeAdLayout(NativeAdLayout nativeAdLayout) {
        this.nativeAdLayout = nativeAdLayout;
    }

    @Override
    public int getItemCount() {
        return mList.size() + mList.size() / 10;
    }

    @Override
    public int getItemViewType(int position) {

        if (position % AD_DISPLAY_FREQUENCY == 6) {
            return AD_TYPE;
        } else {
            return POST_TYPE;
        }

    }

    public void updateData(List<Video.Data> list) {
        mList = (ArrayList<Video.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<Video.Data> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    public List<Video.Data> getData() {
        return mList;
    }

    public void setOnnRecyclerViewItemClick(OnRecyclerViewItemClick onnRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onnRecyclerViewItemClick;
    }

    public void setItemPlay(int position) {
        itemToPlay = position;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public interface OnRecyclerViewItemClick {

        void onItemClick(Video.Data model, int position, int type, ItemVideoListBinding binding);

        void onHashTagClick(String hashTag);

    }


    class VideoFullViewHolder extends RecyclerView.ViewHolder {
        ItemVideoListBinding binding;


        VideoFullViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        public void setModel(int position) {
            binding.setModel(mList.get(position));
            if (position == itemToPlay || postId.equals(mList.get(position).getPostId())) {
                Animation animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
                binding.imgSound.startAnimation(animation);
                onRecyclerViewItemClick.onItemClick(mList.get(position), position, 9, binding);
            }

            binding.tvSoundName.setSelected(true);
            binding.tvLikeCount.setText(Global.prettyCount(mList.get(position).getDummyLikeCount()));
            binding.playerView.setOnTouchListener(new View.OnTouchListener() {
                GestureDetector gestureDetector = new GestureDetector(binding.getRoot().getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        onRecyclerViewItemClick.onItemClick(mList.get(position), position, 2, binding);
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        onRecyclerViewItemClick.onItemClick(mList.get(position), position, 8, binding);
                        super.onLongPress(e);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return false;
                }
            });
            binding.tvDescreption.setOnHashtagClickListener((view, text) -> onRecyclerViewItemClick.onHashTagClick(text.toString()));

            binding.loutUser.setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, 1, binding));

            binding.imgSendBubble.setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, 3, binding));
            binding.likebtn.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    onRecyclerViewItemClick.onItemClick(mList.get(position), position, 4, binding);
                    mList.get(position).setPostLikesCount(String.valueOf(Integer.parseInt(mList.get(position).getPostLikesCount()) + 1));
                    binding.tvLikeCount.setText(Global.prettyCount(Long.parseLong(mList.get(position).getPostLikesCount())));
                    mList.get(position).setVideoIsLiked(1);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    onRecyclerViewItemClick.onItemClick(mList.get(position), position, 4, binding);
                    mList.get(position).setPostLikesCount(String.valueOf(Integer.parseInt(mList.get(position).getPostLikesCount()) - 1));
                    binding.tvLikeCount.setText(Global.prettyCount(Long.parseLong(mList.get(position).getPostLikesCount())));
                    mList.get(position).setVideoIsLiked(0);

                }
            });
            binding.imgComment.setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, 5, binding));
            binding.imgShare.setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, 6, binding));
            binding.imgSound.setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, 7, binding));

        }

    }

    class AdsViewHolder extends RecyclerView.ViewHolder {
        ItemAdsLayBinding binding;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            binding.executePendingBindings();
        }
    }
}
