package com.sy.trucksysapp.user;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.AgreementPage;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 最新的用户注册页面
 * 
 * @author lxs 20150525
 * 
 */
public class UserRegisterActivity extends BaseActivity implements
		OnClickListener {

	private ImageView topbase_back;
	private Context context;
	private CheckBox check_agreement;
	private TextView tv_agreement;
	private Button btn_user_getCode, btn_user_register, btn_user_code;
	private TextView tv_process1, tv_process2, tv_process3;
	private LinearLayout ll_input_mobile, ll_input_code, ll_setpasswprd;
	private EditText edt_mobile, edt_mobile_code, edt_password,
			edt_password_again, edt_setmobile;
	private Button btn_get_phonecode;
	private String tel;
	private String settel;
	private String code;
	private TimeCount time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);
		initView();
	}

	private void initView() {
		context = UserRegisterActivity.this;
		topbase_back = (ImageView) findViewById(R.id.topbase_back);
		topbase_back.setOnClickListener(this);
		check_agreement = (CheckBox) findViewById(R.id.check_agreement);
		btn_user_register = (Button) findViewById(R.id.btn_user_register);
		btn_user_register.setOnClickListener(this);
		btn_user_getCode = (Button) findViewById(R.id.btn_user_getCode);
		btn_user_getCode.setOnClickListener(this);
		btn_user_code = (Button) findViewById(R.id.btn_user_code);
		btn_user_code.setOnClickListener(this);
		tv_agreement = (TextView) findViewById(R.id.tv_agreement);
		tv_agreement.setOnClickListener(this);
		tv_process1 = (TextView) findViewById(R.id.tv_process1);
		tv_process2 = (TextView) findViewById(R.id.tv_process2);
		tv_process3 = (TextView) findViewById(R.id.tv_process3);
		ll_input_mobile = (LinearLayout) findViewById(R.id.ll_input_mobile);
		ll_input_code = (LinearLayout) findViewById(R.id.ll_input_code);
		ll_setpasswprd = (LinearLayout) findViewById(R.id.ll_setpasswprd);
		edt_mobile = (EditText) findViewById(R.id.edt_mobile);
		edt_setmobile = (EditText) findViewById(R.id.edt_setmobile);
		edt_mobile_code = (EditText) findViewById(R.id.edt_mobile_code);
		edt_password = (EditText) findViewById(R.id.edt_password);
		edt_password_again = (EditText) findViewById(R.id.edt_password_again);
		btn_get_phonecode = (Button) findViewById(R.id.btn_get_phonecode);
		btn_get_phonecode.setOnClickListener(this);
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
	}

	/**
	 * 获取验证码
	 */
	private void GetCode() {
		final Dialog pd = CommonUtils.ShowProcess(context, "正在获取验证码...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetMobileCode";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("type", "0");// zhuce
		RequestParams.put("tel", tel);
//		RequestParams.put("Memb_RMobile", settel);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				pd.dismiss();
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						tv_process1.setTextColor(getResources().getColor(
								R.color.register_pressed));
						tv_process2.setTextColor(getResources().getColor(
								R.color.shape_text_white));
						ll_input_mobile.setVisibility(View.GONE);
						ll_input_code.setVisibility(View.VISIBLE);
						time.start();
					} else {
						Toast.makeText(context, jsobj.getString("Msg"), 1000)
								.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					Toast.makeText(context, "获取验证码失败！", 1000).show();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				pd.dismiss();
				Toast.makeText(context, "连接服务器失败，请联系系统管理员！", 1000).show();
			}
		});

	}

	/**
	 * 提交验证码
	 */
	private void PostCode() {
		final Dialog pd = CommonUtils.ShowProcess(context, "正在提交验证码...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/SendMobileCode";
		AsyncHttpClient client = new AsyncHttpClient();
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
						tv_process2.setTextColor(getResources().getColor(
								R.color.register_pressed));
						tv_process3.setTextColor(getResources().getColor(
								R.color.shape_text_white));
						ll_input_code.setVisibility(View.GONE);
						ll_setpasswprd.setVisibility(View.VISIBLE);
					} else {
						Toast.makeText(context, jsobj.getString("Msg"), 1000)
								.show();
					}
				} catch (Exception e) {
					// TODO: handle exception
					pd.dismiss();
					Toast.makeText(context, "提交验证码失败！", 1000).show();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				pd.dismiss();
				Toast.makeText(context, "连接服务器失败，请联系系统管理员！", 1000).show();
			}
		});

	}

	/**
	 * 设置密码
	 */
	private void SetPassword(final String password) {
		final Dialog pd = CommonUtils.ShowProcess(context, "正在注册...");
		pd.show();

		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/RegisterMember";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("Memb_Pwd", password);
		RequestParams.put("Memb_Mobile", tel);
		RequestParams.put("Memb_RMobile", settel);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				pd.dismiss();
				try {
					JSONObject jsobj = new JSONObject(result);
					String msg = jsobj.getString("Msg");
					if (jsobj.getString("State").equals("false")) {
						Toast.makeText(context, "注册失败," + msg + "！", 1000)
								.show();
					} else {
						PreferenceUtils.getInstance(context)
								.setSettingUserName(tel);
						PreferenceUtils.getInstance(context)
								.setSettingUserPassword(password);
						Toast.makeText(context, "注册成功！", 1000).show();
						finish();
					}

				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					Toast.makeText(context, "注册失败！", 1000).show();
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				pd.dismiss();
				Toast.makeText(context, "连接服务器失败，请联系系统管理员！", 1000).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.topbase_back:
			this.finish();
			break;
		case R.id.btn_user_register:
			String password = edt_password.getText().toString().trim();
			String password_again = edt_password_again.getText().toString()
					.trim();
			if (password != null && password_again != null
					&& !password.equals("")) {
				if (!password_again.equals(password)) {
					Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				SetPassword(password_again);
			} else {
				Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btn_user_getCode:
			settel = edt_setmobile.getText().toString().trim();
			tel = edt_mobile.getText().toString().trim();
			if (!settel.equals("")) {
				if (settel == null || !CommonUtils.isPhone(settel)) {
					Toast.makeText(this, "请输入正确的推荐人手机号！", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			if (tel == null || !CommonUtils.isPhone(tel)) {
				Toast.makeText(this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!check_agreement.isChecked()) {
				Toast.makeText(this, "请查看并同意卡车团协议！", Toast.LENGTH_SHORT).show();
				return;
			}
			GetCode();
			break;
		case R.id.btn_user_code:
			code = edt_mobile_code.getText().toString().trim();
			if (null == code || code.equals("")) {
				Toast.makeText(this, "请输入验证码！", Toast.LENGTH_SHORT).show();
				return;
			}
			PostCode();
			break;
		case R.id.tv_agreement:
			Intent intent = new Intent(UserRegisterActivity.this,
					AgreementPage.class);
			// 启动intent
			startActivity(intent);
			break;
		case R.id.btn_get_phonecode:
			GetCode();
			break;
		default:
			break;
		}
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			btn_get_phonecode.setText("重新获取");
			btn_get_phonecode.setEnabled(true);
			btn_get_phonecode.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			btn_get_phonecode.setEnabled(false);
			btn_get_phonecode.setText(millisUntilFinished / 1000 + "秒");
		}
	}
}
