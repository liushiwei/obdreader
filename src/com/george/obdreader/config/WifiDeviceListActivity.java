package com.george.obdreader.config;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.george.obdreader.R;

public class WifiDeviceListActivity extends PreferenceActivity implements
		DialogInterface.OnClickListener {
	
	private static final String TAG = "WifiDeviceListActivity";

	private final IntentFilter mFilter;
	private final BroadcastReceiver mReceiver;
	private final Scanner mScanner;

	private WifiManager mWifiManager;
	private WifiEnabler mWifiEnabler;
	private CheckBoxPreference mNotifyOpenNetworks;
	private Preference mAddNetwork;
	private ProgressCategory mAccessPoints;
	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private int mLastPriority;

	private boolean mResetNetworks = false;
	private int mKeyStoreNetworkId = -1;

	private AccessPoint mSelected;

	// private WifiDialog mDialog;

	public WifiDeviceListActivity() {
		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(intent);
			}
		};

		mScanner = new Scanner();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		addPreferencesFromResource(R.xml.wifi_settings);
		mWifiEnabler = new WifiEnabler(this,
				(CheckBoxPreference) findPreference("enable_wifi"));
		mNotifyOpenNetworks = (CheckBoxPreference) findPreference("notify_open_networks");
		mNotifyOpenNetworks.setChecked(Secure.getInt(getContentResolver(),
				Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);

		mAccessPoints = (ProgressCategory) findPreference("access_points");
		mAccessPoints.setOrderingAsAdded(false);
		mAddNetwork = findPreference("add_network");
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWifiEnabler != null) {
			mWifiEnabler.resume();
		}
		registerReceiver(mReceiver, mFilter);

		mKeyStoreNetworkId = -1;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mWifiEnabler != null) {
			mWifiEnabler.pause();
		}
		unregisterReceiver(mReceiver);
		mScanner.pause();

		if (mResetNetworks) {
			enableNetworks();
		}
	}

	private void forget(int networkId) {
		mWifiManager.removeNetwork(networkId);
		saveNetworks();
	}

	private void connect(int networkId) {
		if (networkId == -1) {
			return;
		}

		// Reset the priority of each network if it goes too high.
		if (mLastPriority > 1000000) {
			for (int i = mAccessPoints.getPreferenceCount() - 1; i >= 0; --i) {
				AccessPoint accessPoint = (AccessPoint) mAccessPoints
						.getPreference(i);
				if (accessPoint.networkId != -1) {
					WifiConfiguration config = new WifiConfiguration();
					config.networkId = accessPoint.networkId;
					config.priority = 0;
					mWifiManager.updateNetwork(config);
				}
			}
			mLastPriority = 0;
		}

		// Set to the highest priority and save the configuration.
		WifiConfiguration config = new WifiConfiguration();
		config.networkId = networkId;
		config.priority = ++mLastPriority;
		mWifiManager.updateNetwork(config);
		saveNetworks();

		// Connect to network by disabling others.
		mWifiManager.enableNetwork(networkId, true);
		mWifiManager.reconnect();
		mResetNetworks = true;
	}

	private void enableNetworks() {
		for (int i = mAccessPoints.getPreferenceCount() - 1; i >= 0; --i) {
			WifiConfiguration config = ((AccessPoint) mAccessPoints
					.getPreference(i)).getConfig();
			if (config != null && config.status != Status.ENABLED) {
				mWifiManager.enableNetwork(config.networkId, false);
			}
		}
		mResetNetworks = false;
	}

	private void saveNetworks() {
		// Always save the configuration with all networks enabled.
		enableNetworks();
		mWifiManager.saveConfiguration();
		updateAccessPoints();
	}

	private void updateAccessPoints() {
		List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

		List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
		if (configs != null) {
			mLastPriority = 0;
			for (WifiConfiguration config : configs) {
				if (config.priority > mLastPriority) {
					mLastPriority = config.priority;
				}

				// Shift the status to make enableNetworks() more efficient.
				if (config.status == Status.CURRENT) {
					config.status = Status.ENABLED;
				} else if (mResetNetworks && config.status == Status.DISABLED) {
					config.status = Status.CURRENT;
				}

				AccessPoint accessPoint = new AccessPoint(this, config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoints.add(accessPoint);
			}
		}

		List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : accessPoints) {
					if (accessPoint.update(result)) {
						found = true;
					}
				}
				if (!found) {
					accessPoints.add(new AccessPoint(this, result));
				}
			}
		}

		mAccessPoints.removeAll();
		for (AccessPoint accessPoint : accessPoints) {
			mAccessPoints.addPreference(accessPoint);
		}
	}

	private void handleEvent(Intent intent) {
		String action = intent.getAction();
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
			updateAccessPoints();
		} else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)) {
			if (mSelected != null && mSelected.networkId != -1) {
				mSelected = null;
			}
			updateAccessPoints();
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			updateConnectionState(WifiInfo
					.getDetailedStateOf((SupplicantState) intent
							.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			updateConnectionState(((NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
					.getDetailedState());
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			updateConnectionState(null);
		}
	}

	private void updateConnectionState(DetailedState state) {
		/* sticky broadcasts can call this when wifi is disabled */
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}

		mLastInfo = mWifiManager.getConnectionInfo();
		if (state != null) {
			mLastState = state;
		}

		for (int i = mAccessPoints.getPreferenceCount() - 1; i >= 0; --i) {
			((AccessPoint) mAccessPoints.getPreference(i)).update(mLastInfo,
					mLastState);
		}

		if (mResetNetworks
				&& (state == DetailedState.CONNECTED
						|| state == DetailedState.DISCONNECTED || state == DetailedState.FAILED)) {
			updateAccessPoints();
			enableNetworks();
		}
	}

	private void updateWifiState(int state) {
		if (state == WifiManager.WIFI_STATE_ENABLED) {
			mScanner.resume();
			updateAccessPoints();
		} else {
			mScanner.pause();
			mAccessPoints.removeAll();
		}
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void pause() {
			mRetry = 0;
			mAccessPoints.setProgress(false);
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			if (mWifiManager.startScan()) {
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(WifiDeviceListActivity.this,
						R.string.wifi_fail_to_scan, Toast.LENGTH_LONG).show();
				return;
			}
			mAccessPoints.setProgress(mRetry != 0);
			sendEmptyMessageDelayed(0, 6000);
		}
	}
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen screen,
			Preference preference) {
		if (preference instanceof AccessPoint) {
			mSelected = (AccessPoint) preference;
			Log.e(TAG,"ssid = "+mSelected.ssid+" networkid = "+mSelected.networkId);
			//connect(mSelected.networkId);
			Intent intent = new Intent();
            intent.putExtra(BaseSetting.EXTRA_DEVICE_ADDRESS, mSelected.ssid);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
		}
		return false;
	}
}
