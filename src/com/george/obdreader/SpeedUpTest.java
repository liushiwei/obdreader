package com.george.obdreader;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;

import eu.lighthouselabs.obd.commands.SpeedObdCommand;

public class SpeedUpTest extends Activity implements OnClickListener {

	private float mSpeed;
	public TimerHandler timerHandler;
	public Timer timer;
	public MyTimerTask task;
	public int m_nTime = 0;// 计时
	public TextView mtimeshow = null;
	private boolean isStart;
	private boolean isReady;

	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;

	private static final String TAG = "SpeedUpTest";

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.e(TAG, "get command message");
			if (msg.obj instanceof ObdCommandJob) {
				ObdCommandJob job = (ObdCommandJob) msg.obj;
				String cmdName = job.getCommand().getName();
				String cmdResult = job.getCommand().getFormattedResult();

				// Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals " +
				// cmdName + "?");
				Log.e(TAG, "cmdName = "+cmdName+"  cmdResult = "+cmdResult);
				if (job.getCommand().getId() == null || job.getCommand().getId()!=eu.lighthouselabs.obd.enums.AvailableCommandNames.SPEED) {
					return;
				}
				float speed = ((SpeedObdCommand) job.getCommand()).getMetricSpeed();
				ProgressBar progressBar = ((ProgressBar)findViewById(R.id.test_progress));
				int progress = (int) (speed/mSpeed*100f);
				Log.e(TAG, "speed ="+speed +" mSpeed = "+mSpeed+" progress = "+progress);
				TextView tip = (TextView) findViewById(R.id.tip); 
				
				if(isStart&&progress<=100&&isReady&&progress>0){
					
					progressBar.setProgress(progress);
				}
				if(isStart&&progress>100&&isReady){
					
					timer.purge();
					timer.cancel();
					isStart = false;
					progressBar.setProgress(progress);
					tip.setText(R.string.over);
					isReady=false;
				}
				if(!isStart&&speed>0&&!isReady){
					tip.setText(R.string.please_stop);
				}
				if(!isStart&&speed==0&&!isReady){
					tip.setText(R.string.ready);
					progressBar.setProgress(0);
					isReady = true;
					mHandler.sendEmptyMessageDelayed(1, 500);
				}
				
				
			}else if(msg.what==1){
				TextView second = (TextView)findViewById(R.id.second);
				second.setText("3");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(2, 1000);
			}else if(msg.what==2){
				TextView second = (TextView)findViewById(R.id.second);
				second.setText("2");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(3, 1000);
			}
			else if(msg.what==3){
				TextView second = (TextView)findViewById(R.id.second);
				second.setText("1");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(4, 1000);
			}else if(msg.what==4){
				TextView second = (TextView)findViewById(R.id.second);
				second.setText("GO!");
				second.setTextColor(Color.GREEN);
				Animation animation = AnimationUtils.loadAnimation(SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				TextView tip = (TextView) findViewById(R.id.tip); 
				tip.setText(R.string.chronograph);
				timer = null;
				task = null;
				timer = new Timer(true);
				task = new MyTimerTask(SpeedUpTest.this);
				timer.schedule(task, 0, 10);
				m_nTime = 0;
				isStart=true;
			}
			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed_up_test_view);
		findViewById(R.id.home_button).setOnClickListener(this);
		findViewById(R.id.back_button).setOnClickListener(this);
		findViewById(R.id.speed_start).setOnClickListener(this);
		mSpeed = getIntent().getIntExtra("speed", -1);
		if (mSpeed < 0) {
			finish();
		}

		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				mHandler.obtainMessage(0, job).sendToTarget();
			}

			@Override
			public void deviceConnected(String deviceName) {
				SpeedObdCommand speed = new SpeedObdCommand();
				speed.setPriority(1);
				speed.setDelay(100);
				mServiceConnection.addJobToQueue(new ObdCommandJob(speed));
			}
		};
		mServiceIntent = new Intent(this, ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		mServiceConnection.setServiceListener(mListener);
		getApplicationContext().bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		timerHandler = new TimerHandler(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_button:
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			break;
		case R.id.back_button:
			finish();
			break;
		case R.id.speed_start:
			if (isStart) {
				timer.purge();
				timer.cancel();
				((TextView) findViewById(R.id.speed_start))
						.setText(R.string.test_start);
				isStart = false;
			} else {
				((TextView) findViewById(R.id.speed_start))
						.setText(R.string.test_stop);
				timer = null;
				task = null;
				timer = new Timer(true);
				task = new MyTimerTask(this);
				timer.schedule(task, 0, 10);
				m_nTime = 0;
				isStart = true;
				ProgressBar progressBar = ((ProgressBar)findViewById(R.id.test_progress));
				progressBar.setProgress(0);
			}

			break;

		}
	}

	private class MyTimerTask extends TimerTask {
		private SpeedUpTest me;

		public MyTimerTask(SpeedUpTest p) {
			me = p;
		}

		public void run() {
			me.m_nTime++;
			me.timerHandler.sendEmptyMessage(0);
		}
	}

	public class TimerHandler extends Handler {
		private SpeedUpTest me;

		public TimerHandler(SpeedUpTest m) {
			me = m;
		}

		@Override
		public void handleMessage(Message msg) {
			((TextView) findViewById(R.id.test_speed_time)).setText(String
					.format("%02d.%02d" + getString(R.string.second),
							me.m_nTime / 100, me.m_nTime % 100));
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mServiceConnection != null && mServiceConnection.isRunning()) {
			mServiceConnection.clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		getApplicationContext().unbindService(mServiceConnection);
		if (mServiceConnection!=null&& mServiceConnection.isRunning())
			stopService(mServiceIntent);
		super.onDestroy();
	}
	
	

}
