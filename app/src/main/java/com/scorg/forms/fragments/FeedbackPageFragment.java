package com.scorg.forms.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.models.form.Field;
import com.scorg.forms.models.form.Page;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;

public class FeedbackPageFragment extends Fragment {

    private static final String FIELDS = "fields";
    private ArrayList<Field> fields;
    private int formNumber;
    private String mReceivedFormName;
    private DatePickerDialog datePickerDialog;

    public FeedbackPageFragment() {
        // Required empty public constructor
    }

    public static FeedbackPageFragment newInstance(int formNumber, int position, Page page, String mReceivedFormName) {
        FeedbackPageFragment fragment = new FeedbackPageFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(FIELDS, page.getFields());
        args.putInt(FORM_NUMBER, formNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fields = getArguments().getParcelableArrayList(FIELDS);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback_page, container, false);
        LinearLayout fieldsContainer = rootView.findViewById(R.id.fieldsContainer);

        for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
            addField(fieldsContainer, fields, fields.get(fieldIndex), fieldIndex, inflater, -1);
        }

        return rootView;
    }

    @SuppressLint("SetTextI18n")
    private void addField(final View fieldsContainer, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {

        String questionNo = String.valueOf(fieldsIndex + 1) + ". ";

        switch (field.getType()) {

            case PageFragment.TYPE.TEXTBOX_GROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.feedback_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, field.getDataList());
                textBox.setAdapter(adapter);

                // set pre value
                textBox.setText(field.getValue());
                final String preValue = field.getValue();

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
                        addField(fieldsContainer, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, indexToAddView + moreFieldIndex + 1);
                    else
                        addField(fieldsContainer, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, -1);
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

                        field.setUpdated(!preValue.equals(field.getValue()));
                    }
                });

                ImageView plusButton = fieldLayout.findViewById(R.id.plusButton);

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

                            addField(fieldsContainer, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case PageFragment.TYPE.TEXTBOX: {
                View fieldLayout = inflater.inflate(R.layout.feedback_textbox_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());

                field.setFieldId(textBox.getId());

                final TextView editTextError = fieldLayout.findViewById(R.id.editTextError);
                editTextError.setId(CommonMethods.generateViewId());
                field.setErrorViewId(editTextError.getId());

                // set pre value
                textBox.setText(field.getValue());
                final String preValue = field.getValue();

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                setInputType(field, textBox);

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

                        field.setUpdated(!preValue.equals(field.getValue()));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case PageFragment.TYPE.RADIOBUTTON: {
                addRadioButton(questionNo, fieldsContainer, field, inflater, indexToAddView, /*radio_button->*/false);
                break;
            }

            case PageFragment.TYPE.RADIOBUTTON_WITH_TEXT: {
                addRadioButton(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case PageFragment.TYPE.CHECKBOX: {
                addCheckbox(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/false);
                break;
            }

            case PageFragment.TYPE.CHECKBOX_WITH_TEXT: {
                addCheckbox(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case PageFragment.TYPE.DROPDOWN: {
                addDropDown(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/false);
                break;
            }

            case PageFragment.TYPE.DROPDOWN_WITH_TEXT: {
                addDropDown(questionNo, fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case PageFragment.TYPE.RATINGBAR: {

                View fieldLayout = inflater.inflate(R.layout.feedback_ratingbar_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

                // Add Rating Bar

                RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                ratingBar.setRating(field.getRating());
                ratingBar.setMax(field.getMaxRating() * 2);
                ratingBar.setNumStars(field.getMaxRating());

                final EditText ratingReasonTextBox = fieldLayout.findViewById(R.id.ratingReasonTextBox);

                ratingReasonTextBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // set latest value
                        field.setTextValue(String.valueOf(editable).trim());
                    }
                });

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

    @SuppressLint("SetTextI18n")
    private void addDropDown(String questionNo, View fieldsContainer, final Field field, LayoutInflater inflater, int indexToAddView, boolean isMixed) {

        View fieldLayout = inflater.inflate(R.layout.feedback_dropdown_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        final Spinner dropDown = fieldLayout.findViewById(R.id.dropDown);
        dropDown.setId(CommonMethods.generateViewId());

        field.setFieldId(dropDown.getId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);
        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());
        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);

        final TextView dropDownError = fieldLayout.findViewById(R.id.dropDownError);
        dropDownError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(dropDownError.getId());

        boolean isOthersThere = false;

        final ArrayList<String> dataList = field.getDataList();
        if (dataList.size() > 0)
            if (!dataList.get(0).equals("Select"))
                dataList.add(0, "Select");

        if (dataList.contains(field.getShowWhenSelect()))
            isOthersThere = true;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.dropdown_item, dataList);
        dropDown.setAdapter(adapter);

        // set pre value
        dropDown.setSelection(dataList.indexOf(field.getValue()));
        final String preValue = field.getValue();
        final String preOtherValue = field.getTextValue();

        dropDown.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CommonMethods.hideKeyboard(getContext());
                }
                return false;
            }
        });

        final boolean finalIsOthersThere = isOthersThere;
        dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {

                    if (finalIsOthersThere) {
                        if (dataList.get(position).equalsIgnoreCase(field.getShowWhenSelect()))
                            otherTextBoxParent.setVisibility(View.VISIBLE);
                        else {
                            otherTextBox.setText("");
                            otherTextBoxParent.setVisibility(View.GONE);
                        }
                    }

                    // set latest value
                    field.setValue(dataList.get(position));
                    dropDownError.setText("");
                    dropDown.setBackgroundResource(R.drawable.dropdown_selector);

                } else field.setValue("");

                field.setUpdated(!preValue.equals(field.getValue()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere)
                otherTextBoxParent.setVisibility(View.GONE);
            else otherTextBoxParent.setVisibility(View.VISIBLE);

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setInputType(field, otherTextBox);
            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));

                    field.setUpdated(!preOtherValue.equals(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

    private void setInputType(final Field field, final EditText textBox) {

//        LinearLayout.LayoutParams textBoxParams = (LinearLayout.LayoutParams) textBox.getLayoutParams();

        switch (field.getInputType()) {
            case PageFragment.INPUT_TYPE.EMAIL:
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case PageFragment.INPUT_TYPE.DATE:
                textBox.setCursorVisible(false);
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.date_size);

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
                                    @SuppressLint("SetTextI18n")
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
            case PageFragment.INPUT_TYPE.MOBILE:
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case PageFragment.INPUT_TYPE.NAME:
                textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                break;
            case PageFragment.INPUT_TYPE.PIN_CODE:
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case PageFragment.INPUT_TYPE.TEXTBOX_BIG:
                textBox.setSingleLine(false);
                textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                textBox.setLines(6);
                textBox.setGravity(Gravity.TOP);
                textBox.setMaxLines(10);
                break;
            case PageFragment.INPUT_TYPE.NUMBER:
                textBox.setSingleLine(false);
//                textBox.setGravity(Gravity.END);
//                textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.number_size);
                textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void addCheckbox(String questionNo, View fieldsContainer, final Field field, LayoutInflater inflater, int indexToAddView, boolean isMixed) {
        View fieldLayout = inflater.inflate(R.layout.feedback_checkbox_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        TableLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
        checkBoxGroup.setId(CommonMethods.generateViewId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);
        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());
        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);

        final TextView checkBoxGroupError = fieldLayout.findViewById(R.id.checkBoxGroupError);
        checkBoxGroupError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(checkBoxGroupError.getId());

        ArrayList<String> dataList = field.getDataList();
        final ArrayList<String> values = field.getValues();
        final ArrayList<String> preValues = new ArrayList<>(values);

        final String preOtherValue = field.getTextValue();

        boolean isOthersThere = false;

        int matrix = field.getMatrix() == 0 ? 2 : field.getMatrix();

        for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
            String data = dataList.get(dataIndex);

            if (data.equalsIgnoreCase(field.getShowWhenSelect()))
                isOthersThere = true;

            final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);
            checkBox.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            checkBox.setId(CommonMethods.generateViewId());

            // set pre value
            for (String value : values)
                if (value.equals(data))
                    checkBox.setChecked(true);

            checkBox.setText(data);
            final boolean finalIsOthersThere = isOthersThere;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    CommonMethods.hideKeyboard(getContext());

                    String valueText = checkBox.getText().toString();

                    if (finalIsOthersThere) {
                        if (valueText.equalsIgnoreCase(field.getShowWhenSelect()) && isChecked)
                            otherTextBoxParent.setVisibility(View.VISIBLE);
                        else if (valueText.equalsIgnoreCase(field.getShowWhenSelect()) && !isChecked) {
                            otherTextBox.setText("");
                            otherTextBoxParent.setVisibility(View.GONE);
                        }
                    }

                    checkBoxGroupError.setText("");

                    // set latest value

                    if (isChecked) {
                        values.add(valueText);
                    } else {
                        values.remove(valueText);
                    }

                    Collections.sort(preValues);
                    Collections.sort(values);
                    field.setUpdated(!Arrays.equals(preValues.toArray(),values.toArray()));
                }
            });

            if (dataIndex % matrix == 0) {

                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                tableRow.addView(checkBox);
                checkBoxGroup.addView(tableRow);
            } else {
                TableRow tableRow = (TableRow) checkBoxGroup.getChildAt(checkBoxGroup.getChildCount() - 1);
                tableRow.addView(checkBox);
            }
        }

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere)
                otherTextBoxParent.setVisibility(View.GONE);
            else otherTextBoxParent.setVisibility(View.VISIBLE);

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setInputType(field, otherTextBox);
            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));

                    field.setUpdated(!preOtherValue.equals(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

    // Add Radio Button

    @SuppressLint("SetTextI18n")
    private void addRadioButton(String questionNo, final View fieldsContainer, final Field field, final LayoutInflater inflater, int indexToAddView, boolean isMixed) {
        View fieldLayout = inflater.inflate(R.layout.feedback_radiobutton_layout, (LinearLayout) fieldsContainer, false);
        TextView labelView = fieldLayout.findViewById(R.id.labelView);
        labelView.setText(questionNo + (field.isMandatory() ? "*" + field.getName() : field.getName()));

        final RadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
        radioGroup.setId(CommonMethods.generateViewId());

        final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
        final TextView unitTextView = fieldLayout.findViewById(R.id.unitTextView);
        if (!field.getUnit().equals(""))
            unitTextView.setText(field.getUnit());
        final LinearLayout otherTextBoxParent = fieldLayout.findViewById(R.id.otherTextBoxParent);

        final TextView radioGroupError = fieldLayout.findViewById(R.id.radioGroupError);
        radioGroupError.setId(CommonMethods.generateViewId());
        field.setErrorViewId(radioGroupError.getId());

        ArrayList<String> dataList = field.getDataList();

        final String preValue = field.getValue();
        final String preOtherValue = field.getTextValue();

        boolean isOthersThere = false;

        for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
            String data = dataList.get(dataIndex);

            if (data.equalsIgnoreCase(field.getShowWhenSelect()))
                isOthersThere = true;

            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
            radioButton.setId(CommonMethods.generateViewId());
            radioButton.setText(data);

            // set pre value
            if (field.getValue().equals(radioButton.getText().toString()))
                radioButton.setChecked(true);

            radioGroup.addView(radioButton);
        }

        final boolean finalIsOthersThere = isOthersThere;
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                CommonMethods.hideKeyboard(getContext());

                String valueText = ((RadioButton) group.findViewById(checkedId)).getText().toString();

                if (finalIsOthersThere) {
                    if (valueText.equalsIgnoreCase(field.getShowWhenSelect()))
                        otherTextBoxParent.setVisibility(View.VISIBLE);
                    else {
                        otherTextBox.setText("");
                        otherTextBoxParent.setVisibility(View.GONE);
                    }
                }

                field.setValue(valueText);
                radioGroupError.setText("");

                field.setUpdated(!preValue.equals(field.getValue()));
            }
        });

        // Add Extra edit text

        if (isMixed) {

            if (isOthersThere)
                otherTextBoxParent.setVisibility(View.GONE);
            else otherTextBoxParent.setVisibility(View.VISIBLE);

            if (!field.getHint().equals(""))
                otherTextBox.setHint(field.getHint());
            setInputType(field, otherTextBox);

            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setTextValue(String.valueOf(s));

                    field.setUpdated(!preOtherValue.equals(field.getTextValue()));
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }
}
