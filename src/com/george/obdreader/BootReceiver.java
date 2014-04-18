package com.george.obdreader;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.george.obdreader.config.AboutSoftwareActivity;
import com.george.obdreader.config.AboutSoftwareSetting;
import com.george.utils.Device;

public class BootReceiver extends BroadcastReceiver {

	private String TAG = "Boot_Receiver";
	
	private Context mContext;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				String respone = (String) msg.obj;
				if (respone != null) {
					Log.e(TAG, "respone = " + respone);
					String version = respone
							.substring(0, respone.indexOf('\n'));
					version = version.trim().substring(
							version.indexOf(':') + 1, version.length());
					String current_version_name = Device.getAppVersionName(mContext);
					if (current_version_name.compareTo(version) != 0) {
						  NotificationManager m_NotificationManager=(NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
					        
					        //点击通知时转移内容
						  Intent  m_Intent=new Intent(mContext,AboutSoftwareActivity.class);
					        
						  PendingIntent  m_PendingIntent=PendingIntent.getActivity(mContext, 0, m_Intent, 0);
					        
						  Notification  m_Notification=new Notification();
					        
					    
					    //设置通知在状态栏显示的图标
					    m_Notification.icon=R.drawable.ic_launcher;
					    
					    //当我们点击通知时显示的内容
					    m_Notification.tickerText=mContext.getString(R.string.checked_new_version);
					        
					    //通知时发出的默认声音
					    m_Notification.defaults=Notification.DEFAULT_SOUND;
					    
					    //设置通知显示的参数
					    m_Notification.setLatestEventInfo(mContext, mContext.getString(R.string.checked_new_version), String.format(mContext.getString(R.string.new_version), version),m_PendingIntent );
					    
					    //这个可以理解为开始执行这个通知
					    m_NotificationManager.notify(0,m_Notification);
					}
				}
				break;
			case 1:
				Toast.makeText(mContext, R.string.net_error, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		/*
		 * Log.e("Boot_Receiver", "get Action :" + intent.getAction());
		 * SharedPreferences userInfo = context.getSharedPreferences(
		 * "maintenance_info", context.MODE_PRIVATE); long next_maintenance_date
		 * = userInfo.getLong("next_maintenance_date", 0); Date today = new
		 * Date(); long last_maintenance_date =
		 * userInfo.getLong("last_maintenance_date", 0); SimpleDateFormat
		 * bartDateFormat = new SimpleDateFormat( "EEEE-MMMM-dd-yyyy"); Date
		 * date = new Date(last_maintenance_date); Log.e(TAG,
		 * "today = "+bartDateFormat.format(today)); Log.e(TAG,
		 * "last_maintenance_date = "+bartDateFormat.format(date)); date = new
		 * Date(next_maintenance_date); Log.e(TAG,
		 * "next_maintenance_date = "+bartDateFormat.format(date)); if
		 * (next_maintenance_date > 0) { if (today.getTime() >
		 * next_maintenance_date && last_maintenance_date <
		 * next_maintenance_date) { notifyObdManager(context); } }
		 */
		this.mContext = context;
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Intent service = new Intent(mContext, OBDService.class);
			mContext.startService(service);
		}
		if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			boolean auto_update_value = preferences.getBoolean(
					AboutSoftwareSetting.AUTO_UPDATE, true);
			boolean wifi_update_only_value = preferences.getBoolean(AboutSoftwareSetting.WIFI_UPDATE_ONLY, true);
			if (auto_update_value) {

				ConnectivityManager connManager = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				// State state = connManager.getActiveNetworkInfo().getState();
				NetworkInfo activeInfo = connManager.getActiveNetworkInfo();
				if (activeInfo == null) {
					Log.e(TAG, "网络已经断开");
					return;
				}

				if (activeInfo != null && activeInfo.isConnected()) {
					// wifi 连接状态
					if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						State state = connManager.getNetworkInfo(
								ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
						switch (state) {
						case CONNECTED:

							Log.e(TAG, "WIFI网络连接成功");
							new Thread(){

								@Override
								public void run() {
									String responeString;
									try {
										responeString = Device
												.getHttpResponse(
														"http://obdreader.sinaapp.com/bin/update.txt",
														Device.HTTP_METHOD_GET);
										handler.obtainMessage(0, responeString).sendToTarget();
									} catch (Exception e) {
										handler.sendEmptyMessage(1);
										e.printStackTrace();
									}
									super.run();
								}
								
							}.start();
							
							
							return;
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
							if(!wifi_update_only_value){
								new Thread(){

									@Override
									public void run() {
										String responeString;
										try {
											responeString = Device
													.getHttpResponse(
															"http://obdreader.sinaapp.com/bin/update.txt",
															Device.HTTP_METHOD_GET);
											handler.obtainMessage(0, responeString).sendToTarget();
										} catch (Exception e) {
											handler.sendEmptyMessage(1);
											e.printStackTrace();
										}
										super.run();
									}
									
								}.start();
							}
							return;
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
}