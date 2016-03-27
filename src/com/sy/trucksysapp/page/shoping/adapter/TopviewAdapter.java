package com.sy.trucksysapp.page.shoping.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.TopItemClickListener;
import com.sy.trucksysapp.photoview.ImagePagerActivity;
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
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class TopviewAdapter extends PagerAdapter {

	String[] imagepathArray;
	Context context;
	DisplayImageOptions options;
	AutoScrollViewPager bcviewpager;
	private boolean flag;
	private TopItemClickListener topItemClickListener=null;

	public TopviewAdapter(String[] imagepathArray, Context context,
			AutoScrollViewPager bcviewpager, boolean flag) {
		super();
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
		ImageView imageView = new ImageView(context);
		imageView.setAdjustViewBounds(true);
		// TODO 调整图片大小
		imageView.setScaleType(ScaleType.FIT_XY);
		android.view.ViewGroup.LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(params);
		if (imagepathArray.length > 0) {
			ImageLoader.getInstance().displayImage(
					imagepathArray[position % imagepathArray.length],
					imageView, options);
			((ViewPager) container).addView(imageView);
			imageView.setOnClickListener(new PosterClickListener(position
					% imagepathArray.length));
		}
		return imageView;

	}

	public TopItemClickListener getTopItemClickListener() {
		return topItemClickListener;
	}

	public void setTopItemClickListener(TopItemClickListener topItemClickListener) {
		this.topItemClickListener = topItemClickListener;
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
				Intent intent = new Intent(context, ImagePagerActivity.class);
				// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS,
						imagepathArray);
				intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
				context.startActivity(intent);
			}else{
				
			}
			if(topItemClickListener!=null){
				topItemClickListener.onClick(position);
			}
			
			// startActivity(new Intent(context,
			// GuideImgAct.class).putExtra("url",
			// guideList.get(position).getGo_url()));
			// bcviewpager.stopAutoScroll();
			// Toast.makeText(context, "position---->" + position, 0).show();
		}
	}

}
