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
 * 生活服务适配器
 * 
 * @author lxs 20150527
 * 
 */
public class HotelListAdapter extends BaseAdapter {
	//
	// private static final int TYPE_COUNT = 3;// item类型的总数
	// private static final int TYPE_PARK = 0;// 停车类型
	// private static final int TYPE_DINING = 1;// 餐饮类型
	// private static final int TYPE_STAY = 2;// 住宿类型
	private Context context;
	private DisplayImageOptions options;
	private ArrayList<HashMap<String, String>> datalist;

	public HotelListAdapter(Context context,
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
		HashMap<String, String> row = datalist.get(position);
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
		holder.tv_hotel_name.setText(row.get("Hote_ShopName"));
		holder.text_price.setText(row.get("Hote_Price") + "元起");
		holder.text_address.setText(row.get("Hote_Address"));
		float r = 0f;
		try {
			r = Float.parseFloat(row.get("StartCount"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p = decimalFormat.format(r);// format 返回的是字符串
		holder.cell_ratinglevel.setRating(r);
		holder.cell_rating.setText(p);
		if (row.get("Distince") != null && !row.get("Distince").equals(""))
			holder.text_dis.setText(TextUtils.FormatDistincestr(row
					.get("Distince")));
		String[] picarray = row.get("Hote_Pic").split("\\|");
		if (picarray.length != 0) {
			ImageLoader.getInstance().displayImage(
					SystemApplication.getInstance().getServiceUrl()
							+ picarray[0], holder.shopIconIv, options);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView shopIconIv;
		TextView tv_hotel_name, text_address, text_price, text_dis,
				cell_rating;
		RatingBar cell_ratinglevel;
	}
}
