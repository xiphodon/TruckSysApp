package com.sy.trucksysapp.page;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.shoping.adapter.UserEvaluationAdapter;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.TextUtils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class CommentService {
	private static int rows = 10;
	private static int page = 1;
	private static int total = 0;
	private static String k = "";
	private static Double avgStar = 0.0d;

	/**
	 * 查询评价列表-通用
	 * 
	 * @param type
	 *            查询类型 down:按最后评价时间,up:分页查询
	 * @param context
	 *            上下文
	 * @param adapter
	 *            待填充适配器
	 * @param list
	 *            集合
	 * @param listKey
	 *            主键集合,防止重复
	 * @param key
	 *            主键ID
	 * @param gType
	 *            评价类型
	 * @param initType
	 *            加载类型, 0:初始化 1:正常操作
	 * @param tv_prase_more
	 *            "加载"控件
	 * @param tv_prase_lable
	 *            "暂无"控件
	 * @param li_parse_content
	 *            "评价"载体
	 * @param scroll
	 *            滚动控件
	 */
	public static void getData(final String type, final Context context,
			final UserEvaluationAdapter adapter,
			final ArrayList<CommentModel> list,
			final ArrayList<String> listKey, String key, String gType,
			final int initType, final TextView tv_prase_more,
			final TextView tv_prase_lable, final LinearLayout li_parse_content,
			final ScrollView scroll, final RatingBar rating,
			final TextView ratingt) {
		avgStar = Double.parseDouble(ratingt.getText().toString());
		if (type.equals("down")) {
			if (adapter.getLastDate().equals("")) {
				rows = 10;
			} else {
				rows = -1;
			}
		} else if (type.equals("up")) {
			rows = 10;
			if (total != 0) {
				page = ((int) Math.floor(list.size() / rows)) + 1;
			} else
				page = 1;
		}
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetGoodsComment";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", rows + "");
		RequestParams.put("currPage", page + "");
		RequestParams.put("GoCo_GoodId", key + "");
		if (type.equals("down"))
			RequestParams.put("lastDate", adapter.getLastDate());
		RequestParams.put("GoCo_GoodType", gType);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					avgStar = jsobj.getDouble("obj");
					rating.setRating(avgStar.floatValue());
					ratingt.setText(TextUtils.FormatStar(avgStar));
					total = jsobj.getInt("total");
					int size = list.size();
					if (total != 0) {
						ArrayList<CommentModel> lists = new ArrayList<CommentModel>();
						JSONArray row = jsobj.getJSONArray("rows");
						for (int i = 0, j = row.length(); i < j; i++) {
							JSONObject obj = row.getJSONObject(i);
							CommentModel mo = new CommentModel();
							mo.setCommentcontent(obj.getString("GoCo_Content"));
							mo.setDatestr(obj.getString("GoCo_CreateDate"));
							mo.setImgurl(obj.getString("GoCo_Pic"));
							mo.setPersonname(obj.getString("GoCo_Name"));
							mo.setStarnum(obj.getInt("GoCo_Star"));
							String key = obj.getString("GoCo_Id");
							if (!listKey.contains(key)) {
								lists.add(mo);
								listKey.add(key);
							} else {
								list.set(listKey.indexOf(key), mo);
							}
						}
						if (type.equals("down")) {
							total = size + lists.size();
							size = 0;
						}
						if (lists.size() > 0)
							list.addAll(size, lists);
						if (adapter != null)
							adapter.notifyDataSetChanged(list);
					}
					if (total == list.size()) {
						tv_prase_more.setVisibility(View.GONE);
						if (total == 0) {
							li_parse_content.setVisibility(View.GONE);
							tv_prase_lable.setVisibility(View.VISIBLE);
						} else {
							tv_prase_lable.setVisibility(View.GONE);
							li_parse_content.setVisibility(View.VISIBLE);
						}
					} else {
						tv_prase_lable.setVisibility(View.GONE);
						tv_prase_more.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	/**
	 * 查询评价列表-通用
	 * 
	 * @param type
	 *            查询类型 down:按最后评价时间,up:分页查询
	 * @param context
	 *            上下文
	 * @param adapter
	 *            待填充适配器
	 * @param list
	 *            集合
	 * @param listKey
	 *            主键集合,防止重复
	 * @param key
	 *            主键ID
	 * @param gType
	 *            评价类型
	 * @param initType
	 *            加载类型, 0:初始化 1:正常操作
	 * @param tv_prase_more
	 *            "加载"控件
	 * @param tv_prase_lable
	 *            "暂无"控件
	 * @param li_parse_content
	 *            "评价"载体
	 * @param scroll
	 *            滚动控件
	 */
	public static void getData(final String type, final Context context,
			final UserEvaluationAdapter adapter,
			final ArrayList<CommentModel> list,
			final ArrayList<String> listKey, String key, String gType,
			final int initType, final TextView tv_prase_more,
			final TextView tv_prase_lable, final LinearLayout li_parse_content,
			final ScrollView scroll, final java.util.HashMap<String, String> map) {
		k = "StartCount";
		if (gType.equals("FsShop") || gType.equals("Garage")
				|| gType.equals("ReCompany"))
			k = "Star";
		avgStar = Double.parseDouble(map.get(k) == null ? "0" : map.get(k));
		if (type.equals("down")) {
			if (adapter.getLastDate().equals("")) {
				rows = 10;
			} else {
				rows = -1;
			}
		} else if (type.equals("up")) {
			rows = 10;
			if (total != 0) {
				page = ((int) Math.floor(list.size() / rows)) + 1;
			} else
				page = 1;
		}
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetGoodsComment";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("pageSize", rows + "");
		RequestParams.put("currPage", page + "");
		RequestParams.put("GoCo_GoodId", key + "");
		if (type.equals("down"))
			RequestParams.put("lastDate", adapter.getLastDate());
		RequestParams.put("GoCo_GoodType", gType);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsobj = new JSONObject(result);
					avgStar = jsobj.getDouble("obj");
					map.put(k, TextUtils.FormatStar(avgStar));
					total = jsobj.getInt("total");
					int size = list.size();
					if (total != 0) {
						ArrayList<CommentModel> lists = new ArrayList<CommentModel>();
						JSONArray row = jsobj.getJSONArray("rows");
						for (int i = 0, j = row.length(); i < j; i++) {
							JSONObject obj = row.getJSONObject(i);
							CommentModel mo = new CommentModel();
							mo.setCommentcontent(obj.getString("GoCo_Content"));
							mo.setDatestr(obj.getString("GoCo_CreateDate"));
							mo.setImgurl(obj.getString("GoCo_Pic"));
							mo.setPersonname(obj.getString("GoCo_Name"));
							mo.setStarnum(obj.getInt("GoCo_Star"));
							String key = obj.getString("GoCo_Id");
							if (!listKey.contains(key)) {
								lists.add(mo);
								listKey.add(key);
							} else {
								list.set(listKey.indexOf(key), mo);
							}
						}
						if (type.equals("down")) {
							total = size + lists.size();
							size = 0;
						}
						if (lists.size() > 0)
							list.addAll(size, lists);
						if (adapter != null)
							adapter.notifyDataSetChanged(list);
					}
					if (total == list.size()) {
						tv_prase_more.setVisibility(View.GONE);
						if (total == 0) {
							li_parse_content.setVisibility(View.GONE);
							tv_prase_lable.setVisibility(View.VISIBLE);
						} else {
							tv_prase_lable.setVisibility(View.GONE);
							li_parse_content.setVisibility(View.VISIBLE);
						}
					} else {
						tv_prase_lable.setVisibility(View.GONE);
						tv_prase_more.setVisibility(View.VISIBLE);
					}
					if (initType == 0&&scroll!=null)
							new Handler().post(new Runnable() {
								public void run() {
									try {
										scroll.scrollTo(0, 0);
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("error", e.getMessage());
				}
			}

			public void onFailure(Throwable arg0, String arg1) {
				CommonUtils.onFailure(arg0, arg1, context);
				super.onFailure(arg0, arg1);
			}
		});
	}

	/**
	 * 获取评分
	 * 
	 * @return
	 */
	public static String getAvgStar() {
		return avgStar + "";
	}
}
