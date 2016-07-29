package com.frame.sdk.file;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 外部存储器信息类
 */
public class ExternalStorageInfo {
	/**
	 * 判断是否挂载了外部存储
	 */
	public static boolean isExistExternalStorage() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 返回外部存储器的总容量（M）
	 */
	public static long getAllSize() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long blockSize = stat.getBlockSize();
		long blockCount = stat.getBlockCount();
		return (blockSize * blockCount) / 1024 / 1024;
	}

	/**
	 * 返回外部存储器的剩余容量（M）
	 */
	public static long getAvailableSize() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long blockSize = stat.getBlockSize();
		long blockCount = stat.getAvailableBlocks();
		return (blockSize * blockCount) / 1024 / 1024;
	}

	/**
	 * 获取扩展SD卡存储目录
	 *
	 * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
	 * 否则：返回内置SD卡目录
	 *
	 * @return
	 */
	public static String getDirectory() {
		if (isExistExternalStorage()) {
			File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
			return sdCardFile.getAbsolutePath();
		}

		String path = null;

		File sdCardFile = null;

		ArrayList<String> devMountList = getDevMountList();

		for (String devMount : devMountList) {
			File file = new File(devMount);

			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();

				String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
				File testWritable = new File(path, "test_" + timeStamp);

				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}

		if (path != null) {
			sdCardFile = new File(path);
			return sdCardFile.getAbsolutePath();
		}

		return null;
	}

	/**
	 * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
	 *
	 * @return
	 */
	private static ArrayList<String> getDevMountList() {
		String[] toSearch = FileUtil.readText("/etc/vold.fstab").split(" ");
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0; i < toSearch.length; i++) {
			if (toSearch[i].contains("dev_mount")) {
				if (new File(toSearch[i + 2]).exists()) {
					out.add(toSearch[i + 2]);
				}
			}
		}
		return out;
	}
}
