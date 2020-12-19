package com.retrytech.vilo.view.home;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.retrytech.vilo.R;
import com.retrytech.vilo.ViloApplication;
import com.retrytech.vilo.adapter.VideoFullAdapter;
import com.retrytech.vilo.databinding.FragmentForUBinding;
import com.retrytech.vilo.databinding.ItemFamousCreatorBinding;
import com.retrytech.vilo.databinding.ItemVideoListBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.GlobalApi;
import com.retrytech.vilo.view.base.BaseFragment;
import com.retrytech.vilo.view.search.FetchUserActivity;
import com.retrytech.vilo.view.search.HashTagActivity;
import com.retrytech.vilo.view.share.ShareSheetFragment;
import com.retrytech.vilo.view.wallet.CoinPurchaseSheetFragment;
import com.retrytech.vilo.viewmodel.ForUViewModel;
import com.retrytech.vilo.viewmodel.HomeViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;


public class ForUFragment extends BaseFragment implements Player.EventListener {


    private final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    private SimpleExoPlayer player;


    private FragmentForUBinding binding;
    private ForUViewModel viewModel;
    private final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    public int lastPosition = -1;
    private HomeViewModel parentViewModel;

    private SimpleCache simpleCache;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private ItemFamousCreatorBinding binding2;
    private String type;
    private NativeAd nativeAd;

    public static ForUFragment getNewInstance(String type) {
        ForUFragment fragment = new ForUFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_for_u, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ForUViewModel()).createFor()).get(ForUViewModel.class);
        initView();
        initAds();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);

    }

    private void initAds() {
        if (getActivity() != null) {
            AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getActivity().getString(R.string.admobe_native_ad_id));
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
            nativeAd = new NativeAd(getActivity(), getActivity().getString(R.string.fb_native_ad_id));

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
                    if (nativeAd == null || nativeAd != ad) {
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
    }

    private void initView() {
        binding.recyclerview.setLayoutManager(layoutManager);
        binding.popularRecyclerview.setLayoutManager(layoutManager1);
        binding.refreshlout.setEnableRefresh(false);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.recyclerview);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(binding.popularRecyclerview);

        if (getArguments() != null) {
            type = getArguments().getString("type");
            if (type != null && type.equals("1")) {
                viewModel.postType = "related";
            } else {
                viewModel.postType = "following";
            }
            viewModel.fetchPostVideos(false);
        }
    }

    private void initListeners() {
        viewModel.famousAdapter.setOnRecyclerViewItemClick((model, position, binding, type) -> {
            if (type == 1) {
                if (parentViewModel.onPageSelect.getValue() != null && parentViewModel.onPageSelect.getValue() == Integer.parseInt(ForUFragment.this.type)) {
                    lastPosition = position;
                    playVideo(Const.ITEM_BASE_URL + model.getPostVideo(), binding);
                }
            } else {
                Intent intent = new Intent(getContext(), FetchUserActivity.class);
                intent.putExtra("userid", model.getUserId());
                startActivity(intent);
            }
        });

        binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (lastPosition != position) {
                        if (position % 10 != 6) {
                            Animation animation = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.slow_rotate);
                            if (binding.recyclerview.getLayoutManager() != null) {
                                int index = position - ((position + 4) / 10);
                                View view = binding.recyclerview.getLayoutManager().findViewByPosition(position);
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

        binding.popularRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = layoutManager1.findFirstCompletelyVisibleItemPosition();

                    if (position != -1 && lastPosition != position && binding.popularRecyclerview.getLayoutManager() != null) {
                        View view = binding.popularRecyclerview.getLayoutManager().findViewByPosition(position);
                        if (view != null) {
                            lastPosition = position;
                            ItemFamousCreatorBinding binding1 = DataBindingUtil.bind(view);

                            playVideo(Const.ITEM_BASE_URL + viewModel.famousAdapter.getData().get(position).getPostVideo(), binding1);
                        }
                    }

                }
            }
        });


        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());

        viewModel.adapter.setOnnRecyclerViewItemClick(new VideoFullAdapter.OnRecyclerViewItemClick() {
            @Override
            public void onItemClick(Video.Data model, int position, int type, ItemVideoListBinding binding) {
                switch (type) {
                    // Send to FetchUser Activity
                    case 1:
                        Intent intent = new Intent(getContext(), FetchUserActivity.class);
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
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).initLogin(getActivity(), () -> showSendBubblePopUp(model.getUserId()));
                            }
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
                        fragment.show(getChildFragmentManager(), fragment.getClass().getSimpleName());
                        break;
                    // On Share Click
                    case 6:
                        handleShareClick(model);
                        break;
                    // On Sound Disk Click
                    case 7:
                        if (Global.ACCESS_TOKEN.isEmpty()) {
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).initLogin(getActivity(), () -> viewModel.likeUnlikePost(model.getPostId()));
                            }
                        } else {
                            Intent intent1 = new Intent(getContext(), SoundVideosActivity.class);
                            intent1.putExtra("soundid", model.getSoundId());
                            intent1.putExtra("sound", model.getSound());
                            startActivity(intent1);
                        }
                        break;
                    // On Long Click (Report Video)
                    case 8:
                        new CustomDialogBuilder(getContext()).showSimpleDialog("Report this post", "Are you sure you want to\nreport this post?", "Cancel", "Yes, Report", new CustomDialogBuilder.OnDismissListener() {
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
                        lastPosition = position;
                        playVideo(Const.ITEM_BASE_URL + model.getPostVideo(), binding);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onHashTagClick(String hashTag) {
                Intent intent = new Intent(getContext(), HashTagActivity.class);
                intent.putExtra("hashtag", hashTag);
                startActivity(intent);
            }
        });

    }

    private void handleShareClick(Video.Data model) {

        ShareSheetFragment fragment = new ShareSheetFragment();
        Bundle args = new Bundle();
        args.putString("video", new Gson().toJson(model));
        fragment.setArguments(args);
        fragment.show(getChildFragmentManager(), fragment.getClass().getSimpleName());


    }

    private void initObserve() {
        parentViewModel.onStop.observe(getViewLifecycleOwner(), onStop -> {
            if (onStop != null && parentViewModel.onPageSelect.getValue() != null && parentViewModel.onPageSelect.getValue() == Integer.parseInt(type) && player != null) {
                player.setPlayWhenReady(!onStop);
            }
        });
        viewModel.onLoadMoreComplete.observe(getViewLifecycleOwner(), onLoadMore -> binding.refreshlout.finishLoadMore());
        viewModel.coinSend.observe(getViewLifecycleOwner(), coinSend -> showSendResult(coinSend.getStatus()));
    }

    private void playVideo(String videoUrl, ItemVideoListBinding binding) {
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        if (getActivity() != null) {
            player = new SimpleExoPlayer.Builder(getActivity()).build();
            simpleCache = ViloApplication.simpleCache;
            cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "BubbleTok"))
                    , CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

            ProgressiveMediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(Uri.parse(videoUrl));
            binding.playerView.setPlayer(player);
            if (parentViewModel.onPageSelect.getValue() != null && parentViewModel.onPageSelect.getValue().equals(Integer.parseInt(ForUFragment.this.type))) {
                player.setPlayWhenReady(true);
            }
            player.seekTo(0, 0);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(this);
            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            player.prepare(progressiveMediaSource, true, false);
        }
    }

    private void playVideo(String videoUrl, ItemFamousCreatorBinding binding) {
        if (player != null) {
            player.removeListener(this);
            player.setPlayWhenReady(false);
            player.release();
        }
        if (binding2 != null) {
            // run scale animation and make it smaller
            Animation anim = AnimationUtils.loadAnimation(binding2.getRoot().getContext(), R.anim.scale_out_tv);
            binding2.getRoot().startAnimation(anim);
            anim.setFillAfter(true);
        }
        binding2 = binding;
        // run scale animation and make it bigger
        Animation anim = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.scale_in_tv);
        binding.getRoot().startAnimation(anim);
        anim.setFillAfter(true);
        if (getActivity() != null) {
            player = new SimpleExoPlayer.Builder(getActivity()).build();
            simpleCache = ViloApplication.simpleCache;
            cacheDataSourceFactory = new CacheDataSourceFactory(simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "BubbleTok"))
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
    }

    private void showSendBubblePopUp(String userId) {

        new CustomDialogBuilder(getContext()).showSendCoinDialogue(new CustomDialogBuilder.OnCoinDismissListener() {
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
        new CustomDialogBuilder(getContext()).showSendCoinResultDialogue(success, success1 -> {
            if (!success1) {
                CoinPurchaseSheetFragment fragment = new CoinPurchaseSheetFragment();
                fragment.show(getChildFragmentManager(), fragment.getClass().getSimpleName());
            }
        });
    }

    private void reportPost(Video.Data model) {
        ReportSheetFragment fragment = new ReportSheetFragment();
        Bundle args = new Bundle();
        args.putString("postid", model.getPostId());
        args.putInt("reporttype", 1);
        fragment.setArguments(args);
        fragment.show(getChildFragmentManager(), fragment.getClass().getSimpleName());
    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            parentViewModel.loadingVisibility.set(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            parentViewModel.loadingVisibility.set(View.GONE);
        }
    }
}
