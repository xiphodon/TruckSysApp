package com.sy.trucksysapp.widget;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.sy.trucksysapp.utils.PreferenceUtils;

public class Finish  extends CordovaPlugin {
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if (action.equals("finish")) {
			cordova.getActivity().finish();
			return true;
		}
		return false;
	}

}