package com.sy.trucksysapp.page.driver;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.DriverMenuModel;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.widget.ScrollGridView;

public class DriverIndexActivity extends BaseActivity {

	private ScrollGridView gridView;
	private List<DriverMenuModel> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_index);
		TextView topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("驾驶员专栏");
		list = new ArrayList<DriverMenuModel>();
		initdata();
		gridView = (ScrollGridView) findViewById(R.id.gridView);
		MenuAdapter adapter = new MenuAdapter(list, DriverIndexActivity.this);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				DriverMenuModel model = list.get(arg2);
				Intent intent = model.getIntent();
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
	}

	private void initdata() {
		list.add(new DriverMenuModel("停车", R.drawable.icon_parking, new Intent(DriverIndexActivity.this,ParkListActivity.class)));
		list.add(new DriverMenuModel("餐饮", R.drawable.icon_meal, new Intent(DriverIndexActivity.this,RestaurantListActivity.class)));
		list.add(new DriverMenuModel("住宿", R.drawable.icon_rest, new Intent(DriverIndexActivity.this,HotelListActivity.class)));
		list.add(new DriverMenuModel("资讯", R.drawable.icon_news, new Intent(DriverIndexActivity.this,NewsmainActivity.class)));
		list.add(new DriverMenuModel("路况", R.drawable.icon_road, new Intent(DriverIndexActivity.this,HighwayConditionActivity.class)));
		list.add(new DriverMenuModel("天气", R.drawable.icon_weather, new Intent(DriverIndexActivity.this,WeatherActivity.class)));
		list.add(new DriverMenuModel("聊天室", R.drawable.icon_forum, null));
		list.add(new DriverMenuModel("驾驶员招聘", R.drawable.icon_recriut, null));
		list.add(new DriverMenuModel("二手车", R.drawable.icon_truck, null));
	}
	
	

	class MenuAdapter extends BaseAdapter {

		private List<DriverMenuModel> list;
		private Context context;

		public MenuAdapter(List<DriverMenuModel> list, Context context) {
			super();
			this.list = list;
			this.context = context;
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
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			DriverMenuModel model = list.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.grid_item, null);
				holder.itemImage = (ImageView) convertView
						.findViewById(R.id.itemImage);
				holder.itemName = (TextView) convertView
						.findViewById(R.id.itemName);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.itemImage.setImageResource(model.getImageresource());
			holder.itemName.setText(model.getName());
			return convertView;
		}
	}

	class ViewHolder {
		ImageView itemImage;
		TextView itemName;
	}
}
