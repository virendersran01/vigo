
package com.retrytech.vilo.model.user;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class User {

    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

        @Expose
        private String bio;
        @SerializedName("created_date")
        private String createdDate;
        @SerializedName("device_token")
        private String deviceToken;
        @SerializedName("fb_url")
        private String fbUrl;
        @SerializedName("full_name")
        private String fullName;
        @Expose
        private String identity;
        @SerializedName("insta_url")
        private String instaUrl;
        @SerializedName("is_verify")
        private String isVerify;
        @SerializedName("login_type")
        private String loginType;
        @SerializedName("my_wallet")
        private String myWallet;
        @Expose
        private String platform;
        @Expose
        private String status;
        @Expose
        private String token;
        @SerializedName("user_email")
        private String userEmail;
        @SerializedName("user_id")
        private String userId;
        @SerializedName("user_mobile_no")
        private String userMobileNo;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_profile")
        private String userProfile;
        @SerializedName("youtube_url")
        private String youtubeUrl;
        @SerializedName("my_post_likes")
        private int myPostLikes;
        @SerializedName("followers_count")
        private int followersCount;
        @SerializedName("following_count")
        private int followingCount;
        @SerializedName("is_following  ")
        private int isFollowing;

        public long getIsFollowing() {
            return isFollowing;
        }

        public void setIsFollowing(int isFollowing) {
            this.isFollowing = isFollowing;
        }

        public int getMyPostLikes() {
            return myPostLikes;
        }

        public void setMyPostLikes(int myPostLikes) {
            this.myPostLikes = myPostLikes;
        }

        public int getFollowersCount() {
            return followersCount;
        }

        public void setFollowersCount(int followersCount) {
            this.followersCount = followersCount;
        }

        public int getFollowingCount() {
            return followingCount;
        }

        public void setFollowingCount(int followingCount) {
            this.followingCount = followingCount;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getFbUrl() {
            return fbUrl;
        }

        public void setFbUrl(String fbUrl) {
            this.fbUrl = fbUrl;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public String getInstaUrl() {
            return instaUrl;
        }

        public void setInstaUrl(String instaUrl) {
            this.instaUrl = instaUrl;
        }

        public String getIsVerify() {
            return isVerify;
        }

        public boolean isVerified() {
            return isVerify.equals("1");
        }

        public void setIsVerify(String isVerify) {
            this.isVerify = isVerify;
        }

        public String getLoginType() {
            return loginType;
        }

        public void setLoginType(String loginType) {
            this.loginType = loginType;
        }

        public String getMyWallet() {
            return myWallet;
        }

        public void setMyWallet(String myWallet) {
            this.myWallet = myWallet;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserMobileNo() {
            return userMobileNo;
        }

        public void setUserMobileNo(String userMobileNo) {
            this.userMobileNo = userMobileNo;
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

        public String getYoutubeUrl() {
            return youtubeUrl;
        }

        public void setYoutubeUrl(String youtubeUrl) {
            this.youtubeUrl = youtubeUrl;
        }

    }
}
