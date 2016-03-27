package com.sy.trucksysapp.page.driver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.driver.adapter.NewsIndexAdapter;
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshBase.OnRefreshListener;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 新闻信息
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class NewshotFragment extends Fragment implements OnRefreshListener {
	private LoadingFrameLayout loading;
	private PullToRefreshListView pull_refresh_list;
	private ArrayList<HashMap<String, Object>> datalist;
	private NewsIndexAdapter hotAdapter;
	private ListView mListView;
	private int POSITIONUP = 1;
	private int POSITIONDOWN = -1;
	private int POSITIONNOMAL = 0;
	private int rows = 10, position;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_frament, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loading = (LoadingFrameLayout) getView().findViewById(R.id.loading);
		loading.getReLoadImage().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loading.dismiss();
				executedata(POSITIONNOMAL);
			}
		});
		pull_refresh_list = (PullToRefreshListView) getView().findViewById(
				R.id.pull_refresh_list);
		datalist = new ArrayList<HashMap<String, Object>>();
		hotAdapter = new NewsIndexAdapter(datalist, getActivity());
		pull_refresh_list.setPullRefreshEnabled(true);
		pull_refresh_list.setPullLoadEnabled(false);
		pull_refresh_list.setScrollLoadEnabled(true);
		mListView = pull_refresh_list.getRefreshableView();
		mListView.setAdapter(hotAdapter);
		pull_refresh_list.setOnRefreshListener(this);
		executedata(POSITIONNOMAL);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				position = arg2;
				HashMap<String, Object> map = null;
				if (arg3 != 0) {
					map = hotAdapter.mListItems.get(Integer.valueOf(arg3 + ""));
				}
				if (map != null) {
					Intent intent = new Intent(getActivity(),
							NewsDetailActivity.class);
					intent.putExtra("rowdata", map);
					startActivityForResult(intent, 100);
				}
			}
		});
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		executedata(POSITIONDOWN);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		executedata(POSITIONUP);
	}

	// 加载数据
	private void executedata(final int position) {
		// TODO Auto-generated method stub
		if (position == POSITIONNOMAL) {
			loading.show("正在努力获取数据...");
		}
		String url = SystemApplication.getBaseurl()
				+ "truckservice/GetNewsByCategory";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(1000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("cname", "热点");
		RequestParams.put(
				"Member_Id",
				PreferenceUtils.getInstance(
						getActivity().getApplicationContext())
						.getSettingUserId());
		if (position == POSITIONDOWN) {
			RequestParams.put("type", "up");
			RequestParams.put("startIndex","0");
			RequestParams.put("rows", rows + "");
		} else {
			RequestParams.put("type", "up");
			RequestParams.put("startIndex",
					(hotAdapter.mListItems.size() == 0 ? 0
							: hotAdapter.mListItems.size() - 1) + "");
			RequestParams.put("rows", rows + "");
		}
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				if (position == POSITIONUP) {
					// 上拉刷新
					hotAdapter.notifyDataSetChanged();
					pull_refresh_list.onPullUpRefreshComplete();
				} else if (position == POSITIONDOWN) {
					// 下拉加载
					hotAdapter.notifyDataSetChanged();
					pull_refresh_list.onPullDownRefreshComplete();
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					pull_refresh_list.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
				} else {
					// 默认加载
					hotAdapter.notifyDataSetChanged();
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					pull_refresh_list.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
					loading.setFail();
				}
				CommonUtils.onFailure(arg0, arg1, getActivity()
						.getApplicationContext());
			}

			@Override
			public void onSuccess(int arg0, String result) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0, result);
				ArrayList<HashMap<String, Object>> headmap = new ArrayList<HashMap<String, Object>>();
				ArrayList<HashMap<String, Object>> contentmap = new ArrayList<HashMap<String, Object>>();
				try {
					JSONObject json = new JSONObject(result);
					JSONObject datajson = json.getJSONObject("Data");
					JSONArray listarray = datajson.getJSONArray("list");
					JSONArray headarray = datajson.getJSONArray("head");
					for (int i = 0; i < listarray.length(); i++) {
						contentmap.add(getMap(listarray.getJSONObject(i)));
					}
					for (int i = 0; i < headarray.length(); i++) {
						headmap.add(getMap(headarray.getJSONObject(i)));
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (position == POSITIONDOWN) {
					// 下拉加载
					hotAdapter.mListItems.clear();
					if (hotAdapter.mListItems.size() != 0) {
						HashMap<String, Object> m = hotAdapter.mListItems
								.get(0);
						ArrayList<HashMap<String, Object>> head = (ArrayList<HashMap<String, Object>>) m
								.get("title");
						if (head.size() == 0) {
							m.put("title", headmap);
						}
						for (int i = contentmap.size(); i > 0; i--) {
							hotAdapter.mListItems.add(1, contentmap.get(i - 1));
						}
					} else {
						HashMap<String, Object> m = new HashMap<String, Object>();
						m.put("title", headmap);
						hotAdapter.mListItems.add(m);
						for (int i = contentmap.size(); i > 0; i--) {
							hotAdapter.mListItems.add(1, contentmap.get(i - 1));
						}
					}
				} else {
					// 上拉加载更多
						
					if (hotAdapter.mListItems.size() != 0) {
						HashMap<String, Object> m = hotAdapter.mListItems
								.get(0);
						ArrayList<HashMap<String, Object>> head = (ArrayList<HashMap<String, Object>>) m
								.get("title");
						if (head.size() == 0) {
							m.put("title", headmap);
						}
						hotAdapter.mListItems.addAll(contentmap);
					} else {
						HashMap<String, Object> m = new HashMap<String, Object>();
						m.put("title", headmap);
						hotAdapter.mListItems.add(m);
						hotAdapter.mListItems.addAll(contentmap);
					}
				}
				if (position == POSITIONUP) {
					// 上拉刷新
					hotAdapter.notifyDataSetChanged();
					pull_refresh_list.onPullUpRefreshComplete();
				} else if (position == POSITIONDOWN) {
					// 下拉加载
					hotAdapter.notifyDataSetChanged();
					pull_refresh_list.onPullDownRefreshComplete();
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					pull_refresh_list.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
				} else {
					// 默认加载
					hotAdapter.notifyDataSetChanged();
					loading.dismiss();
					Calendar c1 = Calendar.getInstance();
					c1.setTime(new Date());
					SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
					pull_refresh_list.getHeaderLoadingLayout()
							.setLastUpdatedLabel(format.format(c1.getTime()));
					if(contentmap.size()==0){
						loading.setEmpty();
						return;
					}else{
						loading.dismiss();
					}
				}
			}
		});
	}

	public static HashMap<String, Object> getMap(JSONObject jsonObject) {
		try {
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			String value;
			HashMap<String, Object> valueMap = new HashMap<String, Object>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.getString(key);
				if (value == null || value.equals("null"))
					value = null;
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (hotAdapter.getViewpager() != null) {
			hotAdapter.getViewpager().stopAutoScroll();
		}
		super.onDestroy();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (resultCode == 100) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) data
						.getSerializableExtra("rowdata");
				datalist.set(position, map);
				hotAdapter.notifyDataSetChanged();
			}
		}
	}
}
