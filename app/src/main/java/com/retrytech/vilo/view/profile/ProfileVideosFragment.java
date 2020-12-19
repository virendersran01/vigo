package com.retrytech.vilo.view.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentProfileVideosBinding;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.viewmodel.ProfileVideosViewModel;
import com.retrytech.vilo.viewmodel.ProfileViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class ProfileVideosFragment extends Fragment {

    FragmentProfileVideosBinding binding;
    ProfileVideosViewModel viewModel;
    ProfileViewModel parentViewModel;

    public static ProfileVideosFragment getNewInstance(int vidType) {
        ProfileVideosFragment fragment = new ProfileVideosFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", vidType);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_videos, container, false);

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getParentFragment() != null) {
            parentViewModel = ViewModelProviders.of(getParentFragment()).get(ProfileViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ProfileVideosViewModel()).createFor()).get(ProfileVideosViewModel.class);
        initView();
        initListeners();
        initObserve();

    }

    private void initView() {
        if (getArguments() != null) {
            viewModel.vidType = getArguments().getInt("type");
        }
        viewModel.userId = parentViewModel.userId;

        if (viewModel.vidType == 0) {
            viewModel.userVidStart = 0;
            viewModel.fetchUserVideos(false);
        } else {
            viewModel.likeVidStart = 0;
            viewModel.fetchUserLikedVideos(false);
        }
        binding.refreshlout.setEnableRefresh(false);

    }

    private void initObserve() {
        parentViewModel.selectPosition.observe(getViewLifecycleOwner(), position -> {
            if (position != null && position == 3) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fall_in);
                binding.recyclerview.startAnimation(animation);
                if (viewModel.userId == null || viewModel.userId.isEmpty()) {
                    viewModel.userId = Global.USER_ID;
                }
                viewModel.likeVidStart = 0;
                viewModel.userVidStart = 0;
                if (viewModel.vidType == 0) {
                    viewModel.fetchUserVideos(false);
                } else {
                    viewModel.fetchUserLikedVideos(false);
                }
                binding.setViewModel(viewModel);
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
    }

    private void initListeners() {
        viewModel.adapter.setOnRecyclerViewItemClick((model, position, binding) -> new CustomDialogBuilder(getContext()).showSimpleDialog("Delete post !", "Do you really want to\ndelete this post?", "Cancel", "yes", new CustomDialogBuilder.OnDismissListener() {
            @Override
            public void onPositiveDismiss() {
                viewModel.deletePost(model.getPostId(), position);
            }

            @Override
            public void onNegativeDismiss() {
                Log.i("", "");
            }
        }));

        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> {
            if (viewModel.vidType == 0) {
                viewModel.onUserVideoLoadMore();
            } else {
                viewModel.onUserLikedVideoLoadMore();
            }
        });
    }


}