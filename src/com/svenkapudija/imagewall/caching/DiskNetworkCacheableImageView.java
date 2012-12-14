package com.svenkapudija.imagewall.caching;

import uk.co.senab.bitmapcache.BitmapLruCache;
import uk.co.senab.bitmapcache.CacheableBitmapWrapper;
import uk.co.senab.bitmapcache.CacheableImageView;
import android.content.Context;
import android.graphics.Bitmap;

import com.svenkapudija.imagewall.api.ImageWallApi;
import com.svenkapudija.imagewall.api.ImageWallApi.ImageListener;

public class DiskNetworkCacheableImageView extends CacheableImageView {

	private final BitmapLruCache cache;
	
	private ImageWallApi api;
	private ImageWallFileUtils fileUtils;
	
	public DiskNetworkCacheableImageView(Context context, BitmapLruCache cache) {
		super(context);
		
		this.cache = cache;
		this.fileUtils = new ImageWallFileUtils(context);
		this.api = new ImageWallApi();
	}

	public void loadImage(BitmapLruCache cache, int imageId) {
		String cacheKey = Integer.toString(imageId);
		CacheableBitmapWrapper bitmapWrapper = cache.get(cacheKey);

		if (inCache(bitmapWrapper)) {
			getFromCache(bitmapWrapper);
		} else if (onDisk(imageId)) {
			getFromDisk(cache, imageId, cacheKey);
		} else {
			getFromNetwork(imageId);
		}
	}

	private void getFromNetwork(int imageId) {
		api.getImage(imageId, new ImageListener() {
			
			@Override
			public void onSuccess(Bitmap image) {
				CacheableBitmapWrapper bitmapWrapper = new CacheableBitmapWrapper(image);
				
				// Display the image
				setImageCachedBitmap(bitmapWrapper);

				// Add to cache
				cache.put(bitmapWrapper);
			}
			
			@Override
			public void onFailure() {
				
			}
		});
	}

	private void getFromDisk(BitmapLruCache cache, int imageId, String cacheKey) {
		Bitmap imageOnDisk = fileUtils.getImage(imageId);
		
		setImageBitmap(imageOnDisk);
		cache.put(cacheKey, new CacheableBitmapWrapper(imageOnDisk));
	}

	private void getFromCache(CacheableBitmapWrapper bitmapWrapper) {
		setImageCachedBitmap(bitmapWrapper);
	}

	private boolean onDisk(int imageId) {
		return fileUtils.existsImage(imageId);
	}

	private boolean inCache(CacheableBitmapWrapper bitmapWrapper) {
		return bitmapWrapper != null && bitmapWrapper.hasValidBitmap();
	}

}
