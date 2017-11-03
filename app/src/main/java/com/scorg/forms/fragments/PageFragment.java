package com.scorg.forms.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.customui.FlowLayout;
import com.scorg.forms.customui.FlowRadioGroup;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Page;
import com.scorg.forms.preference.PreferencesManager;
import com.scorg.forms.util.CommonMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.fragments.FormFragment.IS_EDITABLE;
import static com.scorg.forms.fragments.ProfilePageFragment.PERSONAL_INFO_FORM;
import static com.scorg.forms.fragments.UndertakingFragment.MAX_ATTACHMENT_COUNT;

/**
 * A placeholder fragment containing a simple view.
 */
@RuntimePermissions
@SuppressWarnings("CheckResult")
public class PageFragment extends Fragment {

    private DatePickerDialog datePickerDialog;
    private boolean isEditable = true;
    private TextView mTitleTextView;
    private LinearLayout mSectionsContainer;
    private LayoutInflater mInflater;
    private String mReceivedDate;
    private String mReceivedFormName;

    private ImageView profilePhoto;
    private TextView editButton;

    interface INPUT_TYPE {
        String NAME = "name";
        String MOBILE = "mobile";
        String DATE = "date";
        String EMAIL = "email";
        String PIN_CODE = "pincode";
        String NUMBER = "number";
        String TEXTBOX_BIG = "textboxbig";
    }

    interface TYPE {
        String TEXTBOX = "textbox";
        String DROPDOWN = "dropdown";
        String RADIOBUTTON = "radiobutton";
        String CHECKBOX = "checkbox";
        String RATINGBAR = "ratingbar";
        String TEXTBOX_GROUP = "textboxgroup";
        String RADIOBUTTON_WITH_TEXT = "radiobuttonwithtext";
        String CHECKBOX_WITH_TEXT = "checkboxwithtext";
        String DROPDOWN_WITH_TEXT = "dropdownwithtext";
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

    public PageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PageFragment newInstance(int formNumber, int pageNumber, Page page, boolean isEditable, String mReceivedFormName) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(FORM_NUMBER, formNumber);
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putBoolean(IS_EDITABLE, isEditable);
        args.putParcelable(PAGE, page);
        args.putString(FORM_NAME, mReceivedFormName);
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
            isEditable = getArguments().getBoolean(IS_EDITABLE);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            mReceivedDate = df.format(c.getTime());

            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @SuppressWarnings("CheckResult")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);

        mInflater = inflater;
        mTitleTextView = rootView.findViewById(R.id.titleView);
        mSectionsContainer = rootView.findViewById(R.id.sectionsContainer);

        initializeDataViews();
        return rootView;
    }

    private void initializeDataViews() {

        if (formNumber == PERSONAL_INFO_FORM)
            mTitleTextView.setText(page.getPageName());
        else mTitleTextView.setText(mReceivedFormName + ": " + page.getPageName());

        for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {
            View sectionLayout = mInflater.inflate(R.layout.section_layout, mSectionsContainer, false);


            if (page.getSection().get(sectionIndex).getProfilePhoto() != null) {
                View profilePhotoLayout = sectionLayout.findViewById(R.id.profilePhotoLayout);
                profilePhoto = sectionLayout.findViewById(R.id.profilePhoto);
                editButton = sectionLayout.findViewById(R.id.editButton);

                editButton.setEnabled(isEditable);

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PageFragmentPermissionsDispatcher.onPickPhotoWithCheck(PageFragment.this);
                    }
                });

                Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_photo_camera);
                editButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

                TextView mobileText = sectionLayout.findViewById(R.id.mobileText);
                mobileText.setText(PreferencesManager.getString(PreferencesManager.PREFERENCES_KEY.MOBILE, getContext()));
                Drawable leftDrawablePhone = AppCompatResources.getDrawable(getContext(), R.drawable.ic_phone_iphone_24dp);
                mobileText.setCompoundDrawablesWithIntrinsicBounds(leftDrawablePhone, null, null, null);
                profilePhotoLayout.setVisibility(View.VISIBLE);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.dontAnimate();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.skipMemoryCache(true);
//                requestOptions.error(R.drawable.ic_camera);
//                requestOptions.placeholder(R.drawable.ic_camera);
                Glide.with(getContext())
                        .load(page.getSection().get(sectionIndex).getProfilePhoto())
                        .into(profilePhoto);
            } else {
                View profilePhotoLayout = sectionLayout.findViewById(R.id.profilePhotoLayout);
                profilePhotoLayout.setVisibility(View.GONE);
            }

            View fieldsContainer = sectionLayout.findViewById(R.id.fieldsContainer);

            ArrayList<Field> fields = page.getSection().get(sectionIndex).getFields();

            TextView sectionTitle = sectionLayout.findViewById(R.id.sectionTitleView);
            sectionTitle.setText(page.getSection().get(sectionIndex).getSectionName());

            for (int fieldsIndex = 0; fieldsIndex < fields.size(); fieldsIndex++) {
                final Field field = fields.get(fieldsIndex);
                addField(fieldsContainer, sectionIndex, fields, field, fieldsIndex, mInflater, -1);
            }
            mSectionsContainer.addView(sectionLayout);
        }

        //------ in case of undertaking comes----
        // dont show header if it is undertaking content fragment.
        if (page.getUndertakingContent() != null) {

            UndertakingFragment newRegistrationFragment = UndertakingFragment.newInstance(mReceivedDate, page.getUndertakingContent(), page.getUndertakingImageUrl(), page.getName());
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.pageLayout, newRegistrationFragment, getResources().getString(R.string.undertaking));
            transaction.commit();

        }
        //----------
    }

    private void addField(final View fieldsContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case TYPE.TEXTBOX_GROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.field_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

                if (!field.getHint().equals(""))
                    textBox.setHint(field.getHint());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, field.getDataList());
                textBox.setAdapter(adapter);

                textBox.setEnabled(isEditable);

                // set pre value
                textBox.setText(field.getValue());

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

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

                textBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editTextError.setText("");
                        textBox.setBackgroundResource(R.drawable.edittext_selector);
                        // set latest value
                        field.setValue(String.valueOf(editable).trim());
                    }
                });

                ImageView plusButton = fieldLayout.findViewById(R.id.plusButton);
                plusButton.setEnabled(isEditable);

                plusButton.setVisibility(View.VISIBLE);
                plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final Field fieldGroupNew = (Field) field.clone();
                            fields.add(fields.indexOf(field) + 1, fieldGroupNew);
                            fieldGroupNew.setValue("");
                            fieldGroupNew.setisMandatory(false);

                            ArrayList<Field> clonedMoreFields = new ArrayList<Field>();
                            for (Field field1 :
                                    field.getTextBoxGroup()) {
                                Field cloneField = (Field) field1.clone();
                                cloneField.setValue("");
                                cloneField.setisMandatory(false);
                                clonedMoreFields.add(cloneField);
                            }

                            fieldGroupNew.setTextBoxGroup(clonedMoreFields);

                            addField(fieldsContainer, sectionIndex, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case TYPE.TEXTBOX: {
                View fieldLayout = inflater.inflate(R.layout.field_textbox_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                LinearLayout.LayoutParams textBoxParams = (LinearLayout.LayoutParams) textBox.getLayoutParams();
                textBox.setId(CommonMethods.generateViewId());
                textBox.setEnabled(isEditable);

                if (!field.getHint().equals(""))
                    textBox.setHint(field.getHint());

                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                // set pre value
                textBox.setText(field.getValue());

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                switch (field.getInputType()) {
                    case INPUT_TYPE.EMAIL:
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case INPUT_TYPE.DATE:
                        textBox.setCursorVisible(false);
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.date_size);

                        textBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (hasFocus) {
                                    textBox.clearFocus();
                                    Calendar now = Calendar.getInstance();
                                    // As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
                                    datePickerDialog = DatePickerDialog.newInstance(
                                            null,
                                            now.get(Calendar.YEAR),
                                            now.get(Calendar.MONTH),
                                            now.get(Calendar.DAY_OF_MONTH));
                                    datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimary));
                                    datePickerDialog.setMaxDate(Calendar.getInstance());
                                    if (!datePickerDialog.isAdded()) {
                                        datePickerDialog.show(getChildFragmentManager(), getResources().getString(R.string.select_date));
                                        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                                                if (field.getName().equalsIgnoreCase("age") || field.getName().toLowerCase().contains("age"))
                                                    textBox.setText(CommonMethods.calculateAge((monthOfYear + 1) + "-" + dayOfMonth + "-" + year, "MM-dd-yyyy"));
                                                else
                                                    textBox.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                            }
                                        });
                                    }
                                }
                            }
                        });

                        break;
                    case INPUT_TYPE.MOBILE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                        textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case INPUT_TYPE.NAME:
                        textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        break;
                    case INPUT_TYPE.PIN_CODE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case INPUT_TYPE.TEXTBOX_BIG:
                        textBox.setSingleLine(false);
                        textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        textBox.setMaxLines(10);
                        break;
                    case INPUT_TYPE.NUMBER:
                        textBox.setGravity(Gravity.END);
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                }

                textBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editTextError.setText("");
                        textBox.setBackgroundResource(R.drawable.edittext_selector);
                        // set latest value
                        field.setValue(String.valueOf(editable).trim());
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case TYPE.RADIOBUTTON: {
                View fieldLayout = inflater.inflate(R.layout.field_radiobutton_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final FlowRadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
                radioGroup.setId(CommonMethods.generateViewId());

                final TextView radioGroupError = fieldLayout.findViewById(R.id.radioGroupError);
                radioGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(radioGroupError.getId());

                ArrayList<String> dataList = field.getDataList();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
                    radioButton.setId(CommonMethods.generateViewId());
                    radioButton.setText(data);

                    radioButton.setEnabled(isEditable);

                    // set pre value
                    if (field.getValue().equals(radioButton.getText().toString()))
                        radioButton.setChecked(true);

                    radioGroup.addView(radioButton);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                        CommonMethods.hideKeyboard(getContext());

                        RadioButton radioButton = group.findViewById(checkedId);
                        field.setValue(radioButton.getText().toString());
                        radioGroupError.setText("");
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }
            case TYPE.CHECKBOX: {
                View fieldLayout = inflater.inflate(R.layout.field_checkbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                FlowLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
                checkBoxGroup.setId(CommonMethods.generateViewId());

                final TextView checkBoxGroupError = fieldLayout.findViewById(R.id.checkBoxGroupError);
                checkBoxGroupError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(checkBoxGroupError.getId());

                ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> values = field.getValues();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);

                    checkBox.setEnabled(isEditable);

                    // set pre value
                    for (String value : values)
                        if (value.equals(data))
                            checkBox.setChecked(true);

                    checkBox.setId(CommonMethods.generateViewId());
                    checkBox.setText(data);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            CommonMethods.hideKeyboard(getContext());
                            checkBoxGroupError.setText("");

                            // set latest value

                            if (isChecked) {
                                values.add(checkBox.getText().toString());
                            } else {
                                values.remove(checkBox.getText().toString());
                            }
                        }
                    });
                    checkBoxGroup.addView(checkBox);
                }

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                break;
            }
            case TYPE.DROPDOWN: {
                View fieldLayout = inflater.inflate(R.layout.field_dropdown_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final Spinner dropDown = fieldLayout.findViewById(R.id.dropDown);
                dropDown.setId(CommonMethods.generateViewId());

                field.setFieldId(dropDown.getId());

                final TextView dropDownError = fieldLayout.findViewById(R.id.dropDownError);
                dropDownError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(dropDownError.getId());

                final ArrayList<String> dataList = field.getDataList();
                if (dataList.size() > 0)
                    if (!dataList.get(0).equals("Select"))
                        dataList.add(0, "Select");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataList);
                dropDown.setAdapter(adapter);

                dropDown.setEnabled(isEditable);

                dropDown.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            CommonMethods.hideKeyboard(getContext());
                        }
                        return false;
                    }
                });

                // set pre value
                dropDown.setSelection(dataList.indexOf(field.getValue()));

                dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position != 0) {
                            // set latest value
                            field.setValue(dataList.get(position));
                            dropDownError.setText("");
                            dropDown.setBackgroundResource(R.drawable.dropdown_selector);

                        } else field.setValue("");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case PageFragment.TYPE.RATINGBAR: {

                View fieldLayout = inflater.inflate(R.layout.field_ratingbar_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                // Add Rating Bar

                RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                ratingBar.setRating(field.getRating());
                ratingBar.setMax(field.getMaxRating() * 2);
                ratingBar.setNumStars(field.getMaxRating());

                final EditText ratingReasonTextBox = fieldLayout.findViewById(R.id.ratingReasonTextBox);
                if (!field.getHint().equals(""))
                    ratingReasonTextBox.setHint(field.getHint());

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        field.setRating(rating);
                        if (rating < 3f && rating != 0f)
                            ratingReasonTextBox.setVisibility(View.VISIBLE);
                        else ratingReasonTextBox.setVisibility(View.GONE);
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }
        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .pickPhoto(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PageFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (!data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).isEmpty()) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOptions.skipMemoryCache(true);
//            requestOptions.error(R.drawable.ic_assignment);
//            requestOptions.placeholder(R.drawable.ic_assignment);

                    Glide.with(getContext())
                            .load(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0))
                            .apply(requestOptions)
                            .thumbnail(.1f)
                            .into(profilePhoto);
                }
            }
        }
    }
}