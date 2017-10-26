package com.scorg.forms.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ganeshshirole on 9/10/17.
 */

public class CommonMethods {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static boolean alreadyRegisteredUser = false;

    public static int getAge(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        int y, m, d, noofyears;

        y = cal.get(Calendar.YEAR);// current year ,
        m = cal.get(Calendar.MONTH);// current month
        d = cal.get(Calendar.DAY_OF_MONTH);//current day
        cal.set(year, month, day);// here ur date
        noofyears = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --noofyears;
        }

        return noofyears;
    }

    public static void hideKeyboard(Context cntx) {
        // Check if no view has focus:
        View view = ((Activity) cntx).getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) cntx.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setEditTextLineColor(EditText yourEditText, int color) {

        Drawable drawable = yourEditText.getBackground(); // get current EditText drawable
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // change the drawable color

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            yourEditText.setBackground(drawable); // set the new drawable to EditText
        } else {
            yourEditText.setBackgroundDrawable(drawable); // use setBackgroundDrawable because setBackground required API 16
        }

        // Set Cursor Color

        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(yourEditText);
            Field fEditor = TextView.class.getDeclaredField("mEditor");
            fEditor.setAccessible(true);
            Object editor = fEditor.get(yourEditText);
            Class<?> clazz = editor.getClass();
            Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
            fCursorDrawable.setAccessible(true);
            Drawable[] drawables = new Drawable[2];
            drawables[0] = yourEditText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[1] = yourEditText.getContext().getResources().getDrawable(mCursorDrawableRes);
            drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
            fCursorDrawable.set(editor, drawables);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Generate a value suitable for use in {#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                Log.d("GENERATED_ID:", " " + result);
                return result;
            }
        }
    }

    public static void setAlreadyRegisteredUser(boolean alreadyRegisteredUser) {
        CommonMethods.alreadyRegisteredUser = alreadyRegisteredUser;
    }

    public static boolean isAlreadyRegisteredUser() {
        return CommonMethods.alreadyRegisteredUser;
    }
}
