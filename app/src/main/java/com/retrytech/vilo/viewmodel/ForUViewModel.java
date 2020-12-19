package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.FamousCreatorAdapter;
import com.retrytech.vilo.adapter.VideoFullAdapter;
import com.retrytech.vilo.model.user.RestResponse;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ForUViewModel extends ViewModel {

    public VideoFullAdapter adapter = new VideoFullAdapter();
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public int start = 0;
    public int count = 10;
    public String postType;
    ObservableBoolean isloading = new ObservableBoolean(true);
    private CompositeDisposable disposable = new CompositeDisposable();
    public FamousCreatorAdapter famousAdapter = new FamousCreatorAdapter();
    public ObservableBoolean isEmpty = new ObservableBoolean(false);
    public MutableLiveData<RestResponse> coinSend = new MutableLiveData<>();


    public void fetchPostVideos(boolean isLoadMore) {
        disposable.add(Global.initRetrofit().getPostVideos(postType, 10, start, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(() -> onLoadMoreComplete.setValue(true))
                .subscribe((video, throwable) -> {
                    if (video != null && video.getData() != null && !video.getData().isEmpty()) {
                        if (isLoadMore) {
                            adapter.loadMore(video.getData());
                        } else {
                            adapter.updateData(video.getData());
                        }
                        isloading.set(false);
                        start = start + count;
                    } else {
                        if (adapter.getData().isEmpty() && postType.equals("following")) {
                            fetchFamousVideos();
                            isEmpty.set(true);
                        }
                    }

                }));
    }

    private void fetchFamousVideos() {

        disposable.add(Global.initRetrofit().getPostVideos("trending", 50, 0, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(() -> onLoadMoreComplete.setValue(true))
                .subscribe((video, throwable) -> {
                    if (video != null && video.getData() != null && !video.getData().isEmpty()) {
                        famousAdapter.updateData(video.getData());
                    }
                }));
    }

    public void onLoadMore() {
        fetchPostVideos(true);
    }

    public void likeUnlikePost(String postId) {

        disposable.add(Global.initRetrofit().likeUnlike(Global.ACCESS_TOKEN, postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((likeRequest, throwable) -> {

                }));
    }

    public void sendBubble(String toUserId, String coin) {

        disposable.add(Global.initRetrofit().sendCoin(Global.ACCESS_TOKEN, coin, toUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(() -> onLoadMoreComplete.setValue(true))
                .subscribe((coinsend, throwable) -> {
                    if (coinsend != null && coinsend.getStatus() != null) {
                        this.coinSend.setValue(coinsend);
                    }

                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
