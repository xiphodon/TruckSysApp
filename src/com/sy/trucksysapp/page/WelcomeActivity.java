package com.sy.trucksysapp.page;

import com.sy.trucksysapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 欢迎页面
 * @author lxs 20150521
 *
 */
public class WelcomeActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_welcome);
		super.onCreate(savedInstanceState);
		initControl();
	}

	private void initControl(){
		new Handler().postDelayed(r, 1000);
	}
	Runnable r = new Runnable() {
		@Override
		public void run() {

			Intent in = new Intent(getBaseContext(), MainActivity.class);
			startActivity(in);
			finish();
		};
	};
}
