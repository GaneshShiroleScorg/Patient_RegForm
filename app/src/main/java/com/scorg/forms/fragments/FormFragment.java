package com.scorg.forms.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scorg.forms.R;
import com.scorg.forms.customui.CustomViewPager;
import com.scorg.forms.models.Page;
import com.scorg.forms.util.CommonMethods;

import java.util.ArrayList;

public class FormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String FORM_NUMBER = "form_number";
    private static final String TAG = "Form";
    private static final String PAGES = "pages";
    private static final String FORM_NAME = "form_name";

    ArrayList<Page> pages;
    private String formName;
    private int formNumber;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private CustomViewPager mViewPager;
    private TabLayout mTabLayout;

    ButtonClickListener mListener;

    public FormFragment() {
        // Required empty public constructor
    }

    public static FormFragment newInstance(int formNumber, ArrayList<Page> pages, String formName) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PAGES, pages);
        args.putInt(FORM_NUMBER, formNumber);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View roolView = inflater.inflate(R.layout.fragment_form, container, false);

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

        // Buttons

        Button backButton = roolView.findViewById(R.id.backButton);
        Button nextButton = roolView.findViewById(R.id.nextButton);
        Button submitButton = roolView.findViewById(R.id.submitButton);

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

        backButton.setOnClickListener(new View.OnClickListener() {
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
                mListener.submitClick(formNumber);
            }
        });

        return roolView;
    }

    private void setupViewPager(ArrayList<Page> pages) {

        // Set View Pager Paging Disable.
        mViewPager.setPagingEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(formNumber, getChildFragmentManager(), pages);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int position = 0; position < pages.size(); position++) {
            View tabView = getActivity().getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView indicatorText = tabView.findViewById(R.id.indicatorText);
            TextView leftView = tabView.findViewById(R.id.leftView);
            TextView rightView = tabView.findViewById(R.id.rightView);

            indicatorText.setText(String.valueOf(position + 1));
            if (position == 0) {
                selectTab(indicatorText, true);
                leftView.setVisibility(View.INVISIBLE);
            } else {
                leftView.setVisibility(View.VISIBLE);
                selectTab(indicatorText, false);
            }
            if (pages.size() == (position + 1))
                rightView.setVisibility(View.INVISIBLE);
            else rightView.setVisibility(View.VISIBLE);

            mTabLayout.getTabAt(position).setCustomView(tabView);
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView indicatorText = tab.getCustomView().findViewById(R.id.indicatorText);
                selectTab(indicatorText, true);

                // hide keyboard
                CommonMethods.hideKeyboard(getContext());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView indicatorText = tab.getCustomView().findViewById(R.id.indicatorText);
                selectTab(indicatorText, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void selectTab(TextView indicatorText, boolean isSelected) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) indicatorText.getLayoutParams();
        if (isSelected) {
            params.height = getResources().getDimensionPixelSize(R.dimen.badge_selected_size);
            params.width = getResources().getDimensionPixelSize(R.dimen.badge_selected_size);
            indicatorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.badge_selected_text_size));
        } else {
            params.height = getResources().getDimensionPixelSize(R.dimen.badge_normal_size);
            params.width = getResources().getDimensionPixelSize(R.dimen.badge_normal_size);
            indicatorText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.badge_normal_text_size));
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
        private int formNumber;

        public SectionsPagerAdapter(int formNumber, FragmentManager fm, ArrayList<Page> pages) {
            super(fm);
            this.pages = pages;
            this.formNumber = formNumber;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PageFragment.newInstance(formNumber, position, pages.get(position));
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

        void submitClick(int formNumber);
    }

}
