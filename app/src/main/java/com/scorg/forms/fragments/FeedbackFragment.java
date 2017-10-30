package com.scorg.forms.fragments;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.customui.FlowLayout;
import com.scorg.forms.customui.FlowRadioGroup;
import com.scorg.forms.models.Field;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;
import java.util.Calendar;

import static com.scorg.forms.fragments.FormFragment.FORM_NAME;
import static com.scorg.forms.fragments.FormFragment.FORM_NUMBER;
import static com.scorg.forms.fragments.FormFragment.IS_NEW;

public class FeedbackFragment extends Fragment {

    private static final String FIELDS = "fields";
    private boolean isNew;
    private ArrayList<Field> fields;
    private int formNumber;
    private String mReceivedFormName;
    private boolean isEditable = true;
    private DatePickerDialog datePickerDialog;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    public static FeedbackFragment newInstance(int formNumber, ArrayList<Field> fields, String formName/*, boolean isEditable*/, boolean isNew, String date) {
        FeedbackFragment fragment = new FeedbackFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(FIELDS, fields);
        args.putInt(FORM_NUMBER, formNumber);
//        args.putBoolean(IS_EDITABLE, isEditable);
        args.putBoolean(IS_NEW, isNew);
        args.putString(FORM_NAME, formName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            isEditable = getArguments().getBoolean(IS_EDITABLE);
            isNew = getArguments().getBoolean(IS_NEW);
            fields = getArguments().getParcelableArrayList(FIELDS);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        LinearLayout fieldsContainer = rootView.findViewById(R.id.fieldsContainer);

        TextView mTitleTextView = rootView.findViewById(R.id.titleTextView);
        mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTitleTextView.setText(getString(R.string.feedback));

        for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
            addField(fieldsContainer, fields, fields.get(fieldIndex), fieldIndex, inflater, -1);
        }

        return rootView;
    }

    private void addField(final View fieldsContainer, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case PageFragment.TYPE.TEXTBOXGROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.field_autocomplete_textbox_layout, (LinearLayout) fieldsContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());
                field.setFieldId(textBox.getId());

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
                        field.setValue(String.valueOf(editable));
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

                            addField(fieldsContainer, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case PageFragment.TYPE.TEXTBOX: {
                View fieldLayout = inflater.inflate(R.layout.field_textbox_layout, (LinearLayout) fieldsContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(field.isMandatory() ? "*" + field.getName() : field.getName());

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                LinearLayout.LayoutParams textBoxParams = (LinearLayout.LayoutParams) textBox.getLayoutParams();
                textBox.setId(CommonMethods.generateViewId());
                textBox.setEnabled(isEditable);

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
                    case PageFragment.INPUT_TYPE.EMAIL:
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case PageFragment.INPUT_TYPE.DATE:
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
                    case PageFragment.INPUT_TYPE.MOBILE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.mobile_size);
                        textBox.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case PageFragment.INPUT_TYPE.NAME:
                        textBox.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                        break;
                    case PageFragment.INPUT_TYPE.PIN_CODE:
                        textBoxParams.width = getResources().getDimensionPixelSize(R.dimen.pincode_size);
                        textBox.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case PageFragment.INPUT_TYPE.TEXTBOX_BIG:
                        textBox.setSingleLine(false);
                        textBox.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                        textBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        textBox.setLines(3);
                        textBox.setMaxLines(10);
                        break;
                    case PageFragment.INPUT_TYPE.NUMBER:
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
                        field.setValue(String.valueOf(editable));
                    }
                });

                // Add Rating Bar

                if (field.getMaxRating() > 0) {
                    RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(field.getRating());
                    ratingBar.setMax(field.getMaxRating() * 2);
                    ratingBar.setNumStars(field.getMaxRating());

                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            field.setRating(rating);
                        }
                    });
                }

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);
                break;
            }

            case PageFragment.TYPE.RADIOBUTTON: {
                addRadioButton(fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/false);
                break;
            }

            case PageFragment.TYPE.MIXGROUP: {
                addRadioButton(fieldsContainer, field, inflater, indexToAddView, /*mixed_group->*/true);
                break;
            }

            case PageFragment.TYPE.CHECKBOX: {
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

                // Add Rating Bar

                if (field.getMaxRating() > 0) {
                    RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
                    ratingBar.setVisibility(View.VISIBLE);
                    ratingBar.setRating(field.getRating());
                    ratingBar.setMax(field.getMaxRating() * 2);
                    ratingBar.setNumStars(field.getMaxRating());

                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            field.setRating(rating);
                        }
                    });
                }

                if (indexToAddView != -1)
                    ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) fieldsContainer).addView(fieldLayout);

                break;
            }

            case PageFragment.TYPE.DROPDOWN: {
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
        }
    }

    // Add Radio Button

    private void addRadioButton(final View fieldsContainer, final Field field, final LayoutInflater inflater, int indexToAddView, boolean isMixed) {
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

                String valueText = ((RadioButton) group.findViewById(checkedId)).getText().toString();
                field.setValue(valueText);
                radioGroupError.setText("");
            }
        });

        // Add Extra edit text

        if (isMixed) {
            final EditText otherTextBox = fieldLayout.findViewById(R.id.otherTextBox);
            otherTextBox.setVisibility(View.VISIBLE);
            otherTextBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    field.setOthers(String.valueOf(s));
                }
            });
        }

        // Add Rating Bar

        if (field.getMaxRating() > 0) {
            RatingBar ratingBar = fieldLayout.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.VISIBLE);
            ratingBar.setRating(field.getRating());
            ratingBar.setMax(field.getMaxRating() * 2);
            ratingBar.setNumStars(field.getMaxRating());

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    field.setRating(rating);
                }
            });
        }

        if (indexToAddView != -1)
            ((LinearLayout) fieldsContainer).addView(fieldLayout, indexToAddView);
        else ((LinearLayout) fieldsContainer).addView(fieldLayout);
    }

}
