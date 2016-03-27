package com.sy.trucksysapp.page.map;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;

public class ParkMapActivity extends BaseActivity implements LocationSource,
		AMapLocationListener, OnCameraChangeListener, InfoWindowAdapter {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private int pageSize = 10;
	private int currPage = 0;
	private AMapLocation aLocation;
	private Boolean loadstatus = false;
	private Dialog progDialog = null;// 搜索时进度条
	private int currentcount = 0;
	private int total = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basicmap_activity);
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("附近停车场");
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.tv_title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(16);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.tv_snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
					snippetText.length(), 0);
			snippetUi.setTextSize(14);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在搜索附近停车场...");
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
		for (int i = 0; i < datalist.size(); i++) {
			Double lat = Double.valueOf(datalist.get(i).get("Park_Latitude"));
			Double longit = Double.valueOf(datalist.get(i)
					.get("Park_Longitude"));
			LatLng latlng = new LatLng(lat, longit);
			shoplist.add(latlng);
		}
		for (int i = 0; i < shoplist.size(); i++) {
			aMap.addMarker(new MarkerOptions()
					.position(shoplist.get(i))
					.title(datalist.get(i).get("Park_ShopName"))
					.snippet(datalist.get(i).get("Park_Address"))
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.icon_mapparking)));
		}

	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		// 自定义系统定位小蓝点
//		MyLocationStyle myLocationStyle = new MyLocationStyle();
//		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//				.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
//		myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
//		myLocationStyle.radiusFillColor(Color.TRANSPARENT);// 设置圆形的填充颜色
//		// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
//		myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
//		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位类型
		aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
		aMap.setOnCameraChangeListener(this);
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				arg0.showInfoWindow();
				return true;
			}
		});
		// aMap.setMyLocationType()
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
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(
					LocationProviderProxy.AMapNetwork, 60*1000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
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
		if (aLocation == null || loadstatus || currentcount == total) {
			return;
		}
		loadstatus = true;
		showProgressDialog();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetParking";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("Longitude", aLocation.getLongitude() + "");
		RequestParams.put("Latitude", aLocation.getLatitude() + "");
		RequestParams.put("Province", "");
		RequestParams.put("City", "");
		RequestParams.put("Distince", "");
		RequestParams.put("type", "");
		RequestParams.put("pricesort", "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					total = json.getInt("total");
					JSONArray rows = json.getJSONArray("rows");
					for (int i = 0; i < rows.length(); i++) {
						JSONObject detail = rows.getJSONObject(i);
						HashMap<String, String> map = CommonUtils
								.getMap(detail);
						data.add(map);
					}
					drawMarkers(data);
					currentcount += data.size();
					currPage++;
					loadstatus = false;
				} catch (Exception e) {
					// TODO: handle exception
				}
				dissmissProgressDialog();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				loadstatus = false;
				dissmissProgressDialog();
				super.onFailure(arg0, arg1);
			}
		});
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		doSearchQuery();
	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		// View infoContent = getLayoutInflater().inflate(
		// R.layout.bg_mapwindow, null);
		// render(marker, infoContent);
		return null;
	}

	/**
	 * 监听自定义infowindow窗口的infowindow事件回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(R.layout.bg_mapwindow,
				null);
		render(marker, infoWindow);
		return infoWindow;
	}

}
