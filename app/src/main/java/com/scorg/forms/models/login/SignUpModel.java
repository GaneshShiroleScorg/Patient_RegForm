package com.scorg.forms.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.models.Common;

public class SignUpModel implements CustomResponse {

    @SerializedName("common")
    @Expose
    private Common common;
    @SerializedName("data")
    @Expose
    private String dataResponse;

    public Common getCommon() {
        return common;
    }

    public void setCommon(Common common) {
        this.common = common;
    }

    public String getDataResponse() {
        return dataResponse;
    }

    public void setDataResponse(String dataResponse) {
        this.dataResponse = dataResponse;
    }
}