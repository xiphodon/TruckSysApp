package com.sy.trucksysapp.widget;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.custom.vg.list.CustomAdapter;
import com.sy.trucksysapp.R;

public class SelectCustomAdapter extends CustomAdapter {
	private List<HashMap<String, String>> list;
	private Context con;
	private LayoutInflater inflater;

	public SelectCustomAdapter(Context context, List<HashMap<String, String>> list) {
		this.con = context;
		this.list = list;
		inflater = LayoutInflater.from(con);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();

			convertView = inflater.inflate(
					R.layout.adapter_item_style, null);

			vh.tv = (TextView) convertView.findViewById(R.id.tv_text);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		HashMap<String, String> map = list.get(position);
		vh.tv.setText(map.get("name"));

		return convertView;
	}

	public class ViewHolder {
		public TextView tv;
	}
}
