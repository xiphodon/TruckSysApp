package com.sy.trucksysapp.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class OrderFrament extends Fragment {

	private int type;
	private OrderFramentAdapter adapter;
	private ListView listview;
	private List<HashMap<String, Object>> list;
	private Dialog progDialog = null;// 搜索时进度条
	private LinearLayout li_reLoad;
	private ImageView reLoadImage;
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(getActivity(), "正在获取订单数据...");
		progDialog.show();
	}
	
	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	public OrderFrament() {
		super();
	}

	public OrderFrament(int type) {
		super();
		this.type = type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.frament_listview, container,
				false);
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		initdata();
		super.onResume();
	}
	
	
	private void initdata() {
		// TODO Auto-generated method stub
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetOrderList";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("type", type + "");
		RequestParams.put("id", PreferenceUtils.getInstance(getActivity())
				.getSettingUserId());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				List<HashMap<String, Object>> mlist = new ArrayList<HashMap<String, Object>>();
				try {
					JSONArray array = new JSONArray(result);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("IsSale", obj.getString("IsSale"));
						map.put("OrderNumber", obj.getString("OrderNumber"));
						map.put("OrderState", obj.getString("OrderState"));
						map.put("OrderTitle", obj.getString("ServiceName"));
						JSONArray itemarray = obj
								.getJSONArray("ListOrderDetails");
						List<HashMap<String, String>> itemlist = new ArrayList<HashMap<String, String>>();
						for (int j = 0; j < itemarray.length(); j++) {
							itemlist.add(CommonUtils.getMap(itemarray
									.getJSONObject(j)));
						}
						map.put("OrderRows", itemlist);
						mlist.add(map);
					}
//					if(array.length()==0){
//						CommonUtils.showToast(getActivity(), "未查询到相关订单！");
//					}

				} catch (Exception e) {

				}
				list.clear();
				list.addAll(mlist);
				adapter.notifyDataSetChanged();
				if(list.size()==0){
					li_reLoad.setVisibility(View.VISIBLE);
				}
				dissmissProgressDialog();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				li_reLoad.setVisibility(View.VISIBLE);
				dissmissProgressDialog() ;
			}
		});

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listview = (ListView) getView().findViewById(R.id.listview);
		li_reLoad = (LinearLayout) getView().findViewById(R.id.li_reLoad);
		reLoadImage = (ImageView) getView().findViewById(R.id.reLoadImage);
		reLoadImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				li_reLoad.setVisibility(View.GONE);
				initdata();
			}
		});
		list = new ArrayList<HashMap<String, Object>>();
		adapter = new OrderFramentAdapter(type, list, getActivity(),new OrderStatusChangeListenner() {
			@Override
			public void Orderchanged() {
				// TODO Auto-generated method stub
				initdata();
			}
		});
		listview.setAdapter(adapter);
		showProgressDialog();
		initdata();
	}

}
