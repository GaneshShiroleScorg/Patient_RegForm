package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.ArrayList;

public class FormsModel implements Parcelable {

    @SerializedName("personalInfo")
    @Expose
    private PersonalInfo personalInfo;
    @SerializedName("undertaking")
    @Expose
    private Undertaking undertaking;
    @SerializedName("forms")
    @Expose
    private ArrayList<Form> forms = new ArrayList<Form>();
    public final static Parcelable.Creator<FormsModel> CREATOR = new Creator<FormsModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public FormsModel createFromParcel(Parcel in) {
            return new FormsModel(in);
        }

        public FormsModel[] newArray(int size) {
            return (new FormsModel[size]);
        }

    };

    protected FormsModel(Parcel in) {
        this.personalInfo = ((PersonalInfo) in.readValue((PersonalInfo.class.getClassLoader())));
        this.undertaking = ((Undertaking) in.readValue((Undertaking.class.getClassLoader())));
        in.readList(this.forms, (Form.class.getClassLoader()));
    }

    public FormsModel() {
    }

    public PersonalInfo getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public Undertaking getUndertaking() {
        return undertaking;
    }

    public void setUndertaking(Undertaking undertaking) {
        this.undertaking = undertaking;
    }

    public ArrayList<Form> getForms() {
        return forms;
    }

    public void setForms(ArrayList<Form> forms) {
        this.forms = forms;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(personalInfo);
        dest.writeValue(undertaking);
        dest.writeList(forms);
    }

    public int describeContents() {
        return 0;
    }

}