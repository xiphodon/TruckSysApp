package com.sy.trucksysapp.page.driver.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.TextUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * 餐馆列表适配器
 * 
 * @author lxs 20150601
 * 
 */
public class RestaurantListAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, String>> datalist;
	private Context context;
	private DisplayImageOptions options;

	public RestaurantListAdapter(Context context,
			ArrayList<HashMap<String, String>> datalist) {
		this.context = context;
		this.datalist = datalist;
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
		return datalist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return datalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		HashMap<String, String> map = datalist.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_hotel_list, null);
			holder.shopIconIv = (ImageView) convertView
					.findViewById(R.id.goodsIconIv);
			holder.tv_hotel_name = (TextView) convertView
					.findViewById(R.id.tv_hotel_name);
			holder.text_price = (TextView) convertView
					.findViewById(R.id.text_price);
			holder.text_address = (TextView) convertView
					.findViewById(R.id.text_address);
			holder.text_dis = (TextView) convertView
					.findViewById(R.id.text_dis);
			holder.cell_rating = (TextView) convertView
					.findViewById(R.id.cell_rating);
			holder.cell_ratinglevel = (RatingBar) convertView
					.findViewById(R.id.cell_ratinglevel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String pics = map.get("Rest_Pic");
		String[] picarray = pics.split("\\|");
		if (picarray.length != 0) {
			ImageLoader.getInstance().displayImage(
					SystemApplication.getInstance().getServiceUrl()
							+ picarray[0], holder.shopIconIv, options);
		}
		holder.tv_hotel_name.setText(map.get("Rest_ShopName"));
		holder.text_address.setText(map.get("Rest_Address"));
		holder.text_price.setText(map.get("Rest_Price") + "元起");
		holder.cell_ratinglevel.setStepSize(0.1f);
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		holder.cell_ratinglevel.setRating(r);
		holder.cell_rating.setText(p);
		if (map.get("Distince") != null && !map.get("Distince").equals(""))
			holder.text_dis.setText(TextUtils.FormatDistincestr(map
					.get("Distince")));
		return convertView;
	}

	class ViewHolder {
		ImageView shopIconIv;
		TextView tv_hotel_name, text_address, text_price, text_dis,
				cell_rating;
		RatingBar cell_ratinglevel;
	}
}