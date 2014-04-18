package com.george.obdreader.db;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.george.obdreader.Log;

public class DatabaseProvider extends ContentProvider {

	private static final String TAG = "DatabaseProvider";

	private static final String DATABASE_NAME = "data";

	private static final int DATABASE_VERSION = 1;

	private static HashMap<String, String> sMaintenanceLogTableProjectionMap;
	
	private static HashMap<String, String> sFuellingLogTableProjectionMap;

	private DatabaseHelper mOpenHelper;
	
	public static final String AUTHORITY = "com.george.obdreader.provider";

	private static final UriMatcher sUriMatcher;

	private static final int MAINTENANCE_LOG_TABLE_NO = 0x00;

	private static final int MAINTENANCE_LOG_TABLE_ID = 0x01;
	
	private static final int FUELLING_LOG_TABLE_NO = 0x10;

	private static final int FUELLING_LOG_TABLE_ID = 0x11;

	

	@Override
	public String getType(Uri uri) {
		 switch (sUriMatcher.match(uri)) {
         case MAINTENANCE_LOG_TABLE_NO:
             return MaintenanceLogTable.CONTENT_TYPE;
         case FUELLING_LOG_TABLE_NO:
             return FuellingLogTable.CONTENT_TYPE;

         default:
             throw new IllegalArgumentException("Unknown URI " + uri);
     }
	}

    @Override
    public Uri insert(Uri uri, ContentValues values) {
       

        ContentValues value;
        if (values != null) {
            value = new ContentValues(values);
        } else {
            value = new ContentValues();
        }

        // Make sure that the fields are all set

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)){
        case MAINTENANCE_LOG_TABLE_NO:
        	try {
                long rowId = db.insert(MaintenanceLogTable.TABLE_NAME, MaintenanceLogTable.TIME, value);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(MaintenanceLogTable.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }

                // throw new SQLException("Failed to insert row into " + uri);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        	break;
        case FUELLING_LOG_TABLE_NO:
        	try {
                long rowId = db.insert(FuellingLogTable.TABLE_NAME, FuellingLogTable.TIME, value);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(FuellingLogTable.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }

                // throw new SQLException("Failed to insert row into " + uri);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
            }
        	break;
        	default:
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case MAINTENANCE_LOG_TABLE_NO:
                Log.e(TAG, "MAINTENANCE_LOG_TABLE_NO match!");
                count = db.delete(MaintenanceLogTable.TABLE_NAME, selection, selectionArgs);
                break;

            case MAINTENANCE_LOG_TABLE_ID:
                Log.e(TAG, "MAINTENANCE_LOG_TABLE_ID match!");
                String callLogId = uri.getPathSegments().get(1);
                count = db
                        .delete(MaintenanceLogTable.TABLE_NAME,
                        		MaintenanceLogTable._ID
                                        + "="
                                        + callLogId
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                                + ')' : ""), selectionArgs);
                break;
            case FUELLING_LOG_TABLE_NO:
            	count = db.delete(FuellingLogTable.TABLE_NAME, selection, selectionArgs);
            	
            	break;
            case FUELLING_LOG_TABLE_ID:
                count = db
                        .delete(FuellingLogTable.TABLE_NAME,
                        		FuellingLogTable._ID
                                        + "="
                                        + uri.getPathSegments().get(1)
                                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                                                + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

   

	@Override
	public boolean onCreate() {
		Log.e(TAG, "Database Create!");
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        // If no sort order is specified use the default
        String orderBy;
        switch (sUriMatcher.match(uri)) {
            case MAINTENANCE_LOG_TABLE_NO:
                qb.setProjectionMap(sMaintenanceLogTableProjectionMap);
                orderBy = MaintenanceLogTable.DEFAULT_SORT_ORDER;
                qb.setTables(MaintenanceLogTable.TABLE_NAME);
                break;

            case MAINTENANCE_LOG_TABLE_ID:
                qb.setProjectionMap(sMaintenanceLogTableProjectionMap);
                qb.appendWhere(MaintenanceLogTable._ID + "=" + uri.getPathSegments().get(1));
                orderBy = MaintenanceLogTable.DEFAULT_SORT_ORDER;
                qb.setTables(MaintenanceLogTable.TABLE_NAME);
                break;
            case FUELLING_LOG_TABLE_NO:
                qb.setProjectionMap(sFuellingLogTableProjectionMap);
                orderBy = FuellingLogTable.DEFAULT_SORT_ORDER;
                qb.setTables(FuellingLogTable.TABLE_NAME);
                break;

            case FUELLING_LOG_TABLE_ID:
                qb.setProjectionMap(sFuellingLogTableProjectionMap);
                qb.appendWhere(FuellingLogTable._ID + "=" + uri.getPathSegments().get(1));
                orderBy = FuellingLogTable.DEFAULT_SORT_ORDER;
                qb.setTables(FuellingLogTable.TABLE_NAME);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

       
        if (!TextUtils.isEmpty(sortOrder)) {
            orderBy = sortOrder;
        } 

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.e(TAG, "Database Create!");
			db.execSQL("CREATE TABLE " + MaintenanceLogTable.TABLE_NAME + " ("
					+ MaintenanceLogTable._ID
					+ " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "content TEXT," + "cost INT," + "time INTEGER NOT NULL"
					+ ");");
			
			db.execSQL("CREATE TABLE " + FuellingLogTable.TABLE_NAME + " ("
					+ FuellingLogTable._ID
					+ " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
					+ "content TEXT," + "cost FLOAT," + "isFull BOOLEAN,"  + "isAlert BOOLEAN," + "mileage FLOAT,"+ "price FLOAT," + "forgetLast BOOLEAN," +"gasType INT,"+"amount FLOAT,"
					+ "time INTEGER NOT NULL"
					+ ");");
			
			// db.execSQL("insert into [MaintenanceLogTable.TABLE_NAME] (appName,className,packageName,isSys,imagePath,backgroundImagePath,time,type) values('news', 'com.netease.newsreader.activity.MainIndexActivity', 'com.netease.newsreader.activity', 1, 'btn_src_news', 'app_item_bg1',"
			// + new Date().getTime() + ",1);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
	
	public SQLiteDatabase getDbHandle() {
        return mOpenHelper.getWritableDatabase();
    }

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY,
				MaintenanceLogTable.TABLE_NAME, MAINTENANCE_LOG_TABLE_NO);
		sUriMatcher.addURI(AUTHORITY,
				MaintenanceLogTable.TABLE_NAME + "/#", MAINTENANCE_LOG_TABLE_ID);

		sMaintenanceLogTableProjectionMap = new HashMap<String, String>();
		sMaintenanceLogTableProjectionMap.put(MaintenanceLogTable._ID,
				MaintenanceLogTable._ID);
		sMaintenanceLogTableProjectionMap.put(MaintenanceLogTable.TIME,
				MaintenanceLogTable.TIME);
		sMaintenanceLogTableProjectionMap.put(MaintenanceLogTable.COST,
				MaintenanceLogTable.COST);
		sMaintenanceLogTableProjectionMap.put(MaintenanceLogTable.CONTENT,
				MaintenanceLogTable.CONTENT);
		
		sUriMatcher.addURI(AUTHORITY,
				FuellingLogTable.TABLE_NAME, FUELLING_LOG_TABLE_NO);
		sUriMatcher.addURI(AUTHORITY,
				FuellingLogTable.TABLE_NAME + "/#", FUELLING_LOG_TABLE_ID);

		sFuellingLogTableProjectionMap = new HashMap<String, String>();
		sFuellingLogTableProjectionMap.put(FuellingLogTable._ID,
				FuellingLogTable._ID);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.TIME,
				FuellingLogTable.TIME);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.COST,
				FuellingLogTable.COST);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.CONTENT,
				FuellingLogTable.CONTENT);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.AMOUNT,
				FuellingLogTable.AMOUNT);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.FORGETLAST,
				FuellingLogTable.FORGETLAST);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.ISALERT,
				FuellingLogTable.ISALERT);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.GASTYPE,
				FuellingLogTable.GASTYPE);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.PRICE,
				FuellingLogTable.PRICE);
		sFuellingLogTableProjectionMap.put(FuellingLogTable.MILEAGE,
                FuellingLogTable.MILEAGE);
	}

}
