package com.sy.trucksysapp.page;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.user.LoginActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

public class FollowService {

	private static String favoDate = "Favo_CreateDate";
	private static Boolean isFollowed = false;
	private static String unLoginMsg = "请先登录再使用收藏功能";

	/**
	 * 是否成功触发"收藏"事件
	 * 
	 * @return
	 */
	public static Boolean isFollowed() {
		if (isFollowed) {
			isFollowed = false;
			return true;
		} else
			return isFollowed;
	}

	/**
	 * 收藏-通用方法
	 * 
	 * @param context
	 *            上下文
	 * @param map
	 *            待操作数据集
	 * @param key
	 *            主键ID
	 * @param fType
	 *            收藏类型
	 * @param follow
	 *            收藏按钮控件
	 */
	public static void follow(final Context context,
			final HashMap<String, String> map, String key, String fType,
			final ImageView follow) {
		String MemberId = PreferenceUtils.getInstance(context)
				.getSettingUserId();
		if (MemberId == null || MemberId.equals("")) {
			Activity  a  = (Activity) context;
			a.startActivityForResult(new Intent(context, LoginActivity.class), 888);
			return;
		}
		final String type = CommonUtils.getString(map, favoDate).equals("") ? "0"
				: "1";
		String url = SystemApplication.getBaseurl() + "TruckService/Favorite";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("Favo_MemberId", MemberId);
		RequestParams.put("Favo_Type", fType);
		RequestParams.put("Favo_FavoriteId", map.get(key));
		RequestParams.put("type", type);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						isFollowed = true;
						if (type.equals("1")) {
							follow.setImageDrawable(context.getResources()
									.getDrawable(drawable.ic_follow));
							CommonUtils.showToast(context, "取消收藏成功!");
							map.put(favoDate, "");
						} else {
							follow.setImageDrawable(context.getResources()
									.getDrawable(drawable.ic_followed));
							HashMap<String, String> map2 = CommonUtils
									.getMap(jsobj.getJSONObject("Obj"));
							map.put(favoDate, map2.get(favoDate));
							CommonUtils.showToast(context, "收藏成功!");
						}
					} else {
						if (type.equals("1")) {
							CommonUtils.showToast(context, "取消收藏失败!");
						} else {
							CommonUtils.showToast(context, "收藏失败!");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	/**
	 * 收藏-通用方法
	 * 
	 * @param context
	 *            上下文
	 * @param map
	 *            待操作数据集
	 * @param key
	 *            主键值
	 * @param fType
	 *            收藏类型
	 * @param follow
	 *            收藏按钮控件
	 */
	public static void follow(final Context context,
			final HashMap<String, String> map, Object key, String fType,
			final ImageView follow) {
		String MemberId = PreferenceUtils.getInstance(context)
				.getSettingUserId();
		if (MemberId == null || MemberId.equals("")) {
			Activity  a  = (Activity) context;
			a.startActivityForResult(new Intent(context, LoginActivity.class), 888);
			return;
		}
		final String type = CommonUtils.getString(map, favoDate).equals("") ? "0"
				: "1";
		String url = SystemApplication.getBaseurl() + "TruckService/Favorite";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("Favo_MemberId", MemberId);
		RequestParams.put("Favo_Type", fType);
		RequestParams.put("Favo_FavoriteId", key + "");
		RequestParams.put("type", type);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				super.onSuccess(result);
				try {
					JSONObject jsobj = new JSONObject(result);
					if (jsobj.getBoolean("State")) {
						isFollowed = true;
						if (type.equals("1")) {
							follow.setImageDrawable(context.getResources()
									.getDrawable(drawable.ic_follow));
							CommonUtils.showToast(context, "取消收藏成功!");
							map.put(favoDate, "");
						} else {
							follow.setImageDrawable(context.getResources()
									.getDrawable(drawable.ic_followed));
							HashMap<String, String> map2 = CommonUtils
									.getMap(jsobj.getJSONObject("Obj"));
							map.put(favoDate, map2.get(favoDate));
							CommonUtils.showToast(context, "收藏成功!");
						}
					} else {
						if (type.equals("1")) {
							CommonUtils.showToast(context, "取消收藏失败!");
						} else {
							CommonUtils.showToast(context, "收藏失败!");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}
}
