package com.sy.trucksysapp.entity;

import java.io.Serializable;

/**
 * 收货地址信息
 * 
 * @author lxs 20150612
 * 
 */
public class AddressInfo implements Serializable {
	private int AddressId;
	private String name;
	private String phone;
	private String address;
	private String area;
	private boolean selected;
	private static final long serialVersionUID = 2L;
	/**
	 * 实例化联系人
	 * 
	 * @param name
	 *            联系人
	 * @param phone
	 *            电话
	 * @param address
	 *            地址
	 */
	public AddressInfo(String name, String phone,String area, String address,
			boolean selected, int AddressId) {
		super();
		this.AddressId = AddressId;
		this.name = name;
		this.phone = phone;
		this.area=area;
		this.address = address;
		this.selected = selected;
	}

	public int getAddressId() {
		return AddressId;
	}

	public void setAddressId(int addressId) {
		AddressId = addressId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
