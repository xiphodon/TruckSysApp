package com.sy.trucksysapp.page.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.color;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.CityAdapter;
import com.sy.trucksysapp.page.shoping.adapter.ProvinceAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.GoodsSelectPopWindow;
import com.wheelselect.lib.AreaData;
import com.wheelselect.lib.CityModel;
import com.wheelselect.lib.ProvinceModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class SearchActivity extends Fragment implements OnClickListener {
	private static PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private static ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private static ArrayList<String> listKey = new ArrayList<String>();
	private final static String keyName = "Order_Id";
	private final static String sortName = "Order_CTime";
	private static String memberId = "";
	private static Date lastDate = null;
	private static MyAdapter adapter = null;

	private static int page = 1, rows = 10, total = 0, index = 0, setDate = 0;
	private static TextView start;
	private static TextView end;

	private static Context context;
	private GoodsSelectPopWindow areaprovince;;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;
	private String value;

	/**
	 * 找货源-登录(退出)时执行当前操作
	 */
	public static void refresh() {
		memberId = PreferenceUtils.getInstance(context).getSettingUserId();
		listItem.clear();
		listKey.clear();
		init();
	}

	private static void init() {
		page = 1;
		rows = 10;
		total = 0;
		index = 0;
		setDate = 0;
		lastDate = null;
		listItem.clear();
		listKey.clear();
		getData("up");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_search, container, false);
		context = getActivity();
		memberId = PreferenceUtils.getInstance(context).getSettingUserId();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		start = (TextView) getView().findViewById(R.id.start);
		((RelativeLayout) getView().findViewById(R.id.start_layout))
				.setOnClickListener(this);
		((RelativeLayout) getView().findViewById(R.id.end_layout))
				.setOnClickListener(this);
		((RelativeLayout) getView().findViewById(R.id.exchange_layout))
				.setOnClickListener(this);
		end = (TextView) getView().findViewById(R.id.end);
		adapter = new MyAdapter(context);
		mPullRefreshListView = (PullToRefreshListView) getView().findViewById(
				R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(true);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mListView = mPullRefreshListView.getRefreshableView();
		mListView.setAdapter(adapter);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						getData("down");
					}

					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						getData("up");
					}
				});
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				index = arg2;
				Intent intent = new Intent(context, DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("obj", listItem.get(arg2));
				intent.putExtras(bundle);
				startActivityForResult(intent, 100);
			}
		});
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == 100) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) data
						.getSerializableExtra("obj");
				listItem.set(index, map);
				adapter.notifyDataSetChanged();
			} else if (resultCode == 200) {
				if (data.getIntExtra("State", 0) == DetailActivity.ORDER_DEL) {
					@SuppressWarnings("unchecked")
					HashMap<String, String> map = (HashMap<String, String>) data
							.getSerializableExtra("obj");
					listItem.set(index, map);
					adapter.notifyDataSetChanged();
				} else {
					listItem.remove(index);
					listKey.remove(index);
				}
				CommonUtils
						.showToast(getActivity(), data.getStringExtra("Msg"));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@SuppressWarnings("static-access")
	private void grab(final int position) {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetMember";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("OrderId", listItem.get(position).get("Order_Id"));
		RequestParams.put("MemberId", memberId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@SuppressLint("NewApi")
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						JSONObject obj = jsobj.getJSONObject("Obj");
						HashMap<String, String> map = listItem.get(position);
						map.put("Member_Id", memberId);
						map.put("AcceptDate", obj.getString("AcceptDate"));
						listItem.set(position, map);
						sort(listItem, listKey);
						adapter.notifyDataSetChanged();
					} else {
						if (jsobj.getString("Msg").contains("删除")) {
							HashMap<String, String> map = listItem
									.get(position);
							map.put("Order_Del", "true");
							listItem.set(position, map);
							adapter.notifyDataSetChanged();
						}
					}
					CommonUtils.showToast(context, jsobj.getString("Msg"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	private static void getData(final String type) {
		if (type.equals("down")) {
			if (lastDate == null) {
				rows = 10;
			} else {
				rows = -1;
				setDate = 0;
			}
		} else if (type.equals("up")) {
			rows = 10;
			if (total != 0) {
				page = ((int) Math.floor(listItem.size() / rows)) + 1;
			} else
				page = 1;
		}
		String url = SystemApplication.getBaseurl() + "truckservice/GetOrder";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		String s = start.getText().toString();
		String e = end.getText().toString();
		s = s.replace("全国", "").replace("全境", "");
		e = e.replace("全国", "").replace("全境", "");
		RequestParams.put("Order_Start", s);
		RequestParams.put("Order_End", e);
		RequestParams.put("searchType", "search");
		RequestParams.put("OT_Type", "已发布");
		RequestParams.put("Member_Id", memberId);
		RequestParams.put("currPage", page + "");
		RequestParams.put("lastDate", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(lastDate == null ? new Date()
				: lastDate));
		RequestParams.put("pagesize", rows + "");
		RequestParams.put("s", new Date().getTime() + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					total = jsobj.getInt("total");
					int index = listKey.size();
					ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					if (total != 0) {
						JSONArray row = jsobj.getJSONArray("rows");
						for (int i = 0, j = row.length(); i < j; i++) {
							HashMap<String, String> map = CommonUtils
									.getMap((JSONObject) row.get(i));
							String key = map.get(keyName);
							if (!listKey.contains(key)) {
								list.add(map);
								listKey.add(key);
							} else {
								listItem.set(listKey.indexOf(key), map);
							}
						}
						if (type.equals("down")) {
							total = index + list.size();
							index = 0;
						}
					}
					if (list.size() > 0)
						listItem.addAll(index, list);
					sort(listItem, listKey);
					adapter.notifyDataSetChanged();
					if (total == listItem.size()) {
						mPullRefreshListView.setHasMoreData(false);
					}
					if (type.equals("up"))
						mPullRefreshListView.onPullUpRefreshComplete();
					else
						mPullRefreshListView.onPullDownRefreshComplete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				mPullRefreshListView.onPullUpRefreshComplete();
				mPullRefreshListView.onPullDownRefreshComplete();
				super.onFailure(arg0, arg1);
			}
		});
	}

	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int TYPENOMAL = 0;
		private int TYPEUNNOAML = 1;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getViewTypeCount() {
			return 2;
		}

		public int getItemViewType(int position) {
			if (listItem.get(position).get("Order_Del").equals("true")) {
				return TYPEUNNOAML;
			} else

				return TYPENOMAL;
		}

		public int getCount() {
			return listItem.size();
		}

		public Object getItem(int arg0) {
			return listItem.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("NewApi")
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			int type = getItemViewType(position);
			ViewHolder holder = null;
			if (convertView == null) {
				if (type == TYPENOMAL) {
					holder = new ViewHolder();
					convertView = mInflater.inflate(R.layout.order_search_item,
							null);
					holder.Order_DeliverTime = (TextView) convertView
							.findViewById(R.id.deliverTime);
					holder.Order_Start = (TextView) convertView
							.findViewById(R.id.start);
					holder.Order_End = (TextView) convertView
							.findViewById(R.id.end);
					holder.vtype = (TextView) convertView
							.findViewById(R.id.vtype);
					holder.unit = (TextView) convertView
							.findViewById(R.id.unit);
					holder.remark = (TextView) convertView
							.findViewById(R.id.remark);
					holder.grab = (TextView) convertView
							.findViewById(R.id.grab);
					convertView.setTag(holder);
				} else {
					convertView = mInflater.inflate(R.layout.item_blank, null);
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (type == TYPENOMAL) {
				holder.grab.setTag(position);
				holder.grab.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						if (("").equals(memberId)) {
							CommonUtils.showToast(context, "请先登录");
							return;
						}
						if (((TextView) arg0).getText().toString().equals("抢"))
							grab(Integer.valueOf(arg0.getTag().toString()));
						else
							CommonUtils.showToast(context, "您已抢过该单");
					}
				});
				String memberId = listItem.get(position).get("Member_Id");
				try {
					Date lDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(listItem.get(position).get(sortName));
					if (memberId.equals("0")
							&& (lastDate == null || lDate.after(lastDate))
							&& setDate == 0) {
						lastDate = lDate;
						setDate = 1;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (memberId.equals(SearchActivity.memberId)) {
					holder.grab.setBackground(getResources().getDrawable(
							drawable.grab2));
					holder.grab.setText("已抢");
				} else {
					holder.grab.setBackground(getResources().getDrawable(
							drawable.grab));
					holder.grab.setText("抢");
				}
				holder.Order_DeliverTime.setText("发货时间："
						+ listItem.get(position).get("Order_DeliverTime")
								.substring(0, 10) + "");
				holder.Order_Start.setText(listItem.get(position).get(
						"Order_Start")
						+ "");
				holder.Order_End.setText(listItem.get(position)
						.get("Order_End") + "");
				String gtype = listItem.get(position).get("GType_Name") + "";
				String unit = listItem.get(position).get("Order_Unit") + "";
				String vtype = gtype
						+ " "
						+ Double.valueOf(
								listItem.get(position).get("Order_Num") + "")
								.intValue();
				if (unit.equals("false")) {
					// 重货
					vtype += "吨";
					holder.unit.setText("重货");
					holder.unit.setTextColor(getResources().getColor(
							color.type2));
					holder.unit.setBackground(getResources().getDrawable(
							drawable.border_2_nocorner));
				} else if (unit.equals("true")) {
					// 泡货 + "方");
					vtype += "方";
					holder.unit.setText("泡货");
					holder.unit.setTextColor(getResources().getColor(
							color.type3));
					holder.unit.setBackground(getResources().getDrawable(
							drawable.border_3_nocorner));
				}
				String vtypes = listItem.get(position).get("VType_Name") + "";
				String vsizes = listItem.get(position).get("VSize_Name") + "";
				if (!vtypes.equals("") && !vtypes.equals("null"))
					vtype += " " + vtypes;
				if (!vsizes.equals("") && !vsizes.equals("null"))
					vtype += " " + vsizes;
				holder.vtype.setText(vtype);
				holder.remark.setText("备注："
						+ listItem.get(position).get("Order_Remark") + "");
			}
			return convertView;
		}

	}

	public final class ViewHolder {
		public TextView Order_Start;
		public TextView Order_End;
		public TextView Order_DeliverTime;
		public TextView vtype;
		public TextView unit;
		public TextView remark;
		public TextView grab;
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.start_layout:
			value = "start";
			initDrowList();
			areaprovince.showAsDropDownleft(arg0, 0, 0);
			break;
		case R.id.end_layout:
			value = "end";
			initDrowList();
			areaprovince.showAsDropDownright(arg0, 0, 0);
			break;
		case R.id.exchange_layout:
			String startText = start.getText().toString();
			start.setText(end.getText());
			end.setText(startText);
			startText = null;
			init();
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private void initDrowList() {

		if (areaprovince == null) {
			View v = LayoutInflater.from(context).inflate(
					R.layout.area_select_lay, null);
			provincelistview = (ListView) v.findViewById(R.id.listview);
			citylistview = (ListView) v.findViewById(R.id.listview1);
			com.wheelselect.lib.AreaData areadata = new AreaData(context);
			List<ProvinceModel> provinceList = areadata.getProvinceList();
			selprovincelist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Name", "全国");
			ArrayList<HashMap<String, Object>> itemlist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> itemmap1 = new HashMap<String, Object>();
			itemmap1.put("Name", "全境");
			itemlist.add(itemmap1);
			map.put("city", itemlist);
			selprovincelist.add(map);
			for (int i = 0; i < provinceList.size(); i++) {
				HashMap<String, Object> provancemap = new HashMap<String, Object>();
				provancemap.put("Name", provinceList.get(i).getName());
				List<CityModel> citylist = provinceList.get(i).getCityList();
				ArrayList<HashMap<String, Object>> itemcitylist = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> a = new HashMap<String, Object>();
				a.put("Name", "全境");
				itemcitylist.add(a);
				a = null;
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
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					String province = selprovincelist.get(
							provinceadpter.getPosition()).get("Name")
							+ "";
					String city = cityitemlist.get(arg2).get("Name") + "";
					if (value.equals("start"))
						((TextView) getView().findViewById(R.id.start))
								.setText(province + " " + city);
					else if (value.equals("end")) {
						((TextView) getView().findViewById(R.id.end))
								.setText(province + " " + city);
					}
					areaprovince.dismiss();
					init();
				}
			});

			provincelistview.setOnItemClickListener(new OnItemClickListener() {
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
	}

	/**
	 * 对列表进行排序
	 * 
	 * @param list
	 * @param key
	 */
	private static void sort(ArrayList<HashMap<String, String>> list,
			List<String> key) {
		Collections.sort(listItem, new Comparator<HashMap<String, String>>() {

			public int compare(HashMap<String, String> arg0,
					HashMap<String, String> arg1) {
				if (arg1.get("Member_Id").compareTo(arg0.get("Member_Id")) == 0)
					return arg1.get(sortName).compareTo(arg0.get(sortName));
				else
					return arg1.get("Member_Id").compareTo(
							arg0.get("Member_Id"));
			}
		});
		listKey.clear();
		for (HashMap<String, String> map : listItem) {
			listKey.add(map.get(keyName));
		}

	}

}
