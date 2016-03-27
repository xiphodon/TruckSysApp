package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ServiceItemView extends LinearLayout {

	private RelativeLayout lay_top;
	private TextView tv_service_name, tv_service_content;
	private ImageView iv_arrow;

	public ServiceItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context);
	}

	public ServiceItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ServiceItemView(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initView();
	}
	private void initView() {
		lay_top = (RelativeLayout) findViewById(R.id.lay_top);
		tv_service_name = (TextView) findViewById(R.id.tv_service_name);
		tv_service_content = (TextView) findViewById(R.id.tv_service_content);
		tv_service_content.setVisibility(View.GONE);
		iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
		lay_top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Matrix matrix=new Matrix();
				if (tv_service_content.getVisibility() == View.GONE) {
					matrix.reset();
					tv_service_name.setTextColor(getContext().getResources()
							.getColor(R.color.red));
					iv_arrow.setScaleType(ScaleType.MATRIX);   //required
					Float.parseFloat("90");
					matrix.postRotate(Float.parseFloat("90"));
					iv_arrow.setImageMatrix(matrix);
					tv_service_content.setVisibility(View.VISIBLE);
				} else {
					matrix.reset();
					tv_service_name.setTextColor(getContext().getResources()
							.getColor(R.color.text_color_black));
					iv_arrow.setScaleType(ScaleType.MATRIX);   //required
					matrix.postRotate((float) -90);
					iv_arrow.setImageMatrix(matrix);
					tv_service_content.setVisibility(View.GONE);
				}
			}
		});
	}

	
	
}
