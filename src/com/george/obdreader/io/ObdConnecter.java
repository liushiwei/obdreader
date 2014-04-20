package com.george.obdreader.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.george.obdreader.config.BaseSetting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;


public abstract class ObdConnecter {
	 // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int STATE_FAILED = 4;  // now connected to a remote device
	public abstract void start();
	public abstract int getState();
	public abstract void connect();
	public abstract void stop();
	public abstract void restart();
	public abstract InputStream getInputStream();
	public abstract OutputStream getOutputStream();
	public abstract void write(byte[] out);
	public static WiFiService wiFiService;
	public static BluetoothService bluetoothService;
	public static ObdConnecter getConnecter(Context context ,Handler handler){
	    SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
	    String mConnect_type = preferences.getString(BaseSetting.CONNECT_TYPE,
                null);
	    if (mConnect_type == null || Integer.valueOf(mConnect_type) == 0) {
	        if(wiFiService==null)
	        return new WiFiService(context,handler);
	        else {
                return wiFiService;
            }
	    }else {
	        if(bluetoothService==null)
	            return new BluetoothService(context,handler);
	            else {
	                return bluetoothService;
	            }
        }
	    
	}
}
