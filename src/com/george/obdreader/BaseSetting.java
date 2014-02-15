package com.george.obdreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;

public class BaseSetting extends PreferenceFragment implements OnPreferenceChangeListener {

	private static final String TAG = "BaseSetting";
	
	public static final String CONNECT_TYPE = "connect_type";
	
	public static final String CONNECT_DEVICE = "connect_device";
	

	 @Override  
     public void onCreate(Bundle savedInstanceState) {  
         // TODO Auto-generated method stub  
         super.onCreate(savedInstanceState);  
         addPreferencesFromResource(R.xml.configuration_preferences); 
         Preference device = findPreference(CONNECT_DEVICE);
         device.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
				startActivityForResult(serverIntent, 100);
				return false;
			}
		});
         
         ListPreference type = (ListPreference) findPreference(CONNECT_TYPE);
         type.setOnPreferenceChangeListener(this);
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
         String connect_type = preferences.getString(CONNECT_TYPE, null);
         if(connect_type!=null){
        	 String[] values=getResources().getStringArray(R.array.connect_type_value);
        	 for(int i=0;i<values.length;i++){
        		 if(connect_type.toString().equals(values[i])){
        			 String[] times=getResources().getStringArray(R.array.connect_type);
        			 type.setSummary(getString(R.string.current_connect_type)+" "+times[i]);
        		 }
        		 
        	 }
         }
        
     }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Log.e(TAG, preference.getKey()+" value="+newValue.toString());
		if(CONNECT_TYPE.equals(preference.getKey())){
			String[] values=getResources().getStringArray(R.array.connect_type_value);
			for(int i=0;i<values.length;i++){
				if(newValue.toString().equals(values[i])){
					String[] times=getResources().getStringArray(R.array.connect_type);
					preference.setSummary(getString(R.string.current_connect_type)+" "+times[i]);
				}
				
			}
		}
		return true;
	}  
    
    

}
