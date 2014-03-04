package com.george.obdreader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;

public class MaintenanceSetting extends PreferenceFragment implements OnPreferenceChangeListener {

	private static final int DEFAULT_MONTHS = 0;
	
	private static final String TAG = "MaintenanceSetting";
	
	public static final String MAINTENANCE_TIME = "maintenance_time";
	
	public static final String MAINTENANCE_TIP = "maintenance_tip";
	
	public static final String MAINTENANCE_TIP_TIME = "maintenance_tip_time";
	
	public static final String LAST_MAINTENANCE_TIME = "last_maintenance_time";
	
	public static final String NEXT_MAINTENANCE_TIME = "next_maintenance_time";
	

	 @Override  
     public void onCreate(Bundle savedInstanceState) {  
         // TODO Auto-generated method stub  
         super.onCreate(savedInstanceState);  
         addPreferencesFromResource(R.xml.maintenance_preferences); 
         ListPreference time = (ListPreference) findPreference(MAINTENANCE_TIME);
         time.setOnPreferenceChangeListener(this);
         CheckBoxPreference tip = (CheckBoxPreference) findPreference(MAINTENANCE_TIP);
         tip.setOnPreferenceChangeListener(this);
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
         String maintenance_time = preferences.getString(MAINTENANCE_TIME, null);
         if(maintenance_time!=null){
        	 String[] values=getResources().getStringArray(R.array.maintenance_time_value);
        	 for(int i=0;i<values.length;i++){
        		 if(maintenance_time.toString().equals(values[i])){
        			 String[] times=getResources().getStringArray(R.array.maintenance_time);
        			 time.setSummary(getString(R.string.current_interval_time)+" "+times[i]);
        		 }
        		 
        	 }
        	 Preference last_time = findPreference(LAST_MAINTENANCE_TIME);
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             last_time.setSummary(getString(R.string.maintenance_time)+": "+sdf.format(new Date(preferences.getLong(LAST_MAINTENANCE_TIME, 0))));
             Preference next_time = findPreference(NEXT_MAINTENANCE_TIME);
             next_time.setSummary(getString(R.string.maintenance_time)+" "+sdf.format(new Date(preferences.getLong(NEXT_MAINTENANCE_TIME, 0))));

         }
         
        
     }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e(TAG, preference.getKey()+" value="+newValue.toString());
		if(MAINTENANCE_TIME.equals(preference.getKey())){
			String[] values=getResources().getStringArray(R.array.maintenance_time_value);
			for(int i=0;i<values.length;i++){
				if(newValue.toString().equals(values[i])){
				    Log.e(TAG, "set maintenance intervale time");
					String[] times=getResources().getStringArray(R.array.maintenance_time);
					preference.setSummary(getString(R.string.current_interval_time)+" "+times[i]);
				}
				
			}
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor editor = preferences.edit();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        editor.putLong(LAST_MAINTENANCE_TIME,
                today.getTimeInMillis());
        Preference last_time = findPreference(LAST_MAINTENANCE_TIME);
        
        last_time.setSummary(getString(R.string.maintenance_time)+": "+sdf.format(today.getTime()));
        today.add(Calendar.MONTH, Integer.valueOf((String) newValue));
        editor.putLong(NEXT_MAINTENANCE_TIME,
                today.getTimeInMillis());
        editor.putLong(MAINTENANCE_TIP_TIME,
                today.getTimeInMillis());
        Preference next_time = findPreference(NEXT_MAINTENANCE_TIME);
        next_time.setSummary(getString(R.string.maintenance_time)+" "+sdf.format(today.getTime()));
        editor.commit();
        
		return true;
	}  
    
    

}
