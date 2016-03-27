package com.sy.trucksysapp.page.driver;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyTrafficStyle;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.TrafficModel;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.utils.CommonUtils;

/**
 * 高速路况的显示
 * 
 * @author Administrator
 * 
 */
public class HighwayConditionActivity extends CurrentLocationActivity implements LoacationChangeListener{
	private AMap aMap;
	private MapView mapView;
	private TextView tv_selarea;
	private List<TrafficModel> list = new ArrayList<TrafficModel>();
	private Dialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_way_condition);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		setLoacationChangeListener(this);
		tv_selarea = (TextView) findViewById(R.id.tv_selarea);
		tv_selarea.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.show();
			}
		});
		initarea();
		init();
	}


	private void initarea() {
		// TODO Auto-generated method stub
		list.clear();
		list.add(new TrafficModel("北京市", 116.397428, 39.90923, 12));
		list.add(new TrafficModel("天津市", 117.19116211, 39.13006024, 10));
		list.add(new TrafficModel("河北省", 114.47753906, 38.06539235, 10));
		list.add(new TrafficModel("山西省", 112.54394531, 37.85750716, 10));
		list.add(new TrafficModel("内蒙古自治区", 111.66503906, 40.83043688, 10));
		list.add(new TrafficModel("辽宁省", 123.42041016, 41.80407814, 10));
		list.add(new TrafficModel("吉林省", 125.33203125, 43.8820573, 10));
		list.add(new TrafficModel("黑龙江省", 126.65039063, 45.75219336, 10));
		list.add(new TrafficModel("上海市", 121.46484375, 31.24098538, 10));
		list.add(new TrafficModel("江苏省", 118.76220703, 32.02670629, 10));
		list.add(new TrafficModel("浙江省", 120.14648438, 30.29701788, 10));
		list.add(new TrafficModel("安徽省", 117.31201172, 31.87755764, 10));
		list.add(new TrafficModel("福建省", 119.31152344, 26.09625491, 10));
		list.add(new TrafficModel("江西省", 115.88378906, 28.69058765, 10));
		list.add(new TrafficModel("山东省", 117.00439453, 36.68604128, 10));
		list.add(new TrafficModel("河南省", 113.66455078, 34.7777158, 10));
		list.add(new TrafficModel("湖北省", 114.30175781, 30.60009387, 10));
		list.add(new TrafficModel("湖南省", 112.98339844, 28.2076086, 10));
		list.add(new TrafficModel("广东省", 113.29101563, 23.14035999, 10));
		list.add(new TrafficModel("广西壮族自治区", 108.32519531, 22.81669413, 10));
		list.add(new TrafficModel("海南省", 110.32470703, 20.05593127, 10));
		list.add(new TrafficModel("重庆市", 106.47949219, 29.51611039, 10));
		list.add(new TrafficModel("四川省", 104.0625, 30.65681556, 10));
		list.add(new TrafficModel("贵州省", 106.72119141, 26.56887655, 10));
		list.add(new TrafficModel("云南省", 102.72216797, 25.04579224, 10));
		list.add(new TrafficModel("西藏自治区", 91.14257813, 29.66896253, 10));
		list.add(new TrafficModel("陕西省", 108.94042969, 34.28899187, 10));
		list.add(new TrafficModel("甘肃省", 103.82080078, 36.04909896, 10));
		list.add(new TrafficModel("青海省", 101.77734375, 36.59788913, 10));
		list.add(new TrafficModel("宁夏回族自治区", 106.28173828, 38.46219172, 10));
		list.add(new TrafficModel("新疆维吾尔族自治区", 87.62695313, 43.78695837, 10));
		list.add(new TrafficModel("香港特别行政区", 114.16992188, 22.3297523, 10));
		list.add(new TrafficModel("澳门特别行政区", 113.5546875, 22.22809042, 10));
		list.add(new TrafficModel("台湾省", 121.50878906, 25.04579224, 10));
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i).getName();
		}
		Builder builder = new android.app.AlertDialog.Builder(this);
		// 设置对话框的图标
		// 设置对话框的标题
		ListAdapter catalogsAdapter = new ArrayAdapter<String>(
				HighwayConditionActivity.this, R.layout.simple_mlist_itema,
				array);
		builder.setAdapter(catalogsAdapter,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(list.get(which).getLatitude(), list.get(which).getLongitude())));
						tv_selarea.setText(list.get(which).getName());
						aMap.moveCamera(CameraUpdateFactory.zoomTo(list.get(which).getZoomlevel()));
						dialog.dismiss();
					}
				});
		// 创建一个列表对话框
		dialog = builder.create();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
		myTrafficStyle.setSeriousCongestedColor(0xff92000a);
		myTrafficStyle.setCongestedColor(0xffea0312);
		myTrafficStyle.setSlowColor(0xffff7508);
		myTrafficStyle.setSmoothColor(0xff00a209);
		aMap.setMyTrafficStyle(myTrafficStyle);
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



	@Override
	public void onChange(AMapLocation location) {
		// TODO Auto-generated method stub
		boolean ish = false;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getName().contains(location.getProvince())) {
					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude())));
					tv_selarea.setText(list.get(i).getName());
					aMap.moveCamera(CameraUpdateFactory.zoomTo(list.get(i).getZoomlevel()));
					ish = true;
					break;
				}
			if(!ish){
				tv_selarea.setText(location.getProvince());
				aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
				aMap.moveCamera(CameraUpdateFactory.zoomTo(9));
			}
			aMap.setTrafficEnabled(true);
		}
		
		
	}

	@Override
	public void onFailChange() {
		// TODO Auto-generated method stub
		CommonUtils.showToast(this, "获取位置失败！");
	}

}