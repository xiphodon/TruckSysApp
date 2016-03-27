package com.sy.trucksysapp.widget;

import com.sy.trucksysapp.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @author
 * @version 创建时间：确认性对话框工具类
 */
public class DialogConfirmView extends Dialog {

	private Context context;
	private String title = "提示";
	private String contextmsg = "您确定吗？";
	private OnConfirmListener onconfirm = null;
	private OnCancelListener oncancel = null;

	private static int style = R.style.dialog_fullscreen;

	// 带内容和标题，以及监听的弹窗
	// 构造属性：上下文，标题，内容，点确定的监听回调
	public DialogConfirmView(Context context, String title, String contentmsg,
			OnConfirmListener onconfirm, OnCancelListener oncancel) {
		super(context, style);
		this.context = context;
		this.title = title;
		this.contextmsg = contentmsg;
		this.onconfirm = onconfirm;
		this.oncancel = oncancel;
		init();
	}

	// 带内容和标题，以及监听的弹窗
	// 构造属性：上下文，标题，内容，点确定的监听回调
	public DialogConfirmView(Context context, String title, String contentmsg,
			OnConfirmListener onconfirm) {
		super(context, style);
		this.context = context;
		this.title = title;
		this.contextmsg = contentmsg;
		this.onconfirm = onconfirm;
		init();
	}

	private void init() {

		View dialogView = LayoutInflater.from(context).inflate(
				R.layout.pop_dialog_confirm, null);

		TextView tv_yes = (TextView) dialogView.findViewById(R.id.tv_yes);
		TextView tv_no = (TextView) dialogView.findViewById(R.id.tv_no);
		TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
		TextView tv_msg = (TextView) dialogView.findViewById(R.id.tv_msg);
		tv_msg.setText(contextmsg);
		tv_title.setText(title);
		tv_no.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (oncancel != null) {
					oncancel.OnCancel();
				}
				dismiss();
			}
		});

		tv_yes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				onconfirm.OnConfirm();
				dismiss();
			}
		});
		setContentView(dialogView);
	}

	public interface OnCancelListener {
		public void OnCancel();;
	}

	public interface OnConfirmListener {
		public void OnConfirm();;
	}
}
