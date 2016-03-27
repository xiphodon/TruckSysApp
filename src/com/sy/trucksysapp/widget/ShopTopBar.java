package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShopTopBar extends RelativeLayout {

	private ShoptoolSelectedListener selectedListener;
	private View[] mBtnTabs = new View[4];
	private View[] line = new View[3];

	public ShopTopBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ShopTopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShopTopBar(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initView();
	}

	public void hidden(int index) {
		if (index != 0) {
			((RelativeLayout) mBtnTabs[index]).setVisibility(View.GONE);
			if(index<line.length){
			 line[index].setVisibility(View.GONE);
			}
			
		}
	}

	public void setheadtext(String text1, String text2, String text3,
			String text4) {
		((TextView) mBtnTabs[0].findViewById(R.id.tv_text)).setText(text1);
		((TextView) mBtnTabs[1].findViewById(R.id.tv_text)).setText(text2);
		((TextView) mBtnTabs[2].findViewById(R.id.tv_text)).setText(text3);
		((TextView) mBtnTabs[3].findViewById(R.id.tv_text)).setText(text4);
	}

	private void initView() {
		line[0] = findViewById(R.id.line_sel2);
		line[1] = findViewById(R.id.line_sel3);
		line[2] = findViewById(R.id.line_sel4);
		mBtnTabs[0] = (RelativeLayout) findViewById(R.id.rl_sel1);
		mBtnTabs[1] = (RelativeLayout) findViewById(R.id.rl_sel2);
		mBtnTabs[2] = (RelativeLayout) findViewById(R.id.rl_sel3);
		mBtnTabs[3] = (RelativeLayout) findViewById(R.id.rl_sel4);
		((TextView) mBtnTabs[0].findViewById(R.id.tv_text)).setText("区域");
		((TextView) mBtnTabs[1].findViewById(R.id.tv_text)).setText("排序");
		((TextView) mBtnTabs[2].findViewById(R.id.tv_text)).setText("类型");
		((TextView) mBtnTabs[3].findViewById(R.id.tv_text)).setText("服务");
		mBtnTabs[0].setOnClickListener(clickListener);
		mBtnTabs[1].setOnClickListener(clickListener);
		mBtnTabs[2].setOnClickListener(clickListener);
		mBtnTabs[3].setOnClickListener(clickListener);

	}

	public void setSelectedListener(ShoptoolSelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub bt_line
			for (int i = 0; i < mBtnTabs.length; i++) {
				((TextView) mBtnTabs[i].findViewById(R.id.tv_text))
						.setTextColor(getContext().getResources().getColor(
								R.color.grey));
			}

			if (view == mBtnTabs[0]) {
				((TextView) mBtnTabs[0].findViewById(R.id.tv_text))
						.setTextColor(getContext().getResources().getColor(
								R.color.list_item_text_pressed_bg));
				if (selectedListener != null) {
					selectedListener.areaclick(mBtnTabs[0]);
				}
			}
			if (view == mBtnTabs[1]) {
				((TextView) mBtnTabs[1].findViewById(R.id.tv_text))
						.setTextColor(getContext().getResources().getColor(
								R.color.list_item_text_pressed_bg));
				if (selectedListener != null) {

					selectedListener.sortclick(mBtnTabs[1]);
				}
			}
			if (view == mBtnTabs[2]) {
				((TextView) mBtnTabs[2].findViewById(R.id.tv_text))
						.setTextColor(getContext().getResources().getColor(
								R.color.list_item_text_pressed_bg));
				if (selectedListener != null) {
					selectedListener.typeclick(mBtnTabs[2]);
				}
			}
			if (view == mBtnTabs[3]) {
				((TextView) mBtnTabs[3].findViewById(R.id.tv_text))
						.setTextColor(getContext().getResources().getColor(
								R.color.list_item_text_pressed_bg));
				if (selectedListener != null) {
					selectedListener.serviceclick(mBtnTabs[3]);
				}
			}
		}
	};
}
