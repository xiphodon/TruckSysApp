package com.sy.trucksysapp.page.driver;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.FollowService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.DrivingRouteOverlay;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.SerializableMap;

/**
 * 停车场详情页面
 * 
 * @author lxs 20150601
 * 
 */
public class ParkDetailActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnMarkerClickListener, OnMapLoadedListener,
		OnClickListener, OnRouteSearchListener {

	private LinearLayout ll_info_windows;
	private ImageView topbase_back, follow;
	private MapView mapView;
	private AMap aMap;
	private Button btn_call, btn_navigation;
	private SerializableMap serializableMap;
	private LatLng parkLoation;
	private LatLng MyLocation;
	// 路径规划
	private RouteSearch routeSearch;
	private LatLonPoint startPoint = null;
	private LatLonPoint endPoint = null;
	private DriveRouteResult driveRouteResult;// 驾车模式查询结果
	private TextView tv_name, tv_address;
	Marker marker1;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	Boolean autodriveRouteResult = false;
	DrivingRouteOverlay drivingRouteOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_park_detail);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		initView();
	}

	private void initView() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			serializableMap = (SerializableMap) bundle.get("parkInfo");
			parkLoation = new LatLng(Double.valueOf(serializableMap.getMap()
					.get("Park_Latitude")), Double.valueOf(serializableMap
					.getMap().get("Park_Longitude")));
			MyLocation = new LatLng(bundle.getDouble("Latitude"),
					bundle.getDouble("Longitude"));
		}
		follow = (ImageView) findViewById(R.id.follow);
		if (!CommonUtils.getString(serializableMap.getMap(), "Favo_CreateDate")
				.equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		follow.setOnClickListener(this);
		ll_info_windows = (LinearLayout) findViewById(R.id.ll_info_windows);
		topbase_back = (ImageView) findViewById(R.id.topbase_back);
		topbase_back.setOnClickListener(this);
		btn_call = (Button) findViewById(R.id.btn_call);
		btn_call.setOnClickListener(this);
		btn_navigation = (Button) findViewById(R.id.btn_navigation);
		btn_navigation.setOnClickListener(this);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_address = (TextView) findViewById(R.id.tv_address);
		addMarkersToMap();// 往地图上添加marker
		aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		tv_name.setText(serializableMap.getMap().get("Park_ShopName"));
		tv_address.setText(serializableMap.getMap().get("Park_Address"));
		AMapLocation location = new AMapLocation(LocationProviderProxy.AMapNetwork);
		location.setLatitude(MyLocation.latitude);
		location.setLongitude(MyLocation.longitude);
		mListener.onLocationChanged(location);// 显示系统小蓝点
	}

	private void setUpMap() {
		// TODO Auto-generated method stub
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

	private void addMarkersToMap() {
		marker1 = aMap.addMarker(new MarkerOptions()
				.perspective(true)
				.position(parkLoation)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
	}

	@Override
	public boolean onMarkerClick(Marker mark) {
		// TODO Auto-generated method stub
		if (mark.equals(marker1)) {
			if (aMap != null) {
				jumpPoint(mark);
			}
		}
		if (ll_info_windows.getVisibility() == View.GONE) {
			ll_info_windows.setVisibility(View.VISIBLE);
		} else {
			ll_info_windows.setVisibility(View.GONE);
		}
		return true;
	}

	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkLoation, 14));
		ll_info_windows.setVisibility(View.VISIBLE);
	}

	/**
	 * marker点击时跳动一下
	 */
	public void jumpPoint(final Marker marker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = aMap.getProjection();
		Point startPoint = proj.toScreenLocation(parkLoation);
		startPoint.offset(0, -100);
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 1500;

		final Interpolator interpolator = new BounceInterpolator();
		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * parkLoation.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * parkLoation.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));
				if (t < 1.0) {
					handler.postDelayed(this, 16);
				}
			}
		});
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult() {
		try {
			startPoint = new LatLonPoint(MyLocation.latitude,
					MyLocation.longitude);

			endPoint = new LatLonPoint(parkLoation.latitude,
					parkLoation.longitude);

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

	public void finish() {
		if (FollowService.isFollowed()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("rowdata", serializableMap.getMap());
			intent.putExtras(bundle);
			setResult(100, intent);
		}
		super.finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.topbase_back:// 返回
			finish();
			break;
		case R.id.follow:// 收藏
			FollowService.follow(ParkDetailActivity.this,
					serializableMap.getMap(), "Park_Id", "Parking", follow);
			break;
		case R.id.btn_call:// 电话预约
			// 用intent启动拨打电话
			try {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ serializableMap.getMap().get("Park_Mobile")));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case R.id.btn_navigation:// 去这里
			autodriveRouteResult = true;
			searchRouteResult();
			ll_info_windows.setVisibility(View.GONE);
			break;
		default:
			break;
		}

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
				if (drivingRouteOverlay != null) {
					drivingRouteOverlay.removeFromMap();
				}
				marker1.remove();
				drivingRouteOverlay = new DrivingRouteOverlay(this, aMap,
						drivePath, driveRouteResult.getStartPos(),
						driveRouteResult.getTargetPos());
				drivingRouteOverlay.addToMap();
				// drivingRouteOverlay.zoomToSpan();
			} else {
				CommonUtils.showToast(ParkDetailActivity.this, "未查询到结果");
			}
		} else if (rCode == 27) {
			CommonUtils.showToast(ParkDetailActivity.this, "网络错误");
		} else if (rCode == 32) {
			CommonUtils.showToast(ParkDetailActivity.this, "无效KEY值");
		} else {
			CommonUtils.showToast(ParkDetailActivity.this, "未知错误" + rCode);
		}
	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

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
		if (mListener != null && location != null) {
			if (location.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(location);// 显示系统小蓝点
				MyLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				if (autodriveRouteResult) {
					searchRouteResult();
				}
			}
		}
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
}
