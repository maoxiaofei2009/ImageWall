package com.svenkapudija.imagewall.models;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class LatLonGeoPoint extends GeoPoint {
	
	private double lat;
	private double lon;
	
	public LatLonGeoPoint(double lat, double lon) {
		super((int) (lat*1E6), (int) (lon*1E6));
	}
	
	public double distanceTo(LatLonGeoPoint other) {
		float[] results = new float[2];
		Location.distanceBetween(getLat(), getLon(), other.getLat(), other.getLon(), results);
		return results[0];
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
}