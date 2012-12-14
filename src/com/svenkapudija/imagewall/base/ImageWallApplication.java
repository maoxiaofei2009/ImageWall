package com.svenkapudija.imagewall.base;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.Application;
import android.content.Context;

public class ImageWallApplication extends Application {

	private BitmapLruCache cache;
	
	@Override
	public void onCreate() {
		super.onCreate();

		// Using default constructor, using 1/8th of Heap space (RAM)
		cache = new BitmapLruCache(this);
	}

	public BitmapLruCache getBitmapCache() {
		return cache;
	}

	public static ImageWallApplication getApplication(Context context) {
		return (ImageWallApplication) context.getApplicationContext();
	}

}
