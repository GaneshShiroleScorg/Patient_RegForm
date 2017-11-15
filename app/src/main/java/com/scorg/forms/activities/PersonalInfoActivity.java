package com.scorg.forms.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
import com.scorg.forms.database.AppDBHelper;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.fragments.NewRegistrationFragment;
import com.scorg.forms.fragments.ProfilePageFragment;
import com.scorg.forms.helpers.LoginHelper;
import com.scorg.forms.interfaces.CheckIpConnection;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.form.Form;
import com.scorg.forms.models.form.FormsModel;
import com.scorg.forms.models.login.IpTestResponseModel;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.singleton.Device;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.scorg.forms.activities.FormsActivity.CLINIC_LOGO;
import static com.scorg.forms.activities.FormsActivity.CLINIC_NAME;
import static com.scorg.forms.activities.FormsActivity.FORM;
import static com.scorg.forms.activities.FormsActivity.FORM_INDEX;
import static com.scorg.forms.fragments.ProfilePageFragment.PERSONAL_INFO_FORM;
import static com.scorg.forms.util.Constants.PHONE;

public class PersonalInfoActivity extends AppCompatActivity implements FormFragment.ButtonClickListener, NewRegistrationFragment.OnRegistrationListener, ProfilePageFragment.ButtonClickListener, HelperResponse {

    private FormsModel formsModel;
    private FormsModel newFormsModel;
    private RelativeLayout bottomTabLayout;

    // show
    private CustomProgressDialog customProgressDialog;
    private Timer timer = new Timer();
    private TabLayout formTabLayout;
    private Context mContext;
    private LoginHelper mLoginHelper;

    private String clinicLogo = "";
    private String clinicName = "";
    private Menu menu;

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = PersonalInfoActivity.this;
        mLoginHelper = new LoginHelper(mContext);

        if (Device.getInstance(mContext).getDeviceType().equals(PHONE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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

        customProgressDialog = new CustomProgressDialog(mContext);
        customProgressDialog.setCancelable(false);

        bottomTabLayout = findViewById(R.id.bottomTabLayout);


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

            Glide.with(mContext)
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

    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getString(R.string.logout_message))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
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

    private void logout() {
        AppDBHelper appDBHelper = new AppDBHelper(mContext);
        appDBHelper.deleteDatabase();
        AppPreferencesManager.clearSharedPref(mContext);

        Intent intentObj = new Intent(mContext, LoginActivity.class);
        startActivity(intentObj);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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

        CommonMethods.hideKeyboard(mContext);
    }

    private void addProfileFragment(boolean isEditable, boolean isNew) {

        if (isEditable) {
            FormFragment formFragment = FormFragment.newInstance(PERSONAL_INFO_FORM, isNew ? newFormsModel.getPersonalInfo().getPages() : formsModel.getPersonalInfo().getPages(), getResources().getString(R.string.personal_info)/*, isEditable*/, isNew, "");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, formFragment, getResources().getString(R.string.personal_info) + PERSONAL_INFO_FORM);
            transaction.commit();
        } else {

            bottomTabLayout.setVisibility(GONE);
            showProgress(GONE);

            ProfilePageFragment profilePageFragment = ProfilePageFragment.newInstance(PERSONAL_INFO_FORM, isNew ? newFormsModel : formsModel, isNew);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, profilePageFragment, getResources().getString(R.string.personal_info) + PERSONAL_INFO_FORM);
            transaction.commit();
        }

        CommonMethods.hideKeyboard(mContext);
    }

    @Override
    public void openForm(TabLayout.Tab tab, Form form) {
        Intent intent = new Intent(mContext, FormsActivity.class);
        intent.putExtra(FORM, form);
        intent.putExtra(FORM_INDEX, tab.getPosition());
        intent.putExtra(CLINIC_NAME, clinicName);
        intent.putExtra(CLINIC_LOGO, clinicLogo);

        startActivity(intent);
    }

    public String loadJSONFromAsset(String jsonFile) {
        String json;
        try {
            InputStream is = getAssets().open(jsonFile);
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
        /*bottomTabLayout.setVisibility(GONE);
        showProgress(GONE);*/
        addProfileFragment(false, isNew);
        setTabLayoutDisable(false, true);

    }

    private void setTabLayoutDisable(boolean isDisable, boolean showFormTabLayout) {

        if (showFormTabLayout) {
            formTabLayout.setVisibility(VISIBLE);
        } else {
            formTabLayout.setVisibility(View.GONE);
        }

        LinearLayout tabStrip = ((LinearLayout) formTabLayout.getChildAt(0));
        tabStrip.setAlpha(isDisable ? .3f : 1f);
        tabStrip.setEnabled(isDisable);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(!isDisable);
        }
    }

    @Override
    public void editClick(int formNumber, boolean isNew) {
        showProgress(VISIBLE);
        setTabLayoutDisable(true, false);
        addProfileFragment(true, isNew);
    }

    @Override
    public void onClickRegister() {

        //----Set logo and clinic Name in home window---
        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);

        TextView headerName = findViewById(R.id.titleTextView);
        headerName.setText(clinicName);
        ImageView headerLogo = findViewById(R.id.logo);
        Glide.with(mContext)
                .load(clinicLogo)
                .into(headerLogo);
        //-------

        showProgress(VISIBLE);
        addProfileFragment(true, true);
        setTabLayoutDisable(true, false);
    }

    @Override
    public void onClickGetInfo(String mobileText) {

        //----Set logo and clinic Name in home window---
        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);

        TextView headerName = findViewById(R.id.titleTextView);
        headerName.setText(clinicName);
        ImageView headerLogo = findViewById(R.id.logo);
        Glide.with(mContext)
                .load(clinicLogo)
                .into(headerLogo);
        //-------

        addProfileFragment(false, false);
        setTabLayoutDisable(false, true);

    }

    private void showProgress(final int visible) {
        customProgressDialog.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // dismiss
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomTabLayout.setVisibility(visible);
                    }
                });
                customProgressDialog.dismiss();
            }
        }, 300);
    }

    // Menu

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_menu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.change_ip:
                CommonMethods.showAlertDialog(mContext, getString(R.string.server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                    @Override
                    public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                        AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                        mLoginHelper.checkConnectionToServer(serverPath);
                        dialog.dismiss();
                    }
                }, false);
                return true;
            case R.id.logout:
                logoutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        IpTestResponseModel ipTestResponseModel = (IpTestResponseModel) customResponse;
        if (ipTestResponseModel.getCommon().getStatusCode().equals(Constants.SUCCESS)) {
            AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.IS_VALID_IP_CONFIG, Constants.TRUE, mContext);
            Intent intentObj = new Intent(mContext, LoginActivity.class);
            startActivity(intentObj);
            finish();
        } else {
            AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
            CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
                @Override
                public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                    AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                    mLoginHelper.checkConnectionToServer(serverPath);
                }
            }, false);
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, false);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, false);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        AppPreferencesManager.putString(Constants.LOGIN_SUCCESS, Constants.FALSE, mContext);
        CommonMethods.showAlertDialog(mContext, getString(R.string.wrong_server_path) + "\n" + getString(R.string.for_example_server_path), new CheckIpConnection() {
            @Override
            public void onOkButtonClickListener(String serverPath, Context context, Dialog dialog) {
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SERVER_PATH, serverPath, context);
                mLoginHelper.checkConnectionToServer(serverPath);
            }
        }, false);
    }

    public void setMenuVisibility(boolean isVisible) {
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(isVisible);
    }
}
