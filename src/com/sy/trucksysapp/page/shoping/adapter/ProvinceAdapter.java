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
 * 父ListView适配器
 * 
 * @author zihao
 * 
 */
public class ProvinceAdapter extends BaseAdapter {

	Context mContext;// 上下文对象
	private ArrayList<HashMap<String, Object>> list;
	int mPosition = 0;// 选中的位置

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            // 上下文对象
	 */
	public ProvinceAdapter(Context context,
			ArrayList<HashMap<String, Object>> list) {
		this.mContext = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		// 初始化布局控件
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.group_item_layout, null);
			holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置控件内容
		holder.tv_text.setText(list.get(position).get("Name").toString());
		if (mPosition == position) {
			holder.tv_text.setTextColor(mContext.getResources().getColor(
					R.color.list_item_text_pressed_bg));
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.group_item_pressed_bg));
		} else {
			holder.tv_text.setTextColor(mContext.getResources().getColor(
					android.R.color.black));
			convertView.setBackgroundColor(mContext.getResources().getColor(
					R.color.group_item_bg));
		}

		return convertView;
	}

	/**
	 * 获取item总数
	 */
	@Override
	public int getCount() {
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

	static class ViewHolder {
		/** 父Item名称 **/
		TextView tv_text;
	}

	/**
	 * 设置选择的位置
	 * 
	 * @param position
	 */
	public void setSelectedPosition(int position) {
		this.mPosition = position;
	}
	public int getSelectedPosition() {
		return mPosition;
	}

	public int getPosition() {
		return mPosition;
	}
}
