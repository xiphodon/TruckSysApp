package com.sy.trucksysapp.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.alixpay.AlixPay;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.utils.TextUtils;

/**
 * 账户充值页面
 * 
 * @author lxs 20150701
 * 
 */
public class RechargeActivity extends BaseActivity implements OnClickListener {

	private CheckBox weixin_check, zhifubao_check;
	private int PAYMENT_WAY = 1;
	private EditText edt_count;
	private Button btn_submit;
	AlixPay alixpay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		alixpay = new AlixPay(RechargeActivity.this);
		intView();
	}

	private void intView() {
		weixin_check = (CheckBox) findViewById(R.id.weixin_check);
		weixin_check.setOnClickListener(this);
		zhifubao_check = (CheckBox) findViewById(R.id.zhifubao_check);
		zhifubao_check.setOnClickListener(this);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		edt_count = (EditText)findViewById(R.id.edt_count);
		TextUtils.setPricePoint(edt_count);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.weixin_check:
			edt_count.clearFocus();
			PAYMENT_WAY = 1;
			weixin_check.setChecked(true);
			zhifubao_check.setChecked(false);
			break;
		case R.id.zhifubao_check:
			edt_count.clearFocus();
			PAYMENT_WAY = 2;
			zhifubao_check.setChecked(true);
			weixin_check.setChecked(false);
			break;
		case R.id.btn_submit:
			if(weixin_check.isChecked()){
				CommonUtils.showToast(RechargeActivity.this, "暂不支持微信支付！");
				return;
			}
			if(zhifubao_check.isChecked()){
				String mo = edt_count.getText().toString();
				if(mo.equals("")){
					CommonUtils.showToast(RechargeActivity.this, "请输入充值金额！");
					edt_count.requestFocus();
				}else{
					Double d = 0d;
					try {
						d = Double.parseDouble(mo);
						if(d>0){
							if(d>9999999.99){
								CommonUtils.showToast(RechargeActivity.this, "充值金额过大，系统无法处理！");
							}else{
								alixpay.pay("用户充值", "用户充值", d, getOutTradeNo());
							}
						}else{
							CommonUtils.showToast(RechargeActivity.this, "充值金额不能为0！");
							edt_count.requestFocus();
							edt_count.setText("");
						}
					} catch (Exception e) {
						// TODO: handle exception
						CommonUtils.showToast(RechargeActivity.this, e.getMessage());
					}
				}
			}
			break;
		default:
			break;
		}
	}
	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);
		return key+"-"+PreferenceUtils.getInstance(RechargeActivity.this).getSettingUserId();
	}
}
