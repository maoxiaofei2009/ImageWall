package com.svenkapudija.imagewall.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.svenkapudija.android.fileutils.AndroidFileUtils;
import com.svenkapudija.imagewall.R;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.utils.Fonts;

public class TimelineAdapter extends ArrayAdapter<Image> {

	private Context context;
	private List<Image> items;
	private LayoutInflater inflater;
	private Fonts fonts;
	private AndroidFileUtils fileUtils;
	private Drawable loadingDrawable;
	
	public TimelineAdapter(Context context, List<Image> items) {
		super(context, 0, items);
		
		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fonts = new Fonts(context);
		this.fileUtils = new AndroidFileUtils(context);
		this.loadingDrawable = context.getResources().getDrawable(R.drawable.loading_image);
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
			
			viewHolder.image.setImageDrawable(loadingDrawable);
			viewHolder.imageDescription.setVisibility(View.GONE);
		}
		
		final ViewHolder viewHolderFinal = viewHolder;
		
		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				return fileUtils.getBitmap(AndroidFileUtils.StorageOption.SD_CARD_APP_ROOT, "images", position);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				
				viewHolderFinal.image.setImageBitmap(result);
				viewHolderFinal.imageDescription.setVisibility(View.VISIBLE);
			}
		}.execute();
		
		viewHolderFinal.imageDescription.setText(image.getDescription());
		
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