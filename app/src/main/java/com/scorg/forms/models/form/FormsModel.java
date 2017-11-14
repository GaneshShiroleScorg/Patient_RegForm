package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FormsModel implements Parcelable {

    @SerializedName("clinicLogoUrl")
    @Expose
    private String clinicLogoUrl;
    @SerializedName("clinicName")
    @Expose
    private String clinicName;
    @SerializedName("personalInfo")
    @Expose
    private PersonalInfo personalInfo;

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
        this.clinicLogoUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.clinicName = ((String) in.readValue((String.class.getClassLoader())));

        this.personalInfo = ((PersonalInfo) in.readValue((PersonalInfo.class.getClassLoader())));
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

    public ArrayList<Form> getForms() {
        return forms;
    }

    public void setForms(ArrayList<Form> forms) {
        this.forms = forms;
    }

    public String getClinicLogoUrl() {
        return clinicLogoUrl;
    }

    public void setClinicLogoUrl(String clinicLogoUrl) {
        this.clinicLogoUrl = clinicLogoUrl;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(clinicLogoUrl);
        dest.writeValue(clinicName);

        dest.writeValue(personalInfo);
        dest.writeList(forms);
    }

    public int describeContents() {
        return 0;
    }

}