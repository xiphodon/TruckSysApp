package com.sy.trucksysapp.page.shoping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.entity.SelectModel;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter;
import com.sy.trucksysapp.page.shoping.adapter.LineTextListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.page.shoping.adapter.SelectListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.SpecialSaleListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.TireSpecListAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.user.ShopCartActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.SerializableMap;
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
 * 轮毂列表页面
 * 
 * @author lxs 20150506
 * 
 */
public class TubewheelSaleListActivity extends CurrentLocationActivity
		implements SelectedListener, OnRefreshListener, OnItemClickListener,
		LoacationChangeListener {

	private Context context;
	private GoodsListTopBar toopbar = null;
	private PullToRefreshListView mPullRefreshListView;
	private HotSaleAdapter saleAdapter;
	private RelativeLayout rl_select, rl_term1, rl_term2, rl_area;
	private ArrayList<SelectModel> selectvalue, clonevalue;
	private ListView mListView;
	private int pageSize = 10, currPage = 1, datasize = 0;
	private ArrayList<HashMap<String, String>> datalist;
	private GoodsSelectPopWindow areaprovince;;
	private com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;
	ArrayList<HashMap<String, Object>> speclist;
	LineTextListAdapter speclistAdapter;
	private ImageView scroll_btn;

	private String selectprovince = "", selectcity = "", pricesort = "",
			Brand = "", price = "", Distince = "", type = "", specid = "0";
	private SpinerPopwindow brandSpinerPopWindow, priceSpinerPopWindow,
			typeSpinerPopWindow;
	private TextView topbase_shopbagcount;
	private String sellerType="", sellerId="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_innertubesale_list);
		try {
			sellerType = getIntent().getStringExtra("sellerType");
			sellerId = getIntent().getStringExtra("sellerId");
		} catch (Exception e) {
			// TODO: handle exception
		}
		setLoacationChangeListener(this);
		initView();
		initselectview();
		Executeshopbag();
	}

	/***
	 * 更新购物车数量
	 */
	private void Executeshopbag() {
		if (PreferenceUtils.getInstance(context).getSettingUserId().equals("")
				|| PreferenceUtils.getInstance(context).getSettingUserId()
						.equals("null")
				|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
			return;
		}
		List<CartProduct> listcarts = CommonUtils.getCartProductlist(context);
		if (listcarts != null) {
			topbase_shopbagcount.setText(listcarts.size() + "");
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Executeshopbag();
	}

	private void initselectview() {
		// TODO Auto-generated method stub
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.getReLoadImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.dismiss();
				executeList(true);
			}
		});
		// toopbar.setTopText234("品牌", "类型", "价格区间");
		toopbar.setTopText234("品牌", "类型", "规格");
		selectvalue = new ArrayList<SelectModel>();
		selectvalue.add(new SelectModel("Brand", "品牌", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("price", "价格", "", "",
				new ArrayList<HashMap<String, Object>>()));
		speclist = selectvalue.get(1).getDatadetail();
		String[] ggarray = getResources().getStringArray(R.array.tubewheel);
		for (int i = 0; i < ggarray.length; i++) {
			HashMap mmp = new HashMap<String, String>();
			mmp.put("Name", ggarray[i]);
			mmp.put("val", i + "");
			speclist.add(mmp);
		}

		selectvalue.add(new SelectModel("type", "是否有内胎", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> lltype = selectvalue.get(2)
				.getDatadetail();
		HashMap mmt = new HashMap<String, String>();
		mmt.put("Name", "全部");
		mmt.put("val", "");
		HashMap mmt0 = new HashMap<String, String>();
		mmt0.put("Name", "有内胎");
		mmt0.put("val", "1");
		HashMap mmt1 = new HashMap<String, String>();
		mmt1.put("Name", "无内胎");
		mmt1.put("val", "0");
		lltype.add(mmt);
		lltype.add(mmt0);
		lltype.add(mmt1);
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/getbrand";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("type", "2");
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
				HashMap<String, Object> Brandmap = new HashMap<String, Object>();
				Brandmap.put("BrandId", "");
				Brandmap.put("Name", "全部");
				selectvalue.get(0).getDatadetail().add(Brandmap);
				try {
					JSONObject json = new JSONObject(arg1);
					JSONArray Brands = json.getJSONArray("rows");
					for (int i = 0; i < Brands.length(); i++) {
						JSONObject detail = Brands.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("BrandId", detail.getString("Brand_Id"));
						map.put("Name", detail.getString("Brand_Name"));
						selectvalue.get(0).getDatadetail().add(map);
					}
				} catch (Exception e) {

				}
			}
		});

	}

	private void initView() {
		context = TubewheelSaleListActivity.this;
		scroll_btn = (ImageView) findViewById(R.id.scroll_btn);
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("轮辋");
		toopbar = (GoodsListTopBar) findViewById(R.id.gls_sort_mode);
		toopbar.setSelectedListener(this);
		datalist = new ArrayList<HashMap<String, String>>();
		saleAdapter = new HotSaleAdapter(context, datalist);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(false);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setAdapter(saleAdapter);
		mPullRefreshListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		saleAdapter.notifyDataSetChanged();
		mPullRefreshListView.setToTopView(scroll_btn);
		scroll_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mListView.smoothScrollToPositionFromTop(0, 0);
			}
		});
		rl_term1 = (RelativeLayout) findViewById(R.id.rl_term1);
		rl_term2 = (RelativeLayout) findViewById(R.id.rl_term2);
		rl_area = (RelativeLayout) findViewById(R.id.rl_sel_area);
		rl_select = (RelativeLayout) findViewById(R.id.rl_select);
		// 购物车数量
		RelativeLayout rl_shopbg = (RelativeLayout) findViewById(R.id.rl_shopbg);
		rl_shopbg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 打开购物车页面
				if (PreferenceUtils.getInstance(context).getSettingUserId()
						.equals("")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId().equals("null")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId() == null) {
					context.startActivity(new Intent(context,
							LoginActivity.class));
					return;
				}
				startActivityForResult(new Intent(context,
						ShopCartActivity.class), 555);
			}
		});
		topbase_shopbagcount = (TextView) findViewById(R.id.topbase_shopbagcount);
	}

	@Override
	public void SortByPriceUp() {
		// TODO Auto-generated method stub
		pricesort = "asc";
		executeList(true);

	}

	@Override
	public void SortByPriceDown() {
		// TODO Auto-generated method stub
		pricesort = "desc";
		executeList(true);
	}

	@Override
	public void clickterm1() {
		// TODO Auto-generated method stub
		if (brandSpinerPopWindow == null) {
			brandSpinerPopWindow = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(0).getDatadetail(), context);
			brandSpinerPopWindow.setAdapter(lineTextListAdapter);
			brandSpinerPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if (pos == 0) {
						Brand = "";
					} else {
						Brand = selectvalue.get(0).getDatadetail().get(pos)
								.get("BrandId").toString();
					}
					executeList(true);

					// HashMap<String, Object> row =
					// selectvalue.get(0).getDatadetail().get(pos);
					// selectvalue.get(0).setCode(row.get("SpecId").toString());
				}
			});
		}
		brandSpinerPopWindow.showAsDropDown(rl_term1);
	}

	// brandSpinerPopWindow, priceSpinerPopWindow;
	@Override
	public void clickterm2() {
		// TODO Auto-generated method stub
		if (typeSpinerPopWindow == null) {
			typeSpinerPopWindow = new SpinerPopwindow(this);
			LineTextListAdapter lineTextListAdapter = new LineTextListAdapter(
					selectvalue.get(2).getDatadetail(), context);
			typeSpinerPopWindow.setAdapter(lineTextListAdapter);
			typeSpinerPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if (type.equals(selectvalue.get(2).getDatadetail().get(pos)
							.get("val").toString())) {
						return;
					} else {
						if (pos == 0) {
							type = "";
						} else {
							type = (selectvalue.get(2).getDatadetail().get(pos)
									.get("val").toString());
						}
						speclist.clear();
						specid = "0";
						String[] ggarrayyes = getResources().getStringArray(
								R.array.tubewheelyes);
						String[] ggarrayno = getResources().getStringArray(
								R.array.tubewheelno);
						String[] ggarray = getResources().getStringArray(
								R.array.tubewheel);
						if (type.equals("1")) {
							for (int i = 0; i < ggarrayyes.length; i++) {
								HashMap mmp = new HashMap<String, String>();
								mmp.put("Name", ggarrayyes[i]);
								mmp.put("val", i + "");
								speclist.add(mmp);
							}

						} else if (type.equals("0")) {
							for (int i = 0; i < ggarrayno.length; i++) {
								HashMap mmp = new HashMap<String, String>();
								if (i == 0) {
									mmp.put("Name", ggarrayno[i]);
									mmp.put("val", i + "");
								} else {
									mmp.put("Name", ggarrayno[i]);
									mmp.put("val", (i - 1 + ggarrayyes.length));
								}
								speclist.add(mmp);
							}
						} else {
							for (int i = 0; i < ggarray.length; i++) {
								HashMap mmp = new HashMap<String, String>();
								mmp.put("Name", ggarray[i]);
								mmp.put("val", i + "");
								speclist.add(mmp);
							}
						}
						if (speclistAdapter != null) {
							speclistAdapter.notifyDataSetChanged();
						}
					}
					executeList(true);
				}
			});
		}
		typeSpinerPopWindow.showAsDropDown(rl_term2);
	}

	@Override
	public void clickselect() {
		// TODO Auto-generated method stub
		if (priceSpinerPopWindow == null) {
			priceSpinerPopWindow = new SpinerPopwindow(this);
			speclistAdapter = new LineTextListAdapter(speclist, context);
			priceSpinerPopWindow.setAdapter(speclistAdapter);
			priceSpinerPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					if (!specid.equals(selectvalue.get(1).getDatadetail()
							.get(pos).get("val").toString())) {
						specid = selectvalue.get(1).getDatadetail().get(pos)
								.get("val").toString();
						executeList(true);
					}
				}
			});
		}
		priceSpinerPopWindow.showAsDropDown(rl_select);
	}

	/**
	 * 刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		executeList(false);
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
		}
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetRimlList";
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
		RequestParams.put("type", type);
		RequestParams.put("pricesort", pricesort);
		RequestParams.put("specid", specid);
		RequestParams.put("Brandid", Brand);
		if(!sellerType.equals("")&&!sellerId.equals("")){
			RequestParams.put("sellerType", sellerType);
			RequestParams.put("sellerId", sellerId);
		}
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
					saleAdapter.notifyDataSetChanged();
					mPullRefreshListView.onPullUpRefreshComplete();
					if (datasize == datalist.size()) {
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

	/**
	 * 列表点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, SaleDetailActivity.class);
		HashMap<String, String> data = datalist.get(arg2);
		intent.putExtra("saleid", data.get("RimId"));
		intent.putExtra("Type", 3);
		startActivity(intent);

	}

	@Override
	public void clickselArea() {
		// TODO Auto-generated method stub
		if (areaprovince == null) {
			// areaprovince = new GoodsSelecjjjtPopWindow();
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
					areaprovince.dismiss();
					executeList(true);
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
	public void onChange(AMapLocation location) {
		// TODO Auto-generated method stub
		executeList(true);
	}

	@Override
	public void onFailChange() {
		// TODO Auto-generated method stub
		if (location == null) {
			CommonUtils.showToast(TubewheelSaleListActivity.this, "获取位置信息失败！");
			loading.setFail();
			return;
		}
	}
}
