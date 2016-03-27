package com.sy.trucksysapp.utils;

import java.io.File;

import android.os.Environment;

public class FileUtil {
	public static boolean hasSDCard() {
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			return false;
		} 
		return true;
	}
	
	public static String getRootFilePath() {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
		} else {
			return Environment.getDataDirectory().getAbsolutePath() + "/data/"; // filePath: /data/data/
		}
	}
	
	public static String getSaveFilePath() {
		String path = getRootFilePath() + "com.sy.trucksysapp.data/";
		createDirectory(path);
		path = path + "database/";
		createDirectory(path);
		return getRootFilePath() + "com.sy.trucksysapp.data/database/";
	}
	
	public static boolean fileIsExist(String filePath) {
		if (filePath == null || filePath.length() < 1) {
			return false;
		}

		File f = new File(filePath);
		if (!f.exists()) {
			f.mkdir();
			return false;
		}
		return true;
	}
	
	public static void createDirectory(String path)
	{
		if (path == null || path.length() < 1) {
			return;
		}

		File f = new File(path);
		if (!f.exists()) {
			f.mkdir();
		}
	}

}
