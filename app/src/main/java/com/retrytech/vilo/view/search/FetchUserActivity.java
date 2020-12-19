package com.retrytech.vilo.view.search;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityFetchUserBinding;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.profile.ProfileFragment;

public class FetchUserActivity extends BaseActivity {


    ActivityFetchUserBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fetch_user);

        String userid = getIntent().getStringExtra("userid");

        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userid", userid);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lout_main, fragment)
                .commit();

    }

}