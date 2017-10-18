package com.scorg.forms.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.scorg.forms.R;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.models.Form;

public class FormsActivity extends AppCompatActivity implements FormFragment.ButtonClickListener {

    private static final String TAG = "Form";

    public static final String FORM = "form";
    public static final String FORM_INDEX = "form_index";

    @SuppressWarnings("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        Form form = getIntent().getParcelableExtra(FORM);
        int formIndex = getIntent().getIntExtra(FORM_INDEX, 0);

        FormFragment formFragment = FormFragment.newInstance(formIndex, form.getPages(), form.getFormName(), true, false);
        addFormFragment(formFragment, form.getFormName(), formIndex);

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(form.getFormName());
        ImageView logo = findViewById(R.id.logo);

        int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(iconSize, iconSize);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        requestOptions.skipMemoryCache(true);
        requestOptions.error(R.drawable.ic_assignment);
        requestOptions.placeholder(R.drawable.ic_assignment);

        Glide.with(FormsActivity.this)
                .load(form.getFormIcon())
                .apply(requestOptions)
                .into(logo);

        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void addFormFragment(Fragment formFragment, String formName, int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, formFragment, formName + position);
        transaction.commit();
        Log.d(TAG, "Added " + formName + " fragment");
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void backClick(int formNumber) {

    }

    @Override
    public void nextClick(int formNumber) {

    }

    @Override
    public void submitClick(int formNumber, boolean isNew) {

    }

    @Override
    public void editClick(int formNumber, boolean isNew) {

    }
}
