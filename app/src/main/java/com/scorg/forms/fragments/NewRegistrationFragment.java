package com.scorg.forms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scorg.forms.R;
import com.scorg.forms.preference.AppPreferencesManager;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

public class NewRegistrationFragment extends Fragment {

    private OnRegistrationListener mListener;

    // hard coded

    private String registeredMobile = "8208127880";
    private EditText mobileText;
    private Button getInfoButton;
    private boolean isNewRegistrationVisible = false;

    public NewRegistrationFragment() {
        // Required empty public constructor
    }


    public static NewRegistrationFragment newInstance() {
        return new NewRegistrationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_registration, container, false);

        mobileText = (EditText) rootView.findViewById(R.id.mobileText);
        getInfoButton = (Button) rootView.findViewById(R.id.getInfoButton);

        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        mobileText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_GO)
                    go();

                return false;
            }

        });

        mobileText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return rootView;
    }


    private void go() {

        String mobile = mobileText.getText().toString().trim();

        if (Valid.validateMobileNo(mobile, getContext(), true)) {

            AppPreferencesManager.putString(AppPreferencesManager.PREFERENCES_KEY.MOBILE, mobile, getContext());

            if (mobile.equals(registeredMobile)) {
                CommonMethods.setAlreadyRegisteredUser(true);
                mListener.onClickGetInfo(mobile);
            } else {
                CommonMethods.hideKeyboard(getActivity());

                CommonMethods.setAlreadyRegisteredUser(false); // this means user is newly registered.

                // Register directly if new mobile number, No need to show registration button
                mListener.onClickRegister();

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegistrationListener) {
            mListener = (OnRegistrationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegistrationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegistrationListener {
        // TODO: Update argument type and name
        void onClickRegister();
        void onClickGetInfo(String mobileText);
    }
}
