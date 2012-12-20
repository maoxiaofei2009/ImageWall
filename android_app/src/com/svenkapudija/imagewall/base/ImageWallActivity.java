package com.svenkapudija.imagewall.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.svenkapudija.imagewall.api.ImageWallApi;
import com.svenkapudija.imagewall.caching.ImageWallFileUtils;
import com.svenkapudija.imagewall.orm.DatabaseHelper;
import com.svenkapudija.imagewall.utils.Fonts;

public abstract class ImageWallActivity extends Activity {

	private ImageWallApi api;
	private ImageWallFileUtils fileUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		api = new ImageWallApi();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		initUI();
		
		ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content).getRootView();
		Fonts fonts = new Fonts(this);
		fonts.applyFonts(rootView);
		
		fileUtils = new ImageWallFileUtils(this);
	}

	/**
	 * @return	Global {@link ImageWallApi} to use for REST API access.
	 */
	public ImageWallApi getApi() {
		return api;
	}
	
	/**
	 * @return Global {@link ImageWallFileUtils} to use for file write/read from
	 * SD Card.
	 */
	public ImageWallFileUtils getFileUtils() {
		return fileUtils;
	}
	
	/**
	 * @return	Global {@link DatabaseHelper} to use for database access.
	 */
	public DatabaseHelper getHelper() {
		return ((ImageWallApplication) getApplication()).getHelper();
	}
	
	/**
	 * <p>Initialize your layout views. For example</p>
	 * <code>TextView header = (TextView) findViewById(R.id.textView_header);</code>
	 */
	public abstract void initUI();
	
}
