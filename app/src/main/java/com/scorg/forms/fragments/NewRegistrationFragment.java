package com.scorg.forms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scorg.forms.R;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

public class NewRegistrationFragment extends Fragment {

    private OnRegistrationListener mListener;

    // hard coded

    private String registeredMobile = "8208127880";
    private EditText mobileText;
    private TextView mobileTextLabelView;
    private ImageView getInfoButton;
    private Button newRegistration;

    public NewRegistrationFragment() {
        // Required empty public constructor
    }


    public static NewRegistrationFragment newInstance() {
        NewRegistrationFragment fragment = new NewRegistrationFragment();
        return fragment;
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
        getInfoButton = (ImageView) rootView.findViewById(R.id.getInfoButton);
        newRegistration = (Button) rootView.findViewById(R.id.newRegistrationButton);

        mobileTextLabelView = (TextView) rootView.findViewById(R.id.mobileTextLabelView);

        newRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickRegister();
            }
        });

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

        return rootView;
    }

    private void go() {

        String mobile = mobileText.getText().toString().trim();

        if (Valid.validateMobileNo(mobile, getContext())) {
            if (mobile.equals(registeredMobile))
                mListener.onClickGetInfo(mobile);
            else {

                CommonMethods.hideKeyboard(getActivity());

                mobileTextLabelView.setText(mobile);
                mobileTextLabelView.setVisibility(View.VISIBLE);
                getInfoButton.setVisibility(View.INVISIBLE);
                mobileText.setVisibility(View.INVISIBLE);
                newRegistration.setVisibility(View.VISIBLE);

                newRegistration.requestFocus();
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
