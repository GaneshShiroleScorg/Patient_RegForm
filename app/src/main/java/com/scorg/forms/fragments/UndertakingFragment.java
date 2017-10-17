package com.scorg.forms.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomButton;
import com.scorg.forms.customui.CustomTextView;

public class UndertakingFragment extends Fragment {

//    private OnSubmitListener mListener;

    public UndertakingFragment() {
        // Required empty public constructor
    }

    public static UndertakingFragment newInstance() {
        UndertakingFragment fragment = new UndertakingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Content View Elements

    private ImageView mLogo;
    private CustomTextView mTitleTextView;
    private CustomTextView mDateTextView;
    private CustomTextView mContentTextView;
    private SignaturePad mSignature_pad;
    private CustomButton mClearButton;
    private Button mSubmitButton;

    // End Of Content View Elements

    private void bindViews(View view) {
        mLogo = (ImageView) view.findViewById(R.id.logo);
        mTitleTextView = (CustomTextView) view.findViewById(R.id.titleTextView);
        mDateTextView = (CustomTextView) view.findViewById(R.id.dateTextView);
        mContentTextView = (CustomTextView) view.findViewById(R.id.contentTextView);
        mSignature_pad = (SignaturePad) view.findViewById(R.id.signature_pad);
        mClearButton = (CustomButton) view.findViewById(R.id.clearButton);
        mSubmitButton = (Button) view.findViewById(R.id.submitButton);

        mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mTitleTextView.setText(getResources().getString(R.string.undertaking));

        mContentTextView.setText(Html.fromHtml(getResources().getString(R.string.content)));

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature_pad.clear();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_undertaking, container, false);
        bindViews(rootView);
        return rootView;
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitListener) {
            mListener = (OnSubmitListener) context;
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

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnSubmitListener {
        // TODO: Update argument type and name
        void onClickSubmit();
    }*/
}
