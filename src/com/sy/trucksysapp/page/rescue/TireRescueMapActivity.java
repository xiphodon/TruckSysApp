package com.sy.trucksysapp.page.rescue;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 轮胎救援地图页面
 * 
 * @author Administrator 2015-11-10
 * 
 * 
 */
public class TireRescueMapActivity extends BaseActivity implements
		LocationSource, AMapLocationListener, OnCameraChangeListener,
		InfoWindowAdapter, OnMapLoadedListener, OnMapClickListener {

	private Context context;
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private int pageSize = 10;
	private int currPage = 1;
	private AMapLocation aLocation;
	private Boolean loadstatus = false;
	private Dialog progDialog = null;// 搜索时进度条
	private Marker currentMarket;
	private String MemberId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		setContentView(R.layout.servicemap_activity);
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("附近轮胎救援点");
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在搜索周边...");
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

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	public void drawMarkers(ArrayList<HashMap<String, String>> datalist) {
		ArrayList<LatLng> shoplist = new ArrayList<LatLng>();
		int[] icons = new int[] { 0, R.drawable.service_4s,
				R.drawable.service_garage, R.drawable.service_recompany };
		for (HashMap<String, String> map : datalist) {
			Double lat = Double.valueOf(map.get("FsSh_Latitude"));
			Double longit = Double.valueOf(map.get("FsSh_Longitude"));
			LatLng latlng = new LatLng(lat, longit);
			shoplist.add(latlng);
			aMap.addMarker(
					new MarkerOptions()
							.position(latlng)
							.title(map.get("FsSh_ShopName"))
							.snippet(map.get("FsSh_Address"))
							.icon(BitmapDescriptorFactory
									.fromResource(icons[Integer.parseInt(map
											.get("ServiceType"))]))).setObject(
					map);
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
		// MyLocationStyle myLocationStyle = new MyLocationStyle();
		// myLocationStyle.myLocationIcon(BitmapDescriptorFactory
		// .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
		// myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
		// myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
		// // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
		// myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
		// aMap.setMyLocationStyle(myLocationStyle);
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位类型
		aMap.setOnCameraChangeListener(this);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				currentMarket = arg0;
				arg0.showInfoWindow();
				return true;
			}
		});
		aMap.setInfoWindowAdapter(this);
		aMap.setOnMapClickListener(this);
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
		deactivate();
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
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			this.aLocation = aLocation;
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
		}
	}

	/**
	 * 激活定位
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		if (aLocation == null || loadstatus) {
			return;
		}
		double x = aLocation.getLongitude();
		double y = aLocation.getLatitude();
		loadstatus = true;
		showProgressDialog();
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetServiceList";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("Longitude", x + "");
		RequestParams.put("Latitude", y + "");
		RequestParams.put("Province", "");
		RequestParams.put("City", "");
		RequestParams.put("Distince", "");
		RequestParams.put("ordertrun", "1");
		RequestParams.put("Type", "0");
		RequestParams.put("ServiceType", "3");
		RequestParams.put("Member_Id", MemberId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					JSONArray rows = json.getJSONArray("rows");
					for (int i = 0; i < rows.length(); i++) {
						JSONObject detail = rows.getJSONObject(i);
						if(detail.getInt("ServiceType")==3){
							HashMap<String, String> map = CommonUtils
									.getMap(detail);
							data.add(map);
						}
					}
					if (rows.length() >= 10) {
						currPage++;
					}
					drawMarkers(data);
					loadstatus = false;
				} catch (Exception e) {
				}
				dissmissProgressDialog();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				loadstatus = false;
				dissmissProgressDialog();
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	@SuppressLint("NewApi")
	private void render(Marker marker, View view) {
		TextView type = (TextView) view.findViewById(R.id.type);
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView phone = (TextView) view.findViewById(R.id.phone);
		TextView address = (TextView) view.findViewById(R.id.address);
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) marker
				.getObject();
		String serviceType = map.get("ServiceType");
		String text = "4S";
		int color = 0;
		int bg = 0;
		if (serviceType.equals("1")) {
			text = "4S";
			color = R.color.service_4s;
			bg = R.drawable.border_4s;
		} else if (serviceType.equals("2")) {
			text = "修";
			color = R.color.service_garage;
			bg = R.drawable.border_garage;
		} else if (serviceType.equals("3")) {
			text = "援";
			color = R.color.type5;
			bg = R.drawable.border_5_nocorner;
		}
		type.setTextColor(getResources().getColor(color));
		type.setBackground(getResources().getDrawable(bg));
		type.setText(text);
		title.setText(marker.getTitle());
		phone.setText(map.get("FsSh_Mobile"));
		address.setText(map.get("FsSh_Address"));
	}

	public void onCameraChange(CameraPosition arg0) {

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		doSearchQuery();
	}

	public View getInfoContents(Marker arg0) {
		View view = getLayoutInflater().inflate(R.layout.base_map_info, null);
		render(arg0, view);
		return view;
	}

	public View getInfoWindow(Marker arg0) {
		return null;
	}

	public void onMapLoaded() {
		// doSearchQuery();
	}

	public void onMapClick(LatLng arg0) {
		if (currentMarket != null)
			currentMarket.hideInfoWindow();
	}

}
