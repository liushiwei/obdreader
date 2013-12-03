package com.george.obdreader;

import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

public class MaintenanceTip extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintenance_tip);
		findViewById(R.id.maintenanced).setOnClickListener(this);
		findViewById(R.id.tomorrow).setOnClickListener(this);
		findViewById(R.id.next_time).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = preferences.edit();
		Calendar today = Calendar.getInstance();
		switch(v.getId()){
		case R.id.maintenanced:
	        editor.putLong(MaintenanceSetting.LAST_MAINTENANCE_TIME,
	                today.getTimeInMillis());
	        
	        today.add(Calendar.MONTH, Integer.valueOf((String) preferences.getString(MaintenanceSetting.MAINTENANCE_TIME, "3")));
	        editor.putLong(MaintenanceSetting.NEXT_MAINTENANCE_TIME,
	                today.getTimeInMillis());
	        editor.putLong(MaintenanceSetting.MAINTENANCE_TIP_TIME,
	        		today.getTimeInMillis());
			break;
		case R.id.tomorrow:
			today.add(Calendar.DATE, 3);
	        editor.putLong(MaintenanceSetting.MAINTENANCE_TIP_TIME,
	        		today.getTimeInMillis());

			break;
		case R.id.next_time:
			today.add(Calendar.MONTH, Integer.valueOf((String) preferences.getString(MaintenanceSetting.MAINTENANCE_TIME, "3")));
	        editor.putLong(MaintenanceSetting.NEXT_MAINTENANCE_TIME,
	                today.getTimeInMillis());
	        editor.putLong(MaintenanceSetting.MAINTENANCE_TIP_TIME,
	        		today.getTimeInMillis());
			break;
		}
		editor.commit();
		finish();
		
	}
	
	

}
