package it.moondroid.smbexplorer.models;

import java.io.File;
import java.io.IOException;

import android.net.Uri;

public class LocalFile extends GenericFile {

	private File mFile;

	public LocalFile(File f) {
		mFile = f;
	}

	public LocalFile(String path) {
		mFile = new File(path);
	}

	@Override
	public Uri getUri() {
		return Uri.fromFile(mFile);
	}

	@Override
	
	public Boolean isDirectory() {
		return mFile.isDirectory();
	}

	@Override
	public Boolean isFile() {
		return mFile.isFile();
	}

	@Override
	public Boolean exists() {
		return mFile.exists();
	}

	@Override
	public String getAbsolutePath() {
		return mFile.getAbsolutePath();
	}
	
	@Override
	public String getCanonicalPath() {
		
		try {
			return mFile.getCanonicalPath();
		
		} catch (IOException e) {
			
			e.printStackTrace();
			return "";
		}
	}

//	@Override
//	public LocalFile getParentFile() {
//		String parent = mFile.getParent();
//		if (parent == null)
//			return null;
//		return new LocalFile(parent);
//	}

	@Override
	public Boolean canRead() {
		return mFile.canRead();
	}

	@Override
	public Boolean canWrite() {
		return mFile.canWrite();
	}

	@Override
	public String getName() {
		
		return mFile.getName();
	}

	@Override
	public long length() {
		return mFile.length();
	}

	@Override
	public Boolean isHidden() {
		return mFile.isHidden();
	}

	@Override
	public Long lastModified() {
		return mFile.lastModified();
	}

	@Override
	public String[] list() {
		
		return mFile.list();
		
	}

	@Override
	public String getParent() {

		return mFile.getParent();
	}

	

}
