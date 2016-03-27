package com.sy.trucksysapp.page.shoping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.CommentService;
import com.sy.trucksysapp.page.FollowService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.SalePaperAdapter;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.user.ShopCartActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.AutoScrollViewPager;
import com.sy.trucksysapp.widget.JsWebViewClient;
import com.sy.trucksysapp.widget.NumEditText;
import com.sy.trucksysapp.widget.ScrollListView;

/**
 * 商品详情页面
 * 
 * @author lxs 20150506
 * 
 */
public class SaleDetailActivity extends BaseActivity implements
		OnCheckedChangeListener, OnPageChangeListener, OnClickListener {

	// private ImageView iv_product_pic;
	private ViewPager mViewPager;
	private TextView topbase_center_text;
	private RadioGroup select_rg;
	private int width;
	private RelativeLayout.LayoutParams params;
	private View v_line;
	private Context context;
	private ArrayList<View> list;
	private View salesView;
	private View evaluationView;
	private View supplierView;
	private TextView tv_old_price, tv_now_price, tv_sale_name, tv_left_num;
	private LinearLayout ll_product_detail;
	private ImageView iv_product_buy, follow;
	private Button bt_product_buy;
	private Button bt_tel_service;
	private EditText et_product_num;
	private NumEditText net_product_count;
	private AutoScrollViewPager autoviewpager;
	private LinearLayout llPointGroup;
	private int previousPointEnale = 0;
	private WebView webview;
	private String[] imagepathArray;
	private String detailContent;

	private ImageView img_prodetail;
	// private SerializableMap serializableMap;
	private HashMap<String, String> rowmap;
	/**
	 * 
	 1轮胎2润滑油3轮辋4内胎垫带
	 */
	private int SaleType;
	private WebView tv_product_property;
	// 供应商页面
	private ImageView img_top_shop, img_shop_tel;
	private TextView tv_shop_name, tv_shop_address, tv_contacts, tv_fax,
			tv_email, tv_qq;
	private WebView tv_shop_range;
	private DisplayImageOptions options;
	private String SellerId;
	private String SellerType;
	private String mobile;
	// 评价列表
	private String GoCo_GoodId;
	private String id;
	String gg = "";
	private String MemberId;
	private String type;

	private TextView tv_prase_lable, tv_prase_more;
	private LinearLayout li_parse_content;
	private ArrayList<CommentModel> datalist = new ArrayList<CommentModel>();
	private ArrayList<String> listKey = new ArrayList<String>();
	private UserEvaluationAdapter evaluationAdapter;
	private ScrollListView lv_evaluation;
	private ScrollView scroll;
	private TextView topbase_shopbagcount;

	RelativeLayout rel_buy;
	View line_buy;
	private Dialog progDialog = null;// 进度条

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activtiy_sale_mian);
		context = SaleDetailActivity.this;
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		initView();
		initLine();
		Executeshopbag();
	}

	/***
	 * 更新购物车数量
	 */
	private void Executeshopbag() {
		if (PreferenceUtils.getInstance(context).getSettingUserId().equals("")
				|| PreferenceUtils.getInstance(context).getSettingUserId()
						.equals("null")
				|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
			return;
		}
		List<CartProduct> listcarts = CommonUtils
				.getCartProductlist(SaleDetailActivity.this);
		if (listcarts != null) {
			topbase_shopbagcount.setText(listcarts.size() + "");
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Executeshopbag();
	}

	public void finish() {
		if (FollowService.isFollowed()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("rowdata", rowmap);
			intent.putExtras(bundle);
			setResult(100, intent);
		}
		super.finish();
	}

	private void initView() {
		context = SaleDetailActivity.this;
		topbase_shopbagcount = (TextView) findViewById(R.id.topbase_shopbagcount);
		// 购物车数量
		RelativeLayout rl_shopbg = (RelativeLayout) findViewById(R.id.rl_shopbg);
		rl_shopbg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 打开购物车页面
				if (PreferenceUtils.getInstance(context).getSettingUserId()
						.equals("")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId().equals("null")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId() == null) {
					context.startActivity(new Intent(context,
							LoginActivity.class));
					return;
				}
				startActivityForResult(new Intent(context,
						ShopCartActivity.class), 555);
			}
		});
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("商品详情");
		follow = (ImageView) findViewById(R.id.follow);
		follow.setOnClickListener(this);
		loaddata();
	}

	private void initviewafterdata() {
		if (!CommonUtils.getString(rowmap, "Favo_CreateDate").equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		list = new ArrayList<View>();
		initChildviews();
		list.add(salesView);
		list.add(evaluationView);
		list.add(supplierView);
		mViewPager = (ViewPager) findViewById(R.id.viewPager1);
		mViewPager.setAdapter(new SalePaperAdapter(list));
		select_rg = (RadioGroup) findViewById(R.id.rg_selsect);
		select_rg.setOnCheckedChangeListener(this);
		mViewPager.setOnPageChangeListener(this);
		initDataSaleView(SaleType);
	}

	private void loaddata() {
		// TODO Auto-generated method stub
		try {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				SaleType = bundle.getInt("Type", -1);
				id = bundle.getString("saleid");
				String url = SystemApplication.getBaseurl()
						+ "truckservice/GetProductDetail";
				AsyncHttpClient client = new AsyncHttpClient();
				client.setTimeout(10000);
				// 显示进度条
				showProgressDialog();
				RequestParams RequestParams = new RequestParams();
				RequestParams.put("id", id);
				RequestParams.put("type", SaleType + "");
				RequestParams.put("Member_Id", MemberId);
				client.post(url, RequestParams, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						super.onSuccess(result);
						dissmissProgressDialog();
						try {
							JSONObject json = new JSONObject(result);
							JSONArray array = json.getJSONArray("rows");
							if (array.length() != 0) {
								JSONObject ActivityData = array
										.getJSONObject(0);
								rowmap = CommonUtils.getMap(ActivityData);
								SellerType = rowmap.get("SellerType");
								SellerId = rowmap.get("SellerId");
								initviewafterdata();
							} else {
								CommonUtils.showToast(context,
										"未找到商品记录，或该商品已下架！");
								SaleDetailActivity.this.finish();
							}
						} catch (Exception e) {
							// TODO: handle exception
							CommonUtils.showToast(context, "查询异常，获取数据失败！");
							SaleDetailActivity.this.finish();
						}

					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
						dissmissProgressDialog();
						CommonUtils.showToast(context, "查询异常，获取数据失败！");
						SaleDetailActivity.this.finish();
					}
				});
			}

		} catch (Exception e) {
			dissmissProgressDialog();
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	/**
	 * 初始化滑动界面
	 */
	private void initChildviews() {
		/**
		 * 商品详情页面
		 */
		salesView = getLayoutInflater().inflate(R.layout.activity_sale_detail,
				null);
		rel_buy = (RelativeLayout) salesView.findViewById(R.id.rel_buy);
		line_buy = salesView.findViewById(R.id.line_buy);
		bt_product_buy = (Button) salesView.findViewById(R.id.bt_product_buy);
		bt_product_buy.setOnClickListener(this);
		bt_tel_service = (Button) salesView.findViewById(R.id.bt_tel_service);
		bt_tel_service.setOnClickListener(this);

		net_product_count = (NumEditText) salesView
				.findViewById(R.id.net_product_count);
		tv_now_price = (TextView) salesView.findViewById(R.id.tv_now_price);
		tv_old_price = (TextView) salesView.findViewById(R.id.tv_old_price);
		tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
		tv_sale_name = (TextView) salesView.findViewById(R.id.tv_sale_name);
		tv_left_num = (TextView) salesView.findViewById(R.id.tv_left_num);
		ll_product_detail = (LinearLayout) salesView
				.findViewById(R.id.ll_product_detail);
		tv_product_property = (WebView) salesView
				.findViewById(R.id.tv_product_property);
		img_prodetail = (ImageView) salesView.findViewById(R.id.img_prodetail);
		ll_product_detail.setOnClickListener(this);
		webview = (WebView) salesView.findViewById(R.id.webview);
		iv_product_buy = (ImageView) salesView
				.findViewById(R.id.iv_product_buy);
		iv_product_buy.setOnClickListener(this);
		et_product_num = (EditText) salesView.findViewById(R.id.et_product_num);
		autoviewpager = (AutoScrollViewPager) salesView
				.findViewById(R.id.bcviewpager);
		llPointGroup = (LinearLayout) salesView
				.findViewById(R.id.ll_point_group);
		autoviewpager.setOnPageChangeListener(new PosterPageChange());
		// autoviewpager.startAutoScroll(1000);
		initTopView();
		/**
		 * 评价页面
		 */
		evaluationView = getLayoutInflater().inflate(
				R.layout.activity_sale_parameter, null);
		((TextView) evaluationView.findViewById(R.id.title))
				.setVisibility(View.GONE);
		li_parse_content = (LinearLayout) evaluationView
				.findViewById(R.id.li_parse_content);// 评论内容
		lv_evaluation = (ScrollListView) evaluationView
				.findViewById(R.id.lv_evaluation);
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		View v_split = evaluationView.findViewById(R.id.v_split);
		v_split.setVisibility(View.GONE);
		tv_prase_lable = (TextView) evaluationView
				.findViewById(R.id.tv_prase_lable);// 评论
		tv_prase_more = (TextView) evaluationView
				.findViewById(R.id.tv_prase_more);// 更多评论
		tv_prase_more.setOnClickListener(this);

		/**
		 * 供应商页面
		 */
		supplierView = getLayoutInflater()
				.inflate(R.layout.supplier_view, null);

		LinearLayout li_shoptj = (LinearLayout) supplierView
				.findViewById(R.id.li_shoptj);
		li_shoptj.setVisibility(View.GONE);
		LinearLayout estimate = (LinearLayout) supplierView
				.findViewById(R.id.estimate);
		estimate.setVisibility(View.GONE);
		img_top_shop = (ImageView) supplierView.findViewById(R.id.img_top_shop);
		img_shop_tel = (ImageView) supplierView.findViewById(R.id.img_shop_tel);
		img_shop_tel.setOnClickListener(this);
		tv_shop_name = (TextView) supplierView.findViewById(R.id.tv_shop_name);
		tv_shop_address = (TextView) supplierView
				.findViewById(R.id.tv_shop_address);
		tv_contacts = (TextView) supplierView.findViewById(R.id.tv_contacts);
		tv_fax = (TextView) supplierView.findViewById(R.id.tv_fax);
		tv_email = (TextView) supplierView.findViewById(R.id.tv_email);
		tv_qq = (TextView) supplierView.findViewById(R.id.tv_qq);
		tv_shop_range = (WebView) supplierView.findViewById(R.id.tv_shop_range);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	/**
	 * 根据类型判断填充数据
	 * 
	 * @param SaleType
	 */
	private void initDataSaleView(int SaleType) {

		if (rowmap.get("IsShopping") != null
				&& rowmap.get("IsShopping").equals("0")) {
			rel_buy.setVisibility(View.GONE);
			line_buy.setVisibility(View.GONE);
		}
		switch (SaleType) {
		case 1:
			this.type = "Tire";
			rel_buy.setVisibility(View.VISIBLE);
			line_buy.setVisibility(View.VISIBLE);
			if (rowmap.get("IsShopping") != null
					&& rowmap.get("IsShopping").equals("0")) {
				bt_product_buy.setVisibility(View.GONE);
				net_product_count.setVisibility(View.GONE);
				bt_tel_service.setVisibility(View.VISIBLE);
			} else {
				bt_product_buy.setVisibility(View.VISIBLE);
				net_product_count.setVisibility(View.VISIBLE);
				bt_tel_service.setVisibility(View.GONE);
			}
			SetTube();
			break;
		case 2:
			this.type = "InnerTube";
			SetInnerTube();
			break;
		case 3:
			this.type = "Rim";
			SetTubeWheel();
			break;
		case 4:
			this.type = "LubeOil";
			SetLubeoil();
			break;
		default:
			break;
		}
		getEvaluationData(0, "up");
		getSupplierData();
	}

	/**
	 * 设置轮胎数据
	 */
	private void SetTube() {
		// MaxLoad
		GoCo_GoodId = rowmap.get("TireId");
		tv_now_price.setText("¥" + rowmap.get("Price"));
		tv_old_price.setText("市场价：¥" + rowmap.get("MarketPrice"));
		tv_sale_name.setText(rowmap.get("Title"));
		tv_left_num.setText("库存：" + rowmap.get("Stock") + "件");
		net_product_count.setMaxnum(Integer.valueOf(rowmap.get("Stock")));
		try {
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = rowmap.get("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + rowmap.get("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + rowmap.get("SpecName"));
			gg = rowmap.get("SpecName");
			builder.append("</label></span></li>");

			builder.append("<li><span><label>");
			if (rowmap.get("IsInnerTube") != null
					&& rowmap.get("IsInnerTube").equals("1")) {
				builder.append("有内胎：是");
			} else {
				builder.append("有内胎：否");
			}
			builder.append("</label></span></li>");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				builder.append("<li><span><label>");
				builder.append(item.getString("key") + "："
						+ item.getString("value"));
				builder.append("</label></span></li>");
			}
			builder.append("</ul>");
			tv_product_property.loadData(builder.toString(),
					"text/html; charset=UTF-8", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 设置内胎数据
	 */
	private void SetInnerTube() {
		String[] specarray = getResources().getStringArray(R.array.tubediandai);
		int specindex = Integer.valueOf(rowmap.get("SpecId"));
		String InTuType = rowmap.get("InTuType");
		GoCo_GoodId = rowmap.get("InnerTubeId");
		tv_now_price.setText("¥" + rowmap.get("Price"));
		tv_old_price.setText("市场价：¥" + rowmap.get("MarketPrice"));
		tv_sale_name.setText(rowmap.get("Title"));
		tv_left_num.setText("库存：" + rowmap.get("Stock"));
		net_product_count.setMaxnum(Integer.valueOf(rowmap.get("Stock")));
		try {
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = rowmap.get("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + rowmap.get("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (InTuType.equals("1")) {
				builder.append("规格：" + rowmap.get("SpecName"));
				gg = rowmap.get("SpecName");
			} else {
				builder.append("规格：" + specarray[specindex]);
				gg = specarray[specindex];
			}
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (rowmap.get("InTuType") != null
					&& rowmap.get("InTuType").equals("1")) {
				builder.append("类型：内胎");
			} else {
				builder.append("类型：垫带");
			}
			builder.append("</label></span></li>");

			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				builder.append("<li><span><label>");
				builder.append(item.getString("key") + "："
						+ item.getString("value"));
				builder.append("</label></span></li>");
			}
			builder.append("</ul>");
			tv_product_property.loadData(builder.toString(),
					"text/html; charset=UTF-8", null);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置轮毂数据
	 */
	private void SetTubeWheel() {
		String[] specarray = getResources().getStringArray(R.array.tubewheel);
		int specindex = Integer.valueOf(rowmap.get("SpecId"));
		GoCo_GoodId = rowmap.get("RimId");
		tv_now_price.setText("¥" + rowmap.get("Price"));
		tv_old_price.setText("市场价：¥" + rowmap.get("MarketPrice"));
		tv_sale_name.setText(rowmap.get("Title"));
		tv_left_num.setText("库存：" + rowmap.get("Stock"));
		net_product_count.setMaxnum(Integer.valueOf(rowmap.get("Stock")));
		try {
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = rowmap.get("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + rowmap.get("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + specarray[specindex]);
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (rowmap.get("IsInnerTube") != null
					&& rowmap.get("IsInnerTube").equals("1")) {
				builder.append("有内胎：是");
			} else {
				builder.append("有内胎：否");
			}
			builder.append("</label></span></li>");
			gg = specarray[specindex];
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				builder.append("<li><span><label>");
				builder.append(item.getString("key") + "："
						+ item.getString("value"));
				builder.append("</label></span></li>");
			}
			builder.append("</ul>");
			tv_product_property.loadData(builder.toString(),
					"text/html; charset=UTF-8", null);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置润滑油数据
	 */
	private void SetLubeoil() {
		String[] specarray = getResources().getStringArray(R.array.bubeoil);
		int specindex = Integer.valueOf(rowmap.get("SpecId"));
		GoCo_GoodId = rowmap.get("LubeId");
		tv_now_price.setText("¥" + rowmap.get("Price"));
		tv_old_price.setText("市场价：¥" + rowmap.get("MarketPrice"));
		tv_sale_name.setText(rowmap.get("Title"));
		tv_left_num.setText("库存：" + rowmap.get("Stock"));
		net_product_count.setMaxnum(Integer.valueOf(rowmap.get("Stock")));
		try {
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = rowmap.get("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + rowmap.get("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + specarray[specindex]);
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (rowmap.get("LuOiType") != null
					&& rowmap.get("LuOiType").equals("1")) {
				builder.append("类型：汽油");
			} else {
				builder.append("类型：柴油");
			}
			builder.append("</label></span></li>");
			gg = specarray[specindex];
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				builder.append("<li><span><label>");
				builder.append(item.getString("key") + "："
						+ item.getString("value"));
				builder.append("</label></span></li>");
			}
			builder.append("</ul>");
			tv_product_property.loadData(builder.toString(),
					"text/html; charset=UTF-8", null);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化线
	 */
	@SuppressWarnings("deprecation")
	private void initLine() {
		v_line = this.findViewById(R.id.v_line);
		float scale = this.getResources().getDisplayMetrics().density;
		int height = (int) (4 * scale + 0.5f);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth() / 3;
		params = new RelativeLayout.LayoutParams(width, height);
		v_line.setLayoutParams(params);
	}

	private void initTopView() {
		String pics = rowmap.get("Pic1");
		if (pics != null) {
			String[] array = pics.split("\\|");
			imagepathArray = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				imagepathArray[i] = SystemApplication.getServiceUrl()
						+ array[i];
			}
			LayoutParams params;
			// 初始化广告条资源
			for (int i = 0; i < imagepathArray.length; i++) {
				// 初始化广告条正下方的"点"
				View dot = new View(context);
				dot.setBackgroundResource(R.drawable.point_background);
				params = new LayoutParams(10, 10);
				params.leftMargin = 10;
				dot.setLayoutParams(params);
				dot.setEnabled(false);
				llPointGroup.addView(dot);
			}
			TopviewAdapter adapter = new TopviewAdapter(imagepathArray,
					context, autoviewpager, true);
			autoviewpager.setAdapter(adapter);
			llPointGroup.getChildAt(0).setEnabled(true);

		}

	}

	/**
	 * 获取详细信息
	 */
	private void getDetailData() {
		// 从服务器 获取商品的详情信息
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetDescriptionByTireId";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		switch (SaleType) {
		case 1:
			// 轮胎
			RequestParams.put("id", rowmap.get("TireId"));
			RequestParams.put("type", "1");
			break;
		case 2:
			// 内胎
			RequestParams.put("id", rowmap.get("InnerTubeId"));
			RequestParams.put("type", "2");
			break;
		case 3:
			// 轮毂
			RequestParams.put("id", rowmap.get("RimId"));
			RequestParams.put("type", "3");
			break;
		case 4:
			// 润滑油
			RequestParams.put("id", rowmap.get("LubeId"));
			RequestParams.put("type", "4");
			break;
		default:
			break;
		}
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}

			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						JSONObject obj = json.getJSONObject("Obj");
						if (obj.getString("Description") == null
								|| obj.getString("Description").equals("")
								|| obj.getString("Description").equals("null")) {
							detailContent = "暂无介绍";
						} else {
							detailContent = obj.getString("Description");
						}
						WebSettings setting = webview.getSettings();
						webview.getSettings().setTextSize(
								WebSettings.TextSize.NORMAL);
						webview.getSettings().setDefaultTextEncodingName(
								"UTF -8");// 设置默认为utf-8
						webview.getSettings().setJavaScriptEnabled(true);
						webview.loadData(
								CommonUtils.formatHtmlString(detailContent),
								"text/html; charset=UTF-8", null);
						webview.setWebViewClient(new JsWebViewClient(webview,
								context));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		});
	}

	/**
	 * 获取和填充供应商信息
	 */
	private void getSupplierData() {
		String url = SystemApplication.getBaseurl()
				+ "truckservice/GetSellerDetail";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("SellerType", SellerType);
		RequestParams.put("SellerId", SellerId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						if (SellerType.equals("1")) {
							// 经销商
							JSONObject obj = json.getJSONObject("Obj");
							tv_contacts.setText(TextUtils.FormatStr(obj
									.getString("Deal_LinkName")));
							tv_shop_address.setText(TextUtils.FormatStr(obj
									.getString("Deal_Address")));
							tv_shop_name.setText(TextUtils.FormatStr(obj
									.getString("Deal_Name")));
							tv_fax.setText(TextUtils.FormatStr(obj
									.getString("Deal_Fax")));
							tv_email.setText(TextUtils.FormatStr(obj
									.getString("Deal_Email")));
							tv_qq.setText(TextUtils.FormatStr(obj
									.getString("Deal_QQ")));
							WebSettings setting = tv_shop_range.getSettings();
							tv_shop_range.getSettings().setTextSize(
									WebSettings.TextSize.NORMAL);
							tv_shop_range.getSettings()
									.setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8
							tv_shop_range.getSettings().setJavaScriptEnabled(
									true);
							tv_shop_range.loadData(CommonUtils
									.formatHtmlString(TextUtils.FormatStr(obj
											.getString("Deal_Remark"))),
									"text/html; charset=UTF-8", null);
							tv_shop_range.setWebViewClient(new JsWebViewClient(
									tv_shop_range, context));
							String url = SystemApplication.getServiceUrl()
									+ obj.getString("Deal_HeadPic");
							ImageLoader.getInstance().displayImage(url,
									img_top_shop, options);
							mobile = obj.getString("Deal_TelMobile");
						} else if (SellerType.equals("2")) {
							JSONObject obj = json.getJSONObject("Obj");
							tv_contacts.setText(TextUtils.FormatStr(obj
									.getString("Fact_LinkName")));
							tv_shop_address.setText(TextUtils.FormatStr(obj
									.getString("Fact_Address")));
							tv_shop_name.setText(TextUtils.FormatStr(obj
									.getString("Fact_Name")));
							tv_fax.setText(TextUtils.FormatStr(obj
									.getString("Fact_Fax")));
							tv_email.setText(TextUtils.FormatStr(obj
									.getString("Fact_Email")));
							tv_qq.setText(TextUtils.FormatStr(obj
									.getString("Fact_QQ")));
							WebSettings setting = tv_shop_range.getSettings();
							tv_shop_range.getSettings().setTextSize(
									WebSettings.TextSize.NORMAL);
							tv_shop_range.getSettings()
									.setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8
							tv_shop_range.getSettings().setJavaScriptEnabled(
									true);
							tv_shop_range.loadData(CommonUtils
									.formatHtmlString(TextUtils.FormatStr(obj
											.getString("Fact_Remark"))),
									"text/html; charset=UTF-8", null);
							tv_shop_range.setWebViewClient(new JsWebViewClient(
									tv_shop_range, context));
							String url = SystemApplication.getServiceUrl()
									+ obj.getString("Fact_HeadPic");
							ImageLoader.getInstance().displayImage(url,
									img_top_shop, options);
							mobile = obj.getString("Fact_TelMobile");
						} else {
							JSONObject obj = json.getJSONObject("Obj");
							tv_contacts.setText(TextUtils.FormatStr(obj
									.getString("Serv_LinkName")));
							tv_shop_address.setText(TextUtils.FormatStr(obj
									.getString("Serv_Address")));
							tv_shop_name.setText(TextUtils.FormatStr(obj
									.getString("Serv_Name")));
							tv_fax.setText(TextUtils.FormatStr(obj
									.getString("Serv_Fax")));
							tv_email.setText(TextUtils.FormatStr(obj
									.getString("Serv_Email")));
							tv_qq.setText(TextUtils.FormatStr(obj
									.getString("Serv_QQ")));
							WebSettings setting = tv_shop_range.getSettings();
							tv_shop_range.getSettings().setTextSize(
									WebSettings.TextSize.NORMAL);
							tv_shop_range.getSettings()
									.setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8
							tv_shop_range.getSettings().setJavaScriptEnabled(
									true);
							tv_shop_range.loadData(CommonUtils
									.formatHtmlString(TextUtils.FormatStr(obj
											.getString("Serv_Remark"))),
									"text/html; charset=UTF-8", null);
							tv_shop_range.setWebViewClient(new JsWebViewClient(
									tv_shop_range, context));
							String url = SystemApplication.getServiceUrl()
									+ obj.getString("Serv_HeadPic");
							ImageLoader.getInstance().displayImage(url,
									img_top_shop, options);
							mobile = obj.getString("Serv_TelMobile");
						}
					} else {
						CommonUtils.showToast(context, "获取详细信息失败！");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				CommonUtils.showToast(context, "连接服务器失败！");
			}
		});

	}

	/**
	 * 获取评价列表数据
	 * 
	 * @param type
	 *            0:初始化 1:点击加载
	 */
	private void getEvaluationData(int initType, String type) {
		CommentService.getData(type, context, evaluationAdapter, datalist,
				listKey, GoCo_GoodId, SaleType + "", initType, tv_prase_more,
				tv_prase_lable, li_parse_content, scroll, rowmap);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		int leftPx = (int) (width * (arg0 + arg1));
		params.setMargins(leftPx, 0, 0, 0);
		v_line.setLayoutParams(params);
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		int checkid = 0;
		switch (arg0) {
		case 0:
			checkid = R.id.rb_sales;
			break;
		case 1:
			checkid = R.id.rb_evaluation;
			break;
		case 2:
			checkid = R.id.rb_supplier;
			break;
		}
		select_rg.check(checkid);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		if (checkedId == R.id.rb_sales) {
			mViewPager.setCurrentItem(0);
		} else if (checkedId == R.id.rb_evaluation) {
			mViewPager.setCurrentItem(1);
		} else if (checkedId == R.id.rb_supplier) {
			mViewPager.setCurrentItem(3);
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ll_product_detail:
			if (webview.getVisibility() == View.GONE) {
				webview.setVisibility(View.VISIBLE);
				img_prodetail
						.setBackgroundResource(R.drawable.product_detail_down);
				if (detailContent == null) {
					getDetailData();
				}

			} else {
				img_prodetail
						.setBackgroundResource(R.drawable.product_detail_new);
				webview.setVisibility(View.GONE);
			}

			break;
		case R.id.bt_product_buy:
			// 加入购物车
			try {
				MemberId = PreferenceUtils.getInstance(context)
						.getSettingUserId();
				if (MemberId.equals("")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId().equals("null")
						|| PreferenceUtils.getInstance(context)
								.getSettingUserId() == null) {
					context.startActivity(new Intent(context,
							LoginActivity.class));
					return;
				}
				if (Integer.valueOf(rowmap.get("Stock")) <= 0) {
					CommonUtils.showToast(context, "库存不足，加入购物车失败！");
					return;
				}
				List<CartProduct> list = CommonUtils
						.getCartProductlist(context);
				CartProduct product = new CartProduct();
				product.setCheck(false);
				if (et_product_num.getText().toString().trim().equals("")) {
					CommonUtils.showToast(context, "请输入购买商品数量");
					return;
				}
				product.setCount(Integer.valueOf(et_product_num.getText() + ""));// 购买商品数量
				switch (SaleType) {
				case 1:
					product.setFname(rowmap.get("Title"));
					if (imagepathArray != null && imagepathArray.length != 0) {
						product.setImgsrc(imagepathArray[0]);
					}
					product.setIsSale("0");
					product.setParameters(gg);
					product.setProductId(rowmap.get("TireId") + "");
					product.setProductType("1");
					product.setSellerId(rowmap.get("SellerId") + "");
					product.setSellerType(rowmap.get("SellerType") + "");
					product.setSum(Double.valueOf(rowmap.get("Price") + ""));
					product.setProductcount(Integer.valueOf(rowmap.get("Stock")
							+ ""));
					break;

				case 2:
					product.setFname(rowmap.get("Title"));
					if (imagepathArray != null && imagepathArray.length != 0) {
						product.setImgsrc(imagepathArray[0]);
					}
					product.setIsSale("0");
					product.setParameters(gg);
					product.setProductId(rowmap.get("InnerTubeId") + "");
					product.setProductType("2");
					product.setSellerId(rowmap.get("SellerId") + "");
					product.setSellerType(rowmap.get("SellerType") + "");
					product.setSum(Double.valueOf(rowmap.get("Price") + ""));
					product.setProductcount(Integer.valueOf(rowmap.get("Stock")
							+ ""));
					break;
				case 3:
					product.setFname(rowmap.get("Title"));
					if (imagepathArray != null && imagepathArray.length != 0) {
						product.setImgsrc(imagepathArray[0]);
					}
					product.setIsSale("0");
					product.setParameters(gg);
					product.setProductId(rowmap.get("RimId") + "");
					product.setProductType("3");
					product.setSellerId(rowmap.get("SellerId") + "");
					product.setSellerType(rowmap.get("SellerType") + "");
					product.setSum(Double.valueOf(rowmap.get("Price") + ""));
					product.setProductcount(Integer.valueOf(rowmap.get("Stock")
							+ ""));
					break;
				case 4:
					product.setFname(rowmap.get("Title"));
					if (imagepathArray != null && imagepathArray.length != 0) {
						product.setImgsrc(imagepathArray[0]);
					}
					product.setIsSale("0");
					product.setParameters(gg);
					product.setProductId(rowmap.get("LubeId") + "");
					product.setProductType("4");
					product.setSellerId(rowmap.get("SellerId") + "");
					product.setSellerType(rowmap.get("SellerType") + "");
					product.setSum(Double.valueOf(rowmap.get("Price") + ""));
					product.setProductcount(Integer.valueOf(rowmap.get("Stock")
							+ ""));
					break;
				default:
					break;
				}
				// 判断是否已经购买了卖商品
				boolean isbuy = false;
				for (int i = 0; i < list.size(); i++) {
					CartProduct p = list.get(i);
					java.text.DecimalFormat df = new java.text.DecimalFormat(
							"#.00");
					if (p.getIsSale().equals("0")
							&& p.getProductId().equals(product.getProductId())
							&& p.getProductType().equals(
									product.getProductType())
							&& df.format(p.getSum()).equals(
									df.format(product.getSum()))) {
						// 已经购买了该商品
						int t = p.getCount();
						p.setCount(t + product.getCount());
						isbuy = true;
						break;
					}
				}
				if (!isbuy) {
					list.add(product);
				}
				if (CommonUtils.saveCartProductlist(context, list)) {
					CommonUtils.showToast(context, "已成功加入购物车！");
					Executeshopbag();
				} else {
					CommonUtils.showToast(context, "加入购物车失败！");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CommonUtils.showToast(context, "加入购物车失败！");
			}
			break;
		case R.id.iv_product_buy:

			break;
		case R.id.img_shop_tel:
			// 用intent启动拨打电话
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ mobile));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.follow:// 收藏
			if (rowmap == null) {
				CommonUtils.showToast(context, "请等待信息加载完毕");
				break;
			}
			FollowService.follow(context, rowmap, (Object) id, type, follow);
			break;
		case R.id.bt_tel_service:
			select_rg.check(R.id.rb_supplier);
			break;
		default:
			break;
		}
	}

	/**
	 * 页面切换事件的监听
	 * 
	 * @author Administrator
	 * 
	 */
	class PosterPageChange implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			llPointGroup.getChildAt(previousPointEnale).setEnabled(false);
			// 消除上一次的状态点
			// 设置当前的状态点“点”
			llPointGroup.getChildAt(position % imagepathArray.length)
					.setEnabled(true);
			previousPointEnale = position % imagepathArray.length;
		}
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
