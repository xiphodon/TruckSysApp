package com.sy.trucksysapp.entity;

/****
 * 购物券
 * @author Administrator
 *
 */
public class CouponModel {
	private int coupon_id;
	private Double Coup_Money;
	private String Coup_StartTime;
	private String Coup_EndTime;
	private   Boolean  selected ;
	private String coupon_desc;
	private int Coup_State;
	
	
	
	public int getCoupon_id() {
		return coupon_id;
	}
	public void setCoupon_id(int coupon_id) {
		this.coupon_id = coupon_id;
	}
	public Double getCoup_Money() {
		return Coup_Money;
	}
	public void setCoup_Money(Double coup_Money) {
		Coup_Money = coup_Money;
	}
	public String getCoup_StartTime() {
		return Coup_StartTime;
	}
	public void setCoup_StartTime(String coup_StartTime) {
		Coup_StartTime = coup_StartTime;
	}
	public String getCoup_EndTime() {
		return Coup_EndTime;
	}
	public void setCoup_EndTime(String coup_EndTime) {
		Coup_EndTime = coup_EndTime;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getCoupon_desc() {
		return coupon_desc;
	}
	public void setCoupon_desc(String coupon_desc) {
		this.coupon_desc = coupon_desc;
	}
	public int getCoup_State() {
		return Coup_State;
	}
	public void setCoup_State(int coup_State) {
		Coup_State = coup_State;
	}
	
	
}
