package com.svenkapudija.imagewall.models;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;


public class Image implements Parcelable {

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String description;
	@DatabaseField
	private Date dateCreated;
	@DatabaseField
	private int fileSize;
	@DatabaseField
	private String fileName;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Tag tag;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Location location;

	public Image() {
		// TODO Auto-generated constructor stub
	}
	
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

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(id);
		parcel.writeString(description);
		parcel.writeLong(dateCreated.getTime());
		parcel.writeInt(fileSize);
		parcel.writeString(fileName);
		parcel.writeParcelable(tag, 0);
		parcel.writeParcelable(location, 0);
	}

	public void readFromParcel(Parcel source) {
		id = source.readInt();
		description = source.readString();
		dateCreated = new Date(source.readLong());
		fileSize = source.readInt();
		fileName = source.readString();
		tag = source.readParcelable(Tag.class.getClassLoader());
		location = source.readParcelable(Location.class.getClassLoader());
	}
	
	public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
		public Image createFromParcel(Parcel in) {
			Image route = new Image();
			route.readFromParcel(in);
			
			return route;
		}

		@Override
		public Image[] newArray(int size) {
			return new Image[size];
		}
	};

	@Override
	public String toString() {
		return "Image [id=" + id + ", description=" + description
				+ ", dateCreated=" + dateCreated + ", fileSize=" + fileSize
				+ ", fileName=" + fileName + ", tag=" + tag + ", location="
				+ location + "]";
	}

}
