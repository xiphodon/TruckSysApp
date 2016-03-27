package com.sy.trucksysapp.page.order;

import java.util.ArrayList;
import java.util.List;

import com.sy.trucksysapp.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 货运信息
 * 
 * @author Administrator
 * 
 */
@SuppressLint("ValidFragment")
public class OrderFragment extends Fragment implements OnCheckedChangeListener,
		OnPageChangeListener, OnClickListener {

	private FragmentManager fManage;

	private ViewPager mViewPager;
	private RadioGroup select_rg;

	private SearchActivity searchActivity;
	private ListActivity listActivity;

	private int width;
	private RelativeLayout.LayoutParams params;
	private View v_line;

	private ArrayList<Fragment> list;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_frame, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		fManage = getActivity().getSupportFragmentManager();
		initViews();
		initLine();
	}

	/**
	 * 初始化页面
	 */
	private void initViews() {
		mViewPager = (ViewPager) getView().findViewById(R.id.viewPager1);
		list = new ArrayList<Fragment>();
		listActivity = new ListActivity();
		searchActivity = new SearchActivity();
		list.add(listActivity);
		list.add(searchActivity);
		mViewPager.setAdapter(new MPaperAdapter(fManage, list));
		select_rg = (RadioGroup) getView().findViewById(R.id.rg_selsect);
		select_rg.setOnCheckedChangeListener(this);
		mViewPager.setOnPageChangeListener(this);

	}

	/**
	 * 初始化线
	 */
	@SuppressWarnings("deprecation")
	private void initLine() {
		v_line = getView().findViewById(R.id.v_line);
		float scale = this.getResources().getDisplayMetrics().density;
		int height = (int) (4 * scale + 0.5f);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		width = display.getWidth() / select_rg.getChildCount();
		params = new RelativeLayout.LayoutParams(width, height);
		v_line.setLayoutParams(params);
	}

	public void onClick(View arg0) {

	}

	public void onPageScrollStateChanged(int arg0) {

	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
		int leftPx = (int) (width * (arg0 + arg1));
		params.setMargins(leftPx, 0, 0, 0);
		v_line.setLayoutParams(params);

	}

	public void onPageSelected(int arg0) {
		int checkid = 0;
		switch (arg0) {
		case 0:
			checkid = R.id.rb_list;
			break;
		case 1:
			checkid = R.id.rb_search;
			break;
		}
		select_rg.check(checkid);

	}

	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if (arg1 == R.id.rb_list) {
			mViewPager.setCurrentItem(0);
		} else if (arg1 == R.id.rb_search) {
			mViewPager.setCurrentItem(1);
		}
	}

	class MPaperAdapter extends FragmentPagerAdapter {

		private List<Fragment> list;

		public MPaperAdapter(FragmentManager fm) {
			super(fm);
		}

		public MPaperAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}

		public int getCount() {
			return list.size();
		}
	}
}
