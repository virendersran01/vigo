package com.retrytech.vilo.view.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.budiyev.android.codescanner.CodeScanner;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityQRScanBinding;
import com.retrytech.vilo.view.base.BaseActivity;

public class QRScanActivity extends BaseActivity {

    ActivityQRScanBinding binding;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_q_r_scan);
        initListeners();
        mCodeScanner.startPreview();
    }

    private void initListeners() {
        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setDecodeCallback(result -> QRScanActivity.this.runOnUiThread(() -> {
            Intent intent = new Intent(QRScanActivity.this, FetchUserActivity.class);
            intent.putExtra("userid", result.getText());
            startActivity(intent);
        }));
        binding.scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();

    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();

    }
}