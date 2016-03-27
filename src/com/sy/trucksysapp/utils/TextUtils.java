package com.sy.trucksysapp.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextUtils {
	public static String FormatStr(String str) {
		if (str.equals("null") || str.equals("")) {
			return "";
		}
		return str;
	}

	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}
				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static int ParseInt(String st) {
		int i;
		if (st.equals("") || st.equals("null") || st == null) {
			i = 0;
		} else {
			try {
				i = Integer.valueOf(st);
			} catch (Exception ex) {
				ex.printStackTrace();
				i = 0;
			}
		}
		return i;
	}

	public static String FormatDatestr(String date) {
		if (date.equals("null") || date.equals("")) {
			return "";
		} else {
			Date dNow = new Date(); // 当前时间
			Date dBefore = new Date();
			Calendar calendar = Calendar.getInstance(); // 得到日历
			calendar.setTime(dNow);// 把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
			dBefore = calendar.getTime(); // 得到前一天的时间
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			Date d = null;
			try {
				d = formatDate.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			if (formatDate.format(dNow).equals(formatDate.format(d))) {
				return "今天";
			} else if (formatDate.format(dBefore).equals(formatDate.format(d))) {
				return "昨天";
			} else {
				return formatDate.format(d);
			}

		}
	}

	public static String FormatDateTOyyyyMMddHHsss(String date) {
		if (date.equals("null") || date.equals("")) {
			return "";
		} else {
			SimpleDateFormat formatDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:ss");
			Date d = null;
			try {
				d = formatDate.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			return formatDate.format(d);

		}
	}

	/**
	 * 格式化距离的显示
	 */
	public static String FormatDistincestr(String distince) {
		DecimalFormat decimalFormat = new DecimalFormat(".00");

		if (distince.equals("null") || distince.equals("")) {
			return "";
		} else {
			Double dis = Double.valueOf(distince);
			if (dis > 1) {
				return decimalFormat.format(dis) + " km";
			} else if (0 < dis && dis < 1) {
				return decimalFormat.format(dis * 1000) + " m";
			} else {
				return "";
			}
		}
	}

	/**
	 * 格式化评分
	 * 
	 * @param value
	 *            参数
	 * @return
	 */
	public static String FormatStar(Object value) {
		DecimalFormat df = new DecimalFormat("0.00");
		value = FormatStr(value + "");
		if (value.equals(""))
			value = "0";
		return df.format(Double.valueOf(value + ""));
	}
}
