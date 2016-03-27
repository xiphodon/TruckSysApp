package com.sy.trucksysapp.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.SelectModel;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.page.shoping.adapter.SelectListAdapter;
import com.sy.trucksysapp.page.shoping.adapter.TireSpecListAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.GoodsListTopBar;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.sy.trucksysapp.widget.SelectedListener;
import com.sy.trucksysapp.widget.SpinerPopwindow;
import com.sy.trucksysapp.widget.AbstractSpinerAdapter.IOnItemSelectListener;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 以旧换新界面
 * @author lxs 20150918
 *
 */
public class ExchangeActivity extends BaseActivity  implements SelectedListener, OnRefreshListener{
	
	private Context context;
	private GoodsListTopBar toopbar = null;
	private RelativeLayout rl_select, rl_term1, rl_term2, rl_area;
	private PullToRefreshListView mPullRefreshListView;
	private GoodsSelectPopWindow selectpop;
	private GoodsSelectPopWindow brandpop, figurepop, pricepop;
	private ArrayList<SelectModel> selectvalue, clonevalue;
	private SelectListAdapter brandAdapter;
	private SelectListAdapter figureAdapter;
	private SelectListAdapter priceAdapter;
	private SpinerPopwindow specSpinerPopWindow, strucSpinerPopWindow;
	private ListView mListView;
	private int pageSize = 10, currPage = 1, datasize = 0;
	private ArrayList<HashMap<String, String>> datalist;
	TextView t2 = null;
	TextView t4 = null;
	TextView t6 = null;
	private GoodsSelectPopWindow areaprovince;
	private GoodsSelectPopWindow areacity;;
	private com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;
	private String selectprovince = "", selectcity = "", pricesort = "",
			Distince = "", speid = "";
	CheckBox item_cb = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange);
		initView();
		initselectview();
	}

	/**
	 * 初始化
	 */
	private void initView(){
		context = ExchangeActivity.this;
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("轮胎");
		toopbar = (GoodsListTopBar) findViewById(R.id.gls_sort_mode);
		toopbar.setSelectedListener(this);
		rl_select = (RelativeLayout) findViewById(R.id.rl_select);
		rl_term1 = (RelativeLayout) findViewById(R.id.rl_term1);
		rl_term2 = (RelativeLayout) findViewById(R.id.rl_term2);
		rl_area = (RelativeLayout) findViewById(R.id.rl_sel_area);
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		Executeshopbag();
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
		toopbar.setTopText234("规格", "筛选", "筛选");
		toopbar.setSelectVisiblenone();
		selectvalue = new ArrayList<SelectModel>();
		selectvalue.add(new SelectModel("", "品牌", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("", "用途", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> llu = selectvalue.get(1)
				.getDatadetail();
		HashMap mmu = new HashMap<String, String>();
		mmu.put("Name", "全部");
		mmu.put("val", "");
		HashMap mmu0 = new HashMap<String, String>();
		mmu0.put("Name", "矿山，中短途混合路面(200km以内载重)");
		mmu0.put("val", "1");
		HashMap mmu1 = new HashMap<String, String>();
		mmu1.put("Name", "中距离较好路况(200-500km以内载重)");
		mmu1.put("val", "2");
		HashMap mmu2 = new HashMap<String, String>();
		mmu2.put("Name", "中长途标载(300以上)");
		mmu2.put("val", "3");
		HashMap mmu3 = new HashMap<String, String>();
		mmu3.put("Name", "城市公交专用");
		mmu3.put("val", "4");
		HashMap mmu4 = new HashMap<String, String>();
		mmu4.put("Name", "中长途客车");
		mmu4.put("val", "5");
		HashMap mmu5 = new HashMap<String, String>();
		mmu5.put("Name", "工程轮胎");
		mmu5.put("val", "6");
		HashMap mmu6 = new HashMap<String, String>();
		mmu6.put("Name", "特殊轮胎");
		mmu6.put("val", "7");
		llu.add(mmu);
		llu.add(mmu0);
		llu.add(mmu1);
		llu.add(mmu2);
		llu.add(mmu3);
		llu.add(mmu4);
		llu.add(mmu5);
		llu.add(mmu6);
		selectvalue.add(new SelectModel("", "规格", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("", "结构", "", "",
				new ArrayList<HashMap<String, Object>>()));
		selectvalue.add(new SelectModel("", "价格", "", "",
				new ArrayList<HashMap<String, Object>>()));
		ArrayList<HashMap<String, Object>> ll = selectvalue.get(4)
				.getDatadetail();
		HashMap mmp = new HashMap<String, String>();
		mmp.put("Name", "全部");
		HashMap mmp0 = new HashMap<String, String>();
		mmp0.put("Name", "500以下");
		mmp0.put("val", "0-500");
		HashMap mmp1 = new HashMap<String, String>();
		mmp1.put("Name", "500-1000");
		mmp1.put("val", "500-1000");
		HashMap mmp2 = new HashMap<String, String>();
		mmp2.put("Name", "1000-1500");
		mmp2.put("val", "1000-1500");
		HashMap mmp3 = new HashMap<String, String>();
		mmp3.put("Name", "1500-2000");
		mmp3.put("val", "1500-2000");
		HashMap mmp4 = new HashMap<String, String>();
		mmp4.put("Name", "2000以上");
		mmp4.put("val", "2000");
		ll.add(mmp);
		ll.add(mmp0);
		ll.add(mmp1);
		ll.add(mmp2);
		ll.add(mmp3);
		ll.add(mmp4);
		selectvalue.add(new SelectModel("iscontaininnerTube", "是否带内胎", "", "",
				new ArrayList<HashMap<String, Object>>()));
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetTireAtt";
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
					JSONObject data = json.getJSONObject("Data");
					JSONArray Brands = data.getJSONArray("Brand");
					HashMap<String, Object> Brandmap = new HashMap<String, Object>();
					Brandmap.put("BrandId", "");
					Brandmap.put("Name", "全部");
					selectvalue.get(0).getDatadetail().add(Brandmap);
					for (int i = 0; i < Brands.length(); i++) {
						JSONObject detail = Brands.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.putAll(CommonUtils.getMap(detail));
						selectvalue.get(0).getDatadetail().add(map);
					}
					JSONArray Specifications = data
							.getJSONArray("Specification");
					HashMap<String, Object> specmap = new HashMap<String, Object>();
					specmap.put("SpecId", "");
					specmap.put("Name", "全部");
					selectvalue.get(2).getDatadetail().add(specmap);
					for (int i = 0; i < Specifications.length(); i++) {
						JSONObject detail = Specifications.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.putAll(CommonUtils.getMap(detail));
						selectvalue.get(2).getDatadetail().add(map);
					}
					// JSONArray Structures = data.getJSONArray("Structure");
					// for (int i = 0; i < Structures.length(); i++) {
					// JSONObject detail = Structures.getJSONObject(i);
					// HashMap<String, Object> map = new HashMap<String,
					// Object>();
					// map.putAll(CommonUtils.getMap(detail));
					// selectvalue.get(3).getDatadetail().add(map);
					// }
				} catch (Exception e) {

				}
			}
		});

	}
	private void setselectvalue() {
		// TODO Auto-generated method stub
		selectvalue.get(0).setIsSelected(
				(HashMap<Integer, Boolean>) clonevalue.get(0).getIsSelected()
						.clone());
		selectvalue.get(0).setCode(clonevalue.get(0).getCode());
		selectvalue.get(0).setName(clonevalue.get(0).getName());
		selectvalue.get(0).setValue(clonevalue.get(0).getValue());
		selectvalue.get(1).setIsSelected(
				(HashMap<Integer, Boolean>) clonevalue.get(1).getIsSelected()
						.clone());
		selectvalue.get(1).setCode(clonevalue.get(1).getCode());
		selectvalue.get(1).setName(clonevalue.get(1).getName());
		selectvalue.get(1).setValue(clonevalue.get(1).getValue());
		selectvalue.get(4).setIsSelected(
				(HashMap<Integer, Boolean>) clonevalue.get(4).getIsSelected()
						.clone());
		selectvalue.get(4).setCode(clonevalue.get(4).getCode());
		selectvalue.get(4).setName(clonevalue.get(4).getName());
		selectvalue.get(4).setValue(clonevalue.get(4).getValue());
		selectvalue.get(5).setValue(clonevalue.get(5).getValue());
	}
	private void executeList(final Boolean withcondition) {
		// TODO Auto-generated method stub
//		if (location == null) {
//			startLocation();
//			return;
//		}
//		if (withcondition) {
//			currPage = 1;
//			loading.show();
//		}
//		String url = SystemApplication.getInstance().getBaseurl()
//				+ "TruckService/GetTireProduct";
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.setTimeout(10000);
//		RequestParams RequestParams = new RequestParams();
//		RequestParams.put("pageSize", pageSize + "");
//		RequestParams.put("currPage", currPage + "");
//		RequestParams.put("Longitude", location.getLongitude() + "");
//		RequestParams.put("Latitude", location.getLatitude() + "");
//		RequestParams.put("Province", selectprovince);
//		RequestParams.put("City", selectcity);
//		RequestParams.put("Distince", Distince);
//		RequestParams.put("pricesort", pricesort);
//		if(!sellerType.equals("")&&!sellerId.equals("")){
//			RequestParams.put("sellerType", sellerType);
//			RequestParams.put("sellerId", sellerId);
//		}
//		// 价格
//		RequestParams.put("price", selectvalue.get(4).getCode());
//		// 品牌
//		RequestParams.put("Brandid", selectvalue.get(0).getCode());
//		// 用途
//		RequestParams.put("use", selectvalue.get(1).getCode());
//		if (selectvalue.get(5).getValue().equals("true")) {
//			RequestParams.put("IsInnerTube", "1");
//
//		} else if (selectvalue.get(5).getValue().equals("false")) {
//			RequestParams.put("IsInnerTube", "0");
//
//		} else {
//			RequestParams.put("IsInnerTube", "");
//		}
//		//
//		RequestParams.put("SpecId", selectvalue.get(2).getCode());
//		// 0 品牌1用途2规格4价格5是否有内胎
//
//		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
//			@Override
//			public void onSuccess(String result) {
//				// TODO Auto-generated method stub
//				super.onSuccess(result);
//				ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
//				try {
//					JSONObject json = new JSONObject(result);
//					datasize = json.getInt("total");
//					JSONArray rows = json.getJSONArray("rows");
//					for (int i = 0; i < rows.length(); i++) {
//						JSONObject detail = rows.getJSONObject(i);
//						HashMap<String, String> map = CommonUtils
//								.getMap(detail);
//						data.add(map);
//					}
//					if (withcondition) {
//						datalist.clear();
//					}
//					datalist.addAll(data);
//					currPage++;
//					saleAdapter.notifyDataSetChanged();
//					mPullRefreshListView.onPullUpRefreshComplete();
//					if (datasize == datalist.size()) {
//						mPullRefreshListView.setHasMoreData(false);
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				if (withcondition) {
//					if (datalist.size() == 0) {
//						loading.setEmpty();
//					} else {
//						loading.dismiss();
//					}
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable arg0, String arg1) {
//				// TODO Auto-generated method stub
//				super.onFailure(arg0, arg1);
//				if (withcondition) {
//					loading.setFail();
//				} else {
//					mPullRefreshListView.onPullUpRefreshComplete();
//					CommonUtils.showToast(context, arg1);
//				}
//			}
//		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		
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
		if (specSpinerPopWindow == null) {
			specSpinerPopWindow = new SpinerPopwindow(this);
			TireSpecListAdapter tireSpecListAdapter = new TireSpecListAdapter(
					selectvalue.get(2).getDatadetail(), context);
			specSpinerPopWindow.setAdapter(tireSpecListAdapter);
			specSpinerPopWindow.setItemListener(new IOnItemSelectListener() {
				@Override
				public void onItemClick(int pos) {
					// TODO Auto-generated method stub
					HashMap<String, Object> row = selectvalue.get(2)
							.getDatadetail().get(pos);
					selectvalue.get(2).setCode(row.get("SpecId").toString());
					executeList(true);
				}
			});
		}
		specSpinerPopWindow.showAsDropDown(rl_term1);
	}

	@Override
	public void clickselArea() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
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
	public void clickterm2() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if (selectpop == null) {
			// 加载数据
			View views = LayoutInflater.from(context).inflate(
					R.layout.shop_select, null);
			LinearLayout lin_shopsel = (LinearLayout) views
					.findViewById(R.id.lin_shopsel);
			TextView btn_allcancel = (TextView) views
					.findViewById(R.id.btn_allcancel);
			btn_allcancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					selectpop.dismiss();
				}
			});
			TextView btn_alldo = (TextView) views.findViewById(R.id.btn_alldo);
			btn_alldo.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try {
						clonevalue = (ArrayList<SelectModel>) CommonUtils
								.deepCopy(selectvalue);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					selectpop.dismiss();
					executeList(true);
				}
			});
			// 是否带内胎
			ViewGroup iscontainitem = (ViewGroup) LayoutInflater.from(context)
					.inflate(R.layout.shop_select_checkitem, null);
			TextView item_tv = (TextView) iscontainitem
					.findViewById(R.id.tv_name);
			item_tv.setText(selectvalue.get(5).getName());
			item_cb = (CheckBox) iscontainitem.findViewById(R.id.cb_value);
			iscontainitem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					item_cb.toggle();
					item_cb.postInvalidate();
					if (item_cb.isChecked()) {
						selectvalue.get(5).setValue("true");
					} else {
						selectvalue.get(5).setValue("false");
					}
				}
			});
			lin_shopsel.addView(iscontainitem);
			// 品牌
			ViewGroup Branditem = (ViewGroup) LayoutInflater.from(context)
					.inflate(R.layout.shop_select_item, null);
			TextView t1 = (TextView) Branditem.findViewById(R.id.tv_name);
			t1.setText(selectvalue.get(0).getName());
			t2 = (TextView) Branditem.findViewById(R.id.tv_value);
			t2.setText(selectvalue.get(0).getValue());
			lin_shopsel.addView(Branditem);
			HashMap<Integer, Boolean> Brandselect = new HashMap<Integer, Boolean>();
			for (int i = 0; i < selectvalue.get(0).getDatadetail().size(); i++) {
				if (i == 0) {
					Brandselect.put(i, true);
				} else {
					Brandselect.put(i, false);
				}
			}
			selectvalue.get(0).setIsSelected(Brandselect);
			Branditem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (brandpop == null) {
						View brandview = LayoutInflater.from(context).inflate(
								R.layout.select_list, null);
						TextView btn_yes = (TextView) brandview
								.findViewById(R.id.btn_yes);
						btn_yes.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
								map.putAll(brandAdapter.getIsSelected());
								selectvalue.get(0).setIsSelected(map);
								// 获取选中值
								String data = "";
								String datavalue = "";
								for (int i = 0; i < brandAdapter.getCount(); i++) {
									if (i == 0
											&& brandAdapter.getIsSelected()
													.get(i)) {
										data = "";
										datavalue = "";
									} else {
										if (brandAdapter.getIsSelected().get(i)) {
											data += selectvalue.get(0)
													.getDatadetail().get(i)
													.get("BrandId")
													+ ",";
											datavalue += selectvalue.get(0)
													.getDatadetail().get(i)
													.get("Name")
													+ ",";
										}
									}
								}
								selectvalue.get(0).setValue(datavalue);
								selectvalue.get(0).setCode(data);
								t2.setText(selectvalue.get(0).getValue());
								brandpop.dismiss();
							}
						});
						ListView list = (ListView) brandview
								.findViewById(R.id.list);
						brandAdapter = new SelectListAdapter(selectvalue.get(0)
								.getDatadetail(), context);
						// 保存当前选中项
						HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
						map.putAll(brandAdapter.getIsSelected());
						selectvalue.get(0).setIsSelected(map);
						list.setAdapter(brandAdapter);
						list.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								SelectListAdapter.ViewHolder holder = (SelectListAdapter.ViewHolder) arg1
										.getTag();
								int checksize = 0;
								for (int i = 0; i < brandAdapter.getCount(); i++) {
									if (brandAdapter.getIsSelected().get(i)) {
										checksize++;
									}
								}
								if (arg2 == 0) {
									if (holder.cb.isChecked()) {
										return;
									} else {
										brandAdapter.initData();
										brandAdapter.getIsSelected().put(arg2,
												true);
									}
								} else {
									if (holder.cb.isChecked()) {
										brandAdapter.getIsSelected().put(arg2,
												false);
										if (checksize == 1) {
											brandAdapter.getIsSelected().put(0,
													true);
										}
									} else {
										brandAdapter.getIsSelected().put(0,
												false);
										brandAdapter.getIsSelected().put(arg2,
												true);
									}
								}
								brandAdapter.notifyDataSetChanged();
							}
						});
						brandpop = new GoodsSelectPopWindow(
								context,
								brandview,
								((WindowManager) context
										.getSystemService(Context.WINDOW_SERVICE))
										.getDefaultDisplay().getWidth() * 3 / 4,
								LayoutParams.MATCH_PARENT);
					}
					Set set = selectvalue.get(0).getIsSelected().keySet();
					for (Iterator iterator = set.iterator(); iterator.hasNext();) {
						Object object = (Object) iterator.next();
						int key = Integer.valueOf(object.toString());
						Boolean b = selectvalue.get(0).getIsSelected().get(key);
						brandAdapter.getIsSelected().put(key, b);
					}
					brandpop.showAsDropDownright(rl_select,
							((WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE))
									.getDefaultDisplay().getWidth() * 3 / 4, 0);
					brandAdapter.notifyDataSetChanged();
				}
			});

			// 花纹
			ViewGroup Figureitem = (ViewGroup) LayoutInflater.from(context)
					.inflate(R.layout.shop_select_item, null);
			TextView t3 = (TextView) Figureitem.findViewById(R.id.tv_name);
			t3.setText(selectvalue.get(1).getName());
			t4 = (TextView) Figureitem.findViewById(R.id.tv_value);
			t4.setText(selectvalue.get(1).getValue());
			lin_shopsel.addView(Figureitem);
			Figureitem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (figurepop == null) {
						View figureview = LayoutInflater.from(context).inflate(
								R.layout.select_list, null);
						ListView list = (ListView) figureview
								.findViewById(R.id.list);
						TextView btn_yes = (TextView) figureview
								.findViewById(R.id.btn_yes);
						btn_yes.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
								map.putAll(figureAdapter.getIsSelected());
								selectvalue.get(1).setIsSelected(map);
								// 获取选中的值
								String data = "";
								String datavalue = "";
								for (int i = 0; i < figureAdapter.getCount(); i++) {
									if (i == 0
											&& figureAdapter.getIsSelected()
													.get(i)) {
										data = "";
										datavalue = "";
									} else {
										if (figureAdapter.getIsSelected()
												.get(i)) {
											data += selectvalue.get(1)
													.getDatadetail().get(i)
													.get("val")
													+ ",";
											datavalue += selectvalue.get(1)
													.getDatadetail().get(i)
													.get("Name")
													+ ",";
										}
									}
								}
								selectvalue.get(1).setValue(datavalue);
								selectvalue.get(1).setCode(data);
								t4.setText(selectvalue.get(1).getValue());
								figurepop.dismiss();
							}
						});
						figureAdapter = new SelectListAdapter(selectvalue
								.get(1).getDatadetail(), context);
						HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
						map.putAll(figureAdapter.getIsSelected());
						selectvalue.get(1).setIsSelected(map);
						list.setAdapter(figureAdapter);
						list.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								SelectListAdapter.ViewHolder holder = (SelectListAdapter.ViewHolder) arg1
										.getTag();
								int checksize = 0;
								for (int i = 0; i < figureAdapter.getCount(); i++) {
									if (figureAdapter.getIsSelected().get(i)) {
										checksize++;
									}
								}
								if (arg2 == 0) {
									if (holder.cb.isChecked()) {
										return;
									} else {
										figureAdapter.initData();
										figureAdapter.getIsSelected().put(arg2,
												true);
									}
								} else {
									if (holder.cb.isChecked()) {
										figureAdapter.getIsSelected().put(arg2,
												false);
										if (checksize == 1) {
											figureAdapter.getIsSelected().put(
													0, true);
										}
									} else {
										figureAdapter.getIsSelected().put(0,
												false);
										figureAdapter.getIsSelected().put(arg2,
												true);
									}
								}
								figureAdapter.notifyDataSetChanged();
							}
						});
						figurepop = new GoodsSelectPopWindow(
								context,
								figureview,
								((WindowManager) context
										.getSystemService(Context.WINDOW_SERVICE))
										.getDefaultDisplay().getWidth() * 3 / 4,
								LayoutParams.MATCH_PARENT);
					}
					Set set = selectvalue.get(1).getIsSelected().keySet();
					for (Iterator iterator = set.iterator(); iterator.hasNext();) {
						Object object = (Object) iterator.next();
						int key = Integer.valueOf(object.toString());
						Boolean b = selectvalue.get(1).getIsSelected().get(key);
						figureAdapter.getIsSelected().put(key, b);
					}
					figureAdapter.notifyDataSetChanged();
					figurepop.showAsDropDownright(rl_select,
							((WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE))
									.getDefaultDisplay().getWidth() * 3 / 4, 0);
				}
			});
			HashMap<Integer, Boolean> Figureselect = new HashMap<Integer, Boolean>();
			for (int i = 0; i < selectvalue.get(1).getDatadetail().size(); i++) {
				if (i == 0) {
					Figureselect.put(i, true);
				} else {
					Figureselect.put(i, false);
				}
			}
			selectvalue.get(1).setIsSelected(Figureselect);
			// 价格
			ViewGroup priceitem = (ViewGroup) LayoutInflater.from(context)
					.inflate(R.layout.shop_select_item, null);
			TextView t5 = (TextView) priceitem.findViewById(R.id.tv_name);
			t5.setText(selectvalue.get(4).getName());
			t6 = (TextView) priceitem.findViewById(R.id.tv_value);
			t6.setText(selectvalue.get(4).getValue());
			lin_shopsel.addView(priceitem);
			priceitem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (pricepop == null) {
						View priceview = LayoutInflater.from(context).inflate(
								R.layout.select_list, null);
						ListView list = (ListView) priceview
								.findViewById(R.id.list);
						TextView btn_yes = (TextView) priceview
								.findViewById(R.id.btn_yes);
						btn_yes.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
								map.putAll(priceAdapter.getIsSelected());
								selectvalue.get(4).setIsSelected(map);
								// 获取选中值
								String data = "";
								String datavalue = "";
								for (int i = 0; i < priceAdapter.getCount(); i++) {
									if (priceAdapter.getIsSelected().get(i)) {
										if (i == 0) {
											data = "";
											datavalue = "";
										} else {
											data = selectvalue.get(4)
													.getDatadetail().get(i)
													.get("val").toString();
											datavalue = selectvalue.get(4)
													.getDatadetail().get(i)
													.get("Name").toString();
										}
										break;
									}
								}
								selectvalue.get(4).setValue(datavalue);
								selectvalue.get(4).setCode(data);
								t6.setText(selectvalue.get(4).getValue());
								pricepop.dismiss();
							}
						});
						priceAdapter = new SelectListAdapter(selectvalue.get(4)
								.getDatadetail(), context);
						HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
						map.putAll(priceAdapter.getIsSelected());
						selectvalue.get(4).setIsSelected(map);
						list.setAdapter(priceAdapter);
						list.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								SelectListAdapter.ViewHolder holder = (SelectListAdapter.ViewHolder) arg1
										.getTag();
								if (arg2 == 0 && holder.cb.isChecked()) {
									return;
								}
								if (holder.cb.isChecked()) {
									priceAdapter.getIsSelected().put(arg2,
											false);
									priceAdapter.getIsSelected().put(0, true);
								} else {
									priceAdapter.initData();
									priceAdapter.getIsSelected()
											.put(arg2, true);
								}
								priceAdapter.notifyDataSetChanged();
							}
						});
						pricepop = new GoodsSelectPopWindow(
								context,
								priceview,
								((WindowManager) context
										.getSystemService(Context.WINDOW_SERVICE))
										.getDefaultDisplay().getWidth() * 3 / 4,
								LayoutParams.MATCH_PARENT);
					}
					Set set = selectvalue.get(4).getIsSelected().keySet();
					for (Iterator iterator = set.iterator(); iterator.hasNext();) {
						Object object = (Object) iterator.next();
						int key = Integer.valueOf(object.toString());
						Boolean b = selectvalue.get(4).getIsSelected().get(key);
						priceAdapter.getIsSelected().put(key, b);
					}
					pricepop.showAsDropDownright(rl_select,
							((WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE))
									.getDefaultDisplay().getWidth() * 3 / 4, 0);
					priceAdapter.notifyDataSetChanged();
				}
			});
			HashMap<Integer, Boolean> priceselect = new HashMap<Integer, Boolean>();
			for (int i = 0; i < selectvalue.get(4).getDatadetail().size(); i++) {
				if (i == 0) {
					priceselect.put(i, true);
				} else {
					priceselect.put(i, false);
				}
			}
			selectvalue.get(4).setIsSelected(priceselect);
			selectpop = new GoodsSelectPopWindow(context, views,
					((WindowManager) context
							.getSystemService(Context.WINDOW_SERVICE))
							.getDefaultDisplay().getWidth() * 3 / 4,
					LayoutParams.MATCH_PARENT);
			try {
				clonevalue = (ArrayList<SelectModel>) CommonUtils
						.deepCopy(selectvalue);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setselectvalue();
		t2.setText(selectvalue.get(0).getValue());
		t4.setText(selectvalue.get(1).getValue());
		t6.setText(selectvalue.get(4).getValue());
		item_cb.setChecked(selectvalue.get(5).getValue().equals("true"));
		selectpop.showAsDropDownright(rl_select, ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth() * 3 / 4, 0);
	
	}

	@Override
	public void clickselect() {
		// TODO Auto-generated method stub
		
	}
}
