package com.retrytech.vilo.view.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentSearchItemBinding;
import com.retrytech.vilo.viewmodel.SearchActivityViewModel;

import org.jetbrains.annotations.NotNull;


public class SearchItemFragment extends Fragment {

    public int searchType;
    FragmentSearchItemBinding binding;
    SearchActivityViewModel parentViewModel;

    public static SearchItemFragment getNewInstance(int searchType) {
        SearchItemFragment fragment = new SearchItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", searchType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_item, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(SearchActivityViewModel.class);
        }
        if (getArguments() != null) {
            searchType = getArguments().getInt("type");
        }
        initView();
        initListeners();
        initObserve();
        binding.setFragment(this);
        binding.setViewmodel(parentViewModel);
    }

    private void initListeners() {

        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> {
            if (searchType == 1) {
                parentViewModel.onUserLoadMore();
            } else {
                parentViewModel.onVideoLoadMore();
            }
        });

    }

    private void initView() {

        binding.refreshlout.setEnableRefresh(false);
        if (searchType == 0) {
            binding.recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            parentViewModel.searchForVideos(false);

        } else {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            parentViewModel.searchForUser(false);

        }


    }

    private void initObserve() {
        parentViewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
    }

}