package com.sy.trucksysapp.page.gas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.ServiceShopAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GasAdapter extends ServiceShopAdapter {
	private Context context;

	public GasAdapter(Context context,
			ArrayList<HashMap<String, String>> list) {
		super(context, list);
		this.context = context;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		HashMap<String, String> map = (HashMap<String, String>) super
				.getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.service_list_item, null);
			holder.shopIconIv = (ImageView) convertView
					.findViewById(R.id.shopIconIv);
			holder.tv_shopName = (TextView) convertView
					.findViewById(R.id.tv_shopName);
			holder.text_shoparea = (TextView) convertView
					.findViewById(R.id.text_shoparea);
			holder.text_shopdistince = (TextView) convertView
					.findViewById(R.id.text_shopdistince);
			holder.text_shopaddress = (TextView) convertView
					.findViewById(R.id.text_shopaddress);
			holder.text_shoptime = (TextView) convertView
					.findViewById(R.id.text_shoptime);
			holder.shop_ratinglevel = (RatingBar) convertView
					.findViewById(R.id.shop_ratinglevel);
			holder.shop_rating = (TextView) convertView
					.findViewById(R.id.shop_rating);
			holder.relcount= (RelativeLayout) convertView
					.findViewById(R.id.relcount);
			holder.type = (TextView) convertView.findViewById(R.id.type);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text_shoptime.setVisibility(View.GONE);
		holder.relcount.setVisibility(View.GONE);
//		holder.text_shopdistince.setVisibility(View.GONE);
		holder.text_shoparea.setVisibility(View.GONE);
		holder.shop_ratinglevel.setStepSize(0.1f);
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
//		holder.text_shoptime.setText(text)
//		holder.text_shoparea.setText(text)
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		holder.shop_ratinglevel.setRating(r);
		holder.shop_rating.setText(p);
		
		String prefix = "GaSt_";
		String pics = map.get(prefix + "Pic") + "";
		if (!(pics.equals("") || pics.equals("null"))) {
			if (pics.indexOf("|") != -1) {
				pics = pics.substring(0, pics.indexOf("|"));
			}
			ImageLoader.getInstance().displayImage(
					SystemApplication.getServiceUrl() + pics,
					holder.shopIconIv, super.getOptions());
		}
		String serviceType = map.get(prefix +"ServiceType");
		String text = "加油";
		int color = 0;
		int bg = 0;
		if (serviceType.equals("0")) {
			text = "加油";
			color = R.color.service_4s;
			bg = R.drawable.border_4s;
		} else if (serviceType.equals("1")) {
			text = "加气";
			color = R.color.service_garage;
			bg = R.drawable.border_garage;
		} 
		holder.type.setText(text);
		holder.type.setTextColor(context.getResources().getColor(color));
		holder.type.setBackground(context.getResources().getDrawable(bg));
		text = null;
		holder.tv_shopName.setText(map.get(prefix + "ShopName"));
		holder.text_shopaddress.setText("地址：" + map.get(prefix + "Address"));
		if (map.get("Distince") != null && !map.get("Distince").equals("")) {
			DecimalFormat df = new DecimalFormat("0.0");
			try {
				holder.text_shopdistince.setText(df.format(Double.valueOf(map
						.get("Distince"))) + "km");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return convertView;
	}

	public class ViewHolder {
		RelativeLayout relcount;
		RatingBar shop_ratinglevel;
		TextView shop_rating;
		ImageView shopIconIv;
		TextView tv_shopName, text_shoparea, text_shopdistince,
				text_shopaddress, text_shoptime, type;
	}
}