
package com.retrytech.vilo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.retrytech.vilo.model.videos.Video;

import java.util.List;


@SuppressWarnings("unused")
public class Explore {

    @Expose
    private List<Data> data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
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

    @SuppressWarnings("unused")
    public static class HashTagVideo {

        @SerializedName("created_date")
        private String createdDate;
        @Expose
        private String duration;
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("post_comments_count")
        private Long postCommentsCount;
        @SerializedName("post_description")
        private String postDescription;
        @SerializedName("post_hash_tag")
        private String postHashTag;
        @SerializedName("post_id")
        private String postId;
        @SerializedName("post_image")
        private String postImage;
        @SerializedName("post_likes_count")
        private String postLikesCount;
        @SerializedName("post_video")
        private String postVideo;
        @SerializedName("post_view_count")
        private String postViewCount;
        @Expose
        private String singer;
        @Expose
        private String sound;
        @SerializedName("sound_id")
        private String soundId;
        @SerializedName("sound_image")
        private String soundImage;
        @SerializedName("sound_title")
        private String soundTitle;
        @Expose
        private String status;
        @SerializedName("user_id")
        private String userId;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_profile")
        private String userProfile;

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public Long getPostCommentsCount() {
            return postCommentsCount;
        }

        public void setPostCommentsCount(Long postCommentsCount) {
            this.postCommentsCount = postCommentsCount;
        }

        public String getPostDescription() {
            return postDescription;
        }

        public void setPostDescription(String postDescription) {
            this.postDescription = postDescription;
        }

        public String getPostHashTag() {
            return postHashTag;
        }

        public void setPostHashTag(String postHashTag) {
            this.postHashTag = postHashTag;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getPostImage() {
            return postImage;
        }

        public void setPostImage(String postImage) {
            this.postImage = postImage;
        }

        public String getPostLikesCount() {
            return postLikesCount;
        }

        public void setPostLikesCount(String postLikesCount) {
            this.postLikesCount = postLikesCount;
        }

        public String getPostVideo() {
            return postVideo;
        }

        public void setPostVideo(String postVideo) {
            this.postVideo = postVideo;
        }

        public String getPostViewCount() {
            return postViewCount;
        }

        public void setPostViewCount(String postViewCount) {
            this.postViewCount = postViewCount;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(String userProfile) {
            this.userProfile = userProfile;
        }

    }

    @SuppressWarnings("unused")
    public static class Data {

        @SerializedName("hash_tag_name")
        private String hashTagName;
        @SerializedName("hash_tag_videos")
        private List<Video.Data> hashTagVideos;
        @SerializedName("hash_tag_videos_count")
        private int hashTagVideosCountl;


        public int getHashTagVideosCountl() {
            return hashTagVideosCountl;
        }

        public void setHashTagVideosCountl(int hashTagVideosCountl) {
            this.hashTagVideosCountl = hashTagVideosCountl;
        }

        public String getHashTagName() {
            return hashTagName;
        }

        public void setHashTagName(String hashTagName) {
            this.hashTagName = hashTagName;
        }

        public List<Video.Data> getHashTagVideos() {
            return hashTagVideos;
        }

        public void setHashTagVideos(List<Video.Data> hashTagVideos) {
            this.hashTagVideos = hashTagVideos;
        }

    }
}
