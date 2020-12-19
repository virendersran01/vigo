package com.retrytech.vilo.view.search;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentSearchBinding;
import com.retrytech.vilo.viewmodel.MainViewModel;
import com.retrytech.vilo.viewmodel.SearchFragmentViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    SearchFragmentViewModel viewModel;
    FragmentSearchBinding binding;
    private MainViewModel parentViewModel;
    private static final int MY_PERMISSIONS_REQUEST = 101;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getContext() fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            parentViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        }
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new SearchFragmentViewModel()).createFor()).get(SearchFragmentViewModel.class);
        initView();
        initObserve();
        initListeners();
        binding.setViewmodel(viewModel);
    }

    private void initView() {
        binding.refreshlout.setEnableRefresh(false);
    }


    private void initObserve() {
        parentViewModel.selectedPosition.observe(this, position -> {
            if (position != null && position == 1) {
                viewModel.exploreStart = 0;
                viewModel.fetchExploreItems(false);
                parentViewModel.selectedPosition.setValue(null);
            }
        });
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> binding.refreshlout.finishLoadMore());
    }

    private void initListeners() {

        binding.imgSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            intent.putExtra("search", binding.etSearch.getText().toString());
            startActivity(intent);
        });


        binding.imgQr.setOnClickListener(v -> initPermission());

        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onExploreLoadMore());

    }

    private void initPermission() {
        if (getActivity() != null && getActivity().getPackageManager() != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST);
            } else {
                startActivity(new Intent(getContext(), QRScanActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED && getActivity() != null && getActivity().getPackageManager() != null) {
            startActivity(new Intent(getContext(), QRScanActivity.class));
        }
    }

}
