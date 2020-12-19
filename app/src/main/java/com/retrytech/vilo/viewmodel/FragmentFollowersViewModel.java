package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.FollowersAdapter;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentFollowersViewModel extends ViewModel {

    public FollowersAdapter adapter = new FollowersAdapter();
    public String itemType;
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public String userId;
    ObservableBoolean isloading = new ObservableBoolean(true);
    private CompositeDisposable disposable = new CompositeDisposable();
    private int followerStart = 0;
    private int followingStart = 0;
    private int count = 15;


    public void fetchFollowers(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getFollowerList(Global.ACCESS_TOKEN, userId, count, followerStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isloading.set(false);
                })
                .subscribe((follower, throwable) -> {
                    if (follower != null && follower.getData() != null) {
                        if (isLoadMore) {
                            adapter.loadMore(follower.getData());
                        } else {
                            adapter.updateData(follower.getData());
                        }
                        followerStart = followerStart + count;

                    }
                }));
    }

    public void fetchFollowing(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getFollowingList(Global.ACCESS_TOKEN, userId, count, followingStart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isloading.set(false);
                })
                .subscribe((following, throwable) -> {
                    if (following != null && following.getData() != null) {
                        if (isLoadMore) {
                            adapter.loadMore(following.getData());
                        } else {
                            adapter.updateData(following.getData());
                        }
                        followingStart = followingStart + count;
                    }
                }));
    }


    public void onFollowersLoadMore() {
        fetchFollowers(true);
    }

    public void onFollowingLoadMore() {
        fetchFollowing(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }


}
