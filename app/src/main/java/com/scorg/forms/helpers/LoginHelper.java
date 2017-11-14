package com.scorg.forms.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.scorg.forms.interfaces.ConnectionListener;
import com.scorg.forms.interfaces.CustomResponse;
import com.scorg.forms.interfaces.HelperResponse;
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

public class LoginHelper implements ConnectionListener {
    private String TAG = this.getClass().getName();
    private Context mContext;
    private HelperResponse mHelperResponseManager;
    private String mServerPath;

    public LoginHelper(Context context) {
        this.mContext = context;
        this.mHelperResponseManager = (HelperResponse) context;
    }


    @Override
    public void onResponse(int responseResult, CustomResponse customResponse, String mOldDataTag) {

        //CommonMethods.log(TAG, customResponse.toString());
        switch (responseResult) {
            case ConnectionListener.RESPONSE_OK:
                switch (mOldDataTag) {
                    case Constants.TASK_LOGIN:
                        LoginModel loginModel = (LoginModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, loginModel);
                        break;
                    case Constants.TASK_SIGN_UP:
                        SignUpModel signUpModel = (SignUpModel) customResponse;
                        mHelperResponseManager.onSuccess(mOldDataTag, signUpModel);
                        break;
                    case Constants.TASK_VERIFY_SIGN_UP_OTP:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case Constants.TASK_LOGIN_WITH_PASSWORD:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case Constants.TASK_LOGIN_WITH_OTP:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case Constants.LOGOUT:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case Constants.ACTIVE_STATUS:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
                        break;
                    case Constants.TASK_CHECK_SERVER_CONNECTION:
                        mHelperResponseManager.onSuccess(mOldDataTag, customResponse);
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
    public void doLogin(String email, String password) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_LOGIN, Request.Method.POST, true);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        loginRequestModel.setEmailId(email);
        loginRequestModel.setPassword(password);
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_URL);
        mConnectionFactory.createConnection(Constants.TASK_LOGIN);
    }

    //Do login using Otp
    public void doLoginByOTP(String otp) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_LOGIN_WITH_OTP, Request.Method.POST, true);
        mConnectionFactory.setHeaderParams();
        LoginRequestModel loginRequestModel = new LoginRequestModel();
        //    loginRequestModel.setMobileNumber(otp);  TODO NOT CONFIRMED ABOUT THIS.
        mConnectionFactory.setPostParams(loginRequestModel);
        mConnectionFactory.setUrl(Config.LOGIN_WITH_OTP_URL);
        mConnectionFactory.createConnection(Constants.TASK_LOGIN_WITH_OTP);
    }

    //Verify Otp sent
    public void doVerifyGeneratedSignUpOTP(SignUpVerifyOTPRequestModel requestModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_VERIFY_SIGN_UP_OTP, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(requestModel);
        mConnectionFactory.setUrl(Config.VERIFY_SIGN_UP_OTP);
        mConnectionFactory.createConnection(Constants.TASK_VERIFY_SIGN_UP_OTP);
    }

    //SignUp
    public void doSignUp(SignUpRequestModel signUpRequestModel) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_SIGN_UP, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(signUpRequestModel);
        mConnectionFactory.setUrl(Config.SIGN_UP_URL);
        mConnectionFactory.createConnection(Constants.TASK_SIGN_UP);
    }

    // Logout
    public void doLogout(ActiveRequest activeRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.LOGOUT, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(activeRequest);
        mConnectionFactory.setUrl(Config.LOGOUT);
        mConnectionFactory.createConnection(Constants.LOGOUT);
    }

    // ActiveStatus
    public void doActiveStatus(ActiveRequest activeRequest) {
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.ACTIVE_STATUS, Request.Method.POST, false);
        mConnectionFactory.setHeaderParams();
        mConnectionFactory.setPostParams(activeRequest);
        mConnectionFactory.setUrl(Config.ACTIVE);
        mConnectionFactory.createConnection(Constants.ACTIVE_STATUS);
    }

    public void checkConnectionToServer(String serverPath) {
        this.mServerPath = serverPath;
        ConnectionFactory mConnectionFactory = new ConnectionFactory(mContext, this, null, true, Constants.TASK_CHECK_SERVER_CONNECTION, Request.Method.GET, false);
        mConnectionFactory.setUrl(Config.URL_CHECK_SERVER_CONNECTION);
        mConnectionFactory.createConnection(Constants.TASK_CHECK_SERVER_CONNECTION);
    }
}