package com.retrytech.vilo.view.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.retrytech.vilo.R;
import com.retrytech.vilo.ViloApplication;
import com.retrytech.vilo.adapter.VideoFullAdapter;
import com.retrytech.vilo.databinding.ActivityPlayerBinding;
import com.retrytech.vilo.databinding.ItemVideoListBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.GlobalApi;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.home.CommentSheetFragment;
import com.retrytech.vilo.view.home.MainActivity;
import com.retrytech.vilo.view.home.ReportSheetFragment;
import com.retrytech.vilo.view.home.SoundVideosActivity;
import com.retrytech.vilo.view.search.FetchUserActivity;
import com.retrytech.vilo.view.search.HashTagActivity;
import com.retrytech.vilo.view.share.ShareSheetFragment;
import com.retrytech.vilo.view.wallet.CoinPurchaseSheetFragment;
import com.retrytech.vilo.viewmodel.VideoPlayerViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PlayerActivity extends BaseActivity implements Player.EventListener {

    private ActivityPlayerBinding binding;
    private VideoPlayerViewModel viewModel;
    private SimpleExoPlayer player;
    private int lastPosition = -1;
    private ItemVideoListBinding playerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparentFlag();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new VideoPlayerViewModel()).createFor()).get(VideoPlayerViewModel.class);
        initView();
        initAds();
        initListener();
        initIntent();
        initObserver();
        binding.setViewModel(viewModel);
    }

    private void initObserver() {
        viewModel.onCommentSuccess.observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                playerBinding.getModel().setPostCommentsCount(playerBinding.getModel().getPostCommentsCount() + 1);
                playerBinding.tvCommentCount.setText(Global.prettyCount(playerBinding.getModel().getPostCommentsCount()));
                binding.etComment.setText("");
                closeKeyboard();
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
        viewModel.coinSend.observe(this, coinSend -> showSendResult(coinSend.getStatus()));
    }

    private void initListener() {
        binding.imgBack.setOnClickListener(v -> {
            if (viewModel.type == 5) {
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            } else {
                onBackPressed();
            }
        });
        binding.refreshlout.setEnableRefresh(false);
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());
        viewModel.adapter.setOnnRecyclerViewItemClick(new VideoFullAdapter.OnRecyclerViewItemClick() {
            @Override
            public void onItemClick(Video.Data model, int position, int type, ItemVideoListBinding binding) {
                switch (type) {
                    // Send to FetchUser Activity
                    case 1:
                        Intent intent = new Intent(PlayerActivity.this, FetchUserActivity.class);
                        intent.putExtra("userid", model.getUserId());
                        startActivity(intent);
                        break;
                    // Play/Pause video
                    case 2:
                        if (player != null) {
                            player.setPlayWhenReady(!player.isPlaying());
                        }
                        break;
                    // Send Bubble to creator
                    case 3:
                        if (!Global.ACCESS_TOKEN.isEmpty()) {
                            showSendBubblePopUp(model.getUserId());
                        } else {
                            initLogin(PlayerActivity.this, () -> showSendBubblePopUp(model.getUserId()));

                        }
                        break;
                    // On like btn click
                    case 4:
                        if (!Global.ACCESS_TOKEN.isEmpty()) {
                            viewModel.likeUnlikePost(model.getPostId());
                        }
                        break;
                    // On Comment Click
                    case 5:
                        CommentSheetFragment fragment = new CommentSheetFragment();
                        fragment.onDismissListener = count -> {
                            model.setPostCommentsCount(count);
                            binding.tvCommentCount.setText(Global.prettyCount(count));

                        };
                        Bundle args = new Bundle();
                        args.putString("postid", model.getPostId());
                        args.putInt("commentCount", model.getPostCommentsCount());
                        fragment.setArguments(args);
                        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
                        break;
                    // On Share Click
                    case 6:
                        handleShareClick(model);
                        break;
                    // On Sound Disk Click
                    case 7:
                        if (Global.ACCESS_TOKEN.isEmpty()) {
                            initLogin(PlayerActivity.this, () -> viewModel.likeUnlikePost(model.getPostId()));

                        } else {
                            Intent intent1 = new Intent(PlayerActivity.this, SoundVideosActivity.class);
                            intent1.putExtra("soundid", model.getSoundId());
                            intent1.putExtra("sound", model.getSound());
                            startActivity(intent1);
                        }
                        break;
                    // On Long Click (Report Video)
                    case 8:
                        new CustomDialogBuilder(PlayerActivity.this).showSimpleDialog("Report this post", "Are you sure you want to\nreport this post?", "Cancel", "Yes, Report", new CustomDialogBuilder.OnDismissListener() {
                            @Override
                            public void onPositiveDismiss() {
                                reportPost(model);
                            }

                            @Override
                            public void onNegativeDismiss() {
                                Log.i("", "");
                            }
                        });

                        break;

                    case 9:
                        playVideo(Const.ITEM_BASE_URL + model.getPostVideo(), binding);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onHashTagClick(String hashTag) {
                Intent intent = new Intent(PlayerActivity.this, HashTagActivity.class);
                intent.putExtra("hashtag", hashTag);
                startActivity(intent);
            }
        });
        binding.rvVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && binding.rvVideos.getLayoutManager() instanceof LinearLayoutManager) {
                    int position = ((LinearLayoutManager) binding.rvVideos.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if (lastPosition != position) {
                        if (position % 10 != 6) {
                            Animation animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
                            if (binding.rvVideos.getLayoutManager() != null) {
                                int index = position - ((position + 4) / 10);
                                View view = binding.rvVideos.getLayoutManager().findViewByPosition(position);
                                if (view != null) {
                                    lastPosition = position;
                                    ItemVideoListBinding binding1 = DataBindingUtil.bind(view);
                                    if (binding1 != null) {
                                        binding1.imgSound.startAnimation(animation);
                                        new GlobalApi().increaseView(binding1.getModel().getPostId());
                                        playVideo(Const.ITEM_BASE_URL + viewModel.adapter.getData().get(index).getPostVideo(), binding1);
                                    }
                                }
                            }
                        } else {
                            if (player != null) {
                                player.setPlayWhenReady(false);
                                player.stop();
                                player.release();
                                player = null;
                                lastPosition = position;
                            }
                        }
                    }
                }
            }
        });
    }

    private void handleShareClick(Video.Data model) {
        ShareSheetFragment fragment = new ShareSheetFragment();
        Bundle args = new Bundle();
        args.putString("video", new Gson().toJson(model));
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    private void reportPost(Video.Data model) {
        ReportSheetFragment fragment = new ReportSheetFragment();
        Bundle args = new Bundle();
        args.putString("postid", model.getPostId());
        args.putInt("reporttype", 1);
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    private void showSendBubblePopUp(String userId) {

        new CustomDialogBuilder(this).showSendCoinDialogue(new CustomDialogBuilder.OnCoinDismissListener() {
            @Override
            public void onCancelDismiss() {
                Log.i("", "");
            }

            @Override
            public void on5Dismiss() {
                viewModel.sendBubble(userId, "5");
            }

            @Override
            public void on10Dismiss() {
                viewModel.sendBubble(userId, "10");
            }

            @Override
            public void on20Dismiss() {
                viewModel.sendBubble(userId, "20");
            }
        });
    }

    private void showSendResult(boolean success) {
        new CustomDialogBuilder(this).showSendCoinResultDialogue(success, success1 -> {
            if (!success1) {
                CoinPurchaseSheetFragment fragment = new CoinPurchaseSheetFragment();
                fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
            }
        });
    }

    private void initIntent() {
        String videoStr = getIntent().getStringExtra("video_list");
        int position = getIntent().getIntExtra("position", 0);

        viewModel.type = getIntent().getIntExtra("type", 0);
        viewModel.handleType(getIntent());

        if (videoStr != null && !videoStr.isEmpty()) {
            viewModel.list = new Gson().fromJson(videoStr, new TypeToken<ArrayList<Video.Data>>() {
            }.getType());
            viewModel.adapter.setPostId(viewModel.list.get(position).getPostId());
            viewModel.adapter.setItemPlay(viewModel.position);
            viewModel.start = viewModel.list.size();
            viewModel.adapter.updateData(viewModel.list);
            for (int i = 0; i < viewModel.adapter.getItemCount(); i++) {
                if (i % 10 != 6) {
                    int index = i - ((i + 4) / 10);
                    if (viewModel.adapter.getData().get(index).getPostId().equals(viewModel.list.get(position).getPostId())) {
                        lastPosition = i;
                        binding.rvVideos.scrollToPosition(i);
                        break;
                    }
                }
            }
        }
    }

    private void initView() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.rvVideos);
    }

    private void initAds() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admobe_native_ad_id));
        builder.forUnifiedNativeAd(unifiedNativeAd -> {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (viewModel.adapter.getUnifiedNativeAd() != null) {
                viewModel.adapter.getUnifiedNativeAd().destroy();
            }
            viewModel.adapter.setUnifiedNativeAd(unifiedNativeAd);

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
        NativeAd nativeAd = new NativeAd(this, getString(R.string.fb_native_ad_id));

        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd != ad) {
                    return;
                }
                viewModel.adapter.setFacebookNativeAd(nativeAd);
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();

    }

    private void playVideo(String videoUrl, ItemVideoListBinding binding) {
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        playerBinding = binding;
        player = new SimpleExoPlayer.Builder(this).build();
        SimpleCache simpleCache = ViloApplication.simpleCache;
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "BubbleTok"))
                , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

        ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
        binding.playerView.setPlayer(player);
        player.setPlayWhenReady(true);
        player.seekTo(0, 0);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.addListener(this);
        binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        player.prepare(progressiveMediaSource, true, false);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            viewModel.loadingVisibility.set(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            viewModel.loadingVisibility.set(View.GONE);
        }
    }

    @Override
    public void onResume() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
        }
        super.onDestroy();
    }
}