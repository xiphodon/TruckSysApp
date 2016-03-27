package com.sy.trucksysapp.page.shoping.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.shoping.adapter.HotSaleAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpecialSaleListAdapter extends BaseAdapter {
	// 填充数据的list
	private ArrayList<HashMap<String, String>> list;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;

	public SpecialSaleListAdapter(ArrayList<HashMap<String, String>> list,
			Context context) {
		super();
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(context);
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_special_sale_product, null);
			holder.tv_goodsName = (TextView) convertView.findViewById(R.id.tv_goodsName);
			holder.text_price1 = (TextView) convertView.findViewById(R.id.text_price1);
			holder.text_price2 = (TextView) convertView.findViewById(R.id.text_price2);
			holder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			holder.text_count = (TextView) convertView.findViewById(R.id.text_count);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	class ViewHolder 
	{
		TextView tv_goodsName;
		TextView text_price1;
		TextView text_price2;
		TextView text_time;
		TextView text_count;
		HashMap<String, String> row;
	}
}
