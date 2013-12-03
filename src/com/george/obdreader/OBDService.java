package com.george.obdreader;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

public class OBDService extends Service {
	
	private static final String TAG = "OBDService";

	 @Override  
	    public IBinder onBind(Intent intent) {  
	        // TODO Auto-generated method stub  
	        return null;  
	    }  
	    @Override  
	    public void onCreate() {  
	        // TODO Auto-generated method stub  
	        super.onCreate();  
	        Log.e("OBDService","service on create");
//	        /**接收屏幕改变广播的action*/  
//	        IntentFilter filter1 = new IntentFilter(Intent.ACTION_SCREEN_ON);  
//	         filter1.addAction(Intent.ACTION_SCREEN_OFF);  
//	          
//	         /**接收电量改变广播的action*/  
//	        IntentFilter filter2=new IntentFilter();  
//	        filter2.addAction(Intent.ACTION_BATTERY_CHANGED);  
//	        /**接收时间改变广播的action*/  
	        IntentFilter filter3=new IntentFilter();  
	        filter3.addAction(Intent.ACTION_TIME_TICK);  
	        /**在代码中分别注册屏幕改变的广播,电量改变的广播,时间改变的广播*/  
//	        BroadcastReceiver mReceiver1 = new ScreenBroadcast();  
//	        BroadcastReceiver mReceiver2 = new BatteryBroad();  
	        BroadcastReceiver mReceiver3 = new TimeTickBroadcast();  
	          
//	        registerReceiver(mReceiver1, filter1);  
//	        registerReceiver(mReceiver2, filter2);  
	        registerReceiver(mReceiver3, filter3);  
	     
	    }  
	    
	    public class TimeTickBroadcast extends BroadcastReceiver {

			@Override
			public void onReceive(Context context, Intent intent) {

				Log.e("OBDService","时间在改变!");
				Date today = new Date();
				if(today.getHours()==14&&today.getMinutes()==27){
					SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
					
					boolean is_tip= preferences.getBoolean(MaintenanceSetting.MAINTENANCE_TIP, true); 
					
					long next_maintenance_date = preferences.getLong(MaintenanceSetting.NEXT_MAINTENANCE_TIME,
							0);
				
					long maintenance_tip = preferences.getLong(MaintenanceSetting.MAINTENANCE_TIP_TIME,
							0);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					today = new Date(next_maintenance_date);
					Log.e(TAG, "next_maintenance_date ="+sdf.format(today));
					today = new Date(maintenance_tip);
					Log.e(TAG, "maintenance_tip ="+sdf.format(today));
					
					Time next_maintenance_time = new Time();
					next_maintenance_time.set(next_maintenance_date);
					Log.e(TAG, "next_maintenance_date ="+next_maintenance_time.year+"-"+next_maintenance_time.month+"-"+next_maintenance_time.monthDay);
					
					
					if (is_tip && next_maintenance_date > 0) {
						//if (today.getTime() > maintenance_tip) {
						Intent dialogIntent = new Intent(getBaseContext(), MaintenanceTip.class);
						dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(dialogIntent);
						//}
					}
				}


			}

		}
}
