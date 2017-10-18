package com.scorg.forms.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomProgressDialog;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.fragments.NewRegistrationFragment;
import com.scorg.forms.fragments.UndertakingFragment;
import com.scorg.forms.models.Form;
import com.scorg.forms.models.FormsModel;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.scorg.forms.activities.FormsActivity.FORM;
import static com.scorg.forms.activities.FormsActivity.FORM_INDEX;

public class PersonalInfoActivity extends AppCompatActivity implements FormFragment.ButtonClickListener, NewRegistrationFragment.OnRegistrationListener {

    private FormsModel formsModel;
    private FormsModel newFormsModel;
    private RelativeLayout bottomTabLayout;

    // show

    private CustomProgressDialog customProgressDialog;
    private Timer timer = new Timer();
    private TabLayout formTabLayout;

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        if(!getResources().getBoolean(R.bool.isTab)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.tablet_message))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        customProgressDialog = new CustomProgressDialog(PersonalInfoActivity.this);
        customProgressDialog.setCancelable(false);

        bottomTabLayout = (RelativeLayout) findViewById(R.id.bottomTabLayout);

//        CommonMethods.setEditTextLineColor(mobileText, getResources().getColor(android.R.color.white));

        Gson gson = new Gson();
        formsModel = gson.fromJson(loadJSONFromAsset("registration_form_actual.json"), FormsModel.class);
        newFormsModel = gson.fromJson(loadJSONFromAsset("new_registration_form_actual.json"), FormsModel.class);

        addRegisterFragment();
//        addUndertakingFragment();

        // Form Tab

        formTabLayout = findViewById(R.id.formTabLayout);

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

    private void addUndertakingFragment() {

        UndertakingFragment undertakingFragment = UndertakingFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, undertakingFragment, getResources().getString(R.string.new_registration));
        transaction.commit();

        CommonMethods.hideKeyboard(PersonalInfoActivity.this);
    }

    private void addProfileFragment(boolean isEditable, boolean isNew) {

        FormFragment formFragment = FormFragment.newInstance(20, isNew ? newFormsModel.getPersonalInfo().getPages() : formsModel.getPersonalInfo().getPages(), getResources().getString(R.string.personal_info), isEditable, isNew);
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

    public String loadJSONFromAsset(String jsonFile) {
        String json;
        try {
            InputStream is = getAssets().open(jsonFile);
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
    public void submitClick(int formNumber, boolean isNew) {
        showProgress();
        addProfileFragment(false, isNew);
        setTabLayoutDisable(false);
    }

    private void setTabLayoutDisable(boolean isDisable) {

        LinearLayout tabStrip = ((LinearLayout) formTabLayout.getChildAt(0));
        tabStrip.setAlpha(isDisable ? .3f : 1f);
        tabStrip.setEnabled(isDisable);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(!isDisable);
        }
    }

    @Override
    public void editClick(int formNumber, boolean isNew) {
        showProgress();
        setTabLayoutDisable(true);
        addProfileFragment(true, isNew);
    }

    @Override
    public void onClickRegister() {
        showProgress();
        addProfileFragment(true, true);
        setTabLayoutDisable(true);
    }

    @Override
    public void onClickGetInfo(String mobileText) {
            showProgress();
            addProfileFragment(false, false);
            setTabLayoutDisable(false);
    }

    private void showProgress() {
        customProgressDialog.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // dismiss
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomTabLayout.setVisibility(View.VISIBLE);
                    }
                });
                customProgressDialog.dismiss();
            }
        }, 300);
    }
}
