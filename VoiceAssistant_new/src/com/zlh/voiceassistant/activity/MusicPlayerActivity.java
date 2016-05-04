package com.zlh.voiceassistant.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zlh.voiceassistant.R;
import com.zlh.voiceassistant.classes.Music;



import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends Activity implements OnSeekBarChangeListener,
		MediaPlayer.OnErrorListener, Runnable,SensorEventListener {

	public static MediaPlayer player = null;
	private TextView textName;
	private TextView textSinger;
	private TextView textStartTime;
	private TextView textEndTime;
	private SeekBar seekBar1;
	private ImageView icon;
	private ImageButton imageBtnLast;
	private ImageButton imageBtnRewind;
	private ImageButton imageBtnPlay;
	private ImageButton imageBtnForward;
	private ImageButton imageBtnNext;
	private ImageButton imageBtnLoop;
	private boolean isLoop = false;
	public static int _id = -1;
	private int cruID = -1;
	private List<Music> lists;
	private AudioManager audioManager;// 音量管理者
	private int maxVolume;// 最大音量
	private int currentVolume;// 当前音量
	private SeekBar seekBarVolume;
	
	private SensorManager sensorManager;
	private boolean mRegisteredSensor;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int position = msg.what;
			int total = player.getDuration();
			int progress = position * 100 / total;
			textStartTime.setText(toTime(position));

			seekBar1.setProgress(progress);
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.music);

		textName = (TextView) this.findViewById(R.id.music_name);
		textSinger = (TextView) this.findViewById(R.id.music_singer);
		textStartTime = (TextView) this.findViewById(R.id.music_start_time);
		textEndTime = (TextView) this.findViewById(R.id.music_end_time);
		seekBar1 = (SeekBar) this.findViewById(R.id.music_seekBar);
		icon = (ImageView) this.findViewById(R.id.image_icon);
		imageBtnLast = (ImageButton) this.findViewById(R.id.music_lasted);
		imageBtnRewind = (ImageButton) this.findViewById(R.id.music_rewind);
		imageBtnPlay = (ImageButton) this.findViewById(R.id.music_play);
		imageBtnForward = (ImageButton) this.findViewById(R.id.music_foward);
		imageBtnNext = (ImageButton) this.findViewById(R.id.music_next);
		imageBtnLoop = (ImageButton) this.findViewById(R.id.music_loop);
		seekBarVolume = (SeekBar) this.findViewById(R.id.music_volume);

		setListData();

		imageBtnLast.setOnClickListener(new MyListener());
		imageBtnRewind.setOnClickListener(new MyListener());
		imageBtnPlay.setOnClickListener(new MyListener());
		imageBtnForward.setOnClickListener(new MyListener());
		imageBtnNext.setOnClickListener(new MyListener());
		imageBtnLoop.setOnClickListener(new MyListener());

		seekBar1.setOnSeekBarChangeListener(this);
		
		sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获得最大音量
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 获得当前音量
		seekBarVolume.setMax(maxVolume);
		seekBarVolume.setProgress(currentVolume);
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, AudioManager.FLAG_ALLOW_RINGER_MODES);
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		int id = getIntent().getIntExtra("id", 1);
		// Toast.makeText(this, id + "", 1).show();
		Music m = lists.get(id);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		if (id != _id) {
			_id = id;
			if (player != null) {
				if (player.isPlaying()) {
					player.release();
					player = null;
				}
			}
			String url = m.getUrl();
			Uri myUri = Uri.parse(url);

			player = new MediaPlayer();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
			try {
				player.setDataSource(getApplicationContext(), myUri);
				player.prepare();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			player.start();

			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			int icon = R.drawable.play1;
			CharSequence tickerText = "正在播放音乐 . . . ";

			long when = System.currentTimeMillis();
			Notification notification = new Notification(icon, tickerText, when);
			Context context = getApplicationContext();
			CharSequence contentTitle = "正在播放音乐 . . . ";
			CharSequence contentText = "";
			Intent notificationIntent = new Intent(this, MusicActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			mNotificationManager.notify(1, notification);
		}
		if (player != null) {
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					// 下一首
					player.reset();
					if (isLoop == true) {
						_id = _id - 1;
					}
					forward();
				}
			});
		}
		new Thread(this).start();

		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		List<Sensor> sensors=sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(sensors.size()>0){
			Sensor sensor=sensors.get(0);
			mRegisteredSensor=sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
			Log.e("--------------", sensor.getName());
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(mRegisteredSensor){
			sensorManager.unregisterListener(this);
			mRegisteredSensor=false;
		}
		super.onPause();
	}

	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == imageBtnLast) {
				// 第一首
				player.release();
				player = null;
				first();

			} else if (v == imageBtnRewind) {
				// 前一首
				player.release();
				player = null;
				rewind();
			} else if (v == imageBtnPlay) {
				// 正在播放
				if (player.isPlaying()) {
					player.stop();
					imageBtnPlay.setImageResource(R.drawable.play1);
				} else {
					try {
						player.prepare();
						player.start();
						imageBtnPlay.setImageResource(R.drawable.pause1);
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (v == imageBtnForward) {
				// 下一首
				player.release();
				player = null;
				forward();
			} else if (v == imageBtnNext) {
				// 最后一首
				player.release();
				player = null;
				last();
			} else if (v == imageBtnLoop) {
				if (isLoop == false) {
					// 顺序播放
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_spec);
					isLoop = true;
				} else {
					// 单曲播放
					imageBtnLoop
							.setBackgroundResource(R.drawable.play_loop_sel);
					isLoop = false;
				}
			}
			/*
			 * player.setOnCompletionListener(new OnCompletionListener() {
			 * 
			 * @Override public void onCompletion(MediaPlayer mp) { // TODO
			 * Auto-generated method stub // 下一首 player.release(); player =
			 * null; if(isLoop==true){ _id=_id-1; } forward(); } });
			 */
		}
	}

	private void first() {
		_id = 0;
		Music m = lists.get(0);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// 下一首
				player.reset();
				if (isLoop == true) {
					_id = _id - 1;
				}
				forward();
			}
		});

	}

	private void rewind() {
		if (_id <= 0) {
			_id = lists.size() - 1;
		} else {
			_id = _id - 1;
		}
		Music m = lists.get(_id);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// 下一首
				player.reset();
				if (isLoop == true) {
					_id = _id - 1;
				}
				forward();
			}
		});

	}

	private void forward() {
		if (_id >= lists.size() - 1) {
			_id = 0;
		} else {
			_id = _id + 1;
		}
		Music m = lists.get(_id);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// 下一首
				player.reset();
				if (isLoop == true) {
					_id = _id - 1;
				}
				forward();
			}
		});

	}

	private void last() {
		_id = lists.size() - 1;
		Music m = lists.get(lists.size() - 1);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();
		player.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// 下一首
				player.reset();
				if (isLoop == true) {
					_id = _id - 1;
				}
				forward();
			}
		});

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	/*
	 * @Override protected void onDestroy() { // TODO Auto-generated method stub
	 * player.release(); player = null; super.onDestroy();
	 * 
	 * }
	 */
	private void setListData() {
		lists = new ArrayList<Music>();

		ContentResolver cr = getApplication().getContentResolver();
		if (cr == null) {
			return;
		}
		// 获取所有歌曲
		Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (null == cursor) {
			return;
		}
		if (cursor.moveToFirst()) {
			do {
				Music m = new Music();
				String title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String singer = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				if ("<unknown>".equals(singer)) {
					singer = "未知艺术家";
				}
				String album = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.SIZE));
				long time = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				String url = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));
				m.setTitle(title);
				m.setSinger(singer);
				m.setAlbum(album);
				m.setSize(size);
				m.setTime(time);
				m.setUrl(url);
				lists.add(m);
			} while (cursor.moveToNext());
		}

	}

	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	public String toTime(int time) {

		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		seekBar1.setProgress(seekBar.getProgress());
		// Toast.makeText(this, seekBar.getProgress() + "", 1).show();
		player.reset();
		Music m = lists.get(_id);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		imageBtnPlay.setImageResource(R.drawable.pause1);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.seekTo(seekBar.getProgress() * player.getDuration() / 100);
		player.start();

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		player.reset();
		Music m = lists.get(_id);
		SongsActivity.listMusic.add(m);
		textName.setText(m.getTitle());
		textSinger.setText(m.getSinger());
		textEndTime.setText(toTime((int) m.getTime()));

		imageBtnPlay.setImageResource(R.drawable.pause1);

		String url = m.getUrl();
		Uri myUri = Uri.parse(url);
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(getApplicationContext(), myUri);
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player.start();
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean isTrue = true;
		while (isTrue == true) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (player == null) {
				isTrue = false;

			} else {
				int position = player.getCurrentPosition();
				Message msg = handler.obtainMessage();
				msg.what = position;
				handler.sendEmptyMessage(position);
			}

		}
	}
	//重力感应 甩歌代码
	private static final int SHAKE_THRESHOLD = 3000;
	private long lastUpdate=0;
	private double last_x=0;
	private double last_y= 4.50;
	private double last_z=9.50;
	//这个控制精度，越小表示反应越灵敏
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		//处理精准度改变
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			long curTime = System.currentTimeMillis();
			
			// 每200毫秒检测一次   
			if ((curTime - lastUpdate) > 100) { 
			long diffTime = (curTime - lastUpdate);  
			lastUpdate = curTime;   
			double x=event.values[SensorManager.DATA_X];
			double y=event.values[SensorManager.DATA_Y];
			double z=event.values[SensorManager.DATA_Z];
			float speed = (float) (Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000);   			  
			if (speed > SHAKE_THRESHOLD) {   
                        //检测到摇晃后执行的代码
				  if(player.isPlaying()){
					  player.pause();
					  imageBtnPlay.setImageResource(R.drawable.play1);
				  }else{
					  player.start();
					  imageBtnPlay.setImageResource(R.drawable.pause1);
				  }
			}  
			last_x = x;   
			last_y = y;   
			last_z = z;   
			}
		}
	}

}
