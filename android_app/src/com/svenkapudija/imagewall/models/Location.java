package com.svenkapudija.imagewall.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;


public class Location implements Parcelable {
	
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeDouble(lat);
		parcel.writeDouble(lon);
	}

	public void readFromParcel(Parcel source) {
		id = source.readInt();
		lat = source.readDouble();
		lon = source.readDouble();
	}
	
	public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
		public Location createFromParcel(Parcel in) {
			Location route = new Location();
			route.readFromParcel(in);
			
			return route;
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};

	@Override
	public String toString() {
		return "Location [id=" + id + ", lat=" + lat + ", lon=" + lon + "]";
	}

}