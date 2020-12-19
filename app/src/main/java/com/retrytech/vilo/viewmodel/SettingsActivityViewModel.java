package com.retrytech.vilo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.model.user.RestResponse;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SettingsActivityViewModel extends ViewModel {

    public CompositeDisposable disposable = new CompositeDisposable();
    public MutableLiveData<RestResponse> logOut = new MutableLiveData<>();
    public MutableLiveData<RestResponse> updateToken = new MutableLiveData<>();

    public void logOutUser() {
        disposable.add(Global.initRetrofit().logOutUser(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())

                .subscribe((logoutUser, throwable) -> {
                    if (logoutUser != null && logoutUser.getStatus() != null) {
                        logOut.setValue(logoutUser);
                    }
                }));
    }

    public void updateFireBaseToken(String token) {

        disposable.add(Global.initRetrofit().updateToken(Global.ACCESS_TOKEN, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((updatetoken, throwable) -> this.updateToken.setValue(updatetoken)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
