package com.sy.trucksysapp.alixpay;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.user.ShoplistActivity;
import com.sy.trucksysapp.utils.CommonUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/***
 * 账户余额支付
 * @author Administrator
 *
 */
public class AccountPay {

	private Context context;
	ProgressDialog pd;
	private Handler mHandler;
	private String ordernumber;

	public AccountPay(Context context, ProgressDialog pd) {
		this.context = context;
		this.pd=pd;
		Init();
	}

	public void Init() {
		if (pd == null) {
			pd = new ProgressDialog(context);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					pd.dismiss();
				}
			});
		}
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void payPos(String ordernumber) {
		pd.show();
		pd.setMessage("正在付款...");
		String url = SystemApplication.getBaseurl()
				+ "TruckService/OrderStateChange";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("orderNumber", ordernumber);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT)
								.show();
						((Activity) context).finish();
						context.startActivity(new Intent(context,
								ShoplistActivity.class).putExtra("currentPage", 2));
					} else {
						Toast.makeText(context,
								jsobj.getString("Msg") + ",支付失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
					CommonUtils.showToast(context, "付款失败," + e.getMessage()
							+ "“！");
				}
				pd.dismiss();
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				CommonUtils.showToast(context, "付款失败," + arg1 + "“！");
				super.onFailure(arg0, arg1);
				pd.dismiss();
			}
		});
	}
	
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String ordernumber) {
		pd.show();
		pd.setMessage("正在付款...");
		String url = SystemApplication.getBaseurl()
				+ "TruckService/MemberPayment";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("orderNumber", ordernumber);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT)
								.show();
						((Activity) context).finish();
						context.startActivity(new Intent(context,
								ShoplistActivity.class).putExtra("currentPage", 2));
					} else {
						Toast.makeText(context,
								jsobj.getString("Msg") + ",支付失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
					CommonUtils.showToast(context, "付款失败," + e.getMessage()
							+ "“！");
				}
				pd.dismiss();
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				CommonUtils.showToast(context, "付款失败," + arg1 + "“！");
				super.onFailure(arg0, arg1);
				pd.dismiss();
			}
		});
	}

}
