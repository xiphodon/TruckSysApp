package com.sy.trucksysapp.page;

import org.apache.cordova.api.LOG;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class UploadlocationService extends Service {

	private LocationManagerProxy mAMapLocationManager;
	private AMap amMap;
	private int DELYED = 10000;
	Handler handler;
	Runnable runnable;
	private Handler locationhandler = new Handler();
	private Runnable locationrunnable;
	public AMapLocation location;
	int i = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}

	public void getlocation() {
		amMap = new AMap();
		mAMapLocationManager = LocationManagerProxy.getInstance(this);
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, -1, 10, amMap);
		locationhandler.postDelayed(locationrunnable, 12000);// 设置超过12秒还没有定位到就停止定位
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
			// 获取位置成功
			location = clocation;
			stopLocation();
			uploaddata();
		}
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				try {
					getlocation();
					handler.postDelayed(this, DELYED);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		locationrunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (location == null) {
					stopLocation();// 销毁掉定位
				}
			}
		};
		
		handler.postDelayed(runnable, DELYED);
		 return START_NOT_STICKY;
	}
	public void uploaddata() {
		
		location = null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			handler.removeCallbacks(runnable);
			if (mAMapLocationManager != null) {
				stopLocation();
			}
		} catch (Exception ex) {

		}
		super.onDestroy();
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		locationhandler.removeCallbacks(locationrunnable);
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(amMap);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
}
