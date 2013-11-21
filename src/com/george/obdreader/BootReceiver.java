package com.george.obdreader;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private String TAG = "Boot_Receiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("Boot_Receiver", "get Action :" + intent.getAction());
		SharedPreferences userInfo = context.getSharedPreferences(
				"maintenance_info", context.MODE_PRIVATE);
		long next_maintenance_date = userInfo.getLong("next_maintenance_date",
				0);
		Date today = new Date();
		long last_maintenance_date = userInfo.getLong("last_maintenance_date",
				0);
		SimpleDateFormat bartDateFormat = new SimpleDateFormat(
				"EEEE-MMMM-dd-yyyy");
		Date date = new Date(last_maintenance_date);
		Log.e(TAG, "today = "+bartDateFormat.format(today));
		Log.e(TAG, "last_maintenance_date = "+bartDateFormat.format(date));
		date = new Date(next_maintenance_date);
		Log.e(TAG, "next_maintenance_date = "+bartDateFormat.format(date));
		if (next_maintenance_date > 0) {
			if (today.getTime() > next_maintenance_date
					&& last_maintenance_date < next_maintenance_date) {
				notifyObdManager(context);
			}
		}
		// SharedPreferences.Editor editor = userInfo.edit();
		// Calendar today = Calendar.getInstance();
		// editor.putLong("maintenance_date",
		// today.getTimeInMillis());
		// editor.putLong("last_maintenance_date",
		// today.getTimeInMillis());
		// today.add(Calendar.MONTH, interval);
		// editor.putLong("next_maintenance_date",
		// today.getTimeInMillis());
		// editor.putInt("interval", interval);
		// editor.commit();
	}

	private void notifyObdManager(Context context) {
		CharSequence connectText;
		Notification notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent;
		connectText = context.getText(R.string.obd_health_warn);
		Intent intent = new Intent(context, MaintenanceSetting.class);
		intent.putExtra("notification_confirm", true);
		contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context,
				context.getText(R.string.app_name), connectText, contentIntent);
		// startForeground(R.string.app_name, notification);
		NotificationManager notifcationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		notifcationManager.notify(R.string.obd_alarm, notification);
	}

}