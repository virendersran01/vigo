package com.retrytech.vilo.model.music;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.retrytech.vilo.adapter.MusicsListAdapter;

import java.util.List;

public class Musics {

    @Expose
    private List<Category> data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public List<Category> getData() {
        return data;
    }

    public void setData(List<Category> data) {
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

    public static class Category {

        @SerializedName("sound_category_id")
        private String soundCategoryId;
        @SerializedName("sound_category_name")
        private String soundCategoryName;
        @SerializedName("sound_category_profile")
        private String soundCategoryProfile;
        @SerializedName("sound_list")
        private List<SoundList> soundList;
        @SerializedName("adapter")
        private Object adapter = new MusicsListAdapter();

        public String getSoundCategoryId() {
            return soundCategoryId;
        }

        public void setSoundCategoryId(String soundCategoryId) {
            this.soundCategoryId = soundCategoryId;
        }

        public String getSoundCategoryName() {
            return soundCategoryName;
        }

        public void setSoundCategoryName(String soundCategoryName) {
            this.soundCategoryName = soundCategoryName;
        }

        public String getSoundCategoryProfile() {
            return soundCategoryProfile;
        }

        public void setSoundCategoryProfile(String soundCategoryProfile) {
            this.soundCategoryProfile = soundCategoryProfile;
        }

        public List<SoundList> getSoundList() {
            return soundList;
        }

        public void setSoundList(List<SoundList> soundList) {
            this.soundList = soundList;
        }

        public Object getAdapter() {
            return adapter;
        }

        public void setAdapter(Object adapter) {
            this.adapter = adapter;
        }
    }

    public static class SoundList {

        @SerializedName("added_by")
        private String addedBy;
        @Expose
        private String duration;
        @Expose
        private String singer;
        @Expose
        private String sound;
        @SerializedName("sound_category_id")
        private String soundCategoryId;
        @SerializedName("sound_id")
        private String soundId;
        @SerializedName("sound_image")
        private String soundImage;
        @SerializedName("sound_title")
        private String soundTitle;
        @Expose
        private String status;

        private boolean isSelect;

        private boolean isPlaying;

        public String getAddedBy() {
            return addedBy;
        }

        public void setAddedBy(String addedBy) {
            this.addedBy = addedBy;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getSoundCategoryId() {
            return soundCategoryId;
        }

        public void setSoundCategoryId(String soundCategoryId) {
            this.soundCategoryId = soundCategoryId;
        }

        public String getSoundId() {
            return soundId;
        }

        public void setSoundId(String soundId) {
            this.soundId = soundId;
        }

        public String getSoundImage() {
            return soundImage;
        }

        public void setSoundImage(String soundImage) {
            this.soundImage = soundImage;
        }

        public String getSoundTitle() {
            return soundTitle;
        }

        public void setSoundTitle(String soundTitle) {
            this.soundTitle = soundTitle;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }
    }
}
