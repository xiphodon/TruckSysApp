package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.listener.CartProductItemClickListener;
import com.sy.trucksysapp.listener.CartProductItemDelClickListener;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener;

/**
 * 购物车页面
 * 
 * @author lxs 20150608
 * 
 */
public class ShopCartActivity extends BaseActivity implements
		CartProductItemClickListener, CartProductItemDelClickListener {

	private ListView lv_cart_list;
	private Context context;
	private ShopCartAdapter cartadapter;
	private CheckBox cb_cart_all_check;
	private boolean checkMark = false;// true状态变化来自全选按钮，false状态变化来自列表状态变化
	private List<CartProduct> cProducts = new ArrayList<CartProduct>();
	// private Map<Integer, CartProduct> unCheckedList = new HashMap<Integer,
	// CartProduct>();
	private TextView tv_cart_all_sum;
	private Button iv_cart_buy;
	DialogConfirmView dialogView;
	private Dialog progDialog = null;// 进度条
	private RelativeLayout rl_goods_list, rl_no_goods;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_cart);
		initView();
		initData();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = CommonUtils.ShowProcess(this, "正在处理请求...");
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	private void createorder() {
		// TODO Auto-generated method stub
		// 生成订单
		showProgressDialog();
		JSONArray array = new JSONArray();
		if (tv_cart_all_sum.getText().equals("￥0.00")) {
			dissmissProgressDialog();
			CommonUtils.showToast(context, "未选择任何商品！");
			return;
		}
		try {
			int j = 0;
			for (int i = 0; i < cProducts.size(); i++) {
				if (cProducts.get(i).isCheck()) {
					JSONObject obj = new JSONObject();
					if (cProducts.get(i).getIsSale().equals("0")) {
						obj.put("SellerType", cProducts.get(i).getSellerType());
						obj.put("SellerId", cProducts.get(i).getSellerId());
					}
					obj.put("parameters", cProducts.get(i).getParameters());
					obj.put("ProductType", cProducts.get(i).getProductType());
					obj.put("productId", cProducts.get(i).getProductId());
					obj.put("IsSale", cProducts.get(i).getIsSale());
					obj.put("count", cProducts.get(i).getCount());
					obj.put("sum", cProducts.get(i).getSum());
					obj.put("fname", cProducts.get(i).getFname());
					obj.put("imgsrc", cProducts.get(i).getImgsrc());
					obj.put("MemberId", PreferenceUtils.getInstance(context)
							.getSettingUserId());
					array.put(j, obj);
					j++;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dissmissProgressDialog();
			CommonUtils.showToast(context, "程序异常！");
			return;
		}
		// 提交服务器生成订单
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/ProductOrderAdd";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("orderArr", array.toString());
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				// dissmissProgressDialog();
				CommonUtils.showToast(context, "已成功生成订单！");
				List<CartProduct> lroducts = new ArrayList<CartProduct>();
				// 删除
				for (CartProduct product : cProducts) {
					if (!product.isCheck()) {
						lroducts.add(product);
					}
				}
				CommonUtils.saveCartProductlist(context, lroducts);
				finish();
				startActivityForResult(new Intent(context,
						ShoplistActivity.class).putExtra("currentPage", 1), 222);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				dissmissProgressDialog();
				CommonUtils.showToast(context, "系统处理失败！");
			}
		});

	}

	private void initView() {
		context = ShopCartActivity.this;
		lv_cart_list = (ListView) findViewById(R.id.lv_cart_list);
		tv_cart_all_sum = (TextView) findViewById(R.id.tv_cart_all_sum);
		iv_cart_buy = (Button) findViewById(R.id.iv_cart_buy);
		iv_cart_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// startActivity(new Intent(context, PaymentActivity.class));
				createorder();
			}
		});
		cartadapter = new ShopCartAdapter(context, cProducts);
		tv_cart_all_sum = (TextView) findViewById(R.id.tv_cart_all_sum);
		cartadapter = new ShopCartAdapter(context, cProducts);
		lv_cart_list.setAdapter(cartadapter);
		cartadapter.notifyDataSetChanged();
		cartadapter.setCartProductItemChangedListener(this);
		cartadapter.setItemdeleteListener(this);
		cb_cart_all_check = (CheckBox) findViewById(R.id.cb_cart_all_check);
		cb_cart_all_check.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < cProducts.size(); i++) {
					cProducts.get(i).setCheck(cb_cart_all_check.isChecked());
				}
				cartadapter.notifyDataSetChanged();
			}
		});
		rl_goods_list = (RelativeLayout) findViewById(R.id.rl_goods_list);
		rl_no_goods = (RelativeLayout) findViewById(R.id.rl_no_goods);
	}

	private void initData() {
		List<CartProduct> Products = CommonUtils.getCartProductlist(context);
		if (Products != null) {
			cProducts.clear();
			cProducts.addAll(Products);
		}
		if(cProducts.size()<1){
			rl_goods_list.setVisibility(View.GONE);
			rl_no_goods.setVisibility(View.VISIBLE);
		}else{
			rl_goods_list.setVisibility(View.VISIBLE);
			rl_no_goods.setVisibility(View.GONE);
		}
	}

	/**
	 * 购物车中商品数目改变
	 */
	@Override
	public void itemNumChanged(int position, int num) {
		// TODO Auto-generated method stub
		Double total = 0d;
		for (int i = 0; i < cProducts.size(); i++) {
			if (cProducts.get(i).isCheck()) {
				total += cProducts.get(i).getSum()
						* cProducts.get(i).getCount();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		tv_cart_all_sum.setText("￥" + df.format(total));
	}

	@Override
	public void itemDelClick(final int position) {
		// TODO Auto-generated method stub
		dialogView = new DialogConfirmView(ShopCartActivity.this, "提示",
				"您确定要删除该商品吗", new OnConfirmListener() {
					@Override
					public void OnConfirm() {
						// TODO Auto-generated method stub
						cProducts.remove(position);
						cartadapter.notifyDataSetChanged();
						long total = 0;
						for (int i = 0; i < cProducts.size(); i++) {
							total += cProducts.get(i).getSum()
									* cProducts.get(i).getCount();
						}
						tv_cart_all_sum.setText(total + "");
						CommonUtils.saveCartProductlist(context, cProducts);
						if(cProducts.size()<1){
							rl_goods_list.setVisibility(View.GONE);
							rl_no_goods.setVisibility(View.VISIBLE);
						}else{
							rl_goods_list.setVisibility(View.VISIBLE);
							rl_no_goods.setVisibility(View.GONE);
						}
					}
				});
		dialogView.show();

	}

	@Override
	public void itemCilck(int position, boolean isCheck) {
		// TODO Auto-generated method stub
		Boolean checkall = true;
		cProducts.get(position).setCheck(isCheck);
		for (int i = 0; i < cProducts.size(); i++) {
			if (!cProducts.get(i).isCheck()) {
				checkall = false;
				break;
			} else {
				continue;
			}
		}
		cb_cart_all_check.setChecked(checkall);
		cartadapter.notifyDataSetChanged();
	}
}
