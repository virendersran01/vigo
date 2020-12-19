
package com.retrytech.vilo.model.user;

import com.google.gson.annotations.Expose;

public class RestResponse {

    @Expose
    private String message;
    @Expose
    private Boolean status;

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
