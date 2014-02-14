/*
 * TODO put header
 */
package com.george.obdreader.io;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Service connection for ObdGatewayService.
 */
public class ObdGatewayServiceConnection implements ServiceConnection {

	private static final String TAG = "ObdGatewayServiceConnection";

	private IPostMonitor _service = null;
	private IPostListener _listener = null;
	private boolean isConnected;

	public void onServiceConnected(ComponentName name, IBinder binder) {
		isConnected = true;
		Log.d(TAG, "Service is Connected.");
		_service = (IPostMonitor) binder;
		_service.setListener(_listener);
		_service.connectDevice();
	}

	public void onServiceDisconnected(ComponentName name) {
		isConnected = false;
		_service = null;
		Log.d(TAG, "Service is disconnected.");
	}

	/**
	 * @return true if service is running, false otherwise.
	 */
	public boolean isRunning() {
		if (_service == null) {
			return false;
		}

		return _service.isRunning();
	}

	/**
	 * Queue JobObdCommand.
	 * 
	 * @param the
	 *            job
	 */
	public long addJobToQueue(ObdCommandJob job) {
		if (null != _service)
			return _service.addJobToQueue(job);
		else
			return -1;
	}
	
	public boolean removeJobFromQueue(ObdCommandJob job) {
		if (null != _service)
			return _service.removeJobFromQueue(job);
		else
			return false;
	}

	/**
	 * Sets a callback in the service.
	 * 
	 * @param listener
	 */
	public void setServiceListener(IPostListener listener) {
		_listener = listener;
		if (null != _service){
			_service.setListener(_listener);
		}
	}
	
	public boolean isConnected(){
		return isConnected;
	}
	
	public void clearQueue(){
		if (null != _service)
			_service.clearQueue();
	}
	
	public void connectDevice(){
		if (null != _service)
			_service.connectDevice();
	}

}