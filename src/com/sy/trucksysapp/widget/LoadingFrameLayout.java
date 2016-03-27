package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoadingFrameLayout extends FrameLayout {
	View content;
	LinearLayout li_loading;
	private ImageView reLoadImage;
	TextView protext;
	public boolean hasadd = false;

	public LoadingFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content = inflater.inflate(R.layout.fullsreen_loading_progress, null);
		li_loading = (LinearLayout) content.findViewById(R.id.li_loading);
		reLoadImage=(ImageView) content.findViewById(R.id.reLoadImage);
		protext = (TextView) content.findViewById(R.id.protext);
		// TODO Auto-generated constructor stub
	}

	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	public void show() {
		setloading();
		protext.setText("正在努力获取数据...");
	}

	public void show(String text) {
		setloading();
		protext.setText(text);
	}

	public void setEmpty(){
		reLoadImage.setImageResource(R.drawable.base_empty_view);
		if(!hasadd){
			this.addView(content, this.getChildCount());
			hasadd = true;
		}
		li_loading.setVisibility(View.INVISIBLE); 
		reLoadImage.setVisibility(View.VISIBLE);
	}
	
	public void setFail(){
		reLoadImage.setImageResource(R.drawable.base_fail_view);
		if(!hasadd){
			this.addView(content, this.getChildCount());
			hasadd = true;
		}
		li_loading.setVisibility(View.INVISIBLE);
		reLoadImage.setVisibility(View.VISIBLE);
	}
	public void setloading(){
		if(!hasadd){
			this.addView(content, this.getChildCount());
			hasadd = true;
		}
		reLoadImage.setVisibility(View.INVISIBLE);
		li_loading.setVisibility(View.VISIBLE);
	}
	
	public void setLoadingText(String text) {
		protext.setText(text);
	}

	public void dismiss() {
		if (hasadd) {
			setloading();
			this.removeView(content);
			hasadd = false;
		}
	}

	public LoadingFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content = inflater.inflate(R.layout.fullsreen_loading_progress, null);
		li_loading = (LinearLayout) content.findViewById(R.id.li_loading);
		reLoadImage=(ImageView) content.findViewById(R.id.reLoadImage);
		protext = (TextView) content.findViewById(R.id.protext);
		// TODO Auto-generated constructor stub
	}

	public LoadingFrameLayout(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		content = inflater.inflate(R.layout.fullsreen_loading_progress, null);
		li_loading = (LinearLayout) content.findViewById(R.id.li_loading);
		reLoadImage=(ImageView) content.findViewById(R.id.reLoadImage);
		protext = (TextView) content.findViewById(R.id.protext);
		// TODO Auto-generated constructor stub
	}



	public ImageView getReLoadImage() {
		return reLoadImage;
	}


}
