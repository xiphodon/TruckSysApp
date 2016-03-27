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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.AddressInfo;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.entity.DriverMenuModel;
import com.sy.trucksysapp.page.driver.DriverIndexActivity;
import com.sy.trucksysapp.page.driver.HighwayConditionActivity;
import com.sy.trucksysapp.page.driver.HotelListActivity;
import com.sy.trucksysapp.page.driver.NewsDetailActivity;
import com.sy.trucksysapp.page.driver.NewsmainActivity;
import com.sy.trucksysapp.page.driver.ParkListActivity;
import com.sy.trucksysapp.page.driver.RestaurantListActivity;
import com.sy.trucksysapp.page.driver.WeatherActivity;
import com.sy.trucksysapp.page.shoping.HotSaleDetailActivity;
import com.sy.trucksysapp.page.shoping.SaleDetailActivity;
import com.sy.trucksysapp.widget.ScrollGridView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUtils {

	public static String formatHtmlString(String html) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
		sb.append("<title></title>");
		sb.append("<style type=\"text/css\">");
		sb.append("img, object, embed, video {max-width: 100%;}");
		sb.append("</style>");
		sb.append("</head><body>");
		// sb.append("</head><body style=\"text-align:center;\">");
		sb.append(html);
		sb.append("</body></html>");
		return sb.toString();
	}

	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri)
			return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri,
					new String[] { ImageColumns.DATA }, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	public static void openUrlByType(int index, String[] type, String[] url,
			Context context) {
		// TODO Auto-generated method stub
		if (type[index].equals("0")) {
			if (!url[index].equals("null") && !url[index].equals("")) {
				String regEx = "^(https?|ftp|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
				Pattern p = Pattern.compile(regEx);
				Matcher matcher = p.matcher(url[index]);
				if (matcher.matches()) {
					Intent intent = new Intent();
					Uri content_url = Uri.parse(url[index]);
					intent.setData(content_url);
					intent.setAction(Intent.ACTION_VIEW);
					((Activity) context).startActivity(intent);
				}
			}
			// 特卖
		} else if (type[index].equals("1")) {
			String id = url[index];
			((Activity) context).startActivity(new Intent(context,
					HotSaleDetailActivity.class).putExtra("saleid", id));
			// 轮胎
		} else if (type[index].equals("2")) {
			String id = url[index];
			Intent intent = new Intent(context, SaleDetailActivity.class);
			intent.putExtra("saleid", id);
			intent.putExtra("Type", 1);
			((Activity) context).startActivity(intent);
			// 内胎
		} else if (type[index].equals("3")) {
			String id = url[index];
			Intent intent = new Intent(context, SaleDetailActivity.class);
			intent.putExtra("saleid", id);
			intent.putExtra("Type", 2);
			((Activity) context).startActivity(intent);
			// 润滑油
		} else if (type[index].equals("4")) {
			String id = url[index];
			Intent intent = new Intent(context, SaleDetailActivity.class);
			intent.putExtra("saleid", id);
			intent.putExtra("Type", 4);
			((Activity) context).startActivity(intent);
			// 轮辋
		} else if (type[index].equals("5")) {
			String id = url[index];
			Intent intent = new Intent(context, SaleDetailActivity.class);
			intent.putExtra("saleid", id);
			intent.putExtra("Type", 3);
			((Activity) context).startActivity(intent);
			// 新闻
		} else if (type[index].equals("6")) {
			String id = url[index];
			Intent intent = new Intent(context, NewsDetailActivity.class);
			intent.putExtra("id", id);
			((Activity) context).startActivity(intent);
		} else {

		}

	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	public static void setPopupWindowTouchModal(PopupWindow popupWindow,
			boolean touchModal) {
		if (null == popupWindow) {
			return;
		}
		Method method;
		try {

			method = PopupWindow.class.getDeclaredMethod("setTouchModal",
					boolean.class);
			method.setAccessible(true);
			method.invoke(popupWindow, touchModal);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static List<CartProduct> getCartProductlist(Context context) {
		List<CartProduct> rlist = null;
		File itemfile = null;
		try {
			if (isExitsSdcard()) {
				String SDPATH = Environment.getExternalStorageDirectory()
						.getPath();
				File file = new File(SDPATH + "/卡车团");
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
					itemfile = new File(SDPATH + "/卡车团/shopbag.data");
					itemfile.createNewFile();
					List<CartProduct> list = new ArrayList<CartProduct>();
					ObjectOutputStream out = new ObjectOutputStream(
							new FileOutputStream(SDPATH + "/卡车团/shopbag.data"));
					out.writeObject(list); // 写入customer对象
					out.close();
					rlist = list;
				} else {
					itemfile = new File(SDPATH + "/卡车团/shopbag.data");
					if (!itemfile.exists()) {
						itemfile.createNewFile();
						List<CartProduct> list = new ArrayList<CartProduct>();
						ObjectOutputStream out = new ObjectOutputStream(
								new FileOutputStream(SDPATH
										+ "/卡车团/shopbag.data"));
						out.writeObject(list); // 写入customer对象
						out.close();
						rlist = list;
					} else {
						// 读取
						// 反序列化对象
						ObjectInputStream in = new ObjectInputStream(
								new FileInputStream(SDPATH
										+ "/卡车团/shopbag.data"));
						List<CartProduct> list = (List<CartProduct>) in
								.readObject(); // 读取customer对象
						in.close();
						rlist = list;
					}
				}
			} else {
				showToast(context, "未检测到SD卡!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rlist;
	}

	public static boolean saveCartProductlist(Context context,
			List<CartProduct> list) {
		boolean suc = false;
		File itemfile = null;
		try {
			if (isExitsSdcard()) {
				String SDPATH = Environment.getExternalStorageDirectory()
						.getPath();
				File file = new File(SDPATH + "/卡车团");
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
					itemfile = new File(SDPATH + "/卡车团/shopbag.data");
					itemfile.createNewFile();
				} else {
					itemfile = new File(SDPATH + "/卡车团/shopbag.data");
					if (itemfile.exists()) {
						itemfile.delete();
					}
					itemfile.createNewFile();
				}
				// 序列化对象
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(SDPATH + "/卡车团/shopbag.data"));
				out.writeObject(list); // 写入对象
				out.close();
				suc = true;
			} else {
				showToast(context, "未检测到SD卡!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return suc;

	}

	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return "";
	}

	public static List deepCopy(List src) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(
				byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		List dest = (List) in.readObject();
		return dest;
	}

	/**
	 * 显示加载框
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static Dialog ShowProcess(Context context, String text) {
		final Dialog pd = new Dialog(context, R.style.loading_dialog);
		View v = ((Activity) context).getLayoutInflater().inflate(
				R.layout.loading_progress, null);
		TextView protext = (TextView) v.findViewById(R.id.protext);
		protext.setText(text);
		pd.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		return pd;
	}

	/**
	 * 显示加载框
	 * 
	 * @param context
	 * @param text
	 * @return
	 */
	public static Dialog CreateServiceDialog(Context context) {
		final Dialog pd = new Dialog(context, R.style.loading_dialog);
		View v = ((Activity) context).getLayoutInflater().inflate(
				R.layout.service_dialog, null);
		ImageView iv_close = (ImageView) v.findViewById(R.id.iv_close);
		iv_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pd.cancel();
			}
		});
		TextView protext = (TextView) v.findViewById(R.id.protext);
		protext.setText("安装服务标准");
		TextView provalue = (TextView) v.findViewById(R.id.provalue);
		provalue.setText("1.救援起步价50元（5公里以内，含5公里），每增加1公里，增加10元。\n2.安装轮胎、轮辋、垫带、内胎每次每组30元。\n3.润滑油工时费免费。");
		pd.setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
		return pd;
	}

	/**
	 * 通用Toast显示 lxs 20150504
	 */
	public static void showToast(Context context, String msgTxt) {

		Toast.makeText(context, msgTxt, Toast.LENGTH_SHORT).show();
	}

	/**
	 * lxs 判断是否为正确的手机号
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isPhone(String phone) {
		String strPattern = "^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * 判断是否为固定电话号码
	 * 
	 * @param number
	 */

	public static boolean isFixedPhone(String number) {
		String REGEX_FIXEDPHONE = "^(010|02\\d|0[3-9]\\d{2})?\\d{6,8}$";
		Pattern PATTERN_FIXEDPHONE = Pattern.compile(REGEX_FIXEDPHONE);
		Matcher match = PATTERN_FIXEDPHONE.matcher(number);
		return match.matches();

	}

	/**
	 * 判断是否是合法的密码
	 * 
	 * @param password
	 * @return
	 */
	public static boolean LegalPsw(String password) {
		if (password.matches("[A-Za-z0-9_]+")
				&& (password.length() >= 6 && password.length() <= 12)) {
			return true;
		} else {
			return false;
		}
	}

	// /**
	// * 获取定位信息
	// */
	// public static CurrentLocation getLocation(Context context) {
	// // 获取位置管理服务
	//
	// //CurrentLocation location =new CurrentLocation(context);
	// // Location location = null;
	// // LocationManager locationManager;
	// // String serviceName = Context.LOCATION_SERVICE;
	// // locationManager = (LocationManager) context
	// // .getSystemService(serviceName);
	// // // 查找到服务信息
	// // Criteria criteria = new Criteria();
	// // criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
	// // criteria.setAltitudeRequired(false);
	// // criteria.setBearingRequired(false);
	// // criteria.setCostAllowed(true);
	// // criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
	// // String provider = locationManager.getBestProvider(criteria, true);
	// // if (provider != null) {
	// // location = locationManager.getLastKnownLocation(locationManager
	// // .getBestProvider(criteria, true)); // 通过GPS获取位置
	// // }
	// //
	// return location;
	// }

	public static HashMap<String, String> getMap(JSONObject jsonObject) {
		try {
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			String value;
			HashMap<String, String> valueMap = new HashMap<String, String>();
			while (keyIter.hasNext()) {
				key = (String) keyIter.next();
				value = jsonObject.getString(key);
				if (value.equals("null"))
					value = "";
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void onFailure(Throwable arg0, String arg1, Context context) {
		// 网络异常捕获
		if (arg0 != null
				&& (arg0 instanceof java.net.ConnectException || arg0 instanceof java.net.SocketTimeoutException)) {
			// 请求超时
			if (arg0.getCause() instanceof org.apache.http.conn.ConnectTimeoutException)
				showToast(context, "请求超时");
			// 响应超时
			else if (arg0.getCause() instanceof java.net.SocketTimeoutException)
				showToast(context, "服务器繁忙,请稍后再试");
			return;
		}
		if (("can't resolve host").contains(arg1 + ""))
			showToast(context, "无法解析服务器地址");
	}

	/**
	 * 获取本机手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getLine1Number();
	}

	/**
	 * 获取地址列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<AddressInfo> getAdresslist(Context context, String userid) {
		List<AddressInfo> rlist = null;
		File itemfile = null;
		try {
			if (isExitsSdcard()) {
				String SDPATH = Environment.getExternalStorageDirectory()
						.getPath();
				File file = new File(SDPATH + "/卡车团");
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
					itemfile = new File(SDPATH + "/卡车团/address_" + userid);
					itemfile.createNewFile();
					List<AddressInfo> list = new ArrayList<AddressInfo>();
					ObjectOutputStream out = new ObjectOutputStream(
							new FileOutputStream(SDPATH + "/卡车团/address_"
									+ userid));
					out.writeObject(list); // 写入customer对象
					out.close();
					rlist = list;
				} else {
					itemfile = new File(SDPATH + "/卡车团/address_" + userid);
					if (!itemfile.exists()) {
						itemfile.createNewFile();
						List<AddressInfo> list = new ArrayList<AddressInfo>();
						ObjectOutputStream out = new ObjectOutputStream(
								new FileOutputStream(SDPATH + "/卡车团/address_"
										+ userid));
						out.writeObject(list); // 写入customer对象
						out.close();
						rlist = list;
					} else {
						// 读取
						// 反序列化对象
						ObjectInputStream in = new ObjectInputStream(
								new FileInputStream(SDPATH + "/卡车团/address_"
										+ userid));
						List<AddressInfo> list = (List<AddressInfo>) in
								.readObject(); // 读取customer对象
						in.close();
						rlist = list;
					}
				}
			} else {
				showToast(context, "未检测到SD卡!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rlist;
	}

	/**
	 * 保存地址列表
	 * 
	 * @param context
	 * @param list
	 * @return
	 */
	public static boolean saveAdresslist(Context context,
			List<AddressInfo> list, String userid) {
		boolean suc = false;
		File itemfile = null;
		try {
			if (isExitsSdcard()) {
				String SDPATH = Environment.getExternalStorageDirectory()
						.getPath();
				File file = new File(SDPATH + "/卡车团");
				if (!file.exists() && !file.isDirectory()) {
					file.mkdir();
					itemfile = new File(SDPATH + "/卡车团/address_" + userid);
					itemfile.createNewFile();
				} else {
					itemfile = new File(SDPATH + "/卡车团/address_" + userid);
					if (itemfile.exists()) {
						itemfile.delete();
					}
					itemfile.createNewFile();
				}
				// 序列化对象
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(SDPATH + "/卡车团/address_" + userid));
				out.writeObject(list); // 写入对象
				out.close();
				suc = true;
			} else {
				showToast(context, "未检测到SD卡!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return suc;
	}

	/**
	 * 清除所有选中的地址
	 * 
	 * @param context
	 * @return
	 */
	public static boolean ClearSelectedAddress(Context context, int id,
			String userid) {
		boolean flag = false;
		try {
			String SDPATH = Environment.getExternalStorageDirectory().getPath();
			if (isExitsSdcard()) {
				List<AddressInfo> list = getAdresslist(context, userid);
				for (int i = 0; i < list.size(); i++) {
					if (id == list.get(i).getAddressId()) {
						list.get(i).setSelected(true);
					} else {
						list.get(i).setSelected(false);
					}
				}
				// 序列化对象
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(SDPATH + "/卡车团/address_" + userid));
				out.writeObject(list); // 写入对象
				out.close();
				flag = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return flag;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(contentUri, proj,
				null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 根据路径获得突破并压缩返回bitmap用于显示
	 * 
	 * @param imagesrc
	 * @return
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, 480, 800);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getAlbumDir() {
		String SDPATH = Environment.getExternalStorageDirectory().getPath();
		File dir = new File(SDPATH + "/卡车团/Image");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取示例图片
	 * 
	 * @return
	 */
	public static String getTempPath() {
		String SDPATH = Environment.getExternalStorageDirectory().getPath();
		File dir = new File(SDPATH + "/卡车团/temp");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir.toString();
	}

	static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream("/sdcard/" + filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bmp.compress(format, quality, stream);
	}

	public static File getCommpressedfile(String filepath, int id) {
		File ff = null;
		try {
			File f = new File(filepath);
			Bitmap bm = getSmallBitmap(filepath);
			ff = new File(getAlbumDir(), "small_" + id + "_" + f.getName());
			FileOutputStream fos = new FileOutputStream(ff);
			bm.compress(Bitmap.CompressFormat.JPEG, 40, fos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ff;
	}

	/**
	 * 获取map的key-value
	 */
	public static String getString(HashMap<String, String> map, String key) {
		if (map.get(key) == null || map.get(key).equals("null")
				|| map.get(key).equals(""))
			return "";
		else
			return map.get(key);
	}

	/**
	 * 删除文件和文件夹
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	/**
	 * 判定输入汉字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 检测String是否全是中文
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkNameChese(String name) {
		boolean res = true;
		char[] cTemp = name.toCharArray();
		for (int i = 0; i < name.length(); i++) {
			if (!isChinese(cTemp[i])) {
				res = false;
				break;
			}
		}
		return res;
	}
	/**
	 * 格式化时间
	 * @return
	 */
	public static String fomatTimes(String data){
		Date date = parseDateTime(data);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String times = df.format(date);
		return times;
	}
	/**
	 * 时间的比较
	 * @param system
	 * @param start
	 * @param end
	 * @return
	 */
	public static String CompareTime(String system, String end){
		String times = "";
		Date systemDate = parseDateTime(system);
//		Date startDate = parseDateTime(start);
		Date endDate = parseDateTime(end);
		Calendar systemT = Calendar.getInstance();
		systemT.setTime(systemDate);
//		Calendar startT = Calendar.getInstance();
//		startT.setTime(startDate);
		Calendar endT = Calendar.getInstance();
		endT.setTime(endDate);
		if(systemT.getTimeInMillis()>endT.getTimeInMillis()){
			times = "已过期";
		}else if(endT.getTimeInMillis()>systemT.getTimeInMillis()){
			times= GetTimes(system,end);
		}
		
		return times;
	}
	public static  String GetTimes(String system, String end) {
		Date startDate = parseDateTime(system);
		Date endDate = parseDateTime(end);

		Calendar startT = Calendar.getInstance();
		startT.setTime(startDate);
		Calendar endT = Calendar.getInstance();
		endT.setTime(endDate);

		long between = (endT.getTimeInMillis() - startT.getTimeInMillis()) / 1000;// 获得秒
		long days = between / (24 * 3600);
		long hours = between % (24 * 3600) / 3600;
		long minutes = between % 3600 / 60;
        return days+"天"+hours+"时"+minutes+"分";
	}
	/**
	 * 将字符串形式的日期时间表示解析为时间对象
	 * 
	 * @param timeString
	 * @return
	 */
	public static Date parseDateTime(String timeString) {
		try {
			return DateUtils.parseDate(timeString, new String[] {
					"yyyy-MM-dd HH:mm:ss", "yyyy-M-d H:m:s",
					"yyyy-MM-dd H:m:s", "yyyy-M-d HH:mm:ss",
					"yyyy-MM-dd HH:mm:ss|SSS", "yyyyMMdd HH:mm:ss",
					"yyyy-MM-dd HH:mm", "yyyy-MM-dd" });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
