package com.sy.trucksysapp.user;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
/**
 * 联系我们
 * @author lxs 20150616
 *
 */
public class AboutActivity  extends DroidGap  {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.root
				.setBackgroundResource(android.R.drawable.screen_background_light);
		SetupViews();
		super.loadUrl("file:///android_asset/www/contact.html", 1000);
	}

	private void SetupViews() {
		// 布局方向为垂直方向
		View v = getLayoutInflater().inflate(R.layout.base_webpage, null);
		final ProgressBar progressbar = (ProgressBar) v.findViewById(R.id.progressbar);
		TextView title = (TextView) v.findViewById(R.id.topbase_center_text);
		title.setText("联系我们");
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
