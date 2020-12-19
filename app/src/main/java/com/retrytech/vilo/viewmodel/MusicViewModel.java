package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.MusicsListAdapter;
import com.retrytech.vilo.model.music.Musics;

public class MusicViewModel extends ViewModel {
    public ObservableInt selectPosition = new ObservableInt(0);

    public ObservableBoolean isMore = new ObservableBoolean(false);
    public MusicsListAdapter searchMusicAdapter = new MusicsListAdapter();
    public MutableLiveData<Musics.SoundList> music = new MutableLiveData<>();
    public MutableLiveData<Boolean> stopMusic = new MutableLiveData<>();
}
