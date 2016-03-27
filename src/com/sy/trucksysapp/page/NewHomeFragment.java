package com.sy.trucksysapp.page;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.CircleMenuClick;
import com.sy.trucksysapp.listener.FragmentCallListener;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity.AMap;
import com.sy.trucksysapp.page.driver.HighwayConditionActivity;
import com.sy.trucksysapp.page.driver.HotelListActivity;
import com.sy.trucksysapp.page.driver.NewsmainActivity;
import com.sy.trucksysapp.page.driver.ParkListActivity;
import com.sy.trucksysapp.page.driver.RestaurantListActivity;
import com.sy.trucksysapp.page.driver.WeatherActivity;
import com.sy.trucksysapp.page.freight.FreightListActivtity;
import com.sy.trucksysapp.page.gas.GasListActivity;
import com.sy.trucksysapp.page.rescue.TireRescueActivity;
import com.sy.trucksysapp.page.rescue.TrailerRescueActivity;
import com.sy.trucksysapp.page.service.ServiceActivity;
import com.sy.trucksysapp.page.shoping.ShopServiceFragment;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.CircleMenuLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 最新的首页
 * 
 * @author Administrator 2015-11-4
 * 
 * 
 */

public class NewHomeFragment extends Fragment implements CircleMenuClick,
		OnClickListener ,Runnable{
	private LocationManagerProxy mAMapLocationManager;
	private AMap amMap;
	public AMapLocation location;
	private LoacationChangeListener listenner;
	private Handler locationhandler = new Handler();
	private Dialog progDialog = null;// 搜索时进度条
	private Context context;
	private CircleMenuLayout circleLayout;
	private int xOffset = 15;
	private int yOffset = -13;
	private String city;
	private RelativeLayout composerButtonsWrapperPark,
			composer_buttons_wrapper_rescue, composer_buttons_wrapper_news;
	private FragmentCallListener callListener;
	// 停车住宿
	private ImageButton composer_button_food, composer_button_hotel,
			composer_button_wather, composer_button_park;
	// 维修救援
	private ImageButton composer_button_Tire_rescue,
			composer_button_Repair_rescue, composer_button_tralier_rescue,composer_button_other_rescue;
	// 交流资讯
	private ImageButton composer_button_news, composer_button_chat,
			composer_button_help,composer_button_game;
	private TextView tv_area;
	private RelativeLayout rl_location;
	private RelativeLayout commongRel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.new_home_fragment, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		callListener = (FragmentCallListener) getActivity();
		initView();
		getLocation();
	}

	private void initView() {
		context = getActivity();
		composerButtonsWrapperPark = (RelativeLayout) getView().findViewById(
				R.id.composer_buttons_wrapper_park);
		composer_buttons_wrapper_rescue = (RelativeLayout) getView()
				.findViewById(R.id.composer_buttons_wrapper_rescue);
		composer_buttons_wrapper_news = (RelativeLayout) getView()
				.findViewById(R.id.composer_buttons_wrapper_news);
		click(composerButtonsWrapperPark);
		xOffset = (int) (10.667 * context.getResources().getDisplayMetrics().density);
		yOffset = -(int) (8.667 * context.getResources().getDisplayMetrics().density);
		circleLayout = (CircleMenuLayout) getView().findViewById(
				R.id.circle_layout);
		circleLayout.setclick(this);
		composer_button_food = (ImageButton) getView().findViewById(
				R.id.composer_button_food);
		composer_button_food.setOnClickListener(this);
		composer_button_hotel = (ImageButton) getView().findViewById(
				R.id.composer_button_hotel);
		composer_button_hotel.setOnClickListener(this);
		composer_button_wather = (ImageButton) getView().findViewById(
				R.id.composer_button_wather);
		composer_button_wather.setOnClickListener(this);
		composer_button_park = (ImageButton) getView().findViewById(
				R.id.composer_button_park);
		composer_button_park.setOnClickListener(this);
		composer_button_Tire_rescue = (ImageButton) getView().findViewById(
				R.id.composer_button_Tire_rescue);
		composer_button_Tire_rescue.setOnClickListener(this);
		composer_button_Repair_rescue = (ImageButton) getView().findViewById(
				R.id.composer_button_Repair_rescue);
		composer_button_Repair_rescue.setOnClickListener(this);
		composer_button_tralier_rescue = (ImageButton) getView().findViewById(
				R.id.composer_button_tralier_rescue);
		composer_button_tralier_rescue.setOnClickListener(this);
		composer_button_news = (ImageButton) getView().findViewById(
				R.id.composer_button_news);
		composer_button_news.setOnClickListener(this);
		composer_button_chat = (ImageButton) getView().findViewById(
				R.id.composer_button_chat);
		composer_button_chat.setOnClickListener(this);
		composer_button_help = (ImageButton) getView().findViewById(
				R.id.composer_button_help);
		composer_button_help.setOnClickListener(this);
		composer_button_other_rescue = (ImageButton) getView().findViewById(
				R.id.composer_button_other_rescue);
		composer_button_other_rescue.setOnClickListener(this);
		composer_button_game = (ImageButton) getView().findViewById(
				R.id.composer_button_game);
		composer_button_game.setOnClickListener(this);
		
		tv_area = (TextView)getView().findViewById(R.id.tv_area);
		rl_location = (RelativeLayout)getView().findViewById(R.id.rl_location);
		rl_location.setOnClickListener(this);
	}

	@Override
	public void clickview(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.drawable.home_rescue_seletor://救援信息
			click(composer_buttons_wrapper_rescue);
			break;
		case R.drawable.home_freight_seletor://货运信息
			startActivity(new Intent(getActivity(), FreightListActivtity.class));
			break;
		case R.drawable.home_oil_seletor://加油加气
			startActivity(new Intent(getActivity(), GasListActivity.class));
			break;
		case R.drawable.home_park_seletor://停车住宿
			click(composerButtonsWrapperPark);
			break;
		case R.drawable.home_new_seletor://交流资讯
			click(composer_buttons_wrapper_news);
			break;

		default:
			break;
		}

	}

	/**
	 * 点击中心按钮
	 */
	public void centerClick() {
		startActivity(new Intent(getActivity(), ShopServiceFragment.class));
	}

	private void click(RelativeLayout layout) {
		if(commongRel!=null){
			if(commongRel!=layout){
				startAnimationsOut(commongRel, 1000);
				commongRel= layout;
				startAnimationsIn(layout, 1000);
				layout.setVisibility(View.VISIBLE);	
			}
		}else{
			commongRel= layout;
			startAnimationsIn(layout, 1000);
			layout.setVisibility(View.VISIBLE);
		}
//		composerButtonsWrapperPark.setVisibility(View.GONE);
//		composer_buttons_wrapper_rescue.setVisibility(View.GONE);
//		composer_buttons_wrapper_news.setVisibility(View.GONE);
		

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.composer_button_food://餐饮
			startActivity(new Intent(getActivity(), RestaurantListActivity.class));
			break;
		case R.id.composer_button_hotel://住宿
			startActivity(new Intent(getActivity(), HotelListActivity.class));
			break;
		case R.id.composer_button_wather://天气路况
			startActivity(new Intent(getActivity(), HighwayConditionActivity.class));
			break;
		case R.id.composer_button_park://停车场
			startActivity(new Intent(getActivity(), ParkListActivity.class));
			break;
		case R.id.composer_button_Tire_rescue://轮胎救援
			startActivity(new Intent(getActivity(), TireRescueActivity.class));
			break;
		case R.id.composer_button_Repair_rescue://维修救援
			startActivity(new Intent(getActivity(), ServiceActivity.class));
			break;
		case R.id.composer_button_tralier_rescue://拖车救援
			startActivity(new Intent(getActivity(), TrailerRescueActivity.class));
			break;
		case R.id.composer_button_news://新闻资讯
			startActivity(new Intent(getActivity(), NewsmainActivity.class));
			break;
		case R.id.composer_button_chat://热聊
			CommonUtils.showToast(getActivity(), "稍后上线，敬请关注！");
			break;
		case R.id.composer_button_help://互助
			CommonUtils.showToast(getActivity(), "稍后上线，敬请关注！");
			break;
		case R.id.composer_button_other_rescue:
			CommonUtils.showToast(getActivity(), "稍后上线，敬请关注！");
			break;
		case R.id.composer_button_game:
			CommonUtils.showToast(getActivity(), "稍后上线，敬请关注！");
			break;
		case R.id.rl_location:
			startLocation();
			break;
		default:
			break;
		}
	}

	/**
	 * 进入
	 * 
	 * @param viewgroup
	 * @param durationMillis
	 */
	public void startAnimationsIn(ViewGroup viewgroup, int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			ImageButton inoutimagebutton = (ImageButton) viewgroup
					.getChildAt(i);
			inoutimagebutton.setVisibility(0);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(mlp.rightMargin
					- xOffset, 0F, yOffset + mlp.bottomMargin, 0F);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset((i * 100)
					/ (-1 + viewgroup.getChildCount()));
			animation.setInterpolator(new OvershootInterpolator(2F));
			inoutimagebutton.startAnimation(animation);

		}
	}

	/**
	 * 退出
	 * 
	 * @param viewgroup
	 * @param durationMillis
	 */
	public void startAnimationsOut(ViewGroup viewgroup, int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			final ImageButton inoutimagebutton = (ImageButton) viewgroup
					.getChildAt(i);
			MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(0F, mlp.rightMargin
					- xOffset, 0F, yOffset + mlp.bottomMargin);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset(((viewgroup.getChildCount() - i) * 100)
					/ (-1 + viewgroup.getChildCount()));// 顺序倒一下比较舒服
			animation.setInterpolator(new AnticipateInterpolator(2F));
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					inoutimagebutton.setVisibility(8);
				}
			});
			inoutimagebutton.startAnimation(animation);
			viewgroup.setVisibility(View.GONE);
		}

	}
//*******************************************定位部分***************************************/
	public void getLocation() {
		amMap = new AMap();
		mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, -1, 10, amMap);
		locationhandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	}
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(getActivity(), "正在获取当前位置...");
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
	class AMap implements AMapLocationListener {

		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		// 获取经纬度
		@Override
		public void onLocationChanged(AMapLocation clocation) {
			location = clocation;
			city=location.getCity();
			if(city!=null&&!city.equals("")){
				tv_area.setText(city); 
			}
			stopLocation();
		}
	}
	/**
	 *开始定位
	 */
	public void startLocation() {
		if(mAMapLocationManager==null){
			showProgressDialog();
			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, -1, 10, amMap);
			locationhandler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		}
	}
	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		locationhandler.removeCallbacks(this);
		dissmissProgressDialog();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(amMap);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (location == null) {
//			listenner.onFailChange();
			stopLocation();// 销毁掉定位
		}
	}
}
