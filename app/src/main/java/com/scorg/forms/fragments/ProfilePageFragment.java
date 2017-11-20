package com.scorg.forms.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Form;
import com.scorg.forms.models.form.FormsData;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;

import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.fragments.FormFragment.IS_NEW;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfilePageFragment extends Fragment {

//    private FormFragment mParentFormFragment;
    private ButtonClickListener mListener;

    public static final int PERSONAL_INFO_FORM = 20;

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
//    private static final String PAGE_NUMBER = "section_number";
    private static final String PERSONAL_INFO = "personal_info";
    //    private int pageNumber;
    private int formNumber;
    private boolean isNew;
    private FormsData formsData;

    public ProfilePageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfilePageFragment newInstance(int formNumber, FormsData formsData, boolean isNew) {
        ProfilePageFragment fragment = new ProfilePageFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putParcelable(PERSONAL_INFO, formsData);
        args.putBoolean(IS_NEW, isNew);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            formsData = getArguments().getParcelable(PERSONAL_INFO);
//            pageNumber = getArguments().getInt(PAGE_NUMBER);
            formNumber = getArguments().getInt(FORM_NUMBER);
            isNew = getArguments().getBoolean(IS_NEW);
        }
    }

    @SuppressWarnings("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_dashboard, container, false);

        //--------
//        configureViewsOfParentFragment();
        //------------

        TextView editButton = rootView.findViewById(R.id.editButton);
        Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_edit);
        editButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editClick(20);
            }
        });

        /// Form Tab

        TabLayout formTabLayout = rootView.findViewById(R.id.formTabLayout);

        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(iconSize, iconSize);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.error(R.drawable.ic_assignment);
        requestOptions.placeholder(R.drawable.ic_assignment);

        for (int formIndex = 0; formIndex < formsData.getForms().size(); formIndex++) {

            Form form = formsData.getForms().get(formIndex);

            View tabView = getLayoutInflater().inflate(R.layout.custom_tab_personal_form, null);

            ImageView formIcon = tabView.findViewById(R.id.formIcon);
            TextView titleTextView = tabView.findViewById(R.id.titleTextView);

            Glide.with(getContext())
                    .load(form.getFormIcon())
                    .apply(requestOptions)
                    .into(formIcon);

            titleTextView.setText(form.getFormName());

            TabLayout.Tab customTab = formTabLayout.newTab().setCustomView(tabView);
            formTabLayout.addTab(customTab);
        }

        formTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mListener.openForm(tab, formsData.getForms().get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mListener.openForm(tab, formsData.getForms().get(tab.getPosition()));
            }
        });

        TextView titleTextView = rootView.findViewById(R.id.titleView);
        titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        titleTextView.setText(getString(R.string.personal_information));

        LinearLayout sectionsContainer = rootView.findViewById(R.id.sectionsContainer);
//        titleTextView.setText(page.getPageName());

        View sectionLayout = inflater.inflate(R.layout.profile_section_layout, sectionsContainer, false);
        
        for (int pageIndex = 0; pageIndex < formsData.getPersonalInfo().getPages().size(); pageIndex++) {

            Page page = formsData.getPersonalInfo().getPages().get(pageIndex);

            for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {

                ImageView profilePhoto = sectionLayout.findViewById(R.id.profilePhoto);

                if (page.getSection().get(sectionIndex).getProfilePhoto() != null) {
                    profilePhoto.setVisibility(View.VISIBLE);

                    RequestOptions requestOpt = new RequestOptions();
                    requestOpt.dontAnimate();
                    requestOpt.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOpt.skipMemoryCache(true);
                    requestOpt.error(R.drawable.ic_camera);
                    requestOpt.placeholder(R.drawable.ic_camera);

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

                TextView mobileText = sectionLayout.findViewById(R.id.mobileText);
                mobileText.setText(AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, getContext()));
                Drawable leftDrawablePhone = AppCompatResources.getDrawable(getContext(), R.drawable.ic_phone_iphone_24dp);
                mobileText.setCompoundDrawablesWithIntrinsicBounds(leftDrawablePhone, null, null, null);

            }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfilePageFragment.ButtonClickListener) {
            mListener = (ProfilePageFragment.ButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

   /* */

    /**
     * This is use to visible/invisible views in parent FormFragmnet.java.
     * For now, tablayout,footerButton and footer tabs is gone.
     * Dont call this in case not required.
     *//*
    private void configureViewsOfParentFragment() {
        //-----
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof FormFragment) {
            mParentFormFragment = (FormFragment) parentFragment;
            mParentFormFragment.manageProfileFragmentViews();
        }
    }*/

    // Listener
    public interface ButtonClickListener {
        void editClick(int formNumber);
        void openForm(TabLayout.Tab tab, Form form);
    }
}