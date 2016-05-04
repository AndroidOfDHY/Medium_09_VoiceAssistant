package com.zlh.voiceassistant.activity;

import java.util.ArrayList;

import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.zlh.voiceassistant.R;
import com.zlh.voiceassistant.tool.SystemTool;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class SendActivity extends Activity {

	private EditText editText;
	private RecognizerDialog isrDialog;
	private Context context;
	private String phoneNum;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		context = this;
		Intent intent = this.getIntent();
		phoneNum = intent.getStringExtra("phoneNum");
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
						int i = Selection.getSelectionEnd((Spannable) editText
								.getText());
						String editTextString = editText.getText().toString();
						String string1 = editTextString.substring(0, i);
						String string2 = editTextString.substring(i,
								editTextString.length());
						editText.setText(string1 + text + string2);
						Selection.setSelection((Spannable) editText.getText(),
								i + text.length());
					}
				text = "";
			}
		});
		TextView textView = (TextView) findViewById(R.id.text_send);
		textView.setText("发送给：" + intent.getStringExtra("name") + "   "
				+ phoneNum);
		editText = (EditText) findViewById(R.id.editText_send);
		((Button) findViewById(R.id.button_send_voice))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						isrDialog.show();
					}
				});
		((Button) findViewById(R.id.button_send_submit))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						pd = ProgressDialog.show(context,
								context.getString(R.string.send_submit),
								context.getString(R.string.send_sending));
						new Thread() {
							public void run() {
								Looper.prepare();
								SystemTool.send(context, phoneNum, editText
										.getText().toString());
								pd.dismiss();
								new AlertDialog.Builder(context).setTitle("发送成功")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("确定", null).show();
								Looper.loop();
							}
						}.start();
					}
				});
	}
}
