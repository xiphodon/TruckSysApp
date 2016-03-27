package com.sy.trucksysapp.page.shoping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.CommentService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.driver.HotelDetailActivity;
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.user.CommentActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.JsWebViewClient;
import com.sy.trucksysapp.widget.ScrollListView;

public class SupplierdetailsActivity extends BaseActivity implements
		OnClickListener {
	private ImageView comment, img_top_shop, img_shop_tel;
	private TextView tv_shop_name, tv_shop_address, tv_contacts, tv_fax,
			tv_email, tv_qq;
	private WebView tv_shop_range;
	private DisplayImageOptions options;
	private String mobile;
	private HashMap<String, String> rowdata = null;
	private Context context;
	private ScrollListView lv_mshoplist;
	private HotSaleAdapter lv_madapter;
	private ArrayList<HashMap<String, String>> lv_list;

	private TextView tv_prase_lable, tv_prase_more;
	private LinearLayout li_parse_content, ll_to_here;
	private ArrayList<CommentModel> datalist = new ArrayList<CommentModel>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private UserEvaluationAdapter evaluationAdapter;
	private ScrollListView lv_evaluation;
	private ScrollView scroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_supplier_details);
		context = SupplierdetailsActivity.this;
		initviews();
		try {
			rowdata = (HashMap<String, String>) getIntent()
					.getSerializableExtra("rowdata");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (rowdata != null) {
			initdata();
			executeshopdata();
		}
	}

	/***
	 * 加载商品数据
	 */
	private void executeshopdata() {
		// TODO Auto-generated method stub
		try {
			String url = SystemApplication.getInstance().getBaseurl()
					+ "truckservice/GetRecommendGoods";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(10000);
			RequestParams RequestParams = new RequestParams();
			RequestParams.put("sellerType", rowdata.get("SellerType"));
			RequestParams.put("sellerId", rowdata.get("Deal_Id"));
			client.post(url, RequestParams, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					super.onSuccess(result);
					try {
						JSONObject json = new JSONObject(result);
						final JSONArray array = json.getJSONArray("rows");
						lv_list.clear();
						if (array.length() != 0) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject itemjson = array.getJSONObject(i);
								lv_list.add(CommonUtils.getMap(itemjson));
							}
						}
						lv_madapter.notifyDataSetChanged();
					} catch (Exception e) {
						// TODO: handle exception
						CommonUtils.showToast(context, "获取店铺推荐出错！");
					}
				}

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
					CommonUtils.showToast(context, "获取店铺推荐出错！");
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/***
	 * 加载数据
	 */
	private void initdata() {
		// TODO Auto-generated method stub
		tv_contacts.setText(TextUtils.FormatStr(rowdata.get("Deal_LinkName")));
		tv_shop_address
				.setText(TextUtils.FormatStr(rowdata.get("Deal_Address")));
		tv_shop_name.setText(TextUtils.FormatStr(rowdata.get("Deal_Name")));
		tv_fax.setText(TextUtils.FormatStr(rowdata.get("Deal_Fax")));
		tv_email.setText(TextUtils.FormatStr(rowdata.get("Deal_Email")));
		tv_qq.setText(TextUtils.FormatStr(rowdata.get("Deal_QQ")));
		WebSettings setting = tv_shop_range.getSettings();
		tv_shop_range.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
		tv_shop_range.getSettings().setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8
		tv_shop_range.getSettings().setJavaScriptEnabled(true);
		tv_shop_range.loadData(CommonUtils.formatHtmlString(TextUtils
				.FormatStr(rowdata.get("Deal_Remark"))),
				"text/html; charset=UTF-8", null);
		tv_shop_range.setWebViewClient(new JsWebViewClient(tv_shop_range,
				context));
		String url = SystemApplication.getServiceUrl()
				+ rowdata.get("Deal_HeadPic");
		ImageLoader.getInstance().displayImage(url, img_top_shop, options);
		mobile = rowdata.get("Deal_TelMobile");
		getEvaluationData(0, "up");
	}

	private void initviews() {
		// TODO Auto-generated method stub
		comment = (ImageView) findViewById(R.id.comment);
		comment.setOnClickListener(this);
		img_top_shop = (ImageView) findViewById(R.id.img_top_shop);
		img_top_shop.setOnClickListener(this);
		img_shop_tel = (ImageView) findViewById(R.id.img_shop_tel);
		img_shop_tel.setOnClickListener(this);
		tv_shop_name = (TextView) findViewById(R.id.tv_shop_name);
		tv_shop_address = (TextView) findViewById(R.id.tv_shop_address);
		tv_contacts = (TextView) findViewById(R.id.tv_contacts);
		tv_fax = (TextView) findViewById(R.id.tv_fax);
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_qq = (TextView) findViewById(R.id.tv_qq);
		tv_shop_range = (WebView) findViewById(R.id.tv_shop_range);
		lv_mshoplist = (ScrollListView) findViewById(R.id.lv_mshoplist);
		lv_list = new ArrayList<HashMap<String, String>>();
		lv_madapter = new HotSaleAdapter(context, lv_list);
		lv_mshoplist.setAdapter(lv_madapter);
		lv_mshoplist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				HashMap<String, String> row = lv_list.get(arg2);
				Intent intent = new Intent(context, SaleDetailActivity.class);
				intent.putExtra("saleid", row.get("ID"));
				intent.putExtra("Type", Integer.valueOf(row.get("PType")));
				startActivity(intent);
			}
		});
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		RelativeLayout rl_tire = (RelativeLayout) findViewById(R.id.rl_tire);
		 rl_tire.setOnClickListener(this);
		RelativeLayout rl_Inner_tube = (RelativeLayout) findViewById(R.id.rl_Inner_tube);
		 rl_Inner_tube.setOnClickListener(this);
		RelativeLayout rl_Wheel_Gu = (RelativeLayout) findViewById(R.id.rl_Wheel_Gu);
		 rl_Wheel_Gu.setOnClickListener(this);
		RelativeLayout rl_lub_oil = (RelativeLayout) findViewById(R.id.rl_lub_oil);
		 rl_lub_oil.setOnClickListener(this);
		li_parse_content = (LinearLayout) findViewById(R.id.li_parse_content);// 评论内容
		lv_evaluation = (ScrollListView) findViewById(R.id.lv_evaluation);
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		scroll = (ScrollView) findViewById(R.id.scroll);
		tv_prase_lable = (TextView) findViewById(R.id.tv_prase_lable);// 评论
		tv_prase_more = (TextView) findViewById(R.id.tv_prase_more);// 更多评论
		tv_prase_more.setOnClickListener(this);
	}

	/**
	 * 获取评价列表数据
	 * 
	 * @param type
	 *            0:初始化 1:点击加载
	 */
	private void getEvaluationData(int initType, String type) {
		CommentService.getData(type, context, evaluationAdapter, datalist,
				listKey, rowdata.get("Deal_Id"), "10", initType, tv_prase_more,
				tv_prase_lable, li_parse_content, scroll, rowdata);
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
		rowdata.put("StartCount", CommentService.getAvgStar());
		setResult(100, new Intent().putExtra("rowdata", rowdata));
		super.finish();
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.img_top_shop:
			// 显示更多图片
			Intent imageintent = new Intent(context,
					ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			String[] picarray = rowdata.get("Deal_HeadPic").split("\\|");
			for (int i = 0; i < picarray.length; i++) {
				picarray[i] = SystemApplication.getServiceUrl() + picarray[i];
			}
			imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, picarray);
			imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			startActivity(imageintent);

			break;
		
		case R.id.rl_tire:
			startActivity(new Intent(context, TubeSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId",rowdata.get("Deal_Id")));
			break;
		case R.id.rl_Inner_tube:
			startActivity(new Intent(context, InnerTubeSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Deal_Id")));
			break;
		case R.id.rl_Wheel_Gu:
			startActivity(new Intent(context, TubewheelSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Deal_Id")));
			break;
		case R.id.rl_lub_oil:
			startActivity(new Intent(context, LubeoilSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Deal_Id")));
			break;
		case R.id.img_shop_tel:
			// 用intent启动拨打电话
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ mobile));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.comment:
			startActivityForResult(
					new Intent(context, CommentActivity.class).putExtra("id",
							rowdata.get("Deal_Id")).putExtra("type", "10"), 100);
		case R.id.tv_prase_more:
			// 进入评论页面
			getEvaluationData(1, "up");
			break;
		default:
			break;
		}
	}
}
