package com.sy.trucksysapp.user;

import java.text.DecimalFormat;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.AppCodeActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.user.collection.CollectionActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 个人信息页面
 * 
 * @author Administrator
 * 
 */
public class UserCenterFragment extends Fragment implements OnClickListener {
	LayoutInflater inflater;
	Context context;
	private RelativeLayout lin_un_login, lin_login, rl_sys_setting, rel_orders,
			rl_goods_bag, rel_collection, rl_authentication, rl_account,
			rl_record,rel_code;
	private TextView tv_dologin;
	private String pass;
	private ImageView user_head_img;
	private DisplayImageOptions options;
	private String HeadpicUrl;
	private Button btn_exit;
	private TextView user_nick_text;
	private TextView user_account_leave,user_account_integral;
	private Class cls;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.user_center, container, false);
		return view;
	}

	public void onResume() {
		initView();
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		initView();
	}

	private void initView() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_head_man)
				.showImageForEmptyUri(R.drawable.icon_head_man)
				.showImageOnFail(R.drawable.icon_head_man).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		lin_un_login = (RelativeLayout) getView().findViewById(
				R.id.lin_un_login);
		user_account_leave = (TextView) getView().findViewById(
				R.id.user_account_leave);
		user_account_integral= (TextView) getView().findViewById(
				R.id.user_account_integral);
		lin_login = (RelativeLayout) getView().findViewById(R.id.lin_login);
		rel_orders = (RelativeLayout) getView().findViewById(R.id.rel_orders);
		tv_dologin = (TextView) getView().findViewById(R.id.tv_dologin);
		rl_sys_setting = (RelativeLayout) getView().findViewById(
				R.id.rl_sys_setting);
		rel_collection = (RelativeLayout) getView().findViewById(
				R.id.rel_collection);
		rl_authentication = (RelativeLayout) getView().findViewById(
				R.id.rl_authentication);
		rl_account = (RelativeLayout) getView().findViewById(R.id.rl_account);
		rel_code= (RelativeLayout) getView().findViewById(R.id.rel_code);
		rel_code.setOnClickListener(this);
		rl_record = (RelativeLayout) getView().findViewById(R.id.rl_record);
		rl_record.setOnClickListener(this);
		rl_authentication.setOnClickListener(this);
		rel_collection.setOnClickListener(this);
		rel_orders.setOnClickListener(this);
		rl_goods_bag = (RelativeLayout) getView().findViewById(
				R.id.rl_goods_bag);
		tv_dologin.setOnClickListener(this);
		lin_login.setOnClickListener(this);
		rl_sys_setting.setOnClickListener(this);
		rl_goods_bag.setOnClickListener(this);
		btn_exit = (Button) getView().findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
		rl_account.setOnClickListener(this);
		user_head_img = (ImageView) getView().findViewById(R.id.user_head_img);
		pass = PreferenceUtils.getInstance(getActivity())
				.getSettingUserPassword();
		user_nick_text = (TextView) getView().findViewById(R.id.user_nick_text);
		if (pass != null && !pass.equals("")) {
			HeadpicUrl = SystemApplication.getBaseurl();
			HeadpicUrl = HeadpicUrl.substring(0, HeadpicUrl.length() - 1)
					+ PreferenceUtils.getInstance(getActivity())
							.getSettingHeadImg();
			getaccountMo();
			getPointsMo();
			ImageLoader.getInstance().displayImage(HeadpicUrl, user_head_img,
					options);
			if(PreferenceUtils.getInstance(getActivity())
					.getSettingUserNick().equals("")){
				user_nick_text.setText("未设置昵称");
			}else{
				user_nick_text.setText(PreferenceUtils.getInstance(getActivity())
						.getSettingUserNick());
			}
			if (!PreferenceUtils.getInstance(getActivity()).getSettingHeadImg()
					.equals("null")
					&& !PreferenceUtils.getInstance(getActivity())
							.getSettingHeadImg().equals("")) {
				HeadpicUrl = SystemApplication.getBaseurl();
				HeadpicUrl = HeadpicUrl.substring(0, HeadpicUrl.length() - 1)
						+ PreferenceUtils.getInstance(getActivity())
								.getSettingHeadImg();
				ImageLoader.getInstance().displayImage(HeadpicUrl,
						user_head_img, options);

			}
//			user_nick_text.setText(PreferenceUtils.getInstance(getActivity())
//					.getSettingUserNick());
			lin_un_login.setVisibility(View.INVISIBLE);
			lin_login.setVisibility(View.VISIBLE);
			btn_exit.setVisibility(View.VISIBLE);
		} else {
			lin_un_login.setVisibility(View.VISIBLE);
			lin_login.setVisibility(View.INVISIBLE);
			btn_exit.setVisibility(View.GONE);
		}
	}

	public void getPointsMo() {
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetMemberIntegral";
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
						Integer in = jsobj.getInt("Obj");
						user_account_integral.setText("积分余额："+in);
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
						user_account_leave.setText(df.format(d) + "元");
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

	public void showShopbag(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.tv_dologin:
			startActivityForResult(new Intent(getActivity(),
					LoginActivity.class), 888);
			cls = null;
			break;
		case R.id.lin_login:
			startActivityForResult(new Intent(getActivity(),
					PersonInfoActivity.class), 888);
			break;
		case R.id.rel_code:
			startActivityForResult(new Intent(getActivity(),
					AppCodeActivity.class), 999);
			break;
		case R.id.rl_sys_setting:
			startActivityForResult(new Intent(getActivity(),
					AppsetActivity.class), 666);
			break;
		case R.id.rl_goods_bag:// 购物车
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
				cls = ShopCartActivity.class;
				return;
			}
			startActivityForResult(new Intent(getActivity(),
					ShopCartActivity.class), 555);
			break;
		case R.id.btn_exit:
			PreferenceUtils.getInstance(getActivity()).ClearAll();
			// ListActivity.refresh();
			// SearchActivity.refresh();
			lin_un_login.setVisibility(View.VISIBLE);
			lin_login.setVisibility(View.INVISIBLE);
			btn_exit.setVisibility(View.GONE);
			break;
		case R.id.rel_orders:
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
				cls = ShoplistActivity.class;
				return;
			}
			startActivityForResult(new Intent(getActivity(),
					ShoplistActivity.class), 222);
			break;
		case R.id.rel_collection:
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals(""))
				CommonUtils.showToast(context, "请先登录再使用收藏功能");
			else
				startActivityForResult(new Intent(getActivity(),
						CollectionActivity.class), 222);
			break;
		case R.id.rl_authentication:
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
				cls = DriverAuthActivity.class;
				return;
			} else {
				startActivityForResult(new Intent(getActivity(),
						DriverAuthActivity.class), 777);
			}
			break;
		case R.id.rl_account:
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
				cls = RechargeActivity.class;
				return;
			} else {
				startActivityForResult(new Intent(getActivity(),
						RechargeActivity.class), 999);
			}
			break;
		case R.id.rl_record:
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
				cls = UserAccountmainRecordActivity.class;
				return;
			} else {
				startActivityForResult(new Intent(getActivity(),
						UserAccountmainRecordActivity.class), 9999);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 888:
			// ListActivity.refresh();
			// SearchActivity.refresh();
			lin_un_login.setVisibility(View.INVISIBLE);
			lin_login.setVisibility(View.VISIBLE);
			HeadpicUrl = SystemApplication.getBaseurl();
			HeadpicUrl = HeadpicUrl.substring(0, HeadpicUrl.length() - 1)
					+ PreferenceUtils.getInstance(getActivity())
							.getSettingHeadImg();
			ImageLoader.getInstance().displayImage(HeadpicUrl, user_head_img,
					options);
			if(PreferenceUtils.getInstance(getActivity())
					.getSettingUserNick().equals("")){
				user_nick_text.setText("未设置昵称");
			}else{
				user_nick_text.setText(PreferenceUtils.getInstance(getActivity())
						.getSettingUserNick());
			}
			if(cls!=null){
				startActivityForResult(new Intent(getActivity(),
						cls), 000);
				cls = null;
			}
			btn_exit.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

}
