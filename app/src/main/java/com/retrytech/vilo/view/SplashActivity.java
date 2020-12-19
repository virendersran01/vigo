package com.retrytech.vilo.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivitySplashBinding;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.home.MainActivity;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            binding.executePendingBindings();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 500);
    }
}
