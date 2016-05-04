package com.zlh.voiceassistant.action;

import com.zlh.voiceassistant.tool.SystemTool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;

public class SearchAction {
	EditText editText;
	public void searchByText(final Context context,String name){
		editText=new EditText(context);
		if(name!=null)
		editText.setText(name);
		new AlertDialog.Builder(context).setTitle("请输入需要搜索的内容：")
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
				searchValue(context,editText.getText().toString());
			}
		}).show();
	}

	public void searchValue(final Context context,final String value){
		if(value==null){
			searchByText(context, null);
			return;
		}
		SpeechRecognitionAction.mSpeech.speak("即将搜索" + value + "是否继续",
				TextToSpeech.QUEUE_FLUSH, null);
		new AlertDialog.Builder(context).setTitle("网页搜索：" + value)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("取消", null)
				.setNegativeButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						SystemTool.search(context, value);
					}
				}).show();
	}
}
