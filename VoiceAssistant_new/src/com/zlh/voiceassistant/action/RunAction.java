package com.zlh.voiceassistant.action;

import java.util.List;

import com.zlh.voiceassistant.tool.SystemTool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;

public class RunAction {

	EditText editText;
	public void RunAppName(final Context context,String name){
		editText=new EditText(context);
		if(name!=null)
		editText.setText(name);
		new AlertDialog.Builder(context).setTitle("请输入需要运行的程序：")
		.setView(editText)
		.setNegativeButton("取消", null)
		.setNeutralButton("语音识别", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SpeechRecognitionAction speechRecognitionAction =new SpeechRecognitionAction(context);
				speechRecognitionAction.SpeechRecognition(SpeechRecognitionAction.FUNCTION_RUN);
			}
		})
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getIntentByAppName(context,editText.getText().toString());
			}
		}).show();
	}
	
	public void getIntentByAppName(final Context context,final String appName) {
		if(appName==null){
			RunAppName(context,appName);
			return;
		}
		SpeechRecognitionAction.mSpeech.speak("即将运行" + appName + "是否继续",
				TextToSpeech.QUEUE_FLUSH, null);
		new AlertDialog.Builder(context).setTitle("应用启动：" + appName)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("取消", null)
				.setNegativeButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						PackageManager pm = context.getApplicationContext().getPackageManager();
						List<PackageInfo> allApps = pm.getInstalledPackages(0); // 获取本地所有已经安装的应用
						Intent intent = null;
						if (null != allApps && null != appName) {
							for (PackageInfo pi : allApps) {
								System.out.println(pi.applicationInfo.loadLabel(pm));
								if (appName.equals(pi.applicationInfo.loadLabel(pm))) {
									intent = pm.getLaunchIntentForPackage(pi.packageName);
									break;
								}
							}
						}
						if(intent!=null)
							context.startActivity(intent);
					}
				}).show();
			
	}
}
