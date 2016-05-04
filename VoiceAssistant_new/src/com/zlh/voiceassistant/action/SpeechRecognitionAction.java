package com.zlh.voiceassistant.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.zlh.voiceassistant.activity.MusicActivity;
import com.zlh.voiceassistant.activity.SendActivity;
import com.zlh.voiceassistant.classes.Phone;
import com.zlh.voiceassistant.classes.classDAO.PhoneDAO;
import com.zlh.voiceassistant.tool.SystemTool;
import com.zlh.voiceassistant.tool.WordTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Toast;

public class SpeechRecognitionAction {
	public static TextToSpeech mSpeech;
	private RecognizerDialog isrDialog;
	private Context context;
	public static final int FUNCTION_ROOT = -1;
	public static final int FUNCTION_SEARCH = 0;
	public static final int FUNCTION_RUN = 1;
	public static final int FUNCTION_PLAY = 2;
	public static final int FUNCTION_CALL = 3;
	public static final int FUNCTION_MESSAGES = 4;
	private int type;
	private boolean b = true;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	public SpeechRecognitionAction(final Context context, String alse) {
		this.context = context;
		mSpeech = new TextToSpeech(context, new OnInitListener() {

			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					int result = mSpeech.setLanguage(Locale.CHINESE);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("lanageTag", "not use");
					}
				}
			}
		});
	}

	public SpeechRecognitionAction(final Context context) {
		this.context = context;
		mSpeech = new TextToSpeech(context, new OnInitListener() {

			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					int result = mSpeech.setLanguage(Locale.CHINESE);
					if (result == TextToSpeech.LANG_MISSING_DATA
							|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("lanageTag", "not use");
					}
				}
			}
		});
		isrDialog = new RecognizerDialog(context, "appid=4eb35803");
		isrDialog.setEngine("sms", null, null);
		isrDialog.setListener(new RecognizerDialogListener() {
			String text = "";

			@Override
			public void onResults(ArrayList<RecognizerResult> results,
					boolean isLast) {
				text = text + results.get(0).text;
			}

			@Override
			public void onEnd(SpeechError error) {
				if (error == null)
					if (text != "") {
						System.out.println("语音识别后字段：" + text);
						if (type == FUNCTION_ROOT) {
							wordSearch(text);
						} else {
							if (text.endsWith("。"))
								text = text.substring(0, text.length() - 1);
							if (type == FUNCTION_CALL) {
								CallAction callAction = new CallAction();
								callAction.callName(context, text);
							} else if (type == FUNCTION_MESSAGES) {
								SendAction sendAction = new SendAction();
								sendAction.sendName(context, text);
							} else if (type == FUNCTION_PLAY) {

							} else if (type == FUNCTION_RUN) {
								RunAction runAction = new RunAction();
								runAction.RunAppName(context, text);
							} else if (type == FUNCTION_SEARCH) {
								SearchAction searchAction = new SearchAction();
								searchAction.searchByText(context, text);
							}
						}
					}
				text = "";
			}
		});
	}

	public void SpeechRecognition(int type) {
		if (b)
			isrDialog.show();
		else {
			try {
				// 通过Intent传递语音识别的模式，开启语音
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				// 语言模式和自由模式的语音识别
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				// 提示语音开始
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "开始语音");
				// 开始语音识别
				context.startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		this.type = type;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 	        //回调获取从谷歌得到的数据
		 	        if(requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode==Activity.RESULT_OK){
		 	            //取得语音的字符
		 	            ArrayList<String> results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		 	             
		 	            String resultString="";
		 	            for(int i=0;i<results.size();i++){
		 	                resultString+=results.get(i);
		 	            }
						wordSearch(resultString);
		 	        }
		 	    }
	public void wordSearch(String word) {
		List<Object> list;
		if ((list = WordTool.FindAllSynonyms(word)) != null) {
			mainAction(WordTool.parsingKey(word, String.valueOf(list.get(1)),
					(Integer) list.get(0)), (Integer) list.get(0));
		}
	}

	public void mainAction(final String value, int temp) {
		switch (temp) {
		case WordTool.FUNCTION_CALL:
			CallAction callAction = new CallAction();
			callAction.callAction(context, value);
			break;
		case WordTool.FUNCTION_RUN:
			RunAction runAction = new RunAction();
			runAction.getIntentByAppName(context, value);
			break;
		case WordTool.FUNCTION_MESSAGES:
			SendAction sendAction = new SendAction();
			sendAction.sendByString(context, value);
			break;
		case WordTool.FUNCTION_SEARCH:
			SearchAction searchAction = new SearchAction();
			searchAction.searchValue(context, value);
			break;
		case WordTool.FUNCTION_PLAY:
			MusicAction musicAction = new MusicAction();
			musicAction.musicList(context);
			break;
		default:
			new AlertDialog.Builder(context).setTitle("抱歉，我没有听清楚 请您再说一遍好吗？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定", null).show();
		}
	}

}
