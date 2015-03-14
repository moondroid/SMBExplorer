package it.moondroid.smbexplorer.models;

import it.moondroid.smbexplorer.ExplorerApp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import it.moondroid.smbexplorer.filemanagement.SambaFileSystem;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SambaFile extends GenericFile {

	//public static final URLStreamHandler SMB_HANDLER = new Handler();
	private static final String TAG = "SambaFile";

	private SmbFile mFile, mParentFile;
	private Uri mUri = null;
	private boolean mIsFile, mIsDirectory, mExists, mCanRead, mCanWrite,
			mIsHidden;
	private String mName, mAbsolutePath, mCanonicalPath, mParent;
	private long mLength;
	private Long mLastModified;

	private boolean notExecuted = true;

	private Handler handler;
	
	public SambaFile(String urlString) {
		URL url;
		// try {
		//
		// url = new URL(null, urlString, SMB_HANDLER);
		// NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
		// url.getUserInfo());
		// mFile = new SmbFile(url, auth);
		// Log.d("SambaFile", urlString);
		// mFile = new SmbFile(urlString);
		//
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }

		// if (auth.getPassword() == null || auth.getPassword() == "") {
		// OpenServers servers = OpenServers.getDefaultServers();
		// OpenServer s = servers.findByUser("smb", url.getHost(),
		// auth.getUsername());
		// if (s != null) {
		// auth.setUsername(s.getUser());
		// auth.setPassword(s.getPassword());
		// }
		// }

		// mParent = null;
		// mSize = mModified = null;

		// try {
		// BackgroundWorker task = new BackgroundWorker();
		// task.execute(urlString);
		// Object result = task.get();
		// //Object result = new BackgroundWorker().execute(urlString).get();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		handler = new Handler(Looper.getMainLooper()) {
			
			@Override
			public void handleMessage(Message msg) {
				Log.v(TAG, "handleMessage");
				notExecuted = false;
			}
			
		};

		inizialize(urlString);
		
		while (notExecuted) {
			// wait until background work has finished
		}

	}

	@Override
	public Uri getUri() {
		// return Uri.parse(mFile.getPath());
		return mUri;
	}

	@Override
	public Boolean isDirectory() {
		// try {
		// return mFile.isDirectory();
		// } catch (SmbException e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		return mIsDirectory;
	}

	@Override
	public Boolean isFile() {
		// try {
		// return mFile.isFile();
		// } catch (SmbException e) {
		// e.printStackTrace();
		// return false;
		// }
		return mIsFile;
	}

	@Override
	public Boolean exists() {
		// try {
		// return mFile.exists();
		// } catch (SmbException e) {
		// e.printStackTrace();
		// return false;
		// }
		return mExists;
	}

	@Override
	public String getAbsolutePath() {
		// String ret = "smb://";

		// if (getServer() != null)
		// ret = getServer().getAbsolutePath();
		// if (!ret.endsWith("/"))
		// ret += "/";

		// ret += mFile.getURL().getPath();
		// return ret;
		return mAbsolutePath;
	}

	@Override
	public String getCanonicalPath() {

		// return mFile.getCanonicalPath();
		return mCanonicalPath;
	}

	// @Override
	// public SambaFile getParentFile() {
	// String parent = mFile.getParent();
	// if (parent == null)
	// return null;
	// return new SambaFile(parent);
	// }

	public String getParent() {
		return mParent;
	}

	@Override
	public Boolean canRead() {

		// try {
		// return mFile.canRead();
		// } catch (SmbException e) {
		// e.printStackTrace();
		// return false;
		// }
		return mCanRead;
	}

	@Override
	public Boolean canWrite() {

		// try {
		// return mFile.canWrite();
		//
		// } catch (SmbException e) {
		//
		// e.printStackTrace();
		// return false;
		// }
		return mCanWrite;

		// try {
		// if (mAttributes != null)
		// return (mAttributes & SmbFile.ATTR_READONLY) == 0;
		// if (Thread.currentThread().equals(OpenExplorer.UiThread))
		// return true;
		// else {
		// mAttributes = mFile.getAttributes();
		// return (mAttributes & SmbFile.ATTR_READONLY) == 0;
		// }
		// } catch (SmbException e) {
		// return false;
		// }
		// }
	}

	@Override
	public String getName() {

		// return mFile.getName();
		return mName;
	}

	@Override
	public long length() {

		// try {
		// return mFile.length();
		// } catch (SmbException e) {
		//
		// e.printStackTrace();
		// return 0;
		// }
		return mLength;
	}

	@Override
	public Boolean isHidden() {

		// try {
		// return mFile.isHidden();
		// } catch (SmbException e) {
		// e.printStackTrace();
		// return false;
		// }
		return mIsHidden;
	}

	@Override
	public Long lastModified() {

		// try {
		// return mFile.lastModified();
		// } catch (SmbException e) {
		// e.printStackTrace();
		// return (long) 0;
		// }
		return mLastModified;
	}

	@Override
	public String[] list() {
		try {
			String[] list = mFile.list();
			Log.v(TAG, "list " + list.length);
			for (int i = 0; i < list.length; i++) {
				Log.v(TAG, i + " " + list[i]);
			}

			return list;
		} catch (SmbException e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	private class BackgroundWorker extends AsyncTask<String, Object, Object> {

		@Override
		protected Object doInBackground(String... urlString) {

			try {

				Log.d(TAG, urlString[0]);
				mFile = new SmbFile("smb://" + ExplorerApp.getLocalIp() + urlString[0]);

				// String parent = mFile.getParent();
				// if (parent == null){
				// mParentFile = null;
				// }else{
				// mParentFile = new SambaFile(parent);
				// }

				mUri = Uri.parse(mFile.getPath());
				mIsFile = mFile.isFile();
				mIsDirectory = mFile.isDirectory();
				mExists = mFile.exists();
				mCanRead = mFile.canRead();
				mCanWrite = mFile.canWrite();
				mIsHidden = mFile.isHidden();
				mName = mFile.getName();
				// mAbsolutePath =
				// "smb://"+"192.168.43.60"+mFile.getURL().getPath();
				mAbsolutePath = mFile.getURL().getPath();
				mCanonicalPath = mFile.getCanonicalPath();
				mLength = mFile.length();
				mLastModified = mFile.lastModified();
				mParent = mFile.getParent();
				if (mParent.equalsIgnoreCase("smb://")) {
					mParent += ExplorerApp.getLocalIp() + "/";
				}
				Log.d(TAG, "mIsFile " + mIsFile);
				Log.d(TAG, "mIsDirectory " + mIsDirectory);
				Log.d(TAG, "mExists " + mExists);
				Log.d(TAG, "mCanRead " + mCanRead);
				Log.d(TAG, "mCanWrite " + mCanWrite);
				Log.d(TAG, "mIsHidden " + mIsHidden);
				Log.d(TAG, "mName " + mName);
				Log.d(TAG, "mLength " + mLength);
				Log.d(TAG, "mLastModified " + mLastModified);
				Log.d(TAG, "mAbsolutePath " + mAbsolutePath);
				Log.d(TAG, "mCanonicalPath " + mCanonicalPath);
				Log.d(TAG, "mParent " + mParent);

			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (SmbException e) {
				e.printStackTrace();
				return false;
			}

			return true;

		}

		// @Override
		// protected void onPostExecute(Object result) {
		// notExecuted = false;
		// }

	}

	private void inizialize(String urlString) {

		class BackgroundTask implements Runnable {
			
			String url;

			BackgroundTask(String s) {
				url = s;
			}

			public void run() {
				// someFunc(str);
				try {

					Log.d(TAG, "url "+url);
					//mFile = new SmbFile("smb://" + "192.168.43.60" + url + File.separator);
					mFile = new SmbFile(url, ExplorerApp.getAuthentication());

					// String parent = mFile.getParent();
					// if (parent == null){
					// mParentFile = null;
					// }else{
					// mParentFile = new SambaFile(parent);
					// }

					
					mIsFile = mFile.isFile();
					mIsDirectory = mFile.isDirectory();
					
					mUri = Uri.parse(mFile.getPath());
					String path = mFile.getPath();
					if(mIsFile){
						mUri = Uri.parse(path.substring(0, path.length()-1));
					}else{
						mUri = Uri.parse(path);
					}
					mExists = mFile.exists();
					mCanRead = mFile.canRead();
					mCanWrite = mFile.canWrite();
					mIsHidden = mFile.isHidden();
					mName = mFile.getName();
					if(mName.endsWith(File.separator)){
						mName = mName.substring(0, mName.length()-1);
					}
					// mAbsolutePath =
					// "smb://"+"192.168.43.60"+mFile.getURL().getPath();
					mAbsolutePath = mFile.getURL().getPath();
					mCanonicalPath = mFile.getCanonicalPath();
					mLength = mFile.length();
					mLastModified = mFile.lastModified();
					mParent = mFile.getParent();
					if (mParent.equalsIgnoreCase("smb://")) {
						mParent += ExplorerApp.getLocalIp() + "/";
					}
					Log.d(TAG, "mUri " + mUri);
					Log.d(TAG, "mIsFile " + mIsFile);
					Log.d(TAG, "mIsDirectory " + mIsDirectory);
					Log.d(TAG, "mExists " + mExists);
					Log.d(TAG, "mCanRead " + mCanRead);
					Log.d(TAG, "mCanWrite " + mCanWrite);
					Log.d(TAG, "mIsHidden " + mIsHidden);
					Log.d(TAG, "mName " + mName);
					Log.d(TAG, "mLength " + mLength);
					Log.d(TAG, "mLastModified " + mLastModified);
					Log.d(TAG, "mAbsolutePath " + mAbsolutePath);
					Log.d(TAG, "mCanonicalPath " + mCanonicalPath);
					Log.d(TAG, "mParent " + mParent);

				} catch (MalformedURLException e) {
					e.printStackTrace();
					
				} catch (SmbException e) {
					e.printStackTrace();
					
				}
				
				Message msg = new Message();
				//handler.sendMessage(msg);
				handler.dispatchMessage(msg);
			}
		}
		Thread t = new Thread(new BackgroundTask(urlString));
		t.start();
	}

}
