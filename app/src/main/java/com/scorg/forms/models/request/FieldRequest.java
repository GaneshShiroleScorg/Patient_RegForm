package com.scorg.forms.models.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ganeshshirole on 16/11/17.
 */

public class FieldRequest implements Parcelable {

    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("values")
    @Expose
    private List<String> values = new ArrayList<String>();

    @SerializedName("rating")
    @Expose
    private Float rating = 0f;

    @SerializedName("textValue")
    @Expose
    private String textValue;
    public final static Parcelable.Creator<FieldRequest> CREATOR = new Creator<FieldRequest>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FieldRequest createFromParcel(Parcel in) {
            return new FieldRequest(in);
        }

        public FieldRequest[] newArray(int size) {
            return (new FieldRequest[size]);
        }

    };

    protected FieldRequest(Parcel in) {
        this.key = ((String) in.readValue((String.class.getClassLoader())));
        this.value = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.values, (java.lang.String.class.getClassLoader()));
        this.rating = ((Float) in.readValue((Float.class.getClassLoader())));
        this.textValue = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FieldRequest() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(key);
        dest.writeValue(value);
        dest.writeList(values);
        dest.writeValue(rating);
        dest.writeValue(textValue);
    }

    public int describeContents() {
        return 0;
    }

}