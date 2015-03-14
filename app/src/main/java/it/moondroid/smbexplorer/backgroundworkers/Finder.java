package it.moondroid.smbexplorer.backgroundworkers;

import it.moondroid.smbexplorer.ExplorerApp;
import it.moondroid.smbexplorer.R;
import it.moondroid.smbexplorer.interfaces.FileListInterface;
import it.moondroid.smbexplorer.models.FileListEntry;
import it.moondroid.smbexplorer.models.FileListing;
import it.moondroid.smbexplorer.models.GenericFile;
import it.moondroid.smbexplorer.utils.FileListSorter;
import it.moondroid.smbexplorer.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class Finder extends AsyncTask<GenericFile, Integer, FileListing> {

	private static final String TAG = Finder.class.getName();

	private FileListInterface caller;
	private ProgressDialog waitDialog;

	private GenericFile currentDir;

	public Finder(FileListInterface caller) {

		this.caller = caller;
	}

	@Override
	protected void onPostExecute(FileListing result) {

		FileListing childFilesList = result;
		Log.v(TAG, "Children for " + currentDir.getAbsolutePath() + " received");

		if (waitDialog != null && waitDialog.isShowing()) {
			waitDialog.dismiss();
		}
		Log.v(TAG, "Children for " + currentDir.getAbsolutePath()
				+ " passed to caller");
		caller.setCurrentDirAndChilren(currentDir, childFilesList);

	}

	@Override
	protected FileListing doInBackground(GenericFile... params) {

		Thread waitForASec = new Thread() {

			@Override
			public void run() {

				waitDialog = new ProgressDialog(caller.getActivity());
				waitDialog.setTitle("");
				waitDialog.setMessage(caller.getContext().getString(
						R.string.querying_filesys));
				waitDialog.setIndeterminate(true);

				try {
					Thread.sleep(100);
					if (this.isInterrupted()) {
						return;
					} else {
						caller.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {

								if (waitDialog != null)
									waitDialog.show();
							}
						});

					}
				} catch (InterruptedException e) {

					Log.e(TAG,
							"Progressbar waiting thread encountered exception ",
							e);
					e.printStackTrace();
				}

			}
		};
		caller.getActivity().runOnUiThread(waitForASec);

		currentDir = params[0];
		Log.v(TAG,
				"Received directory to list paths - "
						+ currentDir.getAbsolutePath());

		String[] children = currentDir.list();
		FileListing listing = new FileListing(new ArrayList<FileListEntry>());
		List<FileListEntry> childFiles = listing.getChildren();

		boolean showHidden = caller.getPreferenceHelper().isShowHidden();
		boolean showSystem = caller.getPreferenceHelper().isShowSystemFiles();
		Map<String, Long> dirSizes = FileUtil.getDirSizes(currentDir);

		for (String fileName : children) {
			
			//Log.v(TAG, fileName);
			
			if (".nomedia".equals(fileName)) {
				listing.setExcludeFromMedia(true);
			}
			// File f = new
			// File(currentDir.getAbsolutePath()+File.separator+fileName);
//			GenericFile f = GenericFile.newInstance(MyMoviesApp.FILESYSTEM_TYPE,
//					currentDir.getAbsolutePath() + File.separator + fileName);
			
			GenericFile f = null;
			if (ExplorerApp.FILESYSTEM_TYPE==GenericFile.Type.LOCAL){
				
				f = GenericFile.newInstance(ExplorerApp.FILESYSTEM_TYPE,
						currentDir.getAbsolutePath() + File.separator + fileName );
				Log.v(TAG, currentDir.getAbsolutePath() + File.separator + fileName);
				
			}else if (ExplorerApp.FILESYSTEM_TYPE==GenericFile.Type.SAMBA){
				
				f = GenericFile.newInstance(ExplorerApp.FILESYSTEM_TYPE,
						currentDir.getCanonicalPath()  + fileName + File.separator);
				Log.v(TAG, currentDir.getCanonicalPath() + fileName + File.separator);
			}
			
			
			
			if (!f.exists()) {
				continue;
			}
			if (FileUtil.isProtected(f) && !showSystem) {
				continue;
			}
			if (f.isHidden() && !showHidden) {
				continue;
			}

			String fname = f.getName();
			Log.v(TAG, "added "+fname);
			
			FileListEntry child = new FileListEntry();
			child.setName(fname);
			child.setPath(f);
			if (f.isDirectory()) {
				try {
					Long dirSize = dirSizes.get(f.getCanonicalPath());
					child.setSize(dirSize);
				} catch (Exception e) {

					Log.w(TAG, "Could not find size for "
							+ child.getPath().getAbsolutePath());
					child.setSize(0);
				}
			} else {
				child.setSize(f.length());
			}
			child.setLastModified(new Date(f.lastModified()));
			childFiles.add(child);
		}

		FileListSorter sorter = new FileListSorter(caller.getContext());
		Collections.sort(childFiles, sorter);

		Log.v(TAG, "Will now interrupt thread waiting to show progress bar");
		if (waitForASec.isAlive()) {
			try {
				waitForASec.interrupt();
			} catch (Exception e) {

				Log.e(TAG, "Error while interrupting thread", e);
			}
		}
		return listing;
	}
}
