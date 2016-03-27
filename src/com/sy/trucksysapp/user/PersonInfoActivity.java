package com.sy.trucksysapp.user;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.FileUtil;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.wheelselect.lib.view.DialogWheelView;
import com.wheelselect.lib.view.DialogWheelView.OnCancelListener;
import com.wheelselect.lib.view.DialogWheelView.OnConfirmListener;
import org.json.JSONObject;

/**
 * 个人中心
 * 
 * @author lxs 20150429
 * 
 */
public class PersonInfoActivity extends BaseActivity implements OnClickListener {
	private Bitmap photo = null;
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private ImageView iv_center_head;
	private EditText user_name, user_phone, user_nick;
	private TextView user_sex, user_area;
	private File file;
	private Button btn_save;
	// 地区选择
	private String HeadpicUrl;
	private DisplayImageOptions options;
	private RelativeLayout rl_city, rl_sex_select;
	private DialogWheelView dialogWheelView;
	private String usercount, str_name, str_sex, str_phone, str_area, str_nick;
	/**
	 * @Fields inflater :页面动态载入器
	 */
	private LayoutInflater inflater;
	/**
	 * @Fields sex_select : 性别选择弹出框
	 */
	private PopupWindow sex_select;
	private View sex_select_root_view;
	private RelativeLayout rl_man;
	private RelativeLayout rl_woman;
	private TextView cancel_btn;
	private ImageView topbase_back;
	private Dialog progDialog = null;// 进度条

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_person_center);
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_head_man)
				.showImageForEmptyUri(R.drawable.icon_head_man)
				.showImageOnFail(R.drawable.icon_head_man).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		user_name = (EditText) findViewById(R.id.user_name);
		user_sex = (TextView) findViewById(R.id.user_sex);
		user_phone = (EditText) findViewById(R.id.user_phone);
		user_area = (TextView) findViewById(R.id.user_area);
		user_nick = (EditText) findViewById(R.id.user_nick);
		btn_save = (Button) findViewById(R.id.btn_save);
		iv_center_head = (ImageView) findViewById(R.id.iv_center_head);
		rl_city = (RelativeLayout) findViewById(R.id.rl_city);
		rl_city.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		iv_center_head.setOnClickListener(this);
		rl_sex_select = (RelativeLayout) findViewById(R.id.rl_sex_select);
		rl_sex_select.setOnClickListener(this);
		user_name.setText(PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingTrueName());
		user_sex.setText(PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingSex());
		user_phone.setText(PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingMobile());
			user_nick.setText(PreferenceUtils.getInstance(PersonInfoActivity.this)
					.getSettingUserNick());
		
		user_area.setText(PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingArea());
		if (!PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingHeadImg().equals("null")
				&& !PreferenceUtils.getInstance(PersonInfoActivity.this)
						.getSettingHeadImg().equals("")) {
			HeadpicUrl = SystemApplication.getBaseurl();
			HeadpicUrl = HeadpicUrl.substring(0, HeadpicUrl.length() - 1)
					+ PreferenceUtils.getInstance(PersonInfoActivity.this)
							.getSettingHeadImg();
			ImageLoader.getInstance().displayImage(HeadpicUrl, iv_center_head,
					options);
		}
		usercount = PreferenceUtils.getInstance(PersonInfoActivity.this)
				.getSettingUserName();
		topbase_back = (ImageView) findViewById(R.id.topbase_back);
		topbase_back.setOnClickListener(this);
	}

	/**
	 * 上传头像
	 */
	private void UpdateData() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/UpdateMember";
		RequestParams params = getparams();
		if (params != null) {
			showProgressDialog();
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
					dissmissProgressDialog();
				}

				@Override
				public void onSuccess(int arg0, String result) {
					// TODO Auto-generated method stub
					super.onSuccess(arg0, result);
					dissmissProgressDialog();
					try {
						JSONObject jsobj = new JSONObject(result);
						if (!jsobj.getBoolean("State")) {
							CommonUtils.showToast(PersonInfoActivity.this,
									jsobj.getString("Msg"));
						} else {
							CommonUtils.showToast(PersonInfoActivity.this,
									"修改成功");
							JSONObject obj = jsobj.getJSONObject("Obj");
							if (file != null && obj.has("Memb_HeadPic")) {
								PreferenceUtils.getInstance(
										PersonInfoActivity.this)
										.setSettingHeadImg(
												obj.getString("Memb_HeadPic"));
							}
								PreferenceUtils.getInstance(
										PersonInfoActivity.this).setSettingSex(
										obj.getString("Memb_Sex"));
								PreferenceUtils.getInstance(
										PersonInfoActivity.this)
										.setSettingArea(
												obj.getString("Memb_Address"));
								PreferenceUtils.getInstance(
										PersonInfoActivity.this)
										.setSettingMobile(
												obj.getString("Memb_Mobile"));
								PreferenceUtils.getInstance(
										PersonInfoActivity.this)
										.setSettingUserNick(
												obj.getString("Memb_Nick"));
								PreferenceUtils.getInstance(
										PersonInfoActivity.this)
										.setSettingTrueName(
												obj.getString("Memb_TrueName"));
								
							setResult(888);
							finish();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			});
		}

	}

	private RequestParams getparams() {
		RequestParams params = new RequestParams();
		str_name = user_name.getText().toString();
		str_sex = user_sex.getText().toString();
		str_phone = user_phone.getText().toString();
		str_area = user_area.getText().toString();
		str_nick = user_nick.getText().toString();
		try {
			params.put("Memb_Account", usercount);
			if (file != null) {
				params.put("Memb_HeadPic", file);
			}
			if (str_name != null) {
				params.put("Memb_TrueName", str_name);
			}
			if (str_sex != null) {
				params.put("Memb_Sex", str_sex);
			}
			if (str_phone != null) {
				if (CommonUtils.isPhone(str_phone)) {
					params.put("Memb_Mobile", str_phone);
				} else {
					CommonUtils.showToast(PersonInfoActivity.this, "请输入正确的手机号");
					return null;
				}
			}
			if (str_area != null) {
				params.put("Memb_Address", str_area);
			}
			if (str_nick != null) {
				params.put("Memb_Nick", str_nick);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return params;
	}

	/**
	 * 修改头像的弹框
	 * 
	 * @author lxs 20150429
	 * 
	 */
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));
			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button camera = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button Photo = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button cancel = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			camera.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(
							MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(new File(Environment
									.getExternalStorageDirectory(), "temp.jpg")));
					startActivityForResult(intent, PHOTOHRAPH);
					dismiss();
				}
			});
			Photo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							IMAGE_UNSPECIFIED);
					startActivityForResult(intent, PHOTOZOOM);
					dismiss();

				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	/**
	 * 对图片进行剪切处理
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 64);
		intent.putExtra("outputY", 64);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTORESOULT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/temp.jpg");
			startPhotoZoom(Uri.fromFile(picture));
		}

		if (data == null)
			return;

		// 读取相册缩放图片
		if (requestCode == PHOTOZOOM) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == PHOTORESOULT) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				photo = extras.getParcelable("data");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);// (0 -
				if (photo != null) {
					iv_center_head.setImageBitmap(photo);
				// 将返回的数据生成文件存储到本地
				saveMyBitmap("temp.jpg", photo);
				// 创建file对象
				file = new File(FileUtil.getSaveFilePath(), "temp.jpg");
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 保存图片
	 * 
	 * @param bitName
	 * @param mBitmap
	 */
	public void saveMyBitmap(String bitName, Bitmap mBitmap) {
		File f = new File(FileUtil.getSaveFilePath(), bitName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.iv_center_head:// 更改头像
			new PopupWindows(PersonInfoActivity.this, btn_save);
			break;
		case R.id.btn_save:// 保存修改
			UpdateData();
			break;
		case R.id.rl_city:
			showArea();
			break;
		case R.id.rl_sex_select:
			showSex();
			break;
		case R.id.woman:
			sex_select.dismiss();
			str_sex = "女";
			user_sex.setText("女");
			break;
		case R.id.man:
			sex_select.dismiss();
			str_sex = "男";
			user_sex.setText("男");
			break;
		case R.id.cancel_btn:
			sex_select.dismiss();
			break;
		case R.id.topbase_back:
			finish();
			break;
		}
	}

	/**
	 * 地区的选择
	 */
	private void showArea() {
		dialogWheelView = new DialogWheelView(PersonInfoActivity.this, 2);
		dialogWheelView.setcurValue(user_area.getText().toString().trim());
		dialogWheelView.setOnConfirmListener(new OnConfirmListener() {
			@Override
			public void OnConfirm(CharSequence charSequence) {
				// TODO Auto-generated method stub
				Toast.makeText(PersonInfoActivity.this, charSequence, 1000)
						.show();
				user_area.setText(charSequence);
			}
		});
		dialogWheelView.setOnCancelListener(new OnCancelListener() {
			@Override
			public void OnCancel() {
				// TODO Auto-generated method stub
				dialogWheelView.dismiss();
			}
		});
		dialogWheelView.show();
	}

	/**
	 * 性别选择
	 */
	private void showSex() {
		inflater = (LayoutInflater) PersonInfoActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		sex_select_root_view = inflater.inflate(
				R.layout.activity_person_center_sex_pop, null);
		rl_man = (RelativeLayout) sex_select_root_view.findViewById(R.id.man);
		rl_woman = (RelativeLayout) sex_select_root_view
				.findViewById(R.id.woman);
		cancel_btn = (TextView) sex_select_root_view
				.findViewById(R.id.cancel_btn);
		rl_man.setOnClickListener(this);
		rl_woman.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		sex_select = makePopupWindow(getBaseContext(), sex_select_root_view);
		sex_select.showAtLocation(user_sex, Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL, 0, 0);
	}

	// popwindow的初始化方法
	private PopupWindow makePopupWindow(Context baseContext, View view) {

		PopupWindow window;
		window = new PopupWindow(view, WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT);
		window.setAnimationStyle(android.R.style.Animation_Dialog);
		window.setOutsideTouchable(false);
		return window;
	}
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在处理请求...");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}
}
