
package com.retrytech.vilo.model.wallet;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinRate {

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

        @SerializedName("coin_rate_id")
        private String coinRateId;
        @Expose
        private String status;
        @SerializedName("usd_rate")
        private String usdRate;

        public String getCoinRateId() {
            return coinRateId;
        }

        public void setCoinRateId(String coinRateId) {
            this.coinRateId = coinRateId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUsdRate() {
            return usdRate;
        }

        public void setUsdRate(String usdRate) {
            this.usdRate = usdRate;
        }

    }
}
