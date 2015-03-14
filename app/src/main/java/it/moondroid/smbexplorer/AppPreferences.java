package it.moondroid.smbexplorer;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Marco on 14/03/2015.
 */
public class AppPreferences {

    private static final String SMB_EXPLORER_PREFERENCES = "SMB_EXPLORER_PREFERENCES";
    private static final String LOCAL_IP = "LOCAL_IP";

    public static String getLocalIp(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getString(LOCAL_IP, "192.168.2.6");
    }

    public static void setLocalIp(Context context, String localIp){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(LOCAL_IP, localIp).apply();
    }
}
