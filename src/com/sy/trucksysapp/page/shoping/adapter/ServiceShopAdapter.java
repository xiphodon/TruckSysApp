package com.sy.trucksysapp.page.shoping.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;

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
 * 特卖商品列表适配器
 * 
 * @author lxs 20150506
 * 
 */
public class ServiceShopAdapter extends BaseAdapter {

	private Context context;
	private DisplayImageOptions options;
	private ArrayList<HashMap<String, String>> list;

	public ServiceShopAdapter(Context context,
			ArrayList<HashMap<String, String>> list) {
		this.context = context;
		this.list = list;
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
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public DisplayImageOptions getOptions() {
		return options;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		HashMap<String, String> map = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.serviceshop_list_item, null);
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
			holder.shop_rating = (TextView) convertView
					.findViewById(R.id.shop_rating);
			holder.shop_ratinglevel = (RatingBar) convertView
					.findViewById(R.id.shop_ratinglevel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(map.get("SellerType").equals("3")){
			//加载服务商
			initservicedata(holder, map);
		}else if(map.get("SellerType").equals("1")){
			//加载供应商
			initsupplierdata(holder, map);
		}else{
			
		}
		return convertView;
	}

	private void initsupplierdata(ViewHolder holder, HashMap<String, String> map) {
		// TODO Auto-generated method stub
		String pics = map.get("Deal_HeadPic");
		ImageLoader.getInstance().displayImage(SystemApplication.getInstance().getServiceUrl() + pics,
				holder.shopIconIv, options);
		holder.tv_shopName.setText(map.get("Deal_Name"));
		holder.text_shopaddress.setText("地址：" + map.get("Deal_Address"));
		holder.text_shoparea.setText("区域：" + map.get("Deal_City"));
		holder.text_shoptime.setVisibility(View.GONE);
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		holder.shop_ratinglevel.setStepSize(0.1f);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		holder.shop_ratinglevel.setRating(r);
		holder.shop_rating.setText(p);
		DecimalFormat df = new DecimalFormat("0.0");
		try {
			holder.text_shopdistince.setText(df.format(Double.valueOf(map
					.get("Distince"))) + "km");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void initservicedata(ViewHolder holder, HashMap<String, String> map) {
		// TODO Auto-generated method stub
		String pics = map.get("Serv_HeadPic");
		ImageLoader.getInstance().displayImage(SystemApplication.getInstance().getServiceUrl() + pics,
				holder.shopIconIv, options);
		holder.tv_shopName.setText(map.get("Serv_Name"));
		holder.text_shopaddress.setText("地址：" + map.get("Serv_Address"));
		holder.text_shoparea.setText("区域：" + map.get("Serv_City"));
		holder.text_shoptime.setText("营业时间: " + map.get("Serv_BusinessHours"));
		float r = 0f;
		try {
			r = Float.parseFloat(map.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		holder.shop_ratinglevel.setStepSize(0.1f);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		holder.shop_ratinglevel.setRating(r);
		holder.shop_rating.setText(p);
		DecimalFormat df = new DecimalFormat("0.0");
		try {
			holder.text_shopdistince.setText(df.format(Double.valueOf(map
					.get("Distince"))) + "km");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	class ViewHolder {
		ImageView shopIconIv;
		TextView tv_shopName, text_shoparea, text_shopdistince,
				text_shopaddress, text_shoptime, shop_rating;
		RatingBar shop_ratinglevel;
	}
}
