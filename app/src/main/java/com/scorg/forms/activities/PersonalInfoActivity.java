package com.scorg.forms.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scorg.forms.R;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.fragments.NewRegistrationFragment;
import com.scorg.forms.fragments.UndertakingFragment;
import com.scorg.forms.models.Form;
import com.scorg.forms.models.FormsModel;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

import java.io.IOException;
import java.io.InputStream;

import static com.scorg.forms.activities.FormsActivity.FORM;
import static com.scorg.forms.activities.FormsActivity.FORM_INDEX;

public class PersonalInfoActivity extends AppCompatActivity implements FormFragment.ButtonClickListener, NewRegistrationFragment.OnRegistrationListener {

    private FormsModel formsModel;
    private RelativeLayout bottomTabLayout;

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        ImageView getInfoButton = (ImageView) findViewById(R.id.getInfoButton);
        final EditText mobileText = (EditText) findViewById(R.id.mobileText);

        bottomTabLayout = (RelativeLayout) findViewById(R.id.bottomTabLayout);

//        CommonMethods.setEditTextLineColor(mobileText, getResources().getColor(android.R.color.white));

        Gson gson = new Gson();
        formsModel = gson.fromJson(loadJSONFromAsset(), FormsModel.class);

        addRegisterFragment();

        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Valid.validateMobileNo(mobileText.getText().toString(), PersonalInfoActivity.this)) {
                    addProfileFragment(false);
                    bottomTabLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        mobileText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    addProfileFragment(false);
                    bottomTabLayout.setVisibility(View.VISIBLE);

                }
                return false;
            }
        });

        // Form Tab

        TabLayout formTabLayout = findViewById(R.id.formTabLayout);

        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(iconSize, iconSize);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.error(R.drawable.ic_assignment);
        requestOptions.placeholder(R.drawable.ic_assignment);

        for (int formIndex = 0; formIndex < formsModel.getForms().size(); formIndex++) {

            Form form = formsModel.getForms().get(formIndex);

            View tabView = getLayoutInflater().inflate(R.layout.custom_tab_form, null);

            ImageView formIcon = tabView.findViewById(R.id.formIcon);
            TextView titleTextView = tabView.findViewById(R.id.titleTextView);

            Glide.with(PersonalInfoActivity.this)
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
                openForm(tab, formsModel.getForms().get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                openForm(tab, formsModel.getForms().get(tab.getPosition()));
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void addRegisterFragment() {
        NewRegistrationFragment newRegistrationFragment = NewRegistrationFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newRegistrationFragment, getResources().getString(R.string.new_registration));
        transaction.commit();

        CommonMethods.hideKeyboard(PersonalInfoActivity.this);
    }

    private void addProfileFragment(boolean isEditable) {
        FormFragment formFragment = FormFragment.newInstance(20, formsModel.getPersonalInfo().getPages(), getResources().getString(R.string.personal_info), isEditable);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, formFragment, getResources().getString(R.string.personal_info) + 20);
        transaction.commit();

        CommonMethods.hideKeyboard(PersonalInfoActivity.this);
    }

    private void openForm(TabLayout.Tab tab, Form form) {
        Intent intent = new Intent(PersonalInfoActivity.this, FormsActivity.class);
        intent.putExtra(FORM, form);
        intent.putExtra(FORM_INDEX, tab.getPosition());
        startActivity(intent);
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("registration_form_actual.json");
//            InputStream is = getAssets().open("registration_form.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    public void backClick(int formNumber) {

    }

    @Override
    public void nextClick(int formNumber) {

    }

    @Override
    public void submitClick(int formNumber) {
        addProfileFragment(false);
        bottomTabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void editClick(int formNumber) {
        bottomTabLayout.setVisibility(View.GONE);
        addProfileFragment(true);
    }

    @Override
    public void onClickRegister() {
        addProfileFragment(true);
    }
}
