package com.george.obdreader.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Databasehelper extends SQLiteOpenHelper {

	private SQLiteDatabase myDataBase;
    private final Context mContext;
    private static final String DATABASE_NAME = "OBDDef";
    public static final int DATABASE_VERSION = 1;
    //public static final int DATABASE_VERSION_old = 1;

    //Constructor
    public Databasehelper(Context context)
    {
          super(context, DATABASE_NAME, null, DATABASE_VERSION);
          this.mContext = context;
    }

    //Create a empty database on the system
    public void createDatabase() throws IOException
    {

          boolean dbExist = checkDataBase();

          if(dbExist)
          {
                Log.v("DB Exists", "db exists");
                // By calling this method here onUpgrade will be called on a
                // writeable database, but only if the version number has been
                // bumped
                //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
          }
         
          boolean dbExist1 = checkDataBase();
          if(!dbExist1)
          {
                this.getReadableDatabase();
                try
                {
                      this.close();    
                      copyDataBase();
                }
                catch (IOException e)
                {
                      throw new Error("Error copying database");
                }
          }

    }

    //Check database already exist or not
    private boolean checkDataBase()
    {
          boolean checkDB = false;
          try
          {
                File dbfile = mContext.getDatabasePath(DATABASE_NAME);
                checkDB = dbfile.exists();
          }
          catch(SQLiteException e)
          {
          }
          return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException
    {



          OutputStream myOutput = new FileOutputStream(mContext.getDatabasePath(DATABASE_NAME));
          InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

          byte[] buffer = new byte[1024];
          int length;
          while ((length = myInput.read(buffer)) > 0)
          {
                myOutput.write(buffer, 0, length);
          }
          myInput.close();
          myOutput.flush();
          myOutput.close();
    }

    //delete database
    public void db_delete()
    {
          File file = mContext.getDatabasePath(DATABASE_NAME);
          if(file.exists())
          {
                file.delete();
                System.out.println("delete database file.");
          }
    }

    //Open database
    public void openDatabase() throws SQLException
    {
          myDataBase = SQLiteDatabase.openDatabase(mContext.getDatabasePath(DATABASE_NAME).getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
          if(myDataBase != null)
                myDataBase.close();
          super.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {    
          if (newVersion > oldVersion)
          {
                Log.v("Database Upgrade", "Database version higher than old.");
                db_delete();
          }
    }
   

//add your public methods for insert, get, delete and update data in database.
}
