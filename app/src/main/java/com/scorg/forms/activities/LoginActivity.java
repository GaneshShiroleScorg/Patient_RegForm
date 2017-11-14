package com.scorg.forms.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.scorg.forms.R;
import com.scorg.forms.customui.CustomButton;
import com.scorg.forms.customui.CustomEditText;
import com.scorg.forms.helpers.LoginHelper;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.login.DocDetail;
import com.scorg.forms.models.login.LoginModel;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Constants;
import com.scorg.forms.util.Valid;

import static com.scorg.forms.util.Constants.SUCCESS;

public class LoginActivity extends AppCompatActivity implements HelperResponse {

    // Content View Elements

    private CustomEditText mEmailEditText;
    private CustomEditText mPasswordEditText;
    private CustomButton mLoginButton;
    private Context mContext;

    // End Of Content View Elements

    private void bindViews() {
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mLoginButton = findViewById(R.id.loginButton);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mContext = LoginActivity.this;
        
        bindViews();

        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO)
                    go();
                return false;
            }

        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });
    }

    private void go() {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (!validate(email, password)) {
            LoginHelper loginHelper = new LoginHelper(mContext);
            loginHelper.doLogin(email, password);
        }
    }

    // validation for mobileNo on click of otp Button
    private boolean validate(String email, String password) {
        String message = null;
        if (!Valid.validateEmail(email, mContext, false)) {
            message = getString(R.string.err_email_invalid);
            mEmailEditText.requestFocus();
        } else if (password.isEmpty()) {
            message = getString(R.string.enter_password);
            mPasswordEditText.requestFocus();
        }

        if (message != null)
            CommonMethods.showToast(mContext, message);

        return message != null;
    }

    @Override
    public void onSuccess(String mOldDataTag, CustomResponse customResponse) {
        if (mOldDataTag.equalsIgnoreCase(Constants.TASK_LOGIN)) {
            //After login user navigated to HomepageActivity
            LoginModel receivedModel = (LoginModel) customResponse;
            if (receivedModel.getCommon().getStatusCode().equals(SUCCESS)) {

                DocDetail docDetail = receivedModel.getDoctorLoginData().getDocDetail();
                String authToken = receivedModel.getDoctorLoginData().getAuthToken();

                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.AUTHTOKEN, authToken, mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.DOC_ID, String.valueOf(docDetail.getDocId()), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.USER_NAME, docDetail.getDocName(), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.PROFILE_PHOTO, docDetail.getDocImgUrl(), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.EMAIL, docDetail.getDocEmail(), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.SPECIALITY, docDetail.getDocSpaciality(), mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.ADDRESS, docDetail.getDocAddress(), mContext);

                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, Constants.YES, mContext);
                AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.PASSWORD, mEmailEditText.getText().toString(), mContext);

                Intent intentObj = new Intent(mContext, PersonalInfoActivity.class);
                startActivity(intentObj);
                finish();

            } else {
                CommonMethods.showToast(mContext, receivedModel.getCommon().getStatusMessage());
            }
        }
    }

    @Override
    public void onParseError(String mOldDataTag, String errorMessage) {
        CommonMethods.showToast(mContext, errorMessage);
    }

    @Override
    public void onServerError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
    }

    @Override
    public void onNoConnectionError(String mOldDataTag, String serverErrorMessage) {
        CommonMethods.showToast(mContext, serverErrorMessage);
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
}
