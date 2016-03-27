package com.sy.trucksysapp.page.shoping.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.SystemApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 特卖商品列表适配器
 * 
 * @author lxs 20150506
 * 
 */
public class HotSaleAdapter extends BaseAdapter {

	private Context context;
	private DisplayImageOptions options;
	private ArrayList<HashMap<String, String>> list;

	public HotSaleAdapter(Context context,
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		HashMap<String, String> map = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_hot_sale_product, null);
			holder.goodsIconIv = (ImageView) convertView
					.findViewById(R.id.goodsIconIv);
			holder.tv_goodsName = (TextView) convertView
					.findViewById(R.id.tv_goodsName);
			holder.tv_MarketPrice = (TextView) convertView
					.findViewById(R.id.text_price2);
			holder.tv_goodsPrice = (TextView) convertView
					.findViewById(R.id.text_price1);
			holder.tv_goodsCount = (TextView) convertView
					.findViewById(R.id.text_count);
			holder.tv_MarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String pics = map.get("Pic1");
		String[] picarray = pics.split("\\|");
		if(picarray.length!=0){
			ImageLoader.getInstance().displayImage(SystemApplication.getInstance().getServiceUrl()+ picarray[0],holder.goodsIconIv, options);
		}
		holder.tv_goodsName.setText(map.get("Title"));
		holder.tv_MarketPrice.setText("¥"+map.get("MarketPrice"));
		holder.tv_goodsPrice.setText("¥"+map.get("Price"));
		holder.tv_goodsCount.setText(map.get("Stock")+"件");
		return convertView;
	}

	class ViewHolder {
		ImageView goodsIconIv;
		TextView tv_goodsName, tv_goodsPrice, tv_MarketPrice, tv_goodsCount;
	}
}
