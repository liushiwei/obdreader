package com.george.obdreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OBDProgressBarActivity extends Activity {

	private String[] mPid_decs = new String[OBDEnums.values().length];
	private boolean[] mPid_show = new boolean[OBDEnums.values().length];
	private List<String> mShow_pids;
	private List<OBDEnums> mEnums_pids;
	private OBDEnums[] mPidsEnum;
	private Map<String, Float> mPids;
	private static final float DEFAULT_VALUE = -0xffff;
	private ProgressBarAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.obd_progress_bar_list);
		mPids = new HashMap<String, Float>();
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
		for (int i = 0; i < mPidsEnum.length; i++) {
			mPid_decs[i] = getString(mPidsEnum[i].getDesc());
			if (mShow_pids.contains(mPidsEnum[i].getCommand())) {
				mPid_show[i] = true;
				// mPids.put(mPidsEnum[i].getCommand(), DEFAULT_VALUE);
				mEnums_pids.add(mPidsEnum[i]);
			} else {
				mPid_show[i] = false;
			}
		}
		plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(
						OBDProgressBarActivity.this)
						.setTitle(R.string.obd_pid)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMultiChoiceItems(mPid_decs, mPid_show,
								new OnMultiChoiceClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
										OBDEnums pid = mPidsEnum[which];
										if (isChecked) {
											mShow_pids.add(pid.getCommand());
											mEnums_pids.add(pid);
											mAdapter.notifyDataSetChanged();
										} else {
											mShow_pids.remove(pid.getCommand());
											mEnums_pids.remove(pid);
											mAdapter.notifyDataSetChanged();
										}

									}
								}).setNegativeButton(android.R.string.ok, null)
						.show();
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
					}
				});
			}
		});

		mAdapter = new ProgressBarAdapter(this);
		ListView listView = (ListView) findViewById(R.id.obdlist);
		listView.setAdapter(mAdapter);
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
			convertView = mInflater.inflate(
					R.layout.obd_progress_bar_list_item, null);
			TextView title = (TextView) convertView.findViewById(R.id.obd_pid_desc);
			title.setText(mEnums_pids.get(position).getDesc());
			TextView min = (TextView) convertView.findViewById(R.id.obd_pid_min_value);
			min.setText(mEnums_pids.get(position).getMin()+"");
			TextView max = (TextView) convertView.findViewById(R.id.obd_pid_max_value);
			max.setText(mEnums_pids.get(position).getMax()+"");
			TextView current = (TextView) convertView.findViewById(R.id.obd_pid_current_value);
			current.setText(mPids.get(mEnums_pids.get(position).getCommand())+"");
			return convertView;
		}

	}
}
