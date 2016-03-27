package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author
 * @version 创建时间：确认性对话框工具类
 */
public class DialogProgressView extends Dialog {

	private Context context;
	private TextView text_progress_title, text_progress;
	private ProgressBar pb_bar;
	private Integer max;

	private static int style = R.style.dialog_fullscreen;

	// 带内容和标题，以及监听的弹窗
	// 构造属性：上下文，标题，内容，点确定的监听回调
	public DialogProgressView(Context context) {
		super(context, style);
		this.context = context;
		init();
	}

	public void setMessage(String msg) {
		text_progress_title.setText(msg);
	}

	public void setMax(Integer max) {
		this.max = max;
		pb_bar.setMax(max);
	}

	public void setProgress(Integer total) {
		pb_bar.setProgress(total);
		text_progress.setText((total*100 / max)+"%");
	}


	private void init() {
		View dialogView = LayoutInflater.from(context).inflate(
				R.layout.pop_dialog_operate_progress, null);
		text_progress_title = (TextView) dialogView
				.findViewById(R.id.text_progress_title);
		text_progress = (TextView) dialogView.findViewById(R.id.text_progress);
		pb_bar = (ProgressBar) dialogView.findViewById(R.id.pb_bar);
		setContentView(dialogView);
	}

}
