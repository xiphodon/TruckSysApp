/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sy.trucksysapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceUtils {

	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private String SHARED_KEY_USERNAME = "shared_key_username";
	private String SHARED_KEY_PASSWORD = "shared_key_password";
	private String SHARED_KEY_USERID = "shared_key_userid";
	private String SHARED_KEY_USERNICK = "shared_key_usernick";
	private String SHARED_KEY_USERHEADPIC = "shared_key_userheadpic";
	private String SHARED_KEY_USERTRUENAME = "shared_key_usertruename";
	private String SHARED_KEY_USERSEX = "shared_key_usersex";
	private String SHARED_KEY_USERMOBILE = "shared_key_usermoblie";
	private String SHARED_KEY_USERAREA = "shared_key_userarea";
	private String SHARED_KEY_SHOPBAG = "shared_key_shopbag";

	private PreferenceUtils(Context cxt) {
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * 单例模式，获取instance实例
	 * 
	 * @param cxt
	 * @return
	 */
	public static PreferenceUtils getInstance(Context cxt) {
		if (mPreferenceUtils == null) {
			mPreferenceUtils = new PreferenceUtils(cxt);
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}

	/**
	 * 8 获取与存储用户信息
	 * 
	 * @param username
	 */
	public void setSettingUserName(String username) {
		editor.putString(SHARED_KEY_USERNAME, username);
		editor.commit();
	}

	public String getSettingUserName() {
		return mSharedPreferences.getString(SHARED_KEY_USERNAME, "");
	}

	public void setSettingUserPassword(String password) {
		if (password != null && !password.equals("null")) {
			editor.putString(SHARED_KEY_PASSWORD, password);
			editor.commit();
		}
	}

	public String getSettingUserPassword() {
		return mSharedPreferences.getString(SHARED_KEY_PASSWORD, "");
	}

	public void setSettingUserId(String userid) {
		if (userid != null && !userid.equals("null")) {
			editor.putString(SHARED_KEY_USERID, userid);
			editor.commit();
		}
	}

	public String getSettingUserId() {
		return TextUtils.FormatStr(mSharedPreferences.getString(
				SHARED_KEY_USERID, "") + "");
	}

	public void setSettingUserNick(String nick) {
		
		if (nick != null && !nick.equals("null")) {
			editor.putString(SHARED_KEY_USERNICK, nick);
			editor.commit();
		}else if(nick.equals("null")||(nick.equals(""))){
			editor.putString(SHARED_KEY_USERNICK, "");
			editor.commit();
		}
	}

	public String getSettingUserNick() {
		return mSharedPreferences.getString(SHARED_KEY_USERNICK, "未设置昵称");
	}

	public void setSettingHeadImg(String headimg) {
		editor.putString(SHARED_KEY_USERHEADPIC, headimg);
		editor.commit();
	}

	public String getSettingHeadImg() {
		return mSharedPreferences.getString(SHARED_KEY_USERHEADPIC, "") + "";
	}

	public void setSettingTrueName(String truename) {
		if (truename != null && !truename.equals("null")) {
			editor.putString(SHARED_KEY_USERTRUENAME, truename);
			editor.commit();
		}else if(truename.equals("null")){
			editor.putString(SHARED_KEY_USERTRUENAME, "");
			editor.commit();
		}
	}

	public String getSettingTrueName() {
		return mSharedPreferences.getString(SHARED_KEY_USERTRUENAME, "");
	}

	public void setSettingSex(String sex) {
		if (sex != null && !sex.equals("null")) {
			editor.putString(SHARED_KEY_USERSEX, sex);
			editor.commit();
		}else if(sex.equals("null")){
			editor.putString(SHARED_KEY_USERSEX, "");
			editor.commit();
		}
	}

	public String getSettingSex() {
		return mSharedPreferences.getString(SHARED_KEY_USERSEX, "");
	}

	public void setSettingMobile(String mobile) {
		if (mobile != null && !mobile.equals("null")) {
			editor.putString(SHARED_KEY_USERMOBILE, mobile);
			editor.commit();
		}
	}

	public String getSettingMobile() {
		return mSharedPreferences.getString(SHARED_KEY_USERMOBILE, "");
	}

	public void setSettingArea(String area) {
		if (area != null && !area.equals("null")) {
			editor.putString(SHARED_KEY_USERAREA, area);
			editor.commit();
		}

	}

	public String getSettingArea() {
		return mSharedPreferences.getString(SHARED_KEY_USERAREA, "");
	}

	public void setSettingShopbag(String shopbag) {
		if (shopbag != null && !shopbag.equals("null")) {
			editor.putString(SHARED_KEY_SHOPBAG, shopbag);
			editor.commit();
		}

	}

	public String getShopbag() {
		return mSharedPreferences.getString(SHARED_KEY_SHOPBAG, "");
	}

	/**
	 * 退出登录清除全部
	 */
	public void ClearAll() {
		setSettingArea("");
		setSettingMobile("");
		setSettingSex("");
		setSettingTrueName("");
		setSettingHeadImg("");
		setSettingUserNick("");
		setSettingUserId("");
		setSettingUserPassword("");
		setSettingUserName("");
	}
}
