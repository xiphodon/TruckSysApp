package com.sy.trucksysapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MactchUtil {
	static String regEx = "^([\u4E00-\u9FA5])*$";
	static Pattern pat = Pattern.compile(regEx);
	
	static String sregEx = "^([\u4E00-\u9FA5]|\\W)*$";
	static Pattern spat = Pattern.compile(sregEx);

	public static boolean isContainsChinese(String str) {
		Matcher matcher = pat.matcher(str);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}
	public static boolean isContainsspecialn(String str) {
		Matcher matcher = spat.matcher(str);
		boolean flg = false;
		if (matcher.find()) {
			flg = true;
		}
		return flg;
	}
	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/***
	 * 验证电话号码
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean CheckPhoneNumber(String phoneNumber) {
		boolean isValid = false;
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
				+ "(^0[3-9] {1}d{2}-?d{7,8}$)|"
				+ "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
				+ "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * 棿ߥURL昿否合泿
	 * 
	 * @param value
	 * @return
	 */
	public static boolean checkURL(String value) {
		String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern patt = Pattern.compile(regex);
		Matcher matcher = patt.matcher(value);
		return matcher.matches();
	}

	/**
	 * 棿ߥ身份证是 否合泿15位或18使
	 * 
	 * @param value
	 * @return
	 */
	public static boolean checkIDCard(String value) {
		String regx = "([0-9]{17}([0-9]|X))|([0-9]{15})";
		return value.matches(regx);
	}

}
