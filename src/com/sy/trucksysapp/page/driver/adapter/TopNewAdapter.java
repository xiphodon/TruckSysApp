package com.sy.trucksysapp.page.driver.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.driver.NewsDetailActivity;
import com.sy.trucksysapp.widget.AutoScrollViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class TopNewAdapter extends PagerAdapter {

	ArrayList<HashMap<String, Object>> head;
	String[] imagepathArray;
	Context context;
	DisplayImageOptions options;
	AutoScrollViewPager bcviewpager;
	private boolean flag;

	public TopNewAdapter(ArrayList<HashMap<String, Object>> head,
			String[] imagepathArray, Context context,
			AutoScrollViewPager bcviewpager, boolean flag) {
		super();
		this.head = head;
		this.context = context;
		this.imagepathArray = imagepathArray;
		this.bcviewpager = bcviewpager;
		this.flag = flag;
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
		return Integer.MAX_VALUE;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		((ViewPager) view).removeView((ImageView) object);

	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		if (imagepathArray != null && imagepathArray.length > 0) {
			ImageView imageView = new ImageView(context);
			imageView.setAdjustViewBounds(true);
			// TODO 调整图片大小
			imageView.setScaleType(ScaleType.FIT_XY);
			android.view.ViewGroup.LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			imageView.setLayoutParams(params);
			ImageLoader.getInstance().displayImage(
					SystemApplication.getInstance().getImgUrl()
							+ imagepathArray[position % imagepathArray.length],
					imageView, options);
			((ViewPager) container).addView(imageView);
			imageView.setOnClickListener(new PosterClickListener(position
					% imagepathArray.length));
			return imageView;
		} else
			return null;

	}

	/**
	 * 图片点击事件的监听
	 * 
	 * @author Administrator
	 * 
	 */
	class PosterClickListener implements OnClickListener {

		private int position;

		public PosterClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (flag) {
				HashMap<String, Object> map = head.get(position);
				if (map != null) {
					Intent intent = new Intent(context,
							NewsDetailActivity.class);
					intent.putExtra("rowdata", map);
					context.startActivity(intent);
				}
			}
		}
	}

}
