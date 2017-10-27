package com.scorg.forms.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.scorg.forms.R;
import com.scorg.forms.customui.CustomButton;
import com.scorg.forms.customui.CustomTextView;

import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
@SuppressWarnings("CheckResult")
public class UndertakingFragment extends Fragment {

    private static final String NAME = "name";

    private static final String FORM_RECEIVED_DATE = "FORM_RECEIVED_DATE";
    private static final String CONTENT = "CONTENT";
    private static final String IMAGE_URL = "IMAGE_URL";
    public static final int MAX_ATTACHMENT_COUNT = 1;
    private String mReceivedDate;
    private AppCompatImageView mProfilePhoto;
//    private OnSubmitListener mListener;

    public UndertakingFragment() {
        // Required empty public constructor
    }

    public static UndertakingFragment newInstance(String formReceivedDate, String content, String imageUrl, String name) {
        UndertakingFragment fragment = new UndertakingFragment();
        Bundle args = new Bundle();
        args.putString(FORM_RECEIVED_DATE, formReceivedDate);
        args.putString(CONTENT, content);
        args.putString(IMAGE_URL, imageUrl);
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Content View Elements

    //    private ImageView mLogo;
    private CustomTextView mTitleTextView;
    private CustomTextView mDateTextView;
    private CustomTextView mContentTextView;
    private SignaturePad mSignature_pad;
    private CustomButton mClearButton;
    private TextView mEditButton;
    private TextView mPatientName;

    // End Of Content View Elements

    private void bindViews(View view) {
//        mLogo = (ImageView) view.findViewById(R.id.logo);
        mProfilePhoto = view.findViewById(R.id.profilePhoto);
        mTitleTextView = view.findViewById(R.id.titleTextView);
        mDateTextView = view.findViewById(R.id.dateTextView);
        mContentTextView = view.findViewById(R.id.contentTextView);
        mSignature_pad = view.findViewById(R.id.signature_pad);
        mClearButton = view.findViewById(R.id.clearButton);
        mEditButton = view.findViewById(R.id.editButton);
        mPatientName = view.findViewById(R.id.patientName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_undertaking, container, false);
        bindViews(rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mReceivedDate = arguments.getString(FORM_RECEIVED_DATE);
            mDateTextView.setText(getString(R.string.date) + mReceivedDate);

            mContentTextView.setText(Html.fromHtml(arguments.getString(CONTENT)));

            mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mTitleTextView.setText(getString(R.string.undertaking));

            Drawable leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_photo_camera);
            mEditButton.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);

            mPatientName.setText(getString(R.string.name) + ": " + arguments.getString(NAME));

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.skipMemoryCache(true);
//            requestOptions.error(R.drawable.ic_assignment);
//            requestOptions.placeholder(R.drawable.ic_assignment);

            Glide.with(getActivity())
                    .load(arguments.getString(IMAGE_URL))
                    .apply(requestOptions)
                    .thumbnail(.1f)
                    .into(mProfilePhoto);
        }

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature_pad.clear();
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UndertakingFragmentPermissionsDispatcher.onPickPhotoWithCheck(UndertakingFragment.this);
            }
        });

        return rootView;
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        FilePickerBuilder.getInstance().setMaxCount(MAX_ATTACHMENT_COUNT)
                .setSelectedFiles(new ArrayList<String>())
                .setActivityTheme(R.style.AppTheme)
                .enableVideoPicker(false)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .pickPhoto(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UndertakingFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (!data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).isEmpty()) {
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOptions.skipMemoryCache(true);
//            requestOptions.error(R.drawable.ic_assignment);
//            requestOptions.placeholder(R.drawable.ic_assignment);

                    Glide.with(getContext())
                            .load(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0))
                            .apply(requestOptions)
                            .thumbnail(.1f)
                            .into(mProfilePhoto);
                }
            }
        }
    }
}
