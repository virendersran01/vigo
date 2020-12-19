
package com.retrytech.vilo.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class SearchUser {

    @Expose
    private List<User> data;
    @Expose
    private String message;
    @Expose
    private Boolean status;

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
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

    public static class User {

        @SerializedName("full_name")
        private String fullName;
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
        @SerializedName("is_verify")
        private String isVerify;
        @SerializedName("my_post_count")
        private int userPostCount;
        @SerializedName("followers_count")
        private int userFollowerCount;


        public boolean getIsVerify() {
            return isVerify.equals("1");
        }

        public void setIsVerify(String isVerify) {
            this.isVerify = isVerify;
        }

        public int getUserPostCount() {
            return userPostCount;
        }

        public void setUserPostCount(int userPostCount) {
            this.userPostCount = userPostCount;
        }

        public int getUserFollowerCount() {
            return userFollowerCount;
        }

        public void setUserFollowerCount(int userFollowerCount) {
            this.userFollowerCount = userFollowerCount;
        }


        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
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

    }
}
