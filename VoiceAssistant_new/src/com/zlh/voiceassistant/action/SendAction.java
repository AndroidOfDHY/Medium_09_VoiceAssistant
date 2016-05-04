package com.zlh.voiceassistant.action;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;

import com.zlh.voiceassistant.activity.SendActivity;
import com.zlh.voiceassistant.classes.Phone;
import com.zlh.voiceassistant.classes.classDAO.PhoneDAO;
import com.zlh.voiceassistant.tool.SystemTool;
import com.zlh.voiceassistant.tool.WordTool;

public class SendAction {
	EditText editText;

	public void sendName(final Context context, String name) {
		editText = new EditText(context);
		if (name != null)
			editText.setText(name);
		new AlertDialog.Builder(context).setTitle("请输入联系人姓名或号码：")
				.setView(editText).setNegativeButton("取消", null)
				.setNeutralButton("语音识别", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						SpeechRecognitionAction speechRecognitionAction = new SpeechRecognitionAction(
								context);
						speechRecognitionAction
								.SpeechRecognition(SpeechRecognitionAction.FUNCTION_MESSAGES);
					}
				}).setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendByString(context, editText.getText().toString());
					}
				}).show();
	}

	public void sendByString(final Context context, String num) {
		System.out.println("发短信给：" + num);
		if (num == null){
			sendName(context,null);
			return;
		}
		if (WordTool.isNumeric(num)) {
			Intent intent = new Intent();
			intent.setClass(context, SendActivity.class);
			intent.putExtra("name", "");
			intent.putExtra("phoneNum", num);
			context.startActivity(intent);
			return;
		} else {
			PhoneDAO phoneDAO = new PhoneDAO(context);
			final List<Phone> phones = phoneDAO.findByNameLike(num);
			if (phones.size() == 0) {
				new AlertDialog.Builder(context).setTitle("抱歉，未找到该联系人")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("确定", null).show();
			} else if (phones.size() == 1) {
				SpeechRecognitionAction.mSpeech.speak("将进入"
						+ phones.get(0).getName() + "["
						+ phones.get(0).getPhoneNum() + "]短信发送界面，是否继续",
						TextToSpeech.QUEUE_FLUSH, null);
				new AlertDialog.Builder(context)
						.setTitle(
								"为" + phones.get(0).getName() + "["
										+ phones.get(0).getPhoneNum() + "]发送短信")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setPositiveButton("取消", null)
						.setNegativeButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(context, SendActivity.class);
								intent.putExtra("name", phones.get(0).getName());
								intent.putExtra("phoneNum", phones.get(0)
										.getName());
								context.startActivity(intent);
							}
						}).show();
			} else {
				String[] list = new String[phones.size()];
				for (int i = 0; i < phones.size(); i++) {
					list[i] = phones.get(i).getName() + "["
							+ phones.get(i).getPhoneNum() + "]";
				}
				SpeechRecognitionAction.mSpeech.speak("请选择所希望发送短信的联系人",
						TextToSpeech.QUEUE_FLUSH, null);
				new AlertDialog.Builder(context).setTitle("请选择所希望发送短信的联系人")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setItems(list, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(context, SendActivity.class);
								intent.putExtra("name", phones.get(0).getName());
								intent.putExtra("phoneNum", phones.get(0)
										.getPhoneNum());

								context.startActivity(intent);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
			}
		}
	}
}
