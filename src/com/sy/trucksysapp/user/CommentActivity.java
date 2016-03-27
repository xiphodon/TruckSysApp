package com.sy.trucksysapp.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.RatingBar.OnRatingBarChangeListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.ImageModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.ScrollGridView;

public class CommentActivity extends BaseActivity {
	private GridAdapter GridAdapter;
	private InputMethodManager manager;
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	private final String[] ratings = new String[] { "", "非常不满意", "不满意", "一般",
			"满意", "非常满意" };
	private Context context;
	private ArrayList<ImageModel> imagelist = new ArrayList<ImageModel>();
	private RatingBar bar_comment;
	private TextView commentresult;
	private EditText commentcontent;
	private ScrollGridView gridview;
	private Button addcomment;
	private String type = "-1";
	private String id = "", ordid = "", MemberId = "";
	private Dialog progDialog = null;// 搜索时进度条
	private int photoNum = 0, isEditable = 0;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_user_comment);
		type = getIntent().getStringExtra("type");
		id = getIntent().getStringExtra("id");
		try {
			ordid = getIntent().getStringExtra("OId");
		} catch (Exception e) {
		}

		context = CommentActivity.this;
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		initviews();
	}

	protected void onResume() {
		MemberId = PreferenceUtils.getInstance(context).getSettingUserId();
		super.onResume();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "提交评论...");
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

	private void initviews() {
		bar_comment = (RatingBar) findViewById(R.id.bar_comment);
		commentresult = (TextView) findViewById(R.id.commentresult);
		commentcontent = (EditText) findViewById(R.id.commentcontent);
		gridview = (ScrollGridView) findViewById(R.id.gridview);
		imagelist
				.add(new ImageModel(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused),
						null, false));
		GridAdapter = new GridAdapter(imagelist);
		gridview.setAdapter(GridAdapter);
		bar_comment
				.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar arg0, float arg1,
							boolean arg2) {
						commentresult.setText(ratings[(int) arg1]);
					}
				});
		addcomment = (Button) findViewById(R.id.addcomment);
		addcomment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (MemberId.equals("")) {
					startActivityForResult(new Intent(context,
							LoginActivity.class), 888);
					return;
				}
				
				float level = bar_comment.getRating();
				if(level<1){
					CommonUtils.showToast(context, "您还未做出星级评价");
					return;
				}
				// 保存评价
				showProgressDialog();
				String commenttext = commentcontent.getText().toString();
				String msgStr = null;
				try {
					msgStr = URLEncoder.encode(commenttext, "UTF-8");
					msgStr = msgStr.replaceAll("\\+", "%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url = SystemApplication.getBaseurl()
						+ "TruckService/SaveGoodsComment";
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams RequestParams = new RequestParams();
				RequestParams.put("GoCo_Name",
						PreferenceUtils.getInstance(context)
								.getSettingUserName());
				RequestParams.put("GoCo_ValuerId", MemberId);
				RequestParams.put("GoCo_Content", msgStr);
				RequestParams.put("GoCo_Star", level + "");
				RequestParams.put("GoCo_GoodType", type);
				RequestParams.put("GoCo_GoodId", id);
				RequestParams.put("id", ordid);
				if (imagelist.size() != 1) {
					for (int i = 0; i < imagelist.size() - 1; i++) {
						ImageModel m = imagelist.get(i);
						File cfile = CommonUtils.getCommpressedfile(CommonUtils
								.getRealFilePath(context, m.getUri()), i);
						try {
							RequestParams.put("GoCo_Pic_" + i, cfile);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				client.post(url, RequestParams, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String result) {
						super.onSuccess(result);
						try {
							JSONObject jsobj = new JSONObject(result);
							/* 删除缓存图片 */
							CommonUtils.delete(CommonUtils.getAlbumDir());
							File temp = new File(CommonUtils.getTempPath());
							CommonUtils.delete(temp);
							if (jsobj.getBoolean("State")) {
								dissmissProgressDialog();
								isEditable = 1;
								setResult(100, new Intent().putExtra("state", isEditable == 1));
								finish();
							}
						} catch (Exception e) {
							e.printStackTrace();
							CommonUtils.showToast(context, "评论失败！");
							dissmissProgressDialog();
						}
					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						dissmissProgressDialog();
						CommonUtils.showToast(context, "评论失败！");
						super.onFailure(arg0, arg1);
					}
				});
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == NONE)
				return;
			// 拍照
			if (requestCode == PHOTOHRAPH) {
				// 设置文件保存路径这里放在跟目录下
				int l = imagelist.size();
				File picture = new File(CommonUtils.getTempPath() + "/temp_"
						+ photoNum + ".jpg");
				Uri uri = Uri.fromFile(picture);
				Bitmap bitmap = CommonUtils.revitionImageSize(CommonUtils
						.getRealFilePath(context, uri));
				imagelist.add(l - 1, new ImageModel(bitmap, uri, true));
				GridAdapter.notifyDataSetChanged();
				photoNum++;
			}
			if (data == null)
				return;
			// 读取相册缩放图片
			if (requestCode == PHOTOZOOM) {
				Uri uri = data.getData();
				Bitmap bitmap = CommonUtils.revitionImageSize(CommonUtils
						.getRealFilePath(context, uri));
				if(bitmap!=null){
					int l = imagelist.size();
					imagelist.add(l - 1, new ImageModel(bitmap, uri, true));
					GridAdapter.notifyDataSetChanged();
				}else{
					CommonUtils.showToast(context, "只能上传图片信息!");
				}
				
			}
		} catch (Exception e) {
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 修改头像的弹框
	 * 
	 * @author lxs 20150429
	 * 
	 */
	public class PopupWindows extends PopupWindow {

		@SuppressWarnings("deprecation")
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
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
							.fromFile(new File(CommonUtils.getTempPath(),
									"temp_" + photoNum + ".jpg")));
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

	public class ViewHolder {
		public ImageView item_grida_image;
		public ImageView imagedelete;
	}

	class GridAdapter extends BaseAdapter {
		ArrayList<ImageModel> imagelist;

		public GridAdapter(ArrayList<ImageModel> imagelist) {
			this.imagelist = imagelist;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagelist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return imagelist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.pic_tip,
						null);
				holder = new ViewHolder();
				holder.item_grida_image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.imagedelete = (ImageView) convertView
						.findViewById(R.id.imagedelete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.item_grida_image.setImageBitmap(imagelist.get(position)
					.getBitmap());
			if (imagelist.get(position).getIsshowdelete()) {
				holder.imagedelete.setVisibility(View.VISIBLE);
				holder.imagedelete.setTag(position);
				holder.imagedelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0s) {
						// TODO Auto-generated method stub
						int pos = (Integer) arg0s.getTag();
						CommonUtils.showToast(context, pos + "");
						imagelist.remove(pos);
						GridAdapter.notifyDataSetChanged();
					}
				});
			} else {
				holder.imagedelete.setVisibility(View.GONE);
				holder.item_grida_image
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								hideKeyboard();
								new PopupWindows(context, addcomment);
							}
						});
			}
			return convertView;
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
