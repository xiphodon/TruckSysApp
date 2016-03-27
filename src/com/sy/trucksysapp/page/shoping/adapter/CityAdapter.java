package com.sy.trucksysapp.page.shoping.adapter;


import java.util.ArrayList;
import java.util.HashMap;

import com.sy.trucksysapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 子ListView适配器
 * 
 * @author zihao
 * 
 */
public class CityAdapter extends BaseAdapter {

	Context mContext;
	private ArrayList<HashMap<String, Object>> list;

	public CityAdapter(Context mContext, ArrayList<HashMap<String, Object>> list) {
		super();
		this.mContext = mContext;
		this.list = list;
	}


	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public CityAdapter(Context context) {
		mContext = context;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.child_item_layout, null);
			holder.childText = (TextView) convertView
					.findViewById(R.id.child_textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.childText.setText(list.get(position).get("Name").toString());
		return convertView;
	}

	static class ViewHolder {
		TextView childText;
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	/**
	 * 获取某一个Item的内容
	 */
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	/**
	 * 获取当前item的ID
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

}
