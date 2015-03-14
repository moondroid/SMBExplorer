package it.moondroid.smbexplorer.fragments;

import it.moondroid.smbexplorer.ExplorerApp;
import it.moondroid.smbexplorer.R;
import it.moondroid.smbexplorer.adapters.FileListAdapter;
import it.moondroid.smbexplorer.backgroundworkers.Finder;
import it.moondroid.smbexplorer.callbacks.OnBackPressedListener;
import it.moondroid.smbexplorer.interfaces.FileListInterface;
import it.moondroid.smbexplorer.models.FileListEntry;
import it.moondroid.smbexplorer.models.FileListing;
import it.moondroid.smbexplorer.models.GenericFile;
import it.moondroid.smbexplorer.utils.FileUtil;
import it.moondroid.smbexplorer.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.TextView;

public class FileListFragment extends ListFragment implements
		FileListInterface, OnBackPressedListener {

	private static final String TAG = FileListFragment.class.getName();
	
	private static final String CURRENT_DIR_DIR = "current-dir";

	private ListView explorerListView;
	private GenericFile currentDir;
	private GenericFile previousOpenDirChild;
	private List<FileListEntry> files;
	private FileListAdapter adapter;

	private PreferenceHelper preference;
	private boolean excludeFromMedia = false;
	private boolean isPicker = false;
	private boolean focusOnParent;

	private GenericFile.Type fileSystemType = ExplorerApp.FILESYSTEM_TYPE;

	private FileListFragmentListener mCallback;

	// Container Activity must implement this interface
	public interface FileListFragmentListener {
		public void onSetCurrentDir(String path, boolean isRoot);
	}

	// The Fragment captures the interface implementation during its onAttach()
	// lifecycle method
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (FileListFragmentListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement FileListFragmentListener");
		}
	}

	// create a new instance with a starting folder
	public static FileListFragment newInstance(String startingFolder) {

		FileListFragment f = new FileListFragment();
		Bundle args = new Bundle();
		args.putString(CURRENT_DIR_DIR, startingFolder);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setRetainInstance(true);

		preference = new PreferenceHelper(getContext());
		focusOnParent = preference.focusOnParent();

		initRootDir(savedInstanceState);

		files = new ArrayList<FileListEntry>();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		initFileListView();
		listContents(currentDir);
	}

	
	private void initRootDir(Bundle savedInstanceState) {

		// currentDir = getPreferenceHelper().getStartDir();
		// currentDir = new File("/");

		// If app was restarted, find where the user last left it
		String restartDirPath = null;
		Bundle b = getArguments();
		if (b != null) {
			restartDirPath = getArguments().getString(CURRENT_DIR_DIR);
		}

		if (restartDirPath != null) {
			// File restartDir = (File) GenericFile.newInstance(fileSystemType,
			// restartDirPath);
//			File restartDir = (File) new GenericFile().newInstance(
//					fileSystemType, restartDirPath);
			GenericFile restartDir = GenericFile.newInstance(fileSystemType, restartDirPath);

			if (restartDir.exists() && restartDir.isDirectory()) {
				currentDir = restartDir;
				// getIntent().removeExtra(FileExplorerApp.EXTRA_FOLDER);
			}
		} else if (savedInstanceState != null
				&& savedInstanceState.getSerializable(CURRENT_DIR_DIR) != null) {

			// currentDir = new File(savedInstanceState.getSerializable(
			// CURRENT_DIR_DIR).toString());
			// currentDir = (File) GenericFile.newInstance(fileSystemType,
			// savedInstanceState.getSerializable(
			// CURRENT_DIR_DIR).toString());
//			currentDir = (File) new GenericFile().newInstance(fileSystemType,
//					savedInstanceState.getSerializable(CURRENT_DIR_DIR)
//							.toString());
			currentDir = GenericFile.newInstance(fileSystemType, savedInstanceState.getSerializable(CURRENT_DIR_DIR)
					.toString());
			
		} else {
			currentDir = preference.getStartDir();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// save the current directory, for ex. when rotating the device
		outState.putSerializable(CURRENT_DIR_DIR, currentDir.getAbsolutePath());

	}

	private void initFileListView() {

		// text to display for the list when there're no items
		setEmptyText(getResources().getString(
				it.moondroid.smbexplorer.R.string.empty_folder));

		explorerListView = (ListView) getListView();
		adapter = new FileListAdapter(getActivity().getApplicationContext(),
				files);
		// explorerListView.setAdapter(adapter);
		setListAdapter(adapter);

		explorerListView.setTextFilterEnabled(true);

		// explorerListView.setOnItemLongClickListener(getLongPressListener());
		// registerForContextMenu(explorerListView);
	}

	public void listContents(GenericFile dir) {
		listContents(dir, null);
	}

	private void listContents(GenericFile dir, GenericFile previousOpenDirChild) {
		if (!dir.isDirectory() || FileUtil.isProtected(dir)) {
			return;
		}
		if (previousOpenDirChild != null) {
//			this.previousOpenDirChild = new File(
//					previousOpenDirChild.getAbsolutePath());
			this.previousOpenDirChild = GenericFile.newInstance(fileSystemType, previousOpenDirChild.getAbsolutePath());
			
		} else {
			this.previousOpenDirChild = null;
		}
		new Finder(this).execute(dir);
	}

	@Override
	public boolean isInPickMode() {
		return false;
	}

	@Override
	public PreferenceHelper getPreferenceHelper() {
		return preference;
	}

	@Override
	public synchronized void setCurrentDirAndChilren(GenericFile dir,
			FileListing folderListing) {
		currentDir = dir;

		List<FileListEntry> children = folderListing.getChildren();
		excludeFromMedia = folderListing.isExcludeFromMedia();
		TextView emptyText = (TextView) getActivity().findViewById(
				android.R.id.empty);
		if (emptyText != null) {
			emptyText.setText(R.string.empty_folder);
		}
		files.clear();
		files.addAll(children);

		adapter.notifyDataSetChanged();
		setListShown(true);

		// getActionBar().setSelectedNavigationItem(0);

		if (FileUtil.isRoot(currentDir)) {
			// gotoLocations[0] = getString(R.string.filesystem);
		} else {
			// gotoLocations[0] = currentDir.getName();
		}

		if (previousOpenDirChild != null && focusOnParent) {
			int position = files.indexOf(new FileListEntry(previousOpenDirChild
					.getAbsolutePath()));
			if (position >= 0)
				explorerListView.setSelection(position);
		} else {
			explorerListView.setSelection(0);
		}

		// mSpinnerAdapter.notifyDataSetChanged();

		// ActionBar ab = getActionBar();
		// ab.setSelectedNavigationItem(0);
		//
		// ab.setSubtitle(getString(R.string.item_count_subtitle,
		// children.size()));
		// if (Util.isRoot(currentDir) || currentDir.getParentFile() == null) {
		// ab.setDisplayHomeAsUpEnabled(false);
		// ab.setTitle(getString(R.string.filesystem));
		// } else {
		// ab.setTitle(currentDir.getName());
		// ab.setDisplayHomeAsUpEnabled(true);
		// }

		if (FileUtil.isRoot(currentDir) || currentDir.getParentFile() == null) {
			
			//mCallback.onSetCurrentDir(getString(R.string.filesystem), true);
			mCallback.onSetCurrentDir(dir.getCanonicalPath(), true);
			
		} else {
			// mCallback.onSetCurrentDir(currentDir.getName(), false);
			mCallback.onSetCurrentDir(dir.getAbsolutePath(), false);

		}
	}

	private void select(GenericFile file) {
		if (FileUtil.isProtected(file)) {
			new Builder(getActivity())
					.setTitle(getString(R.string.access_denied))
					.setMessage(
							getString(R.string.cant_open_dir, file.getName()))
					.show();

		} else if (file.isDirectory()) {

			listContents(file);

		} else {

			doFileAction(file);
		}
	}

	private void doFileAction(GenericFile file) {
		if (FileUtil.isProtected(file) || file.isDirectory()) {
			return;
		}

		if (isPicker) {
			// TODO
			// pickFile(file);
			return;
		} else {
			
			if (ExplorerApp.FILESYSTEM_TYPE==GenericFile.Type.LOCAL){
				openFile(file);
			} else {
				openRemoteFile(file);
			}
			
			return;
		}
	}

	private void openFile(GenericFile file) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		Uri uri = file.getUri();
		
		
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		Log.d(TAG, "openFile type "+type);
		
		//intent.setDataAndType(uri, type == null ? "*/*" : type);
		intent.setDataAndType(Uri.parse("http://localhost:8080/"+"file://"+file.getAbsolutePath()), type == null ? "*/*" : type);
		startActivity((Intent.createChooser(intent,
				getString(R.string.open_using))));
	}

	private void openRemoteFile(GenericFile file){
//		Intent intent = new Intent();
//		intent.setAction(Intent.ACTION_VIEW);
//
//		Uri uri = file.getUri();
//		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
//
//
//		intent.setDataAndType(
//				Uri.parse("http://localhost:8080/"+uri.toString()), type);
//		startActivity(intent);
        FileUtil.openSmbFile(getActivity(), file);
	}
	
	private void gotoParent() {

		if (FileUtil.isRoot(currentDir)) {
			// Do nothing finish();
		} else {
			listContents(currentDir.getParentFile(), currentDir);
		}
	}

	@Override
	public Context getContext() {
		return this.getActivity().getApplicationContext();
	}

	// onListItemClick implements onclick listener for the list
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		if (explorerListView.isClickable()) {
			FileListEntry file = (FileListEntry) getListAdapter().getItem(
					position);
			select(file.getPath());
		}
	}

	// OnBackPressedListener implementation
	@Override
	public boolean doBack() {

		if (isPicker) {
			return false;
		}
		if (preference.useBackNavigation()) {
			
			if (FileUtil.isRoot(currentDir)) {
				
				return false;
			} else {
				gotoParent();
				return true;
			}
		} else {
			return false;
		}
	}
}
