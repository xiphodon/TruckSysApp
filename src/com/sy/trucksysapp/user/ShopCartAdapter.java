package com.sy.trucksysapp.user;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.CartProduct;
import com.sy.trucksysapp.listener.CartProductItemClickListener;
import com.sy.trucksysapp.listener.CartProductItemDelClickListener;
import com.sy.trucksysapp.listener.NumChangedListener;
import com.sy.trucksysapp.page.shoping.HotSaleDetailActivity;
import com.sy.trucksysapp.page.shoping.SaleDetailActivity;
import com.sy.trucksysapp.widget.DialogConfirmView;
import com.sy.trucksysapp.widget.NumEditText;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 购物车列表适配器
 * 
 * @author lxs 20150608
 * 
 */
public class ShopCartAdapter extends BaseAdapter {

	private Context context;
	private List<CartProduct> cProducts = new ArrayList<CartProduct>();
	private CartProductItemClickListener itemChangedListener;
	private CartProductItemDelClickListener itemdeleteListener;
	private DisplayImageOptions options;

	public ShopCartAdapter(Context context, List<CartProduct> cProducts) {
		this.context = context;
		this.cProducts = cProducts;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.img_nofound)
				.showImageForEmptyUri(R.drawable.img_nofound)
				.showImageOnFail(R.drawable.img_nofound).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void setCartProductItemChangedListener(
			CartProductItemClickListener itemChangedListener) {
		this.itemChangedListener = itemChangedListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cProducts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cProducts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_cart_listview,
					null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_cart_name);
			holder.tv_max_tips = (TextView) convertView
					.findViewById(R.id.tv_max_tips);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.iv_cart_pic);
			holder.tv_cart_type = (TextView) convertView
					.findViewById(R.id.tv_cart_type);
			holder.fnumber = (TextView) convertView
					.findViewById(R.id.tv_cart_fnumber);
			holder.checked = (CheckBox) convertView
					.findViewById(R.id.cb_cart_item_check);
			holder.sum = (TextView) convertView
					.findViewById(R.id.tv_cart_item_sum);
			holder.count = (NumEditText) convertView
					.findViewById(R.id.net_product_count);
			holder.delete = (ImageView) convertView
					.findViewById(R.id.tv_cart_item_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (cProducts.get(position).getCount() == cProducts.get(position)
				.getProductcount()) {
			cProducts.get(position).setIsmax(true);
		}
		if (cProducts.get(position).isIsmax()) {
			holder.tv_max_tips.setVisibility(View.VISIBLE);
			holder.tv_max_tips.setText("最多可购买"
					+ cProducts.get(position).getProductcount() + "个");
		} else {
			holder.tv_max_tips.setVisibility(View.INVISIBLE);
		}

		holder.count.setMaxnum(cProducts.get(position).getProductcount());
		holder.count.setNumChangedListener(new NumChangedListener() {

			@Override
			public void numchanged(int num) {
				cProducts.get(position).setCount(num);
				
				if (num >= cProducts.get(position).getProductcount()) {
					cProducts.get(position).setIsmax(true);
				} else {
					cProducts.get(position).setIsmax(false);
				}
				if (itemChangedListener != null) {
					itemChangedListener.itemNumChanged(position, num);
				}
				notifyDataSetChanged();
			}
		});
		holder.checked.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckBox checkbox = (CheckBox) arg0;
				cProducts.get(position).setCheck(checkbox.isChecked());
				if (itemChangedListener != null) {
					itemChangedListener.itemCilck(position,
							checkbox.isChecked());
				}
				notifyDataSetChanged();
			}
		});
		holder.tv_cart_type.setText(cProducts.get(position).getParameters());
		holder.checked.setChecked(cProducts.get(position).isCheck());
		DecimalFormat df = new DecimalFormat("0.00");
		holder.sum
				.setText("单价：￥" + df.format(cProducts.get(position).getSum()));
		holder.count.setNum(cProducts.get(position).getCount());
		holder.name.setText(cProducts.get(position).getFname());
		ImageLoader.getInstance().displayImage(
				cProducts.get(position).getImgsrc(), holder.icon, options);
		holder.delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				itemdeleteListener.itemDelClick(position);
			}
		});
		holder.icon.setTag(position);
		holder.icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 点击查看商品详细
				int i = (Integer) arg0.getTag();
				CartProduct cProduct = cProducts.get(i);
				String id = cProduct.getProductId();
				if (cProduct.getIsSale().equals("1")) {
					String name = cProduct.getFname();
					context.startActivity(new Intent(context,
							HotSaleDetailActivity.class).putExtra("saleid", id)
							.putExtra("salename", name));
				} else {
					// 1轮胎2内胎3轮辋4润滑油
					Intent intent = new Intent(context,
							SaleDetailActivity.class);
					intent.putExtra("saleid", id);
					intent.putExtra("Type",
							Integer.valueOf(cProduct.getProductType()));
					context.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	private void setMaxTips(View view) {

	}

	public CartProductItemDelClickListener getItemdeleteListener() {
		return itemdeleteListener;
	}

	public void setItemdeleteListener(
			CartProductItemDelClickListener itemdeleteListener) {
		this.itemdeleteListener = itemdeleteListener;
	}

	class ViewHolder {
		public ImageView delete;// 删除按钮
		private ImageView icon;
		private TextView name;
		private TextView tv_max_tips;
		// private TextView price;
		// private TextView fbagweight;
		private TextView tv_cart_type;// 类型
		private TextView fnumber;
		// private TextView remind;
		// private LinearLayout remindlayout;
		private CheckBox checked;
		private TextView sum;
		private NumEditText count;
	}
}
