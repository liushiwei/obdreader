package com.george.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

public class Device {

	private static final String TAG = "Device";

	public static String getDeviceId() {
		final String MMC_CID_PATH = "/sys/class/mmc_host/mmc0/mmc0:0001/cid";
		String id = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					MMC_CID_PATH)));
			while ((id = br.readLine()) != null) {
				break;
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (id != null) {
			id = Integer.toHexString(id.hashCode()).toUpperCase();
		} else {
			id = "00000000";
		}
		Log.e(TAG, "DevideId = " + id);
		return id;
	}

	public static int getNetConnect(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State state = connManager.getActiveNetworkInfo().getState();
		NetworkInfo activeInfo = connManager.getActiveNetworkInfo();
		if (activeInfo == null) {
			Log.e(TAG, "网络已经断开");
			return -1;
		}

		if (activeInfo != null && activeInfo.isConnected()) {
			// wifi 连接状态
			if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				State state = connManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
				switch (state) {
				case CONNECTED:

					Log.e(TAG, "WIFI网络连接成功");
					return ConnectivityManager.TYPE_WIFI;
				case CONNECTING:
					Log.e(TAG, "正在连接WIFI网络");
					break;
				case DISCONNECTED:
					Log.e(TAG, "WIFI网络已经断开");
					break;
				case DISCONNECTING:
					Log.e(TAG, "正在断开WIFI网络");
					break;
				}

			}
			// mobile连接状态
			if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				State state = connManager.getNetworkInfo(
						ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
				switch (state) {
				case CONNECTED:
					Log.e(TAG, "GPRS网络连接成功");
					return ConnectivityManager.TYPE_MOBILE;
				case CONNECTING:
					Log.e(TAG, "正在连接GPRS网络");
					break;
				case DISCONNECTED:
					Log.e(TAG, "GPRS网络已经断开");
					break;
				case DISCONNECTING:
					Log.e(TAG, "正在断开GPRS网络");
					break;
				}
			}
		}
		return -1;
	}

}
