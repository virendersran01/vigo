package com.retrytech.vilo.view.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityRedeemBinding;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.home.MainActivity;
import com.retrytech.vilo.view.web.WebViewActivity;
import com.retrytech.vilo.viewmodel.RedeemViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

public class RedeemActivity extends BaseActivity {

    ActivityRedeemBinding binding;
    RedeemViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_redeem);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new RedeemViewModel()).createFor()).get(RedeemViewModel.class);

        initView();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        if (getIntent().getStringExtra("coins") != null) {
            viewModel.coindCount = getIntent().getStringExtra("coins");
            viewModel.coinRate = getIntent().getStringExtra("coinrate");
            binding.tvCount.setText(Global.prettyCount(Integer.parseInt(viewModel.coindCount)));
        }
    }


    private void initListeners() {
        binding.tvTerm.setOnClickListener(v -> startActivity(new Intent(this, WebViewActivity.class)));
        String[] paymentTypes = getResources().getStringArray(R.array.payment);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.color_text_light));
                viewModel.requestType = paymentTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("", "");
            }
        });
        binding.btnRedeem.setOnClickListener(v -> {
            if (viewModel.accountId != null && !TextUtils.isEmpty(viewModel.accountId)) {
                viewModel.callApiToRedeem();
            } else {
                Toast.makeText(this, "Please enter your account ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initObserve() {
        viewModel.redeem.observe(this, redeem -> {
            if (redeem != null && redeem.getStatus()) {
                Toast.makeText(this, "Request Submitted Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            }
        });
    }


}