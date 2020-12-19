package com.retrytech.vilo.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentFollowersBinding;
import com.retrytech.vilo.view.search.FetchUserActivity;
import com.retrytech.vilo.viewmodel.FollowerFollowingViewModel;
import com.retrytech.vilo.viewmodel.FragmentFollowersViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class FollowersFragment extends Fragment {


    FragmentFollowersBinding binding;
    FragmentFollowersViewModel viewModel;
    FollowerFollowingViewModel parentViewModel;


    public static FollowersFragment getNewInstance(String type) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_followers, container, false);


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(FollowerFollowingViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new FragmentFollowersViewModel()).createFor()).get(FragmentFollowersViewModel.class);

        initView();
        initObserver();
        initListener();
        binding.setViewmodel(viewModel);

    }

    private void initView() {
        viewModel.userId = parentViewModel.user.getData().getUserId();
        binding.refreshlout.setEnableRefresh(false);
        if (getArguments() != null) {
            viewModel.itemType = getArguments().getString("type");
        }
        if (viewModel.itemType != null && viewModel.itemType.equals("0")) {
            viewModel.fetchFollowers(false);
        } else {
            viewModel.fetchFollowing(false);
        }
    }

    private void initListener() {
        viewModel.adapter.setOnRecyclerViewItemClick((data, position) -> {
            Intent intent = new Intent(getContext(), FetchUserActivity.class);
            if (viewModel.itemType.equals("0")) {
                intent.putExtra("userid", data.getFromUserId());
            } else {
                intent.putExtra("userid", data.getToUserId());
            }
            startActivity(intent);
        });
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> {
            if (viewModel.itemType.equals("0")) {
                viewModel.onFollowersLoadMore();
            } else {
                viewModel.onFollowingLoadMore();
            }
        });
    }

    private void initObserver() {
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
    }
}