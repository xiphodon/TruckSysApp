package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;
import com.sy.trucksysapp.listener.NumChangedListener;
import com.sy.trucksysapp.utils.CommonUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 数量控件
 * 
 * @author Cool
 * 
 */
@SuppressLint("NewApi")
public class NumEditText extends LinearLayout {
	private ImageView numadd = null;
	private ImageView numdes = null;
	private EditText numedit = null;
	private NumChangedListener numChangedListener = null;
	private Context context;

	/**
	 * 设置数字改变监听器
	 * 
	 * @param numChangedListener
	 */
	public void setNumChangedListener(NumChangedListener numChangedListener) {
		this.numChangedListener = numChangedListener;
	}

	int maxnum = 999999;

	public void setNumEditClickListener(OnClickListener clickListener) {
		numedit.setOnClickListener(clickListener);
	}

	public boolean isEmpty() {
		return ("".equals(numedit.getText().toString().trim()) || numedit
				.getText().toString().trim().length() == 0) ? true : false;
	}

	/**
	 * 设置最大值，默认为99999
	 * 
	 * @param maxnum
	 */
	public void setMaxnum(int maxnum) {
		this.maxnum = maxnum;
	}

	public int getNum() {
		String num = numedit.getText().toString().trim();
		return ("".equals(num) || num.length() == 0) ? 1 : Integer
				.parseInt(num);
	}

	/**
	 * 设置值
	 * 
	 * @param num
	 */
	public void setNum(int num) {
		if (num <= 0) {
			num = 1;
		} else if (num > maxnum) {
			num = maxnum;
		}
		numedit.setText(String.valueOf(num));
	}

	public NumEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public NumEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public NumEditText(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		initView();

	}

	private void initView() {
		// TODO Auto-generated method stub
		numadd = (ImageView) findViewById(R.id.iv_product_num_add);
		numdes = (ImageView) findViewById(R.id.iv_product_num_des);
		numedit = (EditText) findViewById(R.id.et_product_num);

		numadd.setOnClickListener(clickListener);
		numdes.setOnClickListener(clickListener);
		numdes.setEnabled(false);
		numedit.addTextChangedListener(textWatcher);
		numedit.setSelection(numedit.length());
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			String numstr = numedit.getText().toString().trim();
			int num = ("".equals(numstr) || numstr.length() == 0) ? 0 : Integer
					.parseInt(numstr);
			numedit.clearFocus();
			if (view == numadd) {
				numadd.requestFocus();
				if (num + 1 <= maxnum) {
					numedit.setText(String.valueOf(num + 1));
				} else {
					CommonUtils.showToast(context, "库存不足");
				}

			} else if (view == numdes) {
				numadd.requestFocus();
				numedit.setText(String.valueOf(num - 1));
			}
		}
	};

	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String num = s.toString().trim();
			if ("1".equals(num)) {
				numedit.setSelection(numedit.length());
				numdes.setEnabled(false);
			}
			if ("".equals(num) || num.length() == 0) {
				numdes.setEnabled(false);
			} else {
				numedit.setSelection(numedit.length());

				if (Integer.parseInt(num) == 0) {
					numdes.setEnabled(false);
					numedit.setText("1");
					return;
				}
				if (Integer.parseInt(num) > 1) {
					numdes.setEnabled(true);
					if (Integer.parseInt(num) == maxnum) {
						numadd.setEnabled(false);
					} else if (Integer.parseInt(num) < maxnum) {
						numadd.setEnabled(true);
					} else if ((Integer.parseInt(num) > maxnum)&&maxnum!=0) {
						numadd.setEnabled(false);
						CommonUtils.showToast(context, "库存不足");
						numedit.setText(String.valueOf(maxnum));
					}
				}

			}

			if (numChangedListener != null) {
				if ("".equals(num) || num.length() == 0) {
					numChangedListener.numchanged(1);
				} else {
					if ((Integer.parseInt(num) == 0)) {
						numChangedListener.numchanged(1);
					} else if (Integer.parseInt(num) > maxnum) {
						numChangedListener.numchanged(maxnum);
					} else {
						numChangedListener.numchanged(Integer.parseInt(num));
					}
				}
			}
		}
	};

}
