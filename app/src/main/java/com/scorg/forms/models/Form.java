package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.ArrayList;

public class Form implements Parcelable {

    @SerializedName("formName")
    @Expose
    private String formName;
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
        this.formName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.pages, (Page.class.getClassLoader()));
    }

    public Form() {
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(formName);
        dest.writeList(pages);
    }

    public int describeContents() {
        return 0;
    }

}