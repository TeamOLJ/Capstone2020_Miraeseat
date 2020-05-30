package com.capstondesign.miraeseat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    //static final String PREF_USER_NAME = "username";
    static final String IS_AUTO_LOGIN = "AutoLogin";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setIsAutoLogin(Context ctx, boolean isAutoLogin) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(IS_AUTO_LOGIN, isAutoLogin);
        editor.apply();
    }

    public static boolean getIsAutoLogin(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(IS_AUTO_LOGIN, false);
    }

//    public static void setUserName(Context ctx, String userName) {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.putString(PREF_USER_NAME, userName);
//        editor.apply();
//    }
//
//    public static String getUserName(Context ctx) {
//        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
//    }
//
//    public static void clearUserName(Context ctx) {
//        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
//        editor.clear();
//        editor.apply();
//    }
}