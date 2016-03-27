package com.sy.trucksysapp.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2588377084846740434L;
	private String code, name, value, ids;
	private ArrayList<HashMap<String, Object>> datadetail;
	private HashMap<Integer, Boolean> isSelected;

	public SelectModel(String code, String name, String value, String ids,
			ArrayList<HashMap<String, Object>> datadetail) {
		super();
		this.code = code;
		this.name = name;
		this.value = value;
		this.ids = ids;
		this.datadetail = datadetail;
	}

	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		if (value.equals("") || value == null) {
			return "全部";
		} else {

			return value;
		}
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public ArrayList<HashMap<String, Object>> getDatadetail() {
		return datadetail;
	}

	public void setDatadetail(ArrayList<HashMap<String, Object>> datadetail) {
		this.datadetail = datadetail;
	}

	public HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		this.isSelected = isSelected;
	}

}
