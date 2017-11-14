package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("fields")
    @Expose
    private ArrayList<Field> fields = new ArrayList<Field>();
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
        in.readList(this.fields, (Field.class.getClassLoader()));
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(formIcon);
        dest.writeValue(formName);
        dest.writeValue(date);
        dest.writeList(pages);
        dest.writeList(fields);
    }

    public int describeContents() {
        return 0;
    }

}