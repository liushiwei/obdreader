package com.george.obdreader.config;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.widget.Toast;

import com.george.obdreader.Log;
import com.george.obdreader.MainActivity;
import com.george.obdreader.R;
import com.george.utils.Device;

public class AboutSoftwareSetting extends PreferenceFragment {

	public static final String CURRENT_VERSION = "current_version";

	public static final String AUTO_UPDATE = "auto_update";

	public static final String WIFI_UPDATE_ONLY = "wifi_update_only";

	private boolean wifi_update_only_value;

	public static final String SOFTWARE_UPDATE = "software_update";

	private String current_version_name;

	private String new_version_name;

	private ProgressDialog progressDialog;

	private long mDownloadId;
	private DownloadManager downloadManager;

	private static final String TAG = "AboutSoftwareSetting";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about_software);

		Preference current_version = findPreference(CURRENT_VERSION);
		current_version_name = Device.getAppVersionName(getActivity());
		current_version.setSummary(current_version_name);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		boolean auto_update_value = preferences.getBoolean(AUTO_UPDATE, true);
		CheckBoxPreference auto_update = (CheckBoxPreference) findPreference(AUTO_UPDATE);
		auto_update.setChecked(auto_update_value);
		wifi_update_only_value = preferences.getBoolean(WIFI_UPDATE_ONLY, true);
		CheckBoxPreference wifi_update_only = (CheckBoxPreference) findPreference(WIFI_UPDATE_ONLY);
		wifi_update_only.setChecked(wifi_update_only_value);
		Preference software_update = findPreference(SOFTWARE_UPDATE);
		software_update
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						int value = Device.getNetConnect(getActivity());
						if (value > -1) {
							if (wifi_update_only_value) {
								if (value == ConnectivityManager.TYPE_WIFI) {

									progressDialog = ProgressDialog
											.show(getActivity(),
													null,
													getString(R.string.checking_new_version),
													true, false);
									new Thread() {

										@Override
										public void run() {

											String responeString = Device
													.getHttpResponse(
															"http://obdreader.sinaapp.com/bin/update.txt",
															Device.HTTP_METHOD_GET);

											// 向handler发消息
											handler.obtainMessage(0,
													responeString)
													.sendToTarget();

										}
									}.start();
								} else {
									handler.sendEmptyMessage(1);
								}
							} else {
								progressDialog = ProgressDialog
										.show(getActivity(),
												null,
												getString(R.string.checking_new_version),
												true, false);
								new Thread() {

									@Override
									public void run() {

										String responeString = Device
												.getHttpResponse(
														"http://obdreader.sinaapp.com/bin/update.txt",
														Device.HTTP_METHOD_GET);

										// 向handler发消息
										handler.obtainMessage(0, responeString)
												.sendToTarget();

									}
								}.start();

							}
						} else {
							handler.sendEmptyMessage(2);

						}
						return false;
					}
				});
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				String respone = (String) msg.obj;
				if (respone != null) {
					Log.e(TAG, "respone = " + respone);
					String version = respone
							.substring(0, respone.indexOf('\n'));
					new_version_name = version.trim().substring(
							version.indexOf(':') + 1, version.length());
					Log.e(TAG, current_version_name.compareTo(new_version_name)
							+ "");
					String messageString = "";
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage(messageString);
					if (current_version_name.compareTo(new_version_name) == 0) {
						builder.setMessage(getString(R.string.no_new_verion));
						builder.setPositiveButton(android.R.string.ok, null);
					} else {
						builder.setTitle(String.format(
								getString(R.string.download_new_version),
								new_version_name));
						builder.setMessage(respone);
						builder.setPositiveButton(android.R.string.cancel, null);
						builder.setNegativeButton(android.R.string.ok,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downloadManager = (DownloadManager) getActivity()
												.getSystemService(
														getActivity().DOWNLOAD_SERVICE);
										Request request = new Request(
												Uri.parse("http://obdreader.sinaapp.com/getapp.php"));

										request.setAllowedNetworkTypes(
												DownloadManager.Request.NETWORK_MOBILE
														| DownloadManager.Request.NETWORK_WIFI)
												.setAllowedOverRoaming(false) // 缺省是true
												.setTitle("ObdReader.apk")
												// 用于信息查看
												.setDescription(
														String.format(
																getString(R.string.new_version),
																new_version_name)) // 用于信息查看
												.setDestinationInExternalPublicDir(
														Environment.DIRECTORY_DOWNLOADS,
														"ObdReader"
																+ new_version_name
																		.replace(
																				'.',
																				'_')
																+ ".apk");
										mDownloadId = downloadManager
												.enqueue(request); // 加入下载队列
										startQuery(mDownloadId);

									}
								});
					}

					builder.create().show();
				}
				progressDialog.dismiss();

				break;
			case 1:
				Toast.makeText(getActivity(),
						R.string.current_connect_not_wifi, Toast.LENGTH_SHORT)
						.show();
				break;
			case 2:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setMessage(R.string.net_uncon);
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								startActivity(new Intent(
										"android.settings.WIFI_SETTINGS"));
							}
						});

				builder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						});
				builder.create().show();

				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onResume() {
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		getActivity().registerReceiver(receiver, filter);

		IntentFilter filter22 = new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED);
		getActivity().registerReceiver(receiver2, filter22);

		super.onResume();
	}

	private void startQuery(long downloadId) {
		if (downloadId != 0) {
			runnable.DownID = downloadId;
			handler.postDelayed(runnable, step);
		}

	};

	private void stopQuery() {
		handler.removeCallbacks(runnable);
	}

	private void queryState(long downID) {
		// 关键：通过ID向下载管理查询下载情况，返回一个cursor
		Cursor c = downloadManager.query(new DownloadManager.Query()
				.setFilterById(downID));
		if (c == null) {
			Toast.makeText(getActivity(), "Download not found!",
					Toast.LENGTH_LONG).show();
		} else { // 以下是从游标中进行信息提取
			if (!c.moveToFirst()) {
				c.close();
				return;
			}
			Log.d(TAG,
					"Column_id : "
							+ c.getLong(c
									.getColumnIndex(DownloadManager.COLUMN_ID)));
			Log.d(TAG,
					"Column_bytes_downloaded so far : "
							+ c.getLong(c
									.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
			Log.d(TAG,
					"Column last modified timestamp : "
							+ c.getLong(c
									.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
			Log.d(TAG,
					"Column local uri : "
							+ c.getString(c
									.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
			Log.d(TAG,
					"Column statue : "
							+ c.getInt(c
									.getColumnIndex(DownloadManager.COLUMN_STATUS)));
			Log.d(TAG,
					"Column reason : "
							+ c.getInt(c
									.getColumnIndex(DownloadManager.COLUMN_REASON)));

			int st = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
			// Toast.makeText(getActivity(), statusMessage(st),
			// Toast.LENGTH_LONG).show();
			// Log.i(TAG, statusMessage(st));

			c.close();
		}
	}

	private String statusMessage(int st) {
		switch (st) {
		case DownloadManager.STATUS_FAILED:
			return "Download failed";
		case DownloadManager.STATUS_PAUSED:
			return "Download paused";
		case DownloadManager.STATUS_PENDING:
			return "Download pending";
		case DownloadManager.STATUS_RUNNING:
			return "Download in progress!";
		case DownloadManager.STATUS_SUCCESSFUL:
			return "Download finished";
		default:
			return "Unknown Information";
		}
	}

	// 监听下载结束，启用BroadcastReceiver
	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			DownloadManager dm = (DownloadManager) getActivity()
					.getSystemService(getActivity().DOWNLOAD_SERVICE);
			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				// 查询
				Query query = new Query();
				query.setFilterById(downloadId);
				Cursor c = dm.query(query);
				if (c.moveToFirst()) {
					int columnIndex = c
							.getColumnIndex(DownloadManager.COLUMN_STATUS);
					if (DownloadManager.STATUS_SUCCESSFUL == c
							.getInt(columnIndex)) {

						String uriString = c
								.getString(c
										.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

						// removeDownload(downloadId);
						// Toast.makeText(getActivity(),
						// "get file complete: " + uriString, 0).show();
						Intent install_intent = new Intent();
						install_intent.setAction(Intent.ACTION_VIEW);
						install_intent.setDataAndType(Uri.parse(uriString),
								"application/vnd.android.package-archive");
						getActivity().startActivity(install_intent);
						// Uri.parse(uriString);
					}
				}// endif

			}// endif

		}// onReceive
	};// end class receiver

	// 监听下载结束，启用BroadcastReceiver
	BroadcastReceiver receiver2 = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);

			String action = intent.getAction();
			if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
				lookDownload();
			}
		}
	};//

	public void lookDownload() {
		startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
	}

	private int removeDownload(long downloadId) {
		return downloadManager.remove(downloadId);
	}

	int step = 1000;
	QueryRunnable runnable = new QueryRunnable();

	class QueryRunnable implements Runnable {
		public long DownID;

		@Override
		public void run() {
			queryState(DownID);
			handler.postDelayed(runnable, step);
		}
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(receiver);
		getActivity().unregisterReceiver(receiver2);
		super.onDestroy();
	};

}
