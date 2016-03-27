package com.sy.trucksysapp.page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/***
 * 开机自启动服务，上传会员位置信息
 * 
 * @author Administrator
 * 
 */
public class StartBroadcastReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ACTION)) {
			Intent i = new Intent(Intent.ACTION_RUN);
			i.setClass(context,
					com.sy.trucksysapp.page.UploadlocationService.class);
			context.startService(i);
		}
	}

}
