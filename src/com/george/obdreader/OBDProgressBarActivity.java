package com.george.obdreader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.george.obdreader.io.IPostListener;
import com.george.obdreader.io.ObdCommandJob;
import com.george.obdreader.io.ObdGatewayService;
import com.george.obdreader.io.ObdGatewayServiceConnection;

import eu.lighthouselabs.obd.commands.protocol.SimpleObdCommand;

public class OBDProgressBarActivity extends Activity implements OnItemClickListener, OnClickListener {

	private String[] mPid_decs = new String[OBDEnums.values().length];
	private boolean[] mPid_show = new boolean[OBDEnums.values().length];
	private List<String> mShow_pids;
	private List<OBDEnums> mEnums_pids;
	private OBDEnums[] mPidsEnum;
	private Map<String, PidValue> mPids;
	private static final float DEFAULT_VALUE = -0xffff;
	private ProgressBarAdapter mAdapter;
	private boolean isBound;
	private boolean isGetSupportedOver;
	private boolean isSupported[];
	private PidsAdapter mPidAdapter;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof ObdCommandJob) {
				ObdCommandJob job = (ObdCommandJob) msg.obj;
				String cmdName = job.getCommand().getName();
				String cmdResult = job.getCommand().getFormattedResult();
				Log.e(TAG, "cmdName = " + cmdName + "  cmdResult = "
						+ cmdResult);
				if (job.getCommand() instanceof OBDCommand) {
					OBDCommand command = (OBDCommand) job.getCommand();
					PidValue pv = mPids.get(command.getEnums().getCommand());
					if(pv!=null){
						pv.value = command.getValue();
						mAdapter.notifyDataSetChanged();
					}
				} else {
					Log.e(TAG, "Command = " + job.getCommand().getCommand());
					if (job.getCommand().getCommand().trim().equals("0100")) {
						cmdResult = job.getCommand().getFormattedResult();
						cmdResult = 	cmdResult.substring(cmdResult.length()-8);
						Log.e(TAG,
								"hex to bin "
										+ cmdResult
										+ " ="
										+ Long.toBinaryString(Long.valueOf(
												cmdResult, 16)));
						cmdResult = Long.toBinaryString(Long.valueOf(cmdResult,
								16));
						for (int i = 1; i < 33; i++) {
							if (i < 33 - cmdResult.length()) {
								isSupported[i] = false;
							} else {
								isSupported[i] = cmdResult.charAt(i - 33
										+ cmdResult.length()) == '1' ? true
										: false;
							}
						}
						if(mPidAdapter!=null)
							mPidAdapter.notifyDataSetChanged();
					}
					if (job.getCommand().getCommand().trim().equals("0120")) {
						cmdResult = job.getCommand().getFormattedResult()
								.substring(4);
						Log.e(TAG,
								"hex to bin "
										+ cmdResult
										+ " ="
										+ Long.toBinaryString(Long.valueOf(
												cmdResult, 16)));
						cmdResult = Long.toBinaryString(Long.valueOf(cmdResult,
								16));
						for (int i = 33; i < 65; i++) {
							if (i < 65 - cmdResult.length()) {
								isSupported[i] = false;
							} else {
								isSupported[i] = cmdResult.charAt(i - 65
										+ cmdResult.length()) == '1' ? true
										: false;
							}
						}
						if(mPidAdapter!=null)
							mPidAdapter.notifyDataSetChanged();
					}
					if (job.getCommand().getCommand().trim().equals("0140")) {
						cmdResult = job.getCommand().getFormattedResult()
								.substring(4);
						Log.e(TAG,
								"hex to bin "
										+ cmdResult
										+ " ="
										+ Long.toBinaryString(Long.valueOf(
												cmdResult, 16)));
						cmdResult = Long.toBinaryString(Long.valueOf(cmdResult,
								16));
						for (int i = 65; i < 97; i++) {
							if (i < 97 - cmdResult.length()) {
								isSupported[i] = false;
							} else {
								isSupported[i] = cmdResult.charAt(i - 97
										+ cmdResult.length()) == '1' ? true
										: false;
							}
						}
						if(mPidAdapter!=null)
							mPidAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	};

	private IPostListener mListener = new IPostListener() {
		public void stateUpdate(ObdCommandJob job) {
			mHandler.obtainMessage(0, job).sendToTarget();
		}

		@Override
		public void deviceConnected(String deviceName) {

			mServiceConnection.addJobToQueue(new ObdCommandJob(
					new SimpleObdCommand("01 00", "PIDs supported [01 - 20]")));
			mServiceConnection.addJobToQueue(new ObdCommandJob(
					new SimpleObdCommand("01 20", "PIDs supported [21 - 40]")));
			mServiceConnection.addJobToQueue(new ObdCommandJob(
					new SimpleObdCommand("01 40", "PIDs supported [41 - 60]")));
			Log.e(TAG, "deviceConnected deviceName = " + deviceName);
			for (int i = 0; i < mPidsEnum.length; i++) {
				mPid_decs[i] = getString(mPidsEnum[i].getDesc());
				if (mShow_pids.contains(mPidsEnum[i].getCommand())) {
					mPid_show[i] = true;
					OBDCommand cmd = new OBDCommand(mPidsEnum[i]);
					cmd.setPriority(1);
					ObdCommandJob job = new ObdCommandJob(cmd);
					PidValue pv = new PidValue();
					pv.jobObj = job;
					mPids.put(mPidsEnum[i].getCommand(), pv);
					mEnums_pids.add(mPidsEnum[i]);
					mServiceConnection.addJobToQueue(job);

				} else {
					mPid_show[i] = false;
				}
			}
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
	private Intent mServiceIntent = null;
	private ObdGatewayServiceConnection mServiceConnection;
	private static final String TAG = "OBDProgressBarActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obd_progress_bar_list);
		isSupported = new boolean[100];
		isGetSupportedOver = false;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mPids = new HashMap<String, PidValue>();
		ImageButton plus = (ImageButton) findViewById(R.id.add_pid);
		mPidsEnum = OBDEnums.values();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		mShow_pids = new ArrayList<String>();
		mEnums_pids = new ArrayList<OBDEnums>();
		String pids = preferences.getString("PIDS", null);
		if (pids != null) {
			mShow_pids = new ArrayList<String>(Arrays.asList(pids.split(",")));
		}
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mServiceConnection != null && isBound) {
					LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
					View customView = inflater.inflate(R.layout.obd_progress_bar_pid_list, null, false);
					ListView pidsList = (ListView) customView.findViewById(R.id.pidslist);
					mPidAdapter = new PidsAdapter(OBDProgressBarActivity.this, mPid_decs);
					pidsList.setAdapter(mPidAdapter);
					
					AlertDialog dialog = new AlertDialog.Builder(
							OBDProgressBarActivity.this)
							.setTitle(R.string.obd_pid)
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(customView)
							.setNegativeButton(android.R.string.ok, null)
							.show();
					dialog.setCanceledOnTouchOutside(false);
					dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
							SharedPreferences preferences = PreferenceManager
									.getDefaultSharedPreferences(OBDProgressBarActivity.this);
							Editor editor = preferences.edit();
							Log.e("OBDProgressBar", mShow_pids.toString());
							String pids = "";

							for (String pid : mShow_pids) {
								pids += pid + ",";
							}
							editor.putString("PIDS", pids);
							editor.commit();
							mPidAdapter = null;
						}
					});
					pidsList.setOnItemClickListener(OBDProgressBarActivity.this);
				}else{
					Toast.makeText(getBaseContext(), R.string.device_unconn, Toast.LENGTH_LONG).show();
				}
			}
		});

		mAdapter = new ProgressBarAdapter(this);
		ListView listView = (ListView) findViewById(R.id.obdlist);
		listView.setAdapter(mAdapter);
	}

	@Override
	protected void onResume() {
		if (mServiceConnection == null) {
			mServiceIntent = new Intent(this, ObdGatewayService.class);
			mServiceConnection = new ObdGatewayServiceConnection();
			isBound = getApplicationContext().bindService(mServiceIntent,
					mServiceConnection, Context.BIND_AUTO_CREATE);
			mServiceConnection.setServiceListener(mListener);
		}
		super.onResume();
	}

	class ProgressBarAdapter extends BaseAdapter {
		private LayoutInflater mInflater;// 动态布局映射

		public ProgressBarAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mEnums_pids.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DecimalFormat df = new DecimalFormat("##0.00");
			convertView = mInflater.inflate(
					R.layout.obd_progress_bar_list_item, null);
			TextView title = (TextView) convertView
					.findViewById(R.id.obd_pid_desc);
			title.setText(mEnums_pids.get(position).getDesc());
			TextView min = (TextView) convertView
					.findViewById(R.id.obd_pid_min_value);
			min.setText(mEnums_pids.get(position).getMin() + "");
			TextView max = (TextView) convertView
					.findViewById(R.id.obd_pid_max_value);
			max.setText(mEnums_pids.get(position).getMax() + "");
			TextView current = (TextView) convertView
					.findViewById(R.id.obd_pid_current_value);
			if (isSupported[Integer.valueOf(mEnums_pids.get(position)
					.getCommand().substring(3), 16)]||!isGetSupportedOver) {
				if (mPids.get(mEnums_pids.get(position).getCommand()) != null)
					current.setText(mPids.get(mEnums_pids.get(position)
							.getCommand()).value == DEFAULT_VALUE ? "NODATE"
							: df.format(mPids.get(mEnums_pids.get(position)
									.getCommand()).value
									- mEnums_pids.get(position).getMin()));
			} else {
				current.setText(R.string.pid_not_supported);
			}

			ProgressBar progressBar = (ProgressBar) convertView
					.findViewById(R.id.progressBar1);
			progressBar
					.setMax((int) (mEnums_pids.get(position).getMax() - mEnums_pids
							.get(position).getMin()));
			progressBar
					.setProgress((int) (mPids.get(mEnums_pids.get(position)
							.getCommand()).value == DEFAULT_VALUE ? mEnums_pids
							.get(position).getMin() : (mPids.get(mEnums_pids
							.get(position).getCommand()).value - mEnums_pids
							.get(position).getMin())));
			return convertView;
		}

	}

	class PidValue {
		public float value;
		public ObdCommandJob jobObj;
	}

	@Override
	protected void onDestroy() {

		stopLiveData();
		super.onDestroy();
	}

	private void stopLiveData() {
		Log.d(TAG, "Stopping live data..");
		if (isBound) {
			getApplicationContext().unbindService(mServiceConnection);
			if (mServiceConnection != null && mServiceConnection.isRunning())
				stopService(mServiceIntent);
		}

		// remove runnable

	}

	class PidsAdapter extends BaseAdapter {
		Context mContext;
		String[] mData;
		LayoutInflater mInflater;
		ViewHolder holder;

		public PidsAdapter(Context context, String[] mPid_decs) {
			this.mContext = context;
			this.mData = mPid_decs;
			mInflater = (LayoutInflater) mContext
					.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			return mData.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.obd_progress_bar_pid_list_item, null);
				holder = new ViewHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.textView1);
				holder.checkBox = (CheckBox) convertView
						.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if(isSupported[mPidsEnum[position].getId()]){
				convertView.setBackgroundResource(R.drawable.pid_item_bg);
			}else{
				convertView.setBackgroundResource(android.R.color.black);
			}
			holder.title.setText(mData[position]);
			holder.checkBox.setTag(position);
			holder.checkBox.setChecked(mPid_show[position]);
			holder.checkBox.setOnClickListener(OBDProgressBarActivity.this);
			return convertView;
		}

		class ViewHolder {
			TextView title;
			CheckBox checkBox;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
		Log.e(TAG, "position = "+position);
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
		if(checkbox.isChecked()){
			checkbox.setChecked(false);
		}else{
			checkbox.setChecked(true);
		}
		OBDEnums pid = mPidsEnum[position];
		if (checkbox.isChecked()) {
			mPid_show[position] = true;
			mShow_pids.add(pid.getCommand());
			mEnums_pids.add(pid);
			OBDCommand cmd = new OBDCommand(pid);
			cmd.setPriority(1);
			ObdCommandJob job = new ObdCommandJob(cmd);
			mServiceConnection.addJobToQueue(job);
			PidValue pv = new PidValue();
			pv.jobObj = job;
			mPids.put(pid.getCommand(),
					pv);
			mAdapter.notifyDataSetChanged();
		} else {
			mPid_show[position] = false;
			mShow_pids.remove(pid
					.getCommand());
			mEnums_pids.remove(pid);
			PidValue pv = mPids.get(pid
					.getCommand());
			if(pv!=null){
				mServiceConnection.removeJobFromQueue(pv.jobObj);
				mPids.remove(pid
						.getCommand());
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	public void onClick(View view) {
		CheckBox checkbox = (CheckBox) view;
		int position = (Integer) checkbox.getTag();
		OBDEnums pid = mPidsEnum[position];
		if (checkbox.isChecked()) {
			mPid_show[position] = true;
			mShow_pids.add(pid.getCommand());
			mEnums_pids.add(pid);
			OBDCommand cmd = new OBDCommand(pid);
			cmd.setPriority(1);
			ObdCommandJob job = new ObdCommandJob(cmd);
			mServiceConnection.addJobToQueue(job);
			PidValue pv = new PidValue();
			pv.jobObj = job;
			mPids.put(pid.getCommand(),
					pv);
			mAdapter.notifyDataSetChanged();
		} else {
			mPid_show[position] = false;
			mShow_pids.remove(pid
					.getCommand());
			mEnums_pids.remove(pid);
			PidValue pv = mPids.get(pid
					.getCommand());
			if(pv!=null){
			mServiceConnection.removeJobFromQueue(pv.jobObj);
			mPids.remove(pid
					.getCommand());
			mAdapter.notifyDataSetChanged();
			}
		}

		
	}

}
