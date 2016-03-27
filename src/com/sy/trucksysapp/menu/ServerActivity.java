package com.sy.trucksysapp.menu;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ServerActivity extends Fragment {
	private Button set;
	private Button reset;
	private EditText base;
	private EditText img;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_produce_server_setting,
				container, false);
		base = (EditText) view.findViewById(R.id.baseurl);
		base.setText(SystemApplication.setBaseurl(null));
		img = (EditText) view.findViewById(R.id.imgurl);
		img.setText(SystemApplication.setImgUrl(null));
		set = (Button) view.findViewById(R.id.set);
		set.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				String b = base.getText().toString();
				if (!b.startsWith("http://"))
					b = "http://" + b;
				SystemApplication.setBaseurl(b);
				String i = img.getText().toString();
				if (!i.startsWith("http://"))
					i = "http://" + i;
				SystemApplication.setImgUrl(i);
				CommonUtils.showToast(getActivity(), "设置成功!");
			}
		});
		reset = (Button) view.findViewById(R.id.reset);
		reset.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
//				img.setText(SystemApplication.getImgUrl2());
//				base.setText(SystemApplication.getBaseurl2());
				CommonUtils.showToast(getActivity(), "重置成功!");
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
