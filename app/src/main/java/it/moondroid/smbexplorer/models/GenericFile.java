package it.moondroid.smbexplorer.models;

import java.io.IOException;


import android.net.Uri;
import android.util.Log;

public abstract class GenericFile  {

	private static final String TAG = "GenericFile";
	
	private static Type mType;
	private static String mUrlString;
	
	public static enum Type {
		LOCAL, SAMBA
	}
	
	public static GenericFile newInstance(Type type, String urlString) {

		mType = type;
		
		switch (type) {
		case LOCAL:
			return new LocalFile(urlString);
		case SAMBA:
			return new SambaFile(urlString);
		default:
			return null;

		}
	}

//	private File fileInstance = null;
//	private SmbFile smbFileInstance = null;
	
//	public FileOperations newInstance(Type type, String path) {
//
//		if (type == Type.LOCAL) {
//			
//			fileInstance = new File(path);
//			smbFileInstance = null;
//			return (FileOperations) fileInstance;
//			
//		} else if (type == Type.SAMBA) {
//			try {
//				
//				smbFileInstance = new SmbFile("smb://" + IP_ADDRESS + path + "/");
//				fileInstance = null;
//				return (FileOperations) smbFileInstance;
//				
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return null;
//
//	}
	
	/**
     * Returns Uri representing path
     * 
     * @return Uri
     */
    public abstract Uri getUri();
    
    /**
     * This should return a Parceleable string that can be used to recreate the
     * OpenPath object
     * 
     * @return String
     */
    public abstract String getAbsolutePath();
    
    public abstract String getCanonicalPath();
    
    /**
     * Indicates if this file represents a <em>directory</em> on the underlying
     * file system.
     * 
     * @return {@code true} if this file is a directory, {@code false}
     *         otherwise.
     */
    public abstract Boolean isDirectory();

    /**
     * Indicates if this file represents a <em>file</em> on the underlying file
     * system.
     * 
     * @return {@code true} if this file is a file, {@code false} otherwise.
     */
    public abstract Boolean isFile();
    
    /**
     * Returns a boolean indicating whether this file can be found on the
     * underlying file system.
     * 
     * @return {@code true} if this file exists, {@code false} otherwise.
     */
    public abstract Boolean exists();
    
    
    //public abstract GenericFile getParentFile();
    public GenericFile getParentFile() {
    	
    	String parent = getParent();
//    	if (parent.equalsIgnoreCase("smb://")){
//    		parent+="192.168.43.60/";
//    	}
    	Log.d(TAG, "getParentFile "+parent);
    	return newInstance(mType, parent);
    }
    
    
    /**
     * Indicates parent of requested path.
     * 
     * @param path Path of which the parent is requested.
     * @return indicating parent of path parameter
     */
//    public static String getParent(String path) {
//        if (path.equals("/"))
//            return null;
//        if (path.endsWith("/"))
//            path = path.substring(0, path.length() - 1);
//        path = path.substring(0, path.lastIndexOf("/") + 1);
//        if (path.endsWith("://") || path.endsWith(":/"))
//            return null;
//        return path;
//    }
    public abstract String getParent();
    
    public abstract String getName();

    /**
     * Indicates whether the current context is allowed to read from this file.
     * 
     * @return {@code true} if this file can be read, {@code false} otherwise.
     */
    public abstract Boolean canRead();

    /**
     * Indicates whether the current context is allowed to write to this file.
     * 
     * @return {@code true} if this file can be written, {@code false}
     *         otherwise.
     */
    public abstract Boolean canWrite();
    
    public abstract long length();
    
    /**
     * Returns whether or not this file is a hidden file as defined by the
     * operating system. The notion of "hidden" is system-dependent. For Unix
     * systems a file is considered hidden if its name starts with a ".". For
     * Windows systems there is an explicit flag in the file system for this
     * purpose.
     * 
     * @return {@code true} if the file is hidden, {@code false} otherwise.
     */
    public abstract Boolean isHidden();
    
    /**
     * Returns the time when this file was last modified, measured in
     * milliseconds since January 1st, 1970, midnight. Returns 0 if the file
     * does not exist.
     * 
     * @return the time when this file was last modified.
     */
    public abstract Long lastModified();

    /**
     * Used to list files in directories.
     * 
     * @return
     * @throws IOException
     */
    public abstract String[] list();
    
}
