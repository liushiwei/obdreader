package com.george.obdreader.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class MaintenanceLogTable implements BaseColumns {
	public static final String TABLE_NAME = "maintenance_log";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseProvider.AUTHORITY
			+ "/"+TABLE_NAME);
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.george.maintenance_log";
	public static final String DEFAULT_SORT_ORDER = "time ASC";
	public static final String TIME = "time";
	public static final String COST = "cost";
	public static final String CONTENT = "content";
	
	

}
