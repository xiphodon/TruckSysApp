package com.sy.trucksysapp.page.shoping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.SelectModel;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.page.CurrentLocationFragment;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.map.BasicMapActivity;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.LineTextListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ServiceShopAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.sy.trucksysapp.widget.ShopTopBar;
import com.sy.trucksysapp.widget.ShoptoolSelectedListener;
import com.sy.trucksysapp.widget.SpinerPopwindow;
import com.sy.trucksysapp.widget.AbstractSpinerAdapter.IOnItemSelectListener;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 服务商列表
 * 
 * @author
 * 
 */
public class ShopServiceFragment extends CurrentLocationActivity implements
		ShoptoolSelectedListener, OnRefreshListener, OnClickListener,
		LoacationChangeListener {
	private ShopTopBar toolbar;
	private Context context;
	private GoodsSelectPopWindow areaprovince;
	private com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;
	private ArrayList<SelectModel> selectvalue;
	private SpinerPopwindow sortPopWindow, typePopWindow, servicePopWindow;
	private PullToRefreshListView mPullRefreshListView;
	private ServiceShopAdapter serviceAdapter;
	private ListView mListView;
	private ArrayList<HashMap<String, String>> datalist;
	private int pageSize = 10, currPage = 1, datasize = 0;
	private ImageView topbase_shop;
	private String selectprovince = "", selectcity = "", Distince = "",
			service = "", type = "0", sort = "";
	private LayoutInflater inflater;
	Boolean isselect = false;
	private int position;

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		this.inflater = inflater;
//		View view = inflater.inflate(R.layout.activity_serviceshop_list,
//				container, false);
//		return view;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onActivityCreated(savedInstanceState);
//		
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		try {
			isselect = getIntent().getBooleanExtra("isselect", false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		setLoacationChangeListener(this);
		initViews();
		initSelectview();
	}

	private void initSelectview() {
		// TODO Auto-generated method stub
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
		HashMap mmp = new HashMap<String, String>();
		mmp.put("Name", "默认排序");
		mmp.put("val", "0");
		HashMap mmp0 = new HashMap<String, String>();
		mmp0.put("Name", "附近优先");
		mmp0.put("val", "1");
		HashMap mmp1 = new HashMap<String, String>();
		mmp1.put("Name", "评分最高");
		mmp1.put("val", "2");
		ll.add(mmp);
		ll.add(mmp0);
		ll.add(mmp1);
		ArrayList<HashMap<String, Object>> lt = selectvalue.get(1)
				.getDatadetail();
//		HashMap mmpatype = new HashMap<String, String>();
//		mmpatype.put("Name", "全部");
//		mmpatype.put("val", "");

		HashMap mmptype = new HashMap<String, String>();
		mmptype.put("Name", "服务商");
		mmptype.put("val", "0");
		HashMap mmptype0 = new HashMap<String, String>();
		mmptype0.put("Name", "供应商");
		mmptype0.put("val", "1");
//		HashMap mmptype1 = new HashMap<String, String>();
//		mmptype1.put("Name", "等级3");
//		mmptype1.put("val", "2");
//		lt.add(mmpatype);
		lt.add(mmptype);
		if(!isselect){
			lt.add(mmptype0);
		}
//		lt.add(mmptype1);
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetServiceType";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, arg1);
				try {
					JSONObject json = new JSONObject(arg1);
					JSONArray rows = json.getJSONArray("rows");
					HashMap<String, Object> Brandmap = new HashMap<String, Object>();
					Brandmap.put("SeTy_Id", "");
					Brandmap.put("Name", "全部");
					selectvalue.get(2).getDatadetail().add(Brandmap);
					for (int i = 0; i < rows.length(); i++) {
						JSONObject detail = rows.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("SeTy_Id", detail.getString("SeTy_Id"));
						map.put("Name", detail.getString("SeTy_Name"));
						selectvalue.get(2).getDatadetail().add(map);
					}
				} catch (Exception e) {

				}
			}
		});

	}

	private void executeList(final Boolean withcondition) {
		// TODO Auto-generated method stub
		if (location == null) {
			startLocation();
			return;
		}
		if (withcondition) {
			currPage = 1;
			mPullRefreshListView.setHasMoreData(true);
			loading.show();
		}
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetServcieOrDealer";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("Longitude", location.getLongitude() + "");
		RequestParams.put("Latitude", location.getLatitude() + "");
		RequestParams.put("Province", selectprovince);
		RequestParams.put("City", selectcity);
		RequestParams.put("StarSort", sort);
		RequestParams.put("type", type);
		RequestParams.put("Distince", Distince);
		RequestParams.put("service", service);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					datasize = json.getInt("total");
					JSONArray rows = json.getJSONArray("rows");
					for (int i = 0; i < rows.length(); i++) {
						JSONObject detail = rows.getJSONObject(i);
						HashMap<String, String> map = CommonUtils
								.getMap(detail);
						data.add(map);
					}
					if (withcondition) {
						datalist.clear();
					}
					datalist.addAll(data);
					currPage++;
					serviceAdapter.notifyDataSetChanged();
					mPullRefreshListView.onPullUpRefreshComplete();
					if (data.size() == 0) {
						mPullRefreshListView.setHasMoreData(false);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (withcondition) {
					if (datalist.size() == 0) {
						loading.setEmpty();
					} else {
						loading.dismiss();
					}
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				if (withcondition) {
					loading.setFail();
				} else {
					mPullRefreshListView.onPullUpRefreshComplete();
					CommonUtils.showToast(context, arg1);
				}
			}
		});
	}

	private void initViews() {
		// TODO Auto-generated method stub
		context = ShopServiceFragment.this;
		TextView topbase_center_text = (TextView)findViewById(
				R.id.topbase_center_text);
		topbase_center_text.setText("服务商");
		datalist = new ArrayList<HashMap<String, String>>();
		serviceAdapter = new ServiceShopAdapter(context, datalist);
		mPullRefreshListView = (PullToRefreshListView)findViewById(
				R.id.pull_refresh_list);
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
				// TODO Auto-generated method stub
				HashMap<String, String> map = datalist.get(arg2);
				position = arg2;
				if(map.get("SellerType").equals("3")){
					Intent intent = new Intent(context,
							ServicesDetailActivity.class);
					intent.putExtra("isselect", isselect);
					intent.putExtra("rowdata", map);
					startActivityForResult(intent, 1000);
				}else if(map.get("SellerType").equals("1")){
					Intent intent = new Intent(context,
							SupplierdetailsActivity.class);
					intent.putExtra("rowdata", map);
					startActivityForResult(intent, 1000);
				}else{
					
				}
				
			}
		});
		serviceAdapter.notifyDataSetChanged();
		topbase_shop = (ImageView)findViewById(R.id.topbase_shop);
		topbase_shop.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1000) {
			if (resultCode == 2000) {
				setResult(2000, data);
				this.finish();
			}
		}
		
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

	@Override
	public void areaclick(View mBtnTabs) {
		// TODO Auto-generated method stub
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
					// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (sortPopWindow == null) {
			sortPopWindow = new SpinerPopwindow(context);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(0).getDatadetail(), context);
			sortPopWindow.setAdapter(lineTextListAdapter);
			sortPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if (pos == 1) {
						sort = "";
					} else if (pos == 2) {
						sort = "desc";
					} else {
						sort = "";
					}
					executeList(true);
				}
			});
		}
		sortPopWindow.showAsDropDown(mBtnTabs);
	}

	@Override
	public void typeclick(View mBtnTabs) {
		// TODO Auto-generated method stub
		if (typePopWindow == null) {
			typePopWindow = new SpinerPopwindow(context);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(1).getDatadetail(), context);
			typePopWindow.setAdapter(lineTextListAdapter);
			typePopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					HashMap<String, Object> row = selectvalue.get(1)
							.getDatadetail().get(pos);
					if(type.equals(row.get("val").toString())){
						return;
					}else{
						type = row.get("val").toString();
					}
					executeList(true);
				}
			});
		}
		typePopWindow.showAsDropDown(mBtnTabs);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		if (location != null) {
			executeList(false);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.topbase_shop:
			startActivity(new Intent(context, BasicMapActivity.class).putExtra("type", type));
			break;

		default:
			break;
		}
	}

	@Override
	public void serviceclick(View mBtnTabs) {
		// TODO Auto-generated method stub
		if (servicePopWindow == null) {
			servicePopWindow = new SpinerPopwindow(context);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(2).getDatadetail(), context);
			servicePopWindow.setAdapter(lineTextListAdapter);
			servicePopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if(!type.equals("0")){
						return;
					}
					if (pos == 0) {
						service = "";
					} else {
						service = selectvalue.get(2).getDatadetail().get(pos)
								.get("SeTy_Id").toString();
					}
					executeList(true);
				}
			});
		}
		servicePopWindow.showAsDropDown(mBtnTabs);
	}

	@Override
	public void onChange(AMapLocation location) {
		// TODO Auto-generated method stub
		try {
			executeList(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onFailChange() {
		// TODO Auto-generated method stub
		try {
			if (location == null) {
				CommonUtils.showToast(context, "获取位置信息失败！");
				loading.setFail();
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
