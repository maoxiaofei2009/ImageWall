package com.svenkapudija.imagewall.models;


public class Image {

	private int id;
	private String description;
	private Tag tag;
	private LatLonGeoPoint geoPoint;

	public Image(int id, String description, Tag tag) {
		this.id = id;
		this.description = description;
		this.tag = tag;
	}

	public int getId() {
		return id;
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
