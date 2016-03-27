package com.sy.trucksysapp.user;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sy.trucksysapp.R;
import com.sy.trucksysapp.page.BaseActivity;
import com.sy.trucksysapp.page.SystemApplication;
import com.sy.trucksysapp.utils.CommonUtils;
import com.sy.trucksysapp.utils.PreferenceUtils;

/**
 * 意见反馈界面
 * 
 * @author lxs 20150504
 * 
 */
public class FeedbackActivity extends BaseActivity implements OnClickListener, TextWatcher {

	private TextView topbase_center_text,left_num;
	private EditText edt_input_content;
	private Button btn_submit;
	private String content;
	private Context context;
	private String FeBa_Type = "bug";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		context = FeedbackActivity.this;
		topbase_center_text = (TextView) findViewById(R.id.topbase_center_text);
		topbase_center_text.setText("意见反馈");
		left_num = (TextView) findViewById(R.id.left_num);
		edt_input_content = (EditText) findViewById(R.id.edt_input_content);
		edt_input_content.addTextChangedListener(this);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
	}

	@Override
	public void back(View v) {
		// TODO Auto-generated method stub
		super.back(v);
		setResult(400);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_submit) {
			content = edt_input_content.getText().toString();
			if (content == null||content.equals("")) {
				CommonUtils.showToast(context, "请输入你要反馈的内容，谢谢！");
				return;
			} else {
				feedBack();
			}

		}

	}

	/**
	 * 反馈意见
	 */
	private void feedBack() {
		final Dialog pd = CommonUtils.ShowProcess(context, "正在提交...");
		pd.show();
		String url = SystemApplication.getInstance().getBaseurl()
				+ "TruckService/SaveFeedBack";
		String FeBa_MemberId = PreferenceUtils.getInstance(context)
				.getSettingUserId();
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams RequestParams = new RequestParams();
		RequestParams.put("FeBa_MemberId", FeBa_MemberId);
		RequestParams.put("FeBa_Content", content);
		RequestParams.put("FeBa_Type", FeBa_Type);
		client.post(url, RequestParams, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(Throwable arg0, String result) {
				// TODO Auto-generated method stub
				super.onFailure(arg0, result);
				pd.dismiss();
				CommonUtils.showToast(context, "提交失败");
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
				super.onSuccess(result);
				pd.dismiss();
				edt_input_content.setText("");
				try {
					JSONObject jsobj = new JSONObject(result);
					CommonUtils.showToast(context, jsobj.getString("Msg"));
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}

		});
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
		content = edt_input_content.getText().toString();
		int num= content.length();
		if(num>=200){
			CommonUtils.showToast(context, "您的输入已超出最大范围");
		}
			left_num.setText("您还可以输入"+(200-num) +"个字符");	
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
}
