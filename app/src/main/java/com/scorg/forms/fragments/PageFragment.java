package com.scorg.forms.fragments;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.scorg.forms.R;
import com.scorg.forms.customui.FlowLayout;
import com.scorg.forms.customui.FlowRadioGroup;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Page;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class PageFragment extends Fragment {

    private DatePickerDialog datePickerDialog;

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

    public PageFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PageFragment newInstance(int formNumber, int pageNumber, Page page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(FormFragment.FORM_NUMBER, formNumber);
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
            formNumber = getArguments().getInt(PAGE_NUMBER);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pages, container, false);

        TextView formNumberTextView = rootView.findViewById(R.id.titleView);
        LinearLayout formContainerLayout = rootView.findViewById(R.id.formContainer);

        formNumberTextView.setText(page.getPageName());

        for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {
            final View sectionContainer = inflater.inflate(R.layout.section_container, formContainerLayout, false);
            final ArrayList<Field> fields = page.getSection().get(sectionIndex).getFields();

            TextView sectionTitle = sectionContainer.findViewById(R.id.sectionTitleView);
            sectionTitle.setText(page.getSection().get(sectionIndex).getSectionName());

            for (int fieldsIndex = 0; fieldsIndex < fields.size(); fieldsIndex++) {
                final Field field = fields.get(fieldsIndex);
                addField(sectionContainer, sectionIndex, fields, field, fieldsIndex, inflater, -1);
            }

            formContainerLayout.addView(sectionContainer);
        }

        return rootView;
    }

    private void addField(final View sectionContainer, final int sectionIndex, final ArrayList<Field> fields, final Field field, final int fieldsIndex, final LayoutInflater inflater, int indexToAddView) {
        switch (field.getType()) {

            case TYPE.TEXTBOXGROUP: {
                // Added Extended Layout
                final View fieldLayout = inflater.inflate(R.layout.autocomplete_textbox_layout, (LinearLayout) sectionContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.getName());

                final AutoCompleteTextView textBox = fieldLayout.findViewById(R.id.editText);
                textBox.setId(CommonMethods.generateViewId());

                // set pre value
                textBox.setText(field.getValue());

                if (field.getLength() > 0) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(field.getLength());
                    textBox.setFilters(fArray);
                }

                // Add Parent First

                if (indexToAddView != -1)
                    ((LinearLayout) sectionContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) sectionContainer).addView(fieldLayout);

                ArrayList<Field> moreFields = field.getTextBoxGroup();

                for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                    if (indexToAddView != -1)
                        addField(sectionContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, indexToAddView + moreFieldIndex + 1);
                    else
                        addField(sectionContainer, sectionIndex, fields, moreFields.get(moreFieldIndex), fieldsIndex, inflater, -1);
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
                        // set latest value
                        field.setValue(String.valueOf(editable));
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

                            ArrayList<Field> clonedMoreFields = new ArrayList<Field>();
                            for (Field field1 :
                                    field.getTextBoxGroup()) {
                                Field cloneField = (Field) field1.clone();
                                cloneField.setValue("");
                                clonedMoreFields.add(cloneField);
                            }

                            fieldGroupNew.setTextBoxGroup(clonedMoreFields);

                            addField(sectionContainer, sectionIndex, fields, fieldGroupNew, fieldsIndex, inflater, ((LinearLayout) fieldLayout.getParent()).indexOfChild(fieldLayout) + 1 + fieldGroupNew.getTextBoxGroup().size());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            }

            case TYPE.TEXTBOX: {
                View fieldLayout = inflater.inflate(R.layout.textbox_layout, (LinearLayout) sectionContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);

                labelView.setText(field.getName());

                final EditText textBox = fieldLayout.findViewById(R.id.editText);
                RelativeLayout.LayoutParams textBoxParams = (RelativeLayout.LayoutParams) textBox.getLayoutParams();

                textBox.setId(CommonMethods.generateViewId());

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
                                if (hasFocus){
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
                                                if (field.getName().equalsIgnoreCase("age"))
                                                    textBox.setText(String.valueOf(CommonMethods.getAge(year, monthOfYear, dayOfMonth)));
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
                        // set latest value
                        field.setValue(String.valueOf(editable));
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) sectionContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) sectionContainer).addView(fieldLayout);
                break;
            }

            case TYPE.RADIOBUTTON: {
                View fieldLayout = inflater.inflate(R.layout.radiobutton_layout, (LinearLayout) sectionContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.getName());

                FlowRadioGroup radioGroup = fieldLayout.findViewById(R.id.radioGroup);
                radioGroup.setId(CommonMethods.generateViewId());
                ArrayList<String> dataList = field.getDataList();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.radiobutton, radioGroup, false);
                    radioButton.setId(CommonMethods.generateViewId());
                    radioButton.setText(data);

                    // set pre value
                    if (field.getValue().equals(radioButton.getText().toString()))
                        radioButton.setChecked(true);

                    radioGroup.addView(radioButton);
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        field.setValue(((RadioButton) group.findViewById(checkedId)).getText().toString());
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) sectionContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) sectionContainer).addView(fieldLayout);
                break;
            }
            case TYPE.CHECKBOX: {
                View fieldLayout = inflater.inflate(R.layout.checkbox_layout, (LinearLayout) sectionContainer, false);
                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.getName());

                FlowLayout checkBoxGroup = fieldLayout.findViewById(R.id.checkBoxGroup);
                checkBoxGroup.setId(CommonMethods.generateViewId());

                ArrayList<String> dataList = field.getDataList();
                final ArrayList<String> values = field.getValues();

                for (int dataIndex = 0; dataIndex < dataList.size(); dataIndex++) {
                    String data = dataList.get(dataIndex);
                    final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, checkBoxGroup, false);

                    // set pre value
                    for (String value : values)
                        if (value.equals(data))
                            checkBox.setChecked(true);

                    checkBox.setId(CommonMethods.generateViewId());
                    checkBox.setText(data);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
                    ((LinearLayout) sectionContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) sectionContainer).addView(fieldLayout);

                break;
            }
            case TYPE.DROPDOWN: {
                View fieldLayout = inflater.inflate(R.layout.dropdown_layout, (LinearLayout) sectionContainer, false);

                TextView labelView = fieldLayout.findViewById(R.id.labelView);
                labelView.setText(field.getName());

                Spinner dropDown = fieldLayout.findViewById(R.id.dropDown);
                dropDown.setId(CommonMethods.generateViewId());

                final ArrayList<String> dataList = field.getDataList();

                ArrayAdapter<String> adapter = new ArrayAdapter<>(dropDown.getContext(), R.layout.spinner_item, dataList);
                dropDown.setAdapter(adapter);

                // set pre value
                dropDown.setSelection(dataList.indexOf(field.getValue()));

                dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // set latest value
                        field.setValue(dataList.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (indexToAddView != -1)
                    ((LinearLayout) sectionContainer).addView(fieldLayout, indexToAddView);
                else ((LinearLayout) sectionContainer).addView(fieldLayout);
                break;
            }
        }
    }

    public Page getPage() {
        return page;
    }
}