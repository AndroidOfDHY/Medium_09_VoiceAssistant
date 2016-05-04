package com.zlh.voiceassistant.action;

import java.io.File;
import java.util.List;

import com.zlh.voiceassistant.activity.MusicActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.speech.tts.TextToSpeech;

public class MusicAction {
	Context context;
	public void onCreate(){
	}
	public void musicList(final Context context){
		SpeechRecognitionAction.mSpeech.speak("即将播放音乐，是否继续", TextToSpeech.QUEUE_FLUSH, null);
		new AlertDialog.Builder(context).setTitle("播放音乐")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("取消", null)
				.setNegativeButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(context, MusicActivity.class);
						context.startActivity(intent);
					}
				}).show();
	}
}
