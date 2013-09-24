/*
 * TODO put header
 */
package com.george.obd.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.george.obd.ConfigActivity;
import com.george.obd.IPostListener;
import com.george.obd.IPostMonitor;
import com.george.obd.MainActivity;
import com.george.obd.R;
import com.george.obd.io.ObdCommandJob.ObdCommandJobState;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.commands.protocol.EchoOffObdCommand;
import eu.lighthouselabs.obd.commands.protocol.LineFeedOffObdCommand;
import eu.lighthouselabs.obd.commands.protocol.MemoryOffObdCommand;
import eu.lighthouselabs.obd.commands.protocol.ObdResetCommand;
import eu.lighthouselabs.obd.commands.protocol.SelectProtocolObdCommand;
import eu.lighthouselabs.obd.commands.protocol.SimpleObdCommand;
import eu.lighthouselabs.obd.commands.protocol.TimeoutObdCommand;
import eu.lighthouselabs.obd.commands.temperature.AmbientAirTemperatureObdCommand;
import eu.lighthouselabs.obd.enums.ObdProtocols;

/**
 * This service is primarily responsible for establishing and maintaining a
 * permanent connection between the device where the application runs and a more
 * OBD Bluetooth interface.
 * 
 * Secondarily, it will serve as a repository of ObdCommandJobs and at the same
 * time the application state-machine.
 */
public class ObdGatewayService extends Service {

	private static final String TAG = "ObdGatewayService";

	private IPostListener _callback = null;
	private final Binder _binder = new LocalBinder();
	private AtomicBoolean _isRunning = new AtomicBoolean(false);
	private NotificationManager _notifManager;

	private BlockingQueue<ObdCommandJob> _queue = new LinkedBlockingQueue<ObdCommandJob>();
	private AtomicBoolean _isQueueRunning = new AtomicBoolean(false);
	private Long _queueCounter = 0L;

	private BluetoothDevice _dev = null;
	private BluetoothSocket _sock = null;
	
	private static final int SERVERPORT = 35000;
	//private static final String SERVER_IP = "192.168.43.1";
	private static final String SERVER_IP = "192.168.0.10";
	private Socket socket;
	/*
	 * http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
	 * #createRfcommSocketToServiceRecord(java.util.UUID)
	 * 
	 * "Hint: If you are connecting to a Bluetooth serial board then try using
	 * the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB. However if
	 * you are connecting to an Android peer then please generate your own
	 * unique UUID."
	 */
	private static final UUID MY_UUID = UUID
	        .fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private Thread ioThread ;

	/**
	 * As long as the service is bound to another component, say an Activity, it
	 * will remain alive.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return _binder;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		_notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		ioThread = new Thread(new ClientThread());
		ioThread.start();
	}

	@Override
	public void onDestroy() {
		stopService();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Received start id " + startId + ": " + intent);

		/*
		 * Register listener Start OBD connection
		 */
		startService();

		/*
		 * We want this service to continue running until it is explicitly
		 * stopped, so return sticky.
		 */
		return START_STICKY;
	}

	private void startService() {
		
		Log.d(TAG, "Starting service..");

		/*
		 * Retrieve preferences
		 */
		SharedPreferences prefs = PreferenceManager
		        .getDefaultSharedPreferences(this);

//		/*
//		 * Let's get the remote Bluetooth device
//		 */
//		String remoteDevice = prefs.getString(
//		        ConfigActivity.BLUETOOTH_LIST_KEY, null);
//		if (remoteDevice == null || "".equals(remoteDevice)) {
//			Toast.makeText(this, "No Bluetooth device selected",
//			        Toast.LENGTH_LONG).show();
//
//			// log error
//			Log.e(TAG, "No Bluetooth device has been selected.");
//
//			// TODO kill this service gracefully
//			stopService();
//		}
//
//		final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//		_dev = btAdapter.getRemoteDevice(remoteDevice);

		/*
		 * TODO put this as deprecated Determine if upload is enabled
		 */
		// boolean uploadEnabled = prefs.getBoolean(
		// ConfigActivity.UPLOAD_DATA_KEY, false);
		// String uploadUrl = null;
		// if (uploadEnabled) {
		// uploadUrl = prefs.getString(ConfigActivity.UPLOAD_URL_KEY,
		// null);
		// }

		/*
		 * Get GPS
		 */
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean gps = prefs.getBoolean(ConfigActivity.ENABLE_GPS_KEY, false);

		/*
		 * TODO clean
		 * 
		 * Get more preferences
		 */
		int period = ConfigActivity.getUpdatePeriod(prefs);
		double ve = ConfigActivity.getVolumetricEfficieny(prefs);
		double ed = ConfigActivity.getEngineDisplacement(prefs);
		boolean imperialUnits = prefs.getBoolean(
		        ConfigActivity.IMPERIAL_UNITS_KEY, false);
		ArrayList<ObdCommand> cmds = ConfigActivity.getObdCommands(prefs);

		/*
		 * Establish Bluetooth connection
		 * 
		 * Because discovery is a heavyweight procedure for the Bluetooth
		 * adapter, this method should always be called before attempting to
		 * connect to a remote device with connect(). Discovery is not managed
		 * by the Activity, but is run as a system service, so an application
		 * should always call cancel discovery even if it did not directly
		 * request a discovery, just to be sure. If Bluetooth state is not
		 * STATE_ON, this API will return false.
		 * 
		 * see
		 * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
		 * .html#cancelDiscovery()
		 */
//		Log.d(TAG, "Stopping Bluetooth discovery.");
//		btAdapter.cancelDiscovery();

		Toast.makeText(this, "Starting OBD connection..", Toast.LENGTH_SHORT);

//		try {
//			startObdConnection();
//		} catch (Exception e) {
//			Log.e(TAG, "There was an error while establishing connection. -> "
//			        + e.getMessage());
//
//			// in case of failure, stop this service.
//			stopService();
//		}
	}

	/**
	 * Start and configure the connection to the OBD interface.
	 * 
	 * @throws IOException
	 */
	private void startObdConnection() throws IOException {
		Log.d(TAG, "Starting OBD connection..");

		// Instantiate a BluetoothSocket for the remote device and connect it.
//		_sock = _dev.createRfcommSocketToServiceRecord(MY_UUID);
//		_sock.connect();
		
//		InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
//
//		socket = new Socket(serverAddr, SERVERPORT);
		

		// Let's configure the connection.
		Log.d(TAG, "Queing jobs for connection configuration..");
		queueJob(new ObdCommandJob(new ObdResetCommand())); //ATZ
		queueJob(new ObdCommandJob(new EchoOffObdCommand()));//ATE0

		/*
		 * Will send second-time based on tests.
		 * 
		 * TODO this can be done w/o having to queue jobs by just issuing
		 * command.run(), command.getResult() and validate the result.
		 */
		queueJob(new ObdCommandJob(new EchoOffObdCommand()));//ATE0
		queueJob(new ObdCommandJob(new MemoryOffObdCommand()));//ATM0
		queueJob(new ObdCommandJob(new LineFeedOffObdCommand()));//ATL0
		queueJob(new ObdCommandJob(new SimpleObdCommand("AT S0","Space Off")));//ATS0
		queueJob(new ObdCommandJob(new SimpleObdCommand("AT @1","Display Device Description")));//ATS0
		queueJob(new ObdCommandJob(new SimpleObdCommand("AT I","Version ID")));//ATS0
		queueJob(new ObdCommandJob(new SimpleObdCommand("AT H0","Headers off")));//ATS0
		queueJob(new ObdCommandJob(new SimpleObdCommand("AT AT0","Adaptive Timing off")));//ATS0
		queueJob(new ObdCommandJob(new TimeoutObdCommand(62)));
		
//		queueJob(new ObdCommandJob(new EchoOffObdCommand()));
//		queueJob(new ObdCommandJob(new LineFeedOffObdCommand()));
//		queueJob(new ObdCommandJob(new TimeoutObdCommand(62)));

		// For now set protocol to AUTO
		queueJob(new ObdCommandJob(new SelectProtocolObdCommand(
		        ObdProtocols.AUTO)));
		// Job for returning dummy data
		queueJob(new ObdCommandJob(new AmbientAirTemperatureObdCommand()));

		Log.d(TAG, "Initialization jobs queued.");

		// Service is running..
		_isRunning.set(true);

		// Set queue execution counter
		_queueCounter = 0L;
	}

	/**
	 * Runs the queue until the service is stopped
	 */
	private void _executeQueue() {
		//Log.d(TAG, "Executing queue..");

		_isQueueRunning.set(true);

		Iterator<ObdCommandJob> iterator =_queue.iterator();
		while (iterator.hasNext()) {
			ObdCommandJob job = null;
			try {
				job = iterator.next();

				// log job
				//Log.d(TAG, "Taking job[" + job.getId() + "]  priority ="+job.getCommand().getPriority());

				if (job.getState().equals(ObdCommandJobState.NEW)) {
					//Log.d(TAG, "Job state is NEW. Run it..");
					if(socket!=null){
					job.setState(ObdCommandJobState.RUNNING);
					job.getCommand().run(socket.getInputStream(),
							socket.getOutputStream());
					}else{
						Log.e(TAG,"Socket is Null");
					}
				} else {
					// log not new job
					Log.e(TAG,
					        "Job state was not new, so it shouldn't be in queue. BUG ALERT!");
				}
				
			} catch (Exception e) {
				job.setState(ObdCommandJobState.EXECUTION_ERROR);
				Log.e(TAG, "Failed to run command. -> " + e.getMessage());
			}

			if (job != null) {
				//Log.d(TAG, "Job is finished.");
				job.setState(ObdCommandJobState.FINISHED);
				if(_callback!=null)
				_callback.stateUpdate(job);
			}
			if(job.getCommand().getPriority()>0){
				if(job.getCommand().getPriority()==1){
					job.setState(ObdCommandJobState.NEW);
				}else{
				if(job.getCommand().getPriority()>1&&job.getCommand().getCurrentPriority()>0){
					job.getCommand().setCurrentPriority(job.getCommand().getCurrentPriority()-1);
					job.setState(ObdCommandJobState.NEW);
				}else if(job.getCommand().getPriority()>1){
					_queue.remove(job);
				}
				}
				
				
			}else{
				Log.e(TAG,"remove command="+job.getCommand().getName());
				_queue.remove(job);
			}
			
		}

		_isQueueRunning.set(false);
	}

	/**
	 * This method will add a job to the queue while setting its ID to the
	 * internal queue counter.
	 * 
	 * @param job
	 * @return
	 */
	public Long queueJob(ObdCommandJob job) {
		_queueCounter++;
		Log.d(TAG, "Adding job[" + _queueCounter + "] to queue..");

		job.setId(_queueCounter);
		try {
			_queue.put(job);
		} catch (InterruptedException e) {
			job.setState(ObdCommandJobState.QUEUE_ERROR);
			// log error
			Log.e(TAG, "Failed to queue job.");
		}

		Log.d(TAG, "Job queued successfully.");
		return _queueCounter;
	}

	/**
	 * Stop OBD connection and queue processing.
	 */
	public void stopService() {
		Log.d(TAG, "Stopping service..");

		clearNotification();
		_queue.removeAll(_queue); // TODO is this safe?
		_isQueueRunning.set(false);
		_callback = null;
		_isRunning.set(false);

		// close socket
		try {
			//_sock.close();
			if(socket!=null){
				Log.d(TAG, "Socket close");
				socket.shutdownInput();         //需要调此方法，不然mReader.readLine还傻傻挂着。
				socket.shutdownOutput();
				socket.close();
			}
			ioThread.interrupt();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		// kill service
		stopSelf();
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
		        getText(R.string.service_started), System.currentTimeMillis());

		// Launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		        new Intent(this, MainActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this,
		        getText(R.string.notification_label),
		        getText(R.string.service_started), contentIntent);

		// Send the notification.
		_notifManager.notify(R.string.service_started, notification);
	}

	/**
	 * Clear notification.
	 */
	private void clearNotification() {
		_notifManager.cancel(R.string.service_started);
	}

	/**
	 * TODO put description
	 */
	public class LocalBinder extends Binder implements IPostMonitor {
		public void setListener(IPostListener callback) {
			_callback = callback;
		}

		public boolean isRunning() {
			return _isRunning.get();
		}

		public void executeQueue() {
			_executeQueue();
		}

		public void addJobToQueue(ObdCommandJob job) {
			Log.d(TAG, "Adding job [" + job.getCommand().getName() + "] to queue.");
			_queue.add(job);

//			if (!_isQueueRunning.get())
//				_executeQueue();
		}
	}
	

	class ClientThread implements Runnable {

		@Override
		public void run() {
			
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

				socket = new Socket(serverAddr, SERVERPORT);
				Log.d(TAG, "Create socket");
//				mReceiveThread = new ReceiveThread(socket);
//	                stop = false;
//	                //开启线程
//	            mReceiveThread.start();
					startObdConnection();
					while(_isRunning.get()){
						_executeQueue();
					}
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}
	

}