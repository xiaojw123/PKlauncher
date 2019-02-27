package com.pkit.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class UpgradeUtil {

	public static File upgrade(String addr, String loadDir, String loadFileName) throws Exception {
		URL url = new URL(addr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30 * 1000);
		InputStream in = conn.getInputStream();
		File loadFileDir = new File(loadDir);
		if (!loadFileDir.exists()) {
			loadFileDir.mkdirs();
		}
		File downloadFile = new File(loadFileDir, loadFileName);
		FileOutputStream out = new FileOutputStream(downloadFile);
		byte[] buf = new byte[1024];
		int len = in.read(buf);
		while (len != -1) {
			out.write(buf, 0, len);
			len = in.read(buf);
		}

		in.close();
		out.close();
		return downloadFile;
	}

	public static void install(Context context, File apk) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apk.getAbsolutePath()), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			String packageName = context.getPackageName();
			versionCode = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}
}
