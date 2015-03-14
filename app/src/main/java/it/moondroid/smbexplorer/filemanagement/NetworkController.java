package it.moondroid.smbexplorer.filemanagement;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.util.Log;

public class NetworkController {

	public static ArrayList<String> checkHosts(String subnet) {
		
		int timeout = 100;
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i = 1; i < 254; i++) {
			String host = subnet + "." + i;
			try {
				if (InetAddress.getByName(host).isReachable(timeout)) {
					Log.d("", host + " is reachable");
					
					list.add(host);
				}
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		
		return list;
	}
}
