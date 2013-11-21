package com.george.obdreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;

import eu.lighthouselabs.obd.commands.SpeedObdCommand;
import eu.lighthouselabs.obd.commands.control.DtcNumberObdCommand;
import eu.lighthouselabs.obd.commands.control.PendingTroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.control.TroubleCodesObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class TroubleCodes extends Activity implements OnClickListener {

	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;

	private static final String TAG = "TroubleCodes";

	private List<Map<String, String>> mTroubleCodes;

	private SimpleAdapter mAdapter;
	
	private boolean isSearching;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Log.e(TAG, "get command message");
			if (msg.obj instanceof ObdCommandJob) {
				ObdCommandJob job = (ObdCommandJob) msg.obj;
				if (job.getCommand().getId() == AvailableCommandNames.DTC_NUMBER) {
					if (((DtcNumberObdCommand) job.getCommand())
							.getTotalAvailableCodes() > 0) {
						TroubleCodesObdCommand troCode = new TroubleCodesObdCommand(
								((DtcNumberObdCommand) job.getCommand())
										.getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(
								troCode));
						PendingTroubleCodesObdCommand pendingTroCode = new PendingTroubleCodesObdCommand(
								((DtcNumberObdCommand) job.getCommand())
										.getTotalAvailableCodes());
						mServiceConnection.addJobToQueue(new ObdCommandJob(
								pendingTroCode));
					} else {
						final AnimationDrawable anim = (AnimationDrawable) ((Button) findViewById(R.id.check_start_check))
								.getCompoundDrawables()[0];
						anim.stop();
						Toast.makeText(TroubleCodes.this,
								R.string.check_not_error, Toast.LENGTH_LONG)
								.show();
						isSearching = false;
					}
				}
				if (job.getCommand().getId() == AvailableCommandNames.TROUBLE_CODES) {
					Log.e(TAG,
							"--------------TROUBLE_CODES = "
									+ ((TroubleCodesObdCommand) job
											.getCommand()).formatResult());
					for (String troubleCode : ((TroubleCodesObdCommand) job
							.getCommand()).getTroubleCodes()) {

						Map<String, String> item = new HashMap<String, String>();
						item.put("PID", troubleCode);
						item.put("DESC", "");
						mTroubleCodes.add(item);
					}

				}
				if (job.getCommand().getId() == AvailableCommandNames.PENDING_TROUBLE_CODES) {
					Log.e(TAG,
							"--------------PENDING_TROUBLE_CODES = "
									+ ((PendingTroubleCodesObdCommand) job
											.getCommand()).formatResult());
					for (String troubleCode : ((PendingTroubleCodesObdCommand) job
							.getCommand()).getTroubleCodes()) {

						Map<String, String> item = new HashMap<String, String>();
						item.put("PID", troubleCode);
						item.put("DESC", "");
						mTroubleCodes.add(item);
					}
					new Thread(dbQueryThread).start();
				}
			}else{
				mAdapter.notifyDataSetChanged();
				final AnimationDrawable anim = (AnimationDrawable) ((Button) findViewById(R.id.check_start_check))
						.getCompoundDrawables()[0];
				anim.stop();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trouble_codes);
		findViewById(R.id.home_button).setOnClickListener(this);
		findViewById(R.id.back_button).setOnClickListener(this);
		findViewById(R.id.check_start_check).setOnClickListener(this);

		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				mHandler.obtainMessage(0, job).sendToTarget();
			}

			@Override
			public void deviceConnected(String deviceName) {
				SpeedObdCommand speed = new SpeedObdCommand();
				speed.setPriority(1);
				speed.setDelay(100);
				mServiceConnection.addJobToQueue(new ObdCommandJob(speed));
			}
		};
		mServiceIntent = new Intent(this, ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		mServiceConnection.setServiceListener(mListener);
		getApplicationContext().bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		ListView troubleCodesListView = (ListView) findViewById(R.id.listView1);
		mTroubleCodes = new ArrayList<Map<String, String>>();
		mAdapter = new SimpleAdapter(this, mTroubleCodes,
				R.layout.trouble_codes_item, new String[] { "PID", "DESC" },
				new int[] { R.id.pid, R.id.desc });
		troubleCodesListView.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_start_check:
			if(!isSearching){
			final AnimationDrawable anim = (AnimationDrawable) ((Button) v)
					.getCompoundDrawables()[0];
			anim.start();
			DtcNumberObdCommand dtcNum = new DtcNumberObdCommand();
			mServiceConnection.addJobToQueue(new ObdCommandJob(dtcNum));
			mTroubleCodes.clear();
			mAdapter.notifyDataSetChanged();
			isSearching = true;
			}
			break;
		case R.id.home_button:
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			break;
		case R.id.back_button:
			finish();
			break;

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mServiceConnection != null && mServiceConnection.isRunning()) {
			mServiceConnection.clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		getApplicationContext().unbindService(mServiceConnection);
		if (mServiceConnection != null && mServiceConnection.isRunning())
			stopService(mServiceIntent);
		super.onDestroy();
	}


	Runnable dbQueryThread = new Runnable() {

		@Override
		public void run() {
			try {
				Databasehelper dbHelper = new Databasehelper(TroubleCodes.this);
				dbHelper.createDatabase();
				dbHelper.openDatabase();
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				for(Map<String, String> item:mTroubleCodes){
					String pid = item.get("PID");
					Log.e(TAG, "PID = "+pid);
					Cursor cursor = db.query("OBDDef", new String[]{"PID","EN_Def"}, "PID='"+pid+"'", null, null, null, null);
					int col = cursor.getColumnIndex("EN_Def");
					Log.e(TAG, "size = "+cursor.getCount());
					Log.e(TAG, "col = "+col);
					cursor.moveToFirst();
					item.put("DESC", cursor.getString(cursor.getColumnIndex("EN_Def")));
					cursor.close();
				}
				db.close();
				mHandler.sendEmptyMessage(1);
				isSearching = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

	};

}
