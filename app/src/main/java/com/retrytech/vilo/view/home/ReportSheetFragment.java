package com.retrytech.vilo.view.home;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentReportSheetBinding;
import com.retrytech.vilo.view.web.WebViewActivity;
import com.retrytech.vilo.viewmodel.ReportViewmodel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class ReportSheetFragment extends BottomSheetDialogFragment {


    FragmentReportSheetBinding binding;
    ReportViewmodel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialog1;
            dialog.setCanceledOnTouchOutside(false);
            FrameLayout bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetBehavior.from(bottomSheet).setSkipCollapsed(true);
                BottomSheetBehavior.from(bottomSheet).setHideable(true);
            }

        });

        return bottomSheetDialog;

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_sheet, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new ReportViewmodel()).createFor()).get(ReportViewmodel.class);

        initView();
        initListeners();
        initObserve();

        binding.setViewmodel(viewModel);
    }

    private void initListeners() {
        if (getActivity() != null) {
            binding.tvTerm.setOnClickListener(v -> startActivity(new Intent(getActivity(), WebViewActivity.class)));
            String[] reasons = getActivity().getResources().getStringArray(R.array.report_reasons);

            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.color_text_light));
                    viewModel.reason = reasons[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.i("", "");
                }
            });
        }
        binding.btnReport.setOnClickListener(v -> viewModel.callApiToReport());
        binding.imgBack.setOnClickListener(v -> dismiss());
    }

    private void initView() {
        if (getArguments() != null) {
            viewModel.reportType = getArguments().getInt("reporttype", 0);
            viewModel.postId = getArguments().getString("postid");
            viewModel.userId = getArguments().getString("userid");
        }
    }

    private void initObserve() {
        viewModel.isValid.observe(this, isValid -> Toast.makeText(getContext(), "Please fill all the details..", Toast.LENGTH_SHORT).show());
    }

}