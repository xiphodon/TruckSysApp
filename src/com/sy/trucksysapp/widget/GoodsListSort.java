package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.SortSelectedListener;
import com.sy.trucksysapp.listener.SortSelectedScrolledListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 商品排序处理
 * @author Cool
 *
 */
@SuppressLint("NewApi")
public class GoodsListSort extends RelativeLayout implements SortSelectedScrolledListener{
	private TextView tv_sort_price;
	private TextView tv_sort_sales;
	private TextView tv_sort_focus;
	private TextView tv_sort_putime;
	private TextView[] tvs = new TextView[4];

	private RelativeLayout rl_sort_price;
	private RelativeLayout rl_sort_sales;
	private RelativeLayout rl_sort_focus;
	private RelativeLayout rl_sort_putime;

	private ImageView sortSelected;
	private ImageView priceSelected;
	private ScrollLayout scrollLayout;
	

	private static final int TV_SELECTED_COLOR = R.color.grey_dark;
	private static final int TV_NONE_COLOR = R.color.grey;
	
	private static final int IV_PRICE_NONE = R.drawable.sort_price_none;
	private static final int IV_PRICE_DOWN = R.drawable.sort_price_down;
	private static final int IV_PRICE_UP = R.drawable.sort_price_up;

	private int sortPosition = 1;
	private int pricePosition = 0;
	
	private int space = 0;
	
	private SortSelectedListener sortSelectedListener = null;
	
	public void setSortSelectedListener(SortSelectedListener sortSelectedListener) {
		this.sortSelectedListener = sortSelectedListener;
	}

	public GoodsListSort(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GoodsListSort(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GoodsListSort(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		space = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()/4;
		initView();
	}

	OnClickListener clickListener = new OnClickListener() {
		

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.rl_sort_price:
				if (sortPosition != 0) {
//					setDisable();
					int[] location = new int[2];
					sortSelected.getLocationOnScreen(location);
//					scrollLayout.startSrcoll(-location[0], 0, (sortPosition)*space, 0);
					tvs[0].setTextColor(getContext().getResources().getColor(TV_SELECTED_COLOR));
					tvs[sortPosition].setTextColor(getContext().getResources().getColor(TV_NONE_COLOR));
					sortPosition = 0;
				}
				if (pricePosition == 0 || pricePosition == 1) {
					priceSelected.setImageResource(IV_PRICE_UP);
					pricePosition = 2;
					sortSelectedListener.SortByPriceUp();
				}else{
					priceSelected.setImageResource(IV_PRICE_DOWN);
					pricePosition = 1;
					sortSelectedListener.SortByPriceDown();
				}
				
				break;
			case R.id.rl_sort_sales:
				if (sortPosition != 1) {
//					setDisable();
					int[] location = new int[2];
					sortSelected.getLocationOnScreen(location);
//					scrollLayout.startSrcoll(-location[0], 0, (sortPosition - 1)*space, 0);
					tvs[1].setTextColor(getContext().getResources().getColor(TV_SELECTED_COLOR));
					tvs[sortPosition].setTextColor(getContext().getResources().getColor(TV_NONE_COLOR));
					if (pricePosition != 0) {
						priceSelected.setImageResource(IV_PRICE_NONE);
						pricePosition = 0;
					}
					sortPosition = 1;
					sortSelectedListener.SortBySales();
				}
				break;

			case R.id.rl_sort_focus:
				if (sortPosition != 2) {
//					setDisable();
					int[] location = new int[2];
					sortSelected.getLocationOnScreen(location);
//					scrollLayout.startSrcoll(-location[0], 0, (sortPosition - 2)*space, 0);
					tvs[2].setTextColor(getContext().getResources().getColor(TV_SELECTED_COLOR));
					tvs[sortPosition].setTextColor(getContext().getResources().getColor(TV_NONE_COLOR));
					if (pricePosition != 0) {
						priceSelected.setImageResource(IV_PRICE_NONE);
						pricePosition = 0;
					}
					sortPosition = 2;
					sortSelectedListener.SortByFocus();
				}
				break;

			case R.id.rl_sort_putime:
//				if (sortPosition != 3) {
//					setDisable();
					int[] location = new int[2];
					sortSelected.getLocationOnScreen(location);
//					scrollLayout.startSrcoll(-location[0], 0, (sortPosition - 3)*space, 0);
					tvs[3].setTextColor(getContext().getResources().getColor(TV_SELECTED_COLOR));
					if(sortPosition!=3){
						tvs[sortPosition].setTextColor(getContext().getResources().getColor(TV_NONE_COLOR));
					}
					if (pricePosition != 0) {
						priceSelected.setImageResource(IV_PRICE_NONE);
						pricePosition = 0;
					}
					sortPosition = 3;
					sortSelectedListener.SortByPutime();
//				}
				break;

			default:
				break;
			}
		}
	};

	private void initView() {
		tv_sort_price = (TextView) findViewById(R.id.tv_sort_price);
		tv_sort_sales = (TextView) findViewById(R.id.tv_sort_sales);
		tv_sort_focus = (TextView) findViewById(R.id.tv_sort_focus);
		tv_sort_putime = (TextView) findViewById(R.id.tv_sort_putime);

		tvs[0] = tv_sort_price;
		tvs[1] = tv_sort_sales;
		tvs[2] = tv_sort_focus;
		tvs[3] = tv_sort_putime;
		tvs[sortPosition].setTextColor(getContext().getResources().getColor(TV_SELECTED_COLOR));
		
		rl_sort_price = (RelativeLayout) findViewById(R.id.rl_sort_price);
		rl_sort_sales = (RelativeLayout) findViewById(R.id.rl_sort_sales);
		rl_sort_focus = (RelativeLayout) findViewById(R.id.rl_sort_focus);
		rl_sort_putime = (RelativeLayout) findViewById(R.id.rl_sort_putime);

		sortSelected = (ImageView) findViewById(R.id.iv_sort_selected);
		priceSelected = (ImageView) findViewById(R.id.iv_sort_price_selected);
		scrollLayout = (ScrollLayout) findViewById(R.id.sort_selected_layout);
		
		scrollLayout.setSortSelectedScrolledListener(this);
		sortSelected.setLayoutParams(new LinearLayout.LayoutParams(space, 8));
		
		rl_sort_price.setOnClickListener(clickListener);
		rl_sort_sales.setOnClickListener(clickListener);
		rl_sort_focus.setOnClickListener(clickListener);
		rl_sort_putime.setOnClickListener(clickListener);
		
		if (sortPosition!=0) {
//			scrollLayout.scrollTo(-(space*sortPosition), 0);
			postInvalidate();
		}
	}

	private void setDisable() {
		rl_sort_price.setClickable(false);
		rl_sort_sales.setClickable(false);
		rl_sort_focus.setClickable(false);
		rl_sort_putime.setClickable(false);
	}

	public void setEnable() {
		rl_sort_price.setClickable(true);
		rl_sort_sales.setClickable(true);
		rl_sort_focus.setClickable(true);
		rl_sort_putime.setClickable(true);
	}

	@Override
	public void isFinished() {
//		setEnable();
	}
}
