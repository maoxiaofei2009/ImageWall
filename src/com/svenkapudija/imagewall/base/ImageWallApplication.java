package com.svenkapudija.imagewall.base;

import android.app.Application;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.svenkapudija.imagewall.caching.BitmapLruCache;
import com.svenkapudija.imagewall.orm.DatabaseHelper;

public class ImageWallApplication extends Application {

	private DatabaseHelper databaseHelper;
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
	
	public DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		
		return databaseHelper;
	}
	
}
