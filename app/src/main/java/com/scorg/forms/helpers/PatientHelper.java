package com.scorg.forms.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.scorg.forms.interfaces.ConnectionListener;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
import com.scorg.forms.models.PatientData;
import com.scorg.forms.models.login.ActiveRequest;
import com.scorg.forms.models.login.LoginModel;
import com.scorg.forms.models.login.SignUpModel;
import com.scorg.forms.models.login.requestmodel.login.LoginRequestModel;
import com.scorg.forms.models.login.requestmodel.login.SignUpRequestModel;
import com.scorg.forms.models.login.requestmodel.login.SignUpVerifyOTPRequestModel;
import com.scorg.forms.network.ConnectRequest;
import com.scorg.forms.network.ConnectionFactory;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Config;
import com.scorg.forms.util.Constants;

import static com.scorg.forms.util.Constants.GET_REGISTRATION_FORM;

public class PatientHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;
    private String mServerPath;

    public PatientHelper(Context context) {
        this.mContext = context;
        this.mHelperResponseManager = (HelperResponse) context;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case GET_REGISTRATION_FORM:
                        PatientData patientData = (PatientData) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, patientData);
                        break;
                }
                break;
            case ConnectionListener.PARSE_ERR0R:
                CommonMethods.log(TAG, "parse error");
                mHelperResponseManager.onParseError(mOldDataTag, "parse error");
                break;
            case ConnectionListener.SERVER_ERROR:
                CommonMethods.log(TAG, "server error");
                mHelperResponseManager.onServerError(mOldDataTag, "server error");
                break;
            case ConnectionListener.NO_CONNECTION_ERROR:
                CommonMethods.log(TAG, "no connection error");
                mHelperResponseManager.onNoConnectionError(mOldDataTag, "no connection error");
                break;
            default:
                CommonMethods.log(TAG, "default error");
                break;
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {

    }

    //Do login using mobileNo and password
    public void getPatientInfo(String mobile) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, GET_REGISTRATION_FORM, Request.Method.GET, true);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setUrl(Config.GET_REGISTRATION_FORM);
        mConnectionFactory.createConnection(GET_REGISTRATION_FORM);
    }
}