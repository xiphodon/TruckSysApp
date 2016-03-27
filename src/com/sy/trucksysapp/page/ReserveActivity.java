package com.sy.trucksysapp.page;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 预约界面
 * 
 * @author lxs 20150909
 * 
 */
public class ReserveActivity extends BaseActivity {

	private Button btn_product_buy;
	private Context context;
	private String userid, mobile;
	private ImageView img_reserve;
	private DisplayImageOptions options;
	private int total;
	private String imgUrl;
	private TextView tv_reserve;
	private Dialog progDialog = null;// 进度条

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reserve);
		initView();
	}

	/**
	 * 初始化数据
	 */
	private void initView() {
		context = ReserveActivity.this;
		userid = PreferenceUtils.getInstance(context).getSettingUserId();
		mobile = PreferenceUtils.getInstance(context).getSettingMobile();
		tv_reserve = (TextView) findViewById(R.id.tv_reserve);
		btn_product_buy = (Button) findViewById(R.id.btn_product_buy);
		btn_product_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonUtils.showToast(context, "点击预约按钮");
				ReserveNext();
			}
		});
		img_reserve = (ImageView) findViewById(R.id.img_reserve);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		GetImg();
	}

	/**
	 * 获取预约的图片信息
	 */
	private void GetImg() {
		// 从服务器 获取商品的详情信息
		showProgressDialog();
		String url = SystemApplication.getBaseurl() + "TruckService/GetBookImg";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
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
					total = json.getInt("total");
					JSONArray array = json.getJSONArray("rows");
					JSONObject data = array.getJSONObject(0);
					imgUrl = data.getString("Pic1");
					ImageLoader.getInstance().displayImage(
							SystemApplication.getInstance().getImgUrl()
									+ imgUrl, img_reserve, options);
					tv_reserve.setText("已经预约人数：" + total + "人");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
	}

	/**
	 * 提交预约
	 */
	private void ReserveNext() {
		// 从服务器 获取商品的详情信息
		showProgressDialog();
		String url = SystemApplication.getBaseurl() + "TruckService/AddBooking";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("MemberId", userid);
		RequestParams.put("MemberMobile", mobile);
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
						CommonUtils.showToast(context, "预约成功！");
						ReserveActivity.this.finish();
					} else {
						CommonUtils.showToast(context, "预约失败！");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
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
}
