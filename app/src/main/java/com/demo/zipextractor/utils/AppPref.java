package com.demo.zipextractor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class AppPref {
    static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    static final String IS_HIDDEN = "IS_HIDDEN";
    static final String IS_RATE_US = "IS_RATE_US";
    static final String IS_RATE_US_ACTION = "IS_RATE_US_ACTION";
    static final String IS_TERMS_ACCEPT = "IS_TERMS_ACCEPT";
    static final String IS_THUMBNAIL = "IS_THUMBNAIL";
    static final String LIST = "LIST";
    static final String MyPref = "userPref";
    static final String PRO_VERSION = "PRO_VERSION";
    public static final String SHOW_NEVER = "SHOW_NEVER";

    public static boolean IsFirstLaunch(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_FIRST_LAUNCH, false);
    }

    public static void setIsFirstLaunch(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_FIRST_LAUNCH, z);
        edit.commit();
    }

    public static boolean IsRateUsAction(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_RATE_US_ACTION, false);
    }

    public static void setRateUsAction(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_RATE_US_ACTION, z);
        edit.commit();
    }

    public static boolean IsRateUs(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_RATE_US, false);
    }

    public static void setRateUs(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_RATE_US, z);
        edit.commit();
    }

    public static boolean IsTermsAccept(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_TERMS_ACCEPT, false);
    }

    public static void setIsTermsAccept(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_TERMS_ACCEPT, z);
        edit.commit();
    }

    public static void setShowNever(Activity activity, boolean z) {
        SharedPreferences.Editor edit = activity.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(SHOW_NEVER, z);
        edit.apply();
    }

    public static boolean ShowNever(Activity activity) {
        return activity.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(SHOW_NEVER, false);
    }

    public static boolean IsHidden(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_HIDDEN, false);
    }

    public static void setIsHidden(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_HIDDEN, z);
        edit.commit();
    }

    public static boolean IsThumbnail(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_THUMBNAIL, true);
    }

    public static void setIsThumbnail(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_THUMBNAIL, z);
        edit.commit();
    }

    public static boolean IsProVersion() {
        return MyApp.getContext().getSharedPreferences(MyPref, 0).getBoolean(PRO_VERSION, false);
    }

    public static void setIsProVersion(boolean z) {
        SharedPreferences.Editor edit = MyApp.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(PRO_VERSION, z);
        edit.commit();
    }
}
