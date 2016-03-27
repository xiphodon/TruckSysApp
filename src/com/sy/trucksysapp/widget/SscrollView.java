package com.sy.trucksysapp.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;


public class SscrollView extends ScrollView {

	public SscrollView(Context context) {
		super(context);
	}

	public SscrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SscrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		// TODO Auto-generated method stub
		return 0;
	}

	

}
