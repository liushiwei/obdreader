package com.george.obdreader.io;

import android.bluetooth.BluetoothDevice;

public interface ObdConnecter {
	 // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    public static final int STATE_FAILED = 4;  // now connected to a remote device
	public void start();
	public int getState();
	public void connect();
	public void stop();
	public void restart();
	public void write(byte[] out);
}
