package com.scorg.forms.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FormRequest implements Parcelable {

    @SerializedName("fields")
    @Expose
    private List<FieldRequest> fields = new ArrayList<FieldRequest>();
    public final static Parcelable.Creator<FormRequest> CREATOR = new Creator<FormRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FormRequest createFromParcel(Parcel in) {
            return new FormRequest(in);
        }

        public FormRequest[] newArray(int size) {
            return (new FormRequest[size]);
        }

    };

    protected FormRequest(Parcel in) {
        in.readList(this.fields, (FieldRequest.class.getClassLoader()));
    }

    public FormRequest() {
    }

    public List<FieldRequest> getFields() {
        return fields;
    }

    public void setFields(List<FieldRequest> fields) {
        this.fields = fields;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(fields);
    }

    public int describeContents() {
        return 0;
    }

}