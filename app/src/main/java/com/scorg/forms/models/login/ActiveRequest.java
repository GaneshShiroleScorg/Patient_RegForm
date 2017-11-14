package com.scorg.forms.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.scorg.forms.interfaces.CustomResponse;

public class ActiveRequest implements CustomResponse {

    @SerializedName("docId")
    @Expose
    private int userId;

    public int getId() {
        return userId;
    }

    public void setId(int patientId) {
        this.userId = patientId;
    }

}