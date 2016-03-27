package com.sy.trucksysapp.page.order;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.color;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class DetailActivity extends BaseActivity {
	private String memberId = "";
	private HashMap<String, String> map;
	private Button call, grab;
	private boolean callEnable, editable;
	private LinearLayout state_layout, code_layout, ctime_layout;
	private TextView state;
	private String[] states = new String[] { "已抢单", "取货中", "送货中", "已到达", "已结算" };
	private int[] colors = new int[] { color.type1, color.type2, color.type4,
			color.type5, color.type5 };
	private int[] draws = new int[] { drawable.border_1, drawable.border_2,
			drawable.border_4, drawable.border_5, drawable.border_5 };

	private Context context;
	private DisplayImageOptions options;
	private ImageView pic;;
	private ImageView follow;

	public static final int ORDER_DEL = 0;
	public static final int ORDER_UNKNOWN = 1;

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		context = getApplicationContext();
		memberId = PreferenceUtils.getInstance(context).getSettingUserId();
		state_layout = (LinearLayout) findViewById(R.id.state_layout);
		code_layout = (LinearLayout) findViewById(R.id.code_layout);
		ctime_layout = (LinearLayout) findViewById(R.id.ctime_layout);
		state = (TextView) findViewById(R.id.state);
		Intent intent = getIntent();
		map = (HashMap<String, String>) intent.getSerializableExtra("obj");
		getData();
		map.put("Order_Num", Double.valueOf(map.get("Order_Num")).intValue()
				+ "");
		call = (Button) findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (callEnable) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + map.get("Ship_Mobile")));
					startActivity(intent);
				} else {
					CommonUtils.showToast(context, "请先抢单");
				}
			}
		});
		// 抢
		grab = (Button) findViewById(R.id.grab);
		grab.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (grab.getText().toString().equals("抢")) {
					if (map.get("OT_Type").equals(states[0]))
						CommonUtils.showToast(context, "您已抢过该单");
					else {
						if (("").equals(memberId)) {
							CommonUtils.showToast(context, "请先登录");
							return;
						} else
							// 抢单操作
							grab();
					}
				} else if (grab.getText().toString().equals("送达")) {
					// 送达操作
					send();
				}
			}
		});
		follow = (ImageView) findViewById(R.id.follow);
		follow.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (follow.getTag().toString().equals("follow")) {
					follow.setTag("followed");
					follow.setImageDrawable(getResources().getDrawable(
							drawable.ic_followed));
					CommonUtils.showToast(getApplicationContext(), "收藏成功");
				} else if (follow.getTag().toString().equals("followed")) {
					follow.setTag("follow");
					follow.setImageDrawable(getResources().getDrawable(
							drawable.ic_follow));
					CommonUtils.showToast(getApplicationContext(), "取消收藏成功");
				}
			}
		});
		init();
	}

	@Override
	public void finish() {
		if (editable) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("obj", map);
			intent.putExtras(bundle);
			setResult(100, intent);
		}
		super.finish();
	}

	@SuppressLint("NewApi")
	private void init() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_cargopic)
				.showImageForEmptyUri(R.drawable.default_cargopic)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// 判断单据状态
		String otype = map.get("OT_Type");
		int index = 0;
		if (otype.equals("已发布") && !map.get("Member_Id").equals(memberId)) {
			// 隐藏单据状态栏,订单详情,评星
			state_layout.setVisibility(View.GONE);
			code_layout.setVisibility(View.GONE);
			ctime_layout.setVisibility(View.VISIBLE);
			callEnable = false;
			call.setBackgroundColor(getResources().getColor(color.grey));
			grab.setBackgroundColor(getResources().getColor(color.red2));
		} else {
			callEnable = true;
			ctime_layout.setVisibility(View.GONE);
			state_layout.setVisibility(View.VISIBLE);
			code_layout.setVisibility(View.VISIBLE);
			call.setBackgroundColor(getResources().getColor(color.green));
			grab.setBackgroundColor(getResources().getColor(color.grey));
			if (map.get("Member_Id").equals(memberId) && otype.equals("已发布"))
				otype = "已抢单";
			map.put("OT_Type", otype);
			if (!map.get("Member_Id").equals("")) {
				for (String s : states) {
					if (s.equals(otype))
						break;
					index++;
				}
			}
			if (index == 2) {
				grab.setText("送达");
				grab.setBackgroundColor(getResources().getColor(color.red2));
			} else if (index != 0)
				grab.setVisibility(View.GONE);
			state.setText(otype);
			state.setTextColor(getResources().getColor(colors[index]));
			state.setBackground(getResources().getDrawable(draws[index]));
		}
		setText(R.id.start, map.get("Order_Start"));
		setText(R.id.end, map.get("Order_End"));
		setText(R.id.deliverTime, "发货日期  "
				+ map.get("Order_DeliverTime").subSequence(0, 10));
		setText(R.id.gtype, map.get("GType_Name"));
		TextView unit = (TextView) findViewById(R.id.unit);
		if (map.get("Order_Unit").equals("false")) {
			// 重货
			setText(R.id.num, map.get("Order_Num") + "吨");
			unit.setText("重货");
			unit.setTextColor(getResources().getColor(color.type2));
			unit.setBackground(getResources().getDrawable(
					drawable.border_2_nocorner));
		} else {
			setText(R.id.num, map.get("Order_Num") + "方");
			unit.setText("泡货");
			unit.setTextColor(getResources().getColor(color.type3));
			unit.setBackground(getResources().getDrawable(
					drawable.border_3_nocorner));
		}
		setText(R.id.remark, map.get("Order_Remark"));
		setText(R.id.vtypes, map.get("VType_Name"));
		setText(R.id.vsizes, map.get("VSize_Name"));

		// 设置图片
		if (map.get("Pic_Url") != null && !map.get("Pic_Url").equals("")) {
			pic = (ImageView) findViewById(R.id.Pic);
			final String url = SystemApplication.getOrderUrl()
					+ map.get("Pic_Url").substring(
							map.get("Pic_Url").indexOf("/Update") + 1);
			ImageLoader.getInstance().displayImage(url, pic, options);
			pic.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent intent = new Intent(context,
							ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
							new String[] { url });
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});
		}

		if (map.get("Order_CTime") != null
				&& !map.get("Order_CTime").equals(""))
			setText(R.id.ctime, map.get("Order_CTime").substring(0, 10));// 发布时间

		setText(R.id.code, map.get("Order_Code"));// 订单号
		if (map.get("AcceptDate") != null && !map.get("AcceptDate").equals(""))
			setText(R.id.grabtime, map.get("AcceptDate").subSequence(0, 16));// 下单时间
		if (map.get("OT_AcceptTime") != null
				&& !map.get("OT_AcceptTime").equals(""))
			setText(R.id.accepttime, map.get("OT_AcceptTime")
					.subSequence(0, 16));// 取货确认
		if (map.get("OT_SendTime") != null
				&& !map.get("OT_SendTime").equals(""))
			setText(R.id.sendtime, map.get("OT_SendTime").subSequence(0, 16));// 送达确认
		if (map.get("OT_SignTime") != null
				&& !map.get("OT_SignTime").equals(""))
			setText(R.id.signtime, map.get("OT_SignTime").subSequence(0, 16));// 签收确认
	}

	/**
	 * 查询明细
	 */
	@SuppressWarnings("static-access")
	private void getData() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetMemOrder";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("OrderId", map.get("Order_Id"));
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					int total = jsobj.getInt("total");
					if (total != 0) {
						map = CommonUtils.getMap(jsobj.getJSONArray("rows")
								.getJSONObject(0));
						if (map.get("Order_Del").equals("true"))
							fail("该货单已删除", ORDER_DEL);
					} else {
						fail("该货单已失效", ORDER_UNKNOWN);
					}
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

	/**
	 * 查询信息失败
	 * 
	 * @param msg
	 */
	private void fail(String msg, int state) {
		editable = false;
		Intent intent = new Intent();
		intent.putExtra("Msg", msg);
		intent.putExtra("State", msg);
		Bundle bundle = new Bundle();
		bundle.putSerializable("obj", map);
		intent.putExtras(bundle);
		setResult(200, intent);
		finish();
	}

	/**
	 * 抢单
	 */
	@SuppressWarnings("static-access")
	private void grab() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetMember";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("OrderId", map.get("Order_Id"));
		RequestParams.put("MemberId", memberId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						editable = true;
						JSONObject obj = jsobj.getJSONObject("Obj");
						map.put("OT_Type", "已抢单");
						map.put("Member_Id", memberId);
						map.put("AcceptDate", obj.getString("AcceptDate"));
						init();
					} else {
						if (jsobj.getString("Msg").contains("删除")) {
							map.put("Order_Del", "true");
							fail("该货单已删除", ORDER_DEL);
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

	/**
	 * 送达确认
	 * 
	 * @param position
	 *            位置
	 */
	@SuppressWarnings("static-access")
	private void send() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/SendConfirm";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("OrderId", map.get("Order_Id"));
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						editable = true;
						JSONObject obj = jsobj.getJSONObject("Obj");
						map.put("OT_Type", "已到达");
						map.put("OT_SendTime", obj.getString("OT_SendTime"));
						init();
					} else {
						if (jsobj.getString("Msg").contains("删除")) {
							map.put("Order_Del", "true");
							fail("该货单已删除", ORDER_DEL);
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

	private void setText(int id, String text) {
		((TextView) findViewById(id)).setText(text);
	}

	private void setText(int id, CharSequence text) {
		((TextView) findViewById(id)).setText(text);
	}
}
