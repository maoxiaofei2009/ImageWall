package com.svenkapudija.imagewall.base;

import android.app.Application;
import android.content.Context;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.svenkapudija.imagewall.caching.BitmapLruCache;

public class ImageWallApplication extends Application {

	private BitmapLruCache cache;
	
	@Override
	public void onCreate() {
		super.onCreate();

		cache = new BitmapLruCache(this);
		
		LocationLibrary.initialiseLibrary(getBaseContext(), "com.svenkapudija.imagewall");
	}

	public BitmapLruCache getBitmapCache() {
		return cache;
	}

	public static ImageWallApplication getApplication(Context context) {
		return (ImageWallApplication) context.getApplicationContext();
	}

}
