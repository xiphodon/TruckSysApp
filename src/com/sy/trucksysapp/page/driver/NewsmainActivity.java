package com.sy.trucksysapp.page.driver;

import java.util.ArrayList;
import java.util.List;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsmainActivity extends FragmentActivity implements
		OnClickListener {

	private ViewPager mViewPager;
	private FragmentViewPagerAdapter mAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	/**
	 * 顶部的4个TextView
	 */
	private TextView tv_hot;
	private TextView tv_traffic;
	private TextView tv_truck;
	private TextView tv_society;

	/**
	 * Tab的那个引导线
	 */
	private ImageView cursor;
	private int offLength;// 1/4屏幕宽
	private int offLeft;// 1/4屏幕宽
	private int currentPage = 0;// 初始化当前页为0（第一页）

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_news);
		initView();
		initTabLine();
	}
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.activity_news, container, false);
//		return view;
//	}
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
////		fManage = getActivity().getSupportFragmentManager();
//		initView();
//		initTabLine();
//	}
	private void initTabLine() {
		// 获取显示屏信息
		Display display = this.getWindow().getWindowManager().getDefaultDisplay();
		// 得到显示屏宽度
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// 1/3屏幕宽度
		offLength = metrics.widthPixels / 4;
		// 获取控件实例
		cursor = (ImageView) findViewById(R.id.cursor);
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		cursor.measure(w, h);
		int width = cursor.getMeasuredWidth();
		LinearLayout.LayoutParams ll = (android.widget.LinearLayout.LayoutParams) cursor
				.getLayoutParams();
		offLeft = (offLength - width) / 2;
		ll.leftMargin = offLeft;
		cursor.setLayoutParams(ll);
	}

	class FragmentViewPagerAdapter extends PagerAdapter {

		private FragmentManager fragmentManager;

		public FragmentViewPagerAdapter(FragmentManager fragmentManager) {
			super();
			this.fragmentManager = fragmentManager;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFragments.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mFragments.get(position).getView()); // 移出viewpager两边之外的page布局
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			Fragment fragment = mFragments.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				android.support.v4.app.FragmentTransaction ft = fragmentManager
						.beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
				 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
				 */
				fragmentManager.executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}
			return fragment.getView();
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		// 实例化对象
		tv_hot = (TextView) findViewById(R.id.tv_hot);
		tv_hot.setOnClickListener(this);
		tv_traffic = (TextView) findViewById(R.id.tv_traffic);
		tv_traffic.setOnClickListener(this);
		tv_truck = (TextView) findViewById(R.id.tv_truck);
		tv_truck.setOnClickListener(this);
		tv_society = (TextView) findViewById(R.id.tv_society);
		tv_society.setOnClickListener(this);
		mFragments.add(new NewshotFragment());
		mFragments.add(new NewsdefaultFragment("交通"));
		mFragments.add(new NewsdefaultFragment("车辆"));
		mFragments.add(new NewsdefaultFragment("社会"));
		mAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
		// 绑定适配器
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				// 取得该控件的实例
				LinearLayout.LayoutParams ll = (android.widget.LinearLayout.LayoutParams) cursor
						.getLayoutParams();
				if (currentPage == 0 && arg0 == 0) { // 0->1移动(第一页到第二页)
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength + arg1 * offLength);
				} else if (currentPage == 1 && arg0 == 1) { // 1->2移动（第二页到第三页）
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength + arg1 * offLength);
				} else if (currentPage == 2 && arg0 == 2) { // 2->3移动（第二页到第三页）
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength + arg1 * offLength);
				} else if (currentPage == 1 && arg0 == 0) { // 1->0移动（第二页到第一页）
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength - (1 - arg1)
									* offLength);
				} else if (currentPage == 2 && arg0 == 1) { // 2->1移动（第三页到第二页）
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength - (1 - arg1)
									* offLength);
				} else if (currentPage == 3 && arg0 == 2) { // 2->1移动（第三页到第二页）
					ll.leftMargin = offLeft
							+ (int) (currentPage * offLength - (1 - arg1)
									* offLength);
				}
				cursor.setLayoutParams(ll);
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				tv_hot.setTextColor(getResources().getColor(
						R.color.text_color_black));
				tv_traffic.setTextColor(getResources().getColor(
						R.color.text_color_black));
				tv_truck.setTextColor(getResources().getColor(
						R.color.text_color_black));
				tv_society.setTextColor(getResources().getColor(
						R.color.text_color_black));
				mFragments.get(currentPage).onPause();
				if (mFragments.get(position).isAdded()) {
					mFragments.get(position).onResume();
				}
				switch (position) {
				case 0:
					tv_hot.setTextColor(getResources().getColor(R.color.red_bg));
					break;
				case 1:
					tv_traffic.setTextColor(getResources().getColor(
							R.color.red_bg));
					break;
				case 2:
					tv_truck.setTextColor(getResources().getColor(
							R.color.red_bg));
					break;
				case 3:
					tv_society.setTextColor(getResources().getColor(
							R.color.red_bg));
					break;

				default:
					break;
				}
				currentPage = position;
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// 实例化对象
		LinearLayout.LayoutParams ll = (android.widget.LinearLayout.LayoutParams) cursor
				.getLayoutParams();
		switch (arg0.getId()) {
		case R.id.tv_hot:
			ll.leftMargin = offLeft;
			mViewPager.setCurrentItem(0);
			break;
		case R.id.tv_traffic:
			ll.leftMargin = offLeft + offLength;
			mViewPager.setCurrentItem(1);
			break;
		case R.id.tv_truck:
			ll.leftMargin = offLeft + 2 * offLength;
			mViewPager.setCurrentItem(2);
			break;
		case R.id.tv_society:
			ll.leftMargin = offLeft + 3 * offLength;
			mViewPager.setCurrentItem(3);
			break;
		default:
			break;
		}
		cursor.setLayoutParams(ll);

	}
}
