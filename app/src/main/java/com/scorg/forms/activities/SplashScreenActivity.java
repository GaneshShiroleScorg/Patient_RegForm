package com.scorg.forms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.scorg.forms.R;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.Constants;

public class SplashScreenActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = SplashScreenActivity.this;

        // Hard Coded
       /* RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.AUTHTOKEN, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGVOdW1iZXIiOjc3Mzg0NzczMDYsInBhc3N3b3JkIjoidGVzdDEyMzQiLCJpYXQiOjE1MDY0OTgxNjgsImV4cCI6MTUwNjU4NDU2OH0.-RDHe-fXjjDW6PQQfglnlxY2k03siwsyaUOIwGj_TjI", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.DOC_ID, "10", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.USER_NAME, "Dr. Shalu Gupta", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.PROFILE_PHOTO, "", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.EMAIL, "shalu.gupta@scorgtechnologies.com", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.SPECIALITY, "General Physician", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.ADDRESS, "Pune", mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, RescribeConstants.YES, mContext);
        RescribePreferencesManager.putString(RescribePreferencesManager.PREFERENCES_KEY.PASSWORD, "1234", mContext);*/
        // End Hard Coded

        doNext();

    }

    private void doNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferencesManager.getString(AppPreferencesManager.PREFERENCES_KEY.LOGIN_STATUS, mContext).equals(Constants.YES)) {
                    Intent intentObj = new Intent(SplashScreenActivity.this, PersonalInfoActivity.class);
                    startActivity(intentObj);
                } else {
                    Intent intentObj = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intentObj);
                }
                finish();
            }
        }, Constants.TIME_STAMPS.THREE_SECONDS);
    }
}
