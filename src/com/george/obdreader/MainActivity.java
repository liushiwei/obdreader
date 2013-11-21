package com.george.obdreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;
import com.george.utils.Device;

import eu.lighthouselabs.obd.commands.SpeedObdCommand;
import eu.lighthouselabs.obd.commands.control.DtcNumberObdCommand;
import eu.lighthouselabs.obd.commands.control.PendingTroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.control.TroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.engine.EngineRPMObdCommand;
import eu.lighthouselabs.obd.commands.fuel.FuelEconomyWithoutMAFObdCommand;
import eu.lighthouselabs.obd.commands.pressure.IntakeManifoldPressureObdCommand;
import eu.lighthouselabs.obd.commands.temperature.AirIntakeTemperatureObdCommand;

public class MainActivity extends Activity implements OnClickListener {

	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;
	private boolean isBound;
	private boolean isConnected;
	private int speed = 1;

	private double fuel_rpm;
	private double fuel_displacement = 2.4f;
	private double fuel_airTemp;
	private double fuel_press;
	private double fuel;

	private final static int HANDLER_FUEL = 0x01;

	private static final String TAG = "MainActivity";

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof ObdCommandJob) {
				ObdCommandJob job = (ObdCommandJob) msg.obj;
				String cmdName = job.getCommand().getName();
				String cmdResult = job.getCommand().getFormattedResult();

				// Log.d(TAG, FuelTrim.LONG_TERM_BANK_1.getBank() + " equals " +
				// cmdName + "?");
				Log.e(TAG, "cmdName = " + cmdName + "  cmdResult = "
						+ cmdResult);
				if (job.getCommand().getId() == null) {
					return;
				}
				switch (job.getCommand().getId()) {
				case ENGINE_RPM:
					GaugeView rpm_view = (GaugeView) findViewById(R.id.rpm_view);
					rpm_view.setTargetValue(((EngineRPMObdCommand) job
							.getCommand()).getRPM());
					fuel_rpm = ((EngineRPMObdCommand) job.getCommand())
							.getRPM();
					break;
				case SPEED:
					GaugeView speed_view = (GaugeView) findViewById(R.id.speed_view);
					speed = ((SpeedObdCommand) job.getCommand())
							.getMetricSpeed();
					speed_view.setTargetValue(speed);

					break;
				case INTAKE_MANIFOLD_PRESSURE:
					TextView intake_manifold_pressure = (TextView) findViewById(R.id.intake_manifold_pressure);
					String result = job.getCommand().getFormattedResult();
					intake_manifold_pressure.setText(result);
					fuel_press = ((IntakeManifoldPressureObdCommand) job
							.getCommand()).getMetricUnit();
					break;
				case AIR_INTAKE_TEMP:
					TextView intake_temperature = (TextView) findViewById(R.id.intake_temperature);
					String intake_temperature_result = job.getCommand()
							.getFormattedResult();
					intake_temperature.setText(intake_temperature_result);
					fuel_airTemp = ((AirIntakeTemperatureObdCommand) job
							.getCommand()).getTemperature() - 40;
					// fuel_airTemp = 30;
					break;

				case FUEL_ECONOMY_WITHOUT_MAF:
					TextView fuel = (TextView) findViewById(R.id.result);
					String fuel_result = job.getCommand().getFormattedResult();
					fuel.setText(fuel_result);
					break;
				case DTC_NUMBER:
					Log.e(TAG,
							"--------------DtcNumber = "
									+ ((DtcNumberObdCommand) job.getCommand())
											.getTotalAvailableCodes());
					if (((DtcNumberObdCommand) job.getCommand())
							.getTotalAvailableCodes() > 0) {
						TroubleCodesObdCommand troCode = new TroubleCodesObdCommand(
								((DtcNumberObdCommand) job.getCommand())
										.getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(
								troCode));
						PendingTroubleCodesObdCommand pendingTroCode = new PendingTroubleCodesObdCommand(
								((DtcNumberObdCommand) job.getCommand())
										.getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(
								pendingTroCode));
					}
					break;
				case TROUBLE_CODES:
					Log.e(TAG,
							"--------------TROUBLE_CODES = "
									+ ((TroubleCodesObdCommand) job
											.getCommand()).formatResult());
					break;
				case PENDING_TROUBLE_CODES:
					Log.e(TAG,
							"--------------PENDING_TROUBLE_CODES = "
									+ ((PendingTroubleCodesObdCommand) job
											.getCommand()).formatResult());
					break;
				}

			} else {
				switch (msg.what) {
				case HANDLER_FUEL:
					if (speed > 0) {
						Log.e("Fuel", "fuel_press = " + fuel_press
								+ " fuel_airTemp = " + fuel_airTemp
								+ " fuel_rpm = " + fuel_rpm + " speed = "
								+ speed);
						if (fuel_press == 0 || fuel_airTemp == 0
								|| fuel_rpm == 0 || speed == 0) {
							fuel = 0;
						} else {

							double intakeAir = (fuel_displacement * fuel_press)
									/ (8.314472 * (273 + fuel_airTemp)) * 0.725
									* (fuel_rpm) / 120 * 29;
							Log.e("Fuel", "intakeAir = " + intakeAir);
							fuel = (intakeAir / 14.64 / 0.725 * 3.6) * 100
									/ speed;
							Log.e("Fuel", "fuel = " + fuel
									+ " speedCmd.getMetricSpeed()=" + speed);
						}
					} else {
						fuel = 0;
					}
					TextView fuel_view = (TextView) findViewById(R.id.result);
					fuel_view.setText((int) fuel + "L/100KM");
					GaugeView rpm_view = (GaugeView) findViewById(R.id.fuel_view);
					rpm_view.setTargetValue((float) fuel);
					mHandler.sendEmptyMessageDelayed(HANDLER_FUEL, 500);
					break;
				}
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GaugeView mGaugeView1 = (GaugeView) findViewById(R.id.speed_view);
		speed = 0;
		mGaugeView1.setTargetValue(speed);
		mGaugeView1 = (GaugeView) findViewById(R.id.rpm_view);
		mGaugeView1.setTargetValue(0);
		mGaugeView1 = (GaugeView) findViewById(R.id.fuel_view);
		mGaugeView1.setTargetValue(0);

		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				mHandler.obtainMessage(0, job).sendToTarget();
			}

			@Override
			public void deviceConnected(String deviceName) {
				// TODO Auto-generated method stub

			}

		};

		final Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(400);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		final ImageView btn = (ImageView) findViewById(R.id.obd_connected);
		btn.startAnimation(animation);

		findViewById(R.id.home_button).setOnClickListener(this);
		findViewById(R.id.back_button).setOnClickListener(this);
		findViewById(R.id.health_setting_button).setOnClickListener(this);
		findViewById(R.id.speed_test_button).setOnClickListener(this);
		findViewById(R.id.trouble_codes_button).setOnClickListener(this);

	}

	private void stopLiveData() {
		Log.d(TAG, "Stopping live data..");
		if (isBound) {
			getApplicationContext().unbindService(mServiceConnection);
			if (mServiceConnection != null && mServiceConnection.isRunning())
				stopService(mServiceIntent);
		}

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
			// mHandler.postDelayed(mQueueCommands, 100);
		}
	};

	/**
	 * 
	 */
	private void queueCommands() {

		Log.d(TAG, "queueCommands add rpm");
		// DtcNumberObdCommand dtcNum = new DtcNumberObdCommand();
		SpeedObdCommand speed = new SpeedObdCommand();
		speed.setPriority(1);
		EngineRPMObdCommand rpm = new EngineRPMObdCommand();
		rpm.setPriority(1);
		// FuelConsumptionObdCommand fuel = new FuelConsumptionObdCommand();
		// fuel.setPriority(1);
		IntakeManifoldPressureObdCommand inMFP = new IntakeManifoldPressureObdCommand();
		inMFP.setPriority(1);
		AirIntakeTemperatureObdCommand inAT = new AirIntakeTemperatureObdCommand();
		inAT.setPriority(1);
		// FuelEconomyWithoutMAFObdCommand fuel = new
		// FuelEconomyWithoutMAFObdCommand();
		// fuel.setPriority(1);
		// final ObdCommandJob rpmJob = new ObdCommandJob(rpm);
		mServiceConnection.addJobToQueue(new ObdCommandJob(rpm));
		mServiceConnection.addJobToQueue(new ObdCommandJob(speed));
		// mServiceConnection.addJobToQueue(new ObdCommandJob(dtcNum));
		mServiceConnection.addJobToQueue(new ObdCommandJob(inMFP));
		mServiceConnection.addJobToQueue(new ObdCommandJob(inAT));
		// mServiceConnection.addJobToQueue(new ObdCommandJob(fuel));
		// mServiceConnection.addJobToQueue(maf);
		// mServiceConnection.addJobToQueue(fuelLevel);
		// mServiceConnection.addJobToQueue(equiv);
		// mServiceConnection.addJobToQueue(ltft1);
		// mServiceConnection.addJobToQueue(ltft2);
		// mServiceConnection.addJobToQueue(stft1);
		// mServiceConnection.addJobToQueue(stft2);

	}

	@Override
	protected void onPause() {
		if (mServiceConnection != null && mServiceConnection.isRunning()) {
			mServiceConnection.clearQueue();
		}
		mHandler.removeCallbacks(getConnectStatusRunable);
		mHandler.removeMessages(HANDLER_FUEL);
		super.onPause();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(R.string.net_uncon);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						startActivity(new Intent(
								"android.settings.WIFI_SETTINGS"));
					}
				});

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						MainActivity.this.finish();
					}
				});
		return builder.create();
	}

	@Override
	protected void onResume() {
		if (Device.getNetConnect(MainActivity.this) < 0) {
			showDialog(0);
		} else {
			if (mServiceConnection == null) {
				mServiceIntent = new Intent(this, ObdGatewayService.class);
				mServiceConnection = new ObdGatewayServiceConnection();
				isBound = getApplicationContext().bindService(mServiceIntent,
						mServiceConnection, Context.BIND_AUTO_CREATE);
			}
			mServiceConnection.setServiceListener(mListener);
			mHandler.post(getConnectStatusRunable);
			mHandler.sendEmptyMessageDelayed(HANDLER_FUEL, 500);
		}
		super.onResume();
	}

	Runnable getConnectStatusRunable = new Runnable() {

		@Override
		public void run() {
			if (!mServiceConnection.isConnected()
					|| !mServiceConnection.isRunning()) {
				mHandler.postDelayed(getConnectStatusRunable, 500);
			} else {
				findViewById(R.id.obd_connected).clearAnimation();
				mHandler.post(mQueueCommands);
				isConnected = true;
			}

			// start command execution

		}

	};

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
			MainActivity.this.finish();
			break;
		case R.id.health_setting_button:
			if (isConnected)
				startActivity(new Intent(this, MaintenanceSetting.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.speed_test_button:
			if (isConnected)
				startActivity(new Intent(this, SpeedUpTestSettings.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.trouble_codes_button:
			if (isConnected)
				startActivity(new Intent(this, TroubleCodes.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
			break;
		}

	}

	@Override
	protected void onDestroy() {

		stopLiveData();
		super.onDestroy();
	}

}
