package com.sy.trucksysapp.page.driver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.LoacationChangeListener;
import com.sy.trucksysapp.page.CurrentLocationActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

public class WeatherActivity extends CurrentLocationActivity implements
		LoacationChangeListener {

	private LoadingFrameLayout loading;
	private String addressName;
	private WebView webview;
	private String murl = "";
	private long timeout = 5000;
	private Handler mHandler;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("今日天气");
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		setLoacationChangeListener(this);
		mHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1){
					CommonUtils.showToast(WeatherActivity.this, "加载超时");
					webview.stopLoading();
					loading.dismiss();
				}
			}
		};
		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setUseWideViewPort(false);// 设置此属性，可任意比例缩放
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setLoadsImagesAutomatically(true);
		// String lasturl="";
		// webview.loadUrl("http://m.weather.com.cn/mweather/101180101.shtml");
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				if (url.equals(murl)) {
					loading.show("正在获取天气信息。。。");
					timer = new Timer();
					TimerTask tt = new TimerTask() {
						@Override
						public void run() {
							/*
							 * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
							 */
							if (WeatherActivity.this.webview.getProgress() < 100) {
								Log.d("testTimeout", "timeout...........");
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
								timer.cancel();
								timer.purge();
							}
						}
					};
					timer.schedule(tt, timeout, 1);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				if (failingUrl.equals(murl)) {
					CommonUtils.showToast(WeatherActivity.this, description
							+ "获取天气信息失败！");
					loading.dismiss();
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				if (url.equals(murl)) {
					loading.dismiss();
					timer.cancel();
					timer.purge();
				}
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url); // 在当前的webview中跳转到新的url
				return true;
			}
		});
	}

	public String getCityCode(String str) {
		String code = "";
		try {
			InputStream is = getAssets().open("file/weather.txt");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string.
			String text = new String(buffer, "GB2312");
			Map<String, String> map = new HashMap<String, String>();
			String[] array = text.split("\n");
			for (int i = 0; i < array.length; i++) {
				String[] itemarray = array[i].split("\\=");
				if (itemarray.length == 2) {
					map.put(itemarray[1].trim(), itemarray[0].trim());
				}
			}
			Set<String> set = map.keySet();
			for (Iterator iterator = set.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				if (str.contains(string)) {
					code = map.get(string);
					break;
				}
			}
			// Finally stick the string into the text view.
		} catch (IOException e) {
			// Should never happen!
			throw new RuntimeException(e);
		}
		return code;
	}

	@Override
	public void onChange(AMapLocation location) {
		// TODO Auto-generated method stub
		// 获取当前所在城市
		murl = "http://m.weather.com.cn/mweather/"
				+ getCityCode(location.getCity()) + ".shtml";
		webview.loadUrl(murl);
		loading.dismiss();
	}

	@Override
	public void onFailChange() {
		// TODO Auto-generated method stub
		loading.dismiss();
		CommonUtils.showToast(WeatherActivity.this, "获取位置信息失败！");
	}

}
