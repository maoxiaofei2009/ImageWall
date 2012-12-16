package com.svenkapudija.imagewall.overlays;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class LocationOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();

	public LocationOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public void addOverlay(OverlayItem overlay) {
		items.add(overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int arg0) {
		return items.get(arg0);
	}

	@Override
	public int size() {
		return items.size();
	}

}
