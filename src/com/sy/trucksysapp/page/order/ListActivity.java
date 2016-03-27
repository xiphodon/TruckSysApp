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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ListActivity extends Fragment {
	private static PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private static ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private static ArrayList<String> listKey = new ArrayList<String>();
	private final static String keyName = "Order_Id";
	private final static String sortName = "AcceptDate";
	private static String memberId = "";
	private static Date lastDate = null;
	private static MyAdapter adapter = null;

	private static int page = 1, rows = 10, total = 0, index = 0, setDate = 0;

	private static Context context;
	ArrayList<HashMap<String, Object>> selprovincelist, cityitemlist;
	ProvinceAdapter provinceadpter;
	CityAdapter cityadpter;
	ListView provincelistview;
	ListView citylistview;

	private String[] states = new String[] { "已抢单", "取货中", "送货中", "已到达", "已结算" };
	private int[] colors = new int[] { color.type1, color.type2, color.type4,
			color.type5, color.type5 };
	private int[] draws = new int[] { drawable.border_1, drawable.border_2,
			drawable.border_4, drawable.border_5, drawable.border_5 };

	/**
	 * 货单-登录(退出)时执行当前操作
	 */
	public static void refresh() {
		memberId = PreferenceUtils.getInstance(context).getSettingUserId();
		listItem.clear();
		listKey.clear();
		if (memberId.equals("") || memberId.equals("null"))
			adapter.notifyDataSetChanged();
		else
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
		View view = inflater.inflate(R.layout.order_list, container, false);
		context = getActivity();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
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

	/**
	 * 送达确认
	 * 
	 * @param position
	 *            位置
	 */
	@SuppressWarnings("static-access")
	private void send(final int position) {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/SendConfirm";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("OrderId", listItem.get(position).get("Order_Id"));
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						JSONObject obj = jsobj.getJSONObject("Obj");
						HashMap<String, String> map = listItem.get(position);
						map.put("OT_Type", "已到达");
						map.put("OT_SendTime", obj.getString("OT_SendTime"));
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

	@SuppressWarnings("static-access")
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
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetOrder";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("searchType", "list");
		RequestParams.put("Member_Id", memberId);
		RequestParams.put("currPage", page + "");
		RequestParams.put("lastDate", new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(lastDate == null ? new Date()
				: lastDate));
		RequestParams.put("pagesize", rows + "");
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
							if (map.get("OT_Type").equals("已发布"))
								map.put("OT_Type", "已抢单");
							else if (map.get("OT_Type").equals("在途"))
								map.put("OT_Type", "送货中");
							if (!listKey.contains(key)) {
								list.add(map);
								listKey.add(key);
							} else
								listItem.set(listKey.indexOf(key), map);
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

		@Override
		public int getCount() {
			return listItem.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listItem.get(arg0);
		}

		@Override
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
					convertView = mInflater.inflate(R.layout.order_list_item,
							null);
					holder.Order_DeliverTime = (TextView) convertView
							.findViewById(R.id.deliverTime);
					holder.Order_Start = (TextView) convertView
							.findViewById(R.id.start);
					holder.Order_End = (TextView) convertView
							.findViewById(R.id.end);
					holder.gtype = (TextView) convertView
							.findViewById(R.id.gtype);
					holder.state = (TextView) convertView
							.findViewById(R.id.state);
					holder.num = (TextView) convertView.findViewById(R.id.num);
					holder.unit = (TextView) convertView
							.findViewById(R.id.unit);
					holder.remark = (TextView) convertView
							.findViewById(R.id.remark);
					holder.call = (LinearLayout) convertView
							.findViewById(R.id.call);
					holder.send = (LinearLayout) convertView
							.findViewById(R.id.send);
				} else {
					convertView = mInflater.inflate(R.layout.item_blank, null);
				}
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (type == TYPENOMAL) {
				holder.call.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri
								.parse("tel:"
										+ listItem.get(position).get(
												"Ship_Mobile")));
						startActivity(intent);
					}
				});
				holder.send.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						send(position);
					}
				});
				int index = 0;
				String otype = listItem.get(position).get("OT_Type");
				for (String s : states) {
					if (s.equals(otype))
						break;
					index++;
				}
				try {
					Date lDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(listItem.get(position).get(sortName));
					if ((lastDate == null || lDate.after(lastDate))
							&& setDate == 0) {
						lastDate = lDate;
						setDate = 1;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (index == 2)
					holder.send.setVisibility(View.VISIBLE);
				else
					holder.send.setVisibility(View.GONE);
				holder.state.setText(otype);
				holder.state.setTextColor(getResources()
						.getColor(colors[index]));
				holder.state.setBackground(getResources().getDrawable(
						draws[index]));
				holder.Order_DeliverTime.setText(listItem.get(position)
						.get("Order_DeliverTime").subSequence(0, 10));
				holder.Order_Start.setText(listItem.get(position).get(
						"Order_Start"));
				holder.Order_End.setText(listItem.get(position)
						.get("Order_End"));
				String gtype = listItem.get(position).get("GType_Name");
				String unit = listItem.get(position).get("Order_Unit") + "";

				String vtype = Double.valueOf(
						listItem.get(position).get("Order_Num") + "")
						.intValue()
						+ "";
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
				holder.gtype.setText(gtype);
				holder.num.setText(vtype);
				holder.remark.setText("备注："
						+ listItem.get(position).get("Order_Remark") + "");
			}
			return convertView;
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

	public final class ViewHolder {
		public TextView Order_Start;
		public TextView Order_End;
		public TextView Order_DeliverTime;
		public TextView gtype;
		public TextView num;
		public TextView unit;
		public TextView remark;
		public LinearLayout call;
		public LinearLayout send;
		public TextView state;
	}
}
