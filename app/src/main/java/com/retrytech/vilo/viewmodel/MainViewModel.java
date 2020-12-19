package com.retrytech.vilo.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public MutableLiveData<Integer> selectedPosition = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> onStop = new MutableLiveData<>();
    public ObservableInt loadingVisibility = new ObservableInt(View.GONE);
    public boolean isBack = false;
}
