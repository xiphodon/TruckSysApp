package com.sy.trucksysapp.user;

import org.apache.cordova.api.LOG;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
/**
 * 我的订单页面
 * @author lxs
 *
 */
public class ShoplistActivity extends FragmentActivity implements
		OnClickListener {

	OrderFrament all;
	OrderFrament unpay;
	OrderFrament paied;
	OrderFrament unconfirm;
	OrderFrament unparse;
	TextView tv_all, tv_unpay, tv_paied, tv_unconfirm, tv_unparse;
	ImageView cursor;
	private int offLength;// 1/4屏幕宽
	private int offLeft;// 1/4屏幕宽
	private int currentPage = 0;// 初始化当前页为0（第一页）

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoplist);
		try {
			currentPage = getIntent().getExtras().getInt("currentPage");
		} catch (Exception ex) {

		}
		initview();
	}

	public void back(View v) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void initview() {
		// TODO Auto-generated method stub
		all = new OrderFrament(0);
		unpay = new OrderFrament(1);
		paied = new OrderFrament(2);
		unconfirm = new OrderFrament(3);
		unparse = new OrderFrament(4);
		tv_all = (TextView) findViewById(R.id.tv_all);
		tv_all.setOnClickListener(this);
		tv_unpay = (TextView) findViewById(R.id.tv_unpay);
		tv_unpay.setOnClickListener(this);
		tv_paied = (TextView) findViewById(R.id.tv_paied);
		tv_paied.setOnClickListener(this);
		tv_unconfirm = (TextView) findViewById(R.id.tv_unconfirm);
		tv_unconfirm.setOnClickListener(this);
		tv_unparse = (TextView) findViewById(R.id.tv_unparse);
		tv_unparse.setOnClickListener(this);
		initTabLine();

	}

	private void initTabLine() {
		// 获取显示屏信息
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		// 得到显示屏宽度
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		// 1/3屏幕宽度
		offLength = metrics.widthPixels / 5;
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
		ll.leftMargin = offLeft +currentPage* offLength;
		cursor.setLayoutParams(ll);
		setCurrentItem(currentPage);
	}

	public void setCurrentItem(int position) {
		android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		tv_all.setTextColor(getResources().getColor(R.color.text_color_black));
		tv_unpay.setTextColor(getResources().getColor(R.color.text_color_black));
		tv_paied.setTextColor(getResources().getColor(R.color.text_color_black));
		tv_unconfirm.setTextColor(getResources().getColor(
				R.color.text_color_black));
		tv_unparse.setTextColor(getResources().getColor(
				R.color.text_color_black));
		switch (position) {
		case 0:
			tv_all.setTextColor(getResources().getColor(R.color.red_bg));
			transaction.replace(R.id.id_frament, all);
			transaction.addToBackStack(null);
			break;
		case 1:
			tv_unpay.setTextColor(getResources().getColor(R.color.red_bg));
			transaction.replace(R.id.id_frament, unpay);
			transaction.addToBackStack(null);
			break;
		case 2:
			tv_paied.setTextColor(getResources().getColor(R.color.red_bg));
			transaction.replace(R.id.id_frament, paied);
			transaction.addToBackStack(null);
			break;
		case 3:
			tv_unconfirm.setTextColor(getResources().getColor(R.color.red_bg));
			transaction.replace(R.id.id_frament, unconfirm);
			transaction.addToBackStack(null);
			break;
		case 4:
			tv_unparse.setTextColor(getResources().getColor(R.color.red_bg));
			transaction.replace(R.id.id_frament, unparse);
			transaction.addToBackStack(null);
			break;

		default:
			break;
		// 提交修改
		}
		transaction.commit();
		// currentPage = position;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams ll = (android.widget.LinearLayout.LayoutParams) cursor
				.getLayoutParams();
		switch (arg0.getId()) {
		// tv_all, tv_unpay, tv_paied, tv_unconfirm, tv_unparse
		case R.id.tv_all:
			ll.leftMargin = offLeft;
			setCurrentItem(0);
			break;
		case R.id.tv_unpay:
			ll.leftMargin = offLeft + offLength;
			setCurrentItem(1);
			break;
		case R.id.tv_paied:
			ll.leftMargin = offLeft + 2 * offLength;
			setCurrentItem(2);
			break;
		case R.id.tv_unconfirm:
			ll.leftMargin = offLeft + 3 * offLength;
			setCurrentItem(3);
			break;
		case R.id.tv_unparse:
			ll.leftMargin = offLeft + 4 * offLength;
			setCurrentItem(4);
			break;

		default:
			break;
		}
		cursor.setLayoutParams(ll);
	}
	
}
