package com.sy.trucksysapp.entity;


public class TrafficModel {

	private String name;
	public Double getLatitude() {
		return latitude;
	}


	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}


	public Double getLongitude() {
		return longitude;
	}


	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setZoomlevel(int zoomlevel) {
		this.zoomlevel = zoomlevel;
	}
	private Double latitude;
	private Double longitude;
	private int zoomlevel;
	
	
	public TrafficModel(String name,Double longitude, Double latitude, 
			int zoomlevel) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zoomlevel = zoomlevel;
	}


	public String getName() {
		return name;
	}
	public int getZoomlevel() {
		return zoomlevel;
	}
	
	
	
	

}
