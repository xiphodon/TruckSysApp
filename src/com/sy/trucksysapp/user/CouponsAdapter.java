package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CouponModel;
import com.sy.trucksysapp.utils.TextUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 优惠券列表适配器
 * 
 * @author lxs 20150612
 * 
 */
public class CouponsAdapter extends BaseAdapter {

	private Context context;
	List<CouponModel> couponslist;

	public CouponsAdapter(Context context, List<CouponModel> couponslist) {
		this.context = context;
		this.couponslist = couponslist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return couponslist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return couponslist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list_coupons, null);
			holder.tv_coupon_price = (TextView) convertView
					.findViewById(R.id.tv_coupon_price);
			holder.tv_coupon_name = (TextView) convertView
					.findViewById(R.id.tv_coupon_name);
			holder.tv_coupon_time = (TextView) convertView
					.findViewById(R.id.tv_coupon_time);
			holder.check_coupon = (CheckBox) convertView
					.findViewById(R.id.check_coupon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CouponModel model = couponslist.get(position);
		DecimalFormat df = new DecimalFormat("0.00");
		holder.tv_coupon_price.setText("￥" + df.format(model.getCoup_Money()));
		holder.tv_coupon_name.setText(model.getCoupon_desc());
		holder.tv_coupon_time
				.setText("使用期限："
						+ TextUtils.FormatDateTOyyyyMMddHHsss(model
								.getCoup_StartTime())
						+ "--"
						+ TextUtils.FormatDateTOyyyyMMddHHsss(model
								.getCoup_EndTime()));
		holder.check_coupon.setChecked(model.getSelected());
		holder.check_coupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckBox c = (CheckBox) arg0;
				model.setSelected(c.isChecked());
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv_coupon_price, tv_coupon_name, tv_coupon_time;
		CheckBox check_coupon;
	}
}
