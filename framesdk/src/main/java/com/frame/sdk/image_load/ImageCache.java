package com.frame.sdk.image_load;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;

import com.frame.sdk.file.ExternalStorageInfo;
import com.frame.sdk.file.FileManager;
import com.frame.sdk.util.ImageUtil;
import com.frame.sdk.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片缓存管理(LruCache)，包括内存及文件缓存的api
 * 
 */
public class ImageCache {
	private final static int hardCachedSize = 6 * 1024 * 1024;
	// 图片内存缓存map
	// private static Map<String, SoftReference<Bitmap>> bitmapMap = new
	// ConcurrentHashMap<String, SoftReference<Bitmap>>();
	private static LruCache<String, Bitmap> bitmapMap = new LruCache<String, Bitmap>(hardCachedSize) {
		@Override
		public int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}

	};

	/**
	 * 保存bitmap到内存
	 * 
	 * @param imageName
	 *            文件名
	 * @param bitmap
	 */
	public static void saveBitmapToMemory(String imageName, Bitmap bitmap) {
		// Log.i(LOG_TAG, "saveBitmapToMemory file imageName==" + imageName);
		if (bitmap == null)
			return;
		// bitmapMap.put(imageName, new SoftReference<Bitmap>(bitmap));
		bitmapMap.put(imageName, bitmap);
	}

	/**
	 * 保存bitmap到文件
	 * 
	 * @param imgName
	 *            文件名
	 * @param bitmap
	 * @return 保存成功返回true，失败返回false
	 */
	public static boolean saveBitmapToFile(String imgName, Bitmap bitmap) {
		String imageName = removeImgSuffix(imgName);
		if (bitmap == null)
			return false;
		if (!ExternalStorageInfo.isExistExternalStorage()) {
			LogUtils.e("没有外部存储器");
			return false;
		}
//		LogUtils.i("saveBitmapToFile file imageName==" + imageName);
		File imageDir = new File(FileManager.getImageDir());
		if (!imageDir.exists())
			imageDir.mkdirs();
		File bitmapFile = new File(FileManager.getImagePath(imageName));
		try {
			if (bitmapFile.exists())
				bitmapFile.delete();
			bitmapFile.createNewFile();
			FileOutputStream fos;
			fos = new FileOutputStream(bitmapFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void saveBitmapToCache(String imageName, Bitmap bitmap) {
		saveBitmapToMemory(imageName, bitmap);
		saveBitmapToFile(imageName, bitmap);
	}

	/**
	 * 从内存加载bitmap
	 * 
	 * @param imageName
	 *            文件名
	 * @return 成功返回bitmap，失败返回null
	 */
	public static Bitmap getBitmapFromMemory(String imageName) {
		// Log.i(LOG_TAG, "getBitmapFromMemory imageName==" + imageName);
		Bitmap bitmap = null;
		// if (bitmapMap.containsKey(imageName) && bitmapMap.get(imageName) !=
		// null)
		// bitmap = bitmapMap.get(imageName).get();
		bitmap = bitmapMap.get(imageName);

		if (bitmap == null) {
			bitmapMap.remove(imageName);
			return null;
		}

		// Log.i(LOG_TAG, "getBitmapFromMemory bitmap==" + bitmap);
		return bitmap;
	}

	/**
	 * 从文件加载bitmap
	 * 
	 * @param imgName
	 *            文件名
	 * @return 加载成功返回bitmap，失败返回null
	 */
	public static Bitmap getBitmapFromFile(String imgName) {
		String imageName = removeImgSuffix(imgName);
		Bitmap bmp = null;
		// Log.i(LOG_TAG, "getBitmapFromFile imageName==" + imageName);
		if (!ExternalStorageInfo.isExistExternalStorage()) {
			LogUtils.e("没有外部存储器");
			return bmp;
		}
		File imageDir = new File(FileManager.getImageDir());
		if (!imageDir.exists())
			return bmp;
		File[] cacheFiles = imageDir.listFiles();
		if (cacheFiles==null||cacheFiles.length<=0)
			return bmp;
		int i = 0;
		for (; i < cacheFiles.length; i++) {
			if (imageName.equals(cacheFiles[i].getName())) {
				break;
			}
		}
		if (i < cacheFiles.length) {
			bmp = BitmapFactory.decodeFile(FileManager.getImagePath(imageName));
		}
		// Log.i(LOG_TAG, "getBitmapFromFile bitmap==" + bmp);
		return bmp;
	}

	public static Bitmap getBitmapFromCache(String imageName) {
		Bitmap bmp = null;
		bmp = getBitmapFromMemory(imageName);
		if (bmp == null)
			bmp = getBitmapFromFile(imageName);
		return bmp;
	}

	public static void deleteBitmapFromMemory(String imageName) {
		bitmapMap.remove(imageName);
	}

	public static void deleteBitmapFromFile(String imgName) {
		String imageName = removeImgSuffix(imgName);
		if (!ExternalStorageInfo.isExistExternalStorage()) {
			return;
		}
		File bitmapFile = new File(FileManager.getImagePath(imageName));
		if (bitmapFile.exists())
			bitmapFile.delete();
	}

	public static void deleteBitmapFromCache(String imageName) {
		deleteBitmapFromMemory(imageName);
		deleteBitmapFromFile(imageName);
	}

	/**
	 * 清空图片缓存
	 */
	public static void clear() {
		// bitmapMap.clear();
		bitmapMap.evictAll();
		FileManager.deleteDir(FileManager.getImageDir());
	}

	/**
	 * 删除图片名后缀
	 * 
	 * @param imageName
	 * @return
	 */
	public static String removeImgSuffix(String imageName) {
		// 处理图片后缀
		if (ImageUtil.isImage(imageName)) {
			int index = imageName.lastIndexOf(".");
			if (index >= 0) {
				imageName = imageName.substring(0, index);
			}
		}
		return imageName;
	}
}
