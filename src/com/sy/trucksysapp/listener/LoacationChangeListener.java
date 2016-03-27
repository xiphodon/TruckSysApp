package com.sy.trucksysapp.listener;

import com.amap.api.location.AMapLocation;


public interface LoacationChangeListener {
	/**
	 * 按价格升排序
	 * @param mBtnTabs 
	 */
	public abstract void onChange(AMapLocation location);
	
	public abstract void onFailChange();

}
