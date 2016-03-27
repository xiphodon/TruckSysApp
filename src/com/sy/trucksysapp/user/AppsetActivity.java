package com.sy.trucksysapp.user;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.DialogProgressView;

public class AppsetActivity extends BaseActivity {

	private Context context;
	private UpdataInfo info = null;
	private static Handler handler;
	final int UPDATA_CLIENT = 0;
	final int GET_UNDATAINFO_ERROR = 1;
	final int DOWN_ERROR = 2;
	final static int DOWN_PROGRESS = 3;
	final static int DOWN_LEHGTH = 4;
	private static DialogProgressView progressView;
	static int FileLength;
	static int DownedFileLength = 0;
	Dialog ServiceDialog = null;
	private TextView app_version_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		context = this;
		app_version_name = (TextView)findViewById(R.id.app_version_name);
		try {
			app_version_name.setText(getVersionName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressView = new DialogProgressView(context);
		progressView.setMessage("正在下载更新");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if (!Thread.currentThread().isInterrupted()) {
					switch (msg.what) {
					case UPDATA_CLIENT:
						// 对话框通知用户升级程序
						showUpdataDialog();
						break;
					case GET_UNDATAINFO_ERROR:
						// 服务器超时
						CommonUtils.showToast(context, "获取服务器更新信息失败");
						break;
					case DOWN_ERROR:
						// 下载apk失败
						CommonUtils.showToast(context, "下载新版本失败");
						break;
					case DOWN_PROGRESS:
						progressView.setProgress(DownedFileLength);
						break;
					case DOWN_LEHGTH:
						progressView.setMax(FileLength);
						break;
					}
				}
			}
		};

	}

	/***
	 * 安装服务标准
	 * 
	 * @param v
	 */
	public void Kservice(View v) {
		if (ServiceDialog == null) {
			ServiceDialog = CommonUtils
					.CreateServiceDialog(AppsetActivity.this);
		}
		ServiceDialog.show();
	}

	public void Feedback(View v) {
		// TODO Auto-generated method stub
		startActivity(new Intent(context, FeedbackActivity.class));
	}

	/**
	 * 联系我们
	 * 
	 * @param v
	 */
	public void AboutUs(View v) {
		startActivity(new Intent(context, AboutActivity.class));
	}

	public static class UpdataInfo {
		public UpdataInfo() {
			super();
			// TODO Auto-generated constructor stub
		}

		private String version;
		private String url;
		private String description;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 * 
	 * 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性, 对话框的内容,样式,按钮
	 * 3.通过builder 创建一个对话框 4.对话框show()出来
	 */
	protected void showUpdataDialog() {
		com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener listener = new com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener() {
			@Override
			public void OnConfirm() {
				// TODO Auto-generated method stub
				DownedFileLength = 0;
				progressView.setMax(100);
				progressView.setProgress(0);
				progressView.show();
				Thread thread = new Thread() {
					public void run() {
						try {
							downLoadApk();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				};
				thread.start();
			}
		};
		DialogConfirmView ConfirmView = new DialogConfirmView(context, "版本升级",
				info.getDescription(), listener);
		ConfirmView.show();

	}

	/*
	 * 获取当前程序的版本号
	 */
	public String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	public static File getFileFromServer(String path) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		File file = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				URL url = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				// 获取到文件的大小
				FileLength = conn.getContentLength();
				InputStream is = conn.getInputStream();
				handler.sendEmptyMessage(DOWN_LEHGTH);
				file = new File(Environment.getExternalStorageDirectory(),
						"updata.apk");
				byte[] buffer = new byte[1024];
				BufferedInputStream bis = new BufferedInputStream(is);
				FileOutputStream outputStream = new FileOutputStream(file);
				int len;
				while ((len = bis.read(buffer)) != -1) {
					outputStream.write(buffer, 0, len);
					DownedFileLength += len;
					handler.sendEmptyMessage(DOWN_PROGRESS);
				}
				outputStream.close();
				bis.close();
				is.close();
			} catch (Exception e) {
				return null;
			}
			return file;
		} else {
			return null;
		}
	}

	/*
	 * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
	 */
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");// 设置解析的数据源
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(parser.getName())) {
					info.setVersion(parser.nextText()); // 获取版本号
				} else if ("url".equals(parser.getName())) {
					info.setUrl(parser.nextText()); // 获取要升级的APK文件
				} else if ("description".equals(parser.getName())) {
					info.setDescription(parser.nextText()); // 获取该文件的信息
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		try {
			File file = getFileFromServer(info.getUrl());
			Thread.sleep(2000);
			installApk(file);
			progressView.dismiss(); // 结束掉进度条对话框
		} catch (Exception e) {
			Message msg = new Message();
			msg.what = DOWN_ERROR;
			progressView.dismiss(); // 结束掉进度条对话框
			handler.sendMessage(msg);
			e.printStackTrace();
		}
	}

	public void update(View v) {
		final String versionname;
		try {
			versionname = getVersionName();
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						// 从资源文件获取服务器 地址
						String path = SystemApplication.getInstance()
								.getBaseurl() + "update.xml";
						// 包装成url的对象
						URL url = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setConnectTimeout(5000);
						InputStream is = conn.getInputStream();
						info = getUpdataInfo(is);
						if (info.getVersion().equals(versionname)) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									CommonUtils.showToast(context,
											"当前版本已经是最新版本！");
								}
							});
						} else {
							Message msg = new Message();
							msg.what = UPDATA_CLIENT;
							handler.sendMessage(msg);
						}
					} catch (Exception e) {
						// 待处理
						Message msg = new Message();
						msg.what = GET_UNDATAINFO_ERROR;
						handler.sendMessage(msg);
						e.printStackTrace();
					}
				}
			}).start();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
