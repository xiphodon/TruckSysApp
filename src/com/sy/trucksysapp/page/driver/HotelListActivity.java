package com.sy.trucksysapp.page.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.SelectModel;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.driver.adapter.HotelListAdapter;
import com.sy.trucksysapp.page.map.HotelMapActivity;
import com.sy.trucksysapp.page.service.ServiceActivity;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.LineTextListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.AbstractSpinerAdapter.IOnItemSelectListener;
import com.sy.trucksysapp.widget.GoodsListTopBar;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.sy.trucksysapp.widget.SelectedListener;
import com.sy.trucksysapp.widget.SpinerPopwindow;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;

/**
 * 住宿列表
 * 
 * @author lxs 20150527
 * 
 */
public class HotelListActivity extends CurrentLocationActivity implements
		SelectedListener, LoacationChangeListener {
	private Context context;
	private GoodsListTopBar toopbar = null;
	private PullToRefreshListView mPullRefreshListView;
	private ArrayList<SelectModel> selectvalue, clonevalue;
	private ListView mListView;
	private RelativeLayout rl_area, rl_term1, rl_term2;
	private GoodsSelectPopWindow areaprovince;
	private SpinerPopwindow categorypop, sortpop;
	private LoadingFrameLayout loading;
	private ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	private ProvinceAdapter provinceadpter;
	private CityAdapter cityadpter;
	private ListView provincelistview;
	private ListView citylistview;
	private String selectprovince = "", selectcity = "", Distince = "",
			pricesort = "", type = "", sort = "0", StarSort = "";
	private HotelListAdapter lifeAdapter;
	private int currPage = 1, position;
	private int pageSize = 10;
	ArrayList<HashMap<String, String>> datalist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_services);
		initView();
		initselectview();
	}

	private void executeList(final Boolean withcondition) {
		// TODO Auto-generated method stub
		if (location == null) {
			startLocation();
			return;
		}
		if (withcondition) {
			currPage = 1;
			loading.show();
		} else {
			if (datalist.size() != 0) {
				currPage = ((int) Math.floor(datalist.size() / pageSize)) + 1;
			} else
				currPage = 1;
		}
		String url = SystemApplication.getBaseurl() + "TruckService/GetHotel";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		RequestParams.put("Longitude", location.getLongitude() + "");
		RequestParams.put("Latitude", location.getLatitude() + "");
		RequestParams.put("Province", selectprovince);
		RequestParams.put("City", selectcity);
		RequestParams.put("Distince", Distince);
		RequestParams.put("Hote_Grade", type);
		RequestParams.put("pricesort", pricesort);
		RequestParams.put("StarSort", StarSort);
		RequestParams.put("Member_Id",
				PreferenceUtils.getInstance(getApplicationContext())
						.getSettingUserId());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject json = new JSONObject(result);
					int datasize = json.getInt("total");
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
					lifeAdapter.notifyDataSetChanged();
					mPullRefreshListView.onPullUpRefreshComplete();
					if (datasize == datalist.size()) {
						mPullRefreshListView.setHasMoreData(false);
					}
				} catch (Exception e) {
					// TODO: handle exception
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
				// TODO Auto-generated method stub
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

	private void initView() {
		context = HotelListActivity.this;
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.getReLoadImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.dismiss();
				executeList(true);
			}
		});
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		RelativeLayout rl_top_right = (RelativeLayout) findViewById(R.id.rl_top_right);
		rl_top_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(HotelListActivity.this,
						HotelMapActivity.class));
			}
		});
		topbase_center_text.setText("住宿");
		toopbar = (GoodsListTopBar) findViewById(R.id.gls_sort_mode);
		toopbar.setSelectedListener(this);
		// 隐藏价格和筛选部分
		toopbar.setSelectVisiblenone();
		toopbar.setPriceVisiblenone();
		toopbar.setTopText234("排序", "类别", "");
		datalist = new ArrayList<HashMap<String, String>>();
		lifeAdapter = new HotelListAdapter(context, datalist);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(false);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						executeList(false);
					}
				});
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setAdapter(lifeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				HashMap<String, String> map = datalist.get(Integer.valueOf(arg3
						+ ""));
				if (map != null) {
					Intent intent = new Intent(HotelListActivity.this,
							HotelDetailActivity.class);
					intent.putExtra("rowdata", map);
					startActivityForResult(intent, 100);
				}
			}
		});
		rl_area = (RelativeLayout) findViewById(R.id.rl_sel_area);
		rl_term1 = (RelativeLayout) findViewById(R.id.rl_term1);
		rl_term2 = (RelativeLayout) findViewById(R.id.rl_term2);
		setLoacationChangeListener(this);
	}

	/**
	 * 初始化筛选数据
	 */
	private void initselectview() {
		selectvalue = new ArrayList<SelectModel>();
		// 设置类型选择
		// selectvalue.add(new SelectModel("type", "类型", "", "",
		// new ArrayList<HashMap<String, Object>>()));
		// ArrayList<HashMap<String, Object>> lltype = selectvalue.get(0)
		// .getDatadetail();
		// HashMap mmt = new HashMap<String, String>();
		// mmt.put("Name", "全部");
		// mmt.put("val", "");
		// HashMap mmt0 = new HashMap<String, String>();
		// mmt0.put("Name", "停车");
		// mmt0.put("val", "1");
		// HashMap mmt1 = new HashMap<String, String>();
		// mmt1.put("Name", "餐饮");
		// mmt1.put("val", "2");
		// HashMap mmt2 = new HashMap<String, String>();
		// mmt2.put("Name", "住宿");
		// mmt2.put("val", "3");
		// lltype.add(mmt);
		// lltype.add(mmt0);
		// lltype.add(mmt1);
		// lltype.add(mmt2);
		// 设置距离选择
		selectvalue.add(new SelectModel("type", "类别", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> llsort = selectvalue.get(0)
				.getDatadetail();
		HashMap mmp3 = new HashMap<String, String>();
		mmp3.put("Name", "全部");
		mmp3.put("val", "");
		HashMap mmp = new HashMap<String, String>();
		mmp.put("Name", "三星级");
		mmp.put("val", "三星级");
		HashMap mmp0 = new HashMap<String, String>();
		mmp0.put("Name", "四星级");
		mmp0.put("val", "四星级");
		HashMap mmp1 = new HashMap<String, String>();
		mmp1.put("Name", "五星级");
		mmp1.put("val", "五星级");
		llsort.add(mmp3);
		llsort.add(mmp);
		llsort.add(mmp0);
		llsort.add(mmp1);

		selectvalue.add(new SelectModel("sort", "排序", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> asort = selectvalue.get(1)
				.getDatadetail();
		HashMap amp = new HashMap<String, String>();
		amp.put("Name", "默认排序");
		amp.put("val", "0");
		HashMap amp0 = new HashMap<String, String>();
		amp0.put("Name", "价格升序");
		amp0.put("val", "1");
		HashMap amp1 = new HashMap<String, String>();
		amp1.put("Name", "价格降序");
		amp1.put("val", "2");
		HashMap amp2 = new HashMap<String, String>();
		amp2.put("Name", "评分最高");
		amp2.put("val", "3");
		asort.add(amp);
		asort.add(amp0);
		asort.add(amp1);
		asort.add(amp2);
		// loading.dismiss();
	}

	/**
	 * 点击区域选择
	 */
	public void clickselArea() {
		// TODO Auto-generated method stub
		if (areaprovince == null) {
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
			citylistview.setAdapter(cityadpter);
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
					executeList(true);
					areaprovince.dismiss();
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
		areaprovince.showAsDropDownleft(rl_area, 0, ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getHeight() / 2);
	}

	@Override
	public void SortByPriceUp() {
		// TODO Auto-generated method stub
		// pricesort = "asc";
		// executeList(true);
	}

	@Override
	public void SortByPriceDown() {
		// TODO Auto-generated method stub
		// pricesort = "desc";
		// executeList(true);
	}

	/**
	 * 类别的选择
	 */
	@Override
	public void clickterm1() {
		// TODO Auto-generated method stub
		if (sortpop == null) {
			sortpop = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(1).getDatadetail(), context);
			sortpop.setAdapter(lineTextListAdapter);
			sortpop.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if (pos == 1) {
						StarSort = "";
						pricesort = "asc";
					} else if (pos == 2) {
						pricesort = "desc";
						StarSort = "";
					} else if (pos == 3) {
						StarSort = "desc";
						pricesort = "";
					} else {
						pricesort = "";
						StarSort = "";
					}
					executeList(true);
				}
			});
		}
		sortpop.showAsDropDown(rl_term1);
	}

	/**
	 * 排序方式的选择
	 */
	@Override
	public void clickterm2() {
		// TODO Auto-generated method stub
		if (categorypop == null) {
			categorypop = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(0).getDatadetail(), context);
			categorypop.setAdapter(lineTextListAdapter);
			categorypop.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
//					if (pos == 0) {
//						type = "";
//					} else {
						type = selectvalue.get(0).getDatadetail().get(pos)
								.get("val").toString();
//					}
					executeList(true);
				}
			});
		}
		categorypop.showAsDropDown(rl_term2);
	}

	@Override
	public void clickselect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChange(AMapLocation location) {
		// TODO Auto-generated method stub
		executeList(true);
	}

	@Override
	public void onFailChange() {
		// TODO Auto-generated method stub
		if(location==null){
			CommonUtils.showToast(context, "获取位置信息失败！");
			loading.setEmpty();
			return;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == 100) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) data
						.getSerializableExtra("rowdata");
				datalist.set(position, map);
				lifeAdapter.notifyDataSetChanged();
			}
		}
	}
}
