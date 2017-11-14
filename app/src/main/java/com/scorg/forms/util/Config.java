package com.scorg.forms.util;

/**
 * Created by Sandeep Bahalkar
 */
public class Config {
    public static final String HTTP = "http://";
    public static final String API = "/api/";
    public static final String TOKEN_TYPE = "Bearer";
    public static final String LOGIN_URL = "authApi/authenticate/doctorLogin";
    public static final String VERIFY_SIGN_UP_OTP = "authApi/authenticate/verifyOTP";
    public static final String SIGN_UP_URL = "authApi/authenticate/signUp";
    public static final String ACTIVE = "api/doctors/logDoctorActivity";

    public static final String URL_CHECK_SERVER_CONNECTION = "connectionCheck";

    public static boolean DEV_BUILD = true;

    //Declared all URL used in app here
    public static final String LOGIN_WITH_OTP_URL = "authApi/authenticate/otpLogin";
    public static final String GET_PATIENT_LIST = "api/patient/getChatPatientList?docId=";
    public static final String DOCTOR_LIST_FILTER_URL = "api/patient/searchDoctors";

    public static final String LOGOUT = "api/doctors/logDoctorSignOut";

}



