package it.moondroid.networkexplorer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Marco on 14/03/2015.
 */

public class AuthenticationDialogFragment extends DialogFragment {

    private EditText mEditTextName;
    private EditText mEditTextPassword;

    public AuthenticationDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static AuthenticationDialogFragment newInstance(String ip){
        Bundle args = new Bundle();
        args.putString("IP", ip);
        AuthenticationDialogFragment f = new AuthenticationDialogFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String ip = getArguments().getString("IP");

        AlertDialog.Builder b =  new  AlertDialog.Builder(getActivity())
                .setTitle(ip)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ExplorerPreferences.setUsername(getActivity(), ip, mEditTextName.getText().toString());
                                ExplorerPreferences.setPassword(getActivity(), ip, mEditTextPassword.getText().toString());
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_authentication, null);
        mEditTextName = (EditText) view.findViewById(R.id.txt_your_name);
        mEditTextPassword = (EditText) view.findViewById(R.id.txt_your_password);

        mEditTextName.setText(ExplorerPreferences.getUsername(getActivity(), ip));
        mEditTextPassword.setText(ExplorerPreferences.getPassword(getActivity(), ip));

        b.setView(view);
        return b.create();
    }
}