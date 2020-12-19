package com.retrytech.vilo.view.search;

import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.retrytech.vilo.R;
import com.retrytech.vilo.adapter.SearchItemPagerAdapter;
import com.retrytech.vilo.databinding.ActivitySearchBinding;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.viewmodel.SearchActivityViewModel;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class SearchActivity extends BaseActivity {

    ActivitySearchBinding binding;
    SearchActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        viewModel = ViewModelProviders.of(this).get(SearchActivityViewModel.class);
        initView();
        initListeners();
        binding.setViewmodel(viewModel);

    }


    private void initView() {

        viewModel.searchText = getIntent().getStringExtra("search");
        binding.etSearch.setText(viewModel.searchText);

        SearchItemPagerAdapter adapter = new SearchItemPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(adapter);


    }

    private void initListeners() {

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.tvVids.setOnClickListener(v -> {
            viewModel.searchtype.set(0);
            binding.viewPager.setCurrentItem(0);
        });

        binding.tvUsers.setOnClickListener(v -> {
            viewModel.searchtype.set(1);
            binding.viewPager.setCurrentItem(1);
        });
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("", "");
            }

            @Override
            public void onPageSelected(int position) {
                viewModel.searchtype.set(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("", "");
            }
        });
    }

}