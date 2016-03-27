package com.sy.trucksysapp.page.shoping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.custom.vg.list.OnItemClickListener;
import com.custom.vg.list.OnItemLongClickListener;
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
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.user.CommentActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.JsWebViewClient;
import com.sy.trucksysapp.widget.ScrollListView;
import com.sy.trucksysapp.widget.SelectCustomAdapter;

/**
 * 服务商详情页面
 * 
 * @author lxs 20150516
 * 
 */
public class ServicesDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView tv_name, tv_LinkName, tv_email, tv_fax, tv_qq, tv_address,
			tv_time, tv_phone;

	private LinearLayout tv_service_content;
	private RelativeLayout rb_address;
	private String phone;
	private HashMap<String, String> rowdata;
	private DisplayImageOptions options;
	private ImageView img_shop_head;
	private ImageView comment;
	private Context context;
	private TextView tv_prase_lable, tv_prase_more;
	private LinearLayout li_parse_content;
	private ArrayList<CommentModel> datalist = new ArrayList<CommentModel>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private UserEvaluationAdapter evaluationAdapter;
	private ScrollListView lv_evaluation;
	private ScrollView scroll;
	private String[] imagepathArray = new String[1];
	Boolean isselect = false;

	RelativeLayout rel_select;
	com.custom.vg.list.CustomListView CustomListView;
	SelectCustomAdapter CustomListViewadapter;
	private List<HashMap<String, String>> servicelist;
	private LinearLayout otherLayout, li_service_content;
	private TextView text_service_content;
	private  ScrollListView lv_mshoplist;
	private HotSaleAdapter lv_madapter;
	private ArrayList<HashMap<String, String>>lv_list;
	private WebView tv_shop_memo;
	
	

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newservice_details);
		context = ServicesDetailActivity.this;
		// 隐藏收藏
		((ImageView) findViewById(R.id.follow)).setVisibility(View.GONE);
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("服务商详情");
		rel_select = (RelativeLayout) findViewById(R.id.rel_select);
		rowdata = (HashMap<String, String>) getIntent().getSerializableExtra(
				"rowdata");
		isselect = getIntent().getBooleanExtra("isselect", false);
		comment = (ImageView) findViewById(R.id.comment);
		comment.setOnClickListener(this);
		if (isselect) {
			comment.setVisibility(View.INVISIBLE);
			rel_select.setVisibility(View.VISIBLE);
			rel_select.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra("rowdata", rowdata);//
					setResult(2000, intent);
					finish();
				}
			});
		}
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
		li_parse_content = (LinearLayout) findViewById(R.id.li_parse_content);// 评论内容
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_name.setText(rowdata.get("Serv_Name"));
		tv_LinkName = (TextView) findViewById(R.id.tv_LinkName);
		tv_LinkName.setText("联系人：" + rowdata.get("Serv_LinkName"));
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_email.setText("email：" + rowdata.get("Serv_Email"));
		tv_fax = (TextView) findViewById(R.id.tv_fax);
		tv_fax.setText("传真：" + rowdata.get("Serv_Fax"));
		tv_qq = (TextView) findViewById(R.id.tv_qq);
		tv_qq.setText("QQ：" + rowdata.get("Serv_QQ"));
		tv_address = (TextView) findViewById(R.id.tv_address);
		tv_address.setText("地址：" + rowdata.get("Serv_Address"));
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_time.setText(rowdata.get("Serv_BusinessHours"));
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_phone.setText(rowdata.get("Serv_TelMobile"));
		
		tv_service_content = (LinearLayout) findViewById(R.id.tv_service_content);
		li_service_content = (LinearLayout) findViewById(R.id.li_service_content);
		text_service_content = (TextView) findViewById(R.id.text_service_content);
		CustomListView = (com.custom.vg.list.CustomListView) findViewById(R.id.angleView);
		servicelist = new ArrayList<HashMap<String, String>>();
		CustomListViewadapter = new SelectCustomAdapter(context, servicelist);
		CustomListView.setAdapter(CustomListViewadapter);
		CustomListViewadapter.notifyDataSetChanged();
		CustomListView.setDividerHeight(10);
		CustomListView.setDividerWidth(10);
		CustomListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						li_service_content.setVisibility(View.VISIBLE);
						text_service_content.setText(servicelist.get(arg2).get(
								"describe"));
						return true;
					}
				});
		CustomListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				li_service_content.setVisibility(View.VISIBLE);
				text_service_content.setText(servicelist.get(arg2).get(
						"describe"));
			}
		});
		otherLayout = (LinearLayout) findViewById(R.id.otherLayout);
		RelativeLayout rl_tire = (RelativeLayout) findViewById(R.id.rl_tire);
		 rl_tire.setOnClickListener(this);
		RelativeLayout rl_Inner_tube = (RelativeLayout) findViewById(R.id.rl_Inner_tube);
		 rl_Inner_tube.setOnClickListener(this);
		RelativeLayout rl_Wheel_Gu = (RelativeLayout) findViewById(R.id.rl_Wheel_Gu);
		 rl_Wheel_Gu.setOnClickListener(this);
		RelativeLayout rl_lub_oil = (RelativeLayout) findViewById(R.id.rl_lub_oil);
		 rl_lub_oil.setOnClickListener(this);
		lv_mshoplist = (ScrollListView) findViewById(R.id.lv_mshoplist);
		lv_list = new ArrayList<HashMap<String,String>>();
		lv_madapter = new HotSaleAdapter(context, lv_list);
		lv_mshoplist.setAdapter(lv_madapter);
		lv_mshoplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		TextView tv_parsevalue = (TextView) findViewById(R.id.tv_parsevalue);// 宾馆星级
		RatingBar rb_value = (RatingBar) findViewById(R.id.rb_value);// 宾馆星级
		rb_value.setStepSize(0.1f);
		float r = 0f;
		try {
			r = Float.parseFloat(rowdata.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		String p = TextUtils.FormatStar(r);// format 返回的是字符串
		rb_value.setRating(r);
		tv_parsevalue.setText(p);

		// tv_service_content.setText(rowdata.get("Serv_Remark"));
		rb_address = (RelativeLayout) findViewById(R.id.rb_address);
		rb_address.setOnClickListener(this);
		tv_phone.setOnClickListener(this);
		img_shop_head = (ImageView) findViewById(R.id.img_shop_head);
		ImageLoader.getInstance()
				.displayImage(
						SystemApplication.getServiceUrl()
								+ rowdata.get("Serv_HeadPic"), img_shop_head,
						options);
		if (!rowdata.get("Serv_HeadPic").equals("")) {

			img_shop_head.setOnClickListener(this);
			imagepathArray[0] = SystemApplication.getServiceUrl()
					+ rowdata.get("Serv_HeadPic");
		}
		
		tv_shop_memo = (WebView) findViewById(R.id.tv_shop_memo);
		tv_shop_memo.getSettings().setJavaScriptEnabled(true);
		tv_shop_memo.getSettings().setTextSize(TextSize.SMALLER);
		tv_shop_memo.loadData(CommonUtils.formatHtmlString(rowdata.get("Serv_Remark")),
				"text/html; charset=UTF-8", null);
		tv_shop_memo.setWebViewClient(new JsWebViewClient(tv_shop_memo, context));
		
		
		li_parse_content = (LinearLayout) findViewById(R.id.li_parse_content);// 评论内容
		lv_evaluation = (ScrollListView) findViewById(R.id.lv_evaluation);
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		scroll = (ScrollView) findViewById(R.id.scroll);
		tv_prase_lable = (TextView) findViewById(R.id.tv_prase_lable);// 评论
		tv_prase_more = (TextView) findViewById(R.id.tv_prase_more);// 更多评论
		tv_prase_more.setOnClickListener(this);

		// 根据服务id获取相关服务内容
		String url = SystemApplication.getBaseurl()
				+ "truckservice/GetServiceOfferById";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("id", rowdata.get("Serv_Id"));
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				try {
					JSONObject json = new JSONObject(arg1);
					JSONArray array = json.getJSONArray("rows");
					if (array.length() != 0) {
						servicelist.clear();
						otherLayout.setVisibility(View.VISIBLE);
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("name", obj.getString("name"));
							map.put("describe", obj.getString("describe"));
							servicelist.add(map);
						}
						CustomListViewadapter.notifyDataSetChanged();
					}
				} catch (Exception e) {

				}
			}
		});
		executeshopdata();
		getEvaluationData(0, "up");
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
			RequestParams.put("sellerId",rowdata.get("Serv_Id"));
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
	/**
	 * 获取评价列表数据
	 * 
	 * @param type
	 *            0:初始化 1:点击加载
	 */
	private void getEvaluationData(int initType, String type) {
		CommentService.getData(type, context, evaluationAdapter, datalist,
				listKey, rowdata.get("Serv_Id"), "9", initType, tv_prase_more,
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
	
	public void onClick(View view) {
		switch (view.getId()) {
		
		case R.id.rl_tire:
			startActivity(new Intent(context, TubeSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Serv_Id")));
			break;
		case R.id.rl_Inner_tube:
			startActivity(new Intent(context, InnerTubeSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Serv_Id")));
			break;
		case R.id.rl_Wheel_Gu:
			startActivity(new Intent(context, TubewheelSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Serv_Id")));
			break;
		case R.id.rl_lub_oil:
			startActivity(new Intent(context, LubeoilSaleListActivity.class).putExtra("sellerType", rowdata.get("SellerType")).putExtra("sellerId", rowdata.get("Serv_Id")));
			break;
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
			intentmap.putExtra("rowdata", rowdata);
			startActivity(intentmap);
			break;
		case R.id.tv_prase_more: // 进入评论页面
			getEvaluationData(1, "up");
			break;
		case R.id.comment:
			startActivityForResult(
					new Intent(context, CommentActivity.class).putExtra("id",
							rowdata.get("Serv_Id")).putExtra("type", "9"), 100);
			break;
		case R.id.img_shop_head:
			Intent intent2 = new Intent(context, ImagePagerActivity.class);
			intent2.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
					imagepathArray);
			intent2.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			context.startActivity(intent2);
			break;
		default:
			break;
		}
	}
}
