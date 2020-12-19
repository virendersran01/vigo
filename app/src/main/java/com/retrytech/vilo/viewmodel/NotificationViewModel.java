package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.NotificationAdapter;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationViewModel extends ViewModel {
    public String userId;
    public int count = 15, start = 0;
    private CompositeDisposable disposable = new CompositeDisposable();
    private ObservableBoolean isLoading = new ObservableBoolean();
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public NotificationAdapter adapter = new NotificationAdapter();
    public ObservableBoolean isEmpty = new ObservableBoolean(false);

    public void fetchNotificationData(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getNotificationList(Global.ACCESS_TOKEN, Global.USER_ID, count, start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isLoading.set(false);
                })
                .subscribe((notification, throwable) -> {
                    if (notification != null && notification.getData() != null) {
                        if (isLoadMore) {
                            adapter.loadMore(notification.getData());
                        } else {
                            adapter.updateData(notification.getData());
                        }
                        start = start + count;
                    }
                    isEmpty.set(adapter.getData().isEmpty());
                }));
    }
}
