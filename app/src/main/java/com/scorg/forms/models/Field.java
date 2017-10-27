package com.scorg.forms.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Field implements Parcelable, Cloneable {

    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("type")
    @Expose
    private String type = "";
    @SerializedName("isMandatory")
    @Expose
    private boolean isMandatory;
    @SerializedName("length")
    @Expose
    private int length;
    @SerializedName("value")
    @Expose
    private String value = "";
    @SerializedName("values")
    @Expose
    private ArrayList<String> values = new ArrayList<String>();
    @SerializedName("inputType")
    @Expose
    private String inputType = "";
    @SerializedName("dataList")
    @Expose
    private ArrayList<String> dataList = new ArrayList<String>();
    @SerializedName("moreField")
    @Expose
    private ArrayList<Field> textBoxGroup = new ArrayList<Field>();
    @SerializedName("dataTable")
    @Expose
    private String dataTable = "";
    @SerializedName("isIncludeInShortDescription")
    @Expose
    private boolean isIncludeInShortDescription = true;

    private int fieldId;
    private int errorViewId;

    public final static Parcelable.Creator<Field> CREATOR = new Creator<Field>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        public Field[] newArray(int size) {
            return (new Field[size]);
        }

    };

    private Field(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.isMandatory = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.length = ((int) in.readValue((int.class.getClassLoader())));
        this.value = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.values, (java.lang.String.class.getClassLoader()));
        this.inputType = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.dataList, (java.lang.String.class.getClassLoader()));
        this.dataTable = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.textBoxGroup, (Field.class.getClassLoader()));
        this.isIncludeInShortDescription = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.fieldId = ((int) in.readValue((int.class.getClassLoader())));
        this.errorViewId = ((int) in.readValue((int.class.getClassLoader())));
    }

    public Field() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setisMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public ArrayList<String> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public String getDataTable() {
        return dataTable;
    }

    public void setDataTable(String dataTable) {
        this.dataTable = dataTable;
    }

    public ArrayList<Field> getTextBoxGroup() {
        return textBoxGroup;
    }

    public void setTextBoxGroup(ArrayList<Field> textBoxGroup) {
        this.textBoxGroup = textBoxGroup;
    }

    public boolean isIncludeInShortDescription() {
        return isIncludeInShortDescription;
    }

    public void setIncludeInShortDescription(boolean includeInShortDescription) {
        isIncludeInShortDescription = includeInShortDescription;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getErrorViewId() {
        return errorViewId;
    }

    public void setErrorViewId(int errorViewId) {
        this.errorViewId = errorViewId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeValue(type);
        dest.writeValue(isMandatory);
        dest.writeValue(length);
        dest.writeValue(value);
        dest.writeList(values);
        dest.writeValue(inputType);
        dest.writeList(dataList);
        dest.writeValue(dataTable);
        dest.writeList(textBoxGroup);
        dest.writeValue(isIncludeInShortDescription);
        dest.writeValue(fieldId);
        dest.writeValue(errorViewId);
    }

    public int describeContents() {
        return 0;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}