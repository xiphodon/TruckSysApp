package com.sy.trucksysapp.user;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.user.UserRegisterActivity.TimeCount;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class ResetPassActivity extends BaseActivity implements OnClickListener {
	private TextView tv_process1, tv_process2;
	private EditText edt_mobile, edt_receivecode, ed_pass1, ed_pass2;
	private LinearLayout li_resetpass, li_invatecode;
	private Button btn_getcode, btn_inviateCode, btn_resetpass;
	private Context context;
	ProgressDialog pd;
	private TimeCount time;
	AsyncHttpClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
		context = ResetPassActivity.this;
		initview();
	}

	private void initview() {
		// TODO Auto-generated method stub
		tv_process1 = (TextView) findViewById(R.id.tv_process1);
		tv_process2 = (TextView) findViewById(R.id.tv_process2);
		edt_mobile = (EditText) findViewById(R.id.edt_mobile);
		edt_receivecode = (EditText) findViewById(R.id.edt_receivecode);
		ed_pass1 = (EditText) findViewById(R.id.ed_pass1);
		ed_pass2 = (EditText) findViewById(R.id.ed_pass2);
		li_resetpass = (LinearLayout) findViewById(R.id.li_resetpass);
		li_invatecode = (LinearLayout) findViewById(R.id.li_invatecode);
		btn_getcode = (Button) findViewById(R.id.btn_getcode);
		btn_getcode.setOnClickListener(this);
		btn_inviateCode = (Button) findViewById(R.id.btn_inviateCode);
		btn_inviateCode.setOnClickListener(this);
		btn_resetpass = (Button) findViewById(R.id.btn_resetpass);
		btn_resetpass.setOnClickListener(this);
		if (pd == null) {
			pd = new ProgressDialog(context);
			pd.setMessage("正在发送请求...");
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					pd.dismiss();
				}
			});
		}
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		client = new AsyncHttpClient();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		String tel = edt_mobile.getText().toString().trim();
		String code = edt_receivecode.getText().toString().trim();
		switch (arg0.getId()) {
		case R.id.btn_getcode:
			// 点击获取验证码
			if (tel == null || !CommonUtils.isPhone(tel)) {
				Toast.makeText(this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
				return;
			} else {
				GetCode();
			}
			break;

		case R.id.btn_inviateCode:
			if (tel == null || !CommonUtils.isPhone(tel)) {
				Toast.makeText(this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
				return;
			} else if (code == null || code.equals("")) {
				Toast.makeText(this, "请输入您收到的验证码！", Toast.LENGTH_SHORT).show();
				return;
			}
			invateCode();
			break;
		case R.id.btn_resetpass:
			resetpassword();
			break;

		default:
			break;
		}
	}

	private void resetpassword() {
		// TODO Auto-generated method stub
		final String pass1 = ed_pass1.getText().toString().trim();
		String pass2 = ed_pass2.getText().toString().trim();
		if(pass1.equals("")){
			Toast.makeText(context, "密码不能为空", 1000).show();
			return;
		}
		if (pass1.equals("") || pass2.equals("") || !pass1.equals(pass2)) {
			Toast.makeText(context, "两次输入密码不一致！", 1000).show();
			return;
		}
		if (!pass1.equals("") && pass1.equals(pass2)) {
			final String tel = edt_mobile.getText().toString().trim();
			pd.setMessage("正在提交新密码...");
			pd.show();
			String url = SystemApplication.getInstance().getBaseurl()
					+ "TruckService/ResetPass";
			RequestParams RequestParams = new RequestParams();
			RequestParams.put("tel", tel);
			RequestParams.put("pass", pass1);
			client.post(url, RequestParams, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					super.onSuccess(result);
					pd.dismiss();
					try {
						JSONObject jsobj = new JSONObject(result);
						if (jsobj.getBoolean("State")) {
							Toast.makeText(context, "密码修改成功！", 1000).show();
							PreferenceUtils.getInstance(context)
									.setSettingUserName(tel);
							PreferenceUtils.getInstance(context)
									.setSettingUserPassword(pass1);
							finish();
						} else {
							Toast.makeText(context,
									jsobj.getString("Msg") + "密码修改失败！", 1000)
									.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(context, e.getMessage() + "密码修改失败！",
								1000).show();
					}

				}

				@Override
				public void onFailure(Throwable arg0, String result) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, result);
					pd.dismiss();
					Toast.makeText(context, result + "密码修改失败！", 1000).show();
				}
			});
		}
	}

	private void invateCode() {
		// TODO Auto-generated method stub
		String tel = edt_mobile.getText().toString().trim();
		String code = edt_receivecode.getText().toString().trim();
		pd.setMessage("正在提交验证...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/SendMobileCode";
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("tel", tel);
		RequestParams.put("code", code);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				pd.dismiss();
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						runOnUiThread(new Runnable() {
							public void run() {
								tv_process1.setTextColor(getResources()
										.getColor(R.color.text_color_gray_c1));
								tv_process2.setTextColor(getResources()
										.getColor(R.color.shape_text_white));
								li_invatecode.setVisibility(View.GONE);
								li_resetpass.setVisibility(View.VISIBLE);
							}
						});
					} else {
						Toast.makeText(context,
								jsobj.getString("Msg") + ",验证失败！", 1000).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					Toast.makeText(context, e.getMessage() + ",验证失败！", 1000)
							.show();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				pd.dismiss();
				Toast.makeText(context, result + "验证失败！", 1000).show();
			}
		});
	}

	/**
	 * 获取验证码
	 */
	private void GetCode() {
		pd.setMessage("正在发送请求...");
		pd.show();
		edt_mobile.setEnabled(false);
		btn_getcode.setEnabled(false);
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetMobileCode";
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("type", "1");// 修改获取验证码
		RequestParams.put("tel", edt_mobile.getText() + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				pd.dismiss();
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						time.start();
					} else {
						edt_mobile.setEnabled(true);
						btn_getcode.setEnabled(true);
						Toast.makeText(context, jsobj.getString("Msg"), 1000)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					edt_mobile.setEnabled(true);
					btn_getcode.setEnabled(true);
					Toast.makeText(context, "获取验证码失败！", 1000).show();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				pd.dismiss();
				edt_mobile.setEnabled(true);
				btn_getcode.setEnabled(true);
				Toast.makeText(context, "获取验证码失败！", 1000).show();
			}
		});

	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			edt_mobile.setEnabled(true);
			btn_getcode.setEnabled(true);
			btn_getcode.setTextSize(14);
			btn_getcode.setText("获取验证码");
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			edt_mobile.setEnabled(false);
			btn_getcode.setEnabled(false);
			btn_getcode.setTextSize(12);
			btn_getcode.setText("验证码已发送\n(" + millisUntilFinished / 1000
					+ "秒)后重新获取");
		}
	}
}
