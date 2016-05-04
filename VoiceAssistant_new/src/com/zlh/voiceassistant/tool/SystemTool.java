package com.zlh.voiceassistant.tool;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

public class SystemTool {

	public static void call(Context context, String num) {
		Intent myIntentDial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ num));
		context.startActivity(myIntentDial);
	}

	public static void send(Context context, String num, String value) {
		SmsManager smsMgr = SmsManager.getDefault();
		PendingIntent dummyEvent = PendingIntent.getBroadcast(context, 0,
				new Intent("cc.androidos.smsdemo.IGNORE_ME"), 0);
		try {
			smsMgr.sendTextMessage(num, null, value, dummyEvent, dummyEvent);
		} catch (Exception e) {
			Log.e("SmsSending", "SendException", e);
		}
	}

	public static void search(Context context, String string) {
		Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, string);
		context.startActivity(intent);
	}

	public static boolean networkSwitch(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
}
