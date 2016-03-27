package com.sy.trucksysapp.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.FragmentCallListener;
import com.sy.trucksysapp.page.driver.NewsdefaultFragment;
import com.sy.trucksysapp.page.driver.NewsmainActivity;
import com.sy.trucksysapp.page.order.OrderFragment;
import com.sy.trucksysapp.page.promotion.PromotionFragment;
import com.sy.trucksysapp.page.shoping.IndexShopFragment;
import com.sy.trucksysapp.page.shoping.ShopServiceFragment;
import com.sy.trucksysapp.user.UserCenterFragment;
import com.sy.trucksysapp.user.AppsetActivity.UpdataInfo;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.DialogProgressView;
import com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener,
		FragmentCallListener {
	// 定义4个Fragment的对象
	private NewHomeFragment homefragment;
	private IndexShopFragment shopfragment;
	private PromotionFragment promotionfragment;
	// private PersonFragment personFragment;
	private UserCenterFragment personFragment;
	// 定义底部导航栏的四个布局
	private RelativeLayout shop_layout;
	private RelativeLayout home_layout;
	private RelativeLayout freight_layout;
	private RelativeLayout person_layout;
	// 定义底部导航栏中的ImageView与TextView
	private ImageView home_image_icon;
	private ImageView shop_image_icon;
	private ImageView freight_image_icon;
	private ImageView person_image_icon;
	private TextView home_text;
	private TextView shop_text;
	private TextView freight_text;
	private TextView person_text;
	// 定义FragmentManager对象
	FragmentManager fManager;
	// 定义要用的颜色值
	private int gray = 0xFF999999;
	private int red = 0xFFFF5000;
	DialogConfirmView dialogView;

	private static Handler handler;
	private static DialogProgressView progressView;
	static int FileLength;
	static int DownedFileLength = 0;
	private Context context;
	private UpdataInfo info = null;
	final int UPDATA_CLIENT = 0;
	final int GET_UNDATAINFO_ERROR = 1;
	final int DOWN_ERROR = 2;
	final static int DOWN_PROGRESS = 3;
	final static int DOWN_LEHGTH = 4;
    private int showFlag = -1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		context = this;
		startService();
		fManager = getSupportFragmentManager();
		initViews();
		// 价差更新新版本
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
						// CommonUtils.showToast(context, "获取服务器更新信息失败");
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
		checkupdate();
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
		com.sy.trucksysapp.widget.DialogConfirmView.OnCancelListener cancellistener = new com.sy.trucksysapp.widget.DialogConfirmView.OnCancelListener() {
			@Override
			public void OnCancel() {
				// TODO Auto-generated method stub
				finish();
				System.exit(0);
			}
		};
		DialogConfirmView ConfirmView = new DialogConfirmView(context, "版本升级",
				info.getDescription(), listener, cancellistener);
		ConfirmView.show();

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

	private void startService() {
		Intent intent1 = new Intent(Intent.ACTION_RUN);
		intent1.setClass(MainActivity.this, UploadlocationService.class);
		this.startService(intent1);
	}

	public void initViews() {
		home_image_icon = (ImageView) findViewById(R.id.home_image_icon);
		shop_image_icon = (ImageView) findViewById(R.id.shop_image_icon);
		freight_image_icon = (ImageView) findViewById(R.id.freight_image_icon);
		person_image_icon = (ImageView) findViewById(R.id.person_image_icon);
		home_text = (TextView) findViewById(R.id.home_text);
		shop_text = (TextView) findViewById(R.id.shop_text);
		freight_text = (TextView) findViewById(R.id.freight_text);
		person_text = (TextView) findViewById(R.id.person_text);
		home_layout = (RelativeLayout) findViewById(R.id.home_layout);
		shop_layout = (RelativeLayout) findViewById(R.id.shop_layout);
		freight_layout = (RelativeLayout) findViewById(R.id.freight_layout);
		person_layout = (RelativeLayout) findViewById(R.id.person_layout);
		home_layout.setOnClickListener(this);
		shop_layout.setOnClickListener(this);
		freight_layout.setOnClickListener(this);
		person_layout.setOnClickListener(this);
		setChioceItem(0);
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

	public void checkupdate() {
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
						if (!info.getVersion().equals(versionname)) {
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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.home_layout:
			setChioceItem(0);
			break;
		case R.id.shop_layout:
			setChioceItem(1);
			break;
		case R.id.freight_layout:
			setChioceItem(2);
			break;
		case R.id.person_layout:
			setChioceItem(3);
			break;

		default:
			break;
		}
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		if(showFlag<0){
			Timer tExit = null;
			if (isExit == false) {
				isExit = true; // 准备退出
				CommonUtils.showToast(this, "再按一次退出程序");
				tExit = new Timer();
				tExit.schedule(new TimerTask() {
					@Override
					public void run() {
						isExit = false; // 取消退出
					}
				}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

			} else {
				finish();
				System.exit(0);
			}
		}else{
			setChioceItem(0);
		}
		
	}

	/**
	 * 设置选中的
	 * 
	 * @param index
	 */
	@SuppressLint("CommitTransaction")
	public void setChioceItem(int index) {
		FragmentTransaction transaction = fManager.beginTransaction();
		clearChioce();
		hideFragments(transaction);
		showFlag = -1;
		switch (index) {
		case 0:
			home_image_icon.setImageResource(R.drawable.home_icon_press);
			home_text.setTextColor(red);
			if (homefragment == null) {
				// 如果homefragment为空，则创建一个并添加到界面上
				homefragment = new NewHomeFragment();
				transaction.add(R.id.content, homefragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(homefragment);
			}
			break;
		case 1:
			shop_image_icon.setImageResource(R.drawable.shop_icon_press);
			shop_text.setTextColor(red);
			if (shopfragment == null) {
				// 如果homefragment为空，则创建一个并添加到界面上
				// shopfragment = new ShopFragment();
				shopfragment = new IndexShopFragment();
				transaction.add(R.id.content, shopfragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(shopfragment);
			}
			break;
		case 2:
//			LayoutParams para= freight_image_icon.getLayoutParams();
//			para.height = 80dp;
//			para.width = 120;	
//			freight_image_icon.setLayoutParams(para);
//			freight_image_icon.setImageResource(R.drawable.services_icon_press);
//			freight_text.setTextColor(red);
//			private PromotionFragment promotionfragment;
			if (promotionfragment == null) {
				// 如果homefragment为空，则创建一个并添加到界面上
				// freightfragment = new FreightFragment();
				promotionfragment = new PromotionFragment();
				transaction.add(R.id.content, promotionfragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(promotionfragment);
			}
			break;
		case 3:
			person_image_icon.setImageResource(R.drawable.person_icon_press);
			person_text.setTextColor(red);
			if (personFragment == null) {
				// 如果homefragment为空，则创建一个并添加到界面上
				personFragment = new UserCenterFragment();
				transaction.add(R.id.content, personFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(personFragment);
			}
			break;

		default:
			break;
		}
		transaction.commit();
	}

	// 隐藏所有的Fragment,避免fragment混乱
	private void hideFragments(FragmentTransaction transaction) {
		if (homefragment != null) {
			transaction.hide(homefragment);
		}
		if (shopfragment != null) {
			transaction.hide(shopfragment);
		}
		if (promotionfragment != null) {
			transaction.hide(promotionfragment);
		}
		if (personFragment != null) {
			transaction.hide(personFragment);
		}
	}

	// 定义一个重置所有选项的方法
	public void clearChioce() {
		home_image_icon.setImageResource(R.drawable.home_icon_normal);
		home_text.setTextColor(gray);
		shop_image_icon.setImageResource(R.drawable.shop_icon_normal);
		shop_text.setTextColor(gray);
//		LayoutParams para= freight_image_icon.getLayoutParams();
//		para.height = 100;
//		para.width = 160;	
//		freight_image_icon.setLayoutParams(para);
		person_image_icon.setImageResource(R.drawable.person_icon_normal);
		person_text.setTextColor(gray);
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_BACK:
	// dialogView = new DialogConfirmView(MainActivity.this, "提示",
	// "您确定要退出应用吗", new OnConfirmListener() {
	// @Override
	// public void OnConfirm() {
	// // TODO Auto-generated method stub
	// System.exit(0);
	// }
	// });
	// dialogView.show();
	// return false;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	public void transfermsg(int item) {
		// TODO Auto-generated method stub
		setChioceItem(item);
		if(item==1||item==2){
			showFlag = 0;
		}
	}

}
