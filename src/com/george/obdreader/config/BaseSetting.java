package com.george.obdreader.config;

import java.text.NumberFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.george.obdreader.Log;
import com.george.obdreader.R;
import com.george.obdreader.db.FuellingLogTable;

public class BaseSetting extends PreferenceFragment implements
		OnPreferenceChangeListener {

	private static final String TAG = "BaseSetting";

	public static final String CONNECT_TYPE = "connect_type";

	public static final String CONNECT_DEVICE = "connect_device";

	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	public static String CUSTOMER_CONNECT = "customer_connect";
	
	public static String CUSTOMER_IP = "customer_ip";
	
	public static String CUSTOMER_PORT = "customer_port";

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
			String[] times = getResources()
					.getStringArray(R.array.connect_type);
			type.setSummary(getString(R.string.current_connect_type) + " "
					+ times[Integer.valueOf(mConnect_type)]);
		}

		Preference device = findPreference(CONNECT_DEVICE);

		if (mConnect_type != null) {
			String obd_device = preferences.getString(CONNECT_DEVICE, null);
			if (obd_device != null)
				device.setSummary(getString(R.string.current_obd_device)
						+ obd_device);
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

		Preference customer_connect = findPreference(CUSTOMER_CONNECT);
		String ip = preferences.getString(CUSTOMER_IP, "192.168.0.10");
		int port = preferences.getInt(CUSTOMER_PORT, 35000);
		customer_connect
				.setSummary(getString(R.string.customer_connect_summary) + ip
						+ ":" + port);
		customer_connect
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						final View mDialogView = LayoutInflater.from(
								getActivity()).inflate(
								R.layout.customer_connect, null);
						SharedPreferences preferences = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						String ip = preferences.getString(CUSTOMER_IP,
								"192.168.0.10");
						int port = preferences.getInt(CUSTOMER_PORT, 35000);
						((EditText) mDialogView.findViewById(R.id.customer_ip))
								.setText(ip);
						((EditText) mDialogView
								.findViewById(R.id.customer_port)).setText(port
								+ "");
						AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setCancelable(false)
								.setTitle(
										getString(R.string.customer_connect_title))
								.setView(mDialogView)
								.setPositiveButton(
										getString(android.R.string.ok),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

											}
										})
								.setNegativeButton(
										getString(android.R.string.cancel),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

											}
										});
						final AlertDialog alertDialog = builder.show();
						Button btn = alertDialog
								.getButton(DialogInterface.BUTTON_POSITIVE);
						btn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								String ip = ((EditText) mDialogView
										.findViewById(R.id.customer_ip))
										.getEditableText().toString().trim();
								String port = ((EditText) mDialogView
										.findViewById(R.id.customer_port))
										.getEditableText().toString().trim();
								if (ip.length()==0||!ip.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
									Toast.makeText(getActivity(), R.string.customer_connect_ip_error,
											Toast.LENGTH_SHORT).show();
									;
									return;
								} else {
									String[] splits = ip.split("\\.");
									for (int i = 0; i < splits.length; i++) {
										if (Integer.valueOf(splits[i]) > 254) {
											Toast.makeText(getActivity(),
													R.string.customer_connect_ip_error, Toast.LENGTH_SHORT)
													.show();
											return;
										}
									}
								}
								if (Integer.valueOf(port) > 65535||Integer.valueOf(port) <0) {
									Toast.makeText(getActivity(), R.string.customer_connect_port_error,
											Toast.LENGTH_SHORT).show();
									return;
								}
								SharedPreferences preferences = PreferenceManager
										.getDefaultSharedPreferences(getActivity());
								Editor editor = preferences.edit();
								editor.putInt(CUSTOMER_PORT,
										Integer.valueOf(port));
								editor.putString(CUSTOMER_IP, ip);
								editor.commit();
								Preference customer_connect = findPreference(CUSTOMER_CONNECT);
								customer_connect
										.setSummary(getString(R.string.customer_connect_summary)
												+ ip + ":" + port);
								alertDialog.dismiss();
								Toast.makeText(getActivity(),
										R.string.reboot, Toast.LENGTH_LONG)
										.show();
							}
						});
						return false;
					}
				});

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e(TAG, preference.getKey() + " value=" + newValue.toString());
		if (CONNECT_TYPE.equals(preference.getKey())) {
			mConnect_type = (String) newValue;
			String[] times = getResources()
					.getStringArray(R.array.connect_type);
			preference.setSummary(getString(R.string.current_connect_type)
					+ " " + times[Integer.valueOf(mConnect_type)]);

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
				device.setSummary(getString(R.string.current_obd_device)
						+ address);
			}
		}
	}

}
