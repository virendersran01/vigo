package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.model.user.RestResponse;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RedeemViewModel extends ViewModel {

    public String coindCount;
    public String coinRate;
    public String requestType;
    public String accountId;
    public ObservableBoolean isLoading = new ObservableBoolean();
    public MutableLiveData<RestResponse> redeem = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    public void afterPaymentAccountChanged(CharSequence s) {
        accountId = s.toString();
    }

    public void callApiToRedeem() {

        disposable.add(Global.initRetrofit().sendRedeemRequest(Global.ACCESS_TOKEN, coindCount, requestType, accountId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.set(true))
                .doOnTerminate(() -> isLoading.set(false))
                .subscribe((redeem1, throwable) -> {
                    if (redeem1 != null && redeem1.getStatus() != null) {
                        this.redeem.setValue(redeem1);
                    }
                }));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
