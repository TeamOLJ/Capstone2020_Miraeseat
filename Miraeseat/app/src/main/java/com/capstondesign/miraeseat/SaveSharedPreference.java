package com.capstondesign.miraeseat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    //static final String PREF_USER_NAME = "username";
    static final String USER_NICK_NAME = "nickname";
    static final String IS_AUTO_LOGIN = "AutoLogin";
    static final String PROFILE_IMAGE = null;
    static final String THEME = "theme";


    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserNickName(Context ctx, String nickName) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_NICK_NAME, nickName);
        editor.apply();
    }

    public static String getUserNickName(Context ctx) {
        return getSharedPreferences(ctx).getString(USER_NICK_NAME, "");
    }

    public static void setIsAutoLogin(Context ctx, boolean isAutoLogin) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(IS_AUTO_LOGIN, isAutoLogin);
        editor.apply();
    }

    public static boolean getIsAutoLogin(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(IS_AUTO_LOGIN, false);
    }

    public static void setProfileImage(Context ctx, String profileImage) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PROFILE_IMAGE, profileImage);
        editor.apply();
    }

    public static String getProfileImage(Context ctx) {
        return getSharedPreferences(ctx).getString(PROFILE_IMAGE, null);
    }

    public static void setTheme(Context ctx, String theme){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(THEME,theme);
        editor.apply();
    }


    public static String getTheme(Context ctx) {
        return getSharedPreferences(ctx).getString(THEME,null);
    }
}