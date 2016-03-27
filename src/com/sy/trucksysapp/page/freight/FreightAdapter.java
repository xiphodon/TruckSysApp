package com.sy.trucksysapp.page.freight;

import java.util.ArrayList;
import java.util.HashMap;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.utils.CommonUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 列表适配器
 * 
 * @author Administrator 2015-10-22
 * 
 * 
 */
public class FreightAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<HashMap<String, String>> datalist;
	private String sysTime;
	public FreightAdapter(Context context,ArrayList<HashMap<String, String>> datalist) {
   
		this.context = context;
		this.datalist = datalist;
	}

	public void setSysTime(String sysTime) {
		this.sysTime = sysTime;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_freight_list, null);
			holder.tv_start_address = (TextView)convertView.findViewById(R.id.tv_start_address);
			holder.tv_caeattime = (TextView)convertView.findViewById(R.id.tv_caeattime);
			holder.tv_times = (TextView)convertView.findViewById(R.id.tv_times);
			holder.tv_car_type = (TextView)convertView.findViewById(R.id.tv_car_type);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		String start=datalist.get(position).get("Goods_BProvince")+datalist.get(position).get("Goods_BCity");
		String end = datalist.get(position).get("Goods_EProvince")+datalist.get(position).get("Goods_ECity");
		String ton = datalist.get(position).get("GType_Name")+datalist.get(position).get("Goods_Ton")+"吨";
		String endTime = datalist.get(position).get("Goods_EnTruckTime");
		String Size_Name=datalist.get(position).get("VSize_Name");
		String VType_Name=datalist.get(position).get("VType_Name");
		holder.tv_start_address.setText(start+"--"+end);
		holder.tv_car_type.setText(ton+"/"+Size_Name+"/"+VType_Name);
		holder.tv_caeattime.setText("发货时间："+CommonUtils.fomatTimes(datalist.get(position).get("Goods_EnTruckTime")));
		holder.tv_times.setText(CommonUtils.CompareTime(sysTime, endTime));
		return convertView;
	}

	class ViewHolder {
		TextView tv_start_address, tv_caeattime, tv_times, tv_car_type;
	}
}