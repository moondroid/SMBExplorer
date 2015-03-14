package it.moondroid.smbexplorer.fragments;

import org.apache.commons.io.FilenameUtils;

import it.moondroid.smbexplorer.R;
import it.moondroid.smbexplorer.webserver.DownloadService;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class DownloadDialogFragment extends DialogFragment {

	private static ProgressDialog mProgressDialog;
	private static int mProgress = 0;

	public DownloadDialogFragment() {
		// Empty constructor required for DialogFragment
	}

	public static DownloadDialogFragment newInstance(String urlString) {
		DownloadDialogFragment frag = new DownloadDialogFragment();

		Bundle args = new Bundle();
		args.putString("url", urlString);
		frag.setArguments(args);

		frag.setRetainInstance(true);

		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String savedInstanceString = savedInstanceState == null ? "null"
				: "not null";
		Log.v("DownloadDialogFragment", "onCreateDialog savedInstanceState: "
				+ savedInstanceString);

		// getting the url
		Bundle b = getArguments();
		String urlString = "";
		if (b != null) {
			urlString = b.getString("url");
		}
		String fileName = FilenameUtils.getBaseName(urlString);
		String fileExtension = FilenameUtils.getExtension(urlString);
		Log.v("DownloadDialogFragment", "urlString " + urlString);

		// initialize the progress dialog
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(getString(R.string.download_text));
		mProgressDialog.setMessage(fileName + "." + fileExtension);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(mProgress);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		if (!DownloadService.isStarted()) {
			// this is how you fire the downloader
			Intent downloadIntent = new Intent(getActivity()
					.getApplicationContext(), DownloadService.class);
			downloadIntent.putExtra("url", urlString);

			if (urlString.startsWith("http://")) {
				downloadIntent.putExtra("url_type",
						DownloadService.URL_INTERNET);
			} else if (urlString.startsWith("smb://")) {
				downloadIntent.putExtra("url_type", DownloadService.URL_SAMBA);
			}

			downloadIntent.putExtra("receiver", new DownloadReceiver(
					new Handler()));
			getActivity().getApplicationContext().startService(downloadIntent);

		}

		// Disable the back button
		OnKeyListener keyListener = new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {

					DownloadService.stop();
					mProgress = 0;

					Toast.makeText(getActivity(),
							getString(R.string.download_cancel_toast),
							Toast.LENGTH_SHORT).show();
					Log.v("DownloadDialogFragment", "download_cancel");

					mProgressDialog.dismiss();
					// return true;
				}
				return false;
			}

		};
		mProgressDialog.setOnKeyListener(keyListener);
		return mProgressDialog;

	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	private class DownloadReceiver extends ResultReceiver {

		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			super.onReceiveResult(resultCode, resultData);

			if (resultCode == DownloadService.UPDATE_PROGRESS) {

				int progress = resultData.getInt("progress");
				mProgressDialog.setProgress(progress);
				mProgress = progress;

			} else if (resultCode == DownloadService.DOWNLOAD_COMPLETED) {

				int progress = resultData.getInt("progress");
				mProgressDialog.setProgress(progress);
				mProgress = progress;

				mProgressDialog.dismiss();
				mProgress = 0;
				Toast.makeText(getActivity(),
						getString(R.string.download_success_toast),
						Toast.LENGTH_SHORT).show();
				Log.v("DownloadDialogFragment", "download_success");

			} else if (resultCode == DownloadService.DOWNLOAD_ERROR) {

				mProgressDialog.dismiss();
				mProgress = 0;

				Toast.makeText(getActivity(),
						getString(R.string.download_fail_toast),
						Toast.LENGTH_SHORT).show();
				Log.v("DownloadDialogFragment", "download_failed");

			}
		}
	}

}