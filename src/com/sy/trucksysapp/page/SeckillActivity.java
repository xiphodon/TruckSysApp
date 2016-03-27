package com.sy.trucksysapp.page;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.user.PaymentActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener;
import com.sy.trucksysapp.widget.AutoScrollViewPager;
import com.sy.trucksysapp.widget.TimeTextView;

/**
 * 限时秒杀界面
 * 
 * @author lxs 20150827
 * 
 */
public class SeckillActivity extends BaseActivity {

	private TimeTextView mTimeTextView;
	// private LoadingFrameLayout loading;
	private DialogConfirmView ConfirmView;
	private long days, hours, minutes, seconds;
	private Context context;
	private Button btn_product_buy;
	private String mTimestr;
	private boolean startOrend = true;
	private AutoScrollViewPager autoviewpager;
	private LinearLayout llPointGroup;
	private String[] imagepathArray;
	private int previousPointEnale = 0;
	private String tireid;
	private String userid, mobile;
	String gg = "";
	private TextView tv_old_price, tv_now_price, tv_sale_name, tv_left_number,
			tv_left_price;
	private WebView tv_product_property;
	private Dialog progDialog = null;// 进度条
	private String SecKillId;
	private String data;
	private int stocks=-1;
	private String tips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seckill);
		initView();
	}

	private void initView() {
		context = SeckillActivity.this;
		data = getIntent().getStringExtra("data");
		userid = PreferenceUtils.getInstance(context).getSettingUserId();
		mobile = PreferenceUtils.getInstance(context).getSettingMobile();
		mTimeTextView = (TimeTextView) findViewById(R.id.electricity_countdown);
		tv_now_price = (TextView) findViewById(R.id.tv_now_price);
		tv_old_price = (TextView) findViewById(R.id.tv_old_price);
		tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
		tv_left_price = (TextView) findViewById(R.id.tv_left_price);
		tv_sale_name = (TextView) findViewById(R.id.tv_sale_name);
		tv_left_number = (TextView) findViewById(R.id.tv_left_number);
		tv_product_property = (WebView) findViewById(R.id.web_product_property);
		autoviewpager = (AutoScrollViewPager) findViewById(R.id.bcviewpager);
		llPointGroup = (LinearLayout) findViewById(R.id.ll_point_group);
		autoviewpager.setOnPageChangeListener(new PosterPageChange());

		btn_product_buy = (Button) findViewById(R.id.btn_product_buy);
		// btn_product_buy.setEnabled(false);
		btn_product_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getSeckill();
			}
		});
		if (data != null) {
			SetGoodInfo();
		} else {
			CommonUtils.showToast(context, "秒杀活动已经结束！");
			SeckillActivity.this.finish();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 获取限时秒杀产品的信息
	 */
	private void SetGoodInfo() {

		try {
			JSONObject json = new JSONObject(data);

			if (json.getInt("total") > 0) {
				//
				JSONArray array = json.getJSONArray("rows");
				JSONObject data = array.getJSONObject(0);
				String systemTime = json.getString("obj");
				tireid = data.getString("TireId");
				String startTime = data.getString("SecKillBeginTime");
				String endTime = data.getString("SecKillEndTime");
				CompareTime(systemTime, startTime, endTime);
				initTopView(data.get("Pic1").toString());
				SecKillId = data.getString("SecKillId");
				stocks = data.getInt("Stock");
				SetTube(data);
			} else {
				CommonUtils.showToast(context, "活动已经结束,请预约下一轮活动！");
				startActivity(new Intent(context, ReserveActivity.class));
				SeckillActivity.this.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonUtils.showToast(context, "数据解析异常,请联系管理员！");
		}

		// }

		// });

	}

	/**
	 * 设置轮胎数据
	 */
	private void SetTube(JSONObject json) {
		try {
			tv_now_price.setText("¥" + json.getString("Price"));
			tv_old_price.setText("市场价：¥" + json.getString("MarketPrice"));
			tv_sale_name.setText(json.getString("Title"));
			tv_left_number.setText("库存：" + json.getString("Stock") + "件");
			tv_left_price
					.setText("省"
							+ (json.getDouble("MarketPrice") - json
									.getDouble("Price")));
			tips = json.getString("Brand_Name")+"("+json.getString("SpecName")+")";
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = json.getString("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + json.getString("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + json.getString("SpecName"));
			gg = json.getString("SpecName");
			builder.append("</label></span></li>");

			builder.append("<li><span><label>");
			if (json.getString("IsInnerTube") != null
					&& json.getString("IsInnerTube").equals("1")) {
				builder.append("有内胎：是");
			} else {
				builder.append("有内胎：否");
			}
			builder.append("</label></span></li>");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				builder.append("<li><span><label>");
				builder.append(item.getString("key") + ":"
						+ item.getString("value"));
				builder.append("</label></span></li>");
			}
			builder.append("</ul>");
			tv_product_property.loadData(builder.toString(),
					"text/html; charset=UTF-8", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 提交订单
	 */
	private void PostOrder() {
		showProgressDialog();
		String url = SystemApplication.getBaseurl()
				+ "TruckService/SecKillOrderAdd";

		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("userid", userid);
		RequestParams.put("tireid", tireid);
		RequestParams.put("SecKillId", SecKillId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				dissmissProgressDialog();
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				dissmissProgressDialog();
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						JSONObject data = json.getJSONObject("Data");
						context.startActivity(new Intent(context,
								PaymentActivity.class).putExtra("OrderNumber",
								data.getString("OrderNumber")).putExtra(
								"OrderTotalPrice",
								data.getString("OrderTotalPrice")));
					} else {
						CommonUtils.showToast(context, json.getString("Msg"));
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		});
	}

	/**
	 * 获取秒杀的机会
	 */
	private void getSeckill() {
		String url = SystemApplication.getBaseurl() + "TruckService/SecKill";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("userid", userid);
		RequestParams.put("tireid", tireid);
		RequestParams.put("SecKillId", SecKillId);
		showProgressDialog();
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				dissmissProgressDialog();
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				dissmissProgressDialog();
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						showDialog();
					} else {
						CommonUtils.showToast(context, json.getString("Msg"));
						startActivity(new Intent(context, ReserveActivity.class));
						SeckillActivity.this.finish();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}

	private void showDialog() {

		ConfirmView = new DialogConfirmView(context, "提交订单", tips+"\n抢购成功，(请确定是否与你的车型吻合)"+"确认是否提交该订单？",
				new OnConfirmListener() {
					@Override
					public void OnConfirm() {
						// TODO Auto-generated method stub
						if(stocks<=0){
							CommonUtils.showToast(context, "商品已经售罄，请预约下一次活动！");
							startActivity(new Intent(context, ReserveActivity.class));
							SeckillActivity.this.finish();
						}else{
							PostOrder();
						}
						
						ConfirmView = null;
					}
				});
		ConfirmView.show();
	}

	private void CompareTime(String system, String start, String end) {
		Date systemDate = parseDateTime(system);
		Date startDate = parseDateTime(start);
		Date endDate = parseDateTime(end);

		Calendar systemT = Calendar.getInstance();
		systemT.setTime(systemDate);

		Calendar startT = Calendar.getInstance();
		startT.setTime(startDate);

		Calendar endT = Calendar.getInstance();
		endT.setTime(endDate);

		// 活动未开始
		if (systemT.getTimeInMillis() < startT.getTimeInMillis()) {
			mTimestr = "距活动开始：";
			GetTimes(system, start);
			btn_product_buy.setEnabled(false);
			mHandler.removeMessages(0);
			// 每隔1秒钟发送一次handler消息
			mHandler.sendEmptyMessageDelayed(0, 1000);
			startOrend = true;
		} else if (systemT.getTimeInMillis() > startT.getTimeInMillis()
				&& systemT.getTimeInMillis() < endT.getTimeInMillis()) {
			mTimestr = "距活动结束：";
			GetTimes(system, end);
			btn_product_buy.setEnabled(true);
			mHandler.removeMessages(0);
			// 每隔1秒钟发送一次handler消息
			mHandler.sendEmptyMessageDelayed(0, 1000);
			startOrend = false;
		} else if (systemT.getTimeInMillis() > endT.getTimeInMillis()) {
			mTimestr = "活动已经结束";
			GetTimes("0000-00-00 00:00:00", "0000-00-00 00:00:00");
			CommonUtils.showToast(context, "活动已经结束");
			btn_product_buy.setEnabled(false);
			startActivity(new Intent(context, ReserveActivity.class));
			SeckillActivity.this.finish();
		}
	}

	/**
	 * 获取时间
	 */
	private void GetTimes(String start, String end) {
		Date startDate = parseDateTime(start);
		Date endDate = parseDateTime(end);

		Calendar startT = Calendar.getInstance();
		startT.setTime(startDate);
		Calendar endT = Calendar.getInstance();
		endT.setTime(endDate);

		long between = (endT.getTimeInMillis() - startT.getTimeInMillis()) / 1000;// 获得秒
		days = between / (24 * 3600);
		hours = between % (24 * 3600) / 3600;
		minutes = between % 3600 / 60;
		seconds = between % 60;
		mTimeTextView.setTime(days, hours, minutes, seconds, mTimestr);

	}

	/**
	 * 将字符串形式的日期时间表示解析为时间对象
	 * 
	 * @param timeString
	 * @return
	 */
	private Date parseDateTime(String timeString) {
		try {
			return DateUtils.parseDate(timeString, new String[] {
					"yyyy-MM-dd HH:mm:ss", "yyyy-M-d H:m:s",
					"yyyy-MM-dd H:m:s", "yyyy-M-d HH:mm:ss",
					"yyyy-MM-dd HH:mm:ss|SSS", "yyyyMMdd HH:mm:ss",
					"yyyy-MM-dd HH:mm", "yyyy-MM-dd" });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新ui的handler
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mTimeTextView.flag) {
					mHandler.sendEmptyMessageDelayed(0, 1000);
				} else {
					if (startOrend) {
						btn_product_buy.setEnabled(true);
					} else {
						btn_product_buy.setEnabled(false);
					}
				}
				break;
			}
		}
	};

	/**
	 * 加载头部图片
	 * 
	 * @param pics
	 */
	private void initTopView(String pics) {
		if (pics != null) {
			String[] array = pics.split("\\|");
			imagepathArray = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				imagepathArray[i] = SystemApplication.getServiceUrl()
						+ array[i];
			}
			LayoutParams params;
			// 初始化广告条资源
			for (int i = 0; i < imagepathArray.length; i++) {
				// 初始化广告条正下方的"点"
				View dot = new View(context);
				dot.setBackgroundResource(R.drawable.point_background);
				params = new LayoutParams(10, 10);
				params.leftMargin = 10;
				dot.setLayoutParams(params);
				dot.setEnabled(false);
				llPointGroup.addView(dot);
			}
			TopviewAdapter adapter = new TopviewAdapter(imagepathArray,
					context, autoviewpager, true);
			autoviewpager.setAdapter(adapter);
			llPointGroup.getChildAt(0).setEnabled(true);

		}

	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在处理请求...");
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

	/**
	 * 页面切换事件的监听
	 * 
	 * @author Administrator
	 * 
	 */
	class PosterPageChange implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			llPointGroup.getChildAt(previousPointEnale).setEnabled(false);
			// 消除上一次的状态点
			// 设置当前的状态点“点”
			llPointGroup.getChildAt(position % imagepathArray.length)
					.setEnabled(true);
			previousPointEnale = position % imagepathArray.length;
		}
	}

	// /**
	// * 弹出二维码分享
	// */
	// private void showReserveDialog() {
	// final Dialog codeDialog = new Dialog(context,
	// R.style.Translucent_NoTitle);
	// View view = LayoutInflater.from(context).inflate(
	// R.layout.dialog_reserve, null);
	// codeDialog.setContentView(view);
	// codeDialog.setCanceledOnTouchOutside(true);
	// ImageView img_close_share = (ImageView)
	// view.findViewById(R.id.img_close_share);
	// Button btn_reserve = (Button)view.findViewById(R.id.btn_reserve);
	// btn_reserve.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// // TODO Auto-generated method stub
	// ReserveNext(codeDialog);
	// }
	// });
	// // tv_show_sharecode = (TextView) view
	// // .findViewById(R.id.tv_show_sharecode);
	// // tv_top_title_share = (TextView) view
	// // .findViewById(R.id.tv_top_title_share);
	// // String code = MyShareprefrence.getInstance(context).getPromoteCode();
	// // tv_show_sharecode.setText(show_code_describe + "我的邀请码：" + code);
	// // tv_top_title_share.setText(show_code_desBottom);
	// img_close_share.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// codeDialog.dismiss();
	// }
	// });
	// Window dialogWindow = codeDialog.getWindow();
	// // WindowManager.LayoutParams lp = dialogWindow.getAttributes();
	// dialogWindow.setGravity(Gravity.CENTER);
	// codeDialog.show();
	// }
	// /**
	// * 提交预约
	// */
	// private void ReserveNext(final Dialog codeDialog) {
	// // 从服务器 获取商品的详情信息
	// String url = SystemApplication.getBaseurl() + "TruckService/AddBooking";
	// AsyncHttpClient client = new AsyncHttpClient();
	// RequestParams RequestParams = new RequestParams();
	// RequestParams.put("MemberId", userid);
	// RequestParams.put("MemberMobile", mobile);
	// client.post(url, RequestParams, new AsyncHttpResponseHandler() {
	//
	// @Override
	// public void onFailure(Throwable arg0, String result) {
	// // TODO Auto-generated method stub
	// super.onFailure(arg0, result);
	//
	// }
	//
	// @Override
	// public void onSuccess(String result) {
	// // TODO Auto-generated method stub
	// super.onSuccess(result);
	//
	// try {
	// codeDialog.dismiss();
	// JSONObject json = new JSONObject(result);
	// if (json.getBoolean("State")) {
	// CommonUtils.showToast(context, "预约成功！");
	// SeckillActivity.this.finish();
	// } else {
	// CommonUtils.showToast(context, "预约失败！");
	// }
	//
	// } catch (Exception e) {
	//
	// }
	//
	// }
	//
	// });
	// }
}
