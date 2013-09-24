package com.george.obd;

import org.codeandmagic.android.gauge.GaugeView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.george.obd.io.ObdCommandJob;
import com.george.obd.io.ObdGatewayService;
import com.george.obd.io.ObdGatewayServiceConnection;

import eu.lighthouselabs.obd.commands.SpeedObdCommand;
import eu.lighthouselabs.obd.commands.control.DtcNumberObdCommand;
import eu.lighthouselabs.obd.commands.control.PendingTroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.control.TroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.engine.EngineRPMObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class MainActivity extends Activity implements OnClickListener {
	
	/**
	 * Callback for ObdGatewayService to update UI.
	 */
	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection = null;
	private boolean isBound = false;
	private int speed = 1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.obj instanceof ObdCommandJob){
				ObdCommandJob job = (ObdCommandJob) msg.obj;
				String cmdName = job.getCommand().getName();
				//String cmdResult = job.getCommand().getFormattedResult();

				//Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals " + cmdName + "?");
				//Log.e(TAG, "cmdName = "+cmdName+"  cmdResult = "+cmdResult);
				if (AvailableCommandNames.ENGINE_RPM.getValue().equals(cmdName)) {
					GaugeView mGaugeView1 = (GaugeView) findViewById(R.id.rpm_gauge_view);
					mGaugeView1.setTargetValue(((EngineRPMObdCommand) job.getCommand()).getRPM());
//					TextView rpm= (TextView)findViewById(R.id.rpm);
//					rpm.setText(((EngineRPMObdCommand) job.getCommand()).getRPM());
				} else if (AvailableCommandNames.SPEED.getValue().equals(
						cmdName)) {
					GaugeView mGaugeView1 = (GaugeView) findViewById(R.id.spd_gauge_view);
					speed = ((SpeedObdCommand) job.getCommand())
							.getMetricSpeed();
					mGaugeView1.setTargetValue(speed);
				}else if (AvailableCommandNames.DTC_NUMBER.getValue().equals(
						cmdName)) {
					Log.e(TAG, "--------------DtcNumber = "+((DtcNumberObdCommand) job.getCommand()).getTotalAvailableCodes());
					if(((DtcNumberObdCommand) job.getCommand()).getTotalAvailableCodes()>0){
						TroubleCodesObdCommand troCode = new TroubleCodesObdCommand(((DtcNumberObdCommand) job.getCommand()).getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(troCode));
						PendingTroubleCodesObdCommand pendingTroCode = new PendingTroubleCodesObdCommand(((DtcNumberObdCommand) job.getCommand()).getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(pendingTroCode));
					}
				}else if (AvailableCommandNames.TROUBLE_CODES.getValue().equals(
						cmdName)) {
					Log.e(TAG, "--------------TROUBLE_CODES = "+((TroubleCodesObdCommand) job.getCommand()).formatResult());
				}
				else if (AvailableCommandNames.PENDING_TROUBLE_CODES.getValue().equals(
						cmdName)) {
					Log.e(TAG, "--------------PENDING_TROUBLE_CODES = "+((PendingTroubleCodesObdCommand) job.getCommand()).formatResult());
				}
						
			}
			super.handleMessage(msg);
		}
		
	};
	
	private static final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				mHandler.obtainMessage(0, job).sendToTarget();
			}
		};
		
		
		mServiceIntent = new Intent(this, ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		mServiceConnection.setServiceListener(mListener);
		//startService(mServiceIntent);
		// bind service
		Log.d(TAG, "Binding service..");
		isBound = getApplicationContext().bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
		findViewById(R.id.Start_Live).setOnClickListener(this);
		findViewById(R.id.Stop).setOnClickListener(this);
		findViewById(R.id.Settings).setOnClickListener(this);
		
		GaugeView mGaugeView1 = (GaugeView) findViewById(R.id.rpm_gauge_view);
		mGaugeView1.setTargetValue(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.Start_Live:
			startLiveData();
			break;
		case R.id.Stop:
			stopLiveData();
			break;
		case R.id.Settings:
			break;
			// case COMMAND_ACTIVITY:
			// staticCommand();
			// return true;
		}
		
	}
	
	private void startLiveData() {
		Log.d(TAG, "Starting live data..");

		if (!mServiceConnection.isRunning()) {
			Log.d(TAG, "Service is not running. Going to start it..");
			startService(mServiceIntent);
			//bindService(mServiceIntent, mServiceConnection,
			//		Context.BIND_AUTO_CREATE);
		}

		// start command execution
		mHandler.post(mQueueCommands);

		// screen won't turn off until wakeLock.release()
	}
	
	private void stopLiveData() {
		Log.d(TAG, "Stopping live data..");
		if(isBound){
			getApplicationContext().unbindService(mServiceConnection);
		}
		if (mServiceConnection.isRunning())
			stopService(mServiceIntent);

		// remove runnable
		mHandler.removeCallbacks(mQueueCommands);

	}
	
	/**
	 * 
	 */
	private Runnable mQueueCommands = new Runnable() {
		public void run() {
			/*
			 * If values are not default, then we have values to calculate MPG
			 */
			
			if (mServiceConnection.isRunning())
				queueCommands();

			// run again in 2s
			//mHandler.postDelayed(mQueueCommands, 100);
		}
	};
	
	/**
	 * 
	 */
	private void queueCommands() {
//		final ObdCommandJob airTemp = new ObdCommandJob(
//				new AmbientAirTemperatureObdCommand());
//		final ObdCommandJob speed = new ObdCommandJob(new SpeedObdCommand());
//		final ObdCommandJob fuelEcon = new ObdCommandJob(
//				new FuelEconomyObdCommand());
//		final ObdCommandJob rpm = new ObdCommandJob(new EngineRPMObdCommand());
//		final ObdCommandJob maf = new ObdCommandJob(new MassAirFlowObdCommand());
//		final ObdCommandJob fuelLevel = new ObdCommandJob(
//				new FuelLevelObdCommand());
//		final ObdCommandJob ltft1 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.LONG_TERM_BANK_1));
//		final ObdCommandJob ltft2 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.LONG_TERM_BANK_2));
//		final ObdCommandJob stft1 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.SHORT_TERM_BANK_1));
//		final ObdCommandJob stft2 = new ObdCommandJob(new FuelTrimObdCommand(
//				FuelTrim.SHORT_TERM_BANK_2));
//		final ObdCommandJob equiv = new ObdCommandJob(new CommandEquivRatioObdCommand());

		// mServiceConnection.addJobToQueue(airTemp);
		//mServiceConnection.addJobToQueue(speed);
		// mServiceConnection.addJobToQueue(fuelEcon);
		Log.d(TAG, "queueCommands add rpm");
		DtcNumberObdCommand dtcNum = new DtcNumberObdCommand();
		SpeedObdCommand speed = new SpeedObdCommand();
		speed.setPriority(1);
		EngineRPMObdCommand rpm =new EngineRPMObdCommand();
		rpm.setPriority(1);
		//final ObdCommandJob rpmJob = new ObdCommandJob(rpm);
		mServiceConnection.addJobToQueue(new ObdCommandJob(rpm));
		mServiceConnection.addJobToQueue(new ObdCommandJob(speed));
		mServiceConnection.addJobToQueue(new ObdCommandJob(dtcNum));
		//mServiceConnection.addJobToQueue(maf);
		//mServiceConnection.addJobToQueue(fuelLevel);
//		mServiceConnection.addJobToQueue(equiv);
		//mServiceConnection.addJobToQueue(ltft1);
		// mServiceConnection.addJobToQueue(ltft2);
		// mServiceConnection.addJobToQueue(stft1);
		// mServiceConnection.addJobToQueue(stft2);
	}

	@Override
	protected void onStop() {
		stopLiveData();
		
		super.onStop();
	}
	
	


}
