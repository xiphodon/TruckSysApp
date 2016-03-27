package com.sy.trucksysapp.page.driver.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.TextUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 停车场列表适配器
 * 
 * @author lxs 20150601
 * 
 */
public class ParkListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> datalist;

	public ParkListAdapter(Context context,
			ArrayList<HashMap<String, String>> datalist) {
		this.context = context;
		this.datalist = datalist;
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
	public View getView(final int position, View convertView, ViewGroup partent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		HashMap<String, String> map = datalist.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_park_list, null);
			holder.tv_park_name = (TextView) convertView
					.findViewById(R.id.tv_park_name);
			holder.tv_park_address = (TextView) convertView
					.findViewById(R.id.tv_park_address);
			holder.tv_park_distince = (TextView) convertView
					.findViewById(R.id.tv_park_distince);
			holder.tv_to_here = (TextView) convertView
					.findViewById(R.id.tv_to_here);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_park_name.setText(map.get("Park_ShopName"));
		holder.tv_park_address.setText(map.get("Park_Address"));
		if (map.get("Distince") != null && !map.get("Distince").equals(""))
			holder.tv_park_distince.setText(TextUtils.FormatDistincestr(map
					.get("Distince")));
		// holder.tv_to_here.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// CommonUtils.showToast(context, "点击去哪"+position);
		// }
		// });
		return convertView;
	}

	class ViewHolder {
		TextView tv_park_name, tv_park_address, tv_to_here, tv_park_distince;
	}
}
