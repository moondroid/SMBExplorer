package it.moondroid.smbexplorer.webserver;

import it.moondroid.smbexplorer.webserver.NanoHTTPD.Response.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbRandomAccessFile;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

public class UploadServer extends NanoHTTPD {

	private static final String TAG = "UploadServer";

	/**
	 * Common mime types for dynamic content
	 */
	public static final String MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html", MIME_JS = "application/javascript",
			MIME_CSS = "text/css", MIME_PNG = "image/png",
			MIME_DEFAULT_BINARY = "application/octet-stream",
			MIME_XML = "text/xml", MIME_FLV = "video/x-flv",
			MIME_VIDEO = "video/*";

	private Context mContext = null;

	public UploadServer() throws IOException {
		super(8080);
	}

	public UploadServer(Context ctx) throws IOException {
		super(8080);
		mContext = ctx;
	}

	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {

		// File rootsd = Environment.getExternalStorageDirectory();
		// File f = new File(rootsd.getAbsolutePath() + "/movies/prova.flv");
		//
		//
		// //Response r = super.serveFile("/file-upload.htm", header, path,
		// true);
		//
		// res = new Response( Status.OK, "video/x-flv", ins);
		// res.addHeader( "Connection", "Keep-alive");
		// res.addHeader( "ETag", etag);
		// res.isStreaming = true;
		// streamingResponse = res;
		//
		// NanoHTTPD.Response res = new NanoHTTPD.Response(status, mimeType,
		// data);
		// res.addHeader( "Connection", "Keep-alive");
		//
		// serv
		//
		// return res;

		return null;

	}

	@Override
	@Deprecated
	public Response serve(String uri, Method method,
			Map<String, String> headers, Map<String, String> parms,
			Map<String, String> files) {

		Log.d(TAG, "SERVE ::  URI " + uri);

		
		
		// final StringBuilder buf = new StringBuilder();
		// for (Entry<Object, Object> kv : header.entrySet())
		// buf.append(kv.getKey() + " : " + kv.getValue() + "\n");

		InputStream mbuffer = null;
		
		
		try {
			if (uri != null) {

				// if (uri.contains(".js")) {
				// mbuffer = mContext.getAssets().open(uri.substring(1));
				// return new NanoHTTPD.Response(Status.OK, MIME_JS, mbuffer);
				// } else if (uri.contains(".css")) {
				// mbuffer = mContext.getAssets().open(uri.substring(1));
				// return new NanoHTTPD.Response(Status.OK, MIME_CSS, mbuffer);
				//
				// } else if (uri.contains(".png")) {
				// mbuffer = mContext.getAssets().open(uri.substring(1));
				// // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check
				// // comments)
				// return new NanoHTTPD.Response(Status.OK, MIME_PNG, mbuffer);
				// } else

//				if (uri.contains("/mnt/sdcard")) {
//					Log.d(TAG, "request for media on sdCard " + uri);
//					File request = new File(uri);
//					mbuffer = new FileInputStream(request);
//					FileNameMap fileNameMap = URLConnection.getFileNameMap();
//					String mimeType = fileNameMap.getContentTypeFor(uri);
//
//					Response streamResponse = new Response(Status.OK, mimeType,
//							mbuffer);
//					Random rnd = new Random();
//					String etag = Integer.toHexString(rnd.nextInt());
//					streamResponse.addHeader("ETag", etag);
//					streamResponse.addHeader("Connection", "Keep-alive");
//					streamResponse.addHeader("Accept-Ranges", "bytes");
//					return streamResponse;
//
//				} else 
					
				if (uri.contains("smb://")) {
//					Log.d(TAG, "request for media on Samba share " + uri);
//					long from = getFromRange(headers);
//					return createSambaResponseStream(uri, from);

					String fileName = uri.substring(1);
					Log.d(TAG, "request for media on Samba share " + fileName);
					SmbFile request = new SmbFile(fileName);
					mbuffer = new SmbFileInputStream(request);

					FileNameMap fileNameMap = URLConnection.getFileNameMap();
					String mimeType = fileNameMap.getContentTypeFor(uri);

					Response streamResponse = new Response(Status.OK, mimeType,
							mbuffer);
					Random rnd = new Random();
					String etag = Integer.toHexString(rnd.nextInt());
					streamResponse.addHeader("ETag", etag);
					streamResponse.addHeader("Connection", "Keep-alive");

//					long start = getFromRange(headers);
//					long end = request.length();
//					if (start != 0) {
//						mbuffer.skip(start);
//
//					}
					streamResponse.addHeader("Accept-Ranges", "bytes");
//					String rangeSpec = "bytes " + start + "-" + end + "/" + end;
//					streamResponse.addHeader("Content-Range", rangeSpec);
//					long sendCount = end - start;
//					streamResponse.addHeader("Content-Length", "" + sendCount);

					return streamResponse;
					
				} else 
					if (uri.contains("file://")) 
					{
//					Log.d(TAG, "request for media on sdCard " + uri);
//					long from = getFromRange(headers);
//					return createResponseStream(uri, from);
					
					String fileName = uri.replace("/file://", "");
					Log.d(TAG, "request for media on sdCard " + fileName);
					File request = new File(fileName);
					mbuffer = new FileInputStream(request);
					FileNameMap fileNameMap = URLConnection.getFileNameMap();
					String mimeType = fileNameMap.getContentTypeFor(uri);

					Response streamResponse = new Response(Status.OK, mimeType,
							mbuffer);
					Random rnd = new Random();
					String etag = Integer.toHexString(rnd.nextInt());
					streamResponse.addHeader("ETag", etag);
					streamResponse.addHeader("Connection", "Keep-alive");
					streamResponse.addHeader("Accept-Ranges", "bytes");
					return streamResponse;

				} 
					else {
//					mbuffer = mContext.getAssets().open("index.html");
//					return new NanoHTTPD.Response(Status.OK, MIME_HTML, mbuffer);
					Log.d(TAG, "request for media on Asset dir " + uri);	
					long from = getFromRange(headers);
					return createAssetResponseStream(uri, from);	
						
				}
			}

		} catch (IOException e) {
			Log.d(TAG, "Error opening file " + uri.substring(1));
			e.printStackTrace();
		}

		return null;
	}

	private Response createAssetResponseStream(String uri, long start)
			throws IOException {
		// File request = new File(uri);
		// InputStream buffer = new FileInputStream(request);
		// FileNameMap fileNameMap = URLConnection.getFileNameMap();
		// String mimeType = fileNameMap.getContentTypeFor(uri);

		AssetFileDescriptor fd = mContext.getAssets().openFd(uri.substring(1));
		long end = fd.getLength();
		Log.d(TAG, "end: " + end);

		InputStream buffer = mContext.getAssets().open(uri.substring(1),
				AssetManager.ACCESS_RANDOM);

		// Status status = Status.OK;
		Status status = Status.PARTIAL_CONTENT;
		String mimeType = MIME_FLV;
		if (start != 0) {
			buffer.skip(start);
			status = Status.PARTIAL_CONTENT;
		}

		Response streamResponse = new Response(status, mimeType, buffer);

		
		
		Random rnd = new Random();
		String etag = Integer.toHexString(rnd.nextInt());
		streamResponse.addHeader("ETag", etag);
		streamResponse.addHeader("Connection", "Keep-alive");
		streamResponse.addHeader("Accept-Ranges", "bytes");
		String rangeSpec = "bytes " + start + "-" + end + "/" + end;
		streamResponse.addHeader("Content-Range", rangeSpec);
		long sendCount = end - start;
		streamResponse.addHeader("Content-Length", "" + sendCount);

		
		return streamResponse;
	}

	private Response createResponseStream(String uri, long start)
			throws IOException {

		String fileName = uri.replace("/file://", "");
		Log.v(TAG, "fileName "+fileName);
		RandomAccessFile request = new RandomAccessFile(fileName, "r" );
		//File request = new File(uri.replace("file://", ""));
		
		//FileInputStream buffer = new FileInputStream(request);
		RandomAccessStream buffer = new RandomAccessStream(request);
		
//		 String mimeType =
//		 MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//		 MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

		long end = request.length();

		// AssetFileDescriptor fd =
		// mContext.getAssets().openFd(uri.substring(1));
		// long end = fd.getLength();
		// Log.d(TAG, "end: "+end);
		//
		// InputStream buffer = mContext.getAssets().open(uri.substring(1),
		// AssetManager.ACCESS_RANDOM);

		// Status status = Status.OK;
		Status status = Status.PARTIAL_CONTENT;
		String mimeType = MIME_FLV;
		
		if (start != 0) {
			buffer.skip(start);
			status = Status.PARTIAL_CONTENT;
		}

		Response streamResponse = new Response(status, mimeType, buffer);

		Random rnd = new Random();
		String etag = Integer.toHexString(rnd.nextInt());
		streamResponse.addHeader("ETag", etag);
		streamResponse.addHeader("Connection", "Keep-alive");
		streamResponse.addHeader("Accept-Ranges", "bytes");
		String rangeSpec = "bytes " + start + "-" + end + "/" + end;
		streamResponse.addHeader("Content-Range", rangeSpec);
		long sendCount = end - start;
		streamResponse.addHeader("Content-Length", "" + sendCount);

		return streamResponse;
	}

	private Response createSambaResponseStream(String uri, long start)
			throws IOException {

		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, null, null);
		SmbFile file = new SmbFile(uri.substring(1), auth);
		
//		if (!file.isFile()){
//			return new Response("");
//		}
		
		SmbRandomAccessFile request = new SmbRandomAccessFile(file, "r");

		SmbFileInputStream buffer = new SmbFileInputStream(file);
		//RandomAccessSmbStream buffer = new RandomAccessSmbStream(request);
		//InputStream buffer = file.getInputStream();
		
		// FileNameMap fileNameMap = URLConnection.getFileNameMap();
		// String mimeType = fileNameMap.getContentTypeFor(uri);

//		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		String mimeType = MIME_VIDEO;
		
		long end = file.length();

		Log.v(TAG, "mimeType "+ mimeType);
		Log.v(TAG, "end "+ end);
		
		// AssetFileDescriptor fd =
		// mContext.getAssets().openFd(uri.substring(1));
		// long end = fd.getLength();
		// Log.d(TAG, "end: "+end);
		//
		// InputStream buffer = mContext.getAssets().open(uri.substring(1),
		// AssetManager.ACCESS_RANDOM);

		Status status = Status.OK;
		//Status status = Status.PARTIAL_CONTENT;
		
		
		
		if (start != 0) {
			buffer.skip(start);
			status = Status.PARTIAL_CONTENT;
		}

		Response streamResponse = new Response(status, mimeType, buffer);

		Random rnd = new Random();
		String etag = Integer.toHexString(rnd.nextInt());
		streamResponse.addHeader("ETag", etag);
		streamResponse.addHeader("Connection", "Keep-alive");
		streamResponse.addHeader("Accept-Ranges", "bytes");
		String rangeSpec = "bytes " + start + "-" + end + "/" + end;
		streamResponse.addHeader("Content-Range", rangeSpec);
		long sendCount = end - start;
		streamResponse.addHeader("Content-Length", "" + sendCount);

		return streamResponse;

	}

	private long getFromRange(Map<String, String> header) {

		long startFrom = 0;
		long end = 0;

		String range = header.get("range");
		// Log.d(TAG, "range: "+range);
		// String range = header.getProperty("range");

		if (range != null) {
			String from = "";
			String to = "";
			if (range.startsWith("bytes=")) {
				range = range.substring("bytes=".length());
				int minus = range.indexOf('-');
				if (minus > 0) {
					from = range.substring(0, minus);
					to = range.substring(minus + 1);
				}
				try {
					startFrom = Long.parseLong(from);
				} catch (NumberFormatException nfe) {
					// Log.d(TAG, "Range parse error");
				}
				try {
					end = Long.parseLong(to);
				} catch (NumberFormatException nfe) {
					// Log.d(TAG, "Range parse error");
				}
			}
		}
		Log.d(TAG, "startFrom: " + startFrom);
		return startFrom;
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
	
	/**
	 * Seekable Samba InputStream. Abstract, you must add implementation for your
	 * purpose.
	 */
	private class RandomAccessStream extends InputStream {

		private RandomAccessFile mFile;
		
		public RandomAccessStream(RandomAccessFile file) {
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
