package it.moondroid.smbexplorer.filemanagement;

import java.net.MalformedURLException;
import java.util.LinkedList;

import android.util.Log;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SambaFileSystem {

	// full SMB address is like
	// "smb://user:pass@nas-ts412/multimedia/film 2011/"
	// Important: all SMB URLs that represent workgroups, servers, shares, or
	// directories require a trailing slash '/'.
	private static String MOVIES_DIR = "multimedia/film 2011";
	private static String DOMAIN = "nas-ts412";
	private static String USERNAME = "";
	private static String PASSWORD = "";
	private static String IP_ADDR = "192.168.2.130";

	private static String TAG = "SambaFileSystem";
	
	private SmbFile moviesPath = null;

	// constructor
	public SambaFileSystem(String moviesDir) {

		jcifs.Config.setProperty("jcifs.netbios.wins", IP_ADDR);
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
				DOMAIN, USERNAME, PASSWORD);

		try {
			moviesPath = new SmbFile("smb://" + USERNAME + ":" + PASSWORD + "@"
					+ DOMAIN + "/" + moviesDir + "/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// SmbFileInputStream in;
		// in = new
		// SmbFileInputStream("smb://user:pass@nas-ts412/multimedia/film 2011");
		//
		// byte[] b = new byte[8192];
		// int n;
		// while(( n = in.read( b )) > 0 ) {
		// System.out.write( b, 0, n );
		// }
		//
	}

	public static NtlmPasswordAuthentication getAuthentication(){
        return new NtlmPasswordAuthentication(DOMAIN, USERNAME, PASSWORD);
    }

	public static LinkedList<String> listFiles(String ipAddress){
		
		
		LinkedList<String> fList = new LinkedList<String>();
        SmbFile f;
        SmbFile[] fArr = new SmbFile[0];
		try {
			
			f = new SmbFile("smb://" + ipAddress + "/");
			fArr = f.listFiles();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		}
        
 
        for(int a = 0; a < fArr.length; a++)
        {
            fList.add(fArr[a].getName());
            Log.d(TAG, fArr[a].getName());
        }
 
        return fList;
		
	}
	
	public static SmbFile[] listServers() {
		
		SmbFile[] domains = null;
		SmbFile[] servers = null;
		
		try {
			domains = (new SmbFile("smb://")).listFiles();
			for (int i = 0; i < domains.length; i++) {
				
				Log.d(TAG, "domain: "+domains[i].toString());
				
				servers = domains[i].listFiles();
				
				for (int j = 0; j < servers.length; j++) {
					
					Log.d(TAG, "server: "+servers[j].toString());
				}
				
			}
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return servers;
	}

	
	
	public boolean isMoviesDirAvailable() {

		return (moviesPath != null) ? true : false;

	}

}
