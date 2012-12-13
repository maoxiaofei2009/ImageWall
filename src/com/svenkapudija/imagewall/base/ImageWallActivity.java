package com.svenkapudija.imagewall.base;

import android.app.Activity;
import android.view.ViewGroup;

import com.svenkapudija.imagewall.utils.Fonts;

public abstract class ImageWallActivity extends Activity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		initUI();
		
		ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content).getRootView();
		Fonts fonts = new Fonts(this);
		fonts.applyFonts(rootView);
	}
	
	public abstract void initUI();
	
}
