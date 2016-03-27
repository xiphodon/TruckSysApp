package com.sy.trucksysapp.page.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.page.shoping.adapter.ServiceShopAdapter;
import com.sy.trucksysapp.utils.TextUtils;

public class ServiceAdapter extends ServiceShopAdapter {
	private Context context;

	public ServiceAdapter(Context context,
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
			holder.type = (TextView) convertView.findViewById(R.id.type);
			holder.shop_rating = (TextView) convertView
					.findViewById(R.id.shop_rating);
			holder.shop_ratinglevel = (RatingBar) convertView
					.findViewById(R.id.shop_ratinglevel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String prefix = "FsSh_";
		String pics = map.get(prefix + "Pic") + "";
		if (!(pics.equals("") || pics.equals("null"))) {
			if (pics.indexOf("|") != -1) {
				pics = pics.substring(0, pics.indexOf("|"));
			}
			ImageLoader.getInstance().displayImage(
					SystemApplication.getServiceUrl() + pics,
					holder.shopIconIv, super.getOptions());
		}
		String serviceType = map.get("ServiceType");
		String text = "4S";
		int color = 0;
		int bg = 0;
		if (serviceType.equals("1")) {
			text = "4S";
			color = R.color.service_4s;
			bg = R.drawable.border_4s;
		} else if (serviceType.equals("2")) {
			text = "修";
			color = R.color.service_garage;
			bg = R.drawable.border_garage;
		} else if (serviceType.equals("3")) {
			text = "援";
			color = R.color.type5;
			bg = R.drawable.border_5_nocorner;
		}
		holder.type.setText(text);
		holder.type.setTextColor(context.getResources().getColor(color));
		holder.type.setBackground(context.getResources().getDrawable(bg));
		text = null;
		holder.tv_shopName.setText(map.get(prefix + "ShopName"));
		holder.text_shopaddress.setText("地址：" + map.get(prefix + "Address"));
		holder.text_shoparea.setText("区域：" + map.get("Serv_City"));
		holder.text_shoptime.setText("营业时间: 08:00-18:00");
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("Star"));
			holder.shop_rating.setText(new DecimalFormat("0.00").format(r));
		} catch (Exception e) {
		}
		holder.shop_ratinglevel.setStepSize(0.01f);
		holder.shop_ratinglevel.setRating(r);
		if (map.get("Distince") != null && !map.get("Distince").equals(""))
			holder.text_shopdistince.setText(TextUtils.FormatDistincestr(map
					.get("Distince")));

		return convertView;
	}

	class ViewHolder {
		ImageView shopIconIv;
		TextView tv_shopName, text_shoparea, text_shopdistince,
				text_shopaddress, text_shoptime, shop_rating, type;
		RatingBar shop_ratinglevel;
	}
}
