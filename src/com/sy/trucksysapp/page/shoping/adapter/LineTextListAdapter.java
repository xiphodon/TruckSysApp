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

public class LineTextListAdapter extends BaseAdapter {
	// 填充数据的list
	private ArrayList<HashMap<String, Object>> list;
	// 上下文
	private Context context;
	// 用来导入布局
	private LayoutInflater inflater = null;
	private HashMap<Integer, View> viewmap;

	public LineTextListAdapter(ArrayList<HashMap<String, Object>> list,
			Context context) {
		super();
		this.list = list;
		this.context = context;
		viewmap = new HashMap<Integer, View>();
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
			convertView = inflater.inflate(
					R.layout.line_text_item, null);
			holder.tv_text = (TextView) convertView
					.findViewById(R.id.tv_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_text.setText(map.get("Name").toString());
		holder.row = map;
		getViewmap().put(position, convertView);
		return convertView;
	}

	public HashMap<Integer, View> getViewmap() {
		return viewmap;
	}


	class ViewHolder {
		TextView tv_text;// 胎面宽
		HashMap<String, Object> row;
	}
}
