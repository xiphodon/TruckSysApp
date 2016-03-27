package com.sy.trucksysapp.page.driver;

import java.text.DecimalFormat;
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
import android.widget.RatingBar;
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
 * 餐馆详情
 * 
 * @author lxs 20150604
 * 
 */
public class RestaurantDetailActivity extends BaseActivity implements
		OnClickListener {

	private ImageView iv_title, img_shop_tel, follow;
	private TextView tv_iccount, tv_hotel_name, tv_parsevalue, tv_shop_address,
			tv_servicecontent, topbase_center_text;
	private RatingBar rb_value;
	private HashMap<String, String> map;
	private DisplayImageOptions options;
	private ImageView comment;
	private Context context;

	private TextView tv_prase_lable, tv_prase_more;
	private LinearLayout li_parse_content;
	private ArrayList<CommentModel> datalist = new ArrayList<CommentModel>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private UserEvaluationAdapter evaluationAdapter;
	private ScrollListView lv_evaluation;
	private ScrollView scroll;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_hotel_detail);
		context = RestaurantDetailActivity.this;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		map = (HashMap<String, String>) getIntent().getSerializableExtra(
				"rowdata");
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		// 标题图片
		follow = (ImageView) findViewById(R.id.follow);
		if (!CommonUtils.getString(map, "Favo_CreateDate").equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		follow.setOnClickListener(this);
		iv_title = (ImageView) findViewById(R.id.iv_title);
		iv_title.setOnClickListener(this);
		comment = (ImageView) findViewById(R.id.comment);
		comment.setOnClickListener(this);
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("餐馆详情");
		tv_iccount = (TextView) findViewById(R.id.tv_iccount);// 图片数
		tv_hotel_name = (TextView) findViewById(R.id.tv_hotel_name);// 餐馆名称
		tv_parsevalue = (TextView) findViewById(R.id.tv_parsevalue);// 餐馆星级
		rb_value = (RatingBar) findViewById(R.id.rb_value);// 餐馆星级
		rb_value.setStepSize(0.1f);
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		rb_value.setRating(r);
		tv_parsevalue.setText(p);

		tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);// 餐馆地址
		tv_shop_address.setOnClickListener(this);
		img_shop_tel = (ImageView) findViewById(R.id.img_shop_tel);// 电话
		img_shop_tel.setOnClickListener(this);
		tv_servicecontent = (TextView) findViewById(R.id.tv_servicecontent);// 提供服务内容

		li_parse_content = (LinearLayout) findViewById(R.id.li_parse_content);// 评论内容
		lv_evaluation = (ScrollListView) findViewById(R.id.lv_evaluation);
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		scroll = (ScrollView) findViewById(R.id.scroll);
		tv_prase_lable = (TextView) findViewById(R.id.tv_prase_lable);// 评论
		tv_prase_more = (TextView) findViewById(R.id.tv_prase_more);// 更多评论
		tv_prase_more.setOnClickListener(this);

		if (map != null) {
			try {
				String[] picarray = map.get("Rest_Pic").split("\\|");
				if (picarray.length != 0) {
					ImageLoader.getInstance().displayImage(
							SystemApplication.getServiceUrl() + picarray[0],
							iv_title, options);
				}
				tv_iccount.setText(picarray.length + "张");
				tv_hotel_name.setText(map.get("Rest_ShopName"));
				tv_shop_address.setText(map.get("Rest_Address"));
				tv_servicecontent.setText(map.get("Rest_Desc"));
				getEvaluationData(0, "up");
			} catch (Exception ex) {

			}
		}

	}

	/**
	 * 获取评价列表数据
	 * 
	 * @param type
	 *            0:初始化 1:点击加载
	 */
	private void getEvaluationData(int initType, String type) {
		CommentService.getData(type, context, evaluationAdapter, datalist,
				listKey, map.get("Rest_Id"), "7", initType, tv_prase_more,
				tv_prase_lable, li_parse_content, scroll,rb_value,tv_parsevalue);
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
		map.put("StartCount", CommentService.getAvgStar());
		setResult(100, new Intent().putExtra("rowdata", map));
		super.finish();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_title:
			// 显示更多图片
			Intent imageintent = new Intent(RestaurantDetailActivity.this,
					ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			String[] picarray = map.get("Rest_Pic").split("\\|");
			for (int i = 0; i < picarray.length; i++) {
				picarray[i] = SystemApplication.getServiceUrl() + picarray[i];
			}
			imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, picarray);
			imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			startActivity(imageintent);

			break;
		case R.id.tv_shop_address:
			// 进入位置导航
			HashMap<String, String> loactionmap = new HashMap<String, String>();
			loactionmap.put("Serv_Latitude", map.get("Rest_Latitude"));
			loactionmap.put("Serv_Longitude", map.get("Rest_Longitude"));
			Intent intentmap = new Intent(RestaurantDetailActivity.this,
					ShowMapwayActivity.class);
			intentmap.putExtra("rowdata", loactionmap);
			startActivity(intentmap);
			break;
		case R.id.img_shop_tel:
			// 进入电话页面
			Intent telintent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ map.get("Rest_Mobile")));
			telintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(telintent);
			break;
		case R.id.tv_prase_more:
			// 进入评论页面
			getEvaluationData(1, "up");
			break;
		case R.id.follow:// 收藏
			FollowService.follow(context, map, "Rest_Id", "Restaurant", follow);
			break;
		case R.id.comment:// 评论类型7
			startActivityForResult(new Intent(context, CommentActivity.class)
					.putExtra("id", map.get("Rest_Id")).putExtra("type", "7"),
					100);
			break;
		default:
			break;
		}

	}

}
