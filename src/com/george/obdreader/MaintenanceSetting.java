package com.george.obdreader;

import java.util.Calendar;
import java.util.Date;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MaintenanceSetting extends Fragment {

	private static final int DEFAULT_MONTHS = 0;

//	
//
//	void showConfirmDialog(final int id, final int value) {
//		LayoutInflater inflater = getLayoutInflater();
//		View confirm = inflater.inflate(R.layout.health_confirm, null);
//
//		if (value > 0) {
//			((TextView) confirm.findViewById(R.id.health_confirm))
//					.setText(String.format(
//							getString(R.string.your_car_health_maintenance),
//							value, getString(R.string.health_month)));
//		} else {
//			((TextView) confirm.findViewById(R.id.health_confirm))
//					.setText(R.string.setting_invalid);
//		}
//
//		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.show();
//		alertDialog.getWindow().setContentView(confirm);
//
//		WindowManager.LayoutParams layoutParams = alertDialog.getWindow()
//				.getAttributes();
//		layoutParams.width = 500;
//		layoutParams.height = 250;
//		alertDialog.getWindow().setAttributes(layoutParams);
//
//		((Button) confirm.findViewById(R.id.health_ok))
//				.setOnClickListener(new Button.OnClickListener() {
//					public void onClick(View v) {
//						alertDialog.cancel();
//						// TODO save date;
//						int interval = 10;
//						if (interval > 0) {
//							SharedPreferences userInfo = getSharedPreferences(
//									"maintenance_info", MODE_PRIVATE);
//							SharedPreferences.Editor editor = userInfo.edit();
//							Calendar today = Calendar.getInstance();
//							editor.putLong("maintenance_date",
//									today.getTimeInMillis());
//							editor.putLong("last_maintenance_date",
//									today.getTimeInMillis());
//							today.add(Calendar.MONTH, interval);
//							editor.putLong("next_maintenance_date",
//									today.getTimeInMillis());
//							editor.putInt("interval", interval);
//							editor.commit();
//						}
//					}
//				});
//
//		((Button) confirm.findViewById(R.id.health_cancel))
//				.setOnClickListener(new Button.OnClickListener() {
//					public void onClick(View v) {
//						alertDialog.cancel();
//					}
//				});
//	}
//
//	void showDetailDialog() {
//		LayoutInflater inflater = getLayoutInflater();
//		View confirm = inflater.inflate(R.layout.health_detail, null);
//		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//		alertDialog.show();
//		alertDialog.getWindow().setContentView(confirm);
//
//		WindowManager.LayoutParams layoutParams = alertDialog.getWindow()
//				.getAttributes();
//		layoutParams.width = WindowManager.LayoutParams.FILL_PARENT;
//		layoutParams.height = WindowManager.LayoutParams.FILL_PARENT;
//		alertDialog.getWindow().setAttributes(layoutParams);
//		float date = 0;
//		SharedPreferences userInfo = getSharedPreferences(
//				"maintenance_info", MODE_PRIVATE);
//		long interval = userInfo.getLong("next_maintenance_date", DEFAULT_MONTHS);
//		if(interval>0){
//		 long diff = interval - new Date().getTime();
//		 date = (int) (diff / (1000 * 60 * 60 * 24));
//		}
//		if (date < 0) {
//			((TextView) confirm.findViewById(R.id.health_detail_time_prompt))
//					.setText(String.format(
//							getString(R.string.your_car_health_maintenance), 0,
//							getString(R.string.health_day)));
//		} else if (date / 30 == 0) {
//			((TextView) confirm.findViewById(R.id.health_detail_time_prompt))
//					.setText(String.format(
//							getString(R.string.your_car_health_maintenance),
//							date % 30, getString(R.string.health_day)));
//		} else {
//			((TextView) confirm.findViewById(R.id.health_detail_time_prompt))
//					.setText(String.format(
//							getString(R.string.your_car_health_maintenance),
//							(int)Math.rint(date / 30f), getString(R.string.health_month)));
//		}
//
//		confirm.findViewById(R.id.health_detail_time).setVisibility(
//				View.VISIBLE);
//
//		((Button) confirm.findViewById(R.id.health_cancel))
//				.setOnClickListener(new Button.OnClickListener() {
//					public void onClick(View v) {
//						alertDialog.cancel();
//					}
//				});
//	}
//	
	
	private String value = "";
    private MaintenanceActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("=====>", "AppleFragment onAttach");
        //value = mainActivity.getAppleData();
        mActivity = (MaintenanceActivity) activity;
        SharedPreferences userInfo = activity.getSharedPreferences("maintenance_info",
                Activity.MODE_PRIVATE);
        int interval = userInfo.getInt("interval", DEFAULT_MONTHS);

         if(activity.getIntent().getBooleanExtra("notification_confirm", false) ){
                SharedPreferences.Editor editor = userInfo.edit();
                Calendar today = Calendar.getInstance();
                editor.putLong("maintenance_date",
                        today.getTimeInMillis());
                editor.putLong("last_maintenance_date",
                        today.getTimeInMillis());
                today.add(Calendar.MONTH, interval);
                editor.putLong("next_maintenance_date",
                        today.getTimeInMillis());
                editor.commit();
         }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d("=====>", "AppleFragment onCreateView");
        View root = inflater.inflate(R.layout.maintenance_settings, container, false);
        
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("=====>", "AppleFragment onActivityCreated");
//      TextView txtResult = (TextView) this.getView().findViewById(R.id.textView1);
//      txtResult.setText(value);
    }
    
    

}
