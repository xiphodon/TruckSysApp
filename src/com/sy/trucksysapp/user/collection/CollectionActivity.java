package com.sy.trucksysapp.user.collection;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.widget.indicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class CollectionActivity extends FragmentActivity {
	public final static String[] content = new String[] { "轮胎", "润滑油", "轮辋",
			"内胎/垫带", "维修救援", "餐饮", "住宿", "加油加气", "资讯" };

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.collection_tab);
		CollectionDetailFrament[] fragments = new CollectionDetailFrament[content.length];
		for (int i = 0; i < fragments.length; i++) {
			fragments[i] = new CollectionDetailFrament(i);
		}
		TabAdapter adapter = new TabAdapter(getSupportFragmentManager(),
				content, fragments);

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setOffscreenPageLimit(0);
		pager.setAdapter(adapter);

		final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	class TabAdapter extends PagerAdapter {
		private String[] content = null;
		private Fragment[] fragments;
		private FragmentManager fragmentManager;

		public TabAdapter(FragmentManager fm, String[] content,
				Fragment[] fragments) {
			super();
			this.fragmentManager = fm;
			this.content = content;
			this.fragments = fragments;
		}

		public Fragment getItem(int position) {

			return fragments[position];
		}

		public CharSequence getPageTitle(int position) {
			return content[position % content.length].toUpperCase();
		}

		public int getCount() {
			return content.length;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(fragments[position].getView()); // 移出viewpager两边之外的page布局
		}

		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = fragments[position];
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

}
