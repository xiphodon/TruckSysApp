package com.sy.trucksysapp.page.driver.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.TopviewAdapter;
import com.sy.trucksysapp.utils.TextUtils;
import com.sy.trucksysapp.widget.AutoScrollViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class NewsIndexAdapter extends BaseAdapter {
	private static final int TYPE_TITLE = 0;
	private static final int TYPE_CONTENT = 1;
	public ArrayList<HashMap<String, Object>> mListItems;
	public Context context;
	private DisplayImageOptions options;
	private AutoScrollViewPager viewpager;
	private LinearLayout llPointGroup;
	private TextView tvDescription;
	String[] imageDescriptionArray;
	String[] imagepathArray;
	private int previousPointEnale = 0;

	public NewsIndexAdapter(ArrayList<HashMap<String, Object>> mListItems,
			Context context) {
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
		// TODO Auto-generated method stub
		return mListItems.size();
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if (position == 0) {
			return TYPE_TITLE;
		} else {
			return TYPE_CONTENT;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		int type = getItemViewType(position);
		HashMap<String, Object> map = mListItems.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			if (type == TYPE_TITLE) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_viewpager, null);
				holder.type = TYPE_TITLE;
				holder.bcviewpager = (AutoScrollViewPager) convertView
						.findViewById(R.id.bcviewpager);
				holder.tv_image_description = (TextView) convertView
						.findViewById(R.id.tv_image_description);
				holder.ll_point_group = (LinearLayout) convertView
						.findViewById(R.id.ll_point_group);
				holder.rowdata = map;
			} else {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.news_item_nomal, null);
				holder.type = TYPE_CONTENT;
				holder.cell_news_img = (ImageView) convertView
						.findViewById(R.id.cell_news_img);
				;
				holder.cell_newslist_textview_title = (TextView) convertView
						.findViewById(R.id.cell_newslist_textview_title);
				;
				holder.cell_newslist_textview_type = (TextView) convertView
						.findViewById(R.id.cell_newslist_textview_type);
				;
				holder.cell_newslist_textview_time = (TextView) convertView
						.findViewById(R.id.cell_newslist_textview_time);
				;
				holder.cell_newslist_textview_comments = (TextView) convertView
						.findViewById(R.id.cell_newslist_textview_comments);
				holder.rowdata = map;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder.type == TYPE_TITLE) {
			previousPointEnale = 0;
			holder.ll_point_group.removeAllViews();
			ArrayList<HashMap<String, Object>> head = null;
			head = (ArrayList<HashMap<String, Object>>) map.get("title");
			imageDescriptionArray = new String[head.size()];
			imagepathArray = new String[head.size()];
			for (int i = 0; i < head.size(); i++) {
				imageDescriptionArray[i] = head.get(i).get("News_Title")
						.toString();
				imagepathArray[i] = head.get(i).get("News_Pic")==null?"null":head.get(i).get("News_Pic").toString();
			}
			LayoutParams params;
			// 初始化广告条资源
			for (int i = 0; i < imagepathArray.length; i++) {
				// 初始化广告条正下方的"点"
				View dot = new View(context);
				dot.setBackgroundResource(R.drawable.point_background);
				params = new LayoutParams(10, 10);
				params.leftMargin = 10;
				dot.setLayoutParams(params);
				dot.setEnabled(false);
				holder.ll_point_group.addView(dot);
			}
			TopNewAdapter adapter = new TopNewAdapter(head, imagepathArray,
					context, holder.bcviewpager, true);
			holder.bcviewpager.setAdapter(adapter);
			tvDescription = holder.tv_image_description;
			llPointGroup = holder.ll_point_group;
			if (llPointGroup.getChildCount() > 0)
				llPointGroup.getChildAt(0).setEnabled(true);
			viewpager = holder.bcviewpager;
			viewpager.setOnPageChangeListener(new PosterPageChange());
			viewpager.startAutoScroll(5000);
		} else {
			// 加载内容
			holder.cell_newslist_textview_title.setText(map.get("News_Title")
					+ "");
			holder.cell_newslist_textview_type.setText(map.get("Cate_Name")
					+ "");
			holder.cell_newslist_textview_time.setText(TextUtils
					.FormatDatestr(map.get("News_CreateDate") + ""));
			holder.cell_newslist_textview_comments.setText(map.get("count")
					+ "");
			ImageLoader.getInstance().displayImage(
					SystemApplication.getInstance().getImgUrl()
							+ map.get("News_Pic") + "", holder.cell_news_img,
					options);
		}
		return convertView;
	}

	public ArrayList<HashMap<String, Object>> getmListItems() {
		return mListItems;
	}

	public void setmListItems(ArrayList<HashMap<String, Object>> mListItems) {
		this.mListItems = mListItems;
	}

	public AutoScrollViewPager getViewpager() {
		return viewpager;
	}

	/**
	 * 页面切换事件的监听
	 * 
	 * @author Administrator
	 * 
	 */
	class PosterPageChange implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			if (llPointGroup.getChildCount() > 0) {
				llPointGroup.getChildAt(previousPointEnale).setEnabled(false);
				tvDescription.setText(imageDescriptionArray[position
						% imagepathArray.length]);
				// 消除上一次的状态点
				// 设置当前的状态点“点”
				llPointGroup.getChildAt(position % imagepathArray.length)
						.setEnabled(true);
				previousPointEnale = position % imagepathArray.length;
			}
		}
	}

	public class ViewHolder {
		int type;
		ImageView newsimageview;
		ImageView cell_news_img;
		TextView cell_newslist_textview_title;
		TextView cell_newslist_textview_type;
		TextView cell_newslist_textview_time;
		TextView cell_newslist_textview_comments;
		HashMap<String, Object> rowdata;
		LinearLayout ll_point_group;
		TextView tv_image_description;
		AutoScrollViewPager bcviewpager;
	}

}
