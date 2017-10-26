package com.scorg.forms.singleton;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by ganeshshirole on 26/10/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
