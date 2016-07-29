package com.frame.sdk.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * 图片处理类
 */
public class ImageUtil {
	/**
	 * 把Bitmap 转成 Byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 把Bitmap 转成 Byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * byte[]转换成Bitmap
	 */
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	/**
	 * Drawable转换成byte[]
	 */
	public static byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2Bytes(bitmap);
	}

	/**
	 * byte[]转换成Drawable
	 */
	public Drawable bytes2Drawable(byte[] b) {
		Bitmap bitmap = bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	/**
	 * Drawable转换成Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap转换成Drawable
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable d = new BitmapDrawable(bitmap);
		return d;
	}

	/**
	 * 根据显示bitmap的控件的宽高从文件中读取图片
	 * 
	 * @param filePath
	 *            图片文件路径
	 * @param width
	 *            显示图片的控件宽度
	 * @param height
	 *            显示图片的控件高度
	 * @return
	 */
	public static Bitmap getBitmap(String filePath, int width, int height) {
		if (!new File(filePath).exists()) {
			LogUtils.e("文件" + filePath + "不存在");
			return null;
		}
		if (width == 0 || height == 0) {
			LogUtils.e("width=0或者height=0");
			return null;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int wScale = (int) Math.ceil(1.0 * newOpts.outWidth / width);
		int yScale = (int) Math.ceil(1.0 * newOpts.outHeight / height);
		newOpts.inSampleSize = wScale > yScale ? wScale : yScale;
		if (newOpts.inSampleSize <= 0)
			newOpts.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		return bitmap;
	}

	/**
	 * 从文件中读取大小不超过maxBytesCount字节的bitmap
	 * 
	 * @param filePath
	 * @param maxBytesCount
	 *            读取的最大字节总数
	 * @return
	 */
	public static Bitmap getBitmap(String filePath, int maxBytesCount) {
		if (!new File(filePath).exists()) {
			LogUtils.e("文件" + filePath + "不存在");
			return null;
		}
		if (maxBytesCount <= 0) {
			LogUtils.e("maxBytesCount<=0");
			return null;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, newOpts);
		newOpts.inJustDecodeBounds = false;

		int pixel = newOpts.outWidth * newOpts.outHeight - maxBytesCount / 4;// 需要减少的像素
		if (pixel > 0) {
			if (newOpts.outWidth > newOpts.outHeight)
				newOpts.inSampleSize = (int) Math.ceil(newOpts.outWidth / (newOpts.outWidth - Math.sqrt(pixel)));
			else
				newOpts.inSampleSize = (int) Math.ceil(newOpts.outHeight / (newOpts.outHeight - Math.sqrt(pixel)));
		}

		if (newOpts.inSampleSize <= 0)
			newOpts.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);
		return bitmap;
	}

	/**
	 * 根据显示bitmap的控件的宽高从资源文件中读取图片
	 * @param width
	 *            显示图片的控件宽度
	 * @param height
	 *            显示图片的控件高度
	 * @return
	 */
	public static Bitmap getBitmap(Resources res, int rId, int width, int height) {
		if (width <= 0 || height <= 0) {
			LogUtils.e("width<=0或者height<=0");
			return null;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, rId, newOpts);
		newOpts.inJustDecodeBounds = false;
		int wScale = (int) Math.ceil(1.0 * newOpts.outWidth / width);
		int yScale = (int) Math.ceil(1.0 * newOpts.outHeight / height);
		newOpts.inSampleSize = wScale > yScale ? wScale : yScale;
		if (newOpts.inSampleSize <= 0)
			newOpts.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeResource(res, rId, newOpts);
		return bitmap;
	}

	/**
	 * 从资源文件中读取大小不超过maxBytesCount字节的bitmap
	 * 
	 * @param maxBytesCount
	 *            读取的最大字节总数
	 * @return
	 */
	public static Bitmap getBitmap(Resources res, int rId, int maxBytesCount) {
		if (maxBytesCount <= 0) {
			LogUtils.e("maxBytesCount<=0");
			return null;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, rId, newOpts);
		newOpts.inJustDecodeBounds = false;

		int pixel = newOpts.outWidth * newOpts.outHeight - maxBytesCount / 4;// 需要减少的像素
		if (pixel > 0) {
			if (newOpts.outWidth > newOpts.outHeight)
				newOpts.inSampleSize = (int) Math.ceil(newOpts.outWidth / (newOpts.outWidth - Math.sqrt(pixel)));
			else
				newOpts.inSampleSize = (int) Math.ceil(newOpts.outHeight / (newOpts.outHeight - Math.sqrt(pixel)));
		}

		if (newOpts.inSampleSize <= 0)
			newOpts.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeResource(res, rId, newOpts);
		return bitmap;
	}

	/**
	 * 以PNG图片格式存储图片
	 * 
	 * @param bmp
	 * @param filePath
	 */
	public static boolean saveBitmap(Bitmap bmp, String filePath) {
		if (bmp == null)
			return false;
		File f = new File(filePath);
		boolean res = false;
		try {
			res = bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 圆角图片(处理方法是在canvas上画一个圆角矩形，然后把目标bitmap画在圆角矩形上)
	 * 
	 * @param bitmap
	 *            原始bitmap
	 * @param roundPx
	 *            圆角的x半径
	 * @param roundPy
	 *            圆角的y半径
	 * @return 圆角图片bitmap
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, float roundPx, float roundPy) {
		if (bitmap == null)
			return null;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.GRAY);

		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);

		canvas.drawColor(Color.TRANSPARENT);

		canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 圆形图片(把一个图片处理成圆形的图片)
	 * 
	 * @param bitmap
	 * @param isClip
	 *            如果图片长宽不等，是否裁剪图片，取图片中间一部分进行圆角处理
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, boolean isClip) {
		if (bitmap == null)
			return null;
		if (isClip && bitmap.getWidth() != bitmap.getHeight()) {
			bitmap = clipBitmap(bitmap);
		}
		return toRoundCorner(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
	}

	/**
	 * 圆形图片(把一个图片处理成圆形的图片， 如果图片长宽不等，是否裁剪图片，取图片中间一部分进行圆角处理)
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap) {
		return toRoundCorner(bitmap, true);
	}


	/**
	 * 质量压缩bitmap。并没有减少像素，只是改变了图片的色深和透明度。所以这里返回的bitmap的占用内存大小并没有减小。
	 *
	 * @param bitmap
	 * @param toByteLen  压缩到的字节数
	 * @param outputPath 压缩后保存路径，为null将不会保存
	 * @return
	 */
	public static void compressBitmap(Bitmap bitmap, double toByteLen,
									  String outputPath) {
		if (bitmap == null)
			return;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		int len = baos.toByteArray().length;
		LogUtils.i("压缩前byte.length==" + len + "(byte),==" + 1.0f
				* len / 1024 + "kb");
		while (len > toByteLen) {
			quality -= 2;
			if (quality < 0) {
				quality = 0;
				break;
			}
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			len = baos.toByteArray().length;
		}
		LogUtils.i("压缩图片后，JPEG格式压缩图片，质量=" + quality
				+ ",压缩后byte.length==" + len + "(byte),==" + 1.0f * len / 1024
				+ "kb");
		FileOutputStream fos;
		try {
			if (!TextUtils.isEmpty(outputPath)) {
				fos = new FileOutputStream(new File(outputPath));
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 裁剪图片到一个正方形图片，根据图片宽高取图片中间的部分
	 */
	public static Bitmap clipBitmap(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		Rect rectOrg = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		Rect rectClip = clipRect(rectOrg);
		return clipBitmap(bitmap, rectClip);
	}

	/**
	 * 裁剪图片(通过画布取图片的一部分)
	 * 
	 * @param desRect
	 *            裁剪图片的目标区域,默认图片左上角为（0,0）
	 */
	public static Bitmap clipBitmap(Bitmap bitmap, Rect desRect) {
		if (bitmap == null)
			return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		if (desRect.left < 0)
			desRect.left = 0;

		if (desRect.right > w)
			desRect.right = w;

		if (desRect.top < 0)
			desRect.top = 0;

		if (desRect.bottom > h)
			desRect.bottom = h;

		if (desRect.left >= desRect.right || desRect.top >= desRect.bottom) {
			LogUtils.e("参数desRect错误，请传入矩形right>left,bottom>top");
			return null;
		}

		Bitmap bmp = Bitmap.createBitmap(desRect.width(), desRect.height(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		canvas.drawBitmap(bitmap, desRect, new RectF(0, 0, desRect.width(), desRect.height()), new Paint());
		return bmp;
	}

	/**
	 * 把一个矩形裁剪成一个正方形，如果宽度、高度不相等，根据宽高来裁剪中间一部分
	 * 
	 * @param orgRect
	 * @return
	 */
	public static Rect clipRect(Rect orgRect) {
		int left = orgRect.left, right = orgRect.right, top = orgRect.top, bottom = orgRect.bottom;
		int orgW = orgRect.width();
		int orgH = orgRect.height();
		if (orgH > orgW) {
			top += (orgH - orgW) / 2;
			bottom = top + orgW;
		} else if (orgH < orgW) {
			left += (orgW - orgH) / 2;
			right = left + orgH;
		}
		return new Rect(left, top, right, bottom);
	}

	/**
	 * 图片叠加
	 * 
	 * @param bmpBelow
	 *            底下的bitmap
	 * @param bmpAbove
	 *            上面的bitmap
	 * @param left
	 *            第二个图片的left坐标
	 * @param top
	 *            第二个图片的top坐标
	 * @return
	 */
	public static Bitmap overlayBitmap(Bitmap bmpBelow, Bitmap bmpAbove, float left, float top) {
		if (bmpBelow == null)
			return null;
		if (bmpAbove == null)
			return bmpBelow;
		Bitmap bitmap = Bitmap.createBitmap(bmpBelow.getWidth(), bmpBelow.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(bmpBelow, 0, 0, new Paint());
		canvas.drawBitmap(bmpAbove, left, top, new Paint());
		return bitmap;
	}

	/**
	 * 旋转图片(通过矩阵旋转)
	 * 
	 * @param bmp
	 * @param degree
	 *            (角度)
	 * @param px
	 *            （中心点x）
	 * @param py
	 *            （中心点y）
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree, float px, float py) {
		if (bmp == null)
			return null;
		Matrix matrix = new Matrix();
		matrix.postRotate(degree, px, py);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * 围绕中心点旋转图片
	 * 
	 * @param bmp
	 * @param degree
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
		return rotateBitmap(bmp, degree, bmp.getWidth() / 2, bmp.getHeight() / 2);
	}

	/**
	 * 图片缩放 (通过矩阵缩放)
	 * 
	 * @param bmp
	 * @param sx
	 *            x的缩放比例
	 * @param sy
	 *            y的缩放比例
	 * @param px
	 *            缩放中心点x
	 * @param py
	 *            缩放中心点y
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bmp, float sx, float sy, float px, float py) {
		if (bmp == null)
			return null;
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy, px, py);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * 绕中心点缩放图片
	 * 
	 * @param bmp
	 * @param sx
	 *            x的缩放比例
	 * @param sy
	 *            y的缩放比例
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bmp, float sx, float sy) {
		return scaleBitmap(bmp, sx, sy, bmp.getWidth() / 2, bmp.getHeight() / 2);
	}

	/**
	 * Bitmap缩放到指定宽度高度
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int desWidth, int desHeight) {
		if (bitmap == null)
			return null;
		if (desHeight <= 0 || desWidth <= 0) {
			LogUtils.e("desHeight<=0或者desWidth<=0");
			return null;
		}
		return Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
	}

	/**
	 * Bitmap缩放,按高度，缩放到指定高度
	 */
	public static Bitmap scaleBitmapByHeight(Bitmap bitmap, int desHeight) {
		if (bitmap == null)
			return null;
		int desWidth = (int) (desHeight * bitmap.getWidth() / ((double) bitmap.getHeight()));
		return scaleBitmap(bitmap, desWidth, desHeight);
	}

	/**
	 * Bitmap缩放,按宽度，缩放到指定宽度
	 */
	public static Bitmap scaleBitmapByWidth(Bitmap bitmap, int desWidth) {
		if (bitmap == null)
			return null;
		int desHeight = (int) (desWidth * bitmap.getHeight() / ((double) bitmap.getWidth()));
		return scaleBitmap(bitmap, desWidth, desHeight);
	}

	/**
	 * 倾斜(错切)
	 * 
	 * @param bmp
	 * @param kx
	 * @param ky
	 * @param px
	 *            倾斜中心点x
	 * @param py
	 *            倾斜中心点y
	 * @return
	 */
	public static Bitmap skewBitmap(Bitmap bmp, float kx, float ky, float px, float py) {
		if (bmp == null)
			return null;
		Matrix matrix = new Matrix();
		matrix.postSkew(kx, ky, px, py);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * 绕中心点倾斜(错切)
	 * 
	 * @param bmp
	 * @param kx
	 * @param ky
	 * @return
	 */
	public static Bitmap skewBitmap(Bitmap bmp, float kx, float ky) {
		return skewBitmap(bmp, kx, ky, bmp.getWidth() / 2, bmp.getHeight() / 2);
	}

	public static Bitmap translateBitmap(Bitmap bmp, float dx, float dy) {
		if (bmp == null)
			return null;
		Matrix matrix = new Matrix();
		matrix.postTranslate(dx, dy);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	public enum ReverseType {
		X_AXIS, Y_AXIS, ORIGIN
	}

	/**
	 * 反转
	 * 
	 * @param bmp
	 * @param reverseType
	 *            ReverseType.X_AXIS:关于X轴反转，Y_AXIS：关于Y轴反转，ORIGIN:关于原点反转
	 * @return
	 */
	public static Bitmap reverseBitmap(Bitmap bmp, ReverseType reverseType) {
		if (bmp == null)
			return null;
		Matrix matrix = new Matrix();
		float[] values = new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
		if (reverseType == ReverseType.X_AXIS) {
			values = new float[] { 1, 0, 0, 0, -1, 0, 0, 0, 1 };
		} else if (reverseType == ReverseType.Y_AXIS) {
			values = new float[] { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
		} else if (reverseType == ReverseType.ORIGIN) {
			values = new float[] { -1, 0, 0, 0, -1, 0, 0, 0, 1 };
		}
		matrix.setValues(values);
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * 根据matrix变换bitmap
	 * 
	 * @param bmp
	 * @param matrix
	 * @return
	 */
	public static Bitmap matrixBitmap(Bitmap bmp, Matrix matrix) {
		if (bmp == null)
			return null;
		return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	}

	/**
	 * 调整图像的色调（rgba）、饱和度（白光越多，饱和度越大）、颜色旋转
	 * 
	 * @param bmp
	 * @param rScale
	 *            red缩放倍数
	 * @param gScale
	 *            green缩放倍数
	 * @param bScale
	 *            blue缩放倍数
	 * @param aScale
	 *            alpha缩放倍数
	 * @param saturate
	 *            饱和度（0.0-1.0）
	 * @param rDegrees
	 *            red旋转角度（0-360）
	 * @param gDegrees
	 *            green旋转角度（0-360）
	 * @param bDegrees
	 *            blue旋转角度（0-360）
	 * @return
	 */
	public static Bitmap adjustImage(Bitmap bmp, float rScale, float gScale, float bScale, float aScale, float saturate, float rDegrees, float gDegrees, float bDegrees) {
		Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		ColorMatrix scaleMatrix = new ColorMatrix();
		scaleMatrix.setScale(rScale, gScale, bScale, aScale);

		ColorMatrix saturateMatrix = new ColorMatrix();
		saturateMatrix.setSaturation(saturate);

		ColorMatrix rotateRMatrix = new ColorMatrix();
		rotateRMatrix.setRotate(0, rDegrees);

		ColorMatrix rotateGMatrix = new ColorMatrix();
		rotateGMatrix.setRotate(1, gDegrees);

		ColorMatrix rotateBMatrix = new ColorMatrix();
		rotateBMatrix.setRotate(2, bDegrees);

		ColorMatrix matrix = new ColorMatrix();
		matrix.postConcat(scaleMatrix);
		matrix.postConcat(saturateMatrix);
		matrix.postConcat(rotateRMatrix);
		matrix.postConcat(rotateGMatrix);
		matrix.postConcat(rotateBMatrix);

		paint.setColorFilter(new ColorMatrixColorFilter(matrix));
		canvas.drawBitmap(bmp, 0, 0, paint);
		return bitmap;
	}

	/**
	 * 图片滤镜处理类型
	 */
	public enum FilterType {
		OLD_PHOTO, GAUSSIAN_BLUR, BOX_BLUR, FAST_BLUR, RANDOM_NOISE, SKETCH, STROKE, OIL_PAINTING, LAPLACE_SHARPEN, BLACK_AND_WHITE_PHOTO, FEATHER, LIGHT, RELIEF, CAST, FROZEN, COMICSTRIP, FILM;
	}

	/**
	 * 图片滤镜类型对应的名称
	 */
	public static String getFilterTypeName(FilterType type) {
		String str = "";
		switch (type) {
		case OLD_PHOTO:
			str = "怀旧";
			break;
		case GAUSSIAN_BLUR:
			str = "高斯模糊";
			break;
		case BOX_BLUR:
			str = "均值模糊";
			break;
		case FAST_BLUR:
			str = "快速模糊";
			break;
		case RANDOM_NOISE:
			str = "随机噪声";
			break;
		case BLACK_AND_WHITE_PHOTO:
			str = "黑白";
			break;
		case SKETCH:
			str = "素描";
			break;
		case STROKE:
			str = "描边";
			break;
		case OIL_PAINTING:
			str = "油画";
			break;
		case LAPLACE_SHARPEN:
			str = "拉普拉斯锐化";
			break;
		case RELIEF:
			str = "浮雕";
			break;
		case FILM:
			str = "胶片(反色)";
			break;
		case LIGHT:
			str = "光照";
			break;
		case FEATHER:
			str = "羽化";
			break;
		case CAST:
			str = "熔铸";
			break;
		case FROZEN:
			str = "冰冻";
			break;
		case COMICSTRIP:
			str = "连环画";
			break;
		}
		return str;
	}

	/**
	 * 滤镜处理
	 * 
	 * @param bmp
	 * @param type
	 *            滤镜类型
	 * @return
	 */
	public static Bitmap doFilter(Bitmap bmp, FilterType type) {
		Bitmap bitmap = null;
		switch (type) {
		case OLD_PHOTO:
			bitmap = oldPhoto(bmp);
			break;
		case GAUSSIAN_BLUR:
			bitmap = gaussianBlur1(bmp, 16);
			// bitmap = gaussianBlur2(bmp, 2);
			break;
		case BOX_BLUR:
			bitmap = boxBlur(bmp, 2);
			break;
		case FAST_BLUR:
			bitmap = fastBlur(bmp, 8);
			break;
		case STROKE:
			bitmap = sobleStroke(bmp);
			break;
		case SKETCH:
			// bitmap = pencilSketch(bmp);
			bitmap = simpleSketch(bmp, 15);
			break;
		case BLACK_AND_WHITE_PHOTO:
			bitmap = blackAndWhitePhoto(bmp);
			break;
		case OIL_PAINTING:
			bitmap = oilPainting(bmp, 2, 100);
			break;
		case RANDOM_NOISE:
			bitmap = randomNoise(bmp, 50);
			break;
		case LAPLACE_SHARPEN:
			bitmap = laplaceSharpen(bmp, 1);
			break;
		case FILM:
			bitmap = film(bmp);
			break;
		case RELIEF:
			bitmap = relief(bmp);
			break;
		case LIGHT:
			bitmap = light(bmp, 150);
			break;
		case FEATHER:
			bitmap = feather(bmp, 0.5f);
			break;
		case CAST:
			bitmap = cast(bmp);
			break;
		case FROZEN:
			bitmap = frozen(bmp);
			break;
		case COMICSTRIP:
			bitmap = comicStrip(bmp);
			break;
		}
		return bitmap;
	}

	/**
	 * 羽化
	 * 
	 * @param bmp
	 * @param value
	 *            羽化值、越大，朦胧范围越窄
	 * @return
	 */
	public static Bitmap feather(Bitmap bmp, float value) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);

		int centerX = width / 2;
		int centerY = height / 2;
		float diff = (centerX * centerX + centerY * centerY) * value;
		float ratio = width > height ? 1.0f * height / width : 1.0f * width / height;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int idx = width * y + x;
				pixColor = pixels[idx];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				float dx = centerX - x;
				float dy = centerY - y;

				if (width > height)
					dx = dx * ratio;
				else
					dy = dy * ratio;

				float v = 255 * (dx * dx + dy * dy) / diff;

				newR = (int) (pixR + v);
				newG = (int) (pixG + v);
				newB = (int) (pixB + v);
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[idx] = Color.argb(Color.alpha(pixels[idx]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		// bitmap = fastBlur(bitmap, 3);
		return bitmap;
	}

	/**
	 * 随机噪声
	 * 
	 * @param bmp
	 * @param n
	 *            n越大，图片上的噪点越多
	 * @return
	 */
	public static Bitmap randomNoise(Bitmap bmp, int n) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		Random random = new Random();
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int idx = width * i + k;
				pixColor = pixels[idx];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				int r = random.nextInt(n);
				pixR = (int) (pixR + r);
				pixG = (int) (pixG + r);
				pixB = (int) (pixB + r);

				pixR = Math.min(255, Math.max(0, pixR));
				pixG = Math.min(255, Math.max(0, pixG));
				pixB = Math.min(255, Math.max(0, pixB));
				pixels[idx] = Color.argb(Color.alpha(pixColor), pixR, pixG, pixB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 怀旧
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap oldPhoto(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[i * width + k] = Color.argb(Color.alpha(pixels[i * width + k]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 黑白
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap blackAndWhitePhoto(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newB = newG = newR = (int) (0.299 * pixR + 0.587 * pixG + 0.114 * pixB);
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[i * width + k] = Color.argb(Color.alpha(pixels[i * width + k]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 连环画
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap comicStrip(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (1.0f * Math.abs(pixG - pixB + pixG + pixR) * pixR / 255);
				newG = (int) (1.0f * Math.abs(pixB - pixG + pixB + pixR) * pixR / 255);
				newB = (int) (1.0f * Math.abs(pixB - pixG + pixB + pixR) * pixG / 255);
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[i * width + k] = Color.argb(Color.alpha(pixels[i * width + k]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 冰冻
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap frozen(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixA = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixA = Color.alpha(pixColor);
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (1.0f * (pixR - pixA - pixB) * 3 / 2);
				newG = (int) (1.0f * (pixA - pixR - pixB) * 3 / 2);
				newB = (int) (1.0f * (pixB - pixG - pixR) * 3 / 2);
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[i * width + k] = Color.argb(Color.alpha(pixels[i * width + k]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 熔铸
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap cast(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (1.0f * pixR * 128 / (pixG + pixB + 1));
				newG = (int) (1.0f * pixG * 128 / (pixR + pixB + 1));
				newB = (int) (1.0f * newB * 128 / (pixG + pixR + 1));
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				pixels[i * width + k] = Color.argb(Color.alpha(pixels[i * width + k]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 油画
	 * 
	 * @param bmp
	 * @param x
	 *            x可以看成窗口半径(x>=1)
	 * @param smoothness
	 *            smoothness 光滑度,越小越光滑(smoothness>1)
	 * @return
	 */
	public static Bitmap oilPainting(Bitmap bmp, int x, int smoothness) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;

		int size = width * height;
		int[] inPixels = new int[size];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[size];

		int tableSize = (2 * x + 1) * (2 * x + 1);
		int[][][] buckets = new int[3][smoothness][tableSize];// 3*smoothness个桶，每个桶长度tableSize
		int[][] index = new int[3][smoothness];// 存放各个桶中元素的个数
		int num = 255 / smoothness;
		if (255 % smoothness != 0) {
			num++;
		}
		for (int i = x, length = height - x; i < length; i++) {
			for (int k = x, len = width - x; k < len; k++) {
				if (k == x) {// 换行了，重新把各个像素放入桶中
					// 卷积
					for (int m = 0; m < 3; m++) {
						for (int n = 0; n < smoothness; n++) {
							index[m][n] = 0;
						}
					}
					for (int m = -x; m <= x; m++) {
						for (int n = -x; n <= x; n++) {
							pixColor = inPixels[(i + m) * width + k + n];
							pixR = Color.red(pixColor);
							pixG = Color.green(pixColor);
							pixB = Color.blue(pixColor);
							// newB = newG = newR = (int) (0.299 * pixR + 0.587
							// *
							// pixG + 0.114 * pixB);

							int bucketIndex = pixR / num;// 当前灰度值放入第几个桶中
							add(buckets[0][bucketIndex], index[0][bucketIndex]++, pixR, 0);
							bucketIndex = pixG / num;// 当前灰度值放入第几个桶中
							add(buckets[1][bucketIndex], index[1][bucketIndex]++, pixG, 0);
							bucketIndex = pixB / num;// 当前灰度值放入第几个桶中
							add(buckets[2][bucketIndex], index[2][bucketIndex]++, pixB, 0);
						}
					}

				} else {// 窗口右移一步，把最左边那一列的像素移出桶中，右边一列加入桶中
					for (int m = -x; m <= x; m++) {// 最左边那一列的像素移出桶中l
						pixColor = inPixels[(i + m) * width + k - 1 + -x];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						int bucketIndex = pixR / num;// 当前灰度值放入第几个桶中
						delete(buckets[0][bucketIndex], index[0][bucketIndex]--, pixR, 0);
						bucketIndex = pixG / num;// 当前灰度值放入第几个桶中
						delete(buckets[1][bucketIndex], index[1][bucketIndex]--, pixG, 0);
						bucketIndex = pixB / num;// 当前灰度值放入第几个桶中
						delete(buckets[2][bucketIndex], index[2][bucketIndex]--, pixB, 0);
					}
					for (int m = -x; m <= x; m++) {// 右边一列加入桶中
						pixColor = inPixels[(i + m) * width + k + x];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						int bucketIndex = pixR / num;// 当前灰度值放入第几个桶中
						add(buckets[0][bucketIndex], index[0][bucketIndex]++, pixR, 0);
						bucketIndex = pixG / num;// 当前灰度值放入第几个桶中
						add(buckets[1][bucketIndex], index[1][bucketIndex]++, pixG, 0);
						bucketIndex = pixB / num;// 当前灰度值放入第几个桶中
						add(buckets[2][bucketIndex], index[2][bucketIndex]++, pixB, 0);
					}
				}

				int max = 0;
				for (int m = 0; m < smoothness; m++) {
					if (index[0][max] < index[0][m])
						max = m;
				}
				int sum = 0;
				for (int m = 0; m < index[0][max]; m++)
					sum += buckets[0][max][m];
				newR = sum / index[0][max];

				max = 0;
				for (int m = 0; m < smoothness; m++) {
					if (index[1][max] < index[1][m])
						max = m;
				}
				sum = 0;
				for (int m = 0; m < index[1][max]; m++)
					sum += buckets[1][max][m];
				newG = sum / index[1][max];

				max = 0;
				for (int m = 0; m < smoothness; m++) {
					if (index[2][max] < index[2][m])
						max = m;
				}
				sum = 0;
				for (int m = 0; m < index[2][max]; m++)
					sum += buckets[2][max][m];
				newB = sum / index[2][max];

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		edge(outPixels, width, height, x);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/*
	 * /** 油画(保存各个桶的和，但时间不明显，貌似没必要)
	 * 
	 * @param bmp
	 * 
	 * @param x x可以看成窗口半径
	 * 
	 * @param smoothness smoothness 光滑度,越小越光滑
	 * 
	 * @return
	 */
	/*
	 * public static Bitmap oilPainting(Bitmap bmp, int x, int smoothness) { if
	 * (bmp == null) return null; int width = bmp.getWidth(); int height =
	 * bmp.getHeight();
	 * 
	 * int pixR = 0; int pixG = 0; int pixB = 0;
	 * 
	 * int pixColor = 0; int newR = 0; int newG = 0; int newB = 0;
	 * 
	 * int size = width * height; int[] inPixels = new int[size];
	 * bmp.getPixels(inPixels, 0, width, 0, 0, width, height); int[] outPixels =
	 * new int[size];
	 * 
	 * int tableSize = (2 * x + 1) * (2 * x + 1); int[][][] buckets = new
	 * int[3][smoothness][tableSize];// 3*smoothness个桶，每个桶长度tableSize int[][]
	 * index = new int[3][smoothness];// 存放各个桶中元素的个数 int[][] sum = new
	 * int[3][smoothness];// 存放各个桶中所有元素之和 for (int i = x, length = height - x; i
	 * < length; i++) { for (int k = x, len = width - x; k < len; k++) { if (k
	 * == x) {// 换行了，重新把各个像素放入桶中 // 卷积 for (int m = 0; m < 3; m++) { for (int n
	 * = 0; n < smoothness; n++) { index[m][n] = 0; sum[m][n] = 0; } } for (int
	 * m = -x; m <= x; m++) { for (int n = -x; n <= x; n++) { pixColor =
	 * inPixels[(i + m) * width + k + n]; pixR = Color.red(pixColor); pixG =
	 * Color.green(pixColor); pixB = Color.blue(pixColor); // newB = newG = newR
	 * = (int) (0.299 * pixR + 0.587 // * // pixG + 0.114 * pixB);
	 * 
	 * int bucketIndex = getIndex(255, smoothness, pixR);// 当前灰度值放入第几个桶中
	 * sum[0][bucketIndex] = add(buckets[0][bucketIndex],
	 * index[0][bucketIndex]++, pixR, sum[0][bucketIndex]); bucketIndex =
	 * getIndex(255, smoothness, pixG);// 当前灰度值放入第几个桶中 sum[1][bucketIndex] =
	 * add(buckets[1][bucketIndex], index[1][bucketIndex]++, pixG,
	 * sum[1][bucketIndex]); bucketIndex = getIndex(255, smoothness, pixB);//
	 * 当前灰度值放入第几个桶中 sum[2][bucketIndex] = add(buckets[2][bucketIndex],
	 * index[2][bucketIndex]++, pixB, sum[2][bucketIndex]); } }
	 * 
	 * } else {// 窗口右移一步，把最左边那一列的像素移出桶中，右边一列加入桶中 for (int m = -x; m <= x; m++)
	 * {// 最左边那一列的像素移出桶中l pixColor = inPixels[(i + m) * width + k - 1 + -x];
	 * pixR = Color.red(pixColor); pixG = Color.green(pixColor); pixB =
	 * Color.blue(pixColor);
	 * 
	 * int bucketIndex = getIndex(255, smoothness, pixR);// 当前灰度值放入第几个桶中
	 * sum[0][bucketIndex] = delete(buckets[0][bucketIndex],
	 * index[0][bucketIndex]--, pixR, sum[0][bucketIndex]); bucketIndex =
	 * getIndex(255, smoothness, pixG);// 当前灰度值放入第几个桶中 sum[1][bucketIndex] =
	 * delete(buckets[1][bucketIndex], index[1][bucketIndex]--, pixG,
	 * sum[1][bucketIndex]); bucketIndex = getIndex(255, smoothness, pixB);//
	 * 当前灰度值放入第几个桶中 sum[2][bucketIndex] = delete(buckets[2][bucketIndex],
	 * index[2][bucketIndex]--, pixB, sum[2][bucketIndex]); } for (int m = -x; m
	 * <= x; m++) {// 右边一列加入桶中 pixColor = inPixels[(i + m) * width + k + x];
	 * pixR = Color.red(pixColor); pixG = Color.green(pixColor); pixB =
	 * Color.blue(pixColor);
	 * 
	 * int bucketIndex = getIndex(255, smoothness, pixR);// 当前灰度值放入第几个桶中
	 * sum[0][bucketIndex] = add(buckets[0][bucketIndex],
	 * index[0][bucketIndex]++, pixR, sum[0][bucketIndex]); bucketIndex =
	 * getIndex(255, smoothness, pixG);// 当前灰度值放入第几个桶中 sum[1][bucketIndex] =
	 * add(buckets[1][bucketIndex], index[1][bucketIndex]++, pixG,
	 * sum[1][bucketIndex]); bucketIndex = getIndex(255, smoothness, pixB);//
	 * 当前灰度值放入第几个桶中 sum[2][bucketIndex] = add(buckets[2][bucketIndex],
	 * index[2][bucketIndex]++, pixB, sum[2][bucketIndex]); } }
	 * 
	 * int max = 0; for (int m = 0; m < smoothness; m++) { if (index[0][max] <
	 * index[0][m]) max = m; } newR = sum[0][max] / index[0][max];
	 * 
	 * max = 0; for (int m = 0; m < smoothness; m++) { if (index[1][max] <
	 * index[1][m]) max = m; } newG = sum[1][max] / index[1][max];
	 * 
	 * max = 0; for (int m = 0; m < smoothness; m++) { if (index[2][max] <
	 * index[2][m]) max = m; } newB = sum[2][max] / index[2][max];
	 * 
	 * newR = Math.min(255, Math.max(0, newR)); newG = Math.min(255, Math.max(0,
	 * newG)); newB = Math.min(255, Math.max(0, newB));
	 * 
	 * outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width +
	 * k]), newR, newG, newB); newR = 0; newG = 0; newB = 0; } } edge(outPixels,
	 * width, height, x); Bitmap bitmap = Bitmap.createBitmap(width, height,
	 * Bitmap.Config.ARGB_8888); bitmap.setPixels(outPixels, 0, width, 0, 0,
	 * width, height); return bitmap; }
	 */

	/**
	 * 把数组a中element元素与最后一个元素互换
	 * 
	 * @param a
	 * @param idx
	 * @param element
	 * @return 返回a中各个元素之和
	 */
	private static int delete(int[] a, int idx, int element, int sum) {
		for (int i = 0; i < idx; i++) {
			if (a[i] == element) {
				a[i] = a[idx - 1];
				sum -= element;
				break;
			}
		}
		return sum;
	}

	/**
	 * 把元素放入数组a中
	 * 
	 * @param a
	 * @param idx
	 * @param element
	 * @return 返回a中各个元素之和
	 */
	private static int add(int[] a, int idx, int element, int sum) {
		a[idx] = element;
		return sum + element;
	}

	/**
	 * 简单的高斯模糊。
	 * 
	 * @param bmp
	 * @param decay
	 *            衰减因子，值越小，图片会越亮
	 * @return
	 */
	public static Bitmap gaussianBlur1(Bitmap bmp, float decay) {
		if (bmp == null)
			return null;
		// 简单高斯矩阵
		float[] matrix = new float[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };
		int x = 1;
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;

		int size = width * height;
		int[] inPixels = new int[size];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[size];
		for (int i = 0; i < size; i++) {
			outPixels[i] = inPixels[i];
		}

		int idx = 0;

		for (int i = x, length = height - x; i < length; i++) {
			for (int k = x, len = width - x; k < len; k++) {

				idx = 0;
				// 卷积
				for (int m = -x; m <= x; m++) {
					for (int n = -x; n <= x; n++) {
						pixColor = inPixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = (int) (newR + (pixR * matrix[idx]));
						newG = (int) (newG + (pixG * matrix[idx]));
						newB = (int) (newB + (pixB * matrix[idx]));
						idx++;
					}
				}
				newR = (int) (newR / decay);
				newG = (int) (newG / decay);
				newB = (int) (newB / decay);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		edge(outPixels, width, height, x);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 高斯模糊（去噪）。1、根据x计算高斯矩阵。2、卷积。3、卷积结果归一化处理。4、边缘处理
	 * 
	 * @param bmp
	 * @param x
	 *            高斯矩阵的宽度=2*x+1.x越大、该函数越耗时。x可以看成窗口半径
	 * @return
	 */
	public static Bitmap gaussianBlur2(Bitmap bmp, int x) {
		if (bmp == null)
			return null;
		// 高斯矩阵
		float[] matrix = getGaussMatrix(x, 1);
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] inPeak = { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, outPeak = { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
		int size = width * height;
		int[] inPixels = new int[size];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[size];
		for (int i = 0; i < size; i++) {
			inPeak[0] = Math.max(inPeak[0], Color.red(inPixels[i]));
			inPeak[1] = Math.max(inPeak[1], Color.green(inPixels[i]));
			inPeak[2] = Math.max(inPeak[2], Color.blue(inPixels[i]));
		}

		int idx = 0;

		for (int i = x, length = height - x; i < length; i++) {
			for (int k = x, len = width - x; k < len; k++) {

				idx = 0;
				// 卷积
				for (int m = -x; m <= x; m++) {
					for (int n = -x; n <= x; n++) {
						pixColor = inPixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = (int) (newR + (pixR * matrix[idx]));
						newG = (int) (newG + (pixG * matrix[idx]));
						newB = (int) (newB + (pixB * matrix[idx]));
						idx++;
					}
				}
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
				outPeak[0] = Math.max(outPeak[0], newR);
				outPeak[1] = Math.max(outPeak[1], newG);
				outPeak[2] = Math.max(outPeak[2], newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		for (int i = 0; i < size; i++) {// 卷积结果归一化处理
			pixR = (int) (1.0f * Color.red(outPixels[i]) * inPeak[0] / outPeak[0]);
			pixG = (int) (1.0f * Color.green(outPixels[i]) * inPeak[1] / outPeak[1]);
			pixB = (int) (1.0f * Color.blue(outPixels[i]) * inPeak[2] / outPeak[2]);
			outPixels[i] = Color.argb(Color.alpha(outPixels[i]), pixR, pixG, pixB);
		}
		// 边缘处理（直接填充像素）
		edge(outPixels, width, height, x);

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 根据两维的高斯分布函数（标准正态分布）计算出高斯矩阵
	 */
	public static float[] getGaussMatrix(int x, float sigma) {
		int size = 2 * x + 1;
		float sigma22 = 2 * sigma * sigma;
		float sigma22PI = (float) (Math.PI * sigma22);
		float[] matrix = new float[size * size];
		int index = 0;
		for (int i = -x; i <= x; i++) {
			for (int j = -x; j <= x; j++) {
				float xDistance = i * i;
				float yDistance = j * j;
				matrix[index] = (float) (Math.exp(-(xDistance + yDistance) / sigma22) / sigma22PI);
				index++;
			}
		}
		return matrix;
	}

	/**
	 * 边缘处理（直接填充处理过的边缘像素，不太好的处理方式，窗口半径较大时会看到处理后的图片边缘有点不自然）
	 * 
	 * @param outPixels
	 * @param width
	 * @param height
	 * @param x
	 */
	private static void edge(int[] outPixels, int width, int height, int x) {
		int size = width * height;
		for (int i = 0; i < x * width; i++) {
			outPixels[i] = outPixels[i % width + x * width];
		}
		for (int i = size - x * width; i < size; i++) {
			outPixels[i] = outPixels[i % width + size - (x + 1) * width];
		}
		for (int i = 0; i < height; i++) {
			int pix = outPixels[i * width + x];
			for (int j = i * width; j < width * i + x; j++)
				outPixels[j] = pix;
			pix = outPixels[(i + 1) * width - x - 1];
			for (int j = (i + 1) * width - x; j < (i + 1) * width; j++) {
				outPixels[j] = pix;
			}
		}
	}

	/**
	 * 均值模糊（主要用于简单去噪）
	 * 
	 * @param bmp
	 * @param x
	 *            矩阵的宽度=2*x+1.x越大、该函数越耗时。x可以看成窗口半径
	 * @return
	 */
	public static Bitmap boxBlur(Bitmap bmp, int x) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int tableSize = (2 * x + 1);
		int decay = tableSize * tableSize;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;

		int size = width * height;
		int[] inPixels = new int[size];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[size];

		for (int i = x, length = height - x; i < length; i++) {
			for (int k = x, len = width - x; k < len; k++) {

				for (int m = -x; m <= x; m++) {
					for (int n = -x; n <= x; n++) {
						pixColor = inPixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR += pixR;
						newG += pixG;
						newB += pixB;
					}
				}

				newR = (int) (1.0f * newR / decay);
				newG = (int) (1.0f * newG / decay);
				newB = (int) (1.0f * newB / decay);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		edge(outPixels, width, height, x);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 快速均值模糊，均值模糊的高效算法,主要用于做特效（如毛玻璃）。
	 * 
	 * @param bmp
	 * @param x
	 *            可以看成窗口半径
	 * @return
	 */
	public static Bitmap fastBlur(Bitmap bmp, int x) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int tableSize = (2 * x + 1);
		int decay = tableSize * tableSize;

		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;

		int size = width * height;
		int[] inPixels = new int[size];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[size];
		int[][] A = new int[3][width];// 保存每一列的rgb值

		// 初始化A
		for (int j = 0; j < width; j++) {
			for (int i = 0; i < tableSize; i++) {
				pixColor = getXY(inPixels, width, i, j);
				A[0][j] += Color.red(pixColor);
				A[1][j] += Color.green(pixColor);
				A[2][j] += Color.blue(pixColor);
			}
		}

		for (int i = x, length = height - x; i < length; i++) {
			if (i != x) {
				// 更新A
				for (int j = 0; j < width; j++) {
					A[0][j] = A[0][j] - Color.red(getXY(inPixels, width, i - x - 1, j)) + Color.red(getXY(inPixels, width, i + x, j));
					A[1][j] = A[1][j] - Color.green(getXY(inPixels, width, i - x - 1, j)) + Color.green(getXY(inPixels, width, i + x, j));
					A[2][j] = A[2][j] - Color.blue(getXY(inPixels, width, i - x - 1, j)) + Color.blue(getXY(inPixels, width, i + x, j));
				}
			}

			int rsum = 0, gsum = 0, bsum = 0;
			for (int m = 0; m < tableSize; m++) {// 矩阵窗口各个元素之和
				rsum += A[0][m];
				gsum += A[1][m];
				bsum += A[2][m];
			}

			for (int k = x, len = width - x; k < len; k++) {
				if (k != x) {
					rsum = rsum - A[0][k - 1 - x] + A[0][k + x];
					gsum = gsum - A[1][k - 1 - x] + A[1][k + x];
					bsum = bsum - A[2][k - 1 - x] + A[2][k + x];
				}

				newR = (int) (1.0f * rsum / decay);
				newG = (int) (1.0f * gsum / decay);
				newB = (int) (1.0f * bsum / decay);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
			}
		}
		edge(outPixels, width, height, x);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private static int getXY(int[] a, int width, int x, int y) {
		return a[x * width + y];
	}

	/**
	 * 拉普拉斯锐化
	 * 
	 * @param bmp
	 * @param decay
	 *            衰减因子，值越小，图片会越亮
	 * @return
	 */
	public static Bitmap laplaceSharpen(Bitmap bmp, float decay) {
		if (bmp == null)
			return null;
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 8, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		int[] inPixels = new int[width * height];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[width * height];
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				newR = 0;
				newG = 0;
				newB = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = inPixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + pixR * laplacian[idx];
						newG = newG + pixG * laplacian[idx];
						newB = newB + pixB * laplacian[idx];
						idx++;
					}
				}

				pixColor = inPixels[i * width + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) ((pixR + newR) / decay);
				newG = (int) ((pixG + newG) / decay);
				newB = (int) ((pixB + newB) / decay);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				outPixels[i * width + k] = Color.argb(Color.alpha(inPixels[i * width + k]), newR, newG, newB);
			}
		}
		edge(outPixels, width, height, 1);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 描边(Soble算子描边)
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap sobleStroke(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] sobleX = { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
		int[] sobleY = { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		int[] inPixels = new int[width * height];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[width * height];
		for (int i = 1, length = height - 1; i < length; i++) {
			for (int k = 1, len = width - 1; k < len; k++) {
				idx = 0;
				int xr = 0, xg = 0, xb = 0, yr = 0, yg = 0, yb = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixColor = inPixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						xr += pixR * sobleX[idx];
						xg += pixG * sobleX[idx];
						xb += pixB * sobleX[idx];

						yr += pixR * sobleY[idx];
						yg += pixG * sobleY[idx];
						yb += pixB * sobleY[idx];

						idx++;
					}
				}

				newR = (int) Math.sqrt(xr * xr + yr * yr);
				newG = (int) Math.sqrt(xg * xg + yg * yg);
				newB = (int) Math.sqrt(xb * xb + yb * yb);

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				// 反色
				newR = 255 - newR;
				newG = 255 - newG;
				newB = 255 - newB;

				// 黑白去色
				newR = (int) (0.299 * newR + 0.587 * newG + 0.114 * newB);
				newG = newB = newR;

				idx = i * width + k;
				outPixels[idx] = Color.argb(Color.alpha(inPixels[idx]), newR, newG, newB);
			}
		}
		edge(outPixels, width, height, 1);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		// bitmap=laplaceSharpen(bitmap, 1);//锐化
		// bitmap=fastBlur(bitmap, 1);//模糊
		return bitmap;
	}

	/**
	 * 素描（铅笔画），未完成，目前返回的是轮廓图片2014-1-23
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap pencilSketch(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int pixColor1 = 0, pixColor2 = 0, pixColor3 = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] inPixels = new int[width * height];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixels = new int[width * height];
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int idx = i * width + k;
				if (i == height - 1 || k == width - 1) {// 边界
					newR = newG = newB = 255;
				} else {
					pixColor1 = getXY(inPixels, width, i, k);
					pixColor2 = getXY(inPixels, width, i, k + 1);
					pixColor3 = getXY(inPixels, width, i + 1, k);

					int r12 = Color.red(pixColor1) - Color.red(pixColor2);
					int r13 = Color.red(pixColor1) - Color.red(pixColor3);
					newR = (int) (2 * Math.sqrt(r12 * r12 + r13 * r13));

					int g12 = Color.green(pixColor1) - Color.green(pixColor2);
					int g13 = Color.green(pixColor1) - Color.green(pixColor3);
					newG = (int) (2 * Math.sqrt(g12 * g12 + g13 * g13));

					int b12 = Color.blue(pixColor1) - Color.blue(pixColor2);
					int b13 = Color.blue(pixColor1) - Color.blue(pixColor3);
					newB = (int) (2 * Math.sqrt(b12 * b12 + b13 * b13));

					newR = Math.min(255, Math.max(0, newR));
					newG = Math.min(255, Math.max(0, newG));
					newB = Math.min(255, Math.max(0, newB));

					// 反色
					newR = 255 - newR;
					newG = 255 - newG;
					newB = 255 - newB;

					// 黑白去色
					newR = (int) (0.299 * newR + 0.587 * newG + 0.114 * newB);
					newG = newB = newR;
				}
				outPixels[idx] = Color.argb(Color.alpha(inPixels[idx]), newR, newG, newB);
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixels, 0, width, 0, 0, width, height);
		// bitmap = laplaceSharpen(bitmap, 1);// 锐化
		// bitmap = fastBlur(bitmap, 1);// 模糊
		return bitmap;
	}

	/**
	 * 素描（简单素描）
	 * 
	 * @param bmp
	 * @param value
	 *            调节笔墨浓度，值越大，色彩越浓
	 * @return
	 */
	public static Bitmap simpleSketch(Bitmap bmp, int value) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] inPixels = new int[width * height];
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		int[] outPixelsA = new int[width * height];
		int[] outPixelsB = new int[width * height];
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int idx = i * width + k;

				int pixColor = inPixels[idx];
				// 黑白去色
				newG = newB = newR = (int) (0.299 * Color.red(pixColor) + 0.587 * Color.green(pixColor) + 0.114 * Color.blue(pixColor));
				outPixelsA[idx] = Color.argb(Color.alpha(inPixels[idx]), newR, newG, newB);

				// 反色
				newR = 255 - newR;
				newG = 255 - newG;
				newB = 255 - newB;

				outPixelsB[idx] = Color.argb(Color.alpha(inPixels[idx]), newR, newG, newB);
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(outPixelsB, 0, width, 0, 0, width, height);

		bitmap = fastBlur(bitmap, value);// 模糊
		bitmap.getPixels(outPixelsB, 0, width, 0, 0, width, height);

		// 颜色减淡
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				int idx = i * width + k;
				int pixA = outPixelsA[idx];
				int pixB = outPixelsB[idx];
				newR = Color.red(pixA) + Color.red(pixA) * Color.red(pixB) / (255 - Color.red(pixB));
				newG = Color.green(pixA) + Color.green(pixA) * Color.green(pixB) / (255 - Color.green(pixB));
				newB = Color.blue(pixA) + Color.blue(pixA) * Color.blue(pixB) / (255 - Color.blue(pixB));

				newR = Math.max(0, Math.min(255, newR));
				newG = Math.max(0, Math.min(255, newG));
				newB = Math.max(0, Math.min(255, newB));

				inPixels[idx] = Color.argb(Color.alpha(inPixels[idx]), newR, newG, newB);
			}
		}
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 浮雕
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap relief(Bitmap bmp) {
		if (bmp == null)
			return null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 0; i < height - 1; i++) {
			for (int k = 0; k < width - 1; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				pixColor = pixels[pos + 1];
				newR = Color.red(pixColor) - pixR + 128;
				newG = Color.green(pixColor) - pixG + 128;
				newB = Color.blue(pixColor) - pixB + 128;
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(Color.alpha(pixels[pos]), newR, newG, newB);
			}
		}

		edge(pixels, width, height, 1);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 胶片(反色)
	 * 
	 * @param bmp
	 * @return
	 */
	public static Bitmap film(Bitmap bmp) {
		if (bmp == null)
			return null;
		// RGBA 的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(Color.alpha(pixels[pos]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 光照效果
	 * 
	 * @param bmp
	 * @param strength
	 *            光照强度100~150
	 * @return
	 */
	public static Bitmap light(Bitmap bmp, float strength) {
		if (bmp == null)
			return null;
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int centerX = width / 2;
		int centerY = height / 2;
		int radius = Math.min(centerX, centerY);

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 0; i < height; i++) {
			for (int k = 0; k < width; k++) {
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));
				if (distance < radius * radius) {
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(Color.alpha(pixels[pos]), newR, newG, newB);
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	/**
	 * 判断图片名称是否是支持的图片后缀
	 */
	public static boolean isImage(String name) {
		if (TextUtils.isEmpty(name))
			return false;
		if (name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".jpeg") || name.endsWith(".JPEG") || name.endsWith(".png") || name.endsWith(".PNG"))
			return true;
		return false;
	}

	/**
	 * 返回bitmap的bytes数组的总数
	 */
	public static int getBitmapBytesCount(Bitmap bmp) {
		if (bmp == null)
			return 0;
		return bmp.getRowBytes() * bmp.getHeight();
	}

	/**
	 * 获取bitmap的宽高及字节总数
	 * 
	 * @param bmp
	 * @return
	 */
	public static String getBitmapInfo(Bitmap bmp) {
		if (bmp == null)
			return null;
		return String.format("width=%d,height=%d,width*height=%d,bytes=%d(b)=%d(kb)", bmp.getWidth(), bmp.getHeight(), bmp.getWidth() * bmp.getHeight(), getBitmapBytesCount(bmp),
				getBitmapBytesCount(bmp) / 1024);
	}

	/**
	 * 通过像素比较两个bitmap是否相等
	 * 
	 * @param bmp1
	 * @param bmp2
	 * @return
	 */
	public static boolean compareBitmap(Bitmap bmp1, Bitmap bmp2) {
		if (bmp1 == null || bmp2 == null) {
			return false;
		}
		int w1 = bmp1.getWidth();
		int w2 = bmp2.getWidth();
		int h1 = bmp1.getHeight();
		int h2 = bmp2.getHeight();
		if (w1 != w2 || h1 != h2)
			return false;
		for (int i = 0; i < w1; i++)
			for (int j = 0; j < h1; j++) {
				if (bmp1.getPixel(i, j) != bmp2.getPixel(i, j))
					return false;
			}
		return true;
	}

	public static Bitmap getBitmap(int ResId, Context context) {
		return BitmapFactory.decodeStream(context.getResources().openRawResource(ResId));
	}

	public static String imageUri2Path(Uri uri, Context context) {
		if (uri.getScheme().compareTo("file") == 0) {
			String res = uri.toString().replace("file://", "");
			return res;
		}

		ContentResolver resolver = context.getContentResolver();
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = resolver.query(uri, proj, null, null, null);
		int index = cursor.getColumnIndexOrThrow(proj[0]);
		cursor.moveToFirst();
		String path = cursor.getString(index);

		if (cursor != null)
			cursor.close();

		return path;
	}

	/**
	 * 默认 100*100
	 * 
	 * @param uri
	 * @param context
	 * @return
	 */
	public static Bitmap getBitmapFromUri(Uri uri, Context context, int maxBytesCount) {
		return getBitmap(imageUri2Path(uri, context), maxBytesCount);
	}

	/**
	 * 从uri获取图片
	 * 
	 * @param uri
	 * @param context
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmapFromUri(Uri uri, Context context, int width, int height) {
		return getBitmap(imageUri2Path(uri, context), width, height);
	}
	public static Bitmap getBitmapFromUriAdjustOritation(Uri uri, Context context, int width, int height) {
		String imgpath=imageUri2Path(uri, context);
		Bitmap bm = getBitmap(imgpath, width, height);
		return adjustOritation(bm, imgpath);
	}
	public static Bitmap getBitmapAdjustOritation(String imgpath, int width, int height) {
		Bitmap bm = getBitmap(imgpath, width, height);
		return adjustOritation(bm, imgpath);
	}

	private static Bitmap adjustOritation(Bitmap bm, String path) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (bm == null || exif == null) {
			return bm;
		}
		int digree = 0;
		// 读取图片中相机方向信息
		int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_UNDEFINED);
		// 计算旋转角度
		switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
		}
		if (digree != 0) {
			// 旋转图片
			Matrix m = new Matrix();
			m.postRotate(digree);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
					m, true);
		}
		return bm;
	}
}
