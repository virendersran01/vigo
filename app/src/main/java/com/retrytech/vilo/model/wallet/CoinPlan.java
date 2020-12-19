
package com.retrytech.vilo.model.wallet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CoinPlan {

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

        @SerializedName("appstore_product_id")
        private String appstoreProductId;
        @SerializedName("coin_amount")
        private String coinAmount;
        @SerializedName("coin_plan_description")
        private String coinPlanDescription;
        @SerializedName("coin_plan_id")
        private String coinPlanId;
        @SerializedName("coin_plan_name")
        private String coinPlanName;
        @SerializedName("coin_plan_price")
        private String coinPlanPrice;
        @SerializedName("created_date")
        private String createdDate;
        @SerializedName("playstore_product_id")
        private String playstoreProductId;
        @Expose
        private String status;

        public String getAppstoreProductId() {
            return appstoreProductId;
        }

        public void setAppstoreProductId(String appstoreProductId) {
            this.appstoreProductId = appstoreProductId;
        }

        public String getCoinAmount() {
            return coinAmount;
        }

        public void setCoinAmount(String coinAmount) {
            this.coinAmount = coinAmount;
        }

        public String getCoinPlanDescription() {
            return coinPlanDescription;
        }

        public void setCoinPlanDescription(String coinPlanDescription) {
            this.coinPlanDescription = coinPlanDescription;
        }

        public String getCoinPlanId() {
            return coinPlanId;
        }

        public void setCoinPlanId(String coinPlanId) {
            this.coinPlanId = coinPlanId;
        }

        public String getCoinPlanName() {
            return coinPlanName;
        }

        public void setCoinPlanName(String coinPlanName) {
            this.coinPlanName = coinPlanName;
        }

        public String getCoinPlanPrice() {
            return coinPlanPrice;
        }

        public void setCoinPlanPrice(String coinPlanPrice) {
            this.coinPlanPrice = coinPlanPrice;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getPlaystoreProductId() {
            return playstoreProductId;
        }

        public void setPlaystoreProductId(String playstoreProductId) {
            this.playstoreProductId = playstoreProductId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }
}
