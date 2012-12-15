package com.svenkapudija.imagewall.models;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;


public class Image {

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
	@DatabaseField(foreign = true)
	private Tag tag;
	@DatabaseField(foreign = true)
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
	public String toString() {
		return "Image [id=" + id + ", description=" + description
				+ ", dateCreated=" + dateCreated + ", fileSize=" + fileSize
				+ ", fileName=" + fileName + ", tag=" + tag + ", location="
				+ location + "]";
	}

}
