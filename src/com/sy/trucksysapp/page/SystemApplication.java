package com.sy.trucksysapp.page;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class SystemApplication extends Application {

	public static SystemApplication instance;
	// public static String baseurl = "http://192.168.1.22:901/";
	// public static String Imgurl = "http://192.168.1.22:903";
	public static String Imgurl = "http://106.3.44.245:10010";// 特卖新闻广告
	public static String baseurl = "http://106.3.44.245:10011/";
	public static String orderurl = "http://106.3.44.245:90/";
	public static String serviceurl = "http://106.3.44.245:10013";
	public static String baseNew;
	public static String imgNew;
	public static String orderNew;
	public static String serviceNew;

	public void onCreate() {
		super.onCreate();
		instance = this;
		initImageLoader(getApplicationContext());
	}

	public static SystemApplication getInstance() {
		return instance;
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout() {
		PreferenceUtils.getInstance(getApplicationContext()).setSettingHeadImg(
				null);
		PreferenceUtils.getInstance(getApplicationContext()).setSettingUserId(
				null);
		PreferenceUtils.getInstance(getApplicationContext())
				.setSettingUserName(null);
		PreferenceUtils.getInstance(getApplicationContext())
				.setSettingUserNick(null);
		PreferenceUtils.getInstance(getApplicationContext())
				.setSettingUserPassword(null);
		CommonUtils.saveCartProductlist(getApplicationContext(),
				new ArrayList<CartProduct>());

	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		ImageLoader.getInstance().init(config.build());
	}

	/**
	 * 获取接口服务器地址
	 */
	public static String getBaseurl() {
		return setBaseurl(null);
	}

	/**
	 * 获取图片上传服务器地址
	 */
	public static String getImgUrl() {
		return setImgUrl(null);
	}

	/**
	 * 获取货单图片服务器地址
	 */
	public static String getOrderUrl() {
		return setOrderUrl(null);
	}

	/**
	 * 获取服务商图片服务器地址
	 */
	public static String getServiceUrl() {
		return setServiceUrl(null);
	}

	public static String setBaseurl(String baseUrl) {
		if (baseUrl == null)
			return baseNew == null ? baseurl : baseNew;
		return baseNew = baseUrl;
	}

	public static String setImgUrl(String imgUrl) {
		if (imgUrl == null)
			return imgNew == null ? Imgurl : imgNew;
		return imgNew = imgUrl;
	}

	public static String setOrderUrl(String orderUrl) {
		if (orderUrl == null)
			return orderNew == null ? orderurl : orderNew;
		return orderNew = orderUrl;
	}

	public static String setServiceUrl(String serviceUrl) {
		if (serviceUrl == null)
			return serviceNew == null ? serviceurl : serviceNew;
		return orderNew = serviceUrl;
	}

}
