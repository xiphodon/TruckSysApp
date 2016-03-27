package com.sy.trucksysapp.page.driver.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.TextUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsFavoriteAdapter extends BaseAdapter {
	private static final int TYPE_TITLE = 0;
	private static final int TYPE_CONTENT = 1;
	public ArrayList<HashMap<String, String>> mListItems;
	public Context context;
	private DisplayImageOptions options;

	public NewsFavoriteAdapter(Context context,
			ArrayList<HashMap<String, String>> mListItems) {
		super();
		this.mListItems = mListItems;
		this.context = context;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_TITLE;
		} else {
			return TYPE_CONTENT;
		}
	}

	@Override
	public Object getItem(int arg0) {
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = mListItems.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.news_item_nomal, null);
			holder.type = TYPE_CONTENT;
			holder.cell_news_img = (ImageView) convertView
					.findViewById(R.id.cell_news_img);
			holder.cell_newslist_textview_title = (TextView) convertView
					.findViewById(R.id.cell_newslist_textview_title);
			holder.cell_newslist_textview_type = (TextView) convertView
					.findViewById(R.id.cell_newslist_textview_type);
			holder.cell_newslist_textview_time = (TextView) convertView
					.findViewById(R.id.cell_newslist_textview_time);
			holder.cell_newslist_textview_comments = (TextView) convertView
					.findViewById(R.id.cell_newslist_textview_comments);
			holder.rowdata = map;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 加载内容
		holder.cell_newslist_textview_title.setText(map.get("News_Summary")
				+ "");
		holder.cell_newslist_textview_type.setText(map.get("Cate_Name") + "");
		holder.cell_newslist_textview_time.setText(TextUtils.FormatDatestr(map
				.get("News_CreateDate") + ""));
		holder.cell_newslist_textview_comments.setText(map.get("count") + "");
		ImageLoader.getInstance().displayImage(
				SystemApplication.getImgUrl() + map.get("News_Pic") + "",
				holder.cell_news_img, options);
		return convertView;
	}

	public ArrayList<HashMap<String, String>> getmListItems() {
		return mListItems;
	}

	public void setmListItems(ArrayList<HashMap<String, String>> mListItems) {
		this.mListItems = mListItems;
	}

	public class ViewHolder {
		int type;
		ImageView newsimageview;
		ImageView cell_news_img;
		TextView cell_newslist_textview_title;
		TextView cell_newslist_textview_type;
		TextView cell_newslist_textview_time;
		TextView cell_newslist_textview_comments;
		HashMap<String, String> rowdata;
	}

}
