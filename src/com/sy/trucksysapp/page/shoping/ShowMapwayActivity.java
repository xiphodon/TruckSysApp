package com.sy.trucksysapp.page.shoping;

import java.util.HashMap;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.DrivingRouteOverlay;

public class ShowMapwayActivity extends BaseActivity implements LocationSource,
		OnRouteSearchListener, AMapLocationListener, OnMapLoadedListener {
	private AMap aMap;
	private MapView mapView;
	private RouteSearch routeSearch;
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	HashMap<String, String> rowdata = null;
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	public AMapLocation location;
	private LocationManagerProxy mAMapLocationManager;
	private OnLocationChangedListener mListener;
	DrivingRouteOverlay drivingRouteOverlay;
	private Dialog progDialog = null;// 搜索时进度条

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.activity_mapway);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		rowdata = (HashMap<String, String>) getIntent().getSerializableExtra(
				"rowdata");
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("驾车详情");
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		showProgressDialog();
	}

	private void setUpMap() {
		// TODO Auto-generated method stub
		// 自定义系统定位小蓝点

		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);// 设置定位类型
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult() {
		try {
			startPoint = new LatLonPoint(location.getLatitude(),
					location.getLongitude());
			if (rowdata != null) {
				endPoint = new LatLonPoint(Double.valueOf(rowdata
						.get("Serv_Latitude")), Double.valueOf(rowdata
						.get("Serv_Longitude")));
			}
		} catch (Exception e) {

		}
		if (startPoint != null && endPoint != null) {
			final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
					startPoint, endPoint);
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo,
					RouteSearch.DrivingDefault, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				// aMap.clear();// 清理地图上的所有覆盖物
				if (drivingRouteOverlay != null) {
					drivingRouteOverlay.removeFromMap();
				}
				drivingRouteOverlay = new DrivingRouteOverlay(this, aMap,
						drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.addToMap();
				// drivingRouteOverlay.zoomToSpan();
			} else {
				CommonUtils.showToast(ShowMapwayActivity.this, "未查询到结果");
			}
		} else if (rCode == 27) {
			CommonUtils.showToast(ShowMapwayActivity.this, "网络错误");
		} else if (rCode == 32) {
			CommonUtils.showToast(ShowMapwayActivity.this, "无效KEY值");
		} else {
			CommonUtils.showToast(ShowMapwayActivity.this, "未知错误" + rCode);
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
		}
	}
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在获取位置信息...");
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
	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if(location!=null){
			dissmissProgressDialog();
		}
		if (mListener != null && location != null) {
			if (location.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(location);// 显示系统小蓝点
				this.location = location;
				searchRouteResult();
			}
		}
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		if (location != null)
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 14));
	}

}
