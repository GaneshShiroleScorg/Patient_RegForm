package com.scorg.forms.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.scorg.forms.R;
import com.scorg.forms.fragments.FormFragment;
import com.scorg.forms.models.Form;

import java.util.ArrayList;

import static com.scorg.forms.activities.PersonalInfoActivity.FORMS;

public class FormsActivity extends AppCompatActivity {

    private static final String TAG = "Form";
    private TabLayout mMainTabLayout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        ArrayList<Form> forms = getIntent().getParcelableArrayListExtra(FORMS);

        mMainTabLayout = (TabLayout) findViewById(R.id.tabsMain);
        setupMainTab(forms);
    }

    private void setupMainTab(final ArrayList<Form> forms) {

        for (int position = 0; position < forms.size(); position++) {
            mMainTabLayout.addTab(mMainTabLayout.newTab().setText(forms.get(position).getFormName()));
        }

        if (mMainTabLayout.getTabCount() > 5){
            mMainTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            mMainTabLayout.setTabMode(TabLayout.MODE_FIXED);
        }

        Fragment formFragment = FormFragment.newInstance(0, forms.get(0).getPages(), forms.get(0).getFormName());
        addFormFragment(formFragment, forms.get(0).getFormName(), 0);

        mMainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment formFragment = fragmentManager.findFragmentByTag((String) tab.getText() + tab.getPosition());
                if (formFragment != null) {
                    transaction.replace(R.id.containerMain, formFragment);
                    transaction.commit();
                    Log.d(TAG, "Reloaded " + forms.get(tab.getPosition()).getFormName() + " fragment");
                } else {
                    formFragment = FormFragment.newInstance(tab.getPosition(), forms.get(tab.getPosition()).getPages(), forms.get(tab.getPosition()).getFormName());
                    addFormFragment(formFragment, forms.get(tab.getPosition()).getFormName(), tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void addFormFragment(Fragment formFragment, String formName, int position) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.containerMain, formFragment, formName + position);
        transaction.addToBackStack(formName + position);
        transaction.commit();
        Log.d(TAG, "Added " + formName + " fragment");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
