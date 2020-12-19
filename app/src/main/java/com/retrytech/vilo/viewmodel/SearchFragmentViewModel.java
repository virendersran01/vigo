package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.retrytech.vilo.adapter.ExploreHashTagAdapter;
import com.retrytech.vilo.utils.Global;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragmentViewModel extends ViewModel {

    public int exploreStart = 0;
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    public ExploreHashTagAdapter adapter = new ExploreHashTagAdapter();
    ObservableBoolean isloading = new ObservableBoolean(true);
    private CompositeDisposable disposable = new CompositeDisposable();
    private int count = 10;

    public void fetchExploreItems(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getExploreVideos(count, exploreStart, Global.USER_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isloading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isloading.set(false);
                })
                .subscribe((explore, throwable) -> {
                    if (explore != null && explore.getData() != null) {
                        if (isLoadMore) {
                            adapter.loadMore(explore.getData());
                        } else {
                            if (!new Gson().toJson(explore.getData()).equals(new Gson().toJson(adapter.getData()))) {
                                adapter.updateData(explore.getData());
                            }
                        }
                        exploreStart = exploreStart + count;
                    }
                }));
    }

    public void onExploreLoadMore() {

        fetchExploreItems(true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
