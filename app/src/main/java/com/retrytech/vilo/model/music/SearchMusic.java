
package com.retrytech.vilo.model.music;

import com.google.gson.annotations.Expose;

import java.util.List;


public class SearchMusic {

    @Expose
    private List<Musics.SoundList> data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public List<Musics.SoundList> getData() {
        return data;
    }

    public void setData(List<Musics.SoundList> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
