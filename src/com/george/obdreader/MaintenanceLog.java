package com.george.obdreader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.george.obdreader.db.MaintenanceLogTable;

public class MaintenanceLog extends Fragment implements OnClickListener {

	private List<String> mMaintenanceSelected;
	private int mTake;
	private Date mTime;
	private ListView mLogListView;
	private SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater
				.inflate(R.layout.maintenance_log, container, false);
		mLogListView = (ListView) root.findViewById(R.id.listView1);
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(MaintenanceLogTable.CONTENT_URI, null,
				null, null, MaintenanceLogTable.DEFAULT_SORT_ORDER);

		mAdapter = new SimpleCursorAdapter(getActivity(), cursor, true);
		mLogListView.setAdapter(mAdapter);
		ImageButton add = (ImageButton) getActivity()
				.findViewById(R.id.add);
		add.setVisibility(View.VISIBLE);
		add.setOnClickListener(this);
		return root;
	}

	@Override
	public void onDestroyView() {
		Log.d("=====>", "MaintenanceLog onDestroyView");
		getActivity().findViewById(R.id.add).setVisibility(View.GONE);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		final String[] options = getResources().getStringArray(
				R.array.maintenance_types);
		boolean isSelected[] = new boolean[options.length];
		mMaintenanceSelected = new ArrayList<String>();
		mMaintenanceSelected.add(options[0]);
		isSelected[0] = true;
		// final View textEntryView = inflater.inflate(
		// R.layout.dialoglayout, null);
		// final EditText
		// edtInput=(EditText)textEntryView.findViewById(R.id.edtInput);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.selecte_maintenance_options));
		builder.setMultiChoiceItems(options, isSelected,
				new OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which,
							boolean isChecked) {
						if (isChecked) {
							mMaintenanceSelected.add(options[which]);
						} else {
							mMaintenanceSelected.remove(options[which]);
						}
					}
				});
		builder.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.d("=====>",
								"MaintenanceLog mMaintenanceSelected = "
										+ mMaintenanceSelected.toString());
						LayoutInflater layoutInflater = LayoutInflater
								.from(getActivity());
						final View textEntryView = layoutInflater.inflate(
								R.layout.maintenance_options, null);
						final EditText edtInput = (EditText) textEntryView
								.findViewById(R.id.editText1);
						final DatePicker picker = (DatePicker) textEntryView
								.findViewById(R.id.datePicker1);
						final Calendar maintenanceDate = Calendar.getInstance();
						picker.init(maintenanceDate.get(Calendar.YEAR), maintenanceDate.get(Calendar.MONTH), maintenanceDate.get(Calendar.DAY_OF_MONTH),
								new OnDateChangedListener() {

									@Override
									public void onDateChanged(DatePicker view,
											int year, int monthOfYear,
											int dayOfMonth) {

										Log.d("=====>", "year = " + year
												+ " monthOfYear = "
												+ monthOfYear
												+ " dayOfMonth = " + dayOfMonth);
										maintenanceDate.set(year, monthOfYear, dayOfMonth);
									}
								});
						final AlertDialog.Builder builder = new AlertDialog.Builder(
								getActivity());
						builder.setCancelable(false);
						builder.setView(textEntryView);
						builder.setTitle(getString(R.string.maintenance_time));
						builder.setPositiveButton(
								getString(android.R.string.ok),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ContentResolver resolver = getActivity().getContentResolver();
										ContentValues values = new ContentValues();  
							            values.put(MaintenanceLogTable.CONTENT, mMaintenanceSelected.toString());  
							            values.put(MaintenanceLogTable.COST, edtInput.getText().toString());  
							            values.put(MaintenanceLogTable.TIME, maintenanceDate.getTimeInMillis());  
							            resolver.insert(MaintenanceLogTable.CONTENT_URI, values);  
							            mAdapter.notifyDataSetChanged();
									}

								});
						builder.setNegativeButton(
								getString(android.R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
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

	class SimpleCursorAdapter extends CursorAdapter {

		public SimpleCursorAdapter(Context context, Cursor c,
				boolean autoRequery) {
			super(context, c, autoRequery);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context arg1, Cursor cursor) {
			// TODO Auto-generated method stub
			ViewHolder holder = (ViewHolder) view.getTag();
			long time = cursor.getLong(cursor
					.getColumnIndex(MaintenanceLogTable.TIME));
			int cost = cursor.getInt(cursor
					.getColumnIndex(MaintenanceLogTable.COST));
			String content = cursor.getString(cursor
					.getColumnIndex(MaintenanceLogTable.CONTENT));
			holder.cost.setText(cost + "");
			holder.content.setText(content);
			holder.time.setText(new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date(time)));
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			ViewHolder holder = new ViewHolder();
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View inflate = inflater
					.inflate(R.layout.maintenance_log_item, null);
			holder.cost = (TextView) inflate.findViewById(R.id.cost);
			holder.content = (TextView) inflate.findViewById(R.id.content);
			holder.time = (TextView) inflate.findViewById(R.id.time);
			inflate.setTag(holder);
			return inflate;// 返回的view传给bindView。
		}

	}

	class ViewHolder {
		TextView cost;
		TextView content;
		TextView time;
	}

}
