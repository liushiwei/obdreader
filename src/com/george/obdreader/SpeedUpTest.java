package com.george.obdreader;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.ValueTitle;
import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;

import eu.lighthouselabs.obd.commands.SpeedObdCommand;

public class SpeedUpTest extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private int mSpeed;
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

	private Line mLine;
	private LineGraph mLineGraph;

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
				Log.e(TAG, "cmdName = " + cmdName + "  cmdResult = "
						+ cmdResult);
				if (job.getCommand().getId() == null
						|| job.getCommand().getId() != eu.lighthouselabs.obd.enums.AvailableCommandNames.SPEED) {
					return;
				}
				float speed = ((SpeedObdCommand) job.getCommand())
						.getMetricSpeed();
				Log.e(TAG, "speed =" + speed + " mSpeed = " + mSpeed);
				TextView tip = (TextView) findViewById(R.id.tip);

				if (isStart && speed <= mSpeed && isReady && speed > 0) {

					mLine.addPoint(new LinePoint((float) m_nTime / 100f, speed));
					mLineGraph.update();
				}
				if (isStart && speed > mSpeed && isReady || m_nTime > 2000) {

					timer.purge();
					timer.cancel();
					isStart = false;
					tip.setText(R.string.over);
					isReady = false;
					
					float current = (float)m_nTime/100f;
					SharedPreferences userInfo  = PreferenceManager.getDefaultSharedPreferences(SpeedUpTest.this);
					SharedPreferences.Editor editor = userInfo.edit();
					float last = userInfo.getFloat(mSpeed + "_best", -1);
					if (last > 0 && current < last||last<0) {
						editor.putFloat(mSpeed + "_best", current);
						((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+current);
					}
					
					editor.commit();
					mLine.addPoint(new LinePoint((float) m_nTime / 100f, speed));
					mLineGraph.update();
				}
				if (!isStart && speed > 0 && !isReady) {
					tip.setText(R.string.please_stop);
				}
				if (!isStart && speed == 0 && !isReady) {
					tip.setText(R.string.ready);
					isReady = true;
					mHandler.sendEmptyMessageDelayed(1, 500);
				}

			} else if (msg.what == 1) {
				TextView second = (TextView) findViewById(R.id.second);
				second.setText("3");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(
						SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(2, 1000);
			} else if (msg.what == 2) {
				TextView second = (TextView) findViewById(R.id.second);
				second.setText("2");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(
						SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(3, 1000);
			} else if (msg.what == 3) {
				TextView second = (TextView) findViewById(R.id.second);
				second.setText("1");
				second.setTextColor(Color.RED);
				Animation animation = AnimationUtils.loadAnimation(
						SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				mHandler.sendEmptyMessageDelayed(4, 1000);
			} else if (msg.what == 4) {
				TextView second = (TextView) findViewById(R.id.second);
				second.setText("GO!");
				second.setTextColor(Color.GREEN);
				Animation animation = AnimationUtils.loadAnimation(
						SpeedUpTest.this, R.anim.fade_out);
				second.startAnimation(animation);
				TextView tip = (TextView) findViewById(R.id.tip);
				tip.setText(R.string.chronograph);
				timer = null;
				task = null;
				timer = new Timer(true);
				task = new MyTimerTask(SpeedUpTest.this);
				timer.schedule(task, 0, 10);
				mLine.clear();
				mLine.addPoint(new LinePoint(0, 0));
				mLineGraph.update();
				m_nTime = 0;
				isStart = true;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed_up_test);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mLine = new Line();
		mLine.setColor(Color.parseColor("#FFBB33"));

		mLineGraph = (LineGraph) findViewById(R.id.linegraph);
		mLineGraph.addLine(mLine);
		mLineGraph.setRangeY(0, 70);
		mLineGraph.setLineToFill(-1);
		mLineGraph.setMaxX(15);
		mLineGraph.setMinX(0);
		mLineGraph.setShowTitles(true);
		List<ValueTitle> ytitles = new ArrayList<ValueTitle>();
		ytitles.add(new ValueTitle("0", 0));
		ytitles.add(new ValueTitle("10", 10));
		ytitles.add(new ValueTitle("20", 20));
		ytitles.add(new ValueTitle("30", 30));
		ytitles.add(new ValueTitle("40", 40));
		ytitles.add(new ValueTitle("50", 50));
		ytitles.add(new ValueTitle("60", 60));
		ytitles.add(new ValueTitle("70", 70));
		ytitles.add(new ValueTitle("80", 80));
		ytitles.add(new ValueTitle("90", 90));
		ytitles.add(new ValueTitle("100", 100));
		mLineGraph.setYTitles(ytitles);
		List<ValueTitle> xtitles = new ArrayList<ValueTitle>();
		xtitles.add(new ValueTitle("0", 0));
		xtitles.add(new ValueTitle("1", 1));
		xtitles.add(new ValueTitle("2", 2));
		xtitles.add(new ValueTitle("3", 3));
		xtitles.add(new ValueTitle("4", 4));
		xtitles.add(new ValueTitle("5", 5));
		xtitles.add(new ValueTitle("5", 5));
		xtitles.add(new ValueTitle("6", 6));
		xtitles.add(new ValueTitle("7", 7));
		xtitles.add(new ValueTitle("8", 8));
		xtitles.add(new ValueTitle("9", 9));
		xtitles.add(new ValueTitle("10", 10));
		xtitles.add(new ValueTitle("11", 11));
		xtitles.add(new ValueTitle("12", 12));
		xtitles.add(new ValueTitle("13", 13));
		xtitles.add(new ValueTitle("14", 14));
		xtitles.add(new ValueTitle("15", 15));
		xtitles.add(new ValueTitle("16", 16));
		xtitles.add(new ValueTitle("17", 17));
		xtitles.add(new ValueTitle("18", 18));
		xtitles.add(new ValueTitle("19", 19));
		xtitles.add(new ValueTitle("20", 20));

		mLineGraph.setXTitles(xtitles);
		mLineGraph.setOnPointClickedListener(new OnPointClickedListener() {

			@Override
			public void onClick(int lineIndex, int pointIndex) {
				// TODO Auto-generated method stub

			}

		});
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

			@Override
			public void connectFailed(String deviceName) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectingDevice(String deviceName) {
				// TODO Auto-generated method stub
				
			}
		};
		mServiceIntent = new Intent(this, ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		mServiceConnection.setServiceListener(mListener);
		getApplicationContext().bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		timerHandler = new TimerHandler(this);
		mSpeed = 60;
		((RadioButton) findViewById(R.id.speed_100))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.speed_60))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.speed_80))
				.setOnCheckedChangeListener(this);
		
		SharedPreferences preference  = PreferenceManager.getDefaultSharedPreferences(SpeedUpTest.this);
		float best = preference.getFloat("60_best", -1);
		if(best>0){
			((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+best);
		}else{
			((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+getString(R.string.no_best_score));
		}
	}

	@Override
	public void onClick(View v) {

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
		if (mServiceConnection != null && mServiceConnection.isRunning())
			stopService(mServiceIntent);
		super.onDestroy();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {

			SharedPreferences preference  = PreferenceManager.getDefaultSharedPreferences(SpeedUpTest.this);
			float best = -1;
			switch (buttonView.getId()) {
			case R.id.speed_100:
				mSpeed = 100;
				mLineGraph.setRangeY(0, 110);
				mLineGraph.update();
				best = preference.getFloat("100_best", -1);
				if(best>0){
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+best);
				}else{
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+getString(R.string.no_best_score));
				}
				break;
			case R.id.speed_80:
				mSpeed = 80;
				mLineGraph.setRangeY(0, 90);
				mLineGraph.update();
				best = preference.getFloat("80_best", -1);
				if(best>0){
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+best);
				}else{
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+getString(R.string.no_best_score));
				}
				break;
			case R.id.speed_60:
				mSpeed = 60;
				mLineGraph.setRangeY(0, 70);
				mLineGraph.update();
				best = preference.getFloat("60_best", -1);
				if(best>0){
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+best);
				}else{
					((TextView)findViewById(R.id.best_score)).setText(getString(R.string.best_score)+" "+getString(R.string.no_best_score));
				}
				break;
			}

		}
	}

}
