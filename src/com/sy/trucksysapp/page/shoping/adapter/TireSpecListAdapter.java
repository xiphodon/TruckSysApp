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

public class TireSpecListAdapter extends BaseAdapter {
	// 填充数据的list
	private ArrayList<HashMap<String, Object>> list;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;

	public TireSpecListAdapter(ArrayList<HashMap<String, Object>> list,
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
		HashMap<String, Object> map = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.line_text_item, null);
			holder.tv_text = (TextView) convertView
					.findViewById(R.id.tv_text);
//			holder.tv_AspectRatio = (TextView) convertView
//					.findViewById(R.id.tv_AspectRatio);
//			holder.tv_Diameter = (TextView) convertView
//					.findViewById(R.id.tv_Diameter);
//			holder.tv_IsCommon = (TextView) convertView
//					.findViewById(R.id.tv_IsCommon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==0){
			holder.tv_text.setText("全部");
		}else{
			holder.tv_text.setText(map.get("SpecName").toString());
//			holder.tv_text.setText(map.get("TreadWidth").toString()+"/"+map.get("AspectRatio").toString()+"R"+map.get("Diameter").toString());
		}
//		holder.tv_AspectRatio.setText(map.get("AspectRatio").toString());
//		holder.tv_Diameter.setText(map.get("Diameter").toString());
//		holder.tv_IsCommon.setText((map.get("IsCommon").toString()).equals("true")?"是":"否");
		holder.row = map;
		return convertView;
	}

	class ViewHolder {
		TextView tv_text;// 胎面宽
//		TextView tv_AspectRatio;// 扁平比
//		TextView tv_Diameter;// 直径
//		TextView tv_IsCommon;// 是否通用
		HashMap<String, Object> row;
	}
}
