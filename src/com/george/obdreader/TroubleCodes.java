package com.george.obdreader;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.george.obdreader.db.Databasehelper;
import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;
import com.george.utils.Device;

import eu.lighthouselabs.obd.commands.control.DtcNumberObdCommand;
import eu.lighthouselabs.obd.commands.control.PendingTroubleCodesObdCommand;
import eu.lighthouselabs.obd.commands.control.TroubleCodesObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

public class TroubleCodes extends Activity implements OnClickListener,
		OnItemClickListener {

	private IPostListener mListener = null;
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;

	private static final String TAG = "TroubleCodes";

	private List<Map<String, Object>> mTroubleCodes;
	
	private static final int OBDCONNECTED = 0;
	
	private static final int UPDATEVIEW = 1;

	private SimpleAdapter mAdapter;

	private boolean isSearching;

	private Dialog mDialog;

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
						// final AnimationDrawable anim = (AnimationDrawable)
						// ((Button) findViewById(R.id.check_start_check))
						// .getCompoundDrawables()[0];
						// anim.stop();
						findViewById(R.id.progressBar1)
								.setVisibility(View.GONE);
						findViewById(R.id.start_check).setVisibility(
								View.VISIBLE);
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
											.getCommand()).getResult());
					for (String troubleCode : ((TroubleCodesObdCommand) job
							.getCommand()).getTroubleCodes()) {

						Map<String, Object> item = new HashMap<String, Object>();
						item.put("PID", troubleCode);
						item.put("CN_DESC", "");
						item.put("ICON", R.drawable.troub_codes_icon);
						mTroubleCodes.add(item);
					}

				}
				if (job.getCommand().getId() == AvailableCommandNames.PENDING_TROUBLE_CODES) {
					Log.e(TAG,
							"--------------PENDING_TROUBLE_CODES = "
									+ ((PendingTroubleCodesObdCommand) job
											.getCommand()).getResult());
					for (String troubleCode : ((PendingTroubleCodesObdCommand) job
							.getCommand()).getTroubleCodes()) {

						Map<String, Object> item = new HashMap<String, Object>();
						item.put("PID", troubleCode);
						item.put("CN_DESC", "");
						item.put("ICON", R.drawable.troub_codes_icon_w);
						mTroubleCodes.add(item);
					}
					new Thread(dbQueryThread).start();
				}
			} else {
				switch(msg.what){
				case OBDCONNECTED:
					DtcNumberObdCommand dtcNum = new DtcNumberObdCommand();
					mServiceConnection.addJobToQueue(new ObdCommandJob(dtcNum));
					mTroubleCodes.clear();
					mAdapter.notifyDataSetChanged();
					isSearching = true;
					break;
				case UPDATEVIEW:
				mAdapter.notifyDataSetChanged();
				findViewById(R.id.progressBar1).setVisibility(View.GONE);
				findViewById(R.id.start_check).setVisibility(View.VISIBLE);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trouble_codes);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		ListView troubleCodesListView = (ListView) findViewById(R.id.listView1);
		mTroubleCodes = new ArrayList<Map<String, Object>>();
		mAdapter = new SimpleAdapter(this, mTroubleCodes,
				R.layout.trouble_codes_item, new String[] { "ICON", "PID",
						"CN_DESC" }, new int[] { R.id.fault_icon, R.id.pid,
						R.id.desc });
		troubleCodesListView.setAdapter(mAdapter);
		troubleCodesListView.setOnItemClickListener(this);
		mListener = new IPostListener() {
			public void stateUpdate(ObdCommandJob job) {
				mHandler.obtainMessage(0, job).sendToTarget();
			}

			@Override
			public void deviceConnected(String deviceName) {
				mHandler.sendEmptyMessage(OBDCONNECTED);
			}

			@Override
			public void connectFailed(String deviceName) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectingDevice(String deviceName) {
				// TODO Auto-generated method stub
				
			}
		};
		mServiceIntent = new Intent(this, ObdGatewayService.class);
		mServiceConnection = new ObdGatewayServiceConnection();
		getApplicationContext().bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
		mServiceConnection.setServiceListener(mListener);

		findViewById(R.id.start_check).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_check:
			if (!isSearching) {
				mServiceConnection.addJobToQueue(new ObdCommandJob( new DtcNumberObdCommand()));
				mTroubleCodes.clear();
				mAdapter.notifyDataSetChanged();
				isSearching = true;
				findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
				v.setVisibility(View.GONE);

			}
			break;
		case R.id.ok:
			mDialog.dismiss();
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
				for (Map<String, Object> item : mTroubleCodes) {
					String pid = (String) item.get("PID");
					Log.e(TAG, "PID = " + pid);
					Cursor cursor = db.query("OBDDef", new String[] { "PID",
							"EN_Def", "CN_Def", "Category", "Knowledge" },
							"PID='" + pid + "'", null, null, null, null);
					int col = cursor.getColumnIndex("EN_Def");
					Log.e(TAG, "size = " + cursor.getCount());
					Log.e(TAG, "col = " + col);
					if(cursor.getCount()==0)
						break;
					cursor.moveToFirst();
					try {
						item.put("CN_DESC",
								Device.deCrypto(cursor.getString(cursor.getColumnIndex("CN_Def")), null));
						item.put("EN_DESC",
								Device.deCrypto(cursor.getString(cursor.getColumnIndex("EN_Def")), null));
						item.put("CATEGORY",
								Device.deCrypto(cursor.getString(cursor.getColumnIndex("Category")), null));
						item.put("KNOWLEDGE", Device.deCrypto(cursor.getString(cursor
								.getColumnIndex("Knowledge")), null));
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalBlockSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					cursor.close();
				}
				db.close();
				dbHelper.close();
				mHandler.sendEmptyMessage(UPDATEVIEW);
				isSearching = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		createDialog(arg2);
	}

	private void createDialog(int position) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.trouble_code_info, null);
		TextView tmp = (TextView) layout.findViewById(R.id.pid);
		tmp.setText(mTroubleCodes.get(position).get("PID").toString());
		tmp = (TextView) layout.findViewById(R.id.cn_des);
		tmp.setText(mTroubleCodes.get(position).get("CN_DESC").toString());
		tmp = (TextView) layout.findViewById(R.id.en_des);
		tmp.setText(mTroubleCodes.get(position).get("EN_DESC").toString());
		tmp = (TextView) layout.findViewById(R.id.category);
		tmp.setText(mTroubleCodes.get(position).get("CATEGORY").toString());
		tmp = (TextView) layout.findViewById(R.id.knowledge);
		tmp.setText(mTroubleCodes.get(position).get("KNOWLEDGE").toString());

		layout.findViewById(R.id.ok).setOnClickListener(this);
		mDialog = new Dialog(this, R.style.FullHeightDialog);
		mDialog.setContentView(layout);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);

		/*
		 * lp.x与lp.y表示相对于原始位置的偏移.
		 * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
		 * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
		 * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
		 * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
		 * 当参数值包含Gravity.CENTER_HORIZONTAL时
		 * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
		 * 当参数值包含Gravity.CENTER_VERTICAL时
		 * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
		 * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
		 * Gravity.CENTER_VERTICAL.
		 * 
		 * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
		 * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
		 * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
		 */
//		lp.x = 100; // 新位置X坐标
//		lp.y = 100; // 新位置Y坐标
//		lp.alpha = 0.7f; // 透明度
		Rect displayRectangle = new Rect();
		dialogWindow.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {  
			lp.width = (int)(displayRectangle.width() * 0.7f); // 宽度
			lp.height = (int)(displayRectangle.height() * 0.9f); // 高度
			Log.e(TAG, "width = "+(int)(displayRectangle.width() * 0.7f));
			Log.e(TAG, "height = "+(int)(displayRectangle.height() * 0.9f));
	        // 加入横屏要处理的代码  
	    } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {  
			lp.width = (int)(displayRectangle.width() * 0.9f); // 宽度
			lp.height = (int)(displayRectangle.height() * 0.5f); // 高度
			Log.e(TAG, "width = "+(int)(displayRectangle.width() * 0.9f));
			Log.e(TAG, "height = "+(int)(displayRectangle.height() * 0.5f));

	    }  
//		layout.setMinimumWidth((int)(displayRectangle.width() * 0.9f));
//		layout.setMinimumHeight((int)(displayRectangle.height() * 0.9f));
		// 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
		// dialog.onWindowAttributesChanged(lp);
		dialogWindow.setAttributes(lp);

		mDialog.show();
	};

}
