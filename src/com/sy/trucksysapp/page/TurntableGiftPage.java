package com.sy.trucksysapp.page;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.utils.PreferenceUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TurntableGiftPage extends DroidGap {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.root
				.setBackgroundResource(android.R.drawable.screen_background_light);
		SetupViews();
		super.clearCache();
		super.loadUrl("http://106.3.44.245:10010/Activity.html?memberid="
				+ PreferenceUtils.getInstance(TurntableGiftPage.this)
						.getSettingUserId(), 1000);
//		super.loadUrl("file:///android_asset/www/index.html", 1000);
		super.setIntegerProperty("loadUrlTimeoutValue", 10000);
	}

	private void SetupViews() {
		// 布局方向为垂直方向
		View v = getLayoutInflater().inflate(R.layout.base_webpage, null);
		final ProgressBar progressbar = (ProgressBar) v
				.findViewById(R.id.progressbar);
		TextView title = (TextView) v.findViewById(R.id.topbase_center_text);
		title.setText("惊喜转不停");
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

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					failingUrl = "file:///android_asset/www/timeout.html";
					view.loadUrl(failingUrl);
					// super.onReceivedError(view, errorCode, description,
					// failingUrl);
					progressbar.setVisibility(View.GONE);
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

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					// TODO Auto-generated method stub
					failingUrl = "file:///android_asset/www/timeout.html";
					view.loadUrl(failingUrl);
					// super.onReceivedError(view, errorCode, description,
					// failingUrl);
					progressbar.setVisibility(View.GONE);
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
		this.appView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					finish();
					return true;
				}
				return onKeyDown(keyCode, event);
			}
		});
	}


	public void back(View v) {
		finish();
	}

	public class Refresh extends CordovaPlugin {
		public boolean execute(String action, JSONArray args,
				CallbackContext callbackContext) throws JSONException {
			if (action.equals("refresh")) {
				this.webView.loadUrl(
						"http://106.3.44.245:10010/Activity.html?memberid="
								+ PreferenceUtils.getInstance(
										TurntableGiftPage.this)
										.getSettingUserId(), 1000);
				return true;
			}
			return false;
		}

	}
}
