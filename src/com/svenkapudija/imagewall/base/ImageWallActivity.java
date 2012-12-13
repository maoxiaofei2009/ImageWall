package com.svenkapudija.imagewall.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.svenkapudija.imagewall.api.ImageWallApi;
import com.svenkapudija.imagewall.utils.Fonts;

public abstract class ImageWallActivity extends Activity {

	private ImageWallApi api;
	
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
	}

	public ImageWallApi getApi() {
		return api;
	}
	
	public abstract void initUI();
	
}
