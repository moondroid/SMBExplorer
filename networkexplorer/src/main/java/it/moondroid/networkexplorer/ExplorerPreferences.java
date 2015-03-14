package it.moondroid.networkexplorer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Marco on 14/03/2015.
 */
public class ExplorerPreferences {

    private static final String SMB_EXPLORER_PREFERENCES = "SMB_EXPLORER_PREFERENCES";
    private static final String IP_ADDRESSES = "IP_ADDRESSES";
    private static final String NAMES = "NAMES";

    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    public static void setIPAddresses(Context context, List<Map<String, String>> ipAddresses){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        Set<String> addressSet = new HashSet<>();
        Set<String> nameSet = new HashSet<>();
        for (Map<String, String> address : ipAddresses){
            addressSet.add(address.get(NetworkScanResultAdapter.KEY_IPADDRESS));
            nameSet.add(address.get(NetworkScanResultAdapter.KEY_NAME));
        }
        prefs.edit().putStringSet(IP_ADDRESSES, addressSet).apply();
        prefs.edit().putStringSet(NAMES, nameSet).apply();
    }

    public static List<Map<String, String>> getIPAddresses(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);

        Set<String> addressSet = prefs.getStringSet(IP_ADDRESSES, new HashSet<String>());
        Set<String> namesSet = prefs.getStringSet(NAMES, new HashSet<String>());
        String[] addressArray = addressSet.toArray(new String[addressSet.size()]);
        String[] namesArray = namesSet.toArray(new String[namesSet.size()]);

        List<Map<String, String>> ipAddresses = new ArrayList<>();

        for (int i=0; i < addressArray.length; i++){
            Map<String, String> map = new HashMap<>();
            map.put(NetworkScanResultAdapter.KEY_IPADDRESS, addressArray[i]);
            map.put(NetworkScanResultAdapter.KEY_NAME, namesArray[i]);
            ipAddresses.add(map);
        }

        return ipAddresses;
    }


    public static void setUsername(Context context, String ip, String username){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(ip + USERNAME, username).apply();
    }

    public static String getUsername(Context context, String ip){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getString(ip + USERNAME, "");
    }

    public static void setPassword(Context context, String ip, String password){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(ip + PASSWORD, password).apply();
    }

    public static String getPassword(Context context, String ip){
        SharedPreferences prefs = context.getSharedPreferences(SMB_EXPLORER_PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getString(ip + PASSWORD, "");
    }

}
