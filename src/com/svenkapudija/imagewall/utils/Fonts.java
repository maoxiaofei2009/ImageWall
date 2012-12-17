package com.svenkapudija.imagewall.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 *	Helper class to apply custom fonts on layout. 
 */
public class Fonts {

	private final static String FONT_PROXIMA_NOVA_SEMIBOLD = "fonts/ProximaNova-Semibold.ttf";
	private final static String FONT_HELVETICA_ROUNDED_LT = "fonts/Helvetica-Rounded-LT-Bold.ttf";
	
	private Typeface typefaceProximaNova;
	private Typeface typefaceHelveticaRounded;
	
	public Fonts(Context context) {
		typefaceProximaNova = Typeface.createFromAsset(context.getAssets(), FONT_PROXIMA_NOVA_SEMIBOLD); 
		typefaceHelveticaRounded = Typeface.createFromAsset(context.getAssets(), FONT_HELVETICA_ROUNDED_LT); 
	}

	/**
	 * Recursively apply fonts to all views in the tree.
	 * 
	 * @param view
	 */
	public void applyFonts(ViewGroup view) {
		if (view == null) return;
		
	    int count = view.getChildCount();
	    for (int i = 0; i < count; i++) {
	        View child = view.getChildAt(i);
	        if (child instanceof EditText) {
	            ((TextView) child).setTypeface(typefaceProximaNova);
	        } else if (child instanceof TextView || child instanceof Button) {
	            ((TextView) child).setTypeface(typefaceHelveticaRounded);
	        } else if (child instanceof ViewGroup) {
	        	applyFonts((ViewGroup) child);
	        }
	    }
	}
	
}
