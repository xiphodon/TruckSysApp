package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.HotSaleDetailActivity;
import com.sy.trucksysapp.page.shoping.SaleDetailActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.DialogConfirmView.OnConfirmListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderFramentAdapter extends BaseAdapter {

	private int type;
	private List<HashMap<String, Object>> list;
	private Context context;
	private DisplayImageOptions options;
	DialogConfirmView ConfirmView;
	OrderStatusChangeListenner OrderStatusChangeListenner;

	public OrderFramentAdapter(int type, List<HashMap<String, Object>> list,
			Context context,OrderStatusChangeListenner OrderStatusChangeListenner) {
		super();
		this.type = type;
		this.list = list;
		this.context = context;
		this.OrderStatusChangeListenner=OrderStatusChangeListenner ;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_order_list, null);
			holder = new ViewHolder();
			holder.tv_shopnumber = (TextView) convertView
					.findViewById(R.id.tv_shopnumber);
			holder.tv_shopname = (TextView) convertView
					.findViewById(R.id.tv_shopname);
			holder.tv_orderstate = (TextView) convertView
					.findViewById(R.id.tv_orderstate);
			holder.tv_goodscount = (TextView) convertView
					.findViewById(R.id.tv_goodscount);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_price);
			holder.content = (LinearLayout) convertView
					.findViewById(R.id.content);
			holder.bt_cancel = (TextView) convertView
					.findViewById(R.id.bt_cancel);
			holder.bt_delete = (TextView) convertView
					.findViewById(R.id.bt_delete);
			holder.bt_parse = (TextView) convertView
					.findViewById(R.id.bt_parse);
			holder.bt_pay = (TextView) convertView.findViewById(R.id.bt_pay);
			holder.bt_confirm = (TextView) convertView
					.findViewById(R.id.bt_confirm);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final HashMap<String, Object> map = list.get(position);
		holder.tv_shopnumber.setText(map.get("OrderNumber").toString());
		if (map.get("OrderTitle").toString().equals("null")) {
			holder.tv_shopname.setText("特卖");
		} else {
			holder.tv_shopname.setText(map.get("OrderTitle").toString());
		}
		List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) map
				.get("OrderRows");
		holder.content.removeAllViews();
		Double total = 0d;
		for (int i = 0; i < itemlist.size(); i++) {
			HashMap<String, String> itemmap = itemlist.get(i);
			View mv = View.inflate(context, R.layout.item_order_good, null);
			ImageView iv_good_pic = (ImageView) mv
					.findViewById(R.id.iv_good_pic);
			ImageLoader.getInstance().displayImage(itemmap.get("OrDeGoodImg"),
					iv_good_pic, options);
			TextView tv_price = (TextView) mv.findViewById(R.id.tv_price);
			tv_price.setText("￥" + itemmap.get("OrDeGoodPrice"));
			TextView tv_count = (TextView) mv.findViewById(R.id.tv_count);
			tv_count.setText("×" + itemmap.get("OrDeNum"));
			TextView param = (TextView) mv.findViewById(R.id.param);
			param.setText(itemmap.get("OrDeParameters"));
			TextView tv_goodname = (TextView) mv.findViewById(R.id.tv_goodname);
			tv_goodname.setText(itemmap.get("OrDeGoodName"));
			mv.setTag(itemmap);
			mv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					HashMap<String, String> rmap = (HashMap<String, String>) arg0
							.getTag();
					String name = rmap.get("OrDeGoodName");
					String id = rmap.get("ProductId");
					String ProductType = rmap.get("ProductType");
					if (map.get("IsSale").equals("1")) {
						context.startActivity(new Intent(context,
								HotSaleDetailActivity.class).putExtra("saleid",
								id).putExtra("salename", name));
					} else {
						Intent intent = new Intent(context,
								SaleDetailActivity.class);
						intent.putExtra("saleid", id);
						intent.putExtra("Type", Integer.valueOf(ProductType));
						context.startActivity(intent);
					}
				}
			});
			total += Double.valueOf(itemmap.get("OrDeGoodPrice"))
					* Integer.valueOf(itemmap.get("OrDeNum"));
			holder.content.addView(mv);
		}
		// 计算总金额
		DecimalFormat df = new DecimalFormat("0.00");
		holder.tv_price.setText("￥" + df.format(total));
		holder.bt_pay.setTag(map);
		int OrderState = Integer.valueOf(map.get("OrderState") + "");
		switch (OrderState) {
		case 1:
			holder.bt_cancel.setVisibility(View.VISIBLE);
			holder.bt_pay.setVisibility(View.VISIBLE);
			holder.bt_parse.setVisibility(View.GONE);
			holder.bt_confirm.setVisibility(View.GONE);
			holder.bt_delete.setVisibility(View.GONE);
			holder.tv_orderstate.setText("待付款");
			break;
		case 2:
			holder.tv_orderstate.setText("待发货");
			holder.bt_cancel.setVisibility(View.GONE);
			holder.bt_pay.setVisibility(View.GONE);
			holder.bt_parse.setVisibility(View.GONE);
			holder.bt_confirm.setVisibility(View.GONE);
			holder.bt_delete.setVisibility(View.GONE);
			break;
		case 3:
			holder.bt_cancel.setVisibility(View.GONE);
			holder.bt_pay.setVisibility(View.GONE);
			holder.bt_parse.setVisibility(View.GONE);
			holder.bt_confirm.setVisibility(View.VISIBLE);
			holder.bt_delete.setVisibility(View.GONE);
			holder.tv_orderstate.setText("待确认");
			break;
		case 4:
			holder.bt_cancel.setVisibility(View.GONE);
			holder.bt_pay.setVisibility(View.GONE);
			holder.bt_parse.setVisibility(View.VISIBLE);
			holder.bt_confirm.setVisibility(View.GONE);
			holder.bt_delete.setVisibility(View.GONE);
			holder.tv_orderstate.setText("待评价");
			break;
		case 5:
			holder.bt_cancel.setVisibility(View.GONE);
			holder.bt_pay.setVisibility(View.GONE);
			holder.bt_parse.setVisibility(View.GONE);
			holder.bt_confirm.setVisibility(View.GONE);
			holder.bt_delete.setVisibility(View.VISIBLE);
			holder.tv_orderstate.setText("交易完成");
			break;
		case 6:
			holder.bt_cancel.setVisibility(View.GONE);
			holder.bt_pay.setVisibility(View.GONE);
			holder.bt_parse.setVisibility(View.GONE);
			holder.bt_confirm.setVisibility(View.GONE);
			holder.bt_delete.setVisibility(View.GONE);
			holder.tv_orderstate.setText("支付处理中");
			break;
		default:
			break;
		}
		holder.bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ConfirmView = new DialogConfirmView(context, "取消订单",
						"确认取消该订单吗？", new OnConfirmListener() {
							@Override
							public void OnConfirm() {
								// TODO Auto-generated method stub
								List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) map
										.get("OrderRows");
								DelorCancelOrder("CancelOrder", itemlist.get(0)
										.get("OrderId"), position);
								ConfirmView = null;
							}
						});
				ConfirmView.show();
			}
		});
		holder.bt_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// CommonUtils.showToast(context, "删除订单！");
				ConfirmView = new DialogConfirmView(context, "删除订单",
						"确认删除该订单吗？", new OnConfirmListener() {
							@Override
							public void OnConfirm() {
								// TODO Auto-generated method stub
								List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) map
										.get("OrderRows");
								DelorCancelOrder("DeleteOrder", itemlist.get(0)
										.get("OrderId"), position);
								ConfirmView = null;
							}
						});
				ConfirmView.show();
			}
		});
		holder.bt_parse.setTag(map);
		holder.bt_parse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HashMap<String, Object> rowdata = (HashMap<String, Object>) arg0
						.getTag();
				context.startActivity(new Intent(context,
						OrderGoodlistActivity.class).putExtra("rowdata",
						rowdata));
			}
		});
		holder.bt_pay.setTag(map);
		holder.bt_pay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				HashMap<String, Object> row = (HashMap<String, Object>) arg0
						.getTag();
				((Activity) context).finish();
				context.startActivity(new Intent(context, PaymentActivity.class)
						.putExtra("rowdata", row));
				
			}
		});
		holder.bt_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) map
						.get("OrderRows");
				ConfirmlOrder(itemlist.get(0).get("OrderId"),4);
			}
		});
		holder.tv_goodscount.setText("共" + itemlist.size() + "种商品");
		return convertView;
	}

	class ViewHolder {
		private TextView tv_shopnumber;
		private TextView tv_shopname;
		private TextView tv_orderstate;// 类型
		private TextView tv_goodscount;
		private TextView tv_price;
		private LinearLayout content;
		private TextView bt_cancel;
		private TextView bt_delete;
		private TextView bt_parse;
		private TextView bt_pay;
		private TextView bt_confirm;
	}

	/**
	 * 删除和取消订单
	 * 
	 * @param method
	 * @param orderId
	 */
	private void ConfirmlOrder(String orderId, int Status) {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/ChangeOrderState";
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("orderId", orderId);
		RequestParams.put("state", Status+"");
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				try {
					JSONObject json = new JSONObject(result);
					if (json.getBoolean("State")) {
						CommonUtils.showToast(context, "已确认订单!");
						OrderStatusChangeListenner.Orderchanged();
					} else {
						CommonUtils.showToast(context, "确认失败!");
					}
				} catch (Exception e) {
					// TODO: handle exception
					CommonUtils.showToast(context, "确认失败!");
				}
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				CommonUtils.showToast(context, "确认失败!");
			}
		});

	}

	/**
	 * 删除和取消订单
	 * 
	 * @param method
	 * @param orderId
	 */
	private void DelorCancelOrder(String method, String orderId,
			final int position) {
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/" + method;
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("orderId", orderId);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				CommonUtils.showToast(context, "取消订单成功");
				list.remove(position);
				notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, arg1);
				CommonUtils.showToast(context, "取消订单失败");
			}
		});

	}
}
