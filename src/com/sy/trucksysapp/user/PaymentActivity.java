package com.sy.trucksysapp.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.alixpay.AccountPay;
import com.sy.trucksysapp.alixpay.AlixPay;
import com.sy.trucksysapp.alixpay.BanksPayActivity;
import com.sy.trucksysapp.entity.AddressInfo;
import com.sy.trucksysapp.entity.CouponModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.PfragmentActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.ShopServiceFragment;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.MyListView;
import com.sy.trucksysapp.wxapi.WxPay;

/**
 * 结算付款页面
 * 
 * @author lxs 20150612
 * 
 */
public class PaymentActivity extends BaseActivity implements OnClickListener {

	private CheckBox weixin_check, zhifubao_check, account_check, pos_check,
			yinlian_check;
	private RelativeLayout rl_weixin, rl_zhifubao, rl_account, rl_yinlian,
			rl_pos;
	// private RadioGroup rg_send_way;
	private RadioButton radio_1, radio_2, radio_3, radio_4, radio_5;
	private LinearLayout li_coupons;
	/**
	 * 结算方式
	 */
	private int SEND_WAY = -1;
	private RelativeLayout rb_coupons;
	private MyListView lv_coupons;
	private View line_lv;
	private CouponsAdapter couponsadapter;
	private TextView tv_complete, tv_coupon_value, tv_contractValue,
			tv_cart_all_sum;
	private ImageView img_right;
	private LinearLayout ll_address_select;
	private TextView tv_user_address, tv_user_phone, tv_user_name;
	private EditText edt_order_mark;
	private List<CouponModel> couponslist = new ArrayList<CouponModel>();
	private HashMap<String, Object> rowmap;
	private Button iv_cart_buy;
	Double total = 0d;
	AlixPay alixpay;
	AccountPay zccountpay;
	private AddressInfo info = null;
	private String setCoupon_value = "";
	double all = 0;// 购物券金额
	private CheckBox check_agreement;
	ProgressDialog pd;
	// private TextView tv_agreement;
	private Double aa = 0d;
	private ImageView iv_payarrow;
	private LinearLayout li_zhifu;
	private RelativeLayout rl_payarrow, rl_payservice;
	Dialog ServiceDialog;
	private RelativeLayout rel_set_address;
	TextView tv_user_setaddress;
	String serviceid = "";
	private String usercount ;
	private String OrderNumber;
	private WxPay wxpay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		usercount = PreferenceUtils.getInstance(PaymentActivity.this).getSettingUserId();
		rowmap = (HashMap<String, Object>) getIntent().getSerializableExtra(
				"rowdata");
		alixpay = new AlixPay(PaymentActivity.this);
		if (pd == null) {
			pd = new ProgressDialog(PaymentActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					pd.dismiss();
				}
			});
		}
		zccountpay = new AccountPay(PaymentActivity.this, pd);
		//微信支付
		wxpay = new WxPay(PaymentActivity.this);
		initView();
		getUserInfo();
		executeCoupon();
	}

	private void calculateorderMoney() {
		// TODO Auto-generated method stub
		if (rowmap != null) {
			try {
				List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) rowmap
						.get("OrderRows");
				for (int i = 0; i < itemlist.size(); i++) {
					HashMap<String, String> itemmap = itemlist.get(i);
					total += Double.valueOf(itemmap.get("OrDeGoodPrice"))
							* Integer.valueOf(itemmap.get("OrDeNum"));
				}
				OrderNumber = rowmap.get("OrderNumber").toString();
				// 计算总金额
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			DecimalFormat df = new DecimalFormat("0.00");
			tv_contractValue.setText("￥" + df.format(total));
			tv_cart_all_sum.setText("￥" + df.format(total));
			
		}else{
			total = Double.valueOf(getIntent().getStringExtra("OrderTotalPrice"));
			OrderNumber  = getIntent().getStringExtra("OrderNumber");
			DecimalFormat df = new DecimalFormat("0.00");
			tv_contractValue.setText("￥" + df.format(total));
			tv_cart_all_sum.setText("￥" + df.format(total));
		}

	}

	private void executeCoupon() {
		// 加载优惠券
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/GetCoupon";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("type", "0");// 查询所有购物券
		RequestParams.put("id",
				PreferenceUtils.getInstance(PaymentActivity.this)
						.getSettingUserId());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				List<CouponModel> mlist = new ArrayList<CouponModel>();
				try {
					JSONObject json = new JSONObject(result);
					JSONArray array = json.getJSONArray("rows");
					for (int i = 0; i < array.length(); i++) {
						CouponModel m = new CouponModel();
						m.setCoup_EndTime(array.getJSONObject(i).getString(
								"Coup_EndTime"));
						m.setCoup_Money(array.getJSONObject(i).getDouble(
								"Coup_Money"));
						m.setCoup_StartTime(array.getJSONObject(i).getString(
								"Coup_StartTime"));
						m.setCoupon_desc(array.getJSONObject(i).getString(
								"MemberAccount"));
						m.setCoupon_id(array.getJSONObject(i).getInt("Coup_Id"));
						m.setSelected(false);
						mlist.add(m);
					}
				} catch (Exception e) {

				}
				couponslist.clear();
				couponslist.addAll(mlist);
				couponsadapter.notifyDataSetChanged();
				if (couponslist.size() == 0) {
					tv_coupon_value.setText("无可用优惠券!");
				}
				super.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
			}
		});

	}

	private void initView() {
		tv_user_setaddress = (TextView) findViewById(R.id.tv_user_setaddress);
		rel_set_address = (RelativeLayout) findViewById(R.id.rel_set_address);
		rel_set_address.setOnClickListener(this);
		li_coupons = (LinearLayout) findViewById(R.id.li_coupons);
		// tv_agreement = (TextView) findViewById(R.id.tv_agreement);
		// tv_agreement.setOnClickListener(this);
		weixin_check = (CheckBox) findViewById(R.id.weixin_check);
		weixin_check.setOnClickListener(this);
		zhifubao_check = (CheckBox) findViewById(R.id.zhifubao_check);
		zhifubao_check.setOnClickListener(this);
		account_check = (CheckBox) findViewById(R.id.account_check);
		account_check.setOnClickListener(this);
		pos_check = (CheckBox) findViewById(R.id.pos_check);
		pos_check.setOnClickListener(this);
		yinlian_check = (CheckBox) findViewById(R.id.yinlian_check);
		yinlian_check.setOnClickListener(this);
		rl_weixin = (RelativeLayout) findViewById(R.id.rl_weixin);
		rl_weixin.setOnClickListener(this);
		rl_zhifubao = (RelativeLayout) findViewById(R.id.rl_zhifubao);
		rl_zhifubao.setOnClickListener(this);
		rl_account = (RelativeLayout) findViewById(R.id.rl_account);
		rl_account.setOnClickListener(this);
		rl_yinlian = (RelativeLayout) findViewById(R.id.rl_yinlian);
		rl_yinlian.setOnClickListener(this);
		rl_pos = (RelativeLayout) findViewById(R.id.rl_pos);
		rl_pos.setOnClickListener(this);
		iv_payarrow = (ImageView) findViewById(R.id.iv_payarrow);
		rl_payarrow = (RelativeLayout) findViewById(R.id.rl_payarrow);
		rl_payarrow.setOnClickListener(this);
		rl_payservice = (RelativeLayout) findViewById(R.id.rl_payservice);
		rl_payservice.setOnClickListener(this);
		li_zhifu = (LinearLayout) findViewById(R.id.li_zhifu);
		// private RelativeLayout rl_weixin,rl_zhifubao,rl_account;
		ll_address_select = (LinearLayout) findViewById(R.id.ll_address_select);
		ll_address_select.setOnClickListener(this);
		iv_cart_buy = (Button) findViewById(R.id.iv_cart_buy);
		iv_cart_buy.setOnClickListener(this);
		check_agreement = (CheckBox) findViewById(R.id.check_agreement);
		// rg_send_way = (RadioGroup) findViewById(R.id.rg_send_way);
		radio_1 = (RadioButton) findViewById(R.id.radio_1);
		radio_1.setOnClickListener(this);
		radio_2 = (RadioButton) findViewById(R.id.radio_2);
		radio_2.setOnClickListener(this);
		radio_3 = (RadioButton) findViewById(R.id.radio_3);
		radio_3.setOnClickListener(this);
		radio_4 = (RadioButton) findViewById(R.id.radio_4);
		radio_4.setOnClickListener(this);
		radio_5 = (RadioButton) findViewById(R.id.radio_5);
		radio_5.setOnClickListener(this);
		line_lv = (View) findViewById(R.id.line_lv);
		rb_coupons = (RelativeLayout) findViewById(R.id.rb_coupons);
		rb_coupons.setOnClickListener(this);
		lv_coupons = (MyListView) findViewById(R.id.lv_coupons);
		couponsadapter = new CouponsAdapter(this, couponslist);
		lv_coupons.setAdapter(couponsadapter);
		couponsadapter.notifyDataSetChanged();
		tv_complete = (TextView) findViewById(R.id.tv_complete);
		tv_complete.setOnClickListener(this);
		tv_coupon_value = (TextView) findViewById(R.id.tv_coupon_value);
		tv_contractValue = (TextView) findViewById(R.id.tv_contractValue);
		tv_cart_all_sum = (TextView) findViewById(R.id.tv_cart_all_sum);
		img_right = (ImageView) findViewById(R.id.img_right);
		// tv_user_address,tv_user_phone,tv_user_name;
		tv_user_address = (TextView) findViewById(R.id.tv_user_address);
		tv_user_phone = (TextView) findViewById(R.id.tv_user_phone);
		tv_user_name = (TextView) findViewById(R.id.tv_user_name);
		edt_order_mark = (EditText) findViewById(R.id.edt_order_mark);
		calculateorderMoney();
	}

	private void resetCoupons(Boolean b) {
		// TODO Auto-generated method stub
		if (li_coupons.getVisibility() == View.VISIBLE) {
			if (!b) {
				li_coupons.setVisibility(View.GONE);
				// 清空已选的代金券
				for (int i = 0; i < couponslist.size(); i++) {
					CouponModel mo = couponslist.get(i);
					mo.setSelected(false);
				}
				couponsadapter.notifyDataSetChanged();
				setCoupon_value();
			}
		} else {
			if (b) {
				li_coupons.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.rl_payarrow:
			Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.app_network_arrow)).getBitmap();
			Bitmap bitmap1 = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.app_network_arrow_down)).getBitmap();
			if (li_zhifu.getVisibility() != View.VISIBLE) {
				iv_payarrow.setImageBitmap(bitmap1);
			} else {
				iv_payarrow.setImageBitmap(bitmap);
			}
			li_zhifu.setVisibility((li_zhifu.getVisibility() == View.VISIBLE) ? View.GONE
					: View.VISIBLE);
			break;
		case R.id.rl_payservice:
			if (ServiceDialog == null) {
				ServiceDialog = CommonUtils
						.CreateServiceDialog(PaymentActivity.this);
			}
			ServiceDialog.show();
			break;
		case R.id.weixin_check:
			edt_order_mark.clearFocus();
			weixin_check.setChecked(true);
			account_check.setChecked(false);
			zhifubao_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.zhifubao_check:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(true);
			account_check.setChecked(false);
			weixin_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.account_check:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			account_check.setChecked(true);
			resetCoupons(true);
			break;
		case R.id.yinlian_check:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			account_check.setChecked(false);
			yinlian_check.setChecked(true);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.pos_check:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			account_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(true);
			resetCoupons(false);

			break;
		case R.id.rl_weixin:
			edt_order_mark.clearFocus();
			weixin_check.setChecked(true);
			account_check.setChecked(false);
			zhifubao_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.rl_zhifubao:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(true);
			account_check.setChecked(false);
			weixin_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.rl_account:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			account_check.setChecked(true);
			yinlian_check.setChecked(false);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.rl_yinlian:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			account_check.setChecked(false);
			yinlian_check.setChecked(true);
			pos_check.setChecked(false);
			resetCoupons(true);
			break;
		case R.id.rl_pos:
			edt_order_mark.clearFocus();
			zhifubao_check.setChecked(false);
			weixin_check.setChecked(false);
			account_check.setChecked(false);
			yinlian_check.setChecked(false);
			pos_check.setChecked(true);
			resetCoupons(false);
			break;
		case R.id.rel_set_address:
			Intent intit = new Intent(PaymentActivity.this,
					ShopServiceFragment.class);
			intit.putExtra("isselect", true);
//			intit.putExtra("classname",
//					"com.sy.trucksysapp.page.shoping.ShopServiceFragment");
			startActivityForResult(intit, 1000);
			break;
		case R.id.radio_1:
			setSendWay(1);
			break;
		case R.id.radio_2:
			setSendWay(2);
			break;
		case R.id.radio_3:
			setSendWay(3);
			break;
		case R.id.radio_4:
			setSendWay(4);
			break;
		case R.id.radio_5:
			setSendWay(5);
			break;
		case R.id.rb_coupons:
			edt_order_mark.clearFocus();
			if (couponslist.size() == 0) {
				return;
			}
			if (lv_coupons.getVisibility() == View.GONE) {
				lv_coupons.setVisibility(View.VISIBLE);
				line_lv.setVisibility(View.VISIBLE);
				tv_complete.setVisibility(View.VISIBLE);
				tv_coupon_value.setVisibility(View.GONE);
				img_right.setVisibility(View.GONE);
			}
			break;
		case R.id.tv_complete:
			setCoupon_value();
			break;
		case R.id.ll_address_select:
			edt_order_mark.clearFocus();
			startActivityForResult(new Intent(this, AddressActivity.class), 100);
			break;
		case R.id.iv_cart_buy:
			if (!check_agreement.isChecked()) {
				CommonUtils.showToast(PaymentActivity.this, "未同意相关协议！");
				return;
			}
//			if (true) {
//				CommonUtils.showToast(PaymentActivity.this, "结算中心即将上线，敬请期待！");
//				return;
//			}
			if (info == null) {
				CommonUtils.showToast(PaymentActivity.this, "请填写收货地址！");
				return;
			} else {
				try {
					JSONObject object = new JSONObject();
					// 收货地址
					JSONObject address = new JSONObject();
					address.put("linkman", info.getName());
					address.put("phone", info.getPhone());
					address.put("address", info.getAddress());
					address.put("areadetal", info.getArea());
					object.put("address", address);
					// 支付方式
					int paytype = -1;
					if (zhifubao_check.isChecked()) {
						paytype = 1;
					}
					if (account_check.isChecked()) {
						paytype = 2;
					}
					if (weixin_check.isChecked()) {
						paytype = 0;
					}
					object.put("paytype", paytype);
					// 配送方式
					int sendtype = -1;
					if (radio_1.isChecked()) {
						sendtype = 1;
					} else {
						if (serviceid.equals("")) {
							CommonUtils.showToast(PaymentActivity.this,
									"请选择安装地址！");
							return;
						}
					}
					if (radio_2.isChecked()) {
						sendtype = 2;
					}
					if (radio_3.isChecked()) {
						sendtype = 3;
					}
					if (radio_4.isChecked()) {
						sendtype = 4;
					}
					if (radio_5.isChecked()) {
						sendtype = 5;
					}
					object.put("sendtype", sendtype);
					// 商品金额
					object.put("shoptotal", total);
					// 购物券
					object.put("coupon",setCoupon_value.contains(",") ? setCoupon_value.substring(0, setCoupon_value.length() - 1): setCoupon_value);
					// 商品金额
					Double a = total - all;
					if (a > 0) {
					} else {
						a = 0d;
					}
					aa = a;
					final BigDecimal bd = new BigDecimal(a);
					object.put("serviceid", serviceid);
					object.put("paytotal", bd.setScale(2, RoundingMode.HALF_UP).doubleValue());
					object.put("paymark", edt_order_mark.getText());
					// 订单号
					object.put("payorder", OrderNumber);
					String param = object.toString();
					pd.setMessage("正在处理请求...");
					pd.show();
					String url = SystemApplication.getInstance().getBaseurl()
							+ "TruckService/UpdateOrderInfo";
					AsyncHttpClient client = new AsyncHttpClient();
					RequestParams RequestParams = new RequestParams();
					RequestParams.put("param", param);
					client.post(url, RequestParams,
							new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(String result) {
									// TODO Auto-generated method stub
									super.onSuccess(result);
									try {
										JSONObject json = new JSONObject(result);
										if (json.getBoolean("State")) {
											if (aa > 0) {
												if (zhifubao_check.isChecked()) {
													String paytitle = "";
													if(rowmap!=null){
														if (rowmap.get("OrderTitle").toString().equals("null")) {
															paytitle = "特卖";
														} else {
															paytitle = rowmap.get("OrderTitle").toString();
														}
													}else{
														paytitle="秒杀";
													}
													
													pd.dismiss();
													alixpay.pay(paytitle,paytitle,bd.setScale(2,RoundingMode.HALF_UP).doubleValue(),OrderNumber);
												}
												else if(pos_check.isChecked()){
													pd.dismiss();
													zccountpay.payPos(OrderNumber);
												}
												else if(yinlian_check.isChecked()){
													pd.dismiss();
//													startActivity(new Intent(PaymentActivity.this, BanksPayActivity.class).putExtra("OrderNumber", OrderNumber).putExtra("total", bd.setScale(2,RoundingMode.HALF_UP).doubleValue()));
													CommonUtils.showToast(PaymentActivity.this,"暂不支持银联支付！");
												}
												else if (weixin_check.isChecked()) {
													String paytitle = "";
													if(rowmap!=null){
														if (rowmap.get("OrderTitle").toString().equals("null")) {
															paytitle = "特卖";
														} else {
															paytitle = rowmap.get("OrderTitle").toString();
														}
													}else{
														paytitle="秒杀";
													}
													pd.dismiss();
//													CommonUtils.showToast(PaymentActivity.this,"暂不支持微信支付！");
													wxpay.pay(paytitle, paytitle, bd.setScale(2,RoundingMode.HALF_UP).doubleValue(), OrderNumber);
												}
												else if (account_check.isChecked()) {
													pd.dismiss();
													zccountpay.pay(OrderNumber);
												}else{
													
												}
											} else {
												zccountpay.pay(OrderNumber);
											}

										} else {
											pd.dismiss();
											CommonUtils.showToast(
													PaymentActivity.this,
													"处理订单失败！");
										}

									} catch (Exception e) {
										// TODO: handle exception
										pd.dismiss();
										CommonUtils
												.showToast(
														PaymentActivity.this,
														"处理订单失败！");
									}
								}

								@Override
								public void onFailure(Throwable arg0,
										String arg1) {
									// TODO Auto-generated method stub
									super.onFailure(arg0, arg1);
									pd.dismiss();
									CommonUtils.showToast(PaymentActivity.this,
											"处理订单信息失败！");
								}
							});

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;
		default:
			break;
		}
	}

	private void setCoupon_value() {
		// TODO Auto-generated method stub
		edt_order_mark.clearFocus();
		lv_coupons.setVisibility(View.GONE);
		tv_complete.setVisibility(View.GONE);
		line_lv.setVisibility(View.GONE);
		tv_coupon_value.setVisibility(View.VISIBLE);
		img_right.setVisibility(View.VISIBLE);
		all = 0;
		for (int i = 0; i < couponslist.size(); i++) {
			CouponModel mo = couponslist.get(i);
			if (mo.getSelected()) {
				all += mo.getCoup_Money();
				setCoupon_value += mo.getCoupon_id() + ",";
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		tv_coupon_value.setText("￥" + df.format(all));
		if ((total - all) > 0) {
			tv_cart_all_sum.setText("￥" + df.format(total - all));
		} else {
			tv_cart_all_sum.setText("￥0.00");
		}
	}

	private void setSendWay(int type) {
		clearCheck();
		SEND_WAY = type;
		switch (type) {
		case 1:
			radio_1.setChecked(true);
			break;
		case 2:
			radio_2.setChecked(true);
			break;
		case 3:
			radio_3.setChecked(true);
			break;
		case 4:
			radio_4.setChecked(true);
			break;
		case 5:
			radio_5.setChecked(true);
			break;
		default:
			break;
		}
	}

	private void clearCheck() {
		radio_1.setChecked(false);
		radio_2.setChecked(false);
		radio_3.setChecked(false);
		radio_4.setChecked(false);
		radio_5.setChecked(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 100) {
			if (resultCode == 100) {
				getUserInfo();
			}
		}
		if (requestCode == 1000) {
			if (resultCode == 2000) {
				Bundle bundle = data.getExtras();
				HashMap<String, String> rowdata = (HashMap<String, String>) bundle
						.getSerializable("rowdata");
				tv_user_setaddress.setText(rowdata.get("Serv_Name"));
				serviceid = rowdata.get("Serv_Id");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取收件人信息
	 */
	private void getUserInfo() {
		String Areaaddress = "收货地址：";
		String username = "联系人：";
		String phone = "";
		List<AddressInfo> Address = CommonUtils
				.getAdresslist(PaymentActivity.this,usercount);
		int selectpos = 0;
		if (Address != null && Address.size() > 0) {
			for (int i = 0; i < Address.size(); i++) {
				if (Address.get(i).isSelected()) {
					selectpos = i;
					break;
				}
			}
			info = Address.get(selectpos);
			Areaaddress += Address.get(selectpos).getArea().replace("-", "")
					+ Address.get(selectpos).getAddress();
			username += Address.get(selectpos).getName();
			phone += Address.get(selectpos).getPhone();

		}else{
			info=null;
		}
		tv_user_address.setText(Areaaddress);
		tv_user_phone.setText(phone);
		tv_user_name.setText(username);

	}
}
