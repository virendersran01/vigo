
package com.retrytech.vilo.model.follower;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Follower {

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


    public static class Data {

        @SerializedName("created_date")
        private String createdDate;
        @SerializedName("follower_id")
        private String followerId;
        @SerializedName("followers_count")
        private Long followersCount;
        @SerializedName("following_count")
        private Long followingCount;
        @SerializedName("from_user_id")
        private String fromUserId;
        @SerializedName("full_name")
        private String fullName;
        @SerializedName("is_verify")
        private String isVerify;
        @SerializedName("my_post_count")
        private Long myPostCount;
        @SerializedName("my_post_likes")
        private Long myPostLikes;
        @SerializedName("to_user_id")
        private String toUserId;
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

        public String getFollowerId() {
            return followerId;
        }

        public void setFollowerId(String followerId) {
            this.followerId = followerId;
        }

        public Long getFollowersCount() {
            return followersCount;
        }

        public void setFollowersCount(Long followersCount) {
            this.followersCount = followersCount;
        }

        public Long getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(Long followingCount) {
            this.followingCount = followingCount;
        }

        public String getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public boolean getIsVerify() {
            return isVerify.equals("1");
        }

        public void setIsVerify(String isVerify) {
            this.isVerify = isVerify;
        }

        public Long getMyPostCount() {
            return myPostCount;
        }

        public void setMyPostCount(Long myPostCount) {
            this.myPostCount = myPostCount;
        }

        public Long getMyPostLikes() {
            return myPostLikes;
        }

        public void setMyPostLikes(Long myPostLikes) {
            this.myPostLikes = myPostLikes;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
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
}
