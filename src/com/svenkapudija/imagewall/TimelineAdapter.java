package com.svenkapudija.imagewall;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svenkapudija.imagewall.models.Image;

public class TimelineAdapter extends ArrayAdapter<Image> {

	private Context context;
	private List<Image> items;
	private LayoutInflater inflater;
	
	public TimelineAdapter(Context context, List<Image> items) {
		super(context, 0, items);
		
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Image row = items.get(position);
        
		ViewHolder viewHolder = null;
        
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_image, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.imageDescription = (TextView) convertView.findViewById(R.id.textView_imageDescription);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView_image);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		// Binding
		
		
        return convertView;
    }
	
	private static class ViewHolder {
		TextView imageDescription;
		ImageView image;
	}

	@Override
	public int getCount() {
		return items.size();
	}
	
	@Override
	public Image getItem(int position) {
		return items.get(position);
	}
	
}