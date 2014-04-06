package com.george.obdreader;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.george.obdreader.db.DatabaseProvider;
import com.george.obdreader.db.FuellingLogTable;
import com.george.obdreader.ui.CustomerSpinner;

public class FuellingLogsFragment extends Fragment implements OnClickListener,
		OnFocusChangeListener {

	private List<String> mMaintenanceSelected;
	private int mTake;
	private Date mTime;
	private ExpandableListView mLogListView;
	private CursorTreeAdapterExample mAdapter;
	private View mRootView;
	private View mDialogView;
	private long mLogTime;
    private int mFuelType;
private String[] fuelTypes;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fuelling_log, container, false);
		mLogListView = (ExpandableListView) root.findViewById(R.id.expandableListView1);
		ContentResolver resolver = getActivity().getContentResolver();
		Cursor cursor = resolver.query(FuellingLogTable.CONTENT_URI, null,
				null, null, FuellingLogTable.DEFAULT_SORT_ORDER);
		mAdapter = new CursorTreeAdapterExample(cursor,getActivity(),  true);
		mLogListView.setAdapter(mAdapter);
		NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
		((TextView)root.findViewById(R.id.costs)).setText(format.format(getSumCost()));
		ImageButton add = (ImageButton) getActivity().findViewById(R.id.add);
		add.setVisibility(View.VISIBLE);
		add.setOnClickListener(this);
		mRootView = root;
		fuelTypes = getResources().getStringArray(R.array.fuel_types);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		getActivity().findViewById(R.id.add).setVisibility(View.GONE);
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add:
			mDialogView = LayoutInflater.from(getActivity()).inflate(
					R.layout.fuelling_log_dialog, null);
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			
	        CustomerSpinner fuel_type_spinner = (CustomerSpinner)mDialogView.findViewById(R.id.spinner1);
	        fuel_type_spinner.setList(fuelTypes);
	        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(),
	                R.layout.spinner_item, fuelTypes);
	        fuel_type_spinner.setAdapter(adapter);
	        fuel_type_spinner.setSelection(preferences.getInt("last_time_fule_type", 2), false);
	        fuel_type_spinner
	                .setOnItemSelectedListener(new OnItemSelectedListener() {

	                    @Override
	                    public void onItemSelected(AdapterView<?> parent,
	                            View view, int position, long id) {
	                        mFuelType = position;

	                    }

	                    @Override
	                    public void onNothingSelected(AdapterView<?> parent) {
	                        // TODO Auto-generated method stub

	                    }

	                });
			EditText edtInput = (EditText) mDialogView.findViewById(R.id.today);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			edtInput.setText(sdf.format(new Date()));
			edtInput.setOnClickListener(this);
			edtInput = (EditText) mDialogView.findViewById(R.id.price);
			float price = preferences.getFloat("last_time_price", -1);
			if (price > 0) {
				edtInput.setText(price + "");
			}
			edtInput.setOnFocusChangeListener(this);
			edtInput = (EditText) mDialogView.findViewById(R.id.cost);
			edtInput.setOnFocusChangeListener(this);
			edtInput = (EditText) mDialogView.findViewById(R.id.amount);
			edtInput.setOnFocusChangeListener(this);
			 AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			builder.setCancelable(false)
					.setView(mDialogView)
					.setPositiveButton(getString(android.R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									

								}
							})
					.setNegativeButton(getString(android.R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							});
			final AlertDialog alertDialog = builder.show();
			Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText mileage = (EditText) mDialogView
							.findViewById(R.id.currentMileage);
					String currentMileage = mileage.getText().toString();
					if (currentMileage.length() == 0) {
//						Toast.makeText(getActivity(),
//								R.string.not_null, Toast.LENGTH_LONG)
//								.show();
						Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
						mileage.startAnimation(shake);
						return;
					}
					mileage = (EditText) mDialogView
							.findViewById(R.id.price);
					String price = mileage.getText().toString();
					if (price.length() == 0) {
						Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
						mileage.startAnimation(shake);
						return;
					}
					mileage = (EditText) mDialogView
							.findViewById(R.id.cost);
					String cost = mileage.getText().toString();
					if (cost.length() == 0) {
						Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
						mileage.startAnimation(shake);
						return;
					}
					mileage = (EditText) mDialogView
							.findViewById(R.id.amount);
					String amount = mileage.getText().toString();
					if (amount.length() == 0) {
						Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
						mileage.startAnimation(shake);
						return;
					}
					
					mileage = (EditText) mDialogView
							.findViewById(R.id.content);
					String content = mileage.getText().toString();
					
					
					ContentResolver resolver = getActivity().getContentResolver();
					ContentValues values = new ContentValues();  
		            values.put(FuellingLogTable.CONTENT, content);  
		            values.put(FuellingLogTable.COST, Float.valueOf(cost));
		            values.put(FuellingLogTable.PRICE, Float.valueOf(price));
		            values.put(FuellingLogTable.AMOUNT, Float.valueOf(amount));
		            values.put(FuellingLogTable.MILEAGE, Float.valueOf(currentMileage));
		            values.put(FuellingLogTable.ISFULL, ((CheckBox) mDialogView.findViewById(R.id.isFull)).isChecked());
		            values.put(FuellingLogTable.ISALERT, ((CheckBox) mDialogView.findViewById(R.id.isLightOn)).isChecked());
		            values.put(FuellingLogTable.FORGETLAST, ((CheckBox) mDialogView.findViewById(R.id.forgetLog)).isChecked());
		            values.put(FuellingLogTable.TIME, mLogTime);  
		            values.put(FuellingLogTable.GASTYPE, mFuelType);
		            resolver.insert(FuellingLogTable.CONTENT_URI, values);  
		            mAdapter.notifyDataSetChanged();
		            NumberFormat format = NumberFormat.getInstance();
		            format.setMaximumFractionDigits(2);
		            ((TextView)mRootView.findViewById(R.id.costs)).setText(format.format(getSumCost()));
					alertDialog.dismiss();
					
				}
			});

			break;
		case R.id.today:
			Calendar today = Calendar.getInstance();
			mLogTime = today.getTimeInMillis();
			DatePickerDialog dialog = new DatePickerDialog(
					FuellingLogsFragment.this.getActivity(), dateListener,
					today.get(Calendar.YEAR), today.get(Calendar.MONTH),
					today.get(Calendar.DAY_OF_MONTH));
			dialog.show();
			break;

		}

	}


	class ViewHolder {
		TextView cost;
		TextView price;
		TextView mileage;
		TextView content;
		TextView amount;
		TextView fueltype;
		TextView time;
	}

	DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {
			EditText editText = (EditText) mDialogView.findViewById(R.id.today);
			editText.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
			Calendar today = Calendar.getInstance();
			today.set(year, month, dayOfMonth);
			mLogTime = today.getTimeInMillis();

		}
	};

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			switch (v.getId()) {
			case R.id.price: {
				Log.e("FuellingLog", "cost");
				EditText cost = (EditText) mDialogView.findViewById(R.id.cost);
				EditText amount = (EditText) mDialogView
						.findViewById(R.id.amount);
				String c = cost.getText().toString().trim();
				String a = amount.getText().toString().trim();
				if (c.length() != 0 && a.length() != 0) {
					float p = Float.valueOf(c) / Float.valueOf(a);
					EditText price = (EditText) v;
					NumberFormat format = NumberFormat.getInstance();
					format.setMaximumFractionDigits(2);
					price.setText(format.format(p));
					Log.e("FuellingLog", "price = " + format.format(p));
				}
				break;
			}
			case R.id.cost: {
				Log.e("FuellingLog", "cost");
				EditText price = (EditText) mDialogView
						.findViewById(R.id.price);
				EditText amount = (EditText) mDialogView
						.findViewById(R.id.amount);
				String p = price.getText().toString().trim();
				String a = amount.getText().toString().trim();
				if (p.length() != 0 && a.length() != 0) {
					float c = Float.valueOf(p) * Float.valueOf(a);
					EditText cost = (EditText) v;
					NumberFormat format = NumberFormat.getInstance();
					format.setMaximumFractionDigits(2);
					cost.setText(format.format(c));
					Log.e("FuellingLog", "cost = " + format.format(c));
				}
			}
				break;
			case R.id.amount: {
				Log.e("FuellingLog", "amount");
				EditText price = (EditText) mDialogView
						.findViewById(R.id.price);
				EditText cost = (EditText) mDialogView.findViewById(R.id.cost);
				String p = price.getText().toString().trim();
				String c = cost.getText().toString().trim();
				if (p.length() != 0 && c.length() != 0) {
					float a = Float.valueOf(c) / Float.valueOf(p);
					EditText amount = (EditText) v;
					NumberFormat format = NumberFormat.getInstance();
					format.setMaximumFractionDigits(2);
					amount.setText(format.format(a));
					Log.e("FuellingLog", "amount = " + format.format(a));
				}
			}
				break;

			default:
				break;
			}
		}

	}
	
	class CursorTreeAdapterExample extends CursorTreeAdapter {
        private int mGroupIdColumnIndex;
        private LayoutInflater mInflater;
        //注意这里的游标是一级项的
        public CursorTreeAdapterExample(Cursor cursor, Context context) {
            super(cursor, context);
            
            mGroupIdColumnIndex = cursor.getColumnIndexOrThrow(Phone._ID);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        
        public CursorTreeAdapterExample(Cursor cursor, Context context, boolean autoRequery) {
            super(cursor, context, autoRequery);
            mGroupIdColumnIndex = cursor.getColumnIndexOrThrow(Phone._ID);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        //注意这里的游标是二级项的
        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isExpanded) {
            // Bind the related data with this child view
            ViewHolder holder = (ViewHolder) view.getTag();
            
            float price = cursor.getFloat(cursor
                    .getColumnIndex(FuellingLogTable.PRICE));
           
            String content = cursor.getString(cursor
                    .getColumnIndex(FuellingLogTable.CONTENT));
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            holder.price.setText(format.format(price));
            holder.content.setText(content);
            holder.amount.setText(cursor.getFloat(cursor
                    .getColumnIndex(FuellingLogTable.AMOUNT))+"");
            holder.fueltype.setText(fuelTypes[cursor.getInt(cursor
                    .getColumnIndex(FuellingLogTable.GASTYPE))]);
        }
        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            // Bind the related data with this group view
            ViewHolder holder = (ViewHolder) view.getTag();
            long time = cursor.getLong(cursor
                    .getColumnIndex(FuellingLogTable.TIME));
            float cost = cursor.getFloat(cursor
                    .getColumnIndex(FuellingLogTable.COST));
            float mileage = cursor.getInt(cursor
                    .getColumnIndex(FuellingLogTable.MILEAGE));
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            holder.cost.setText(format.format(cost));
            holder.mileage.setText(format.format(mileage));
            holder.time.setText(new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date(time)));
        }
        //注意这里通过一次数据库查询才得到了二级项的数据
        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            Uri.Builder builder = FuellingLogTable.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, groupCursor.getLong(groupCursor.getColumnIndex(FuellingLogTable._ID)));
            Uri uri = builder.build();
            ContentResolver resolver = getActivity().getContentResolver();
            
            return resolver.query(uri, null, null, null, null);
        }
        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            
           
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View inflate = inflater
                    .inflate(R.layout.fuelling_log_item_children, null);
            holder.content = (TextView) inflate.findViewById(R.id.content);
            holder.price = (TextView) inflate.findViewById(R.id.price);
            holder.amount = (TextView) inflate.findViewById(R.id.amount);
            holder.fueltype = (TextView) inflate.findViewById(R.id.fueltype);
            inflate.setTag(holder);
            return inflate;// 返回的view传给bindView。
            
        }
        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View inflate = inflater
                    .inflate(R.layout.fuelling_log_item, null);
            holder.cost = (TextView) inflate.findViewById(R.id.cost);
            holder.time = (TextView) inflate.findViewById(R.id.time);
            holder.mileage = (TextView) inflate.findViewById(R.id.mileage);
            inflate.setTag(holder);
            return inflate;// 返回的view传给bindView。
        }
    }
	
	private float getSumCost(){
	    ContentProviderClient client =  getActivity().getContentResolver().acquireContentProviderClient(DatabaseProvider.AUTHORITY);
	    SQLiteDatabase dbHandle= ((DatabaseProvider)client.getLocalContentProvider()).getDbHandle();
	    Cursor cursor = dbHandle.rawQuery("SELECT sum("+FuellingLogTable.COST+") FROM "+FuellingLogTable.TABLE_NAME , null);
	    cursor.moveToFirst();
	    float cnt =  cursor.getFloat(0);
	    cursor.close();
	    cursor.deactivate();
	    client.release();
	    return cnt;
	}

}