package com.svenkapudija.imagewall.caching;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.svenkapudija.imagewall.adapters.TimelineAdapter.ViewHolder;
import com.svenkapudija.imagewall.api.ImageWallApi;
import com.svenkapudija.imagewall.api.ImageWallApi.BitmapListener;
import com.svenkapudija.imagewall.api.ImageWallApi.ImageSizeType;

public class BitmapLruCache {

	private LruCache<String, Bitmap> memoryCache;
	private ImageWallFileUtils fileUtils;
	private Resources resources;
	
	public BitmapLruCache(Context context) {
	    initMemoryCache(context);
	    
	    this.fileUtils = new ImageWallFileUtils(context);
	    this.resources = context.getResources();
	}

	private void initMemoryCache(Context context) {
		final int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = 1024 * 1024 * memClass / 8;

	    memoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return getByteCount(bitmap);
	        }
	        
	        private int getByteCount(Bitmap bitmap) {
	        	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	        	bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
	        	return bao.toByteArray().length;
			}
	    };
	}

	/**
	 * Load image from memory, disk or network.
	 * 
	 * @param position
	 * @param viewHolder
	 * @param key
	 */
	public void loadBitmap(final int position, final ViewHolder viewHolder, final String key) {
		new AsyncTask<String, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(String... params) {
				return memoryCache.get(key);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				
				if (result != null) { // Memory
					if(viewReused(viewHolder, position) == false) {
						viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, result));
					}
			    } else if(fileUtils.existsThumbnail(key)) { // Disk
			    	GetBitmapFromDiskTask task = new GetBitmapFromDiskTask(viewHolder, position);
			        task.execute(key);
			    } else { // Network
			    	GetBitmapFromNetworkTask task = new GetBitmapFromNetworkTask(viewHolder, position);
			        task.execute(key);
			    }
			}
			
		}.execute();
	}
	
	private class GetBitmapFromNetworkTask {
		
		private ViewHolder viewHolder;
		private int position;
		
		public GetBitmapFromNetworkTask(ViewHolder viewHolder, int position) {
			this.viewHolder = viewHolder;
			this.position = position;
		}
		
		public void execute(final String imageName) {
			final ImageWallApi api = new ImageWallApi();
			api.getBitmap(ImageSizeType.THUMBNAIL_ANDROID, imageName, new BitmapListener() {
				
				@Override
				public void onSuccess(Bitmap image) {
					try {
						fileUtils.writeThumbnail(image, imageName);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					addBitmapToMemoryCache(imageName, image);
					
					if(viewReused(viewHolder, position) == false) {
						viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, image));
					}
				}

				@Override
				public void onFailure() {
					
				}
			});
		}
	}

	private class GetBitmapFromDiskTask extends AsyncTask<String, Bitmap, Bitmap> {
		
		private ViewHolder viewHolder;
		private int position;
		
		public GetBitmapFromDiskTask(ViewHolder viewHolder, int position) {
			this.viewHolder = viewHolder;
			this.position = position;
		}
		
	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	String key = params[0];
	    	
	    	Bitmap imageOnDisk = fileUtils.getThumbnail(key);
	    	publishProgress(imageOnDisk);
	    	
	        addBitmapToMemoryCache(key, imageOnDisk);
	        
	        return imageOnDisk;
	    }
	    
	    @Override
	    protected void onProgressUpdate(Bitmap... values) {
	    	super.onProgressUpdate(values);
	    	
	    	if(viewReused(viewHolder, position) == false) {
	    		viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, values[0]));
	    	}
	    }
	    
	}
	
	private boolean viewReused(ViewHolder viewHolder, int position) {
		return viewHolder.position != position;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null && !bitmap.isRecycled()) {
	    	memoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return memoryCache.get(key);
	}
	
}
