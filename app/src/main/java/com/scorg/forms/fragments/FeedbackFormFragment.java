package com.scorg.forms.fragments;


import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.scorg.forms.R;
import com.scorg.forms.customui.CustomAutoCompleteEditText;
import com.scorg.forms.customui.CustomEditText;
import com.scorg.forms.customui.CustomViewPager;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Page;
import com.scorg.forms.models.Section;
import com.scorg.forms.util.Valid;

import java.util.ArrayList;

public class FeedbackFormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String FORM_NUMBER = "form_number";
    private static final String TAG = "FeedbackForm";
    private static final String PAGES = "pages";
    public static final String FORM_NAME = "form_name";
    public static final String IS_EDITABLE = "is_editable";
    public static final String IS_NEW = "is_new";

    ArrayList<Page> pages;
    private String formName;
    private int formNumber;
    private boolean isEditable = true;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;

    private Button preButton;
    private Button nextButton;
    private Button submitButton;
    private String mReceivedFormName;
    private boolean isValid;

    public FeedbackFormFragment() {
        // Required empty public constructor
    }

    public static FeedbackFormFragment newInstance(int formNumber, ArrayList<Page> pages, String formName, boolean isNew, String date) {
        FeedbackFormFragment fragment = new FeedbackFormFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PAGES, pages);
        args.putInt(FORM_NUMBER, formNumber);
        args.putBoolean(IS_NEW, isNew);
        args.putString(FORM_NAME, formName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pages = getArguments().getParcelableArrayList(PAGES);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback_form, container, false);

        TextView mTitleTextView = rootView.findViewById(R.id.titleTextView);
        mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTitleTextView.setText(getString(R.string.feedback));

        // Buttons

        preButton = (Button) rootView.findViewById(R.id.backButton);
        nextButton = (Button) rootView.findViewById(R.id.nextButton);
        submitButton = (Button) rootView.findViewById(R.id.submitEditButton);

        Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_edit);

        if (isEditable) {
            submitButton.setText(getResources().getString(R.string.submit));
            submitButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            submitButton.setText(getResources().getString(R.string.edit));
            submitButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }

        mViewPager = (CustomViewPager) rootView.findViewById(R.id.container);
        mViewPager.setId(formNumber + 2000);
        setupViewPager(pages);


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageValidation(mViewPager.getCurrentItem(), true);
                if (isValid) {
                    int nextPosition = mViewPager.getCurrentItem() + 1;
                    mViewPager.setCurrentItem(nextPosition);
                    handleButtons(nextPosition);
                }
            }
        });

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prePosition = mViewPager.getCurrentItem() - 1;
                mViewPager.setCurrentItem(prePosition);
                handleButtons(prePosition);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageValidation(mViewPager.getCurrentItem(), true);
                if (isValid) {
                   // api call
                }
            }
        });

        return rootView;
    }

    private void pageValidation(int selectedTabPosition, boolean isShowError) {

        View parentView = null;

        if (isShowError)
            parentView = mSectionsPagerAdapter.getItem(selectedTabPosition).getView();

        Page page = pages.get(selectedTabPosition);

        isValid = true;
            for (int fieldsIndex = 0; fieldsIndex < page.getFields().size(); fieldsIndex++) {
                Field field = page.getFields().get(fieldsIndex);

                fieldValidation(field, parentView, isShowError);

                ArrayList<Field> moreFields = field.getTextBoxGroup();

                for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                    Field moreField = moreFields.get(moreFieldIndex);
                    fieldValidation(moreField, parentView, isShowError);
                }
        }
    }

    private void fieldValidation(Field field, View roolView, boolean isShowError) {

        switch (field.getType()) {

            case PageFragment.TYPE.TEXTBOX_GROUP: {

                if (field.isMandatory()) {
                    if (field.getValue().equals("")) {
                        if (isShowError) {
                            CustomAutoCompleteEditText editText = roolView.findViewById(field.getFieldId());
                            TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                            errorTextView.setText("Please enter " + field.getName());
                            editText.setBackgroundResource(R.drawable.edittext_error_selector);
                        }
                        isValid = false;
                        return;
                    }
                }

                break;
            }

            case PageFragment.TYPE.TEXTBOX: {

                if (field.isMandatory()) {
                    if (field.getValue().equals("")) {
                        if (isShowError) {
                            CustomEditText editText = roolView.findViewById(field.getFieldId());
                            TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                            errorTextView.setText("Please enter " + field.getName());
                            editText.setBackgroundResource(R.drawable.edittext_error_selector);
                        }
                        isValid = false;
                        return;
                    }
                }

                switch (field.getInputType()) {
                    case PageFragment.INPUT_TYPE.EMAIL:
                        if (!field.getValue().equals("")) {
                            if (!Valid.validateEmail(field.getValue(), getContext(), false)) {
                                if (isShowError) {
                                    CustomEditText editText = roolView.findViewById(field.getFieldId());
                                    TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                                    errorTextView.setText("Please enter valid " + field.getName());
                                    editText.setBackgroundResource(R.drawable.edittext_error_selector);
                                }
                                isValid = false;
                                return;
                            }
                        }
                        break;
                    case PageFragment.INPUT_TYPE.MOBILE:
                        if (!field.getValue().equals("")) {
                            if (!Valid.validateMobileNo(field.getValue(), getContext(), false)) {
                                if (isShowError) {
                                    CustomEditText editText = roolView.findViewById(field.getFieldId());
                                    TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                                    errorTextView.setText("Please enter valid " + field.getName());
                                    editText.setBackgroundResource(R.drawable.edittext_error_selector);
                                }
                                isValid = false;
                                return;
                            }
                        }
                        break;
                    case PageFragment.INPUT_TYPE.PIN_CODE:
                        if (!field.getValue().equals("")) {
                            if (field.getValue().length() != 6) {
                                if (isShowError) {
                                    CustomEditText editText = roolView.findViewById(field.getFieldId());
                                    TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                                    errorTextView.setText("Please enter valid " + field.getName());
                                    editText.setBackgroundResource(R.drawable.edittext_error_selector);
                                }
                                isValid = false;
                                return;
                            }
                        }
                        break;
                }

                break;
            }

            case PageFragment.TYPE.RADIOBUTTON_WITH_TEXT:
            case PageFragment.TYPE.RADIOBUTTON: {

                if (field.isMandatory()) {
                    if (field.getValue().equals("")) {
                        if (isShowError) {
                            TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                            errorTextView.setText("Please Select " + field.getName());
                        }
                        isValid = false;
                        return;
                    }
                }

                break;
            }

            case PageFragment.TYPE.CHECKBOX_WITH_TEXT:
            case PageFragment.TYPE.CHECKBOX: {
                if (field.isMandatory()) {
                    if (field.getValues().size() == 0) {
                        if (isShowError) {
                            TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                            errorTextView.setText("Please Select " + field.getName());
                        }
                        isValid = false;
                        return;
                    }
                }
                break;
            }

            case PageFragment.TYPE.DROPDOWN_WITH_TEXT:
            case PageFragment.TYPE.DROPDOWN: {

                if (field.isMandatory()) {
                    if (field.getValue().equals("")) {
                        if (isShowError) {
                            Spinner dropDown = roolView.findViewById(field.getFieldId());
                            TextView errorTextView = roolView.findViewById(field.getErrorViewId());
                            errorTextView.setText("Please Select " + field.getName());
                            dropDown.setBackgroundResource(R.drawable.dropdown_error_selector);
                        }
                        isValid = false;
                        return;
                    }
                }
                break;
            }
        }
    }

    @SuppressWarnings("CheckResult")
    private void setupViewPager(final ArrayList<Page> pages) {

        // Set View Pager Paging Disable.
        mViewPager.setPagingEnabled(false);

        ArrayList<FeedbackPageFragment> pageFragments = new ArrayList<>();

        for (int position = 0; position < pages.size(); position++) {

            FeedbackPageFragment pageFragment = FeedbackPageFragment.newInstance(formNumber, position, pages.get(position), isEditable, mReceivedFormName);
            pageFragments.add(pageFragment);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(pageFragments, getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        handleButtons(0);

    }

    private void handleButtons(int position) {
        if (position == 0) {
            preButton.setEnabled(false);
            preButton.setVisibility(View.GONE);
        } else {
            preButton.setEnabled(true);
            preButton.setVisibility(View.VISIBLE);
        }

        if ((position + 1) == mSectionsPagerAdapter.getCount()) {
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<FeedbackPageFragment> pageFragments;

        public SectionsPagerAdapter(ArrayList<FeedbackPageFragment> pageFragments, FragmentManager fm) {
            super(fm);
            this.pageFragments = pageFragments;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            if (isEditable)
            return pageFragments.get(position);
//            else return ProfilePageFragment.newInstance(formNumber, position, pages.get(position));
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position).getPageName();
        }
    }

}
