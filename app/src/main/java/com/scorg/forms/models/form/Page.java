package com.scorg.forms.models.form;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Page implements Parcelable {

    @SerializedName("pageIcon")
    @Expose
    private String pageIcon;
    @SerializedName("pageName")
    @Expose
    private String pageName;
    @SerializedName("section")
    @Expose
    private ArrayList<Section> section = new ArrayList<Section>();
    @SerializedName("fields")
    @Expose
    private ArrayList<Field> fields = new ArrayList<Field>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("undertakingContent")
    @Expose
    private String undertakingContent;
    @SerializedName("undertakingImageUrl")
    @Expose
    private String undertakingImageUrl;

    public final static Parcelable.Creator<Page> CREATOR = new Creator<Page>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Page createFromParcel(Parcel in) {
            return new Page(in);
        }

        public Page[] newArray(int size) {
            return (new Page[size]);
        }

    };

    protected Page(Parcel in) {
        this.pageIcon = ((String) in.readValue((String.class.getClassLoader())));
        this.pageName = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.section, (Section.class.getClassLoader()));
        in.readList(this.fields, (Section.class.getClassLoader()));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.undertakingContent = ((String) in.readValue((String.class.getClassLoader())));
        this.undertakingImageUrl = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Page() {
    }

    public String getPageIcon() {
        return pageIcon;
    }

    public void setPageIcon(String pageIcon) {
        this.pageIcon = pageIcon;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public ArrayList<Section> getSection() {
        return section;
    }

    public void setSection(ArrayList<Section> section) {
        this.section = section;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void setFields(ArrayList<Field> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUndertakingContent() {
        return undertakingContent;
    }

    public void setUndertakingContent(String undertakingContent) {
        this.undertakingContent = undertakingContent;
    }

    public String getUndertakingImageUrl() {
        return undertakingImageUrl;
    }

    public void setUndertakingImageUrl(String undertakingImageUrl) {
        this.undertakingImageUrl = undertakingImageUrl;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(pageIcon);
        dest.writeValue(pageName);
        dest.writeList(section);
        dest.writeList(fields);
        dest.writeValue(name);
        dest.writeValue(undertakingContent);
        dest.writeValue(undertakingImageUrl);

    }

    public int describeContents() {
        return 0;
    }

}