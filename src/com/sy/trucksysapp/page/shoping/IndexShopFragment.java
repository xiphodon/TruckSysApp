package com.sy.trucksysapp.page.shoping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.listener.FragmentCallListener;
import com.sy.trucksysapp.listener.TopItemClickListener;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.pullrefresh.ui.XScrollView;
import com.sy.trucksysapp.pullrefresh.ui.XScrollView.IXScrollViewListener;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.user.ShopCartActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.AutoScrollViewPager;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

/**
 * 商城的首页
 * 
 * @author lxs 20150506
 * 
 */
public class IndexShopFragment extends Fragment implements OnClickListener {
	private Context context;
	private LayoutInflater inflater;
	private LinearLayout content;

	private AutoScrollViewPager bcviewpager;
	private LinearLayout llPointGroup;
	private TextView tvDescription;
	private String[] imageDescriptionArray;
	private String[] imagepathArray;
	private String[] imagetypeArray;
	private String[] imageUrlArray;
	private DisplayImageOptions options;
	private int previousPointEnale = 0;
	/* 轮胎、内胎、轮毂、润滑油 */
	private RelativeLayout rl_tire, rl_Inner_tube, rl_Wheel_Gu, rl_lub_oil;
	private com.sy.trucksysapp.widget.LoadingFrameLayout test;
	private XScrollView mScrollView;
	private Handler mHandler;
	private int pageSize = 10;
	private int currPage = 1;
	private int allpage = 0;
	private TextView topbase_shopbagcount;
	private FragmentCallListener callListener;
	private List<View> views = new ArrayList<View>();
	private LinearLayout ll_content;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.activity_shop_index, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		callListener = (FragmentCallListener) getActivity();
		mHandler = new Handler();
		initView();
		ExecuteImageData();
		GetHotListData();
		Executeshopbag();
	}

	private void initView() {
		context = getActivity();
		// 购物车数量
		RelativeLayout rl_shopbg = (RelativeLayout) getView().findViewById(
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
				startActivityForResult(new Intent(getActivity(),
						ShopCartActivity.class), 555);
			}
		});
		topbase_shopbagcount = (TextView) getView().findViewById(
				R.id.topbase_shopbagcount);
		TextView topbase_center_text = (TextView) getView().findViewById(
				R.id.topbase_center_text);
		topbase_center_text.setText("商城");
		Button icon_service = (Button) getView().findViewById(
				R.id.icon_showservice);
		icon_service.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity(), ShopServiceFragment.class));
				// startActivity(new Intent(context,
				// ShopServiceActivity.class));
			}
		});
		content = (LinearLayout) inflater.inflate(
				R.layout.shop_index_viewpager, null);
		bcviewpager = (AutoScrollViewPager) content
				.findViewById(R.id.bcviewpager);
		llPointGroup = (LinearLayout) content.findViewById(R.id.ll_point_group);
		ll_content = (LinearLayout) content.findViewById(R.id.ll_content);
		tvDescription = (TextView) content
				.findViewById(R.id.tv_image_description);
		test = (LoadingFrameLayout) getView().findViewById(R.id.test);
		test.show("页面加载中...");
		rl_tire = (RelativeLayout) content.findViewById(R.id.rl_tire);
		rl_tire.setOnClickListener(this);
		rl_Inner_tube = (RelativeLayout) content
				.findViewById(R.id.rl_Inner_tube);
		rl_Inner_tube.setOnClickListener(this);
		rl_Wheel_Gu = (RelativeLayout) content.findViewById(R.id.rl_Wheel_Gu);
		rl_Wheel_Gu.setOnClickListener(this);
		rl_lub_oil = (RelativeLayout) content.findViewById(R.id.rl_lub_oil);
		rl_lub_oil.setOnClickListener(this);
		mScrollView = (XScrollView) getView().findViewById(
				R.id.pull_refresh_list);
		mScrollView.setPullRefreshEnable(true);
		mScrollView.setPullLoadEnable(true);
		mScrollView.setAutoLoadEnable(true);
		mScrollView.setContentView(content);
		mScrollView.setIXScrollViewListener(new IXScrollViewListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						ExecuteImageData();
						ll_content.removeAllViews();
						GetHotListData();
					}
				}, 2500);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						GetHotListData();
					}
				}, 2500);
			}
		});

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		bcviewpager.setOnPageChangeListener(new PosterPageChange());
		bcviewpager.startAutoScroll(5000);
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
				.getCartProductlist(getActivity());
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

	private void ExecuteImageData() {
		// 从服务器 读取广告列表
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetAd";
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(result);
					previousPointEnale = 0;
					int total = json.getInt("total");
					JSONArray adList = json.getJSONArray("rows");
					imageDescriptionArray = new String[total];
					imagepathArray = new String[total];
					imageUrlArray = new String[total];
					imagetypeArray = new String[total];
					for (int i = 0; i < adList.length(); i++) {
						imageDescriptionArray[i] = adList.getJSONObject(i)
								.getString("Ad_Name");
						imagepathArray[i] = SystemApplication.getInstance()
								.getImgUrl()
								+ adList.getJSONObject(i).getString("Ad_Pic");
						imageUrlArray[i] = adList.getJSONObject(i)
								.getString("Ad_Url");
						imagetypeArray[i] = adList.getJSONObject(i)
								.getString("Ad_Type");
					}
					LayoutParams params;
					// 初始化广告条资源
					llPointGroup.removeAllViews();
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
					if (imagepathArray.length != 0) {
						TopviewAdapter adapter = new TopviewAdapter(
								imagepathArray, context, bcviewpager, false);
						bcviewpager.setAdapter(adapter);
						tvDescription.setText(imageDescriptionArray[0]);
						adapter.setTopItemClickListener(new TopItemClickListener() {
							@Override
							public void onClick(int index) {
								// TODO Auto-generated method stub
								try {
									CommonUtils.openUrlByType(index,imagetypeArray,imageUrlArray,getActivity());
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
						if (llPointGroup.getChildCount() > 0)
							llPointGroup.getChildAt(0).setEnabled(true);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar c1 = Calendar.getInstance();
				c1.setTime(new Date());
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				mScrollView.setRefreshTime(format.format(c1.getTime()));
				onLoad();
				super.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(new Date());
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				mScrollView.setRefreshTime(format.format(c1.getTime()));
				onLoad();
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
			if (llPointGroup.getChildCount() > 0) {
				llPointGroup.getChildAt(previousPointEnale).setEnabled(false);
				tvDescription.setText(imageDescriptionArray[position
						% imagepathArray.length]);
				// 消除上一次的状态点
				// 设置当前的状态点“点”
				llPointGroup.getChildAt(position % imagepathArray.length)
						.setEnabled(true);
				previousPointEnale = position % imagepathArray.length;
			}
		}
	}

	/**
	 * 获取特卖的列表数据
	 */
	private void GetHotListData() {
		// 从服务器 获取特卖的列表数据
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetSaleProduct";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", pageSize + "");
		RequestParams.put("currPage", currPage + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				try {
					JSONObject json = new JSONObject(result);
					JSONArray list = json.getJSONArray("rows");
					int total = json.getInt("total");
					for (int i = 0; i < list.length(); i++) {
						final JSONObject detail = list.getJSONObject(i);
						View v = inflater.inflate(
								R.layout.item_special_sale_product, null);
						ImageView goodsIconIv = (ImageView) v
								.findViewById(R.id.goodsIconIv);
						TextView tv_goodsName = (TextView) v
								.findViewById(R.id.tv_goodsName);
						TextView text_price1 = (TextView) v
								.findViewById(R.id.text_price1);
						TextView text_price2 = (TextView) v
								.findViewById(R.id.text_price2);
						text_price2.getPaint().setFlags(
								Paint.STRIKE_THRU_TEXT_FLAG); // 中划线
						TextView text_time = (TextView) v
								.findViewById(R.id.text_time);
						TextView text_count = (TextView) v
								.findViewById(R.id.text_count);
						LinearLayout lin_item = (LinearLayout) v
								.findViewById(R.id.lin_item);
						String pics = detail.getString("Pic1");
						String[] picsarray = pics.split("\\|");
						if (picsarray.length != 0) {
							ImageLoader.getInstance().displayImage(
									SystemApplication.getInstance().getImgUrl()
											+ picsarray[0], goodsIconIv,
									options);
						}
						text_price1.setText("¥" + detail.getString("Price"));
						text_price2.setText("¥"
								+ detail.getString("MarketPrice"));
						text_count.setText(detail.getString("Stock") + "件");
						tv_goodsName.setText(detail.getString("Title"));
						text_time.setText(detail.getString("EndTime"));
						lin_item.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								try {
									String id = detail.getString("SaleId");
									String name = detail.getString("Title");
									startActivity(new Intent(context,
											HotSaleDetailActivity.class)
											.putExtra("saleid", id).putExtra(
													"salename", name));
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
						ll_content.addView(v);
						allpage++;
					}
					if (total > allpage) {
						mScrollView.setPullLoadEnable(true);
						currPage++;
					} else {
						mScrollView.setPullLoadEnable(false);
					}
					test.dismiss();
					onLoad();
				} catch (Exception e) {
					// TODO: handle exception
					CommonUtils.showToast(context, "服务器异常，请重试！");
					test.dismiss();
					onLoad();
				}

			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				test.dismiss();
				onLoad();
			}
		});
	}

	private void onLoad() {
		mScrollView.stopRefresh();
		mScrollView.stopLoadMore();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.rl_tire:
			startActivity(new Intent(context, TubeSaleListActivity.class).putExtra("sellerType", "").putExtra("sellerId", ""));
			break;
		case R.id.rl_Inner_tube:
			startActivity(new Intent(context, InnerTubeSaleListActivity.class).putExtra("sellerType", "").putExtra("sellerId", ""));
			break;
		case R.id.rl_Wheel_Gu:
			startActivity(new Intent(context, TubewheelSaleListActivity.class).putExtra("sellerType", "").putExtra("sellerId", ""));
			break;
		case R.id.rl_lub_oil:
			startActivity(new Intent(context, LubeoilSaleListActivity.class).putExtra("sellerType", "").putExtra("sellerId", ""));
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		bcviewpager.stopAutoScroll();
		super.onDestroy();
	}

}
