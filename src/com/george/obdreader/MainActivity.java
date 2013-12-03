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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
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

	private final static int INIT_ANIM = 0x02;

	private final static int START_ANIM = 0x03;

	private final static int SHOW_MENU = 0x04;

	private final static int SHOW_ICON = 0x05;

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
				case INIT_ANIM:
					initAnim();
					break;
				case START_ANIM:
					Animation right = AnimationUtils.loadAnimation(
							MainActivity.this, msg.arg1);
					// AlphaAnimation right=new AlphaAnimation(0.1f, 1.0f);
					findViewById(msg.arg2).setVisibility(View.VISIBLE);
					// findViewById(msg.arg2).setAlpha(0);
					// right.setDuration(300);
					findViewById(msg.arg2).startAnimation(right);
					right.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							Message msg = new Message();
							msg.what = SHOW_MENU;
							msg.arg1 = R.anim.right_out;
							msg.arg2 = R.id.right_top;
							mHandler.sendMessageDelayed(msg, 50);
							// msg = new Message();
							// msg.what = SHOW_MENU;
							// msg.arg1 = R.anim.right_out;
							// msg.arg2 = R.id.right_bottom;
							// mHandler.sendMessageDelayed(msg, 100);
							//
							// msg = new Message();
							// msg.what = SHOW_MENU;
							// msg.arg1 = R.anim.left_out;
							// msg.arg2 = R.id.left_top;
							// mHandler.sendMessageDelayed(msg, 200);
							// msg = new Message();
							// msg.what = SHOW_MENU;
							// msg.arg1 = R.anim.left_out;
							// msg.arg2 = R.id.left_bottom;
							// mHandler.sendMessageDelayed(msg, 100);

						}
					});
					break;

				case SHOW_MENU:
					/*
					 * Animation show_menu =
					 * AnimationUtils.loadAnimation(MainActivity.this,msg.arg1);
					 * //AlphaAnimation right=new AlphaAnimation(0.1f, 1.0f);
					 * findViewById(msg.arg2).setVisibility(View.VISIBLE);
					 * //findViewById(msg.arg2).setAlpha(0);
					 * //right.setDuration(300);
					 * findViewById(msg.arg2).startAnimation(show_menu);
					 * show_menu.setAnimationListener(new AnimationListener() {
					 * 
					 * @Override public void onAnimationStart(Animation
					 * animation) { // TODO Auto-generated method stub
					 * 
					 * }
					 * 
					 * @Override public void onAnimationRepeat(Animation
					 * animation) { // TODO Auto-generated method stub
					 * 
					 * }
					 * 
					 * @Override public void onAnimationEnd(Animation animation)
					 * { Message msg = new Message(); msg.what = SHOW_ICON;
					 * msg.arg1 = R.anim.show_out; msg.arg2 = R.id.stopwatch;
					 * mHandler.sendMessageDelayed(msg, 200);
					 * 
					 * } });
					 */
					Animation show_menu = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.right_out);
					// AlphaAnimation right=new AlphaAnimation(0.1f, 1.0f);
					findViewById(R.id.right_top).setVisibility(View.VISIBLE);
					findViewById(R.id.right_top).startAnimation(show_menu);
					findViewById(R.id.right_bottom).setVisibility(View.VISIBLE);
					findViewById(R.id.right_bottom).startAnimation(show_menu);
					show_menu = AnimationUtils.loadAnimation(MainActivity.this,
							R.anim.left_out);
					findViewById(R.id.left_top).setVisibility(View.VISIBLE);
					findViewById(R.id.left_top).startAnimation(show_menu);
					findViewById(R.id.left_bottom).setVisibility(View.VISIBLE);
					findViewById(R.id.left_bottom).startAnimation(show_menu);
					show_menu.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							mHandler.sendEmptyMessage(SHOW_ICON);
						}
					});
					break;
				case SHOW_ICON:
					Animation show_icon = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.show_out);
					findViewById(R.id.stopwatch).setVisibility(View.VISIBLE);
					findViewById(R.id.stopwatch).startAnimation(show_icon);
					findViewById(R.id.obd).setVisibility(View.VISIBLE);
					findViewById(R.id.obd).startAnimation(show_icon);
					findViewById(R.id.trouble_codes).setVisibility(View.VISIBLE);
					findViewById(R.id.trouble_codes).startAnimation(show_icon);
					findViewById(R.id.maintenance).setVisibility(View.VISIBLE);
					findViewById(R.id.maintenance).startAnimation(show_icon);
					break;
				}
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (true) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//					.detectDiskReads().detectDiskWrites().detectNetwork()
//					.penaltyLog().build());
//		}
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

		findViewById(R.id.obd).setOnClickListener(this);
		findViewById(R.id.stopwatch).setOnClickListener(this);
		findViewById(R.id.trouble_codes).setOnClickListener(this);
		findViewById(R.id.maintenance).setOnClickListener(this);
		mHandler.sendEmptyMessageDelayed(INIT_ANIM, 500);
		
		Intent service = new Intent(this,OBDService.class);  
	    startService(service);    

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
		// mServiceConnection.addJobToQueue(new ObdCommandJob(speed));
		// mServiceConnection.addJobToQueue(new ObdCommandJob(dtcNum));
		// mServiceConnection.addJobToQueue(new ObdCommandJob(inMFP));
		// mServiceConnection.addJobToQueue(new ObdCommandJob(inAT));
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
			// showDialog(0);
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
		case R.id.obd:
			if (isConnected)
				startActivity(new Intent(this, MaintenanceSetting.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.stopwatch:
			//if (isConnected)
				startActivity(new Intent(this, SpeedUpTestSettings.class));
//			else {
//				Toast.makeText(getBaseContext(), R.string.device_unconn,
//						Toast.LENGTH_LONG).show();
//			}
			break;
		case R.id.trouble_codes:
			if (isConnected)
				startActivity(new Intent(this, TroubleCodes.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.money:
			startActivity(new Intent(this, MoneyActivity.class));
			break;
		case R.id.maintenance:
			startActivity(new Intent(this, MaintenanceActivity.class));
			break;
		}

	}

	@Override
	protected void onDestroy() {

		stopLiveData();
		super.onDestroy();
	}

	private void initAnim() {
		// Animation right = AnimationUtils.loadAnimation(this,
		// R.anim.right_out);
		// Animation left = AnimationUtils.loadAnimation(this, R.anim.left_out);
		//
		// findViewById(R.id.imageView4).startAnimation(right);
		// findViewById(R.id.imageView5).startAnimation(right);
		// findViewById(R.id.imageView6).startAnimation(right);
		// findViewById(R.id.imageView1).startAnimation(left);
		// findViewById(R.id.imageView2).startAnimation(left);
		// findViewById(R.id.imageView3).startAnimation(left);
		Message msg = new Message();
		msg.what = START_ANIM;
		msg.arg1 = R.anim.show_out;
		msg.arg2 = R.id.light;
		mHandler.sendMessageDelayed(msg, 10);

	}

}
