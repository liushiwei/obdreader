package com.george.obdreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class OBDProgressBarActivity extends Activity {
	
	private String[] mPid_decs  = new String[OBDEnums.values().length];
	private boolean[] mPid_show = new boolean[OBDEnums.values().length];
	private List<String> mShow_pids;
	private OBDEnums[] mPidsEnum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obd_progress_bar_list);
		ImageButton plus = (ImageButton) findViewById(R.id.add_pid);
		mPidsEnum = OBDEnums.values();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mShow_pids = new ArrayList<String>();
		String pids = preferences.getString("PIDS", null);
		if(pids!=null){
			mShow_pids =new ArrayList<String>(Arrays.asList(pids.split(",")));
		}
		for(int i=0;i<mPidsEnum.length;i++){
			mPid_decs[i] = getString(mPidsEnum[i].getDesc());
			if(mShow_pids.contains(mPidsEnum[i].getCommand())){
				mPid_show[i] = true;
			}else{
				mPid_show[i] = false;
			}
		}
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(OBDProgressBarActivity.this)
						.setTitle(R.string.obd_pid)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMultiChoiceItems(mPid_decs, mPid_show, new OnMultiChoiceClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								OBDEnums pid =mPidsEnum[which];
								if(isChecked){
									mShow_pids.add(pid.getCommand());
								}else{
									mShow_pids.remove(pid.getCommand());
								}
								
							}
						}).setNegativeButton(android.R.string.ok, null).show();
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(OBDProgressBarActivity.this);
						Editor editor = preferences.edit();
						Log.e("OBDProgressBar",mShow_pids.toString());
						String pids = ""; 
								
						for(String pid :mShow_pids){
							pids += pid+",";
						}
						editor.putString("PIDS", pids);
						editor.commit();
					}
				});
			}
		});
	}
}
