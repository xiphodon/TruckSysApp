package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CouponModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class UserAccountmainRecordActivity extends BaseActivity {
	private ListView mListView;
	private ArrayList<CouponModel> datalist;
	Context context;
	private TextView leaverecord;
	RelativeLayout rel_record_detail;
	CheckBox check_show;
	RecordAdapter recordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ac_record);
		context = UserAccountmainRecordActivity.this;
		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		leaverecord = (TextView) findViewById(R.id.leaverecord);
		rel_record_detail = (RelativeLayout) findViewById(R.id.rel_record_detail);
		rel_record_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivityForResult(new Intent(context,
						UserAccountRecordActivity.class), 9999);
			}
		});
		check_show = (CheckBox) findViewById(R.id.check_show);
		check_show.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				executelist(check_show.isChecked());
			}
		});
		mListView = (ListView) findViewById(R.id.listview);
		datalist = new ArrayList<CouponModel>();
		recordAdapter = new RecordAdapter();
		mListView.setAdapter(recordAdapter);
		getaccountMo();
		executelist(false);
	}

	private void executelist(boolean checked) {
		// TODO Auto-generated method stub
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetCoupon";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("id", PreferenceUtils.getInstance(context)
				.getSettingUserId());
		if (checked) {
			RequestParams.put("type", "1");// 查询所有购物券
		} else {
			RequestParams.put("type", "2");// 查询全部购物券出去已过期的
		}
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				List<CouponModel> mlist = new ArrayList<CouponModel>();
				try {
					JSONObject json = new JSONObject(result);
					JSONArray array = json.getJSONArray("rows");
					for (int i = 0; i < array.length(); i++) {
						CouponModel m = new CouponModel();
						m.setCoup_EndTime(array.getJSONObject(i).getString(
								"Coup_EndTime"));
						m.setCoup_Money(array.getJSONObject(i).getDouble(
								"Coup_Money"));
						m.setCoup_StartTime(array.getJSONObject(i).getString(
								"Coup_StartTime"));
						m.setCoupon_desc(array.getJSONObject(i).getString(
								"MemberAccount"));
						m.setCoup_State(array.getJSONObject(i).getInt(
								"Coup_IsUse"));
						m.setCoupon_id(array.getJSONObject(i).getInt("Coup_Id"));
						m.setSelected(false);
						mlist.add(m);
					}
				} catch (Exception e) {

				}
				datalist.clear();
				datalist.addAll(mlist);
				recordAdapter.notifyDataSetChanged();
				super.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}
		});
	}

	public void getaccountMo() {
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetMemberAccount";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("memberId", PreferenceUtils.getInstance(context)
				.getSettingUserId());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						Double d = jsobj.getDouble("Obj");
						DecimalFormat df = new DecimalFormat("0.00");
						leaverecord.setText(df.format(d));
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
			}
		});
	}

	private class RecordAdapter extends BaseAdapter {

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
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.item_coupon, null);
				holder = new ViewHolder();
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.description = (TextView) convertView
						.findViewById(R.id.description);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			CouponModel model = datalist.get(position);
			DecimalFormat df = new DecimalFormat("0.00");
			holder.price.setText("￥" + df.format(model.getCoup_Money()));
			holder.description.setText(model.getCoupon_desc());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
			Date date = null;
			Date cdate = new Date();
			holder.date.setText(model.getCoup_EndTime() + "到期");
			try {
				date = sdf.parse(model.getCoup_EndTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (date != null) {
				if (date.getTime() > cdate.getTime()) {
					// 未过期
					if (model.getCoup_State() == 1) {
						holder.date.setText("已使用");
					}
				} else if (date.getTime() < cdate.getTime()) {
					holder.date.setText("已过期");
				} else {
				}
			}

			return convertView;
		}

	}

	class ViewHolder {
		private TextView price;
		private TextView description;;
		private TextView date;
	}
}
