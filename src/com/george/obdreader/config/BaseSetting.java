package com.george.obdreader.config;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.widget.Toast;

import com.george.obdreader.Log;
import com.george.obdreader.R;

public class BaseSetting extends PreferenceFragment implements
		OnPreferenceChangeListener {

	private static final String TAG = "BaseSetting";

	public static final String CONNECT_TYPE = "connect_type";

	public static final String CONNECT_DEVICE = "connect_device";

	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	private String mConnect_type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.configuration_preferences);

		ListPreference type = (ListPreference) findPreference(CONNECT_TYPE);

		type.setOnPreferenceChangeListener(this);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mConnect_type = preferences.getString(CONNECT_TYPE, null);
		if (mConnect_type != null) {
			String[] values = getResources().getStringArray(
					R.array.connect_type_value);
			for (int i = 0; i < values.length; i++) {
				if (mConnect_type.toString().equals(values[i])) {
					String[] times = getResources().getStringArray(
							R.array.connect_type);
					type.setSummary(getString(R.string.current_connect_type)
							+ " " + times[i]);
				}

			}
		}

		Preference device = findPreference(CONNECT_DEVICE);
		
		if (mConnect_type != null) {
			String obd_device = preferences.getString(CONNECT_DEVICE, null);
			if(obd_device!=null)
			device.setSummary(getString(R.string.current_obd_device)+obd_device);
		}

		device.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (mConnect_type != null) {
					if (mConnect_type.equals("0")) {
						Intent serverIntent = new Intent(getActivity(),
								WifiDeviceListActivity.class);
						startActivityForResult(serverIntent, 100);
					} else {
						Intent serverIntent = new Intent(getActivity(),
								BTDeviceListActivity.class);
						startActivityForResult(serverIntent, 100);
					}
				} else {
					Toast.makeText(getActivity(), R.string.choice_connect_type,
							Toast.LENGTH_LONG).show();
				}

				return false;
			}
		});

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e(TAG, preference.getKey() + " value=" + newValue.toString());
		if (CONNECT_TYPE.equals(preference.getKey())) {
			mConnect_type = (String) newValue;
			String[] values = getResources().getStringArray(
					R.array.connect_type_value);
			for (int i = 0; i < values.length; i++) {
				if (newValue.toString().equals(values[i])) {
					String[] times = getResources().getStringArray(
							R.array.connect_type);
					preference
							.setSummary(getString(R.string.current_connect_type)
									+ " " + times[i]);
				}

			}
		}
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case 100:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						EXTRA_DEVICE_ADDRESS);
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				Editor editor = preferences.edit();
				editor.putString(CONNECT_DEVICE, address);
				editor.commit();
				Preference device = findPreference(CONNECT_DEVICE);
				device.setSummary(getString(R.string.current_obd_device)+address);
			}
		}
	}

}
