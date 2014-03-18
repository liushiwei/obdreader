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

import com.george.obdreader.db.FuellingLogTable;

public class FuellingLogsFragment extends Fragment implements OnClickListener {

	private List<String> mMaintenanceSelected;
	private int mTake;
	private Date mTime;
	private ListView mLogListView;
	private SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fuelling_log, container, false);
		mLogListView = (ListView) root.findViewById(R.id.listView1);
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(FuellingLogTable.CONTENT_URI, null,
				null, null, FuellingLogTable.DEFAULT_SORT_ORDER);
		mAdapter = new SimpleCursorAdapter(getActivity(), cursor, true);
		mLogListView.setAdapter(mAdapter);
		ImageButton add = (ImageButton) getActivity()
				.findViewById(R.id.add_pid);
		add.setVisibility(View.VISIBLE);
		add.setOnClickListener(this);
		return root;
	}

	@Override
	public void onDestroyView() {
		getActivity().findViewById(R.id.add_pid).setVisibility(View.GONE);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {

		final View textEntryView = LayoutInflater.from(getActivity()).inflate(
				R.layout.fuelling_log_dialog, null);
		final EditText edtInput = (EditText) textEntryView
				.findViewById(R.id.editText1);
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.selecte_maintenance_options));
		builder.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

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
					.getColumnIndex(FuellingLogTable.TIME));
			int cost = cursor.getInt(cursor
					.getColumnIndex(FuellingLogTable.COST));
			String content = cursor.getString(cursor
					.getColumnIndex(FuellingLogTable.CONTENT));
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