package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GoodsListTopBar extends RelativeLayout {
	private TextView tv_sel_area;
	private TextView tv_sort_price;
	private TextView tv_term1;
	private TextView tv_term2;
	private TextView tv_select;
	private TextView[] tvs = new TextView[5];
	
	private RelativeLayout rl_sel_area;
	private RelativeLayout rl_sort_price;
	private RelativeLayout rl_term1;
	private RelativeLayout rl_term2;
	private RelativeLayout rl_select;
	private View line_rl_select,line_rl_price,line_rl_term2;

	private ImageView priceSelected;
	private int sortPosition = 0;
	private int pricePosition = 0;

	private static final int TV_SELECTED_COLOR = R.color.grey_dark;
	private static final int TV_NONE_COLOR = R.color.grey;

	private static final int IV_PRICE_NONE = R.drawable.sort_price_none;
	private static final int IV_PRICE_DOWN = R.drawable.sort_price_down;
	private static final int IV_PRICE_UP = R.drawable.sort_price_up;
	private SelectedListener selectedListener = null;

	private String defaluttext1 = null, defaluttext2 = null, defaluttext3 = null;

	public GoodsListTopBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GoodsListTopBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GoodsListTopBar(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initView();
	}
    /**
     * 隐藏筛选
     */
	public void setSelectVisiblenone(){
		line_rl_select.setVisibility(View.GONE);
		rl_select.setVisibility(View.GONE);
	}
	/**
	 * 隐藏价格
	 */
	public void  setPriceVisiblenone(){
		line_rl_price.setVisibility(View.GONE);
		rl_sort_price.setVisibility(View.GONE);
	}
	/**
	 * 隐藏人气
	 */
	public void  setterm2Visiblenone(){
		line_rl_term2.setVisibility(View.GONE);
		rl_term2.setVisibility(View.GONE);
	}
	/***
	 * 设置第2个与第三个的文本内容
	 * 
	 * @param text1
	 * @param text2
	 */
	public void setTopText234(String text1, String text2, String text3) {
		if (defaluttext1 == null || defaluttext1.equals("")) {
			defaluttext1 = text1;
		}
		if (defaluttext2 == null || defaluttext2.equals("")) {
			defaluttext2 = text2;
		}
		if (defaluttext3 == null || defaluttext3.equals("")) {
			defaluttext3 = text3;
		}
		if (!text1.equals("") && text1 != null) {
			tv_term1.setText(text1);
		}
		if (!text2.equals("") && text2 != null) {
			tv_term2.setText(text2);
		}
		if (!text3.equals("") && text3 != null) {
			tv_select.setText(text3);
		}
	}

	/***
	 * 设置第2个与第三个的文本内容
	 * 
	 * @param text1
	 * @param text2
	 */
	public void setDefaultText23() {
		tv_term1.setText(defaluttext1);
		tv_term2.setText(defaluttext2);
		tv_select.setText(defaluttext3);
	}

	public String[] getTopText234() {
		String[] ss = { defaluttext1, defaluttext2,defaluttext3 };
		return ss;
	}

	/****
	 * 0不按照价格排序1降序2升序
	 * 
	 * @return
	 */
	public int getSortpricePosition() {

		return pricePosition;
	}

	private void initView() {
		tv_sel_area = (TextView) findViewById(R.id.tv_sel_area);
		tv_sort_price = (TextView) findViewById(R.id.tv_sort_price);
		tv_term1 = (TextView) findViewById(R.id.tv_term1);
		tv_term2 = (TextView) findViewById(R.id.tv_term2);
		tv_select = (TextView) findViewById(R.id.tv_select);
		tvs[0] = tv_sel_area;
		tvs[1] = tv_sort_price;
		tvs[2] = tv_term1;
		tvs[3] = tv_term2;
		tvs[4] = tv_select;
		line_rl_select = (View) findViewById(R.id.line_rl_select);
		line_rl_price = (View) findViewById(R.id.line_rl_price);
		line_rl_term2 = (View) findViewById(R.id.line_rl_term2);
//		tvs[sortPosition].setTextColor(getContext().getResources().getColor(
//				TV_SELECTED_COLOR));
		
		rl_sel_area= (RelativeLayout) findViewById(R.id.rl_sel_area);
		rl_sort_price = (RelativeLayout) findViewById(R.id.rl_sort_price);
		rl_term1 = (RelativeLayout) findViewById(R.id.rl_term1);
		rl_term2 = (RelativeLayout) findViewById(R.id.rl_term2);
		rl_select = (RelativeLayout) findViewById(R.id.rl_select);

		priceSelected = (ImageView) findViewById(R.id.iv_sort_price_selected);
		priceSelected.setImageResource(IV_PRICE_NONE);
		rl_sel_area.setOnClickListener(clickListener);
		rl_sort_price.setOnClickListener(clickListener);
		rl_term1.setOnClickListener(clickListener);
		rl_term2.setOnClickListener(clickListener);
		rl_select.setOnClickListener(clickListener);

	}

	public void setSelectedListener(SelectedListener selectedListener) {
		this.selectedListener = selectedListener;
	}

	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.rl_sel_area:
				tvs[0].setTextColor(getContext().getResources().getColor(
						TV_SELECTED_COLOR));
				if (sortPosition != 0) {
					tvs[sortPosition].setTextColor(getContext().getResources()
							.getColor(TV_NONE_COLOR));
				}
				sortPosition = 0;
				selectedListener.clickselArea();
				break;
			
			case R.id.rl_sort_price:
				tvs[1].setTextColor(getContext().getResources().getColor(
						TV_SELECTED_COLOR));
				if (sortPosition != 1) {
					tvs[sortPosition].setTextColor(getContext().getResources()
							.getColor(TV_NONE_COLOR));
				}
				sortPosition = 1;
				if (pricePosition == 0 || pricePosition == 1) {
					priceSelected.setImageResource(IV_PRICE_UP);
					pricePosition = 2;
					selectedListener.SortByPriceUp();
				} else {
					priceSelected.setImageResource(IV_PRICE_DOWN);
					pricePosition = 1;
					selectedListener.SortByPriceDown();
				}
				break;
			case R.id.rl_term1:
				tvs[2].setTextColor(getContext().getResources().getColor(
						TV_SELECTED_COLOR));
				if (sortPosition != 2) {
					tvs[sortPosition].setTextColor(getContext().getResources()
							.getColor(TV_NONE_COLOR));
				}
				sortPosition = 2;
				selectedListener.clickterm1();
				break;

			case R.id.rl_term2:
				tvs[3].setTextColor(getContext().getResources().getColor(
						TV_SELECTED_COLOR));
				if (sortPosition != 3) {
					tvs[sortPosition].setTextColor(getContext().getResources()
							.getColor(TV_NONE_COLOR));
				}
				sortPosition = 3;
				selectedListener.clickterm2();
				break;

			case R.id.rl_select:
				tvs[4].setTextColor(getContext().getResources().getColor(
						TV_SELECTED_COLOR));
				if (sortPosition != 4) {
					tvs[sortPosition].setTextColor(getContext().getResources()
							.getColor(TV_NONE_COLOR));
				}
				sortPosition = 4;
				selectedListener.clickselect();
				break;

			default:
				break;
			}
		}
	};

}
