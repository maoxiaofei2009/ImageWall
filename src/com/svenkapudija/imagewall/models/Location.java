package com.svenkapudija.imagewall.models;

import com.j256.ormlite.field.DatabaseField;


public class Location {
	
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private double lat;
	@DatabaseField
	private double lon;
	
	public Location() {
		// TODO Auto-generated constructor stub
	}
	
	public Location(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public double distanceTo(Location other) {
		float[] results = new float[2];
		android.location.Location.distanceBetween(getLat(), getLon(), other.getLat(), other.getLon(), results);
		return results[0];
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lon=" + lon + "]";
	}
	
}