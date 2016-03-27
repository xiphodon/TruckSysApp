package com.sy.trucksysapp.page.shoping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.amap.api.location.f;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.order.OrderFragment;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.user.ShopCartActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.AutoScrollViewPager;
import com.sy.trucksysapp.widget.JsWebViewClient;
import com.sy.trucksysapp.widget.LoadingFrameLayout;
import com.sy.trucksysapp.widget.NumEditText;
import com.sy.trucksysapp.widget.ScrollListView;

/**
 * 特卖商品详情
 * 
 * @author lxs 20150512
 * 
 */
public class HotSaleDetailActivity extends BaseActivity implements
		OnClickListener {

	private Context context;
	private TextView topbase_center_text;
	private TextView tv_sale_name, tv_now_price, tv_old_price, tv_left_time,
			tv_left_number, tv_product_category, tv_product_FigureName,
			tv_product_SpeedGrade, tv_product_MaxLoad, tv_product_TreadWidth,
			tv_product_Diameter, tv_product_AbrasionGrade;
	private JSONObject ActivityData = null;
	private LinearLayout ll_product_evaluation, ll_product_detail;
	private ScrollListView lv_evaluation;
	private TextView tv_prase_more;
	private UserEvaluationAdapter evaluationAdapter;
	private Button iv_product_buy;
	private AutoScrollViewPager autoviewpager;
	private LinearLayout llPointGroup;
	private int previousPointEnale = 0;
	private String[] imagepathArray;
	private LinearLayout ll_product_property;
	private int SaleId;
	private String detailContent = "";
	private WebView webview;
	private ImageView img_evaluation, img_prodetail;
	private Handler handler;
	private Runnable runnable;
	private String lltimestr;
	boolean haswan = true;
	private EditText et_product_num;
	private NumEditText net_product_count;
	String pics;
	String gg = "";
	/**
	 * 特卖的商品类型, 1:轮胎，2：内胎，3：轮辋，4：润滑油，5：其他
	 */
	private int SaleType;
	private WebView tv_product_property;
	private ArrayList<CommentModel> datalist;
	private int datasize = 0;
	private int evaluationpage = 1;
	private int evaluationpagesize = 10;
	private LinearLayout li_evaluation;
	private String id = "0";
	com.sy.trucksysapp.widget.LoadingFrameLayout loading;
	private TextView tv_prase_no;
	private TextView topbase_shopbagcount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_sale_detail);
		context = HotSaleDetailActivity.this;
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("特卖商品详情");
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.show("正在努力加载数据...");
		// 购物车数量
		RelativeLayout rl_shopbg = (RelativeLayout) findViewById(
				R.id.rl_shopbg);
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
		topbase_shopbagcount = (TextView)findViewById(
				R.id.topbase_shopbagcount);
		Executeshopbag();
		loaddata();
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
				.getCartProductlist(HotSaleDetailActivity.this);
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
	private void initView() {
		et_product_num = (EditText) findViewById(R.id.et_product_num);
		net_product_count = (NumEditText) findViewById(R.id.net_product_count);
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				tv_left_time.setText("剩余时间：" + getDistanceTimes(lltimestr));
				if (haswan) {
					handler.postDelayed(this, 1000);
				}
			}
		};
		tv_sale_name = (TextView) findViewById(R.id.tv_sale_name);
		tv_now_price = (TextView) findViewById(R.id.tv_now_price);
		tv_old_price = (TextView) findViewById(R.id.tv_old_price);
		tv_left_time = (TextView) findViewById(R.id.tv_left_time);
		tv_left_number = (TextView) findViewById(R.id.tv_left_number);
		tv_product_property = (WebView) findViewById(R.id.tv_product_property);
		img_evaluation = (ImageView) findViewById(R.id.img_evaluation);
		img_prodetail = (ImageView) findViewById(R.id.img_prodetail);
		webview = (WebView) findViewById(R.id.webview);
		WebSettings setting = webview.getSettings();
		// setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// setting.setUseWideViewPort(true);
		// setting.setLoadWithOverviewMode(true);
		webview.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
		webview.getSettings().setDefaultTextEncodingName("UTF -8");// 设置默认为utf-8
		ll_product_evaluation = (LinearLayout) findViewById(R.id.ll_product_evaluation);
		ll_product_evaluation.setOnClickListener(this);
		ll_product_detail = (LinearLayout) findViewById(R.id.ll_product_detail);
		ll_product_detail.setOnClickListener(this);
		ll_product_property = (LinearLayout) findViewById(R.id.ll_product_property);
		iv_product_buy = (Button) findViewById(R.id.iv_product_buy);
		iv_product_buy.setOnClickListener(this);
		lv_evaluation = (ScrollListView) findViewById(R.id.lv_evaluation);
		li_evaluation = (LinearLayout) findViewById(R.id.li_evaluation);
		tv_prase_more = (TextView) findViewById(R.id.tv_prase_more);
		tv_prase_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getEvaluationData();
			}
		});
		tv_prase_no = (TextView) findViewById(R.id.tv_prase_no);
		datalist = new ArrayList<CommentModel>();
		evaluationAdapter = new UserEvaluationAdapter(context, datalist);
		lv_evaluation.setAdapter(evaluationAdapter);
		autoviewpager = (AutoScrollViewPager) findViewById(R.id.bcviewpager);
		llPointGroup = (LinearLayout) findViewById(R.id.ll_point_group);
		autoviewpager.setOnPageChangeListener(new PosterPageChange());
		initData();
		initTopView();
		initDataSaleView(SaleType);
	}

	private void loaddata() {
		// TODO Auto-generated method stub
		try {
			id = getIntent().getStringExtra("saleid");
			String url = SystemApplication.getInstance().getBaseurl()
					+ "truckservice/GetProductDetail";
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(10000);
			RequestParams RequestParams = new RequestParams();
			RequestParams.put("id", id);
			RequestParams.put("type", "0");
			client.post(url, RequestParams, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					super.onSuccess(result);
					try {
						JSONObject json = new JSONObject(result);
						JSONArray array = json.getJSONArray("rows");
						if (array.length() != 0) {
							ActivityData = array.getJSONObject(0);
							initView();
						} else {
							CommonUtils.showToast(context, "未找到商品记录，或该商品已下架！");
						}

					} catch (Exception e) {
						// TODO: handle exception
						CommonUtils.showToast(context, "查询异常，获取数据失败！");
					}
					loading.dismiss();
				}

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
					CommonUtils.showToast(context, "查询异常，获取数据失败！");
					loading.dismiss();
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * 两个时间相差距离多少天多少小时多少分多少秒
	 * 
	 * @param str1
	 *            时间参数 1 格式：1990-01-01 12:00:00
	 * @param str2
	 *            时间参数 2 格式：2009-01-01 12:00:00
	 * @return String 返回值为：xx天xx小时xx分xx秒
	 */
	public String getDistanceTimes(String str1) {
		String rsstr = "00时00分00秒";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = new Date();
			;
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
				haswan = false;
				return rsstr;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			if (day == 0) {
				rsstr = ((hour < 10 ? ("0" + hour) : (hour + "")) + "时")
						+ ((min < 10 ? ("0" + min) : (min + "")) + "分")
						+ ((sec < 10 ? ("0" + sec) : (sec + "")) + "秒");
			} else {
				rsstr = day + "天"
						+ ((hour < 10 ? ("0" + hour) : (hour + "")) + "时")
						+ ((min < 10 ? ("0" + min) : (min + "")) + "分")
						+ ((sec < 10 ? ("0" + sec) : (sec + "")) + "秒");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return rsstr;
	}

	private void initData() {
		if (ActivityData != null) {
			try {
				SaleId = ActivityData.getInt("SaleId");
				SaleType = ActivityData.getInt("SaleType");
				tv_sale_name.setText(ActivityData.getString("Title"));
				tv_now_price
						.setText("现价 : ¥" + ActivityData.getString("Price"));
				tv_old_price.setText("原价 : ¥"
						+ ActivityData.getString("MarketPrice"));
				tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
				lltimestr = ActivityData.getString("EndTime");
				handler.postDelayed(runnable, 1000);
				tv_left_number.setText("剩余数量："
						+ ActivityData.getString("Stock") + "件");
				net_product_count.setMaxnum(Integer.valueOf(ActivityData
						.getString("Stock")));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ll_product_evaluation:
			if (li_evaluation != null) {
				if (li_evaluation.getVisibility() == View.GONE) {
					if (datasize == 0) {
						getEvaluationData();
					}
					li_evaluation.setVisibility(View.VISIBLE);
					img_evaluation
							.setBackgroundResource(R.drawable.product_detail_down);
				} else {
					tv_prase_more.setVisibility(View.GONE);
					li_evaluation.setVisibility(View.GONE);
					tv_prase_no.setVisibility(View.GONE);
					img_evaluation
							.setBackgroundResource(R.drawable.product_detail_new);
				}
			}
			break;
		case R.id.ll_product_detail:
			if (webview != null) {
				if (webview.getVisibility() == View.GONE) {
					webview.setVisibility(View.VISIBLE);
					getDetailData();
					img_prodetail
							.setBackgroundResource(R.drawable.product_detail_down);
				} else {
					webview.setVisibility(View.GONE);
					img_prodetail
							.setBackgroundResource(R.drawable.product_detail_new);
				}
			}
			break;
		case R.id.iv_product_buy:
			// 加入购物车
			try {
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
				if(!haswan){
					CommonUtils.showToast(context, "商品已过期！");
					return;
				}
				if (Integer.valueOf(ActivityData.getString("Stock")) <= 0) {
					CommonUtils.showToast(context, "库存不足，加入购物车失败！");
					return;
				}
				List<CartProduct> list = CommonUtils
						.getCartProductlist(context);
				// 判断是否已经购买了卖商品
				CartProduct product = new CartProduct();
				product.setCheck(false);
				if(et_product_num.getText().toString().trim().equals("")){
					CommonUtils.showToast(context, "请输入购买商品数量");
					return;
				}
				product.setCount(Integer.valueOf(et_product_num.getText() + ""));// 购买商品数量
				product.setFname(ActivityData.getString("Title"));
				pics = ActivityData.getString("Pic1");
				if (pics != null) {
					String[] array = pics.split("\\|");
					imagepathArray = new String[array.length];
					product.setImgsrc(SystemApplication.getInstance()
							.getImgUrl() + array[0]);
				}
				product.setIsSale("1");
				product.setParameters(gg);
				product.setProductId(ActivityData.getString("SaleId") + "");
				product.setProductType(ActivityData.getString("SaleType") + "");
				product.setSellerId("");
				product.setSellerType("");
				product.setSum(new Double(ActivityData.getString("Price")));
				product.setProductcount(ActivityData.getInt("Stock"));
				
				boolean isbuy = false;
				for (int i = 0; i < list.size(); i++) {
					CartProduct p = list.get(i);
					java.text.DecimalFormat df = new java.text.DecimalFormat(
							"#.00");
					if (p.getIsSale().equals("1")
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CommonUtils.showToast(context, "加入购物车失败！");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 加载顶部图片
	 */
	private void initTopView() {

		try {
			pics = ActivityData.getString("Pic1");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pics != null) {
			String[] array = pics.split("\\|");
			imagepathArray = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				imagepathArray[i] = SystemApplication.getInstance().getImgUrl()
						+ array[i];
			}
			int total = imagepathArray.length;
			LayoutParams params;
			// 初始化广告条资源
			for (int i = 0; i < imagepathArray.length; i++) {
				// 初始化广告条正下方的"点"
				View dot = new View(context);
				dot.setBackgroundResource(R.drawable.point_background);
				params = new LayoutParams(15, 15);
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
	 * 根据类型判断填充数据
	 * 
	 * @param SaleType
	 */
	private void initDataSaleView(int SaleType) {

		switch (SaleType) {
		case 1:
			SetTube();
			break;
		case 2:
			SetInnerTube();
			break;
		case 3:
			SetTubeWheel();
			break;
		case 4:
			SetLubeoil();
			break;

		default:
			break;
		}
	}

	/**
	 * 设置轮胎数据
	 */
	private void SetTube() {
		try {
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = ActivityData.getString("AttrList");
			JSONArray array = new JSONArray(rowstr);
			builder.append("<li><span><label>");
			builder.append("品牌：" + ActivityData.getString("Brand_Name"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + ActivityData.getString("SpecName"));
			gg = ActivityData.getString("SpecName");
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (ActivityData.getString("IsInnerTube") != null
					&& ActivityData.getString("IsInnerTube").equals("1")) {
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
		try {
			String[] specarray = getResources().getStringArray(
					R.array.tubediandai);
			int specindex = ActivityData.getInt("SpecId");
			String IsInnerTube = ActivityData.getString("IsInnerTube");
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = ActivityData.getString("AttrList");
			builder.append("<li><span><label>");
			builder.append("品牌：" + ActivityData.getString("BrandName"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (IsInnerTube.equals("1")) {
				builder.append("规格：" + ActivityData.getString("SpecName"));
				gg = ActivityData.getString("SpecName");
			} else {
				builder.append("规格：" + specarray[specindex]);
				gg = specarray[specindex];
			}
			builder.append("</label></span></li>");

			builder.append("<li><span><label>");
			if (IsInnerTube != null && IsInnerTube.equals("1")) {
				builder.append("类型：内胎");
			} else {
				builder.append("类型：垫带");
			}
			builder.append("</label></span></li>");

			if (!rowstr.equals("null") && rowstr.equals("")
					&& rowstr.equals("[]")) {
				JSONArray array = new JSONArray(rowstr);
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);
					builder.append("<li><span><label>");
					builder.append(item.getString("key") + "："
							+ item.getString("value"));
					builder.append("</label></span></li>");
				}
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
		try {
			String[] specarray = getResources().getStringArray(
					R.array.tubewheel);
			int specindex = ActivityData.getInt("SpecId");
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = ActivityData.getString("AttrList");
			builder.append("<li><span><label>");
			builder.append("品牌：" + ActivityData.getString("BrandName"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + specarray[specindex]);
			builder.append("</label></span></li>");
			gg = specarray[specindex];
			builder.append("<li><span><label>");
			if (ActivityData.getString("IsInnerTube") != null
					&& ActivityData.getString("IsInnerTube").equals("1")) {
				builder.append("有内胎：是");
			} else {
				builder.append("有内胎：否");
			}
			builder.append("</label></span></li>");
			if (!rowstr.equals("null") && rowstr.equals("")
					&& rowstr.equals("[]")) {
				JSONArray array = new JSONArray(rowstr);
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);
					builder.append("<li><span><label>");
					builder.append(item.getString("key") + "："
							+ item.getString("value"));
					builder.append("</label></span></li>");
				}
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
		try {
			String[] specarray = getResources().getStringArray(R.array.bubeoil);
			int specindex = ActivityData.getInt("SpecId");
			StringBuilder builder = new StringBuilder("");
			builder.append("<ul style=\"margin-left:-20px;\">");
			String rowstr = ActivityData.getString("AttrList");
			builder.append("<li><span><label>");
			builder.append("品牌：" + ActivityData.getString("BrandName"));
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			builder.append("规格：" + specarray[specindex]);
			builder.append("</label></span></li>");
			builder.append("<li><span><label>");
			if (ActivityData.getString("LuOiType") != null
					&& ActivityData.getString("LuOiType").equals("1")) {
				builder.append("类型：汽油");
			} else {
				builder.append("类型：柴油");
			}
			builder.append("</label></span></li>");
			gg = specarray[specindex];
			if (!rowstr.equals("null") && rowstr.equals("")
					&& rowstr.equals("[]")) {
				JSONArray array = new JSONArray(rowstr);
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = array.getJSONObject(i);
					builder.append("<li><span><label>");
					builder.append(item.getString("key") + "："
							+ item.getString("value"));
					builder.append("</label></span></li>");
				}
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
	 * 获取详细信息
	 */
	private void getDetailData() {
		// 从服务器 获取商品的详情信息
		try {
			detailContent = ActivityData.getString("Description");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (detailContent.equals("")) {
			detailContent = "暂无介绍";
		}
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadData(CommonUtils.formatHtmlString(detailContent),
				"text/html; charset=UTF-8", null);
		webview.setWebViewClient(new JsWebViewClient(webview, context));
	}

	/**
	 * 获取评价列表数据
	 */
	private void getEvaluationData() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetGoodsComment";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", evaluationpagesize + "");
		RequestParams.put("currPage", evaluationpage + "");
		RequestParams.put("GoCo_GoodId", SaleId + "");
		RequestParams.put("GoCo_GoodType", 0 + "");

		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				ArrayList<CommentModel> data = new ArrayList<CommentModel>();
				try {
					JSONObject json = new JSONObject(result);
					datasize = json.getInt("total");
					JSONArray array = json.getJSONArray("rows");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						CommentModel mo = new CommentModel();
						mo.setCommentcontent(obj.getString("GoCo_Content"));
						mo.setDatestr(obj.getString("GoCo_CreateDate"));
						mo.setImgurl(obj.getString("GoCo_Pic"));
						mo.setPersonname(obj.getString("GoCo_Name"));
						mo.setStarnum(obj.getInt("GoCo_Star"));
						data.add(mo);
					}
					datalist.addAll(data);
					if (datasize == datalist.size()) {
						tv_prase_more.setVisibility(View.GONE);
					} else {
						evaluationpage++;
						tv_prase_more.setVisibility(View.VISIBLE);
					}
					if (datasize <= 0) {
						tv_prase_no.setVisibility(View.VISIBLE);
					} else {
						tv_prase_no.setVisibility(View.GONE);
					}
					evaluationAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
					CommonUtils.showToast(context, "获取评价信息失败！");
				}
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
}
