package com.sy.trucksysapp.widget;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.sy.trucksysapp.utils.PreferenceUtils;

public class Refresh  extends CordovaPlugin {
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("refresh")) {
			this.webView.loadUrl(
					"http://106.3.44.245:10011/Activity.html?memberid="
							+ PreferenceUtils.getInstance(
									cordova.getActivity())
									.getSettingUserId(), 1000);
			return true;
		}
		return false;
	}

}