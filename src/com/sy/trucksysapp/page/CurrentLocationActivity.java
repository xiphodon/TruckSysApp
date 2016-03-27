package com.sy.trucksysapp.page;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;

public class CurrentLocationActivity extends BaseActivity implements Runnable{

	private LocationManagerProxy mAMapLocationManager;
	private AMap amMap;
	public AMapLocation location;
	private LoacationChangeListener listenner;
	private Handler locationhandler = new Handler();
	private Dialog progDialog = null;// 搜索时进度条

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		showProgressDialog();
		getLocation();
	}
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在获取当前位置...");
		progDialog.show();
	}
	
	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
	
	public void getLocation() {
		amMap = new AMap();
		mAMapLocationManager = LocationManagerProxy.getInstance(this);
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, amMap);
		locationhandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}

	public void setLoacationChangeListener(LoacationChangeListener listenner) {
		this.listenner = listenner;
	}
	class AMap implements AMapLocationListener {

		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		// 获取经纬度
		@Override
		public void onLocationChanged(AMapLocation clocation) {
			location = clocation;
			stopLocation();
			listenner.onChange(clocation);
		}
	}
	/**
	 * 销毁定位
	 */
	public void startLocation() {
		if(mAMapLocationManager==null){
			showProgressDialog();
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 2000, 10, amMap);
			locationhandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		}
	}
	
	
	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		locationhandler.removeCallbacks(this);
		dissmissProgressDialog();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(amMap);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (location == null) {
			listenner.onFailChange();
			stopLocation();// 销毁掉定位
		}
	}
}
