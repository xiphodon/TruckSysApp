package com.sy.trucksysapp.menu;

import com.sy.trucksysapp.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProduceSetting extends FragmentActivity implements OnClickListener {
	private RelativeLayout server_layout;
	private ImageView server_image;
	private TextView server_text;
	private int gray = 0xFF7597B3;
	private int blue = 0xFF0AB2FB;
	FragmentManager fManager;
	private ServerActivity server;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.menu_produce_setting);
		fManager = this.getSupportFragmentManager();
		initViews();
	}

	private void initViews() {
		server_image = (ImageView) findViewById(R.id.server_image);
		server_text = (TextView) findViewById(R.id.server);
		server_layout = (RelativeLayout) findViewById(R.id.server_layout);
		server_layout.setOnClickListener(this);
		setChioceItem(0);
	}

	private void setChioceItem(int index) {
		FragmentTransaction transaction = fManager.beginTransaction();
		clearChioce();
		hideFragments(transaction);
		switch (index) {
		case 0:
			server_image.setImageResource(R.drawable.icon_ap_set);
			server_text.setTextColor(blue);
			server_layout
					.setBackgroundResource(R.drawable.prescription_bottom_btn_bg_pressed);
			if (server == null) {
				server = new ServerActivity();
				transaction.add(R.id.order_content, server);
			} else {
				transaction.show(server);
			}
			break;
		}
		transaction.commit();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (server != null) {
			transaction.hide(server);
		}
	}

	private void clearChioce() {
		server_image.setImageResource(R.drawable.icon_ap_set);
		server_layout
				.setBackgroundResource(R.drawable.prescription_bottom_btn_bg_normal);
		server_text.setTextColor(gray);
	}

	public void back(View v) {
		finish();
		setResult(500);
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.server:
			break;
		default:
			break;
		}
	}
}
