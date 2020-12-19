package com.retrytech.vilo.view.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityHashtagBinding;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.viewmodel.HashTagViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

public class HashTagActivity extends BaseActivity {

    ActivityHashtagBinding binding;

    HashTagViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hashtag);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new HashTagViewModel()).createFor()).get(HashTagViewModel.class);

        initView();
        initObserve();
        initListeners();

        binding.setViewmodel(viewModel);

    }

    private void initListeners() {
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (getIntent().getStringExtra("hashtag") != null) {
            viewModel.hashtag = getIntent().getStringExtra("hashtag");
        }
        binding.refreshlout.setEnableRefresh(false);
        viewModel.adapter.setHashTag(true);
        viewModel.adapter.setWord(viewModel.hashtag);
        viewModel.fetchHashTagVideos(false);


    }

    private void initObserve() {
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
        viewModel.video.observe(this, video -> binding.tvVideoCount.setText(Global.prettyCount(video.getPostCount()).concat(" Videos")));
    }
}