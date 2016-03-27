package com.sy.trucksysapp.alixpay;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;
/**
 * bank pay
 * @author lxs 20150923
 *
 */
public class BanksPayActivity extends BaseActivity {

	private String OrderNumber;
	private double total;
	private Context context;
	private TextView recharge_amount;
	private EditText cardNo;
	private Button get_verifycode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_bank);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView(){
		context = BanksPayActivity.this;
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			OrderNumber = bundle.getString("OrderNumber");
			total = bundle.getDouble("total");
		}else{
			CommonUtils.showToast(context, "加载订单失败，请重新提交！");
			BanksPayActivity.this.finish();
		}
		recharge_amount = (TextView)findViewById(R.id.recharge_amount);
		cardNo = (EditText)findViewById(R.id.cardNo);
		get_verifycode = (Button)findViewById(R.id.get_verifycode);
		get_verifycode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommonUtils.showToast(context, "点击获取验证码");
			}
		});
		recharge_amount.setText(total+"");
	}
}
