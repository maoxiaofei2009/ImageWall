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
import com.svenkapudija.imagewall.api.ImageWallApi.BitmapSizeType;
import com.svenkapudija.imagewall.api.ImageWallApi.ImageListener;
import com.svenkapudija.imagewall.models.Image;

public class BitmapLruCache {

	private LruCache<Integer, Bitmap> memoryCache;
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

	    memoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(Integer key, Bitmap bitmap) {
	            return getByteCount(bitmap);
	        }
	        
	        private int getByteCount(Bitmap bitmap) {
	        	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	        	bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao);
	        	return bao.toByteArray().length;
			}
	    };
	}

	public void loadBitmap(final int position, final ViewHolder viewHolder, final int key) {
		new AsyncTask<Integer, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Integer... params) {
				return memoryCache.get(key);
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				
				if (result != null) {
					if(viewHolder.position == position) {
						viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, result));
					}
			    } else if(fileUtils.existsImage(key)) {
			    	GetBitmapFromDiskTask task = new GetBitmapFromDiskTask(viewHolder, position);
			        task.execute(key);
			    } else {
			    	GetBitmapFromNetworkTask task = new GetBitmapFromNetworkTask(viewHolder, position);
			        task.execute(key);
			    }
			}
			
		}.execute();
		
	    //final Bitmap bitmap = getBitmapFromMemCache(key);
	    
	}
	
	private class GetBitmapFromNetworkTask {
		
		private ViewHolder viewHolder;
		private int position;
		
		public GetBitmapFromNetworkTask(ViewHolder viewHolder, int position) {
			this.viewHolder = viewHolder;
			this.position = position;
		}
		
		public void execute(final int imageId) {
			final ImageWallApi api = new ImageWallApi();
	    	api.getImage(imageId, new ImageListener() {
				
				@Override
				public void onSuccess(Image image) {
					downloadBitmap(imageId, api, image);
				}

				private void downloadBitmap(final int imageId, final ImageWallApi api, Image image) {
					api.getBitmap(BitmapSizeType.THUMBNAIL_ANDROID, image.getFileName(), new BitmapListener() {
						
						@Override
						public void onSuccess(Bitmap image) {
							try {
								fileUtils.writeImage(null, imageId);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
//							addBitmapToMemoryCache(imageId, null);
//							
//							Bitmap bitmap = null;
//							if(viewHolder.position == position) {
//								viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, bitmap));
//							}
						}
						
						@Override
						public void onFailure() {
							
						}
					});
				}
				
				@Override
				public void onFailure() {
					
				}
			});
		}
	}

	private class GetBitmapFromDiskTask extends AsyncTask<Integer, Bitmap, Bitmap> {
		
		private ViewHolder viewHolder;
		private int position;
		
		public GetBitmapFromDiskTask(ViewHolder viewHolder, int position) {
			this.viewHolder = viewHolder;
			this.position = position;
		}
		
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	    	int key = params[0];
	    	
	    	Bitmap imageOnDisk = fileUtils.getImage(key);
	    	publishProgress(imageOnDisk);
	    	
	        addBitmapToMemoryCache(key, imageOnDisk);
	        
	        return imageOnDisk;
	    }
	    
	    @Override
	    protected void onProgressUpdate(Bitmap... values) {
	    	super.onProgressUpdate(values);
	    	
	    	if(viewHolder.position == position) {
	    		viewHolder.image.setBackgroundDrawable(new BitmapDrawable(resources, values[0]));
	    	}
	    }
	    
	}
	
	public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null && !bitmap.isRecycled()) {
	    	memoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(Integer key) {
	    return memoryCache.get(key);
	}
	
}
