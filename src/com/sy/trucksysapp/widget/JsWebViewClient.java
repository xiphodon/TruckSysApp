package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.photoview.ImagePagerActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JsWebViewClient extends WebViewClient {

	private WebView webview;

	@SuppressLint("JavascriptInterface")
	public JsWebViewClient(WebView webview,Context context) {
		super();
		this.webview = webview;
		webview.addJavascriptInterface(new JavascriptInterface(context),
				"imagelistener");
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		return super.shouldOverrideUrlLoading(view, url);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		// TODO Auto-generated method stub
		view.getSettings().setJavaScriptEnabled(true);
		super.onPageFinished(view, url);
		addImageClickListener();
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		view.getSettings().setJavaScriptEnabled(true);
		super.onPageStarted(view, url, favicon);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		// TODO Auto-generated method stub
		super.onReceivedError(view, errorCode, description, failingUrl);
	}

	// 注入js函数监听

	private void addImageClickListener() {
		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
		webview.loadUrl("javascript:(function(){"
				+ "var objs = document.getElementsByTagName(\"img\"); "
				+ "for(var i=0;i<objs.length;i++)" + "{"
				+ "objs[i].onclick=function()" + "{"
				+ "window.imagelistener.openImage(this.src);" + "}" + "}"
				+ "})()");
	}

	// js通信接口

	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void openImage(String img) {
			Intent intent = new Intent(context, ImagePagerActivity.class);
			// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
					new String[]{img});
			intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, 0);
			context.startActivity(intent);
		}

	}

}
