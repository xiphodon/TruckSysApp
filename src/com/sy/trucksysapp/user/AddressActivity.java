package com.sy.trucksysapp.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.AddressInfo;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 收货地址列表
 * 
 * @author lxs 20150615
 * 
 */
public class AddressActivity extends BaseActivity implements OnClickListener {
	private Context context;
	private ListView adlist = null;
	private List<AddressInfo> AddressInfos = new ArrayList<AddressInfo>();
	private AddressAdapter adapter;
	private String usercount ;
	private Button btn_add_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_list);
		initView();
		initdate();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		setResult(100);
		super.finish();
	}
	private void initView() {
		context = AddressActivity.this;
		usercount = PreferenceUtils.getInstance(context).getSettingUserId();
		adlist = (ListView) findViewById(R.id.lv_ad_list);
		adapter = new AddressAdapter(context, AddressInfos);
		adlist.setAdapter(adapter);
		btn_add_address = (Button) findViewById(R.id.btn_add_address);
		btn_add_address.setOnClickListener(this);
	}

	private void initdate() {
		// TODO Auto-generated method stub
		List<AddressInfo> Address = CommonUtils.getAdresslist(context,usercount);
		if (Address != null) {
			AddressInfos.clear();
			AddressInfos.addAll(Address);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initdate();
		super.onResume();
	}
	

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_add_address:
			Intent intent = new Intent(context, AddressEditActivity.class);
			intent.putExtra("type", "add");
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
