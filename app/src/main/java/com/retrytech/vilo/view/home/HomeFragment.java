package com.retrytech.vilo.view.home;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.retrytech.vilo.R;
import com.retrytech.vilo.adapter.HomeViewPagerAdapter;
import com.retrytech.vilo.databinding.FragmentMainBinding;
import com.retrytech.vilo.view.base.BaseFragment;
import com.retrytech.vilo.viewmodel.HomeViewModel;
import com.retrytech.vilo.viewmodel.MainViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {


    private FragmentMainBinding binding;
    private MainViewModel parentViewModel;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        viewModel = ViewModelProviders.of(requireActivity(), new ViewModelFactory(new HomeViewModel()).createFor()).get(HomeViewModel.class);
        initView();
        initViewPager();
        initObserve();
        initListener();
    }

    private void initView() {
        binding.tvFollowing.setTextColor(getResources().getColor(R.color.grey));
    }

    private void initViewPager() {
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(homeViewPagerAdapter);
        binding.viewPager.setCurrentItem(1);
        viewModel.onPageSelect.postValue(1);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("", "");
            }

            @Override
            public void onPageSelected(int position) {
                viewModel.onPageSelect.postValue(position);
                viewModel.onStop.setValue(true);
                if (position == 0) {
                    binding.tvForu.setTextColor(getResources().getColor(R.color.grey));
                    binding.tvFollowing.setTextColor(getResources().getColor(R.color.white));
                } else {
                    binding.tvFollowing.setTextColor(getResources().getColor(R.color.grey));
                    binding.tvForu.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("", "");
            }
        });
    }

    private void initObserve() {
        parentViewModel.onStop.observe(getViewLifecycleOwner(), onStop -> {
            viewModel.onPageSelect.setValue(binding.viewPager.getCurrentItem());
            viewModel.onStop.postValue(onStop);
        });
        viewModel.loadingVisibility = parentViewModel.loadingVisibility;

    }

    private void initListener() {
        binding.tvFollowing.setOnClickListener(v -> {
            binding.tvForu.setTextColor(getResources().getColor(R.color.grey));
            binding.tvFollowing.setTextColor(getResources().getColor(R.color.white));
            binding.viewPager.setCurrentItem(0);
        });
        binding.tvForu.setOnClickListener(v -> {
            binding.tvFollowing.setTextColor(getResources().getColor(R.color.grey));
            binding.tvForu.setTextColor(getResources().getColor(R.color.white));
            binding.viewPager.setCurrentItem(1);
        });
    }

}
