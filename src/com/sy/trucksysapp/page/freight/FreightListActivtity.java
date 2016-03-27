package com.sy.trucksysapp.page.freight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;
import com.wheelselect.lib.view.DialogWheelView;
import com.wheelselect.lib.view.DialogWheelView.OnCancelListener;
import com.wheelselect.lib.view.DialogWheelView.OnConfirmListener;

/**
 * 货运信息列表
 * 
 * @author Administrator 2015-10-22
 * 
 * 
 */
public class FreightListActivtity extends BaseActivity implements
		OnClickListener {
	private TextView topbase_center_text;
	private Context context;
	private FreightAdapter adapter;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private EditText edt_start, edt_end;
	private Button btn_search;
	private String startStr, endStr;
	private int currPage = 1, position;
	private int pageSize = 10;
	private ArrayList<HashMap<String, String>> datalist = new ArrayList<HashMap<String, String>>();
	private String BProvince ="", BCity="", EProvince="", ECity="";
	private ProvinceAdapter provinceadpter;
	private CityAdapter cityadpter;
	private ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	private ListView provincelistview;
	private ListView citylistview;
	private String selectprovince = "", selectcity = "";
	private GoodsSelectPopWindow areaprovince;
	private ImageView img_clear_start,img_clear_end;
	private LoadingFrameLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_freight_list);
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GetData(true);
	}

	private void initView() {
		context = FreightListActivtity.this;
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("货运信息");
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(true);
		mPullRefreshListView.setPullLoadEnabled(true);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mListView = mPullRefreshListView.getRefreshableView();
		adapter = new FreightAdapter(context, datalist);
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> map = datalist.get(Integer.valueOf(arg3
						+ ""));
				if (map != null) {
					Intent intent = new Intent(FreightListActivtity.this,
							FreightDetailActivity.class);
					intent.putExtra("rowdata", map);
					startActivityForResult(intent, 100);
				}
			}
		});
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						currPage = 1;
//						BProvince = "";
//						BCity = "";
//						EProvince = "";
//						ECity = "";
						datalist.clear();
						GetData(true);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						GetData(false);
					}
				});
		edt_start = (EditText) findViewById(R.id.edt_start);
		edt_start.setOnClickListener(this);
		edt_end = (EditText) findViewById(R.id.edt_end);
		edt_end.setOnClickListener(this);
		btn_search = (Button) findViewById(R.id.btn_search);
		btn_search.setOnClickListener(this);
		img_clear_start = (ImageView)findViewById(R.id.img_clear_start);
		img_clear_start.setOnClickListener(this);
		img_clear_end = (ImageView)findViewById(R.id.img_clear_end);
		img_clear_end.setOnClickListener(this); 
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.getReLoadImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.dismiss();
				currPage = 1;
//				BProvince = "";
//				BCity = "";
//				EProvince = "";
//				ECity = "";
				datalist.clear();
				GetData(true);
			}
		});
//		GetData(true);
	}

	private void GetData(final Boolean withcondition) {
		loading.show();
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetGoodsSource";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("BProvince", BProvince + "");
		RequestParams.put("BCity", BCity + "");
		RequestParams.put("EProvince", EProvince + "");
		RequestParams.put("ECity", ECity + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0);
				loading.dismiss();
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					int datasize = json.getInt("total");
					adapter.setSysTime(json.getString("obj"));
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
					adapter.notifyDataSetChanged();
					mPullRefreshListView.onPullDownRefreshComplete();
					mPullRefreshListView.onPullUpRefreshComplete();
					if (datasize == datalist.size()) {
						mPullRefreshListView.setHasMoreData(false);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				if (withcondition) {
					if(datalist.size()==0){
						loading.setEmpty();
					}else{
						loading.dismiss();
					}
				}
			}

		});

	}

	private void ShowArea(final EditText view) {
		if (areaprovince == null) {
			View v = LayoutInflater.from(context).inflate(
					R.layout.area_select_lay, null);
			provincelistview = (ListView) v.findViewById(R.id.listview);
			citylistview = (ListView) v.findViewById(R.id.listview1);
			com.wheelselect.lib.AreaData areadata = new AreaData(context);
			List<ProvinceModel> provinceList = areadata.getProvinceList();
			selprovincelist = new ArrayList<HashMap<String, Object>>();
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
			citylistview.setAdapter(cityadpter);
			citylistview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					selectprovince = selprovincelist
							.get(provinceadpter.getSelectedPosition())
							.get("Name").toString();
					selectcity = cityitemlist.get(arg2).get("Name").toString();
					if (view == edt_start) {
						edt_start.setText(selectprovince+selectcity);
						BProvince = selectprovince;
						BCity = selectcity;
					} else if (view == edt_end) {
						edt_end.setText(selectprovince+selectcity);
						EProvince = selectprovince;
						ECity = selectcity;
					}
					areaprovince.dismiss();
					areaprovince = null;
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
					((WindowManager) context
							.getSystemService(Context.WINDOW_SERVICE))
							.getDefaultDisplay().getWidth(),
					LayoutParams.MATCH_PARENT);
		}
		areaprovince.showAsDropDownleft(view, 0, ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight() / 2);
	}

	/**
	 * 执行搜索
	 */
	private void doSearch() {
		startStr = edt_start.getText().toString().trim();
		endStr = edt_end.getText().toString().trim();
		if ((startStr == null || startStr.equals(""))&&(endStr == null || endStr.equals(""))) {
			CommonUtils.showToast(context, "请输入始发地或者目的地");
			return;
		}
		datalist.clear();
		GetData(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edt_start:
			ShowArea(edt_start);// 始发地
			break;
		case R.id.edt_end:
			ShowArea(edt_end);// 目的地
			break;
		case R.id.btn_search:// 搜索按钮
			doSearch();
			break;
		case R.id.img_clear_start:
			edt_start.setText("");
			BProvince = "";
			BCity ="";
			break;
		case R.id.img_clear_end:
			edt_end.setText("");
			EProvince = "";
			ECity ="";
			break;
		default:
			break;
		}
	}
}
