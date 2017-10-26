package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.ArrayList;

public class Form implements Parcelable {

    @SerializedName("formIcon")
    @Expose
    private String formIcon;
    @SerializedName("formName")
    @Expose
    private String formName;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("pages")
    @Expose
    private ArrayList<Page> pages = new ArrayList<Page>();
    public final static Parcelable.Creator<Form> CREATOR = new Creator<Form>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        public Form[] newArray(int size) {
            return (new Form[size]);
        }

    };

    protected Form(Parcel in) {
        this.formIcon = ((String) in.readValue((String.class.getClassLoader())));
        this.formName = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.pages, (Page.class.getClassLoader()));
    }

    public Form() {
    }

    public String getFormIcon() {
        return formIcon;
    }

    public void setFormIcon(String formIcon) {
        this.formIcon = formIcon;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(formIcon);
        dest.writeValue(formName);
        dest.writeValue(date);
        dest.writeList(pages);
    }

    public int describeContents() {
        return 0;
    }

}