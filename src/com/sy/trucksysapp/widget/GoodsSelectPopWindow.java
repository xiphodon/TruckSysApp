package com.sy.trucksysapp.widget;

import java.lang.reflect.Method;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.utils.CommonUtils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class GoodsSelectPopWindow {
	@SuppressWarnings("unused")
	private Context context;
	private PopupWindow popupWindow;

	// private OnItemClickListener listener;

	@SuppressWarnings("deprecation")
	public GoodsSelectPopWindow(Context context, View view,int width,int height) {
		// TODO Auto-generated constructor stub
		this.context = context;
		popupWindow = new PopupWindow(view,width,height);
		// 实例化一个ColorDrawable颜色为半透明
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
//		CommonUtils.setPopupWindowTouchModal(popupWindow, false);
//		ColorDrawable dw = new ColorDrawable(0x343a41);
//		popupWindow.setBackgroundDrawable(dw);
		//view.setFocusableInTouchMode(true);
	}

	public Boolean isShowing() {
		if(popupWindow!=null){
			return popupWindow.isShowing();
		}
		else{
			return true;
		}
		// TODO Auto-generated method stub

	}
	
	public void showAsDropDownleft(View parent, int xoff, int yoff) {
		popupWindow.setAnimationStyle(R.style.PopupleftAnimation);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
		popupWindow.showAsDropDown(parent,xoff , xoff);
	}
	
	public void showAsDropDownright(View parent, int xoff, int yoff) {
		popupWindow.setAnimationStyle(R.style.PopuprightAnimation);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
		popupWindow.showAsDropDown(parent, xoff, yoff);
	}
	
	
	
	public void dismiss() {
		popupWindow.dismiss();
	}

}
