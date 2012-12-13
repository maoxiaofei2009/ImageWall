package com.svenkapudija.imagewall.models;

import android.graphics.Bitmap;

public class Image {

	private Bitmap image;
	private String description;
	private Tag tag;
	private LatLonGeoPoint geoPoint;

	public Image(Bitmap image, String description, Tag tag) {
		this.image = image;
		this.description = description;
		this.tag = tag;
	}

	public Bitmap getImage() {
		return image;
	}

	public String getDescription() {
		return description;
	}

	public Tag getTag() {
		return tag;
	}

	public LatLonGeoPoint getGeoPoint() {
		return geoPoint;
	}
	
	public void setGeoPoint(LatLonGeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
}
