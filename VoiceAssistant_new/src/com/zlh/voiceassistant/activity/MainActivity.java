package com.zlh.voiceassistant.activity;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.zlh.voiceassistant.R;
import com.zlh.voiceassistant.action.SpeechRecognitionAction;
import com.zlh.voiceassistant.pocketsphinx.RecognitionListener;
import com.zlh.voiceassistant.pocketsphinx.RecognizerTask;
import com.zlh.voiceassistant.tool.GridDialog;
import com.zlh.voiceassistant.tool.SystemTool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity implements RecognitionListener {
	private Context context;
	private RecognizerTask rec;
	private Thread rec_thread;
	private Date start_date;
	private float speech_dur;
	private boolean listening;
	private ImageButton button;
	private String text;
	
    Handler mHandler = new Handler() {  
    @Override public void handleMessage(Message msg) {//覆盖handleMessage方法  
        switch (msg.what) {//根据收到的消息的what类型处理  
            case BIND_ABOVE_CLIENT:  
            	SpeechRecognitionAction speechRecognitionAction=new SpeechRecognitionAction(context);
        		System.out.println("htp:"+text);
        		speechRecognitionAction.wordSearch(text);
                break;  
            default:  
                super.handleMessage(msg);//这里最好对不需要或者不关心的消息抛给父类，避免丢失消息  
                break;  
        }  
    }  
    };  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		button = (ImageButton) findViewById(R.id.button_speak);
		button.setBackgroundResource(R.drawable.voicebutton);
		if (SystemTool.networkSwitch(context)) {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SpeechRecognitionAction speechRecognitionAction = new SpeechRecognitionAction(
							context);
					speechRecognitionAction
							.SpeechRecognition(SpeechRecognitionAction.FUNCTION_ROOT);
				}
			});
		} else {
			System.loadLibrary("pocketsphinx_jni");
			this.rec = new RecognizerTask();
			this.rec_thread = new Thread(this.rec);
			listening = false;
			this.rec.setRecognitionListener(this);
			rec_thread.start();
			button.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						button.setBackgroundResource(R.drawable.voicebutton_on);
						start_date = new Date();
						listening = true;
						rec.start();
						break;
					case MotionEvent.ACTION_UP:
						button.setBackgroundResource(R.drawable.voicebutton);
						Date end_date = new Date();
						long nmsec = end_date.getTime() - start_date.getTime();
						speech_dur = (float) nmsec / 1000;
						if (listening) {
							Log.d(getClass().getName(), "Showing Dialog");
							listening = false;
						}
						rec.stop();
						break;
					default:
						;
					}
					return false;
				}

			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "功能菜单");
		menu.add(0, 2, 2, "设置");
		return super.onCreateOptionsMenu(menu);
	}

	public void onPartialResults(Bundle b) {
		String hyp = b.getString("hyp");
	}

	public void onResults(Bundle b) {
		String hyp = b.getString("hyp");
		Date end_date = new Date();
		long nmsec = end_date.getTime() - start_date.getTime();
		float rec_dur = (float) nmsec / 1000;
		System.out.println("p:"
				+ (String.format("%.2f seconds %.2f xRT", speech_dur, rec_dur
						/ speech_dur)));
		Message msg=new Message();
		msg.what=BIND_ABOVE_CLIENT;
		text=hyp.replace(" ", "");
		mHandler.sendMessage(msg);
		Log.d(getClass().getName(), "Hiding Dialog");
	}

	public void onError(int err) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			GridDialog dialog = new GridDialog(MainActivity.this);
			dialog.bindEvent(MainActivity.this);
			dialog.show();
		} else if (item.getItemId() == 2) {
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}
}
