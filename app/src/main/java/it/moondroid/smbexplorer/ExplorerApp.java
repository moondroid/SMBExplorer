package it.moondroid.smbexplorer;

import it.moondroid.networkexplorer.ExplorerPreferences;
import it.moondroid.smbexplorer.models.GenericFile;
import jcifs.smb.NtlmPasswordAuthentication;

import android.app.Application;
import android.content.Context;

public class ExplorerApp extends Application {

    private static Context mContext;

	public static final int THEME_WHITE = R.style.Theme_FileExplorer_Light;
	public static final int THEME_DARK = R.style.Theme_FileExplorer_Dark;

	public static final String EXTRA_FOLDER = "it.moondroid.smbexplorer.extra.FOLDER";

	public static GenericFile.Type FILESYSTEM_TYPE = GenericFile.Type.SAMBA;



    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static String getLocalIp(){
        return AppPreferences.getLocalIp(mContext);
    }

    public static NtlmPasswordAuthentication getAuthentication(){

        String username = ExplorerPreferences.getUsername(mContext, getLocalIp());
        String password = ExplorerPreferences.getPassword(mContext, getLocalIp());
        String domain = "nas-ts412";

        return new NtlmPasswordAuthentication(domain, username, password);
    }
}
