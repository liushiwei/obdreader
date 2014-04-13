
package com.george.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.george.obdreader.Log;

public class Device {

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";

    private static final String TAG = "Device";

    public static String getDeviceId() {
        final String MMC_CID_PATH = "/sys/class/mmc_host/mmc0/mmc0:0001/cid";
        String id = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(
                    MMC_CID_PATH)));
            while ((id = br.readLine()) != null) {
                break;
            }
            br.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (id != null) {
            id = Integer.toHexString(id.hashCode()).toUpperCase();
        } else {
            id = "00000000";
        }
        Log.e(TAG, "DevideId = " + id);
        return id;
    }

    public static int getNetConnect(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        // State state = connManager.getActiveNetworkInfo().getState();
        NetworkInfo activeInfo = connManager.getActiveNetworkInfo();
        if (activeInfo == null) {
            Log.e(TAG, "网络已经断开");
            return -1;
        }

        if (activeInfo != null && activeInfo.isConnected()) {
            // wifi 连接状态
            if (activeInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                State state = connManager.getNetworkInfo(
                        ConnectivityManager.TYPE_WIFI).getState(); // 获取网络连接状态
                switch (state) {
                    case CONNECTED:

                        Log.e(TAG, "WIFI网络连接成功");
                        return ConnectivityManager.TYPE_WIFI;
                    case CONNECTING:
                        Log.e(TAG, "正在连接WIFI网络");
                        break;
                    case DISCONNECTED:
                        Log.e(TAG, "WIFI网络已经断开");
                        break;
                    case DISCONNECTING:
                        Log.e(TAG, "正在断开WIFI网络");
                        break;
                }

            }
            // mobile连接状态
            if (activeInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                State state = connManager.getNetworkInfo(
                        ConnectivityManager.TYPE_MOBILE).getState(); // 获取网络连接状态
                switch (state) {
                    case CONNECTED:
                        Log.e(TAG, "GPRS网络连接成功");
                        return ConnectivityManager.TYPE_MOBILE;
                    case CONNECTING:
                        Log.e(TAG, "正在连接GPRS网络");
                        break;
                    case DISCONNECTED:
                        Log.e(TAG, "GPRS网络已经断开");
                        break;
                    case DISCONNECTING:
                        Log.e(TAG, "正在断开GPRS网络");
                        break;
                }

            }
        }
        return -1;
    }

    /**
     * @param url
     * @param method
     * @return
     */
    public static String getHttpResponse(String url, String method) {
        String response = null;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                // 防止多行响应数据，变更读取方法
                char[] cbuf = new char[1024];
                StringBuilder sb = new StringBuilder();
                int n = reader.read(cbuf);
                while (n != -1) {
                    sb.append(cbuf, 0, n);
                    n = reader.read(cbuf);
                }
                // response = reader.readLine();
                response = sb.toString();
            } else {
                response = "{\"httpError\":" + responseCode + "}";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public String getHttpResponse(String url) {
        return getHttpResponse(url, HTTP_METHOD_GET);
    }

    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

}
