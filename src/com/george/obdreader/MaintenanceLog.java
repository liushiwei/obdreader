package com.george.obdreader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class MaintenanceLog extends Fragment implements OnClickListener {
	
	private List<String> mMaintenanceSelected;
	private int mTake;
	private Date mTime;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("=====>", "MaintenanceLog onCreateView");
        View root = inflater.inflate(R.layout.maintenance_log, container, false);
        ImageButton add = (ImageButton) getActivity().findViewById(R.id.add_pid);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(this);
        return root;
    }

	@Override
	public void onDestroyView() {
		 Log.d("=====>", "MaintenanceLog onDestroyView");
		 getActivity().findViewById(R.id.add_pid).setVisibility(View.GONE);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		final String[] options=getResources().getStringArray(R.array.maintenance_types);
		boolean isSelected[] = new boolean[options.length];
		mMaintenanceSelected = new ArrayList<String>();
		mMaintenanceSelected.add(options[0]);
		isSelected[0] = true;
//        final View textEntryView = inflater.inflate(  
//                R.layout.dialoglayout, null);  
//        final EditText edtInput=(EditText)textEntryView.findViewById(R.id.edtInput);  
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
        builder.setCancelable(false);  
        builder.setTitle(getString(R.string.selecte_maintenance_options));  
        builder.setMultiChoiceItems(options,isSelected,new OnMultiChoiceClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(isChecked){
					mMaintenanceSelected.add(options[which]);
				}else{
					mMaintenanceSelected.remove(options[which]);
				}
			}
		});
        builder.setPositiveButton(getString(android.R.string.ok),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	Log.d("=====>", "MaintenanceLog mMaintenanceSelected = "+mMaintenanceSelected.toString());
                    	 LayoutInflater layoutInflater = LayoutInflater.from(getActivity());  
                    	  final View textEntryView = layoutInflater.inflate(  
                                R.layout.maintenance_options, null);  
                        final EditText edtInput=(EditText)textEntryView.findViewById(R.id.editText1); 
                        final DatePicker picker = (DatePicker) textEntryView.findViewById(R.id.datePicker1);
                        Time today = new Time();
                        today.setToNow();
                        picker.init(today.year, today.month, today.monthDay, new OnDateChangedListener() {
							
							@Override
							public void onDateChanged(DatePicker view, int year, int monthOfYear,
									int dayOfMonth) {
								
								Log.d("=====>", "year = "+year+" monthOfYear = "+monthOfYear+" dayOfMonth = "+dayOfMonth);
							}
						});
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
                        builder.setCancelable(false); 
                        builder.setView(textEntryView);
                        builder.setTitle(getString(R.string.maintenance_time));
                        builder.setPositiveButton(getString(android.R.string.ok),  
                                new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										
									}  
                        	
                        }
                        );
                        builder.setNegativeButton(getString(android.R.string.cancel),  
                        		new DialogInterface.OnClickListener() {  
                        	public void onClick(DialogInterface dialog, int whichButton) {  
                        	}  
                        });  
                        builder.show();
                    }  
                });  
        builder.setNegativeButton(getString(android.R.string.cancel),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    }  
                });  
        builder.show();  
		
	}
	
	
	


}
