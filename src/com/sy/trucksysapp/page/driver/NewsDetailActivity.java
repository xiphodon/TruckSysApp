package com.sy.trucksysapp.page.driver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.R.drawable;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.FollowService;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.JsWebViewClient;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

public class NewsDetailActivity extends BaseActivity {
	private HashMap<String, String> map;
	private DisplayImageOptions options;
	private DisplayImageOptions optionsuser;
	private ImageView news_headimg;
	private TextView news_headtype, news_headtitle, news_headdate,
			news_headsummary, news_headcomment_count;
	private WebView news_headcontent;
	private LinearLayout news_headcommentlay;
	private EditText reply_button;
	private InputMethodManager manager;
	private LoadingFrameLayout loading;
	private String id;

	private ImageView follow;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail);
		follow = (ImageView) findViewById(R.id.follow);
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.show("页面加载中...");
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("资讯");
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		optionsuser = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.user_login_default_avatar)
				.showImageForEmptyUri(R.drawable.user_login_default_avatar)
				.showImageOnFail(R.drawable.user_login_default_avatar)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		try {
			map = (HashMap<String, String>) getIntent().getSerializableExtra(
					"rowdata");
			id = getIntent().getStringExtra("id");
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (map != null) {
			initview();
		} else {
			// 获取新闻数据
			executenewsdetail(id);
		}
	}

	private void executenewsdetail(String id) {
		// TODO Auto-generated method stub
		String url = SystemApplication.getBaseurl()
				+ "TruckService/GetNewDetail";
		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(10000);
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("id", id + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						map = CommonUtils.getMap(json.getJSONObject("Obj"));
						initview();
					} else {
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				loading.dismiss();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				loading.dismiss();
			}
		});
	}

	@SuppressLint("JavascriptInterface")
	private void initview() {
		// TODO Auto-generated method stub
		if (!CommonUtils.getString(map, "Favo_CreateDate").equals("")) {
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_followed));
		} else
			follow.setImageDrawable(getResources().getDrawable(
					drawable.ic_follow));
		follow.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				FollowService.follow(NewsDetailActivity.this, map, "News_Id",
						"News", follow);
			}
		});
		news_headimg = (ImageView) findViewById(R.id.news_headimg);
		ImageLoader.getInstance().displayImage(
				SystemApplication.getImgUrl() + map.get("News_Pic") + "",
				news_headimg, options);
		final String img_url = SystemApplication.getImgUrl()
				+ map.get("News_Pic");
		news_headimg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NewsDetailActivity.this,
						ImagePagerActivity.class);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
						new String[] { img_url });
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
				NewsDetailActivity.this.startActivity(intent);
			}
		});
		TextView news_headtype = (TextView) findViewById(R.id.news_headtype);
		news_headtype.setText(map.get("Cate_Name") + "");
		TextView news_headtitle = (TextView) findViewById(R.id.news_headtitle);
		news_headtitle.setText(map.get("News_Source") + "");
		TextView news_headdate = (TextView) findViewById(R.id.news_headdate);
		news_headdate.setText(TextUtils.FormatDatestr(map
				.get("News_CreateDate") + ""));
		TextView news_headsummary = (TextView) findViewById(R.id.news_headsummary);
		news_headsummary.setText(map.get("News_Summary") + "");
		news_headcontent = (WebView) findViewById(R.id.news_headcontent);
		news_headcontent.getSettings().setDefaultTextEncodingName("UTF-8");// 设置默认为utf-8
		news_headcontent.getSettings().setJavaScriptEnabled(true);
		news_headcontent.loadData(
				CommonUtils.formatHtmlString(map.get("News_Content") + ""),
				"text/html; charset=UTF-8", null);// 这种写法可以正确解码
		news_headcontent.setWebViewClient(new JsWebViewClient(news_headcontent,
				NewsDetailActivity.this));
		reply_button = (EditText) findViewById(R.id.reply_button);
		news_headcommentlay = (LinearLayout) findViewById(R.id.news_headcommentlay);
		news_headcomment_count = (TextView) findViewById(R.id.news_headcomment_count);
		news_headcomment_count.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				hideKeyboard();
				// 添加评论内容
				if (!reply_button.getText().toString().equals("")) {
					
					String msgStr = null;
					try {
						msgStr = URLEncoder.encode(reply_button.getText().toString(), "UTF-8");
						msgStr = msgStr.replaceAll("\\+", "%20");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String url = SystemApplication.getInstance().getBaseurl()
							+ "truckservice/SaveNewsComment";
					AsyncHttpClient client = new AsyncHttpClient();
					RequestParams RequestParams = new RequestParams();
					RequestParams.put("NeCo_Content", msgStr);
					RequestParams.put("NeCo_NewsId", map.get("News_Id") + "");
					RequestParams.put("NeCo_MemberId", PreferenceUtils
							.getInstance(NewsDetailActivity.this)
							.getSettingUserId());
					client.post(url, RequestParams,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(String arg0) {
									// TODO Auto-generated method stub
									ExecuteData();

									Toast.makeText(NewsDetailActivity.this,
											"保存成功！", 1000).show();
									reply_button.setText("");
									super.onSuccess(arg0);
								}

								@Override
								public void onFailure(Throwable arg0,
										String arg1) {
									// TODO Auto-generated method stub
									super.onFailure(arg0, arg1);
									Toast.makeText(NewsDetailActivity.this,
											"保存失败，请重试！", 1000).show();
								}
							});
				} else {
					Toast.makeText(NewsDetailActivity.this, "请输入评论内容！", 1000).show();
				}
			}
		});
		loading.dismiss();
		ExecuteData();
	}

	public void finish() {
		if (FollowService.isFollowed()) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("rowdata", map);
			intent.putExtras(bundle);
			setResult(100, intent);
		}
		super.finish();
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void ExecuteData() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "truckservice/GetAllComment";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("id", map.get("News_Id") + "");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				try {
					news_headcommentlay.removeViews(1,
							news_headcommentlay.getChildCount() - 1);
					JSONObject json = new JSONObject(result);
					JSONArray array = json.getJSONArray("rows");
					if (array.length() > 0) {
						for (int i = 0; i < array.length(); i++) {
							JSONObject item = array.getJSONObject(i);
							View v = getLayoutInflater().inflate(
									R.layout.news_reply_item, null);
							ImageView news_headcomment_personimg = (ImageView) v
									.findViewById(R.id.news_headcomment_personimg);
							ImageLoader.getInstance().displayImage(
									SystemApplication
											.getInstance()
											.getBaseurl()
											.substring(
													0,
													SystemApplication
															.getInstance()
															.getBaseurl()
															.length() - 1)
											+ item.getString("Memb_HeadPic"),
									news_headcomment_personimg, optionsuser);
							TextView news_headcomment_person = (TextView) v
									.findViewById(R.id.news_headcomment_person);

							if (TextUtils.FormatStr(
									item.getString("Memb_Account")).equals("")) {
								news_headcomment_person.setText("游客");
							} else {
								// 设置隐藏评论的用户手机号
								if (CommonUtils.isPhone(item
										.getString("Memb_Account"))) {
									String phone = item.getString(
											"Memb_Account").substring(0, 3)
											+ "****"
											+ item.getString("Memb_Account")
													.substring(
															7,
															item.getString(
																	"Memb_Account")
																	.length());
									news_headcomment_person.setText(phone);
								} else {
									news_headcomment_person.setText(item
											.getString("Memb_Account"));
								}

							}
							TextView news_headcomment_date = (TextView) v
									.findViewById(R.id.news_headcomment_date);
							news_headcomment_date.setText(TextUtils
									.FormatDatestr(item
											.getString("NeCo_CreateDate")));
							final String commentid = item.getString("NeCo_Id");
							final TextView news_headcomment_prasecount = (TextView) v
									.findViewById(R.id.news_headcomment_prasecount);
							news_headcomment_count.setText(array.length() + "");
							news_headcomment_prasecount
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub

										}
									});
							// 评论数
							news_headcomment_prasecount.setText("");
							TextView news_headcomment_content = (TextView) v
									.findViewById(R.id.news_headcomment_content);
							String str = URLDecoder.decode(item.getString("NeCo_Content"));
							news_headcomment_content.setText(str);
							View vline = getLayoutInflater().inflate(
									R.layout.new_comment_line, null);
							news_headcommentlay.addView(v);
							news_headcommentlay.addView(vline);
							news_headcommentlay.postInvalidate();
						}
					} else {
                         TextView no_data = new TextView(NewsDetailActivity.this);
                         no_data.setBackgroundColor(getResources().getColor(R.color.white));
                         no_data.setText("暂无用户评价");
                         no_data.setGravity(Gravity.CENTER_HORIZONTAL);
                         news_headcommentlay.addView(no_data);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(NewsDetailActivity.this, "获取评论失败！", 1000)
							.show();
				}
				super.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(NewsDetailActivity.this, "获取评论失败！", 1000).show();
				super.onFailure(arg0, arg1);
			}
		});
	}

}
