package com.retrytech.vilo.view.music;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentMusicChildBinding;
import com.retrytech.vilo.databinding.ItemMusicBinding;
import com.retrytech.vilo.model.music.Musics;
import com.retrytech.vilo.utils.Const;
import com.retrytech.vilo.view.base.BaseFragment;
import com.retrytech.vilo.viewmodel.MusicChildViewModel;
import com.retrytech.vilo.viewmodel.MusicViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class MusicChildFragment extends BaseFragment implements Player.EventListener {

    private FragmentMusicChildBinding binding;
    private MusicViewModel parentViewModel;
    private MusicChildViewModel viewModel;
    private SimpleExoPlayer player;
    private ItemMusicBinding previousView;
    private String previousUrl = "none";
    private int type;

    public static MusicChildFragment newInstance(int type) {
        MusicChildFragment fragment = new MusicChildFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", -1);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_child, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getParentFragment() != null) {
            parentViewModel = ViewModelProviders.of(getParentFragment()).get(MusicViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new MusicChildViewModel()).createFor()).get(MusicChildViewModel.class);
        initView();
        initObserve();
        iniListener();
        binding.setViewModel(viewModel);
    }

    private void initView() {
        viewModel.type = type;
        if (type == 1) {
            if (sessionManager.getFavouriteMusic() != null && !sessionManager.getFavouriteMusic().isEmpty()) {
                viewModel.getFavMusicList(sessionManager.getFavouriteMusic());
            }
        } else {
            viewModel.getMusicList();
        }
    }

    private void initObserve() {
        parentViewModel.stopMusic.observe(this, it -> stopPlay());
    }

    private void iniListener() {
        viewModel.categoryAdapter.setOnItemClickListener((view, position, musics, type) -> {
            switch (type) {
                case 0:
                    stopPlay();
                    playAudio(view, musics);
                    break;
                case 1:
                    sessionManager.saveFavouriteMusic(musics.getSoundId());
                    if (sessionManager != null && sessionManager.getFavouriteMusic() != null) {
                        view.setIsFav(sessionManager.getFavouriteMusic().contains(musics.getSoundId()));
                    }
                    if (this.type == 1) {
                        viewModel.musicsListAdapter.removeItem(position);
                    }
                    break;
                case 2:
                    stopPlay();
                    parentViewModel.music.setValue(musics);
                    break;
                default:
                    break;
            }
        });
        viewModel.musicsListAdapter.setOnMusicClick(viewModel.categoryAdapter.getOnItemClickListener());
        viewModel.categoryAdapter.setOnItemMoreClickListener(lists -> {
            parentViewModel.searchMusicAdapter.updateData(lists);
            parentViewModel.isMore.set(true);
            if (getParentFragment() != null) {
                getParentFragment().getParentFragment().getChildFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
                        .replace(R.id.frame, new SearchMusicFragment())
                        .addToBackStack(SearchMusicFragment.class.getSimpleName())
                        .commit();
            }
            stopPlay();
        });
    }

    public void playAudio(ItemMusicBinding view, final Musics.SoundList musics) {

        if (previousView != null) {
            previousView.btnSelect.setVisibility(View.GONE);
            previousView.spinKit.setVisibility(View.GONE);
        }
        previousView = view;
        if (previousUrl.equals(musics.getSound())) {
            previousUrl = "none";
            previousView.btnSelect.setVisibility(View.VISIBLE);
        } else {
            if (getActivity() != null) {
                previousUrl = musics.getSound();
                previousView.btnSelect.setVisibility(View.VISIBLE);
                player = new SimpleExoPlayer.Builder(getActivity()).build();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                        Util.getUserAgent(getActivity(), "BubbleTok"));
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(Const.ITEM_BASE_URL + musics.getSound()));
                player.prepare(videoSource);
                player.addListener(this);
                player.setPlayWhenReady(true);
            }
        }

    }

    private void stopPlay() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.removeListener(this);
            player.release();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            previousView.spinKit.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            previousView.spinKit.setVisibility(View.GONE);
        }
    }
}