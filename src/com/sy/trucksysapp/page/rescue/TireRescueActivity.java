package com.sy.trucksysapp.page.rescue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.amap.api.location.AMapLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.SelectModel;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.map.ServiceMapActivity;
import com.sy.trucksysapp.page.service.ServiceAdapter;
import com.sy.trucksysapp.page.service.ServicesDetailActivity;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.LineTextListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.sy.trucksysapp.widget.ShopTopBar;
import com.sy.trucksysapp.widget.ShoptoolSelectedListener;
import com.sy.trucksysapp.widget.SpinerPopwindow;
import com.sy.trucksysapp.widget.AbstractSpinerAdapter.IOnItemSelectListener;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;

/**
 * 轮胎救援
 * 
 * @author Administrator 2015-11-10
 * 
 * 
 */
public class TireRescueActivity extends CurrentLocationActivity implements
		ShoptoolSelectedListener, OnRefreshListener, OnClickListener,
		LoacationChangeListener {
	private ShopTopBar toolbar;
	private Context context;
	private GoodsSelectPopWindow areaprovince;;
	private com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;
	private ArrayList<SelectModel> selectvalue;
	private SpinerPopwindow sortPopWindow, typePopWindow, servicePopWindow;
	private PullToRefreshListView mPullRefreshListView;
	private ServiceAdapter serviceAdapter;
	private ListView mListView;
	private ArrayList<HashMap<String, String>> datalist;
	private int pageSize = 10, currPage = 1, datasize = 0, position = 0;
	private String selectprovince = "", selectcity = "", Distince = "",
			service = "3", type = "0", sort = "0";
	private String MemberId;
    private ImageView topbase_back;
    private RelativeLayout rl_top_right;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serviceshop_list);
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.getReLoadImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.dismiss();
				executeList(true);
			}
		});
		setLoacationChangeListener(this);
		initViews();
		initSelectview();
		toolbar.hidden(2);
		toolbar.hidden(3);
		toolbar.setheadtext("区域", "排序", "", "类型");
	}
	private void initSelectview() {
		toolbar = (ShopTopBar) findViewById(R.id.toolbar);
		toolbar.setSelectedListener(this);
		selectvalue = new ArrayList<SelectModel>();
		selectvalue.add(new SelectModel("Sort", "排序", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("type", "类型", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("service", "服务", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> ll = selectvalue.get(0)
				.getDatadetail();
		HashMap<String, Object> mmp = new HashMap<String, Object>();
		mmp.put("Name", "附近优先");
		mmp.put("val", "0");
		ll.add(mmp);
		mmp = new HashMap<String, Object>();
		mmp.put("Name", "评分最高");
		mmp.put("val", "1");
		ll.add(mmp);
		ArrayList<HashMap<String, Object>> lt = selectvalue.get(1)
				.getDatadetail();
		HashMap<String, Object> mmpatype = new HashMap<String, Object>();
		mmpatype.put("Name", "全部");
		mmpatype.put("val", "0");
		lt.add(mmpatype);

		HashMap<String, Object> Brandmap = new HashMap<String, Object>();
		Brandmap.put("SeTy_Id", "0");
		Brandmap.put("Name", "全部");
		selectvalue.get(2).getDatadetail().add(Brandmap);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("Name",
				context.getResources().getString(R.string.service_garage));
		map.put("SeTy_Id", "2");
		selectvalue.get(2).getDatadetail().add(map);
		map = new HashMap<String, Object>();
		map.put("Name", context.getResources().getString(R.string.service_4s));
		map.put("SeTy_Id", "1");
		selectvalue.get(2).getDatadetail().add(map);
//		map = new HashMap<String, Object>();
//		map.put("Name",
//				context.getResources().getString(R.string.service_recompany));
//		map.put("SeTy_Id", "3");
//		selectvalue.get(2).getDatadetail().add(map);
	}
	@SuppressLint("SimpleDateFormat")
	private void executeList(final Boolean withcondition) {
		if (location == null) {
			startLocation();
			return;
		}
		double x = 0.0;
		double y = 0.0;
		x = location.getLongitude();
		y = location.getLatitude();
		if (withcondition) {
			currPage = 1;
			mPullRefreshListView.setHasMoreData(true);
			loading.show();
		} else {
			if (datalist.size() != 0) {
				currPage = ((int) Math.floor(datalist.size() / pageSize)) + 1;
			} else
				currPage = 1;
		}
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetServiceList";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("Longitude", x + "");
		RequestParams.put("Latitude", y + "");
		RequestParams.put("Province", selectprovince);
		RequestParams.put("City", selectcity);
		RequestParams.put("Distince", Distince);
		RequestParams.put("ordertrun", sort);
		RequestParams.put("Type", type);
		RequestParams.put("ServiceType", service);
		RequestParams.put("Member_Id", MemberId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					datasize = json.getInt("total");
					JSONArray rows = json.getJSONArray("rows");
					for (int i = 0; i < rows.length(); i++) {
						JSONObject detail = rows.getJSONObject(i);
						if(detail.getInt("ServiceType")==3){
							HashMap<String, String> map = CommonUtils
									.getMap(detail);
							data.add(map);	
						}
						
					}
					if (withcondition) {
						datalist.clear();
					}
					datalist.addAll(data);
					currPage++;
					serviceAdapter.notifyDataSetChanged();
					mPullRefreshListView.onPullUpRefreshComplete();
					if (datalist.size() == datasize) {
						mPullRefreshListView.setHasMoreData(false);
					}
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					mPullRefreshListView.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
				} catch (Exception e) {
				}
				if (withcondition) {
					if(datalist.size()==0){
						loading.setEmpty();
					}else{
						loading.dismiss();
					}
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
				if (withcondition) {
					loading.setFail();
				}else{
					mPullRefreshListView.onPullUpRefreshComplete();
					CommonUtils.showToast(context, arg1);
				}
			}
		});
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		context = TireRescueActivity.this;
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("轮胎救援");
		topbase_back = (ImageView)findViewById(R.id.topbase_back);
		topbase_back.setVisibility(View.VISIBLE);
		datalist = new ArrayList<HashMap<String, String>>();
		serviceAdapter = new ServiceAdapter(context, datalist);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(false);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setAdapter(serviceAdapter);
		mPullRefreshListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				Intent intent = new Intent(context,
						ServicesDetailActivity.class);
				intent.putExtra("rowdata", datalist.get(arg2));
				startActivityForResult(intent, 100);
			}
		});
		serviceAdapter.notifyDataSetChanged();
		rl_top_right = (RelativeLayout) findViewById(R.id.rl_top_right);
		rl_top_right.setOnClickListener(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == 100) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) data
						.getSerializableExtra("rowdata");
				datalist.set(position, map);
				serviceAdapter.notifyDataSetChanged();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void areaclick(View mBtnTabs) {
		// 区域点击 显示区域选择
		if (areaprovince == null) {
			// areaprovince = new GoodsSelectPopWindow();
			View v = LayoutInflater.from(context).inflate(
					R.layout.area_select_lay, null);
			provincelistview = (ListView) v.findViewById(R.id.listview);
			citylistview = (ListView) v.findViewById(R.id.listview1);
			com.wheelselect.lib.AreaData areadata = new AreaData(context);
			List<ProvinceModel> provinceList = areadata.getProvinceList();
			selprovincelist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> mapall = new HashMap<String, Object>();
			mapall.put("Name", "全部");
			ArrayList<HashMap<String, Object>> itemlist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> itemmap1 = new HashMap<String, Object>();
			itemmap1.put("Name", "全部");
			itemmap1.put("val", "");
			itemlist.add(itemmap1);
			mapall.put("city", itemlist);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Name", "附近");
			itemlist = new ArrayList<HashMap<String, Object>>();
			itemmap1 = new HashMap<String, Object>();
			itemmap1.put("Name", "10公里");
			itemmap1.put("val", "10");
			itemlist.add(itemmap1);
			HashMap<String, Object> itemmap2 = new HashMap<String, Object>();
			itemmap2.put("Name", "50公里");
			itemmap2.put("val", "50");
			itemlist.add(itemmap2);
			map.put("city", itemlist);
			selprovincelist.add(mapall);
			selprovincelist.add(map);
			mapall = null;
			map = null;
			for (int i = 0; i < provinceList.size(); i++) {
				HashMap<String, Object> provancemap = new HashMap<String, Object>();
				provancemap.put("Name", provinceList.get(i).getName());
				List<CityModel> citylist = provinceList.get(i).getCityList();
				ArrayList<HashMap<String, Object>> itemcitylist = new ArrayList<HashMap<String, Object>>();
				for (int j = 0; j < citylist.size(); j++) {
					HashMap<String, Object> citymap = new HashMap<String, Object>();
					citymap.put("Name", citylist.get(j).getName());
					itemcitylist.add(citymap);
				}
				provancemap.put("city", itemcitylist);
				selprovincelist.add(provancemap);
			}

			provinceadpter = new ProvinceAdapter(context, selprovincelist);
			provincelistview.setAdapter(provinceadpter);
			cityitemlist = new ArrayList<HashMap<String, Object>>();
			cityitemlist
					.addAll((ArrayList<HashMap<String, Object>>) selprovincelist
							.get(0).get("city"));
			cityadpter = new CityAdapter(context, cityitemlist);
			citylistview.setAdapter(cityadpter);// selectprovince
			citylistview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (provinceadpter.getSelectedPosition() == 1
							|| provinceadpter.getSelectedPosition() == 0) {
						selectprovince = "";
						selectcity = "";
						Distince = cityitemlist.get(arg2).get("val").toString();
					} else {
						Distince = "";
						selectprovince = selprovincelist
								.get(provinceadpter.getSelectedPosition())
								.get("Name").toString();
						selectcity = cityitemlist.get(arg2).get("Name")
								.toString();
					}
					areaprovince.dismiss();
					if (location != null) {
						executeList(true);
					}
				}
			});
			provincelistview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					provinceadpter.setSelectedPosition(position);
					cityitemlist.clear();
					cityitemlist
							.addAll((ArrayList<HashMap<String, Object>>) selprovincelist
									.get(position).get("city"));
					provinceadpter.notifyDataSetChanged();
					cityadpter.notifyDataSetChanged();

				}
			});
			areaprovince = new GoodsSelectPopWindow(context, v,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		areaprovince.showAsDropDownleft(mBtnTabs, 0, 0);
	}

	@Override
	public void sortclick(View mBtnTabs) {
		if (sortPopWindow == null) {
			sortPopWindow = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(0).getDatadetail(), context);
			sortPopWindow.setAdapter(lineTextListAdapter);
			sortPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					HashMap<String, Object> row = selectvalue.get(0)
							.getDatadetail().get(pos);
					sort = row.get("val").toString();
					executeList(true);
				}
			});
		}
		sortPopWindow.showAsDropDown(mBtnTabs);
	}

	@Override
	public void typeclick(View mBtnTabs) {
		if (typePopWindow == null) {
			typePopWindow = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(1).getDatadetail(), context);
			typePopWindow.setAdapter(lineTextListAdapter);
			typePopWindow.setItemListener(new IOnItemSelectListener() {
				public void onItemClick(int pos) {
					HashMap<String, Object> row = selectvalue.get(1)
							.getDatadetail().get(pos);
					type = row.get("val").toString();
					executeList(true);
				}
			});
		}
		typePopWindow.showAsDropDown(mBtnTabs);
	}

	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
	}

	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		if (location != null) {
			executeList(false);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_top_right:
			startActivity(new Intent(context, TireRescueMapActivity.class));
			break;
		default:
			break;
		}
	}

	public void onChange(AMapLocation location) {
		executeList(true);
	}

	public void onFailChange() {
		if(location==null){
			CommonUtils.showToast(context, "获取位置信息失败！");
			loading.setFail();
			return;
		}
	}

	public void serviceclick(View mBtnTabs) {
		if (servicePopWindow == null) {
			servicePopWindow = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(2).getDatadetail(), context);
			servicePopWindow.setAdapter(lineTextListAdapter);
			servicePopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					service = selectvalue.get(2).getDatadetail().get(pos)
							.get("SeTy_Id").toString();
					executeList(true);
				}
			});
		}
		servicePopWindow.showAsDropDown(mBtnTabs);

	}
}