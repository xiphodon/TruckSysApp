package com.sy.trucksysapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.CircleMenuClick;

public class CircleMenuLayout extends ViewGroup {

	/**
	 * layout的半径
	 */
	private int mRadius;

	private float mMaxChildDimesionRadio = 1 / 4f;
	private float mCenterItemDimesionRadio = 1 / 3f;

	private LayoutInflater mInflater;

	private double mStartAngle = 55;

	private String[] mItemTexts = new String[] { "货运信息", "停车住宿", "加油加气",
			"维修救援", "交流咨询" };
	private int[] mItemImgs = new int[] { R.drawable.home_rescue_seletor,
			R.drawable.home_freight_seletor, R.drawable.home_oil_seletor, R.drawable.home_park_seletor,
			R.drawable.home_new_seletor };

	private int mTouchSlop;

	/**
	 * 加速度检测
	 */

	private float mDownAngle;
	private float mTmpAngle;
	private long mDownTime;
	private boolean isFling;
	private CircleMenuClick clickview;

	public void setclick(CircleMenuClick clickview) {
		this.clickview = clickview;
	}

	public CircleMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mInflater = LayoutInflater.from(context);

		for (int i = 0; i < mItemImgs.length; i++) {
			final int j = i;
			View view = mInflater.inflate(R.layout.turnpalte_inner_view, this,
					false);
			ImageView iv = (ImageView) view
					.findViewById(R.id.id_circle_menu_item_image);
			TextView tv = (TextView) view
					.findViewById(R.id.id_circle_menu_item_text);
			iv.setImageResource(mItemImgs[i]);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					clickview.clickview(mItemImgs[j]);
				}
			});
			addView(view);
		}

		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getSuggestedMinimumWidth(),
				getSuggestedMinimumHeight());
		// 获得半径
		mRadius = Math.max(getWidth(), getHeight());

		final int count = getChildCount();
		int childSize = (int) (mRadius * mMaxChildDimesionRadio);
		int childMode = MeasureSpec.EXACTLY;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == GONE) {
				continue;
			}
			int makeMeasureSpec = -1;
			if (child.getId() == R.id.id_circle_menu_item_center) {
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(
						(int) (mRadius * mCenterItemDimesionRadio), childMode);
			} else {
				makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize,
						childMode);
			}
			child.measure(makeMeasureSpec, makeMeasureSpec);
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		int layoutWidth = r - l;
		int layoutHeight = b - t;

		int layoutRadius = Math.max(layoutWidth, layoutHeight);

		// Laying out the child views
		final int childCount = getChildCount();
		int left, top;
		int radius = (int) (layoutRadius * mMaxChildDimesionRadio);

		float angleDelay = 360 / (getChildCount() - 1);
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);

			if (child.getId() == R.id.id_circle_menu_item_center)
				continue;

			if (child.getVisibility() == GONE) {
				continue;
			}

			mStartAngle %= 360;

			float tmp = layoutRadius * 1f / 3 - 1 / 22f * layoutRadius;

			left = layoutRadius
					/ 2
					+ (int) Math.round(tmp
							* Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
							* radius);
			top = layoutRadius
					/ 2
					+ (int) Math.round(tmp
							* Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
							* radius);

			if(i==0||i==1){
				child.layout(left, top-10, left + radius, top + radius);
			}else if(i==2||i==4){
				child.layout(left, top-35, left + radius, top + radius);
			}else{
				child.layout(left, top, left + radius, top + radius);
			}
			

			mStartAngle += angleDelay;
		}

		View cView = findViewById(R.id.id_circle_menu_item_center);
		cView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clickview.centerClick();
//				Toast.makeText(getContext(),
//						"you can do something just like ccb  ",
//						Toast.LENGTH_SHORT).show();
			}
		});
		// Log.e("TAG",
		// cView.getMeasuredWidth() + " , " + cView.getMeasuredWidth());
		int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
		int cr = cl + cView.getMeasuredWidth();
		cView.layout(cl, cl, cr, cr);

	}

	// private float mLastX;
	// private float mLastY;

	// private FlingRunnable mFlingRunnable;

	// @Override
	// public boolean onTouchEvent(MotionEvent event)
	// {
	// }

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent event)
	// {
	// float x = event.getX();
	// float y = event.getY();
	//
	// switch (event.getAction())
	// {
	// case MotionEvent.ACTION_DOWN:
	// mLastX = x;
	// mLastY = y;
	// mDownAngle = getAngle(x, y);
	// mDownTime = System.currentTimeMillis();
	// mTmpAngle = 0;
	//
	// if (isFling)
	// {
	// removeCallbacks(mFlingRunnable);
	// isFling = false;
	// return true ;
	// }
	//
	// break;
	// case MotionEvent.ACTION_MOVE:
	//
	// float start = getAngle(mLastX, mLastY);
	// float end = getAngle(x, y);
	//
	// // Log.e("TAG", "start = " + start + " , end =" + end);
	// if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4)
	// {
	// mStartAngle += end - start;
	// mTmpAngle += end - start;
	// } else
	// {
	// mStartAngle += start - end;
	// mTmpAngle += start - end;
	// }
	// // rotateButtons((float) (mStartAngle - currentAngle));
	// requestLayout();
	//
	// mLastX = x;
	// mLastY = y;
	//
	// break;
	// case MotionEvent.ACTION_UP:
	//
	// float anglePrMillionSecond = mTmpAngle * 1000
	// / (System.currentTimeMillis() - mDownTime);
	//
	// Log.e("TAG", anglePrMillionSecond + " , mTmpAngel = " + mTmpAngle);
	//
	// if (Math.abs(anglePrMillionSecond) > 230 && !isFling)
	// {
	// post(mFlingRunnable = new FlingRunnable(anglePrMillionSecond));
	//
	// }
	// if(Math.abs(anglePrMillionSecond) >230 || isFling)
	// {
	// return true ;
	// }
	//
	// break;
	// }
	// return super.dispatchTouchEvent(event);
	// }
	//
	// private float getAngle(float xTouch, float yTouch)
	// {
	// double x = xTouch - (mRadius / 2d);
	// double y = yTouch - (mRadius / 2d);
	// return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
	// }
	//
	// private int getQuadrant(float x, float y)
	// {
	// int tmpX = (int) (x - mRadius / 2);
	// int tmpY = (int) (y - mRadius / 2);
	// if (tmpX >= 0)
	// {
	// return tmpY >= 0 ? 4 : 1;
	// } else
	// {
	// return tmpY >= 0 ? 3 : 2;
	// }
	//
	// }
	//
	// private class FlingRunnable implements Runnable
	// {
	//
	// private float velocity;
	//
	// public FlingRunnable(float velocity)
	// {
	// this.velocity = velocity;
	// }
	//
	// public void run()
	// {
	// if ((int) Math.abs(velocity) < 20)
	// {
	// isFling = false;
	// return;
	// }
	// isFling = true;
	// // rotateButtons(velocity / 75);
	// mStartAngle += (velocity / 30);
	// velocity /= 1.0666F;
	// postDelayed(this, 30);
	// requestLayout();
	// Log.e("TAG", velocity + "");
	//
	// }
	// }

}
