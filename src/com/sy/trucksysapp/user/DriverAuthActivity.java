package com.sy.trucksysapp.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.sy.trucksysapp.utils.IdVerification;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.LoadingFrameLayout;

/**
 * 司机认证页面
 * 
 * @author lxs 20150615
 * 
 */
public class DriverAuthActivity extends BaseActivity implements OnClickListener {

	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private Context context;
	private RelativeLayout rl_user_card, rl_driver_card, rl_driver_truck;
	private EditText et_edit_name, et_edit_idnum;
	private String name, numId;
	private ImageView img_user_card, img_driver_card, img_driver_truck;
	private DisplayImageOptions options;
	private Button btn_save;
	private File file_user_card;
	private File file_driver_card;
	private File file_driver_truck;
	private int PHOTOTYPE = -1;
	private String usercount;
	private String baseUrl = SystemApplication.getBaseurl();
	private Dialog progDialog = null;// 进度条
	/**
	 * 未提交认证 0 已提交待审核 1 未通过审核 2 已通过审核 3
	 */
	private int auth_flag;
	private TextView tv_auth_flag;
	private LoadingFrameLayout loading;
	private Dialog dlg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_auth);
		initView();
		initFailData();
		loading = (LoadingFrameLayout) findViewById(R.id.loading);
		loading.show("页面加载中...");
		GetAuthData();
	}

	private void initView() {
		context = DriverAuthActivity.this;
		progDialog = CommonUtils.ShowProcess(this, "正在处理请求...");
		usercount = PreferenceUtils.getInstance(context).getSettingUserId();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_auth_bg)
				.showImageForEmptyUri(R.drawable.no_auth_bg)
				.showImageOnFail(R.drawable.no_auth_bg).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		rl_user_card = (RelativeLayout) findViewById(R.id.rl_user_card);
		rl_driver_card = (RelativeLayout) findViewById(R.id.rl_driver_card);
		rl_driver_truck = (RelativeLayout) findViewById(R.id.rl_driver_truck);
		btn_save = (Button) findViewById(R.id.btn_save);
		rl_user_card.setOnClickListener(this);
		rl_driver_card.setOnClickListener(this);
		rl_driver_truck.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		et_edit_name = (EditText) findViewById(R.id.et_edit_name);
		et_edit_name.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(et_edit_name.getText().toString().trim().length()>12){
					CommonUtils.showToast(context, "姓名长度为2-12个字符");
				}
			}
		});
		et_edit_idnum = (EditText) findViewById(R.id.et_edit_idnum);
		img_user_card = (ImageView) findViewById(R.id.img_user_card);
		img_driver_card = (ImageView) findViewById(R.id.img_driver_card);
		img_driver_truck = (ImageView) findViewById(R.id.img_driver_truck);
		tv_auth_flag = (TextView) findViewById(R.id.tv_auth_flag);
	}

	/**
	 * 获取信息失败
	 */
	private void initFailData() {
		ImageLoader.getInstance().displayImage("", img_user_card, options);
		ImageLoader.getInstance().displayImage("", img_driver_card, options);
		ImageLoader.getInstance().displayImage("", img_driver_truck, options);
	}

	/**
	 * 驾驶员认证信息
	 */
	private void initData(JSONObject jsobj) {
		try {
			et_edit_name.setText(jsobj.getString("MeAu_Name"));
			et_edit_idnum.setText(jsobj.getString("MeAu_Card"));
			auth_flag = jsobj.getInt("MeAu_AuthenState");
			// auth_flag= 2;
			ImageLoader.getInstance().displayImage(
					baseUrl + jsobj.getString("MeAu_DrivePic"), img_user_card,
					options);
			ImageLoader.getInstance().displayImage(
					baseUrl + jsobj.getString("MeAu_DrivingPic"),
					img_driver_card, options);
			ImageLoader.getInstance().displayImage(
					baseUrl + jsobj.getString("MeAu_MemPic"), img_driver_truck,
					options);
			if (auth_flag == 3) {
				btn_save.setVisibility(View.GONE);
				rl_user_card.setClickable(false);
				rl_driver_card.setClickable(false);
				rl_driver_truck.setClickable(false);
				et_edit_name.setEnabled(false);
				et_edit_idnum.setEnabled(false);
				tv_auth_flag.setText("已认证");
			} else if (auth_flag == 2) {
				tv_auth_flag.setText("未通过审核");
				initdialog(auth_flag, jsobj.getString("MeAu_Reason"));
				initFailData();
				btn_save.setVisibility(View.VISIBLE);
				rl_user_card.setClickable(true);
				rl_driver_card.setClickable(true);
				rl_driver_truck.setClickable(true);
				et_edit_name.setEnabled(true);
				et_edit_idnum.setEnabled(true);
			} else if (auth_flag == 1) {
				initdialog(auth_flag, "您的认证正在审核中，请等待审核！");
				tv_auth_flag.setText("未审核");
				btn_save.setVisibility(View.GONE);
				rl_user_card.setClickable(false);
				rl_driver_card.setClickable(false);
				rl_driver_truck.setClickable(false);
				et_edit_name.setEnabled(false);
				et_edit_idnum.setEnabled(false);
			} else {
				tv_auth_flag.setText("未认证");
				initFailData();
				btn_save.setVisibility(View.VISIBLE);
				rl_user_card.setClickable(true);
				rl_driver_card.setClickable(true);
				rl_driver_truck.setClickable(true);
				et_edit_name.setEnabled(true);
				et_edit_idnum.setEnabled(true);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.rl_user_card:
			new PopupWindows(context, btn_save, 1);
			break;
		case R.id.rl_driver_card:
			new PopupWindows(context, btn_save, 2);
			break;
		case R.id.rl_driver_truck:
			new PopupWindows(context, btn_save, 3);
			break;
		case R.id.btn_save:
			UpdateData();
			break;
		default:
			break;
		}
	}

	/**
	 * 获取本人认证信息
	 */
	private void GetAuthData() {

		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetAuth";
		RequestParams params = new RequestParams();
		params.put("MemberId", usercount);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				loading.dismiss();
				CommonUtils.showToast(context, "获取认证 信息失败");
				DriverAuthActivity.this.finish();

			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				loading.dismiss();
				try {
					JSONObject jsobj = new JSONObject(result);
					int total = jsobj.getInt("total");
					if (total > 0) {
						JSONArray rows = jsobj.getJSONArray("rows");
						initData(rows.getJSONObject(0));
					} else {
						initFailData();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
	}

	/**
	 * 上传认证数据
	 */
	private void UpdateData() {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/IsAuth";
		RequestParams params = getparams();
		if (params != null) {
			progDialog.show();
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					// TODO Auto-generated method stub
					super.onFailure(arg0, arg1);
					CommonUtils.showToast(context, "提交认证失败，请重新提交");
					progDialog.dismiss();
				}

				@Override
				public void onSuccess(int arg0, String result) {
					// TODO Auto-generated method stub
					super.onSuccess(arg0, result);
					progDialog.dismiss();
					try {
						JSONObject jsobj = new JSONObject(result);
						if (jsobj.getBoolean("State")) {
							CommonUtils.showToast(context, "提交认证成功，请等待管理员审核");
							if (file_user_card.exists()) {
								file_user_card.delete();
								file_driver_card.delete();
								file_driver_truck.delete();
							}
							DriverAuthActivity.this.finish();
						} else {
							CommonUtils.showToast(context, "提交认证失败，请重新提交");
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
		name = et_edit_name.getText().toString().trim();
		numId = et_edit_idnum.getText().toString().trim();
		if (name == null || name.equals("")) {
			CommonUtils.showToast(context, "请输入真实姓名");
			return null;
		}else if(name.length()<2){
			CommonUtils.showToast(context, "姓名长度为2-12个字符");
			return null;
		}else if(!CommonUtils.checkNameChese(name)){
			CommonUtils.showToast(context, "请输入真实姓名");
			return null;
		}
		if (numId == null || numId.equals("")) {
			CommonUtils.showToast(context, "请输入身份证号");
			return null;
		}else if(!IdVerification.IDCardValidate(numId).equals("")){
			CommonUtils.showToast(context, "请输入合法的身份证信息");
			return null;
		}
		if (file_user_card == null) {
			CommonUtils.showToast(context, "请上传身份证照片");
			return null;
		}
		if (file_driver_card == null) {
			CommonUtils.showToast(context, "请上传行驶证照片");
			return null;
		}
		if (file_driver_truck == null) {
			CommonUtils.showToast(context, "请上传驾驶员和车辆合照");
			return null;
		}
		params.put("MemberId", usercount);
		params.put("MeAu_Name", name);
		params.put("MeAu_Card", numId);
		try {
			params.put("MeAu_DrivePic", file_user_card);
			params.put("MeAu_DrivingPic", file_driver_card);
			params.put("MeAu_MemPic", file_driver_truck);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * 照片的弹框
	 * 
	 * @author lxs 20150429
	 * 
	 */
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent, final int type) {
			PHOTOTYPE = type;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == NONE)
			return;
		// 拍照
		if (requestCode == PHOTOHRAPH) {
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/temp.jpg");
			Uri uri = Uri.fromFile(picture);
			try {
				Bitmap bitmap = CommonUtils.revitionImageSize(CommonUtils
						.getRealFilePath(context, uri));
				setData(bitmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (data == null)
			return;

		if (requestCode == PHOTOZOOM) {

			Uri uri = data.getData();
			try {
				Bitmap bitmap = CommonUtils.revitionImageSize(CommonUtils
						.getRealFilePath(context, uri));
				if(bitmap!=null){
					setData(bitmap);
				}else{
					CommonUtils.showToast(context, "只支持图片文件的上传！");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private void setData(Bitmap photo) {
		switch (PHOTOTYPE) {
		case 1:
			if (photo != null) {
				img_user_card.setImageBitmap(photo);
			}
			// 将返回的数据生成文件存储到本地
			saveMyBitmap("file_user_card.jpg", photo);
			// 创建file对象
			file_user_card = new File(FileUtil.getSaveFilePath(),
					"file_user_card.jpg");
			break;
		case 2:
			if (photo != null) {
				img_driver_card.setImageBitmap(photo);
			}
			// 将返回的数据生成文件存储到本地
			saveMyBitmap("file_driver_card.jpg", photo);
			// 创建file对象
			file_driver_card = new File(FileUtil.getSaveFilePath(),
					"file_driver_card.jpg");
			break;
		case 3:
			if (photo != null) {
				img_driver_truck.setImageBitmap(photo);
			}
			// 将返回的数据生成文件存储到本地
			saveMyBitmap("file_driver_truck.jpg", photo);
			// 创建file对象
			file_driver_truck = new File(FileUtil.getSaveFilePath(),
					"file_driver_truck.jpg");
			break;

		default:
			break;
		}
	}

	/**
	 * 弹出提示框
	 * 
	 * @param flag
	 * @param msg
	 */
	private void initdialog(int flag, String msg) {

		View dialog = getLayoutInflater().inflate(R.layout.pop_dialog_selet,
				null);
		TextView tv_msg = (TextView) dialog.findViewById(R.id.tv_msg);
		tv_msg.setText(msg);
		TextView tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dlg != null)
					dlg.dismiss();
				if (auth_flag == 1) {
					DriverAuthActivity.this.finish();
				}
			}
		});
		dlg = new Dialog(DriverAuthActivity.this, R.style.dialog_fullscreen);
		dlg.setContentView(dialog);
		dlg.show();
	}
}