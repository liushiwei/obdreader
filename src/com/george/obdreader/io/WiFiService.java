package com.george.obdreader.io;

import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class WiFiService implements ObdConnecter {
	
	// Debugging
    private static final String TAG = "WiFiService";
    private static final boolean D = true;
	
	private final Handler mHandler;
    private AcceptThread mAcceptThread;
//    private ConnectThread mConnectThread;
//    private ConnectedThread mConnectedThread;
    private int mState;
    private Context mContext;
    
    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public WiFiService(Context context, Handler handler) {
        mState = STATE_NONE;
        mHandler = handler;
        mContext = context;
    }


	@Override
	public void start() {
		if (D) Log.d(TAG, "start");

//        // Cancel any thread attempting to make a connection
//        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
//
//        // Cancel any thread currently running a connection
//        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);

	}
	
	/**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket

        public AcceptThread() {
           
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
               

//                // If a connection was accepted
//                if (socket != null) {
//                    synchronized (WiFiService.this) {
//                        switch (mState) {
//                        case STATE_LISTEN:
//                        case STATE_CONNECTING:
//                            // Situation normal. Start the connected thread.
//                            connected(socket, socket.getRemoteDevice());
//                            break;
//                        case STATE_NONE:
//                        case STATE_CONNECTED:
//                            // Either not ready or already connected. Terminate new socket.
//                            try {
//                                socket.close();
//                            } catch (IOException e) {
//                                Log.e(TAG, "Could not close unwanted socket", e);
//                            }
//                            break;
//                        }
//                    }
//                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        
    }

	 /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
//        mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

	@Override
	public void connect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void restart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(byte[] out) {
		// TODO Auto-generated method stub

	}


	/**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

}
