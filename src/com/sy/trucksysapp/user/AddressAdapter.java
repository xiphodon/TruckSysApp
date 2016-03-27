package com.sy.trucksysapp.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.AddressInfo;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 地址列表适配器
 * 
 * @author lxs 20150615
 * 
 */
public class AddressAdapter extends BaseAdapter {
	private Context context;
	private List<AddressInfo> AddressInfos = new ArrayList<AddressInfo>();
	private ViewHolder holder = null;
	private String usercount ;
	public AddressAdapter(Context context, List<AddressInfo> linkmans) {
		this.context = context;
		this.AddressInfos = linkmans;
		usercount = PreferenceUtils.getInstance(this.context).getSettingUserId();
	}

	@Override
	public int getCount() {
		return AddressInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return AddressInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = View.inflate(context, R.layout.item_address_list,
					null);
			holder = new ViewHolder();
			holder.selected = (ImageView) convertView
					.findViewById(R.id.iv_ad_selected);
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_ad_linkman);
			holder.phone = (TextView) convertView
					.findViewById(R.id.tv_ad_phone);
			holder.address = (TextView) convertView
					.findViewById(R.id.tv_address);
			holder.ad_layout = (FrameLayout) convertView
					.findViewById(R.id.fl_ad_select);
			holder.ad_edit = (FrameLayout) convertView
					.findViewById(R.id.fl_ad_edit);
			convertView.setTag(holder);
		}
		if (AddressInfos.size() == 1) {
			AddressInfos.get(position).setSelected(true);
		}
		if (AddressInfos.get(position).isSelected()) {
			holder.selected.setVisibility(View.VISIBLE);
		} else {
			holder.selected.setVisibility(View.GONE);
		}

		holder.name.setText(AddressInfos.get(position).getName());
		holder.phone.setText(AddressInfos.get(position).getPhone());
		holder.address.setText(AddressInfos.get(position).getArea()+AddressInfos.get(position).getAddress());
		holder.ad_layout.setOnClickListener(new lvButtonListener(position));
		holder.ad_edit.setOnClickListener(new lvButtonListener(position));
		return convertView;
	}

	class lvButtonListener implements OnClickListener {
		private int position;

		lvButtonListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			// 编辑地址
			case R.id.fl_ad_edit:
				Intent intent = new Intent(context, AddressEditActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("info",  AddressInfos.get(position));
				intent.putExtras(bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			// 选择地址
			case R.id.fl_ad_select:
				CommonUtils.ClearSelectedAddress(context,
						AddressInfos.get(position).getAddressId(),usercount);
				Activity activity = (Activity) context;
				activity.finish();

				break;

			default:
				break;
			}

		}
	}

	static class ViewHolder {
		private ImageView selected;
		private TextView name;
		private TextView phone;
		private TextView address;
		private FrameLayout ad_layout;
		private FrameLayout ad_edit;
	}
}
