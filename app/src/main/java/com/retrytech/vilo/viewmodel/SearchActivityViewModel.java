package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.SearchUserAdapter;
import com.retrytech.vilo.adapter.VideoListAdapter;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchActivityViewModel extends ViewModel {
    public ObservableInt searchtype = new ObservableInt(0);
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public String searchText;
    public int userStart = 0;
    public int videoStart = 0;
    public ObservableBoolean noUserData = new ObservableBoolean(false);
    public ObservableBoolean noVideoData = new ObservableBoolean(false);
    private int count = 10;
    public SearchUserAdapter searchUseradapter = new SearchUserAdapter();
    public VideoListAdapter videoListAdapter = new VideoListAdapter();
    private CompositeDisposable disposable = new CompositeDisposable();
    ObservableBoolean isloading = new ObservableBoolean(true);

    public void searchForUser(boolean isLoadMore) {


        disposable.add(Global.initRetrofit().searchUser(searchText, count, userStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isloading.set(false);
                })
                .subscribe((searchUser, throwable) -> {
                    if (searchUser != null && searchUser.getData() != null) {
                        if (isLoadMore) {
                            searchUseradapter.loadMore(searchUser.getData());
                        } else {
                            searchUseradapter.updateData(searchUser.getData());
                        }

                        userStart = userStart + count;
                    }
                    noUserData.set(searchUseradapter.getData().isEmpty());

                }));
    }

    public void searchForVideos(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().searchVideo(searchText, count, videoStart, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate(() -> onLoadMoreComplete.setValue(true))
                .subscribe((searchVideo, throwable) -> {
                    if (searchVideo != null && searchVideo.getData() != null) {
                        if (isLoadMore) {
                            videoListAdapter.loadMore(searchVideo.getData());
                        } else {
                            videoListAdapter.updateData(searchVideo.getData());
                        }
                        isloading.set(false);
                        videoStart = videoStart + count;
                    }
                    noVideoData.set(videoListAdapter.getData().isEmpty());

                }));
    }

    public void afterTextChanged(CharSequence s) {
        searchText = s.toString();

        if (searchtype.get() == 1) {
            userStart = 0;
            searchForUser(false);
        } else {
            videoStart = 0;
            videoListAdapter.setWord(searchText);
            searchForVideos(false);
        }
    }

    public void onUserLoadMore() {

        searchForUser(true);
    }

    public void onVideoLoadMore() {
        searchForVideos(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
