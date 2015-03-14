package it.moondroid.smbexplorer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import it.moondroid.smbexplorer.R;
import it.moondroid.smbexplorer.models.FileListEntry;
import it.moondroid.smbexplorer.utils.FileUtil;

import java.util.List;

public class FileListAdapter extends BaseAdapter {

	public static class ViewHolder 
	{
	  public TextView resName;
	  public ImageView resIcon;
	  public TextView resMeta;
	}

	private static final String TAG = FileListAdapter.class.getName();
	  
	private Context mContext;
	private List<FileListEntry> files;
	private LayoutInflater mInflater;
	
	public FileListAdapter(Context context, List<FileListEntry> files) {
		super();
		mContext = context;
		this.files = files;
		mInflater =  LayoutInflater.from(mContext);
		
	}

	
	@Override
	public int getCount() {
		if(files == null)
		{
			return 0;
		}
		else
		{
			return files.size();
		}
	}

	@Override
	public Object getItem(int arg0) {

		if(files == null)
			return null;
		else
			return files.get(arg0);
	}

	public List<FileListEntry> getItems()
	{
	  return files;
	}
	
	@Override
	public long getItemId(int position) {

		return position;
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.file_explorer_item, parent, false);
            holder = new ViewHolder();
            holder.resName = (TextView)convertView.findViewById(R.id.explorer_resName);
            holder.resMeta = (TextView)convertView.findViewById(R.id.explorer_resMeta);
            holder.resIcon = (ImageView)convertView.findViewById(R.id.explorer_resIcon);

            convertView.setTag(holder);
        } 
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        final FileListEntry currentFile = files.get(position);
        holder.resName.setText(currentFile.getName());
        holder.resIcon.setImageDrawable(FileUtil.getIcon(mContext, currentFile.getPath()));
        String meta = FileUtil.prepareMeta(currentFile, mContext);
        holder.resMeta.setText(meta);

        
        return convertView;
	}

}
