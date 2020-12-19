package com.retrytech.vilo.view.profile;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.retrytech.vilo.R;
import com.retrytech.vilo.adapter.FollowersPagerAdapter;
import com.retrytech.vilo.databinding.ActivityFollowerFollowingBinding;
import com.retrytech.vilo.model.user.User;
import com.retrytech.vilo.viewmodel.FollowerFollowingViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class FollowerFollowingActivity extends AppCompatActivity {

    ActivityFollowerFollowingBinding binding;
    FollowerFollowingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follower_following);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new FollowerFollowingViewModel()).createFor()).get(FollowerFollowingViewModel.class);
        initView();
        initListeners();
        binding.setViewmodel(viewModel);
    }

    private void initView() {

        FollowersPagerAdapter adapter = new FollowersPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewModel.itemType.set(getIntent().getIntExtra("itemtype", 0));
        viewModel.user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setCurrentItem(viewModel.itemType.get());

    }

    private void initListeners() {
        binding.tvVids.setOnClickListener(v -> binding.viewPager.setCurrentItem(0));
        binding.tvUsers.setOnClickListener(v -> binding.viewPager.setCurrentItem(1));
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.i("", "");

            }

            @Override
            public void onPageSelected(int position) {
                viewModel.itemType.set(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("", "");
            }
        });
    }
}