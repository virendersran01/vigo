package com.retrytech.vilo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.retrytech.vilo.view.search.SearchItemFragment;

public class SearchItemPagerAdapter extends FragmentPagerAdapter {

    public SearchItemPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SearchItemFragment.getNewInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
