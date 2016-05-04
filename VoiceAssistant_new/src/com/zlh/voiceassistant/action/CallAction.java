package com.zlh.voiceassistant.action;

import java.util.List;

import com.zlh.voiceassistant.activity.MusicActivity;
import com.zlh.voiceassistant.classes.Phone;
import com.zlh.voiceassistant.classes.classDAO.PhoneDAO;
import com.zlh.voiceassistant.tool.SystemTool;
import com.zlh.voiceassistant.tool.WordTool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;

public class CallAction {

	String text;
	EditText editText;
	private TextToSpeech mSpeech;
	public void callName(final Context context,String name){
		editText=new EditText(context);
		if(name!=null)
		editText.setText(name);
		new AlertDialog.Builder(context).setTitle("请输入联系人姓名或号码：")
		.setView(editText)
		.setNegativeButton("取消", null)
		.setNeutralButton("语音识别", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				SpeechRecognitionAction speechRecognitionAction =new SpeechRecognitionAction(context);
				speechRecognitionAction.SpeechRecognition(SpeechRecognitionAction.FUNCTION_CALL);
			}
		})
		.setPositiveButton("确定", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				callAction(context,editText.getText().toString());
			}
		}).show();
	}
	
	public void callAction(final Context context,String num) {
		System.out.println("打电话给：" + num);
		if (num == null){
			callName(context ,null);
			return;
		}
		if (WordTool.isNumeric(num)) {
			text = num;
			new AlertDialog.Builder(context).setTitle("打电话给" + num)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SystemTool.call(context, text);
						}
					}).setNeutralButton("取消", null).show();
		} else {
			PhoneDAO phoneDAO = new PhoneDAO(context);
			final List<Phone> phones = phoneDAO.findByNameLike(num);
			if (phones.size() == 0) {
				new AlertDialog.Builder(context).setTitle("抱歉，未找到该联系人")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("确定", null).show();
			} else if (phones.size() == 1) {
				SpeechRecognitionAction.mSpeech.speak("即将拨打电话至"+phones.get(0).getName()+"["+phones.get(0).getPhoneNum()+"]，是否继续",
						TextToSpeech.QUEUE_FLUSH, null);
				new AlertDialog.Builder(context).setTitle("电话拨打至"+phones.get(0).getName()+"["+phones.get(0).getPhoneNum()+"]")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("取消", null).setNegativeButton("确定", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SystemTool.call(context, phones.get(0).getPhoneNum());
					}
				}).show();
			} else {
				String[] list=new String[phones.size()];
				for(int i=0;i<phones.size();i++){
					list[i]=phones.get(i).getName()+"["+phones.get(i).getPhoneNum()+"]";
				}
				SpeechRecognitionAction.mSpeech.speak("请选择所希望拨打的电话",
						TextToSpeech.QUEUE_FLUSH, null);
			    new AlertDialog.Builder(context)  
			    .setTitle("请选择所希望拨打的电话")
			    .setIcon(android.R.drawable.ic_dialog_info)                  
			    .setItems(list, new DialogInterface.OnClickListener() {  
			                                  
			         public void onClick(DialogInterface dialog, int which) {  
			        	 SystemTool.call(context, phones.get(which).getPhoneNum()); 
			            dialog.dismiss();
			         }  
			      }  
			    )  
			    .setNegativeButton("取消", null)  
			    .show();
			}
		}
	}
}
