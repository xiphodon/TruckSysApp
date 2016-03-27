package com.sy.trucksysapp.entity;

import android.content.Intent;

public class DriverMenuModel {
	private String name;
	private int imageresource;
	private Intent intent;

	public DriverMenuModel(String name, int imageresource, Intent intent) {
		super();
		this.setName(name);
		this.setImageresource(imageresource);
		this.setIntent(intent);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getImageresource() {
		return imageresource;
	}

	public void setImageresource(int imageresource) {
		this.imageresource = imageresource;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

}
