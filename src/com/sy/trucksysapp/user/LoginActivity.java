package com.sy.trucksysapp.user;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.UserInfo;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
/**
 * 登录页面最新
 * @author Administrator
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	private Context context;
	private EditText usernameEditText, passwordEditText;
	private InputMethodManager manager;
	private boolean progressShow;
	private TextView tv_doregister, tv_forget_password;
	private Button btn_login;
	private ImageView topbase_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_login);
		initView();
	}

	private void initView() {
		context = LoginActivity.this;
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		tv_doregister = (TextView) findViewById(R.id.tv_doregister);
		tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
		tv_doregister.setOnClickListener(this);
		tv_forget_password.setOnClickListener(this);
		btn_login = (Button)findViewById(R.id.activity_user_login_inptut_submit);
		btn_login.setOnClickListener(this);
		topbase_back = (ImageView)findViewById(R.id.topbase_back);
		topbase_back.setOnClickListener(this);
		usernameEditText = (EditText) findViewById(R.id.activity_user_login_inptut_username);
		passwordEditText = (EditText) findViewById(R.id.activity_user_login_inptut_password);
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			// 注册成功返回
			usernameEditText.setText(PreferenceUtils.getInstance(this)
					.getSettingUserName());
		}
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

	public void login() {
		hideKeyboard();
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		if(!CommonUtils.isPhone(username)){
			Toast.makeText(this, "请输入正确的手机号！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "账号密码不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, "网络连接不可用！", Toast.LENGTH_SHORT).show();
			return;
		}
		dologin(username, password);
	}

	/****
	 * 登录方法
	 * 
	 * @param username
	 * @param password
	 */
	private void dologin(final String username, final String password) {
		progressShow = true;
		final Dialog pd = CommonUtils
				.ShowProcess(LoginActivity.this, "正在登陆...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/MemberLogin";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("mobile", username);
		RequestParams.put("accountPwd", password);
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
					if (!jsobj.getBoolean("State")) {
						pd.dismiss();
						Toast.makeText(LoginActivity.this, jsobj.getString("Msg"), 1000)
						.show();
					} else {
						JSONObject obj = jsobj.getJSONObject("Obj");
						UserInfo muser = new UserInfo();
						muser.setId(obj.getString("Memb_Id"));
						muser.setBirthday(obj.getString("Memb_Birthday"));
						muser.setDriverLicense(obj
								.getString("Memb_DriverLicense"));
						muser.setEmail(obj.getString("Memb_Email"));
						muser.setHeadpic(obj.getString("Memb_HeadPic"));
						muser.setMobile(obj.getString("Memb_Mobile"));
						muser.setNick(obj.getString("Memb_Nick"));
						muser.setPassword(password);
						muser.setTruename(obj.getString("Memb_TrueName"));
						muser.setUsername(username);
						muser.setAddress(obj.getString("Memb_Address"));
						muser.setSex(obj.getString("Memb_Sex"));
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingHeadImg(muser.getHeadpic());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingUserId(muser.getId());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingUserName(username);
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingUserNick(muser.getNick());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingUserPassword(password);
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingTrueName(muser.getTruename());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingSex(muser.getSex());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingMobile(muser.getMobile());
						PreferenceUtils.getInstance(LoginActivity.this)
								.setSettingArea(muser.getAddress());
						Toast.makeText(LoginActivity.this, "登陆成功！", 1000)
								.show();
						setResult(888);
						finish();
					}

				} catch (Exception e) {
					e.printStackTrace();
					pd.dismiss();
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				pd.dismiss();
				super.onFailure(arg0, arg1);
				Toast.makeText(LoginActivity.this, "连接服务器失败，请联系系统管理员！", 1000)
				.show();

			}
		});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tv_doregister:
			startActivityForResult(new Intent(context, UserRegisterActivity.class),100);
			break;
		case R.id.tv_forget_password:
			startActivityForResult(new Intent(context, ResetPassActivity.class),100);
			break;
		case R.id.activity_user_login_inptut_submit:
			login();
			break;
		case R.id.topbase_back:
			finish();
			break;
		default:
			break;
		}
	}
}
