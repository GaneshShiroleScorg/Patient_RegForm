package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.ArrayList;

public class PersonalInfo implements Parcelable {

    @SerializedName("pages")
    @Expose
    private ArrayList<Page> pages = new ArrayList<Page>();
    public final static Parcelable.Creator<PersonalInfo> CREATOR = new Creator<PersonalInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PersonalInfo createFromParcel(Parcel in) {
            return new PersonalInfo(in);
        }

        public PersonalInfo[] newArray(int size) {
            return (new PersonalInfo[size]);
        }

    };

    protected PersonalInfo(Parcel in) {
        in.readList(this.pages, (Page.class.getClassLoader()));
    }

    public PersonalInfo() {
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(pages);
    }

    public int describeContents() {
        return 0;
    }

}