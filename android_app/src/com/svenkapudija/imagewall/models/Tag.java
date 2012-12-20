package com.svenkapudija.imagewall.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;

public class Tag implements Parcelable {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String value;
	
	public Tag() {
		// TODO Auto-generated constructor stub
	}
	
	public Tag(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
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
		parcel.writeString(value);
	}

	public void readFromParcel(Parcel source) {
		id = source.readInt();
		value = source.readString();
	}
	
	public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
		public Tag createFromParcel(Parcel in) {
			Tag route = new Tag();
			route.readFromParcel(in);
			
			return route;
		}

		@Override
		public Tag[] newArray(int size) {
			return new Tag[size];
		}
	};

	@Override
	public String toString() {
		return "Tag [id=" + id + ", value=" + value + "]";
	}

}
