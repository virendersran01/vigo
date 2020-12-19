package com.retrytech.vilo.view.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ActivityEditProfileBinding;
import com.retrytech.vilo.view.base.BaseActivity;
import com.retrytech.vilo.view.media.BottomSheetImagePicker;
import com.retrytech.vilo.viewmodel.EditProfileViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import static com.retrytech.vilo.utils.BindingAdapters.loadMediaImage;

public class EditProfileActivity extends BaseActivity {

    ActivityEditProfileBinding binding;
    private EditProfileViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        startReceiver();
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new EditProfileViewModel()).createFor()).get(EditProfileViewModel.class);
        initView();
        initObserve();
        initListener();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        viewModel.user = sessionManager.getUser();
        viewModel.updateData();
        if (viewModel.user != null) {
            viewModel.curUserName = sessionManager.getUser().getData().getUserName();
            if (viewModel.user.getData().getBio() != null && !viewModel.user.getData().getBio().isEmpty()) {
                viewModel.length.set(viewModel.user.getData().getBio().length());
            }
        }
    }

    private void initObserve() {
        viewModel.updateProfile.observe(this, isUpdate -> {
            if (isUpdate != null && isUpdate) {
                Intent intent = new Intent();
                intent.putExtra("user", new Gson().toJson(viewModel.user));
                sessionManager.saveUser(viewModel.user);
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }

    private void initListener() {
        binding.setOnChangeClick(view -> showPhotoSelectSheet());

        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }


    private void showPhotoSelectSheet() {
        BottomSheetImagePicker bottomSheetImagePicker = new BottomSheetImagePicker();
        bottomSheetImagePicker.setOnDismiss(uri -> {
            if (!uri.isEmpty()) {
                loadMediaImage(binding.profileImg, uri, true);
                viewModel.imageUri = uri;
            }
        });
        bottomSheetImagePicker.show(getSupportFragmentManager(), BottomSheetImagePicker.class.getSimpleName());
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkChanges();
        super.onDestroy();
    }


}