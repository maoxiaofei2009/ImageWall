package com.svenkapudija.imagewall.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svenkapudija.imagewall.R;
import com.svenkapudija.imagewall.base.ImageWallApplication;
import com.svenkapudija.imagewall.caching.BitmapLruCache;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.utils.Fonts;

public class TimelineAdapter extends ArrayAdapter<Image> {

	private List<Image> items;
	private LayoutInflater inflater;
	private Fonts fonts;
	
	private final BitmapLruCache cache;
	
	public TimelineAdapter(Context context, List<Image> items) {
		super(context, 0, items);
		
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fonts = new Fonts(context);
		
		this.cache = ImageWallApplication.getApplication(context).getBitmapCache();
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		Image image = items.get(position);
        
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_image, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.imageDescription = (TextView) convertView.findViewById(R.id.textView_imageDescription);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView_image);
			
			fonts.applyFonts((ViewGroup) convertView.getRootView());
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			
			viewHolder.image.setBackgroundResource(R.drawable.loading_image);
		}
		
		// Required because after the bitmap is fetched from network, row should
		// refresh it's imageView only if it WASN'T recycled (because of ListView architecture)
		viewHolder.position = position;
		
		cache.loadBitmap(position, viewHolder, image.getFileName());
		
		String imageDescription = image.getDescription();
		if(imageDescription != null && imageDescription.length() > 0) {
			viewHolder.imageDescription.setText(imageDescription);
			viewHolder.imageDescription.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imageDescription.setVisibility(View.GONE);
		}
		
        return convertView;
    }
	
	public static class ViewHolder {
		public TextView imageDescription;
		public ImageView image;
		public int position;
	}
	
	@Override
	public int getCount() {
		return (items != null) ? items.size() : 0;
	}
	
	@Override
	public Image getItem(int position) {
		return items.get(position);
	}
	
}