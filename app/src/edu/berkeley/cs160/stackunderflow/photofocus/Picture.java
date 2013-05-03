package edu.berkeley.cs160.stackunderflow.photofocus;

import android.graphics.Bitmap;

public class Picture {
	
	private int id;
	private Bitmap picture;
	
	public Picture(int id, Bitmap picture) {
		this.id = id;
		this.picture = picture;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}
	
	public String toString() {
		return "Picture ID: " + id;
	}
}
