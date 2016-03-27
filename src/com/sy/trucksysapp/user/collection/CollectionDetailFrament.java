package com.sy.trucksysapp.user.collection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.driver.HotelDetailActivity;
import com.sy.trucksysapp.page.driver.NewsDetailActivity;
import com.sy.trucksysapp.page.driver.ParkDetailActivity;
import com.sy.trucksysapp.page.driver.RestaurantDetailActivity;
import com.sy.trucksysapp.page.driver.adapter.HotelListAdapter;
import com.sy.trucksysapp.page.driver.adapter.NewsFavoriteAdapter;
import com.sy.trucksysapp.page.driver.adapter.ParkListAdapter;
import com.sy.trucksysapp.page.driver.adapter.RestaurantListAdapter;
import com.sy.trucksysapp.page.gas.GasAdapter;
import com.sy.trucksysapp.page.gas.GasDetailActivity;
import com.sy.trucksysapp.page.service.ServiceAdapter;
import com.sy.trucksysapp.page.service.ServicesDetailActivity;
import com.sy.trucksysapp.page.shoping.SaleDetailActivity;
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.SerializableMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressWarnings("rawtypes")
@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
public class CollectionDetailFrament extends Fragment implements
		OnRefreshListener, OnClickListener {
	private int index, position;
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	private ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private final String sortName = "Favo_CreateDate";
	private String MemberId, title;
	private String[] types = new String[] { "Tire", "LubeOil", "Rim",
			"InnerTube", "Service", "Restaurant", "Hotel",
			"GasStation", "News" };
	private String[] keyNames = new String[] { "TireId", "LubeId", "RimId",
			"InnerTubeId", "FsSh_Id,ServiceType", "Rest_Id",
			"Hote_Id", "GaSt_Id", "News_Id" };
	private Date lastDate = null;
	private int page = 1, rows = 10, total = 0, setDate = 0;
	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private View view;
	private Context context;
	private Intent intent;
	private BaseAdapter baseAdapter;

	public CollectionDetailFrament() {
	}

	public CollectionDetailFrament(int index) {
		this.index = index;
		this.title = CollectionActivity.content[index];
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.collection_list, container, false);
		context = view.getContext();
		return view;
	}

	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		listItem = new ArrayList<HashMap<String, String>>();
		mPullRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(true);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(true);
		mListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setOnRefreshListener(this);
		initIntent();
		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				if (title.equals("维修救援")) {
					intent.putExtra("rowdata", listItem.get(arg2));
				} else if (title.equals("加油加气")) {
					intent.putExtra("rowdata", listItem.get(arg2));
				} else if (title.equals("停车")) {
					HashMap<String, String> data = listItem.get(arg2);
					final SerializableMap myMap = new SerializableMap();
					myMap.setMap(data);// 将map数据添加到封装的myMap<span></span>中
					Bundle bundle = new Bundle();
					bundle.putSerializable("parkInfo", myMap);
					bundle.putDouble("Latitude", 0.0);
					bundle.putDouble("Longitude", 0.0);
					intent.putExtras(bundle);
				} else if (title.equals("餐饮")) {
					intent.putExtra("rowdata", listItem.get(arg2));
				} else if (title.equals("住宿")) {
					intent.putExtra("rowdata", listItem.get(arg2));
				} else if (title.equals("轮胎")) {
					HashMap<String, String> data = listItem.get(arg2);
					intent.putExtra("saleid", data.get("TireId"));
					intent.putExtra("Type", 1);
				} else if (title.equals("内胎/垫带")) {
					HashMap<String, String> data = listItem.get(arg2);
					intent.putExtra("saleid", data.get("InnerTubeId"));
					intent.putExtra("Type", 2);
				} else if (title.equals("轮辋")) {
					HashMap<String, String> data = listItem.get(arg2);
					intent.putExtra("saleid", data.get("RimId"));
					intent.putExtra("Type", 3);
				} else if (title.equals("润滑油")) {
					HashMap<String, String> data = listItem.get(arg2);
					intent.putExtra("saleid", data.get("LubeId"));
					intent.putExtra("Type", 4);
				} else if (title.equals("资讯")) {
					intent.putExtra("rowdata", listItem.get(arg2));
				}
				startActivityForResult(intent, 100);
			}
		});
		if (baseAdapter != null) {
			mListView.setAdapter(baseAdapter);
			baseAdapter.notifyDataSetChanged();
		}
		getData("up");
	}

	/**
	 * 初始化适配器及内容
	 */
	private void initIntent() {
		if (title.equals("轮胎")) {
			intent = new Intent(view.getContext(), SaleDetailActivity.class);
			baseAdapter = new HotSaleAdapter(context, listItem);
		} else if (title.equals("润滑油")) {
			intent = new Intent(view.getContext(), SaleDetailActivity.class);
			baseAdapter = new HotSaleAdapter(context, listItem);
		} else if (title.equals("轮辋")) {
			intent = new Intent(view.getContext(), SaleDetailActivity.class);
			baseAdapter = new HotSaleAdapter(context, listItem);
		} else if (title.equals("内胎/垫带")) {
			intent = new Intent(view.getContext(), SaleDetailActivity.class);
			baseAdapter = new HotSaleAdapter(context, listItem);
		} else if (title.equals("维修救援")) {
			intent = new Intent(view.getContext(), ServicesDetailActivity.class);
			baseAdapter = new ServiceAdapter(context, listItem);
		} else if (title.equals("停车")) {
			intent = new Intent(context, ParkDetailActivity.class);
			baseAdapter = new ParkListAdapter(context, listItem);
		} else if (title.equals("餐饮")) {
			intent = new Intent(context, RestaurantDetailActivity.class);
			baseAdapter = new RestaurantListAdapter(context, listItem);
		} else if (title.equals("住宿")) {
			intent = new Intent(context, HotelDetailActivity.class);
			baseAdapter = new HotelListAdapter(context, listItem);
		} else if (title.equals("加油加气")) {
			intent = new Intent(context, GasDetailActivity.class);
			baseAdapter = new GasAdapter(context, listItem);
		} else if (title.equals("资讯")) {
			intent = new Intent(context, NewsDetailActivity.class);
			baseAdapter = new NewsFavoriteAdapter(context, listItem);
		}
	}

	/**
	 * 获取数据
	 */
	private void getData(final String type) {
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
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetFavoriteList";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("Member_Id", MemberId);
		RequestParams.put("type", types[index]);
		RequestParams.put("currPage", page + "");
		RequestParams.put("lastDate",
				sdf.format(lastDate == null ? new Date() : lastDate));
		RequestParams.put("pageSize", rows + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					total = jsobj.getInt("total");
					int size = listKey.size();
					ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
					if (total != 0) {
						JSONArray row = jsobj.getJSONArray("rows");
						String[] keys = new String[] { keyNames[index] };
						if (keyNames[index].indexOf(",") != -1)
							keys = keyNames[index].split("\\,");
						for (int i = 0, j = row.length(); i < j; i++) {
							HashMap<String, String> map = CommonUtils
									.getMap((JSONObject) row.get(i));
							Date lDate = sdf.parse(map.get(sortName));
							if (setDate == 0
									&& (lastDate == null || lDate
											.after(lastDate))) {
								lastDate = lDate;
								setDate = 1;
							}
							String key = "";
							for (String k : keys)
								key += map.get(k) + ",";
							if (!listKey.contains(key)) {
								list.add(map);
								listKey.add(key);
							} else {
								listItem.set(listKey.indexOf(key), map);
							}
						}
						if (type.equals("down")) {
							total = size + list.size();
							size = 0;
						}
					}
					if (list.size() > 0)
						listItem.addAll(size, list);
					if (baseAdapter != null)
						baseAdapter.notifyDataSetChanged();
					if (total == listItem.size()) {
						mPullRefreshListView.setHasMoreData(false);
					}
					if (type.equals("up"))
						mPullRefreshListView.onPullUpRefreshComplete();
					else
						mPullRefreshListView.onPullDownRefreshComplete();
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					mPullRefreshListView.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		HashMap<String, String> map = null;
		if (requestCode == 100) {
			if (resultCode == 100) {
				map = (HashMap<String, String>) data
						.getSerializableExtra("rowdata");
				try {
					listItem.set(position, map);
					if (!map.get(sortName).equals("")) {
						lastDate = sdf.parse(map.get(sortName));
					} else {
						// listItem.remove(position);
						if (position == 0) {
							for (HashMap<String, String> m : listItem) {
								if (!m.get(sortName).equals("")) {
									lastDate = sdf.parse(m.get(sortName));
									break;
								}
							}
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				baseAdapter.notifyDataSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		getData("down");

	}

	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		getData("up");
	}

	public void onClick(View arg0) {

	}

}
