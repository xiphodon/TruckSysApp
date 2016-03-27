package com.sy.trucksysapp.widget;


import com.sy.trucksysapp.listener.SortSelectedScrolledListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollLayout extends LinearLayout {

	private Scroller mScroller;
	private SortSelectedScrolledListener listener = null;

	public void setSortSelectedScrolledListener(SortSelectedScrolledListener listener) {
		this.listener = listener;
	}

	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
	}

	public void startSrcoll(int startX,int startY,int dx,int dy) {
		mScroller.startScroll(startX, startY, dx, dy, 500);
		invalidate();
	}
	
	
	public boolean isSrcoll(){
		return !mScroller.isFinished();
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.isFinished()) {
			listener.isFinished();
		}
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

}
