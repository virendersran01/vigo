package com.retrytech.vilo.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.retrytech.vilo.adapter.MusicsCategoryAdapter;
import com.retrytech.vilo.adapter.MusicsListAdapter;
import com.retrytech.vilo.utils.Global;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MusicChildViewModel extends ViewModel {

    public int type = 0;
    public MusicsCategoryAdapter categoryAdapter = new MusicsCategoryAdapter();
    public MusicsListAdapter musicsListAdapter = new MusicsListAdapter();
    private CompositeDisposable disposable = new CompositeDisposable();


    public void getMusicList() {
        disposable.add(Global.initRetrofit().getSoundList(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())

                .subscribe((soundList, throwable) -> {
                    if (soundList != null && soundList.getStatus() && soundList.getData() != null && !soundList.getData().isEmpty()) {
                        categoryAdapter.updateData(soundList.getData());
                    }
                }));
    }


    public void getFavMusicList(List<String> favouriteMusic) {
        JsonObject jsonObject = new JsonObject();
        JsonArray ids = new JsonArray();
        for (int i = 0; i < favouriteMusic.size(); i++) {
            ids.add(favouriteMusic.get(i));
        }
        jsonObject.add("sound_ids", ids);
        disposable.add(Global.initRetrofit().getFavSoundList(Global.ACCESS_TOKEN, jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((soundList, throwable) -> {
                    if (soundList != null/* && soundList.getStatus()*/ && soundList.getData() != null && !soundList.getData().isEmpty()) {
                        musicsListAdapter.updateData(soundList.getData());
                    }
                }));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
