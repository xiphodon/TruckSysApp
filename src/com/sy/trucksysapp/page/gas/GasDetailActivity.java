package com.sy.trucksysapp.page.gas;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.FollowService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.ShowMapwayActivity;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 加油加气的详细信息
 * 
 * @author lxs 20150617
 * 
 */
public class GasDetailActivity extends BaseActivity implements OnClickListener {
	private TextView tv_name, tv_LinkName, tv_address, tv_phone;
	private String phone;
	private HashMap<String, String> rowdata;
	private DisplayImageOptions options;
	private ImageView img_shop_head, follow;
	private Context context;
	private RelativeLayout rb_address;

	private boolean editable;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gas_detail);
		rowdata = (HashMap<String, String>) getIntent().getSerializableExtra(
				"rowdata");
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		if (rowdata != null) {
			initView();
		} else {
			this.finish();
			CommonUtils.showToast(this, "加载数据失败");
		}
	}

	private void initView() {
		follow = (ImageView) findViewById(R.id.follow);
		if (!CommonUtils.getString(rowdata, "Favo_CreateDate").equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		follow.setOnClickListener(this);
		context = GasDetailActivity.this;
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_LinkName = (TextView) findViewById(R.id.tv_LinkName);
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		rb_address = (RelativeLayout) findViewById(R.id.rb_address);
		rb_address.setOnClickListener(this);
		tv_phone.setOnClickListener(this);
		img_shop_head = (ImageView) findViewById(R.id.img_shop_head);
		String prefix = "GaSt_";
		tv_name.setText(rowdata.get(prefix + "ShopName"));
		tv_LinkName.setText("联系人：" + rowdata.get(prefix + "LinkName"));
		tv_address.setText("地址：" + rowdata.get(prefix + "Address"));
		tv_phone.setText(rowdata.get(prefix + "Mobile"));
		String pic = rowdata.get(prefix + "Pic") + "";
		if (!pic.equals("") || pic.equals("null")) {
			final String url = pic;
			if (pic.indexOf("|") != -1)
				pic = pic.substring(0, pic.indexOf("|"));
			ImageLoader.getInstance().displayImage(
					SystemApplication.getServiceUrl() + pic, img_shop_head,
					options);
			img_shop_head.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					String[] urls;
					if (url.indexOf("|") != -1)
						urls = url.split("\\|");
					else
						urls = new String[] { url };
					String sUrl = SystemApplication.getServiceUrl();
					int index = 0;
					for (String u : urls) {
						urls[index] = u = sUrl + u;
						index++;
					}
					Intent intent = new Intent(context,
							ImagePagerActivity.class);
					// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
					intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			});
		}
	}

	public void finish() {
		if (FollowService.isFollowed()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("rowdata", rowdata);
			intent.putExtras(bundle);
			setResult(100, intent);
		}
		super.finish();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.rb_address:
			Intent intentmap = new Intent(context, ShowMapwayActivity.class);
			rowdata.put("Serv_Longitude", rowdata.get("GaSt_Longitude"));
			rowdata.put("Serv_Latitude", rowdata.get("GaSt_Latitude"));
			intentmap.putExtra("rowdata", rowdata);
			startActivity(intentmap);
			break;
		case R.id.tv_phone:
			phone = tv_phone.getText().toString();
			// 用intent启动拨打电话
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.follow:// 收藏
			FollowService.follow(context,rowdata,"GaSt_Id","GasStation",follow);
			break;
		default:
			break;
		}
	}
}
