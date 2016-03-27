package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.order.OrderFragment;

public class OrderGoodlistActivity extends BaseActivity {
	private TextView tv_shopname, tv_goodscount, tv_price,tv_orderstate;
	private LinearLayout content;
	HashMap<String, Object> rowdata = null;
	private TextView[] currentviews = new TextView[2];
	private Context context;
	private DisplayImageOptions options;
	private int noevcount=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordergood_list);
		context = OrderGoodlistActivity.this;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		rowdata = (HashMap<String, Object>) getIntent().getSerializableExtra(
				"rowdata");
		initviews();
		initdata();
	}

	
	private void initviews() {
		// TODO Auto-generated method stub
		tv_shopname = (TextView) findViewById(R.id.tv_shopname);
		tv_goodscount = (TextView) findViewById(R.id.tv_goodscount);
		tv_price = (TextView) findViewById(R.id.tv_price);
		content = (LinearLayout) findViewById(R.id.content);
		tv_orderstate = (TextView) findViewById(R.id.tv_orderstate);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (resultCode == 100) {
				try {
					noevcount--;
					currentviews[0].setVisibility(View.VISIBLE);
					currentviews[1].setVisibility(View.INVISIBLE);
					if(noevcount==0){
						tv_orderstate.setText("已评价");
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void initdata() {
		// TODO Auto-generated method stub
		if (rowdata != null) {
			if (rowdata.get("OrderTitle").toString().equals("null")) {
				tv_shopname.setText("特卖");
			} else {
				tv_shopname.setText(rowdata.get("OrderTitle").toString());
			}
			List<HashMap<String, String>> itemlist = (List<HashMap<String, String>>) rowdata
					.get("OrderRows");
			content.removeAllViews();
			Double total = 0d;
			noevcount = itemlist.size();
			for (int i = 0; i < itemlist.size(); i++) {
				final HashMap<String, String> itemmap = itemlist.get(i);
				View mv = View.inflate(context, R.layout.item_ordergood_list,
						null);
				ImageView iv_good_pic = (ImageView) mv
						.findViewById(R.id.iv_good_pic);
				ImageLoader.getInstance().displayImage(
						itemmap.get("OrDeGoodImg"), iv_good_pic, options);
				TextView bt_prased = (TextView) mv.findViewById(R.id.bt_prased);
				TextView bt_prase = (TextView) mv.findViewById(R.id.bt_prase);
				TextView tv_price = (TextView) mv.findViewById(R.id.tv_price);
				tv_price.setText("￥" + itemmap.get("OrDeGoodPrice"));
				TextView tv_count = (TextView) mv.findViewById(R.id.tv_count);
				tv_count.setText("×" + itemmap.get("OrDeNum"));
				TextView param = (TextView) mv.findViewById(R.id.param);
				param.setText(itemmap.get("OrDeParameters"));
				TextView tv_goodname = (TextView) mv
						.findViewById(R.id.tv_goodname);
				tv_goodname.setText(itemmap.get("OrDeGoodName"));
				mv.setTag(itemmap);
				mv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						HashMap<String, String> rmap = (HashMap<String, String>) arg0
								.getTag();
					}
				});
				if (itemmap.get("IsEvaluate").equals("1")) {
					bt_prased.setVisibility(View.VISIBLE);
					noevcount--;
					bt_prase.setVisibility(View.INVISIBLE);
				} else {
					bt_prased.setVisibility(View.INVISIBLE);
					bt_prase.setVisibility(View.VISIBLE);
					bt_prase.setTag(bt_prased);
					bt_prase.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub\
							String ProductType = "";
							String type = (String) rowdata.get("IsSale");
							if (type.equals("1")) {
								ProductType = "0";
							} else {
								ProductType = itemmap.get("ProductType");
							}
							startActivityForResult(
									new Intent(context, CommentActivity.class)
											.putExtra("id",
													itemmap.get("ProductId"))
											.putExtra("type", ProductType)
											.putExtra("OId",
													itemmap.get("OrDeId")), 100);
							currentviews[0] = (TextView) arg0.getTag();
							currentviews[1] = (TextView) arg0;
						}
					});
				}
				total += Double.valueOf(itemmap.get("OrDeGoodPrice"))
						* Integer.valueOf(itemmap.get("OrDeNum"));
				content.addView(mv);
			}
			// 计算总金额
			DecimalFormat df = new DecimalFormat("0.00");
			tv_price.setText("￥" + df.format(total));
			tv_goodscount.setText("共" + itemlist.size() + "种商品");
		}
	}
}
