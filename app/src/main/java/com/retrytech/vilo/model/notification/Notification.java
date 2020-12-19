
package com.retrytech.vilo.model.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Notification {

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
        @SerializedName("full_name")
        private String fullName;
        @Expose
        private String message;
        @SerializedName("notification_type")
        private String notificationType;
        @SerializedName("received_user_id")
        private String receivedUserId;
        @SerializedName("sender_user_id")
        private String senderUserId;
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

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(String notificationType) {
            this.notificationType = notificationType;
        }

        public String getReceivedUserId() {
            return receivedUserId;
        }

        public void setReceivedUserId(String receivedUserId) {
            this.receivedUserId = receivedUserId;
        }

        public String getSenderUserId() {
            return senderUserId;
        }

        public void setSenderUserId(String senderUserId) {
            this.senderUserId = senderUserId;
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
