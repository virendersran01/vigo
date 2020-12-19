package com.retrytech.vilo.utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class GlobalApi {

    private CompositeDisposable disposable = new CompositeDisposable();

    public void rewardUser(String rewardActionId) {
        disposable.add(Global.initRetrofit().rewardUser(Global.ACCESS_TOKEN, rewardActionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((reward, throwable) -> {

                }));
    }

    public void increaseView(String postId) {
        disposable.add(Global.initRetrofit().increaseView(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((reward, throwable) -> {

                }));
    }
}
