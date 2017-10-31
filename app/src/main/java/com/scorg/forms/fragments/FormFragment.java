package com.scorg.forms.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomAutoCompleteEditText;
import com.scorg.forms.customui.CustomEditText;
import com.scorg.forms.customui.CustomViewPager;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Page;
import com.scorg.forms.models.Section;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

import java.util.ArrayList;

public class FormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String FORM_NUMBER = "form_number";
    private static final String TAG = "Form";
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
    private TabLayout mTabLayout;

    ButtonClickListener mListener;
    private Button preButton;
    private Button nextButton;
    private Button submitButton;
    private RelativeLayout mAllButtonLayout;
    private boolean isNew;
    private String mReceivedFormName;
    private boolean isValid;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance(int formNumber, ArrayList<Page> pages, String formName/*, boolean isEditable*/, boolean isNew, String date) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PAGES, pages);
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
            pages = getArguments().getParcelableArrayList(PAGES);
            formNumber = getArguments().getInt(FORM_NUMBER);
            mReceivedFormName = getArguments().getString(FORM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View roolView = inflater.inflate(R.layout.fragment_form, container, false);

        // Buttons

        mAllButtonLayout = roolView.findViewById(R.id.buttonLayout);
        preButton = (Button) roolView.findViewById(R.id.backButton);
        nextButton = (Button) roolView.findViewById(R.id.nextButton);
        submitButton = (Button) roolView.findViewById(R.id.submitEditButton);

        Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_edit);

        if (isEditable) {
            submitButton.setText(getResources().getString(R.string.submit));
            submitButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            submitButton.setText(getResources().getString(R.string.edit));
            submitButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }

        // Set up the ViewPager with the sections adapter.
        mTabLayout = (TabLayout) roolView.findViewById(R.id.tabs);
        mViewPager = (CustomViewPager) roolView.findViewById(R.id.container);
        mTabLayout.setId(formNumber + 1000);
        mViewPager.setId(formNumber + 2000);
        setupViewPager(pages);

        if (mTabLayout.getTabCount() > 5) {
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mTabLayout.getTabCount() - 1) == mTabLayout.getSelectedTabPosition()) {
                    mListener.nextClick(formNumber);
                } else {
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition() + 1).select();
                }
            }
        });

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == mTabLayout.getSelectedTabPosition()) {
                    mListener.backClick(formNumber);
                } else {
                    mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition() - 1).select();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditable) {

                    pageValidation(mTabLayout.getSelectedTabPosition(), true);

                    if (isValid)
                        mListener.submitClick(formNumber, isNew);

                    // hide keyboard
                    CommonMethods.hideKeyboard(getContext());

                } else mListener.editClick(formNumber, isNew);
            }
        });

        return roolView;
    }

    private void pageValidation(int selectedTabPosition, boolean isShowError) {

        View parentView = null;

        if (isShowError)
            parentView = mSectionsPagerAdapter.getItem(selectedTabPosition).getView();

        Page page = pages.get(selectedTabPosition);

        isValid = true;
        for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {
            Section section = page.getSection().get(sectionIndex);
            for (int fieldsIndex = 0; fieldsIndex < section.getFields().size(); fieldsIndex++) {
                Field field = section.getFields().get(fieldsIndex);

                fieldValidation(field, parentView, isShowError);

                ArrayList<Field> moreFields = field.getTextBoxGroup();

                for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                    Field moreField = moreFields.get(moreFieldIndex);
                    fieldValidation(moreField, parentView, isShowError);
                }
            }
        }
    }

    @SuppressWarnings("CheckResult")
    private void setupViewPager(final ArrayList<Page> pages) {

        // Set View Pager Paging Disable.
        mViewPager.setPagingEnabled(false);

        ArrayList<PageFragment> pageFragments = new ArrayList<>();

        for (int position = 0; position < pages.size(); position++) {

            PageFragment pageFragment = PageFragment.newInstance(formNumber, position, pages.get(position), isEditable, mReceivedFormName);
            pageFragments.add(pageFragment);

            View tabView = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);

            ImageView indicatorIcon = tabView.findViewById(R.id.indicatorIcon);

            TextView leftView = tabView.findViewById(R.id.leftView);
            TextView rightView = tabView.findViewById(R.id.rightView);
            TextView titleTextView = tabView.findViewById(R.id.titleTextView);

            int iconSize = getResources().getDimensionPixelSize(R.dimen.page_icon_size);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.override(iconSize, iconSize);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
            requestOptions.error(R.drawable.ic_assignment_black);
            requestOptions.placeholder(R.drawable.ic_assignment_black);

            Glide.with(getContext())
                    .load(pages.get(position).getPageIcon())
                    .apply(requestOptions)
                    .into(indicatorIcon);

            titleTextView.setText(pages.get(position).getPageName());

            if (position == 0) {
                selectTab(tabView, true);
                leftView.setVisibility(View.INVISIBLE);
            } else {
                leftView.setVisibility(View.VISIBLE);
                selectTab(tabView, false);
            }
            if (pages.size() == (position + 1))
                rightView.setVisibility(View.INVISIBLE);
            else rightView.setVisibility(View.VISIBLE);

            TabLayout.Tab customTab = mTabLayout.newTab().setCustomView(tabView);
            mTabLayout.addTab(customTab);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(pageFragments, getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        handleButtons(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            TabLayout.Tab unSelectedTab;
            boolean isTabSelectedListener = true;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (isTabSelectedListener) {

                    if (unSelectedTab.getPosition() > tab.getPosition())
                        isValid = true;
                    else {
                        for (int pageIndex = unSelectedTab.getPosition(); pageIndex < tab.getPosition(); pageIndex++) {
                            if (pageIndex == unSelectedTab.getPosition())
                                pageValidation(pageIndex, true);
                            else {
                                pageValidation(pageIndex, false);
                                if (!isValid)
                                    break;
                            }
                        }
                    }

                    if (isValid) {
                        selectTab(unSelectedTab.getCustomView(), false);
                        selectTab(tab.getCustomView(), true);
                        mViewPager.setCurrentItem(tab.getPosition(), true);
                        handleButtons(tab.getPosition());
                    } else {
                        isTabSelectedListener = false;
                        unSelectedTab.select();
                    }

                } else isTabSelectedListener = true;

                // hide keyboard
                CommonMethods.hideKeyboard(getContext());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (isTabSelectedListener)
                    unSelectedTab = tab;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void handleButtons(int position) {
        if (position == 0) {
            preButton.setEnabled(false);
            preButton.setVisibility(View.GONE);
        } else {
            preButton.setEnabled(true);
            preButton.setVisibility(View.VISIBLE);
        }

        if ((position + 1) == mTabLayout.getTabCount()) {
            nextButton.setEnabled(false);
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setEnabled(true);
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void selectTab(View tabView, boolean isSelected) {

        LinearLayout tabBackground = tabView.findViewById(R.id.tabBackground);

        TextView titleTextView = tabView.findViewById(R.id.titleTextView);
        ImageView downArrow = tabView.findViewById(R.id.downArrow);

        if (isSelected) {
            tabBackground.setBackgroundColor(getResources().getColor(R.color.form_background));
            titleTextView.setTextColor(getResources().getColor(android.R.color.white));
            downArrow.setImageResource(R.drawable.down_arrow);

        } else {
            tabBackground.setBackgroundColor(getResources().getColor(R.color.tab_unfilled_color));
            titleTextView.setTextColor(getResources().getColor(R.color.text_color));
            downArrow.setImageDrawable(null);
        }
    }

    private void fillTab(TextView indicatorText, boolean isFilled) {
        if (isFilled)
            indicatorText.setBackgroundResource(R.drawable.badge);
        else
            indicatorText.setBackgroundResource(R.drawable.unfilled_badge);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<PageFragment> pageFragments;

        public SectionsPagerAdapter(ArrayList<PageFragment> pageFragments, FragmentManager fm) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ButtonClickListener) {
            mListener = (ButtonClickListener) context;
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

    // Listener
    public interface ButtonClickListener {
        void backClick(int formNumber);

        void nextClick(int formNumber);

        void submitClick(int formNumber, boolean isNew);

        void editClick(int formNumber, boolean isNew);
    }

    private void fieldValidation(Field field, View roolView, boolean isShowError) {

        switch (field.getType()) {

            case PageFragment.TYPE.TEXTBOXGROUP: {

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
}
