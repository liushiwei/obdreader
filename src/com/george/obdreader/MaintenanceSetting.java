package com.george.obdreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;

public class MaintenanceSetting extends PreferenceFragment implements OnPreferenceChangeListener {

	private static final int DEFAULT_MONTHS = 0;
	
	private static final String TAG = "MaintenanceSetting";
	
	private static final String MAINTENANCE_TIME = "maintenance_time";
	
	private static final String MAINTENANCE_TIP = "maintenance_tip";
	
	
	

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
        	 
         }

     }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e(TAG, preference.getKey()+" value="+newValue.toString());
		if(MAINTENANCE_TIP.equals(preference.getKey())){
			String[] values=getResources().getStringArray(R.array.maintenance_time_value);
			for(int i=0;i<values.length;i++){
				if(newValue.toString().equals(values[i])){
					String[] times=getResources().getStringArray(R.array.maintenance_time);
					preference.setSummary(getString(R.string.current_interval_time)+" "+times[i]);
				}
				
			}
		}
		return true;
	}  
    
    

}
