package com.retrytech.vilo.view.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentMusicMainBinding;
import com.retrytech.vilo.view.base.BaseFragment;
import com.retrytech.vilo.viewmodel.MusicMainViewModel;
import com.retrytech.vilo.viewmodel.MusicViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class MusicMainFragment extends BaseFragment {


    private FragmentMusicMainBinding binding;
    private MusicViewModel viewModel;
    private MusicMainViewModel parentViewModel;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getParentFragment() != null) {
            parentViewModel = ViewModelProviders.of(getParentFragment()).get(MusicMainViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new MusicViewModel()).createFor()).get(MusicViewModel.class);
        initListener();
        binding.setViewModel(viewModel);
    }

    private void initListener() {
        openFragment(0);
        viewModel.music = parentViewModel.music;
        viewModel.isMore = parentViewModel.isMore;
        viewModel.searchMusicAdapter = parentViewModel.searchMusicAdapter;
        parentViewModel.stopMusic.observe(getViewLifecycleOwner(), aBoolean -> viewModel.stopMusic.setValue(aBoolean));
        binding.tvDiscover.setOnClickListener(v -> {
            viewModel.selectPosition.set(0);
            parentViewModel.stopMusic.setValue(true);
            openFragment(0);
        });
        binding.tvFavourite.setOnClickListener(v -> {
            viewModel.selectPosition.set(1);
            parentViewModel.stopMusic.setValue(true);
            openFragment(1);
        });

    }

    private void openFragment(int position) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame, MusicChildFragment.newInstance(position))
                .commit();
    }

}