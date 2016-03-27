package com.sy.trucksysapp.page.freight;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;

/**
 * 货运详情页面
 * 
 * @author Administrator 2015-10-22
 * 
 * 
 */
public class FreightDetailActivity extends BaseActivity implements
		OnClickListener {
	private TextView topbase_center_text;
	private Context context;
	private Button btn_call_phone, btn_call_tel;
	private TextView tv_start_addr, tv_end_addr, tv_desc, tv_car, tv_price,
			tv_pay_way, tv_creat_time, tv_truck_time, tv_msg_source,
			tv_user_address, tv_user_name, tv_user_phone, tv_user_tel;
	private HashMap<String, String> map;
	private String[] strs={"面议","回单付款","货到付款"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_freight_detail);
		initView();
	}

	private void initView() {
		context = FreightDetailActivity.this;
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("货运详情");
		btn_call_tel = (Button) findViewById(R.id.btn_call_tel);
		btn_call_tel.setOnClickListener(this);
		btn_call_phone = (Button) findViewById(R.id.btn_call_phone);
		btn_call_phone.setOnClickListener(this);
		tv_start_addr = (TextView)findViewById(R.id.tv_start_addr);
		tv_end_addr = (TextView)findViewById(R.id.tv_end_addr);
		tv_desc = (TextView)findViewById(R.id.tv_desc);
		tv_car = (TextView)findViewById(R.id.tv_car);
		tv_price = (TextView)findViewById(R.id.tv_price);
		tv_pay_way = (TextView)findViewById(R.id.tv_pay_way);
		tv_creat_time = (TextView)findViewById(R.id.tv_creat_time);
		tv_truck_time = (TextView)findViewById(R.id.tv_truck_time);
		tv_msg_source = (TextView)findViewById(R.id.tv_msg_source);
		tv_user_address = (TextView)findViewById(R.id.tv_user_address);
		tv_user_name = (TextView)findViewById(R.id.tv_user_name);
		tv_user_phone = (TextView)findViewById(R.id.tv_user_phone);
		tv_user_tel = (TextView)findViewById(R.id.tv_user_tel);
		map = (HashMap<String, String>) getIntent().getSerializableExtra(
				"rowdata");
		initData();
	}

	private void initData(){
		if(map!=null){
			tv_start_addr.setText(map.get("Goods_BProvince")+map.get("Goods_BCity")+map.get("Goods_BCounty"));
			tv_end_addr.setText(map.get("Goods_EProvince")+map.get("Goods_ECity")+map.get("Goods_ECounty"));
			tv_desc.setText(map.get("GType_Name")+map.get("Goods_Ton")+"吨");
			tv_car.setText(map.get("VSize_Name")+map.get("VType_Name"));
			tv_pay_way.setText(strs[Integer.valueOf(map.get("Goods_Closing"))-1]);
			tv_creat_time.setText(map.get("Goods_CreateTime"));
			tv_truck_time.setText(map.get("Goods_EnTruckTime"));
			tv_msg_source.setText(map.get("Tran_Name"));
			tv_user_address.setText(map.get("Tran_Address"));
			tv_user_name.setText(map.get("Tran_LinkName"));
			tv_user_phone.setText(map.get("Tran_TelMobile"));
			tv_user_tel.setText(map.get("Tran_Tel"));
			if(map.get("Goods_PayType").equals("0")){
				if(map.get("Goods_FeeTon").equals("")||map.get("Goods_FeeTon").equals("null")){
					tv_price.setText(map.get("Goods_FeeCart")+"元/车");
				}else{
					tv_price.setText(map.get("Goods_FeeTon")+"元/吨");
				}
			}else if(map.get("Goods_PayType").equals("1")){
				tv_price.setText("面议");
			}
			
		}else{
			CommonUtils.showToast(context, "数据报错！");
			this.finish();
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_call_tel:// 拨打固话
             call(tv_user_tel);
			break;
		case R.id.btn_call_phone:// 拨打电话
			call(tv_user_phone);
			break;

		default:
			break;
		}
	}
	/**
	 * 拨打电话
	 * @param view
	 */
	private void call(TextView view){
		String phone = view.getText().toString();
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phone));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}
