package com.svenkapudija.imagewall.models;

import com.j256.ormlite.field.DatabaseField;

public class Tag {

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
	public String toString() {
		return "Tag [id=" + id + ", value=" + value + "]";
	}

}
