package com.george.obdreader.db;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.george.obdreader.Log;
import com.george.obdreader.R;
import com.george.utils.Device;

public class DBOperation extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_operation);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Thread(dbQueryThread).start();
				
			}
		});
	}
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar1);
			progress.setProgress(msg.arg1);
		}
		
	};

	protected String TAG = "DBOperation";

	Runnable dbQueryThread = new Runnable() {

		
		@Override
		public void run() {
			try {
				Databasehelper dbHelper = new Databasehelper(DBOperation.this);
				dbHelper.createDatabase();
				dbHelper.openDatabase();
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				for(int i=0;i<5557;i++){
					Cursor cursor = db.query("OBDDef", new String[] {
							"EN_Def", "CN_Def", "Category", "Knowledge" },
							"id='" + i + "'", null, null, null, null);
					int col = cursor.getColumnIndex("EN_Def");
					Log.e(TAG, "size = " + cursor.getCount());
					Log.e(TAG, "col = " + col);
					if(cursor.getCount()==0)
						continue;
					cursor.moveToFirst();
					String CN_Def = cursor.getString(cursor.getColumnIndex("CN_Def"));
					String EN_Def =cursor.getString(cursor.getColumnIndex("EN_Def"));
					String Category = cursor.getString(cursor.getColumnIndex("Category"));
					String Knowledge = cursor.getString(cursor
							.getColumnIndex("Knowledge"));
					cursor.close();
					ContentValues cv = new ContentValues();  
			        try {
						cv.put("CN_Def", Device.enCrypto(CN_Def, null));
						cv.put("EN_Def", Device.enCrypto(EN_Def, null));  
				        cv.put("Category", Device.enCrypto(Category, null));  
				        if(Knowledge!=null&&Knowledge.trim().length()>0)
				        cv.put("Knowledge", Device.enCrypto(Knowledge, null));  
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
			        
			        String[] args = {String.valueOf(i)}; 
					db.update("OBDDef", cv, "id=?", args);
					mHandler.obtainMessage(0, i, 0).sendToTarget();
				}
				db.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

}
