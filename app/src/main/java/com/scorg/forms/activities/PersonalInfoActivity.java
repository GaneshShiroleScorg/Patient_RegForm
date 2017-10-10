package com.scorg.forms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.scorg.forms.R;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.models.FormsModel;
import com.scorg.forms.util.CommonMethods;
import com.scorg.forms.util.Valid;

import java.io.IOException;
import java.io.InputStream;

public class PersonalInfoActivity extends AppCompatActivity {

    public static final String FORMS = "pages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        ImageView getInfoButton = (ImageView) findViewById(R.id.getInfoButton);
        final EditText mobileText = (EditText) findViewById(R.id.mobileText);
        CommonMethods.setEditTextLineColor(mobileText, getResources().getColor(android.R.color.white));

        Gson gson = new Gson();
        final FormsModel formsModel = gson.fromJson(loadJSONFromAsset(), FormsModel.class);

        FormFragment formFragment = FormFragment.newInstance(20, formsModel.getPersonalInfo().getPages(), getResources().getString(R.string.personal_info));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, formFragment, getResources().getString(R.string.personal_info) + 20);
        transaction.commit();

        getInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Valid.validateMobileNo(mobileText.getText().toString(), PersonalInfoActivity.this)) {
                    Intent intent = new Intent(PersonalInfoActivity.this, FormsActivity.class);
                    intent.putExtra(FORMS, formsModel.getForms());
                    startActivity(intent);
                }
            }
        });
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("registration_form_actual.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
