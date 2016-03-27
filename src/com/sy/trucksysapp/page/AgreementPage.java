package com.sy.trucksysapp.page;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import com.sy.trucksysapp.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AgreementPage extends DroidGap {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.root
				.setBackgroundResource(android.R.drawable.screen_background_light);
		SetupViews();
		super.loadUrl("file:///android_asset/www/agreement.html",1000);
	}

	private void SetupViews() {
		// 布局方向为垂直方向
		View v = getLayoutInflater().inflate(R.layout.base_webpage, null);
		final ProgressBar progressbar = (ProgressBar) v.findViewById(R.id.progressbar);
		TextView title = (TextView) v.findViewById(R.id.topbase_center_text);
		title.setText("卡车团平台用户协议");
		LinearLayout content = (LinearLayout) v.findViewById(R.id.content);
		CordovaWebView webView = new CordovaWebView(this);
		CordovaWebViewClient webViewClient;
		if (android.os.Build.VERSION.SDK_INT < 11) {
			webViewClient = new CordovaWebViewClient(this, webView) {
				@Override
				public void onPageFinished(WebView arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onPageFinished(arg0, arg1);
					appView.setVisibility(View.VISIBLE);
				}
			};
		} else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView) {
				@Override
				public void onPageFinished(WebView arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onPageFinished(arg0, arg1);
					appView.setVisibility(View.VISIBLE);
				}
			};
		}
		// Set up web container
		this.appView = webView;
		this.appView.setId(100);
		CordovaChromeClient webChromeClient = new CordovaChromeClient(this,
				webView) {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				if (newProgress == 100) {
					progressbar.setVisibility(View.GONE);
				} else {
					if (progressbar.getVisibility() == View.GONE)
						progressbar.setVisibility(View.VISIBLE);
					progressbar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		};
		this.appView.setWebViewClient(webViewClient);
		this.appView.setWebChromeClient(webChromeClient);
		webViewClient.setWebView(this.appView);
		webChromeClient.setWebView(this.appView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		this.appView.setLayoutParams(params); // Add web view but make it
		this.appView.setVisibility(View.VISIBLE);
		content.addView(this.appView);
		this.appView.setVisibility(View.INVISIBLE);
		this.root.addView(v);
		setContentView(this.root);
		this.cancelLoadUrl = false;
	}

	public void back(View v) {
		finish();
	}
}
