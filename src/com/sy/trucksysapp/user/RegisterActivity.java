package com.sy.trucksysapp.user;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.MactchUtil;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class RegisterActivity extends BaseActivity {
	private TextView topbase_center_text;
	private EditText activity_user_username, activity_user_password,
			activity_user_again_password, activity_user_phone;
	private CheckBox check_agreement;
	private InputMethodManager manager;
	private boolean progressShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_register);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText(getResources().getString(
				R.string.activity_user_register_top_title));
		activity_user_username = (EditText) findViewById(R.id.activity_user_username);
		activity_user_password = (EditText) findViewById(R.id.activity_user_password);
		activity_user_again_password = (EditText) findViewById(R.id.activity_user_again_password);
		activity_user_phone = (EditText) findViewById(R.id.activity_user_phone);
		check_agreement = (CheckBox) findViewById(R.id.check_agreement);
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void register(View view) {
		hideKeyboard();
		final String username = activity_user_username.getText().toString();
		final String password = activity_user_password.getText().toString();
		String apassword = activity_user_again_password.getText().toString();
		String userphone = activity_user_phone.getText().toString();
		Boolean b = check_agreement.isChecked();
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "账号密码不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!CommonUtils.LegalPsw(password)){
			Toast.makeText(this, "密码格式不合法！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!apassword.equals(password)) {
			Toast.makeText(this, "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(userphone)) {
			Toast.makeText(this, "电话号码不能为空！", Toast.LENGTH_SHORT).show();
			activity_user_phone.requestFocus();
			return;
		}
		if (!MactchUtil.CheckPhoneNumber(userphone)) {
			Toast.makeText(this, "请输入正确的电话号码！", Toast.LENGTH_SHORT).show();
			activity_user_phone.requestFocus();
			return;
		}
		if (!b) {
			Toast.makeText(this, "未同意相关协议！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, "网络连接不可用！", Toast.LENGTH_SHORT).show();
			return;
		}
		progressShow = true;
		final Dialog pd = CommonUtils.ShowProcess(RegisterActivity.this,
				"正在注册...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/RegisterMember";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("Memb_Account", username);
		RequestParams.put("Memb_Pwd", password);
		RequestParams.put("Memb_Mobile", password);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				if (!progressShow) {
					pd.dismiss();
					return;
				}
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getString("State").equals("0")) {
						pd.dismiss();
						forError(jsobj.getString("Msg"));
					} else {

						PreferenceUtils.getInstance(RegisterActivity.this)
								.setSettingUserName(username);
						PreferenceUtils.getInstance(RegisterActivity.this)
								.setSettingUserPassword(password);
						Toast.makeText(RegisterActivity.this, "注册成功！", 1000)
								.show();
						setResult(200);
						finish();
					}

				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
					forError(e.getMessage() + "注册失败！");
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				pd.dismiss();
				forError(arg1 + "注册失败！");
				super.onFailure(arg0, arg1);

			}
		});
	}

	public void forError(final String a) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, a, 1000).show();
			}
		});
	}
}
