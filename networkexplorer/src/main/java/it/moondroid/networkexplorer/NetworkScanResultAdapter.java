package it.moondroid.networkexplorer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Provides the adapter to display each ipaddress found
 * in a list.
 * @author dream09
 *
 */
public class NetworkScanResultAdapter extends ArrayAdapter<Map<String, String>>{

    /* Variables */
    public static final String KEY_NAME = "name";
    public static final String KEY_IPADDRESS = "ipaddress";

    private Context context;
    private int id;
    private List<Map<String, String>> result;

    private OnInfoClickListener listener;

    public interface OnInfoClickListener {
        public void onInfoClick(int position);
    }


    /**
     * Constructor.
     * @param context
     * @param resource
     * @param objects
     */
    public NetworkScanResultAdapter(Context context, int resource, List<Map<String, String>> objects) {
        super(context, resource, objects);

        this.context = context;
        this.id = resource;
        this.result = objects;

    }



	/* Overridden methods */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Setup the view.
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        // Add the ipaddress.
        HashMap<String, String> info = (HashMap<String, String>) getItem(position);
        final String name = info.get(KEY_NAME);
        final String address = info.get(KEY_IPADDRESS);

        if (name != "") {
            TextView itemName = (TextView) v.findViewById(R.id.ItemName);
            if (itemName != null) {
                itemName.setText(name);
            }
        }

        if (address != "") {
            TextView itemIP = (TextView) v.findViewById(R.id.ItemIP);
            if (itemIP != null) {
                itemIP.setText(address);
            }
        }

        v.findViewById(R.id.tel_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null){
                    listener.onInfoClick(position);
                }
            }
        });

        return v;
    }



	/* Methods */

    /**
     * Method returns the result specified by the
     * argument i.
     */
    public Map<String, String> getItem(int i) {
        return result.get(i);
    }


    public void setOnInfoClickListener(OnInfoClickListener listener){
        this.listener = listener;
    }
}
