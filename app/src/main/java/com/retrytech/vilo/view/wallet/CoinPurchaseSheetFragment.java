package com.retrytech.vilo.view.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.CustomToastBinding;
import com.retrytech.vilo.databinding.FragmentPurchaseCoinSheetBinding;
import com.retrytech.vilo.utils.CustomDialogBuilder;
import com.retrytech.vilo.viewmodel.CoinPurchaseViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class CoinPurchaseSheetFragment extends BottomSheetDialogFragment {

    FragmentPurchaseCoinSheetBinding binding;
    CoinPurchaseViewModel viewModel;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialog1;
            dialog.setCanceledOnTouchOutside(true);

        });

        return bottomSheetDialog;

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase_coin_sheet, container, false);

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new CoinPurchaseViewModel()).createFor()).get(CoinPurchaseViewModel.class);

        initView();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        viewModel.fetchCoinPlans();
    }


    private void initListeners() {
        viewModel.adapter.setListener((data, position) -> new CustomDialogBuilder(getActivity()).showSimpleDialog("Attention !", "In app purchase will be added \nat no cost ,once you purchase this code\nSkype: RetryTech", "OK", "Contact Us", new CustomDialogBuilder.OnDismissListener() {
            @Override
            public void onPositiveDismiss() {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/917990425274")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNegativeDismiss() {
                Toast.makeText(getActivity(), "Thanks for taking interest..", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initObserve() {
        viewModel.purchase.observe(this, purchase -> showPurchaseResultToast(purchase.getStatus()));
    }

    private void showPurchaseResultToast(Boolean status) {

        dismiss();
        CustomToastBinding customToastBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.custom_toast, null, false);
        if (customToastBinding != null) {
            customToastBinding.setStatus(status);
            String string;
            if (status != null && status) {
                string = "Coins Added To Your Wallet\nSuccessfully..";
            } else {
                string = "Something Went Wrong !";
            }
            customToastBinding.tvToastMessage.setText(string);
            Toast toast = new Toast(getContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(customToastBinding.getRoot());
            toast.show();
        }
    }


}