package com.retrytech.vilo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.retrytech.vilo.view.home.HomeFragment;
import com.retrytech.vilo.view.notification.NotificationFragment;
import com.retrytech.vilo.view.profile.ProfileFragment;
import com.retrytech.vilo.view.search.SearchFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new NotificationFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
