package com.retrytech.vilo.view.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentNotificationBinding;
import com.retrytech.vilo.viewmodel.MainViewModel;
import com.retrytech.vilo.viewmodel.NotificationViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    private MainViewModel parentViewModel;
    NotificationViewModel viewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new NotificationViewModel()).createFor()).get(NotificationViewModel.class);
        initView();
        initListeners();
        initObserve();
        binding.setViewModel(viewModel);
    }

    private void initView() {
        binding.refreshlout.setEnableRefresh(false);
    }

    private void initListeners() {
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.fetchNotificationData(true));
    }

    private void initObserve() {
        parentViewModel.selectedPosition.observe(this, position -> {

            if (position != null && position == 2) {
                viewModel.start = 0;
                viewModel.fetchNotificationData(false);
                parentViewModel.selectedPosition.setValue(null);
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
    }
}
