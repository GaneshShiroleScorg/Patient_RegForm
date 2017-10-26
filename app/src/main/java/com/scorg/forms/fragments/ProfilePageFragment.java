package com.scorg.forms.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomButton;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Form;
import com.scorg.forms.models.Page;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;

import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfilePageFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private FormFragment mParentFormFragment;

    interface TYPE {
        String TEXTBOX = "textbox";
        String DROPDOWN = "dropdown";
        String RADIOBUTTON = "radiobutton";
        String CHECKBOX = "checkbox";
        String TEXTBOXGROUP = "textboxgroup";
    }

    interface INPUT_TYPE {
        String NAME = "name";
        String MOBILE = "mobile";
        String DATE = "date";
        String EMAIL = "email";
        String PIN_CODE = "pincode";
        String NUMBER = "number";
        String TEXTBOX_BIG = "textboxbig";
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String PAGE_NUMBER = "section_number";
    private static final String PAGE = "page";
    private int pageNumber;
    private int formNumber;
    private Page page;

    public ProfilePageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfilePageFragment newInstance(int formNumber, int pageNumber, Page page) {
        ProfilePageFragment fragment = new ProfilePageFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putParcelable(PAGE, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getParcelable(PAGE);
            pageNumber = getArguments().getInt(PAGE_NUMBER);
            formNumber = getArguments().getInt(FORM_NUMBER);
        }
    }

    @SuppressWarnings("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_dashboard, container, false);

        //--------
        configureViewsOfParentFragment();
        //------------

        TextView titleTextView = rootView.findViewById(R.id.titleView);
        LinearLayout sectionsContainer = rootView.findViewById(R.id.sectionsContainer);

        titleTextView.setText(page.getPageName());

        View sectionLayout = inflater.inflate(R.layout.profile_section_layout, sectionsContainer, false);

        for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {

            ImageView profilePhoto = sectionLayout.findViewById(R.id.profilePhoto);

            if (page.getSection().get(sectionIndex).getProfilePhoto() != null) {
                profilePhoto.setVisibility(View.VISIBLE);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.skipMemoryCache(true);
                requestOptions.error(R.drawable.ic_camera);
                requestOptions.placeholder(R.drawable.ic_camera);

                Glide.with(getContext())
                        .load(page.getSection().get(sectionIndex).getProfilePhoto())
                        .into(profilePhoto);
            }

            View fieldsContainer = sectionLayout.findViewById(R.id.fieldsContainer);

            ArrayList<Field> fields = page.getSection().get(sectionIndex).getFields();

            for (int fieldsIndex = 0; fieldsIndex < fields.size(); fieldsIndex++) {
                final Field field = fields.get(fieldsIndex);
                if (field.isIncludeInShortDescription())
                    addField(fieldsContainer, sectionIndex, fields, field, fieldsIndex, inflater, -1);
            }

            //----- This is done to call edit/submit functionality of FormFragment.java---
            CustomButton profileSectionEditButton = sectionLayout.findViewById(R.id.profileSectionEditButton);
            profileSectionEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mParentFormFragment.doOperationEditOrSubmit();
                }
            });

        }

        sectionsContainer.addView(sectionLayout);

        return rootView;
    }

    private void addField(final View fieldsContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case TYPE.TEXTBOXGROUP: {

                if (!field.getValue().equals("")) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue());
                    // Add Parent First
                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                    ArrayList<Field> moreFields = field.getTextBoxGroup();

                    for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                        if (indexToAddView != -1)
                            addField(fieldsContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, indexToAddView + moreFieldIndex + 1);
                        else
                            addField(fieldsContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, -1);
                    }
                }

                break;
            }

            case TYPE.TEXTBOX: {

                if (!field.getValue().equals("")) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                }

                break;
            }

            case TYPE.RADIOBUTTON: {

                if (!field.getValue().equals("")) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                }

                break;
            }
            case TYPE.CHECKBOX: {

                ArrayList<String> dataList = field.getDataList();
                ArrayList<String> values = field.getValues();

                String valueText = "";

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    // set pre value
                    for (String value : values)
                        if (value.equals(data)) {
                            if (!valueText.equals(""))
                                valueText += ", " + value;
                            else valueText += value;
                        }
                }

                if (!valueText.equals("")) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    textBox.setText(valueText);

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                }

                break;
            }
            case TYPE.DROPDOWN: {

                if (!field.getValue().equals("")) {

                    View fieldLayout = inflater.inflate(R.layout.label_layout, (LinearLayout) fieldsContainer, false);
                    TextView labelView = fieldLayout.findViewById(R.id.labelView);
                    labelView.setText(field.getName());
                    TextView textBox = fieldLayout.findViewById(R.id.labelValueText);
                    textBox.setId(CommonMethods.generateViewId());
                    // set pre value
                    textBox.setText(field.getValue());

                    if (indexToAddView != -1)
                        ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                    else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                }

                break;
            }
        }
    }

    public Page getPage() {
        return page;
    }

    /**
     * This is use to visible/invisible views in parent FormFragmnet.java.
     * For now, tablayout,footerButton and footer tabs is gone.
     * Dont call this in case not required.
     */
    private void configureViewsOfParentFragment() {
        //-----
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FormFragment) {
            mParentFormFragment = (FormFragment) parentFragment;
            mParentFormFragment.manageProfileFragmentViews();
        }
    }
}