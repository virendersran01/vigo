
package com.retrytech.vilo.model.wallet;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MyWallet {

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

        @SerializedName("check_in")
        private String checkIn;
        @SerializedName("from_fans")
        private String fromFans;
        @SerializedName("my_wallet")
        private String myWallet;
        @Expose
        private String purchased;
        @SerializedName("spen_in_app")
        private String spenInApp;
        @SerializedName("upload_video")
        private String uploadVideo;
        @SerializedName("total_received")
        private String totalReceived;
        @SerializedName("total_send")
        private String totalSend;

        public String getTotalReceived() {
            return totalReceived;
        }

        public void setTotalReceived(String totalReceived) {
            this.totalReceived = totalReceived;
        }

        public String getTotalSend() {
            return totalSend;
        }

        public void setTotalSend(String totalSend) {
            this.totalSend = totalSend;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(String checkIn) {
            this.checkIn = checkIn;
        }

        public String getFromFans() {
            return fromFans;
        }

        public void setFromFans(String fromFans) {
            this.fromFans = fromFans;
        }

        public String getMyWallet() {
            return myWallet;
        }

        public void setMyWallet(String myWallet) {
            this.myWallet = myWallet;
        }

        public String getPurchased() {
            return purchased;
        }

        public void setPurchased(String purchased) {
            this.purchased = purchased;
        }

        public String getSpenInApp() {
            return spenInApp;
        }

        public void setSpenInApp(String spenInApp) {
            this.spenInApp = spenInApp;
        }

        public String getUploadVideo() {
            return uploadVideo;
        }

        public void setUploadVideo(String uploadVideo) {
            this.uploadVideo = uploadVideo;
        }

    }
}
