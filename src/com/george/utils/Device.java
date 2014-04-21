package com.george.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.george.obdreader.Log;

public class Device {

	static {
		System.loadLibrary("obdreader-jni");
	}

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
	 * @throws Exception
	 */
	public static String getHttpResponse(String url, String method)
			throws Exception {
		return openHttpClient(url, method, null);
		// String response = null;
		// HttpURLConnection conn = null;
		// BufferedReader reader = null;
		// try {
		// if(method.equals(HTTP_METHOD_GET)){
		//
		// conn = (HttpURLConnection) new URL(url).openConnection();
		// conn.setRequestMethod(method);
		// int responseCode = conn.getResponseCode();
		// if (responseCode == HttpURLConnection.HTTP_OK) {
		// reader = new BufferedReader(new InputStreamReader(
		// conn.getInputStream(), "UTF-8"));
		// // 防止多行响应数据，变更读取方法
		// char[] cbuf = new char[1024];
		// StringBuilder sb = new StringBuilder();
		// int n = reader.read(cbuf);
		// while (n != -1) {
		// sb.append(cbuf, 0, n);
		// n = reader.read(cbuf);
		// }
		// // response = reader.readLine();
		// response = sb.toString();
		// } else {
		// response = "{\"httpError\":" + responseCode + "}";
		// }
		// }else{
		// conn = (HttpURLConnection) new URL(url).openConnection();
		// conn.setRequestMethod(method);
		// //因为这个是post请求，需要设置为true
		// conn.setDoOutput(true);
		// conn.setDoInput(true);
		// //设置以POST方式
		// conn.setRequestMethod("POST");
		// //POST请求不能使用缓存
		// conn.setUseCaches(false);
		// conn.setInstanceFollowRedirects(true);
		//
		// //配置本次连接的Content_type,配置为application/x-www-form-urlencoded
		// conn.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// //连接，从postUrl.OpenConnection()至此的配置必须要在connect之前完成。
		// //要注意的是connection.getOutputStream会隐含地进行connect.
		// conn.connect();
		// //DataOutputStream流。
		// DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		// //要上传的参数
		// String content = "par=" + URLEncoder.encode("ABCDEF","gb2312");
		// //将要上传的内容写入流中
		// out.writeBytes(content);
		// //刷新、关闭
		// out.flush();
		// out.close();
		// //获取数据
		// reader = new BufferedReader(new
		// InputStreamReader(conn.getInputStream()));
		// String inputLine = null;
		//
		// //---///得到读取的内容(流)
		// //---InputStreamReader in = new
		// InputStreamReader(urlConn.getInputStream());
		// //---// 为输出创建BufferedReader
		// //---BufferedReader buffer = new BufferedReader(in);
		// //---String inputLine = null;
		// //---//使用循环来读取获得的数据
		// while (((inputLine = reader.readLine()) != null))
		// {
		// //我们在每一行后面加上一个"\n"来换行
		// response += inputLine + "\n";
		//
		// }
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (conn != null) {
		// conn.disconnect();
		// }
		// if (reader != null) {
		// try {
		// reader.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// return response;
	}

	public String getHttpResponse(String url) throws Exception {
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

	public static List<NameValuePair> encodeParameters(Parameters httpParams) {
		if (null == httpParams || isBundleEmpty(httpParams)) {
			return null;
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				httpParams.size());
		// StringBuilder buf = new StringBuilder();
		// int j = 0;
		// for (int loc = 0; loc < httpParams.size(); loc++) {
		// String key = httpParams.getKey(loc);
		// if (j != 0) {
		// buf.append("&");
		// }
		// try {
		// buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
		// .append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
		// } catch (java.io.UnsupportedEncodingException neverHappen) {
		// }
		// j++;
		// }
		for (int loc = 0; loc < httpParams.size(); loc++) {
			nameValuePairs.add(new BasicNameValuePair(httpParams.getKey(loc),
					httpParams.getValue(loc)));
		}
		return nameValuePairs;

	}

	public static boolean isBundleEmpty(Parameters bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}

	public static String openHttpClient(String url, String method,
			Parameters params) throws Exception {
		String response = null;
		HttpUriRequest request = null;
		if (method.equals(HTTP_METHOD_GET)) {
			url = url + encodeUrl(params);
			HttpGet get = new HttpGet(url);
			request = get;
		} else if (method.equals(HTTP_METHOD_POST)) {
			HttpPost post = new HttpPost(url);
			List<NameValuePair> nameValuePairs = encodeParameters(params);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			request = post;
		} else if (method.equals("DELETE")) {
			request = new HttpDelete(url);
		}
		HttpClient hc = new DefaultHttpClient();
		String readLine = null;

		HttpResponse ht = hc.execute(request);
		// 连接成功
		if (ht.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity het = ht.getEntity();
			InputStream is = het.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			response = "";

			while ((readLine = br.readLine()) != null) {
				response = response + readLine;
			}
			is.close();
			br.close();
		}
		if (response == null) {
			// Toast.makeText(mContext,R.string.download_file_not_found,
			// Toast.LENGTH_LONG).show();
		}
		return response;

	}

	public static String encodeUrl(Parameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		// boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			// if (first)
			// first = false;
			// else
			sb.append("&");
			sb.append(URLEncoder.encode(parameters.getKey(loc)) + "="
					+ URLEncoder.encode(parameters.getValue(loc)));
		}
		return sb.toString();
	}

	/**
	 * 加密（使用DES算法）
	 * 
	 * @param txt
	 *            需要加密的文本
	 * @param key
	 *            密钥
	 * @return 成功加密的文本
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String enCrypto(String txt, String key)
			throws InvalidKeySpecException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		StringBuffer sb = new StringBuffer();
		DESKeySpec desKeySpec = new DESKeySpec(key==null?jni_key.getBytes():key.getBytes());
		SecretKeyFactory skeyFactory = null;
		Cipher cipher = null;
		try {
			skeyFactory = SecretKeyFactory.getInstance("DES");
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecretKey deskey = skeyFactory.generateSecret(desKeySpec);
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] cipherText = cipher.doFinal(txt.getBytes());
		for (int n = 0; n < cipherText.length; n++) {
			String stmp = (java.lang.Integer.toHexString(cipherText[n] & 0XFF));

			if (stmp.length() == 1) {
				sb.append("0" + stmp);
			} else {
				sb.append(stmp);
			}
		}
		return sb.toString().toUpperCase();
	}
	private static String jni_key = keyFromJNI();
	/**
	 * 解密（使用DES算法）
	 * 
	 * @param txt
	 *            需要解密的文本
	 * @param key
	 *            密钥
	 * @return 成功解密的文本
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String deCrypto(String txt, String key)
			throws InvalidKeyException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {
		DESKeySpec desKeySpec = new DESKeySpec(key==null?jni_key.getBytes():key.getBytes());
		SecretKeyFactory skeyFactory = null;
		Cipher cipher = null;
		try {
			skeyFactory = SecretKeyFactory.getInstance("DES");
			cipher = Cipher.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		SecretKey deskey = skeyFactory.generateSecret(desKeySpec);
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		byte[] btxts = new byte[txt.length() / 2];
		for (int i = 0, count = txt.length(); i < count; i += 2) {
			btxts[i / 2] = (byte) Integer.parseInt(txt.substring(i, i + 2), 16);
		}
		return (new String(cipher.doFinal(btxts)));
	}

	public static native String keyFromJNI();

}
