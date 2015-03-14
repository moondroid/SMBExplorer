package it.moondroid.smbexplorer.interfaces;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import it.moondroid.smbexplorer.models.FileListing;
import it.moondroid.smbexplorer.models.GenericFile;
import it.moondroid.smbexplorer.utils.PreferenceHelper;


public interface FileListInterface {

	boolean isInPickMode();	
	void listContents(GenericFile f);
	void setCurrentDirAndChilren(GenericFile dir, FileListing folderListing);
	PreferenceHelper getPreferenceHelper();
	Context getContext();
	FragmentActivity getActivity();
}

