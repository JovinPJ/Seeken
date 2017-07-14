package com.android.seeken.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * for saving and retrieving from SharedPreference
 * Created by Jovin on 28-06-2017.
 */

public class PreferenceHelper {

    private static final String PHONE_PREFERENCES = "playerPreferences";
    private static final String PREFERENCE_EMAIL_SENT = PHONE_PREFERENCES + ".email";
    private static final String PREFERENCE_MSG_SENT = PHONE_PREFERENCES + ".message";
    private static final String PREFERENCE_ACTIVATED_TIME = PHONE_PREFERENCES + ".activated_time";

    private PreferenceHelper() {
        //no instance
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PHONE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void setEmailSent(Context context, boolean isSent) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(PREFERENCE_EMAIL_SENT, isSent);
        editor.apply();
    }

    public static boolean isEmailSent(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREFERENCE_EMAIL_SENT, false);
    }


    public static void setMessageSent(Context context, boolean isSent) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(PREFERENCE_MSG_SENT, isSent);
        editor.apply();
    }

    public static boolean isMessageSent(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getBoolean(PREFERENCE_MSG_SENT, false);
    }


    public static void setActivatedTime(Context context, String activatedTime) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(PREFERENCE_ACTIVATED_TIME, activatedTime);
        editor.apply();
    }

    public static String getActivatedTime(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(PREFERENCE_ACTIVATED_TIME, "");
    }

    public static boolean isDataSent(Context context) {
        return isEmailSent(context) && isMessageSent(context);
    }


}
