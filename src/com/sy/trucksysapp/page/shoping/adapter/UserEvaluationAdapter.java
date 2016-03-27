package com.sy.trucksysapp.page.shoping.adapter;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CommentModel;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.ScrollGridView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * 用户评价列表适配器
 * 
 * @author lxs 20150514
 * 
 */
public class UserEvaluationAdapter extends BaseAdapter {
	@SuppressLint("SimpleDateFormat")
	private final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private Context context;
	private ArrayList<CommentModel> list;
	private DisplayImageOptions options;
	private Date lastDate = null;

	public UserEvaluationAdapter(Context context, ArrayList<CommentModel> list) {
		this.context = context;
		this.list = list;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public String getLastDate() {
		if (lastDate == null)
			return "";
		else
			return sdf.format(lastDate);
	}

	public void notifyDataSetChanged(ArrayList<CommentModel> list) {
		lastDate = null;
		this.list = list;
		super.notifyDataSetChanged();
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_evaluation_list, null);
			holder.tv_driver_name = (TextView) convertView
					.findViewById(R.id.tv_driver_name);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_message_content = (TextView) convertView
					.findViewById(R.id.tv_message_content);
			holder.ratbar = (RatingBar) convertView
					.findViewById(R.id.rb_product_value);
			holder.gridview = (ScrollGridView) convertView
					.findViewById(R.id.gridview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CommentModel model = list.get(position);
		if (lastDate == null) {
			try {
				lastDate = sdf.parse(model.getDatestr());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		// holder.tv_driver_name.setText(model.getPersonname());
		
		if (model.getPersonname().equals("")
				|| model.getPersonname().equals("null")) {
			holder.tv_driver_name.setText("匿名");
		} else {
			if(CommonUtils.isPhone(model.getPersonname())){
				String phone = model.getPersonname().substring(0, 3)
						+ "****"
						+ model.getPersonname().substring(7,
								model.getPersonname().length());
				holder.tv_driver_name.setText(phone);	
			}else{
				holder.tv_driver_name.setText(model.getPersonname());
			}
			
		}
		if (model.getCommentcontent().equals("")
				|| model.getCommentcontent().equals("null")) {
			holder.tv_message_content.setText("这个家伙很懒，未留下文字评论！");
		}else{
			String str = URLDecoder.decode(model.getCommentcontent());
			holder.tv_message_content.setText(str);
		}
		holder.tv_time.setText(TextUtils.FormatDatestr(model.getDatestr()));
		holder.ratbar.setRating(model.getStarnum());
		String imgstr = model.getImgurl();
		String[] array = imgstr.split("\\|");
		if (array.length > 0) {
			if (array[0].equals("null") || array[0].equals("")) {
				array = new String[0];
				holder.gridview.setVisibility(View.GONE);
			}
		}
		if (array.length != 0) {
			holder.gridview.setVisibility(View.VISIBLE);
			GridAdapter adapter = new GridAdapter(array);
			holder.gridview.setAdapter(adapter);
		}
		return convertView;
	}

	class GridAdapter extends BaseAdapter {
		String[] imagelist;

		public GridAdapter(String[] imagelist) {
			this.imagelist = imagelist;
		}

		public int getCount() {
			return imagelist.length;
		}

		public Object getItem(int arg0) {
			return imagelist[arg0];
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int position, View convertView, ViewGroup arg2) {
			aViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.comment_pic, null);
				holder = new aViewHolder();
				holder.item_grida_image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				convertView.setTag(holder);
			} else {
				holder = (aViewHolder) convertView.getTag();
			}
			ImageLoader.getInstance().displayImage(
					SystemApplication.getBaseurl() + imagelist[position],
					holder.item_grida_image, options);
			holder.item_grida_image.setTag(position);
			holder.item_grida_image.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					int index = (Integer) arg0.getTag();
					String[] mimagelist = new String[imagelist.length];
					for (int i = 0; i < imagelist.length; i++) {
						mimagelist[i] = SystemApplication.getBaseurl()
								+ imagelist[i];
					}
					Intent imageintent = new Intent(context,
							ImagePagerActivity.class);
					imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
							mimagelist);
					imageintent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX,
							index);
					context.startActivity(imageintent);
				}
			});
			return convertView;
		}
	}

	class aViewHolder {
		ImageView item_grida_image;
	}

	class ViewHolder {
		TextView tv_driver_name;
		TextView tv_time;
		TextView tv_message_content;
		RatingBar ratbar;
		ScrollGridView gridview;
	}
}
