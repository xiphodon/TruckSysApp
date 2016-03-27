package com.sy.trucksysapp.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.pullrefresh.ui.PullToRefreshListView;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

public class UserAccountRecordActivity extends BaseActivity {
	private PullToRefreshListView mPullRefreshListView;
	private com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	private ListView mListView;
	private ArrayList<HashMap<String, String>> datalist;
	MlistAdapter MlistAdapter;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_record);
		context = UserAccountRecordActivity.this;
		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setPullRefreshEnabled(false);
		mPullRefreshListView.setPullLoadEnabled(false);
		mPullRefreshListView.setScrollLoadEnabled(false);
		// mPullRefreshListView
		// .setOnRefreshListener(new OnRefreshListener<ListView>() {
		// @Override
		// public void onPullDownToRefresh(
		// PullToRefreshBase<ListView> refreshView) {
		// // TODO Auto-generated method stub
		// }
		// @Override
		// public void onPullUpToRefresh(
		// PullToRefreshBase<ListView> refreshView) {
		// // TODO Auto-generated method stub
		// }
		// });
		datalist = new ArrayList<HashMap<String, String>>();
		mListView = mPullRefreshListView.getRefreshableView();
		MlistAdapter = new MlistAdapter();
		mListView.setAdapter(MlistAdapter);
		executeList();
	}

	public void executeList() {
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetMemberRecharge";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("memberId", PreferenceUtils.getInstance(context)
				.getSettingUserId());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				super.onSuccess(result);
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				try {
					JSONObject jsobj = new JSONObject(result);
					JSONArray array = jsobj.getJSONArray("rows");
					for (int i = 0; i < array.length(); i++) {
						list.add(CommonUtils.getMap(array.getJSONObject(i)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				datalist.addAll(list);
				MlistAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
			}
		});

	}

	class MlistAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datalist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return datalist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			HashMap<String, String> map = datalist.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_account_record, null);
				holder.tv_type = (TextView) convertView
						.findViewById(R.id.tv_type);
				holder.tv_num = (TextView) convertView
						.findViewById(R.id.tv_num);
				holder.tv_number = (TextView) convertView
						.findViewById(R.id.tv_number);
				holder.tv_date = (TextView) convertView
						.findViewById(R.id.tv_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			DecimalFormat decimalFormat = new DecimalFormat("0.00");//
			// 构造方法的字符格式这里如果小数不足2位,会以0补足.
			String p = decimalFormat.format(Double.parseDouble(map
					.get("MeRe_Money")));// format 返回的是字符串
			holder.tv_type.setText("消费");
			holder.tv_num.setText(p);
			holder.tv_number.setText(map.get("MeRe_Order"));
			holder.tv_date.setText(map.get("MeRe_CreateDate"));
			if (map.get("MeRe_Type").equals("1")) {
				holder.tv_type.setText("充值");
				holder.tv_num.setText("+"+p);
			}
			return convertView;
		}
	}

	class ViewHolder {
		TextView tv_type, tv_num, tv_number, tv_date;
	}
}
