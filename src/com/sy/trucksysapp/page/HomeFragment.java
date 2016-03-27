package com.sy.trucksysapp.page;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.FragmentCallListener;
import com.sy.trucksysapp.listener.TopItemClickListener;
import com.sy.trucksysapp.page.driver.DriverIndexActivity;
import com.sy.trucksysapp.page.driver.HighwayConditionActivity;
import com.sy.trucksysapp.page.driver.NewsmainActivity;
import com.sy.trucksysapp.page.freight.FreightListActivtity;
import com.sy.trucksysapp.page.gas.GasListActivity;
import com.sy.trucksysapp.page.service.ServiceActivity;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.pullrefresh.ui.XScrollView;
import com.sy.trucksysapp.pullrefresh.ui.XScrollView.IXScrollViewListener;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.AutoScrollViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 首页
 * 
 * @author lxs 20150522
 * 
 */
public class HomeFragment extends Fragment implements OnClickListener {
	private Context context;
	private TextView topbase_center_text, tv_image_description;
	private ImageView topbase_back;
	/* 商城、维修救援、货运、驾驶员、加油 */
	// private LinearLayout ll_shop, ll_rescue, ll_freight, ll_oil, ll_driver,
	// ll_more;
	private RelativeLayout ll_shop, ll_rescue, ll_freight, ll_oil, ll_driver,
			ll_news, ll_traffic, ll_more;
	private FragmentCallListener callListener;
	private AutoScrollViewPager ScrollViewPager;
	private LinearLayout llPointGroup;
	private String[] imagepathArray;
	private String[] imageDescriptionArray;
	private String[] imageUrlArray;
	private String[] imagetypeArray;
	private int previousPointEnale = 0;
	/* 特卖汇、天天有礼、限时抢购 */
	private RelativeLayout rl_hot_sale, rl_day_gift, rl_flash_sale;
	private XScrollView sv_scrollView;
	private LayoutInflater inflater;
	private Handler mHandler;
	private Dialog progDialog = null;// 进度条

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_home, container, false);
		this.inflater = inflater;
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		callListener = (FragmentCallListener) getActivity();
		mHandler = new Handler();
		initViews();
		ExecuteImageData();
	}

	private void initViews() {
		context = getActivity();
		topbase_center_text = (TextView) getView().findViewById(
				R.id.topbase_center_text);
		topbase_center_text.setText("卡车团");
		topbase_back = (ImageView) getView().findViewById(R.id.topbase_back);
		topbase_back.setVisibility(View.GONE);
		sv_scrollView = (XScrollView) getView()
				.findViewById(R.id.sv_scrollView);
		sv_scrollView.setPullRefreshEnable(true);
		sv_scrollView.setPullLoadEnable(false);
		sv_scrollView.setAutoLoadEnable(true);
		LinearLayout contentview = (LinearLayout) inflater.inflate(
				R.layout.index_content, null);
		tv_image_description = (TextView) contentview
				.findViewById(R.id.tv_image_description);
		ScrollViewPager = (AutoScrollViewPager) contentview
				.findViewById(R.id.home_viewpager);
		llPointGroup = (LinearLayout) contentview
				.findViewById(R.id.ll_point_group);
		// ll_shop = (LinearLayout) contentview.findViewById(R.id.ll_shop);
		// ll_rescue = (LinearLayout) contentview.findViewById(R.id.ll_rescue);
		// ll_freight = (LinearLayout)
		// contentview.findViewById(R.id.ll_freight);
		// ll_oil = (LinearLayout) contentview.findViewById(R.id.ll_oil);
		// ll_driver = (LinearLayout) contentview.findViewById(R.id.ll_driver);
		// ll_more = (LinearLayout) contentview.findViewById(R.id.ll_more);

		ll_shop = (RelativeLayout) contentview.findViewById(R.id.rl_shop);
		ll_rescue = (RelativeLayout) contentview.findViewById(R.id.rl_rescue);
		ll_freight = (RelativeLayout) contentview.findViewById(R.id.rl_freight);
		ll_oil = (RelativeLayout) contentview.findViewById(R.id.rl_oil);
		ll_driver = (RelativeLayout) contentview.findViewById(R.id.rl_driver);
		ll_news = (RelativeLayout) contentview.findViewById(R.id.rl_news);
		ll_traffic = (RelativeLayout) contentview.findViewById(R.id.rl_traffic);
		ll_more = (RelativeLayout) contentview.findViewById(R.id.rl_more);
		rl_hot_sale = (RelativeLayout) contentview
				.findViewById(R.id.rl_hot_sale);
		rl_day_gift = (RelativeLayout) contentview
				.findViewById(R.id.rl_day_gift);
		rl_flash_sale = (RelativeLayout) contentview
				.findViewById(R.id.rl_flash_sale);
		ll_shop.setOnClickListener(this);
		ll_rescue.setOnClickListener(this);
		ll_freight.setOnClickListener(this);
		ll_oil.setOnClickListener(this);
		ll_driver.setOnClickListener(this);
		ll_news.setOnClickListener(this);
		rl_hot_sale.setOnClickListener(this);
		rl_day_gift.setOnClickListener(this);
		rl_flash_sale.setOnClickListener(this);
		ll_traffic.setOnClickListener(this);
		ll_more.setOnClickListener(this);
		ScrollViewPager.setOnPageChangeListener(new PosterPageChange());
		ScrollViewPager.startAutoScroll(5000);
		sv_scrollView.setContentView(contentview);
		sv_scrollView.setIXScrollViewListener(new IXScrollViewListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						ExecuteImageData();
					}
				}, 2500);
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.rl_shop:// 商城
			callListener.transfermsg(1);
			break;
		case R.id.rl_rescue:// 维修救援
			startActivity(new Intent(getActivity(), ServiceActivity.class));
			break;
		case R.id.rl_freight:// 新闻资讯
			callListener.transfermsg(2);
			break;
		case R.id.rl_oil:// 加油加气
			startActivity(new Intent(getActivity(), GasListActivity.class));
			break;
		case R.id.rl_driver:// 驾驶员 LifeServicesActivity
			startActivity(new Intent(getActivity(), DriverIndexActivity.class));

			break;
		case R.id.rl_news:// 更多
			startActivity(new Intent(getActivity(), NewsmainActivity.class));
			break;
		case R.id.rl_traffic:// 路况
			startActivity(new Intent(getActivity(),
					HighwayConditionActivity.class));
			break;
		case R.id.rl_more:// 货运信息
			startActivity(new Intent(getActivity(),
					FreightListActivtity.class));
			break;
		case R.id.rl_hot_sale:// 以旧换新
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
			} else {
				CommonUtils.showToast(context, "以旧换新，敬请期待！");
//				startActivity(new Intent(getActivity(), ExchangeActivity.class));
			}
			break;
		case R.id.rl_day_gift:// 限时秒杀
			if (PreferenceUtils.getInstance(context).getSettingUserId()
					.equals("")
					|| PreferenceUtils.getInstance(context).getSettingUserId()
							.equals("null")
					|| PreferenceUtils.getInstance(context).getSettingUserId() == null) {
				startActivityForResult(
						new Intent(context, LoginActivity.class), 888);
			} else {
				// startActivity(new Intent(getActivity(),
				// SeckillActivity.class));
				getGoodInfo();
			}
			break;
		case R.id.rl_flash_sale:// 限时抢购
			// callListener.transfermsg(1);
			CommonUtils.showToast(context, "爆款产品，敬请期待");
			break;

		default:
			break;
		}
	}

	private void ExecuteImageData() {
		// 从服务器 读取广告列表
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetAd";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		client.post(url, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(result);
					previousPointEnale = 0;
					int total = json.getInt("total");
					if (total > 0) {
						JSONArray adList = json.getJSONArray("rows");
						imagepathArray = new String[total];
						imageDescriptionArray = new String[total];
						imageUrlArray = new String[total];
						imagetypeArray = new String[total];
						for (int i = 0; i < adList.length(); i++) {
							imageDescriptionArray[i] = adList.getJSONObject(i)
									.getString("Ad_Name");
							imagepathArray[i] = SystemApplication.getInstance()
									.getImgUrl()
									+ adList.getJSONObject(i).getString(
											"Ad_Pic");
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
						TopviewAdapter adapter = new TopviewAdapter(
								imagepathArray, context, ScrollViewPager, false);
						adapter.setTopItemClickListener(new TopItemClickListener() {
							@Override
							public void onClick(int index) {
								// TODO Auto-generated method stub
								try {
									CommonUtils.openUrlByType(index,
											imagetypeArray, imageUrlArray,
											getActivity());
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						});
						ScrollViewPager.setAdapter(adapter);
						if (llPointGroup.getChildCount() > 0)
							llPointGroup.getChildAt(0).setEnabled(true);
						tv_image_description.setText(imageDescriptionArray[0]);
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar c1 = Calendar.getInstance();
				c1.setTime(new Date());
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
				sv_scrollView.setRefreshTime(format.format(c1.getTime()));
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
				sv_scrollView.setRefreshTime(format.format(c1.getTime()));
				onLoad();
			}
		});
	}

	private void onLoad() {
		sv_scrollView.stopRefresh();
		sv_scrollView.stopLoadMore();
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
			tv_image_description.setText(imageDescriptionArray[position
					% imagepathArray.length]);
			llPointGroup.getChildAt(position % imagepathArray.length)
					.setEnabled(true);
			previousPointEnale = position % imagepathArray.length;
		}
	}

	/**
	 * 获取限时秒杀产品的信息
	 */
	private void getGoodInfo() {
		// 从服务器 获取商品的详情信息
		showProgressDialog();
		String url = SystemApplication.getBaseurl() + "TruckService/GetTire";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", "" + 10);
		RequestParams.put("currPage", "" + 1);
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

					if (json.getInt("total") > 0) {
						//
						JSONArray array = json.getJSONArray("rows");
						JSONObject data = array.getJSONObject(0);

						String systemTime = json.getString("obj");
						String endTime = data.getString("SecKillEndTime");
						Date systemDate = parseDateTime(systemTime);
						Date endDate = parseDateTime(endTime);
						Calendar systemT = Calendar.getInstance();
						systemT.setTime(systemDate);
						Calendar endT = Calendar.getInstance();
						endT.setTime(endDate);
						if (systemT.getTimeInMillis() < endT.getTimeInMillis()) {
							startActivity(new Intent(getActivity(),
									SeckillActivity.class).putExtra("data",
									result));
						} else {
							CommonUtils.showToast(context, "活动已经结束,请预约下一轮活动！");
							startActivity(new Intent(getActivity(),
									ReserveActivity.class));
						}
					} else {
						CommonUtils.showToast(context, "活动已经结束,请预约下一轮活动！");
						startActivity(new Intent(getActivity(),
								ReserveActivity.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});
	}

	/**
	 * 将字符串形式的日期时间表示解析为时间对象
	 * 
	 * @param timeString
	 * @return
	 */
	private Date parseDateTime(String timeString) {
		try {
			return DateUtils.parseDate(timeString, new String[] {
					"yyyy-MM-dd HH:mm:ss", "yyyy-M-d H:m:s",
					"yyyy-MM-dd H:m:s", "yyyy-M-d HH:mm:ss",
					"yyyy-MM-dd HH:mm:ss|SSS", "yyyyMMdd HH:mm:ss",
					"yyyy-MM-dd HH:mm", "yyyy-MM-dd" });
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(getActivity(), "正在处理请求...");
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
