package com.sy.trucksysapp.page.service;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.CommentService;
import com.sy.trucksysapp.page.FollowService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.ShowMapwayActivity;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.user.CommentActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.ScrollListView;

/**
 * 维修救援详情页面
 * 
 * @author lxs 20150516
 * 
 */
public class ServicesDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView tv_name, tv_LinkName, tv_email, tv_fax, tv_qq, tv_address,
			tv_phone;
	private RelativeLayout rb_address;
	private String phone;
	private HashMap<String, String> rowdata;
	private DisplayImageOptions options;
	private ImageView img_shop_head, follow, comment;
	private Context context;
	private TextView tv_prase_lable, tv_prase_more;
	private LinearLayout li_parse_content;
	private ArrayList<CommentModel> datalist = new ArrayList<CommentModel>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private UserEvaluationAdapter evaluationAdapter;
	private ScrollListView lv_evaluation;
	private ScrollView scroll;
	private String type;

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mservices_detail);
		context = ServicesDetailActivity.this;
		// 隐藏评价
		((LinearLayout) findViewById(R.id.otherLayout))
				.setVisibility(View.GONE);
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
		}
	}

	private void initView() {
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		follow = (ImageView) findViewById(R.id.follow);
		if (!CommonUtils.getString(rowdata, "Favo_CreateDate").equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		follow.setOnClickListener(this);
		comment = (ImageView) findViewById(R.id.comment);
		comment.setOnClickListener(this);

		li_parse_content = (LinearLayout) findViewById(R.id.li_parse_content);// 评论内容
		lv_evaluation = (ScrollListView) findViewById(R.id.lv_evaluation);
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		scroll = (ScrollView) findViewById(R.id.scroll);
		tv_prase_lable = (TextView) findViewById(R.id.tv_prase_lable);// 评论
		tv_prase_more = (TextView) findViewById(R.id.tv_prase_more);// 更多评论
		tv_prase_more.setOnClickListener(this);

		String sType = rowdata.get("ServiceType");
		if (sType.equals("1")) {
			type = "FsShop";
			topbase_center_text.setText("4S店详细");
		} else if (sType.equals("2")) {
			type = "Garage";
			topbase_center_text.setText("维修站详细");
		} else if (sType.equals("3")) {
			type = "ReCompany";
			topbase_center_text.setText("救援公司详细");
		}
		String prefix = "FsSh_";
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(rowdata.get(prefix + "ShopName"));
		tv_LinkName = (TextView) findViewById(R.id.tv_LinkName);
		tv_LinkName.setText("联系人：" + rowdata.get(prefix + "LinkName"));
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_email.setText("Email："
				+ CommonUtils.getString(rowdata, "Serv_Email"));
		tv_fax = (TextView) findViewById(R.id.tv_fax);
		tv_fax.setText("传真：" + CommonUtils.getString(rowdata, "Serv_Fax"));
		tv_qq = (TextView) findViewById(R.id.tv_qq);
		tv_qq.setText("QQ：" + rowdata.get("Serv_QQ"));
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_address.setText("地址：" + rowdata.get(prefix + "Address"));
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_phone.setText(rowdata.get(prefix + "Mobile"));
		rb_address = (RelativeLayout) findViewById(R.id.rb_address);
		rb_address.setOnClickListener(this);
		tv_phone.setOnClickListener(this);
		img_shop_head = (ImageView) findViewById(R.id.img_shop_head);
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
		getEvaluationData(0, "down");
	}

	/**
	 * 获取评价列表数据
	 * 
	 * @param type
	 *            0:初始化 1:点击加载
	 */
	private void getEvaluationData(int initType, String type) {
		CommentService.getData(type, context, evaluationAdapter, datalist,
				listKey, rowdata.get("FsSh_Id"), this.type, initType,
				tv_prase_more, tv_prase_lable, li_parse_content, scroll,rowdata);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			if (data.getBooleanExtra("state", false)) {
				CommonUtils.showToast(context, "评价成功");
				getEvaluationData(1, "down");
			}
		}
	}

	public void finish() {
		rowdata.put("Star", CommentService.getAvgStar());
		setResult(100, new Intent().putExtra("rowdata", rowdata));
		super.finish();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_phone:
			phone = tv_phone.getText().toString();
			// 用intent启动拨打电话
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.rb_address:
			Intent intentmap = new Intent(context, ShowMapwayActivity.class);
			rowdata.put("Serv_Longitude", rowdata.get("FsSh_Longitude"));
			rowdata.put("Serv_Latitude", rowdata.get("FsSh_Latitude"));
			intentmap.putExtra("rowdata", rowdata);
			startActivity(intentmap);
			break;
		case R.id.follow:// 收藏
			FollowService.follow(context, rowdata, "FsSh_Id", type, follow);
			break;
		case R.id.tv_prase_more: // 进入评论页面
			getEvaluationData(1, "up");
			break;
		case R.id.comment:// 评论
			startActivityForResult(
					new Intent(context, CommentActivity.class).putExtra("id",
							rowdata.get("FsSh_Id")).putExtra("type", type), 100);
			break;
		default:
			break;
		}
	}
}
