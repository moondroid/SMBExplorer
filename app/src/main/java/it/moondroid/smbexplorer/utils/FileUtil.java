package it.moondroid.smbexplorer.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;


import org.apache.commons.io.FileUtils;

import it.moondroid.smbexplorer.ExplorerApp;
import it.moondroid.smbexplorer.R;
import it.moondroid.smbexplorer.callbacks.CancellationCallback;
import it.moondroid.smbexplorer.interfaces.FileListInterface;
import it.moondroid.smbexplorer.models.FileListEntry;
import it.moondroid.smbexplorer.models.GenericFile;
import it.moondroid.smbexplorer.streamer.Streamer;
import jcifs.smb.SmbFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public final class FileUtil {

	private static final String TAG = FileUtil.class.getName();
	private static GenericFile COPIED_FILE = null;
	private static int pasteMode = 1;
	
	
	public static final int PASTE_MODE_COPY = 0;
	public static final int PASTE_MODE_MOVE = 1;
	
	
	private FileUtil(){}
	
	 public static synchronized void setPasteSrcFile(GenericFile f, int mode) 
	  {  
	         COPIED_FILE = f;  
	         pasteMode = mode%2; 
	  }  

	 public static synchronized GenericFile getFileToPaste()
	 {
		 return COPIED_FILE;
	 }
	 
	 public static synchronized int getPasteMode()
	 {
		 return pasteMode;
	 }

	static boolean isMusic(GenericFile file) {

		//Uri uri = Uri.fromFile(file);
		Uri uri = file.getUri();
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("audio/"));

	}

	static boolean isVideo(GenericFile file) {

		//Uri uri = Uri.fromFile(file);
		Uri uri = file.getUri();
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("video/"));
	}

	public static boolean isPicture(GenericFile file) {
		
		//Uri uri = Uri.fromFile(file);
		Uri uri = file.getUri();
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		
		if(type == null)
			return false;
		else
		return (type.toLowerCase().startsWith("image/"));
	}
	
	public static boolean isProtected(GenericFile path)
	{
		return (!path.canRead() && !path.canWrite());
	}
	
	public static boolean isUnzippable(GenericFile path)
	{
		return (path.isFile() && path.canRead() && path.getName().endsWith(".zip"));
	}


	public static boolean isRoot(GenericFile dir) {
		
		return dir.getAbsolutePath().equals("/");
	}


	public static boolean isSdCard(GenericFile file) {
		
		try {
			return (file.getCanonicalPath().equals(Environment.getExternalStorageDirectory().getCanonicalPath()));
		} catch (IOException e) {
			return false;
		}
		
	}


	public static Drawable getIcon(Context mContext, GenericFile file) {
		
		if(!file.isFile()) //dir
		{
			if(FileUtil.isProtected(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sys_dir);
					
			}
			else if(FileUtil.isSdCard(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sdcard);
			}
			else 
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_dir);
			}
		}
		else //file
		{
			String fileName = file.getName();
			if(FileUtil.isProtected(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_sys_file);
					
			}
			if(fileName.endsWith(".apk"))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_apk);
			}
			if(fileName.endsWith(".zip"))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_zip);
			}
			else if(FileUtil.isMusic(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_music);
			}
			else if(FileUtil.isVideo(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_video);
			}
			else if(FileUtil.isPicture(file))
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_image);
			}
			else
			{
				return mContext.getResources().getDrawable(R.drawable.filetype_generic);
			}
		}
		
	}


//	public static boolean delete(GenericFile fileToBeDeleted) {
//
//		try
//		{
//			FileUtils.forceDelete(fileToBeDeleted);
//			return true;
//		} catch (IOException e) {
//			return false;
//		}
//	}


//	public static boolean mkDir(String canonicalPath, CharSequence newDirName) {
//		
//		GenericFile newdir = new File(canonicalPath+File.separator+newDirName);
//		return newdir.mkdirs();
//		
//	}

	public static String prepareMeta(FileListEntry file, Context context) {
		
		GenericFile f = file.getPath();
		try
		{
			if(isProtected(f))
			{
				return context.getString(R.string.system_path);
			}
			if(file.getPath().isFile())
			{
				return context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()));
			}
			
		}
		catch (Exception e) {
			Log.e(FileUtil.class.getName(), e.getMessage());
		}
		
		return "";
	}

//	public static boolean paste(int mode, GenericFile destinationDir, AbortionFlag flag) {
//		
//		Log.v(TAG, "Will now paste file on clipboard");
//		GenericFile fileBeingPasted = new File(getFileToPaste().getParent(),getFileToPaste().getName());
//		if(doPaste(mode, getFileToPaste(), destinationDir, flag))
//		{
//			if(getPasteMode() == PASTE_MODE_MOVE)
//			{
//				if(fileBeingPasted.isFile())
//				{
//					if(FileUtils.deleteQuietly(fileBeingPasted))
//					{
//						Log.i(TAG, "File deleted after paste "+fileBeingPasted.getAbsolutePath());
//					}
//					else
//					{
//						Log.w(TAG, "File NOT deleted after paste "+fileBeingPasted.getAbsolutePath());
//					}
//				}
//				else
//				{
//					try {
//						FileUtils.deleteDirectory(fileBeingPasted);
//					} catch (IOException e) {
//						Log.e(TAG, "Error while deleting directory after paste - "+fileBeingPasted.getAbsolutePath(), e);
//						return false;
//					}
//				}
//			}
//			return true;
//		}
//		else
//		{
//			return false;
//		}
//	}
//	
//	private static boolean doPaste(int mode, GenericFile srcFile, GenericFile destinationDir, AbortionFlag flag) {
//		
//		if(!flag.isAborted())
//		try
//		{
//			if(srcFile.isDirectory())
//			{
//				
//				GenericFile newDir = new File(destinationDir.getAbsolutePath()+File.separator+srcFile.getName());
//				newDir.mkdirs();
//				
//				for(GenericFile child : srcFile.listFiles())
//				{
//					doPaste(mode, child, newDir, flag);
//				}
//				return true;
//			}
//			else
//			{
//				FileUtils.copyFileToDirectory(srcFile, destinationDir);
//				return true;
//			}
//		}
//		catch (Exception e) {
//			return false;
//		}
//		else
//		{
//			return false;
//		}
//	}
//
//
//	public static boolean canPaste(GenericFile destDir) {
//		
//		if(getFileToPaste() == null)
//		{
//			return false;
//		}
//		if(getFileToPaste().isFile())
//		{
//			return true;
//		}
//		try
//		{
//			if(destDir.getCanonicalPath().startsWith(COPIED_FILE.getCanonicalPath()))
//			{
//				return false;
//			}
//			else
//			{
//				return true;
//			}
//		}
//		catch (Exception e) {
//			
//			return false;
//		}
//	}

	public static boolean canShowQuickActions(FileListEntry currentFile, FileListInterface fileList) {
		
		if(!fileList.getPreferenceHelper().useQuickActions() || fileList.isInPickMode())
		{
			return false;
		}
		
		GenericFile path = currentFile.getPath();
		if(isProtected(path))
		{
			return false;
		}
		if(isSdCard(path))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@SuppressWarnings("deprecation")
	public static CharSequence[] getFileProperties(FileListEntry file, Context context) {
		
		if(FileUtil.isSdCard(file.getPath()))
		{
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long sdAvailSize = (long)stat.getAvailableBlocks() *(long)stat.getBlockSize();
			long totalSize = (long)stat.getBlockCount() *(long)stat.getBlockSize();
			
			return new CharSequence[]{context.getString(R.string.total_capacity, FileUtil.getSizeStr(totalSize)),
					context.getString(R.string.free_space, FileUtil.getSizeStr(sdAvailSize))};
		}
		else if(file.getPath().isFile())
		return new CharSequence[]{context.getString(R.string.filepath_is, file.getPath().getAbsolutePath()),
				context.getString(R.string.mtime_is, DateFormat.getDateFormat(context).format(file.getLastModified())),
				context.getString(R.string.size_is, FileUtils.byteCountToDisplaySize(file.getSize()))};
		
		else
		{
			return new CharSequence[]{context.getString(R.string.filepath_is, file.getPath().getAbsolutePath()),
					context.getString(R.string.mtime_is, DateFormat.getDateFormat(context).format(file.getLastModified()))};
		}
	}
	
	private static String getSizeStr(long bytes) {
		
		if(bytes >= FileUtils.ONE_GB)
		{
			return (double)Math.round((((double)bytes / FileUtils.ONE_GB)*100))/100 + " GB";
		}
		else if(bytes >= FileUtils.ONE_MB)
		{
			return (double)Math.round((((double)bytes / FileUtils.ONE_MB)*100))/100 + " MB";
		}
		else if(bytes >= FileUtils.ONE_KB)
		{
			return (double)Math.round((((double)bytes / FileUtils.ONE_KB)*100))/100 + " KB";
		}

		return bytes+" bytes";
	}

	public static Map<String, Long> getDirSizes(GenericFile dir)
	{
		Map<String, Long> sizes = new HashMap<String, Long>();
		
		try {
			
			Process du = Runtime.getRuntime().exec("/system/bin/du -b -d1 "+dir.getCanonicalPath(), new String[]{}, Environment.getRootDirectory());
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					du.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null)
			{
				String[] parts = line.split("\\s+");
				
				String sizeStr = parts[0];
				Long size = Long.parseLong(sizeStr);
				
				String path = parts[1];
				
				sizes.put(path, size);
			}
			
		} catch (IOException e) {
			Log.w(TAG, "Could not execute DU command for "+dir.getAbsolutePath(), e);
		}
		
		return sizes;
		
	}

	public static void gotoPath(final String currentPath, final Context mContext, final FileListInterface fileList) {
		
		gotoPath(currentPath, mContext, fileList, null);
	}
	
	public static void gotoPath(final String currentPath, final Context mContext, final FileListInterface fileList, final CancellationCallback callback) {
		
		final EditText input = new EditText(mContext);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
		input.setSingleLine();
		new Builder(mContext)
		.setTitle(mContext.getString(R.string.goto_path))
		.setView(input)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  
			CharSequence toPath = input.getText();
			try
			{
				//GenericFile toDir = new File(toPath.toString());
				GenericFile toDir = GenericFile.newInstance(ExplorerApp.FILESYSTEM_TYPE, toPath.toString());
				
				if(toDir.isDirectory() && toDir.exists())
				{
					fileList.listContents(toDir);
				}
				else
				{
					throw new FileNotFoundException();
				}
				
			}
			catch (Exception e) {
				Log.e(TAG, "Error navigating to path"+toPath, e);
				new Builder(mContext)
				.setTitle(mContext.getString(R.string.error))
				.setMessage(mContext.getString(R.string.path_not_exist))
				.show();
			}
		  }
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		   
			  dialog.dismiss();
			  if(callback != null)
			  callback.onCancel();
		  }
		})
		.show();
		input.setText(currentPath);
	}

//	public static GenericFile getDownloadsFolder() {
//		return new File("/sdcard/"+Environment.DIRECTORY_DOWNLOADS);
//	}
//
//	public static GenericFile getDcimFolder() {
//		return new File("/sdcard/"+Environment.DIRECTORY_DCIM);
//	}

    public static void openSmbFile(final Activity activity, final GenericFile genericFile) {
        final Streamer s = Streamer.getInstance();
        //final String staticPath = "smb://"+ Constants.LOCAL_IP+"/Users/Public/Homer_Simpson.mp4";
        final String path = removeLastSeparator(genericFile.getCanonicalPath());

        Uri uri = genericFile.getUri();
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

        new Thread() {
            public void run() {
                try {
                    final SmbFile file = new SmbFile(path);
                    s.setStreamSrc(file, null);//the second argument can be a list of subtitle files
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                Uri uri = Uri.parse(Streamer.URL + Uri.fromFile(new File(Uri.parse(path).getPath())).getEncodedPath());
                                Log.d("FileUtil", "uri.toString() " + uri.toString());
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setDataAndType(uri, mimeType);
                                activity.startActivity(i);
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

    public static String removeLastSeparator(String str) {
        if (str.length() > 0 && str.charAt(str.length()-1) == File.separatorChar) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }
}
