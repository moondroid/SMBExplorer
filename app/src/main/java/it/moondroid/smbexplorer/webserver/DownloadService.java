package it.moondroid.smbexplorer.webserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbRandomAccessFile;

import org.apache.commons.io.FilenameUtils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadService extends IntentService {

	private static final String TAG = DownloadService.class.getName();
	// result codes for receiver
	public static final int UPDATE_PROGRESS = 8344;
	public static final int DOWNLOAD_COMPLETED = 8345;
	public static final int DOWNLOAD_ERROR = 8346;

	public static final int URL_INTERNET = 0;
	public static final int URL_SAMBA = 1;
	
	private static boolean mIsStarted = false;
	private static boolean mStop = false;

	public DownloadService() {
		super("DownloadService");
	}

	public static boolean isStarted() {
		return mIsStarted;
	}

	public static void stop(){
		mStop = true;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		mIsStarted = true;

		String urlToDownload = intent.getStringExtra("url");
		Log.v(TAG, "onHandleIntent " + urlToDownload);
		
		ResultReceiver receiver = (ResultReceiver) intent
				.getParcelableExtra("receiver");

		int urlType = intent.getIntExtra("url_type", URL_INTERNET);
		
		try {			

			String fileName = FilenameUtils.getBaseName(urlToDownload);
			String fileExtension = FilenameUtils.getExtension(urlToDownload);
			String dirPath = getDefaultDownloadDir();

			InputStream input = null;
			long fileLength = 0;
			
			switch (urlType){
				
			case URL_INTERNET:
				
				URL url = new URL(urlToDownload);
				URLConnection connection = url.openConnection();
				connection.connect();

				// this will be useful so that you can show a typical 0-100%
				// progress bar
				fileLength = connection.getContentLength();
				
				// download the file
				input = new BufferedInputStream(url.openStream());
				break;
			
			case URL_SAMBA:
				
//				SmbFile f = new SmbFile(urlToDownload);
//				fileLength = f.length();
//				input = new SmbFileInputStream(f);
				
				SmbFile f = new SmbFile(urlToDownload);
				fileLength = f.length();
				SmbRandomAccessFile request = new SmbRandomAccessFile(f, "rw");
				//input = new RandomAccessSmbStream(request);
				input = new SmbFileInputStream(f);
				
				break;
			}
			
			
			
			OutputStream output = new FileOutputStream(dirPath + File.separator
					+ fileName + "." + fileExtension);
			Log.v(TAG, "FileOutputStream " + dirPath + File.separator
					+ fileName + "." + fileExtension);
			
			
			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				Bundle resultData = new Bundle();
				resultData.putInt("progress", (int) (total * 100 / fileLength));
				receiver.send(UPDATE_PROGRESS, resultData);
				output.write(data, 0, count);
				
				if(mStop){
					mStop = false;
					mIsStarted = false;
					output.flush();
					output.close();
					input.close();
					this.stopSelf();
					Log.v(TAG, "Service stopped");
					return;
				}
			}

			output.flush();
			output.close();
			input.close();

			Bundle resultData = new Bundle();
			resultData.putInt("progress", 100);
			receiver.send(DOWNLOAD_COMPLETED, resultData);

		} catch (IOException e) {
			e.printStackTrace();

			Bundle resultData = new Bundle();
			resultData.putString("message", e.getMessage());
			receiver.send(DOWNLOAD_ERROR, resultData);
		}

		mIsStarted = false;

	}

	private String getDefaultDownloadDir() {

		String downloadDir = "";
		try {
			downloadDir = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v(TAG, "downloadDir " + downloadDir);
		return downloadDir;
	}
	
	
	/**
	 * Seekable Samba InputStream. Abstract, you must add implementation for your
	 * purpose.
	 */
	private class RandomAccessSmbStream extends InputStream {

		private SmbRandomAccessFile mFile;
		
		public RandomAccessSmbStream(SmbRandomAccessFile file) {
			mFile = file;
		}

		/**
		 * @return total length of stream (file)
		 * @throws IOException 
		 */
		public long length() throws IOException {
			return mFile.length();
		}

		/**
		 * Seek within stream for next read-ing.
		 */
		public void seek(long offset) throws IOException{
			mFile.seek(offset);
		}

		@Override
		public int read() throws IOException {
			byte[] b = new byte[1];
			mFile.read(b);
			return b[0] & 0xff;
		}
	}
	
}
