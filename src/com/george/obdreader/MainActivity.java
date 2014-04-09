package com.george.obdreader;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.george.obdreader.config.BaseSetting;
import com.george.obdreader.config.ConfigurationActivity;
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

public class MainActivity extends FragmentActivity implements OnClickListener {

	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentsList;
	private FirstIndexFragment mFirstfragment;

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

	private final static int WIFI_CONNECT_FAILED = 0x06;

	private final static int WIFI_CONNECTING = 0x07;

	private static final String TAG = "MainActivity";

	private IPostListener mListener = new IPostListener() {
		public void stateUpdate(ObdCommandJob job) {
			mHandler.obtainMessage(0, job).sendToTarget();
		}

		@Override
		public void deviceConnected(String deviceName) {
			// TODO Auto-generated method stub

		}

		@Override
		public void connectFailed(String deviceName) {
			mHandler.sendEmptyMessage(WIFI_CONNECT_FAILED);
		}

		@Override
		public void connectingDevice(String deviceName) {
			mHandler.sendEmptyMessage(WIFI_CONNECTING);
		}

	};

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
					GaugeView rpm_view = (GaugeView) mFirstfragment
							.getRootView().findViewById(R.id.rpm_view);
					rpm_view.setTargetValue(((EngineRPMObdCommand) job
							.getCommand()).getRPM());
					fuel_rpm = ((EngineRPMObdCommand) job.getCommand())
							.getRPM();
					break;
				case SPEED:
					// GaugeView speed_view = (GaugeView)
					// findViewById(R.id.speed_view);
					speed = ((SpeedObdCommand) job.getCommand())
							.getMetricSpeed();
					// speed_view.setTargetValue(speed);

					break;
				case INTAKE_MANIFOLD_PRESSURE:
					// TextView intake_manifold_pressure = (TextView)
					// findViewById(R.id.intake_manifold_pressure);
					String result = job.getCommand().getFormattedResult();
					// intake_manifold_pressure.setText(result);
					fuel_press = ((IntakeManifoldPressureObdCommand) job
							.getCommand()).getMetricUnit();
					break;
				case AIR_INTAKE_TEMP:
					// TextView intake_temperature = (TextView)
					// findViewById(R.id.intake_temperature);
					String intake_temperature_result = job.getCommand()
							.getFormattedResult();
					// intake_temperature.setText(intake_temperature_result);
					fuel_airTemp = ((AirIntakeTemperatureObdCommand) job
							.getCommand()).getTemperature() - 40;
					// fuel_airTemp = 30;
					break;

				case FUEL_ECONOMY_WITHOUT_MAF:
					// TextView fuel = (TextView) findViewById(R.id.result);
					// String fuel_result =
					// job.getCommand().getFormattedResult();
					// fuel.setText(fuel_result);
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
					// TextView fuel_view = (TextView)
					// findViewById(R.id.result);
					// fuel_view.setText((int) fuel + "L/100KM");
					// GaugeView rpm_view = (GaugeView)
					// findViewById(R.id.fuel_view);
					// rpm_view.setTargetValue((float) fuel);
					// mHandler.sendEmptyMessageDelayed(HANDLER_FUEL, 500);
					break;
				case INIT_ANIM:
					initAnim();
					break;
				case START_ANIM:
					Log.d("FirstIndexFragment", "mFirstfragment="
							+ mFirstfragment);
					View first = mPager.getChildAt(0);
					if (first.findViewById(R.id.light) != null) {
						Animation right = AnimationUtils.loadAnimation(
								MainActivity.this, msg.arg1);
						// AlphaAnimation right=new AlphaAnimation(0.1f, 1.0f);
						// mFirstfragment.getRootView().findViewById(msg.arg2).setVisibility(View.VISIBLE);
						// findViewById(msg.arg2).setAlpha(0);
						// right.setDuration(300);
						AlphaAnimation anim = new AlphaAnimation(0.01f, 1.0f);
						anim.setDuration(500);

						first.findViewById(R.id.light).startAnimation(anim);
						anim.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								Log.e(TAG, "Animation start");

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								Log.e(TAG, "Animation end");
								Message msg = new Message();
								msg.what = SHOW_MENU;
								msg.arg1 = R.anim.right_out;
								msg.arg2 = R.id.right_top;
								mHandler.sendMessageDelayed(msg, 50);

							}
						});
						anim.start();
					} else {
						Log.d("FirstIndexFragment", "view is null");
						Message ms = new Message();
						ms.what = START_ANIM;
						ms.arg1 = R.anim.show_out;
						ms.arg2 = R.id.light;
						mHandler.sendMessageDelayed(ms, 100);
					}

					break;

				case SHOW_MENU:
					View view = mPager.getChildAt(0);
					if (view.findViewById(R.id.right_top) != null) {
						Animation show_menu = AnimationUtils.loadAnimation(
								MainActivity.this, R.anim.right_out);
						// AlphaAnimation right=new AlphaAnimation(0.1f, 1.0f);

						view.findViewById(R.id.right_top).startAnimation(
								show_menu);
						view.findViewById(R.id.right_bottom).startAnimation(
								show_menu);
						show_menu = AnimationUtils.loadAnimation(
								MainActivity.this, R.anim.left_out);
						view.findViewById(R.id.left_top).startAnimation(
								show_menu);
						view.findViewById(R.id.left_bottom).startAnimation(
								show_menu);
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
					}

					break;
				case SHOW_ICON:
					View view1 = mPager.getChildAt(0);
					if (view1.findViewById(R.id.stopwatch) != null) {
						Animation show_icon = AnimationUtils.loadAnimation(
								MainActivity.this, R.anim.show_out);

						view1.findViewById(R.id.stopwatch).startAnimation(
								show_icon);
						view1.findViewById(R.id.obd).startAnimation(show_icon);
						view1.findViewById(R.id.trouble_codes).startAnimation(
								show_icon);
						view1.findViewById(R.id.maintenance).startAnimation(
								show_icon);
					}

					break;

				case WIFI_CONNECT_FAILED:
					ImageView con = (ImageView) findViewById(R.id.obd_connected);
					con.clearAnimation();
					con.setImageResource(R.drawable.obd_uncon);
					break;
				case WIFI_CONNECTING:
					final Animation animation = new AlphaAnimation(1, 0);
					animation.setDuration(400);
					animation
							.setInterpolator(new AccelerateDecelerateInterpolator());
					animation.setRepeatCount(Animation.INFINITE);
					animation.setRepeatMode(Animation.REVERSE);
					final ImageView btn = (ImageView) findViewById(R.id.obd_connected);
					btn.setImageResource(R.drawable.obd_con);
					btn.startAnimation(animation);
					break;
				}
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if (true) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		// }
		setContentView(R.layout.activity_main);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mPager = (ViewPager) findViewById(R.id.vPager);
		fragmentsList = new ArrayList<Fragment>();

		mFirstfragment = new FirstIndexFragment();
		Log.d("FirstIndexFragment", "onCreate() mFirstfragment="
				+ mFirstfragment);
		SecondIndexFragment secondFragment = new SecondIndexFragment();

		fragmentsList.add(mFirstfragment);
		fragmentsList.add(secondFragment);

		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentsList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		findViewById(R.id.obd_connected).setOnClickListener(this);
		findViewById(R.id.obd_config).setOnClickListener(this);
		mHandler.sendEmptyMessageDelayed(INIT_ANIM, 500);

		Intent service = new Intent(this, OBDService.class);
		startService(service);
		hideView();

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
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String mConnect_type = preferences.getString(BaseSetting.CONNECT_TYPE,
				null);
		String ssid = preferences.getString(BaseSetting.CONNECT_DEVICE, null);
		if (mConnect_type == null || Integer.valueOf(mConnect_type) == 0) {
			WifiManager mWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			if (!mWifi.isWifiEnabled()) {
				mWifi.setWifiEnabled(true);
			}

			WifiInfo wifiInfo = mWifi.getConnectionInfo();

			if (wifiInfo.getIpAddress() == 0) {
				showDialog(0);
			} else if (ssid != null && !wifiInfo.getSSID().equals(ssid)) {
				Toast.makeText(
						this,
						String.format(getString(R.string.wifi_connect_wrong),
								ssid), Toast.LENGTH_LONG).show();
			}
			// if (Device.getNetConnect(MainActivity.this) < 0) {
			//
			// showDialog(0);
			// }

		} else if (Integer.valueOf(mConnect_type) == 1) {

		}
		if (mServiceConnection == null) {
			mServiceIntent = new Intent(this, ObdGatewayService.class);
			mServiceConnection = new ObdGatewayServiceConnection();
			isBound = getApplicationContext().bindService(mServiceIntent,
					mServiceConnection, Context.BIND_AUTO_CREATE);
		}
		mServiceConnection.setServiceListener(mListener);
		mHandler.post(getConnectStatusRunable);
		mHandler.sendEmptyMessageDelayed(HANDLER_FUEL, 500);
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
				startActivity(new Intent(this, OBDProgressBarActivity.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.stopwatch:
			if (isConnected)
				startActivity(new Intent(this, SpeedUpTest.class));
			else {
				Toast.makeText(getBaseContext(), R.string.device_unconn,
						Toast.LENGTH_LONG).show();
			}
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

		case R.id.obd_connected:
			if (mServiceConnection != null) {
				mServiceConnection.connectDevice();
			}
			break;
		case R.id.obd_config:
			startActivity(new Intent(this, ConfigurationActivity.class));
			break;
		}

	}

	@Override
	protected void onDestroy() {

		stopLiveData();
		super.onDestroy();
	}

	private void initAnim() {
		Message msg = new Message();
		msg.what = START_ANIM;
		msg.arg1 = R.anim.show_out;
		msg.arg2 = R.id.light;
		mHandler.sendMessageDelayed(msg, 10);

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			Log.d(TAG, "onPageScrollStateChanged = " + state);

		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			Log.d(TAG, "onPageScrolled = " + position + " arg1="
					+ positionOffset + " arg2= " + positionOffsetPixels
					+ " mPager.getCurrentItem()=" + mPager.getCurrentItem());
			if (position == 0) {
				findViewById(R.id.left_light).getBackground().setAlpha(
						(int) (255 * positionOffset));
				findViewById(R.id.right_light).getBackground().setAlpha(
						(int) (255 * (1 - positionOffset)));
			}

		}

		@Override
		public void onPageSelected(int position) {
			Log.d(TAG, "onPageSelected = " + position);

		}

	}

	private void hideView() {
		findViewById(R.id.left_light).getBackground().setAlpha(0);
	}

}
