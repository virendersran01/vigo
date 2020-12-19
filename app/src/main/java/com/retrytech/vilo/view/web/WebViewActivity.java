package com.retrytech.vilo.view.web;


import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityWebViewBinding;
import com.retrytech.vilo.view.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    ActivityWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        binding.webview.loadUrl("http://instamask.invatomarket.com/terms&conditions.html");
        binding.imgBack.setOnClickListener(v -> onBackPressed());

    }
}