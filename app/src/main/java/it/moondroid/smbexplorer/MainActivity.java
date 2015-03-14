package it.moondroid.smbexplorer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import it.moondroid.networkexplorer.NetworkExplorerActivity;
import it.moondroid.smbexplorer.filemanagement.SambaFileSystem;
import it.moondroid.smbexplorer.fragments.DownloadDialogFragment;
import it.moondroid.smbexplorer.fragments.FileListFragment;
import it.moondroid.smbexplorer.fragments.FileListFragment.FileListFragmentListener;
import it.moondroid.smbexplorer.streamer.Streamer;
import it.moondroid.smbexplorer.webserver.UploadServer;
import jcifs.smb.SmbFile;

import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		FileListFragmentListener {

	private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CODE_IP_ADDRESS = 1;

	//private final String urlString = "http://www.tecnoandroid.it/wp-content/uploads/2013/03/android-wallpaper.png";
	private final String urlString = "smb://192.168.43.60/Users/Public/Android_KITKAT.flv";
	
	private UploadServer server;
	private WebView mWebView;
	private TextView tvCurrentDir;

    private Streamer s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//				.permitAll().build();
//		StrictMode.setThreadPolicy(policy);

		tvCurrentDir = (TextView) findViewById(R.id.tvCurrentDir);

		Button btnLoad = (Button) findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//viewVideo();
				//launch();

				//SambaFileSystem.listServers();

				//new Connection().execute();

				// Log.d("MainActivity", "local IP: "+getLocalIpAddress());
				
				//showDownloadDialog();
                startActivityForResult(new Intent(MainActivity.this, NetworkExplorerActivity.class), REQUEST_CODE_IP_ADDRESS);
			}
		});

		//startServer();

        if (savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            FileListFragment f = new FileListFragment();
            fm.beginTransaction().replace(R.id.fragmentFileExplorer, f).commit();
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_CODE_IP_ADDRESS){
                String ipAddress = data.getStringExtra(NetworkExplorerActivity.KEY_RETURN_IPADDRESS);
                String name = data.getStringExtra(NetworkExplorerActivity.KEY_RETURN_NAME);
                AppPreferences.setLocalIp(this, ipAddress);

                FragmentManager fm = getSupportFragmentManager();
                FileListFragment f = new FileListFragment();
                fm.beginTransaction().replace(R.id.fragmentFileExplorer, f).commitAllowingStateLoss();

            }
        }
    }

    private void startServer() {

		server = null;
		try {
			server = new UploadServer(getApplicationContext());
			try {
				server.start();
			} catch (IOException ioe) {

				Toast.makeText(this, "Couldn't start server:\n" + ioe,
						Toast.LENGTH_SHORT).show();
			}

			Toast.makeText(this, "Listening on port 8080.", Toast.LENGTH_SHORT)
					.show();
			// mWebView.loadUrl("http://localhost:8080/");

		} catch (IOException e1) {

			e1.printStackTrace();
		}
	}

	private void viewVideo() {

		if (server.isAlive()) {

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			// intent.setDataAndType(Uri.parse("http://localhost:8080/movies.png"),
			// "image/*");
			
			//Video in Assets folder
//			intent.setDataAndType(
//					Uri.parse("http://localhost:8080/Android_KITKAT.flv"),
//					"video/*");
			
			//Video in SD card
			String downloadDir="";
			try {
				downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "downloadDir "+downloadDir);
			
//			intent.setDataAndType(
//					Uri.parse("http://localhost:8080/file://"+downloadDir+"/Homer_Simpson.mp4"),
//					"video/*");
			
			//Video in Samba Share
			intent.setDataAndType(
					Uri.parse("http://127.0.0.1:8080/smb://"+ ExplorerApp.getLocalIp() +"/Users/Public/Homer_Simpson.mp4"),
					"video/*");
			startActivity(intent);

			// mWebView.loadUrl("http://localhost:8080/movies.png");
		}

	}

    private void launch() {
        s = Streamer.getInstance();
        final String path = "smb://"+ ExplorerApp.getLocalIp() +"/Users/Public/Homer_Simpson.mp4";
        new Thread() {
            public void run() {
                try {
                    SmbFile file = new SmbFile(path);
                    s.setStreamSrc(file, null);//the second argument can be a list of subtitle files
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Uri uri = Uri.parse(Streamer.URL + Uri.fromFile(new File(Uri.parse(path).getPath())).getEncodedPath());
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setDataAndType(uri, "video/mp4");
                                startActivity(i);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// if (!inetAddress.isLoopbackAddress() &&
					// !inetAddress.isLinkLocalAddress() &&
					// inetAddress.isSiteLocalAddress() ) {
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						String ipAddr = inetAddress.getHostAddress();
						return ipAddr;
					}
				}
			}
		} catch (SocketException ex) {
			Log.d("MainActivity", ex.toString());
		}
		return null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (server != null) {
			server.stop();
			Toast.makeText(this, "Server Stopped", Toast.LENGTH_SHORT).show();
		}

	}

	private class Connection extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {

			// connect();
			// wifi();
			listFiles();
			
			return null;

		}

	}

	private void connect() {

		try {
			InetAddress ipaddr = InetAddress.getLocalHost();
			byte[] addr = ipaddr.getAddress();

			String hostname = "";
			for (int i = 0; i < addr.length; i++) {
				if (i > 0) {
					hostname += ".";
				}
				hostname += addr[i];
			}

			// String hostname = addr.toString();
			String host = ipaddr.getHostName();

			Log.d("MainActivity", "hostname: " + hostname);
			Log.d("MainActivity", "host: " + host);

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void wifi() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();

		String ip = null;

		ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));

		Log.d("MainActivity", "wifi ip: " + ip);
	}

	private void listFiles() {
		SambaFileSystem.listFiles(ExplorerApp.getLocalIp());
	}
	
	private void showDownloadDialog() {
		FragmentManager fm = getSupportFragmentManager();
		//DownloadDialogFragment dialog = new DownloadDialogFragment();
		DownloadDialogFragment dialog = DownloadDialogFragment.newInstance(urlString);
		dialog.show(fm, "download_fragment");
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		FileListFragment f = (FileListFragment) fm
				.findFragmentById(R.id.fragmentFileExplorer);

		if (f != null) {
			boolean backDone = f.doBack();
			if (!backDone) {
				super.onBackPressed();
				finish();
			}
		}

	}

	@Override
	public void onSetCurrentDir(String path, boolean isRoot) {

		tvCurrentDir.setText(path);
	}
}
