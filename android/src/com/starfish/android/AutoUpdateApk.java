//
//	Copyright (c) 2012 lenik terenin
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//		http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.

package com.starfish.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.starfish.game.android.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

public class AutoUpdateApk extends Observable {
	protected final static String TAG = AutoUpdateApk.class.getSimpleName();
	private final static String ANDROID_PACKAGE = "application/vnd.android.package-archive";
	private final static String API_URL = "http://haixing.sinaapp.com";

	protected static Context context = null;
	private static int appIcon = android.R.drawable.ic_popup_reminder;
	private static int versionCode = 0; // as low as it gets
	private static String packageName;
	private static String appName;
	private static int device_id;

	private static int NOTIFICATION_ID = 0xBEEF;
	private boolean commReady;
	private AsyncTask<Void, Void, String[]> checkTask = null;
	private static String newApkUrl;
	private static String downloadPath;

	public AutoUpdateApk(Context ctx) {
		commReady = false;
		setupVariables(ctx);
	}

	public void start() {
		if (commReady) {
			checkTask = new checkUpdateTask().execute();
		}
	}

	public void stop() {
		if (null != checkTask && !checkTask.isCancelled()) {
			checkTask.cancel(true);
		}
	}

	// To set commReady, if network is ready, set to true
	private void checkNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED == state) {
			Log.i(TAG, "WIFI is ready");
			commReady = true;
		} else {
			Log.i(TAG, "WIFI is not ready");
			commReady = false;
		}
	}

	private void setupVariables(Context ctx) {
		context = ctx;

		packageName = context.getPackageName();
		device_id = crc32(Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID));
		NOTIFICATION_ID += crc32(packageName);

		ApplicationInfo appinfo = context.getApplicationInfo();
		if (appinfo.icon != 0) {
			appIcon = appinfo.icon;
		} else {
			Log.d(TAG, "unable to find application icon");
		}
		if (appinfo.labelRes != 0) {
			appName = context.getString(appinfo.labelRes);
		} else {
			Log.d(TAG, "unable to find application label");
		}

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			downloadPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/tmp/";
			File f = new File(downloadPath);
			if (!f.exists()) {
				f.mkdirs();
			}
		}
		if (haveInternetPermissions()) {
			checkNetwork();
		}
	}

	private class checkUpdateTask extends AsyncTask<Void, Void, String[]> {
		private DefaultHttpClient httpclient = new DefaultHttpClient();
		private HttpPost post = new HttpPost(API_URL);

		protected String[] doInBackground(Void... v) {
			long start = System.currentTimeMillis();

			HttpParams httpParameters = new BasicHttpParams();
			// set the timeout in milliseconds until a connection is
			// established
			// the default value is zero, that means the timeout is not used
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// set the default socket timeout (SO_TIMEOUT) in milliseconds
			// which is the timeout for waiting for data
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			httpclient.setParams(httpParameters);

			try {
				StringEntity params = new StringEntity("pkgname=" + packageName
						+ "&version=" + versionCode + "&id="
						+ String.format("%08x", device_id));
				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				post.setEntity(params);

				String response = EntityUtils.toString(httpclient.execute(post)
						.getEntity(), "UTF-8");

				String[] result = response.split("\n");
				if (result.length > 1
						&& result[0].equalsIgnoreCase("have update")) {
					// Notify have update
					newApkUrl = result[1];
					buildNotification();
				} else {
					Log.d(TAG, "no update available");
				}
				return result;
			} catch (ParseException e) {
				// e.printStackTrace();
				Log.e(TAG, e.getMessage());
			} catch (ClientProtocolException e) {
				// e.printStackTrace();
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				httpclient.getConnectionManager().shutdown();
				long elapsed = System.currentTimeMillis() - start;
				Log.d(TAG, "update check finished in " + elapsed + "ms");
			}

			return null;
		}

		protected void onPreExecute() {
		}

		protected void onPostExecute(String[] result) {

		}
	}

	private boolean haveInternetPermissions() {
		Set<String> required_perms = new HashSet<String>();
		required_perms.add("android.permission.INTERNET");
		required_perms.add("android.permission.ACCESS_WIFI_STATE");
		required_perms.add("android.permission.ACCESS_NETWORK_STATE");

		PackageManager pm = context.getPackageManager();
		String packageName = context.getPackageName();
		int flags = PackageManager.GET_PERMISSIONS;
		PackageInfo packageInfo = null;

		try {
			packageInfo = pm.getPackageInfo(packageName, flags);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		if (packageInfo.requestedPermissions != null) {
			for (String p : packageInfo.requestedPermissions) {
				Log.v(TAG, "permission: " + p.toString());
				required_perms.remove(p);
			}
			if (required_perms.size() == 0) {
				Log.v(TAG, "have all permission");
				return true; // permissions are in order
			}
			// something is missing
			for (String p : required_perms) {
				Log.e(TAG, "required permission missing: " + p);
			}
		}
		Log.e(TAG,
				"INTERNET/WIFI access required, but no permissions are found in Manifest.xml");
		return false;
	}

	private static int crc32(String str) {
		byte bytes[] = str.getBytes();
		Checksum checksum = new CRC32();
		checksum.update(bytes, 0, bytes.length);
		return (int) checksum.getValue();
	}

	private void buildNotification() {
		Notification.Builder mBuilder = new Notification.Builder(context);
		mBuilder.setSmallIcon(appIcon);
		mBuilder.setContentTitle(context.getString(R.string.noi_title));
		mBuilder.setContentText(context.getString(R.string.noi_click));
		mBuilder.setAutoCancel(true);
		Intent intent = new Intent(context, Download.class);
		PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
		mBuilder.setContentIntent(pIntent);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
		nm.notify(NOTIFICATION_ID, mBuilder.build());
	}

	static public class Download extends Service {

		private NotificationManager nm;
		private Notification.Builder mBuilder;
		private Thread downloadThread;

		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void onCreate() {
			Log.d(TAG, "Download service create");
			super.onCreate();

			// Update Notification
			mBuilder = new Notification.Builder(context);
			mBuilder.setSmallIcon(appIcon);
			mBuilder.setContentTitle(context.getString(R.string.noi_title));
			mBuilder.setContentText(context.getString(R.string.noi_dl_content));
			mBuilder.setProgress(0, 0, true);
			mBuilder.setAutoCancel(true);
			Intent intent = new Intent(context, Download.class);
			PendingIntent pIntent = PendingIntent.getService(context, 0,
					intent, 0);
			mBuilder.setContentIntent(pIntent);

			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancelAll();
			nm.notify(NOTIFICATION_ID, mBuilder.build());

			// Start download
			downloadThread = new Thread(downloadApk);
			downloadThread.start();
		}

		@Override
		public void onDestroy() {
			Log.d(TAG, "Download service destory");
			super.onDestroy();

			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.cancelAll();
			if (downloadThread != null && downloadThread.isAlive()) {
				downloadThread.interrupt();
			}
		}

		private Runnable downloadApk = new Runnable() {

			@Override
			public void run() {
				URL myURL;
				URLConnection conn;
				int fileSize = 0;
				int downLoadFileSize = 0;
				InputStream is = null;
				FileOutputStream fos = null;
				String fileName = newApkUrl.substring(newApkUrl
						.lastIndexOf('/') + 1);

				File f = new File(downloadPath, fileName);
				if (f.exists()) {
					f.delete();
				}
				try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					stopSelf();
					Log.d(TAG, "Can't create download file");
					return;
				}

				try {
					myURL = new URL(newApkUrl);
					conn = myURL.openConnection();
					conn.connect();
					is = conn.getInputStream();
					if (is == null)
						throw new RuntimeException("stream is null");

					fileSize = conn.getContentLength();
					if (fileSize <= 0)
						throw new RuntimeException("无法获知文件大小 ");

					fos = new FileOutputStream(downloadPath + fileName);
					byte buf[] = new byte[1024 * 100];

					Log.d(TAG, "got a package from update server");
					while (!downloadThread.isInterrupted()) {
						// 循环读取
						int numread = 0;
						numread = is.read(buf);
						if (numread == -1) {
							break;
						}
						fos.write(buf, 0, numread);
						downLoadFileSize += numread;

						mBuilder.setProgress(100, 100 * downLoadFileSize
								/ fileSize, false);// 设置为true，表示刻度
						nm.notify(NOTIFICATION_ID, mBuilder.build());
						// Log.d(TAG, "Downloading total:" + fileSize
						// + " download " + downLoadFileSize
						// + "  progress" + 100 * downLoadFileSize
						// / fileSize);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

					Log.d(TAG, "Download stop");
					mBuilder.setProgress(100, 100, false);
					nm.cancelAll();

					installApk(f.getAbsoluteFile().toString());
					stopSelf();
				}
			}
		};

		private void installApk(String fileName) {
			Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
			notificationIntent.setDataAndType(Uri.parse("file://" + fileName),
					ANDROID_PACKAGE);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(notificationIntent);
		}
	}
}
