package com.sy.trucksysapp.user;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.entity.AddressInfo;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;
import com.wheelselect.lib.view.DialogWheelView;
import com.wheelselect.lib.view.DialogWheelView.OnCancelListener;
import com.wheelselect.lib.view.DialogWheelView.OnConfirmListener;

/**
 * 地址信息编辑页面
 * 
 * @author lxs 20150615
 * 
 */
public class AddressEditActivity extends BaseActivity implements
		OnClickListener {

	private Context context;
	private EditText et_edit_linkman, et_edit_phone, et_edit_address,
			et_edit_area;
	private String name, phone, address, area;
	private Button btn_edit_ok;
	private DialogWheelView dialogWheelView;
	private AddressInfo info = null;
	private ImageView topbase_delete;
	private TextView topbase_center_text;
	private String usercount ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_edit);
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_delete = (ImageView) findViewById(R.id.topbase_delete);
		topbase_delete.setOnClickListener(this);
		try {
			info = (AddressInfo) getIntent().getSerializableExtra("info");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info != null) {
			topbase_delete.setVisibility(View.VISIBLE);
			topbase_center_text.setText("编辑地址");
		} else {
			topbase_delete.setVisibility(View.GONE);
			topbase_center_text.setText("新增地址");
		}
		initView();
	}

	private void initView() {
		context = AddressEditActivity.this;
		usercount = PreferenceUtils.getInstance(context).getSettingUserId();
		et_edit_linkman = (EditText) findViewById(R.id.et_edit_linkman);
		et_edit_phone = (EditText) findViewById(R.id.et_edit_phone);
		et_edit_address = (EditText) findViewById(R.id.et_edit_address);
		et_edit_area = (EditText) findViewById(R.id.et_edit_area);
		et_edit_area.setOnClickListener(this);
		btn_edit_ok = (Button) findViewById(R.id.btn_edit_ok);
		btn_edit_ok.setOnClickListener(this);
		if (info != null) {
			et_edit_linkman.setText(info.getName());
			et_edit_phone.setText(info.getPhone());
			et_edit_address.setText(info.getAddress());
			et_edit_area.setText(info.getArea());
		}
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_edit_ok:
			SaveAddress();
			break;
		case R.id.et_edit_area:
			showArea();
			break;
		case R.id.topbase_delete:
			deleteAddress();
			break;
		default:
			break;
		}
	}

	private void deleteAddress() {
		// TODO Auto-generated method stub
		List<AddressInfo> list = CommonUtils
				.getAdresslist(AddressEditActivity.this,usercount);
		int j = -1;
		if (info != null) {
			try {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getAddressId() == info.getAddressId()) {
						j = i;
					}
				}
				list.remove(j);
				if (CommonUtils.saveAdresslist(context, list,usercount)) {
					CommonUtils.showToast(context, "删除地址成功！");
					finish();
				} else {
					CommonUtils.showToast(context, "删除地址失败！");
				}

			} catch (Exception e) {
				CommonUtils.showToast(context, "删除地址失败！");
			}
		}

	}

	/**
	 * 保存地址
	 */
	private void SaveAddress() {
		name = et_edit_linkman.getText().toString().trim();
		phone = et_edit_phone.getText().toString().trim();
		address = et_edit_address.getText().toString().trim();
		area = et_edit_area.getText().toString().trim();
		if (name == null || name.equals("")) {
			CommonUtils.showToast(context, "收件人不能为空");
			return;
		}
		if (phone == null || phone.equals("")) {
			CommonUtils.showToast(context, "电话不能为空！");
			return;

		} else if (!(CommonUtils.isPhone(phone)||CommonUtils.isFixedPhone(phone))) {
			CommonUtils.showToast(context, "请正确输入电话");
			return;
		}

		if (area == null || area.equals("")) {
			CommonUtils.showToast(context, "请选择所在城市");
			return;
		}
		if (address == null || address.equals("")) {
			CommonUtils.showToast(context, "请输入详细地址");
			return;
		}

		List<AddressInfo> list = CommonUtils
				.getAdresslist(AddressEditActivity.this,usercount);
		if (info != null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getAddressId() == info.getAddressId()) {
					AddressInfo a = list.get(i);
					a.setAddress(address);
					a.setArea(area);
					a.setName(name);
					a.setPhone(phone);
				}
			}
		} else {
			list.add(new AddressInfo(name, phone, area, address, false, list
					.size() + 1));
		}

		if (CommonUtils.saveAdresslist(context, list,usercount)) {
			CommonUtils.showToast(context, "增加地址成功！");
			finish();
		} else {
			CommonUtils.showToast(context, "增加地址失败！");
		}
	}

	/**
	 * 地区的选择
	 */
	private void showArea() {
		dialogWheelView = new DialogWheelView(AddressEditActivity.this, 2);
		dialogWheelView.setOnConfirmListener(new OnConfirmListener() {
			@Override
			public void OnConfirm(CharSequence charSequence) {
				// TODO Auto-generated method stub
				Toast.makeText(AddressEditActivity.this, charSequence, 1000)
						.show();
				et_edit_area.setText(charSequence);
				area = charSequence.toString();
			}
		});
		dialogWheelView.setOnCancelListener(new OnCancelListener() {
			@Override
			public void OnCancel() {
				// TODO Auto-generated method stub
				dialogWheelView.dismiss();
			}
		});
		dialogWheelView.show();
	}
}
