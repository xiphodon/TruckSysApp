package com.sy.trucksysapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sy.trucksysapp.R;

public class TimeTextView extends LinearLayout {

	private Context mContext;
	private TextView mDay, mHour, mMin, mMse, tip_title;
	private long days, hours, minutes, seconds;
	private String tips = "";
	private boolean run = true; // 是否启动了
	public boolean flag = false;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (run) {
					if (computeTime()) {
						setUi();
						handler.sendEmptyMessageDelayed(0, 1000);
					}
				}
				break;
			}
		}
	};

	public TimeTextView(Context context) {
		super(context);
		this.mContext = context;
		inflateLayout();
	};

	public TimeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		inflateLayout();
	}

	@SuppressLint("NewApi")
	public TimeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		inflateLayout();
	}

	private void inflateLayout() {
		this.addView(LayoutInflater.from(mContext).inflate(
				R.layout.time_view_layout, null));
		mDay = (TextView) this.findViewById(R.id.electricity_time_day);
		mHour = (TextView) this.findViewById(R.id.electricity_time_hour);
		mMin = (TextView) this.findViewById(R.id.electricity_time_min);
		mMse = (TextView) this.findViewById(R.id.electricity_time_mse);
		tip_title = (TextView) this.findViewById(R.id.tip_title);
	}

	public void setTime(long day, long hour, long minute, long second,
			String tip) {
		tips = tip;
		days = day;
		hours = hour;
		minutes = minute;
		seconds = second;
		setUi();
		handler.removeMessages(0);

		// 每隔1秒钟发送一次handler消息

		handler.sendEmptyMessageDelayed(0, 1000);
	}

	private void setUi() {
		mDay.setText(String.valueOf(days));
		mHour.setText(String.valueOf(hours));
		mMin.setText(String.valueOf(minutes));
		mMse.setText(String.valueOf(seconds));
		tip_title.setText(tips);
	}

	private boolean computeTime() {
		flag= true;
		seconds--;
		if (seconds < 0) {
			minutes--;
			seconds = 59;
			if (minutes < 0) {
				minutes = 59;
				hours--;
				if (hours < 0) {
					hours = 23;
					days--;
					if (days < 0) {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	public void stopComputeTime() {
		run = false;
	}
}