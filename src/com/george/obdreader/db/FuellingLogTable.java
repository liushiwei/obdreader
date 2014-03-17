package com.george.obdreader.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class FuellingLogTable implements BaseColumns {
	public static final String TABLE_NAME = "fuelling_log";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY
			+ "/"+TABLE_NAME);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.george.fuelling_log";
	public static final String DEFAULT_SORT_ORDER = "time ASC";
	public static final String TIME = "time";
	public static final String COST = "cost";
	public static final String CONTENT = "content";
	public static final String ISFULL = "isFull";
	public static final String ISALERT = "isAlert";
	public static final String MILEAGE = "mileage";
	public static final String PRICE = "price";
	public static final String FORGETLAST = "forgetLast";
	public static final String GASTYPE = "gasType";
	public static final String AMOUNT = "amount";
	

}
