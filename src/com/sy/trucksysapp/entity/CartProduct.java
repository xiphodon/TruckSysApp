package com.sy.trucksysapp.entity;

import java.io.Serializable;

public class CartProduct implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/***
	 * 产品编码
	 */
	private String productId;
	private String ProductType;// 产品类型1轮胎2内胎3轮辋4润滑油5其他
	private String SellerType;// 供应商类型1经销商2厂商3服务商
	private String SellerId;// 供应商id
	private String IsSale;// 是否特卖 1特卖0非特卖
	private int productcount;// 产品库存
	private boolean ismax = false;
	/**
	 * 是否被选中
	 */
	private boolean isCheck;
	/**
	 * 商品数量
	 */
	private int count;

	/**
	 * 商品参数
	 */
	private String parameters;
	/**
	 * 商品单价
	 */
	private Double sum;
	/**
	 * 名称
	 */
	private String fname;
	/**
	 * 图片URL
	 */
	private String imgsrc;

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getImgsrc() {
		return imgsrc;
	}

	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getProductType() {
		return ProductType;
	}

	public void setProductType(String productType) {
		ProductType = productType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSellerType() {
		return SellerType;
	}

	public void setSellerType(String sellerType) {
		SellerType = sellerType;
	}

	public String getSellerId() {
		return SellerId;
	}

	public void setSellerId(String sellerId) {
		SellerId = sellerId;
	}

	public String getIsSale() {
		return IsSale;
	}

	public void setIsSale(String isSale) {
		IsSale = isSale;
	}

	public int getProductcount() {
		return productcount;
	}

	public void setProductcount(int productcount) {
		this.productcount = productcount;
	}

	public boolean isIsmax() {
		return ismax;
	}

	public void setIsmax(boolean ismax) {
		this.ismax = ismax;
	}

}
