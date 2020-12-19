package com.retrytech.vilo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.retrytech.vilo.view.profile.FollowersFragment;

public class FollowersPagerAdapter extends FragmentPagerAdapter {


    public FollowersPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FollowersFragment.getNewInstance(String.valueOf(position));
    }

    @Override
    public int getCount() {
        return 2;
    }
}
