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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomViewPager;
import com.scorg.forms.models.Field;
import com.scorg.forms.models.Page;
import com.scorg.forms.models.Section;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;

public class FormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String FORM_NUMBER = "form_number";
    private static final String TAG = "Form";
    private static final String PAGES = "pages";
    private static final String FORM_NAME = "form_name";
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
    private Button submitEditButton;
    private boolean isNew;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance(int formNumber, ArrayList<Page> pages, String formName, boolean isEditable, boolean isNew) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PAGES, pages);
        args.putInt(FORM_NUMBER, formNumber);
        args.putBoolean(IS_EDITABLE, isEditable);
        args.putBoolean(IS_NEW, isNew);
        args.putString(FORM_NAME, formName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEditable = getArguments().getBoolean(IS_EDITABLE);
            isNew = getArguments().getBoolean(IS_NEW);
            pages = getArguments().getParcelableArrayList(PAGES);
            formNumber = getArguments().getInt(FORM_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View roolView = inflater.inflate(R.layout.fragment_form, container, false);

        // Buttons

        preButton = (Button) roolView.findViewById(R.id.backButton);
        nextButton = (Button) roolView.findViewById(R.id.nextButton);
        submitEditButton = (Button) roolView.findViewById(R.id.submitEditButton);

        Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_edit);

        if (isEditable) {
            submitEditButton.setText(getResources().getString(R.string.submit));
            submitEditButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            submitEditButton.setText(getResources().getString(R.string.edit));
            submitEditButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }

        // Set up the ViewPager with the sections adapter.
        mTabLayout = (TabLayout) roolView.findViewById(R.id.tabs);
        mViewPager = (CustomViewPager) roolView.findViewById(R.id.container);
        mTabLayout.setId(formNumber + 1000);
        mViewPager.setId(formNumber + 2000);
        setupViewPager(pages);

        if (mTabLayout.getTabCount() > 5){
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
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

        submitEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditable)
                mListener.submitClick(formNumber, isNew);
                else mListener.editClick(formNumber, isNew);
            }
        });

        return roolView;
    }

    @SuppressWarnings("CheckResult")
    private void setupViewPager(final ArrayList<Page> pages) {

        // Set View Pager Paging Disable.
        mViewPager.setPagingEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(formNumber, isEditable, getChildFragmentManager(), pages);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        for (int position = 0; position < pages.size(); position++) {
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

        handleButtons(0);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab.getCustomView(), true);
                mViewPager.setCurrentItem(tab.getPosition(), true);
                handleButtons(tab.getPosition());
                // hide keyboard
                CommonMethods.hideKeyboard(getContext());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Page page = pages.get(tab.getPosition());

                for (int sectionIndex = 0; sectionIndex < page.getSection().size(); sectionIndex++) {
                    Section section = page.getSection().get(sectionIndex);

                    for (int fieldsIndex = 0; fieldsIndex < section.getFields().size(); fieldsIndex++) {
                        Field field = section.getFields().get(fieldsIndex);
                        fieldValidation(field);
                    }

                }

                selectTab(tab.getCustomView(), false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void handleButtons(int position) {
        if (position == 0)
            preButton.setEnabled(false);
        else preButton.setEnabled(true);

        if ((position + 1) == mTabLayout.getTabCount())
            nextButton.setEnabled(false);
        else nextButton.setEnabled(true);
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
        private final ArrayList<Page> pages;
        private boolean isEditable = true;
        private int formNumber;

        public SectionsPagerAdapter(int formNumber, boolean isEditable, FragmentManager fm, ArrayList<Page> pages) {
            super(fm);
            this.pages = pages;
            this.formNumber = formNumber;
            this.isEditable = isEditable;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (isEditable)
                return PageFragment.newInstance(formNumber, position, pages.get(position), isEditable);
            else return ProfilePageFragment.newInstance(formNumber, position, pages.get(position));
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

    private void fieldValidation(Field field) {
        switch (field.getType()) {

            case PageFragment.TYPE.TEXTBOXGROUP: {

                ArrayList<Field> moreFields = field.getTextBoxGroup();

                for (int moreFieldIndex = 0; moreFieldIndex < moreFields.size(); moreFieldIndex++) {
                    Field moreField = moreFields.get(moreFieldIndex);

                }

                break;
            }

            case PageFragment.TYPE.TEXTBOX: {

                switch (field.getInputType()) {
                    case PageFragment.INPUT_TYPE.EMAIL:

                        break;
                    case PageFragment.INPUT_TYPE.DATE:


                        break;
                    case PageFragment.INPUT_TYPE.MOBILE:

                        break;
                    case PageFragment.INPUT_TYPE.NAME:

                        break;
                    case PageFragment.INPUT_TYPE.PIN_CODE:

                        break;
                    case PageFragment.INPUT_TYPE.TEXTBOX_BIG:

                        break;
                    case PageFragment.INPUT_TYPE.NUMBER:

                        break;
                }

                break;
            }

            case PageFragment.TYPE.RADIOBUTTON: {

                break;
            }
            case PageFragment.TYPE.CHECKBOX: {

                break;
            }
            case PageFragment.TYPE.DROPDOWN: {

                break;
            }
        }

    }

}
