package com.frame.sdk.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.frame.sdk.file.FileManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 打开其他应用的工具来
 */
public class OpenOtherAppUtil {

    /**
     * 复制文件
     *
     * @param sourceFilePath
     * @param targetFilePath
     */
    public static void copyFile(String sourceFilePath, String targetFilePath) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(
                    new FileInputStream(sourceFilePath));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(
                    targetFilePath));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (inBuff != null)
                    inBuff.close();
                if (outBuff != null)
                    outBuff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static byte[] getFile(String path) throws IOException {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        FileInputStream fs = new FileInputStream(file);
        BufferedInputStream buf = new BufferedInputStream(fs);
        buf.read(bytes, 0, bytes.length);
        buf.close();
        return bytes;
    }


    public static File saveBytesToFile(byte[] bytes, String path) {
        BufferedOutputStream stream = null;
        File file = null;
        file = new File(path);
        FileOutputStream fstream;
        try {
            fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.e("error!", e.getMessage(), e);
            return null;
        }
        return bitmap;
    }

    public static Bitmap getBitmap(String imgPath) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(imgPath);
        return bitmap;
    }

    /**
     * 根据uri从文件中缩放的获取图片
     */
    public static Bitmap getBitmap(Context context, Uri uri, int requiredWidth,
                                   int requiredHeight) {
        ContentResolver cr = context.getContentResolver();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
            options.inSampleSize = calculateInSampleSize(options,
                    requiredWidth, requiredHeight);
            Log.i("getBitmap by uri", "options.inSampleSize=="
                    + options.inSampleSize);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
                    options);
        } catch (FileNotFoundException e) {
            Log.e("error!", e.getMessage(), e);
        }
        LogUtils.i("bitmap.info==" + getBitmapInfo(bitmap));
        return bitmap;

    }

    /**
     * 根据图片路径从文件中缩放的获取图片
     */
    public static Bitmap getBitmap(String imgPath, int requiredWidth,
                                   int requiredHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = null;
        try {
            BitmapFactory.decodeFile(imgPath, options);
            options.inSampleSize = calculateInSampleSize(options,
                    requiredWidth, requiredHeight);
            Log.i("getBitmap by imgPath", "options.inSampleSize=="
                    + options.inSampleSize);
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imgPath, options);
        } catch (Exception e) {
            Log.e("error!", e.getMessage(), e);
        }
        Log.i("getBitmap by imgPath", "bitmap.info==" + getBitmapInfo(bitmap));
        return bitmap;
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int wScale = (int) Math.ceil(1.0 * width / reqWidth);
        int yScale = (int) Math.ceil(1.0 * height / reqHeight);
        inSampleSize = wScale > yScale ? wScale : yScale;

        if (inSampleSize < 1) {
            inSampleSize = 1;
        }
        return inSampleSize;
    }

    /**
     * 获取调整角度后的图片
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmapAdjustOritation(String imgPath) {
        Bitmap bm = getBitmap(imgPath);
        return AdjustOritation(bm, imgPath);
    }

    private static Bitmap AdjustOritation(Bitmap bm, String path) {
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

    /**
     * 获取调整角度后的图片
     *
     * @param imgPath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapAdjustOritation(String imgPath, int width, int height) {
        Bitmap bm = getBitmap(imgPath, width, height);
        return AdjustOritation(bm, imgPath);
    }

    /**
     * 获取调整角度后的图片
     *
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapAdjustOritation(Context context, Uri uri, int width,
                                                  int height) {
        Bitmap bm = getBitmap(context, uri, width, height);
        return AdjustOritation(bm, uri.getPath());

    }

    /**
     * 质量压缩图片
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
     * 保存bitmap到文件
     */
    public static boolean saveBitmapToFile(String filePath, Bitmap bitmap) {
        if (bitmap == null)
            return false;
        File bitmapFile = new File(filePath);
        try {
            if (bitmapFile.exists())
                bitmapFile.delete();
            bitmapFile.createNewFile();
            FileOutputStream fos;
            fos = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String imageUri2Path(Uri uri, Context context) {
        if (uri.getScheme().compareTo("file") == 0) {
            String res = uri.toString().replace("file://", "");
            return res;
        }

        ContentResolver resolver = context.getContentResolver();
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        String path = null;
        try {
            cursor = resolver.query(uri, proj, null, null, null);
            if (cursor != null) {
                int index = cursor.getColumnIndex(proj[0]);
                cursor.moveToFirst();
                path = cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return path;
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
        return String.format(
                "width=%d,height=%d,width*height=%d,bytes=%d(b)=%d(kb)=%d(M)",
                bmp.getWidth(), bmp.getHeight(),
                bmp.getWidth() * bmp.getHeight(), getBitmapBytesCount(bmp),
                (int) (1.0f * getBitmapBytesCount(bmp) / 1024),
                (int) (1.0f * getBitmapBytesCount(bmp) / 1024 / 1024));
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


    /***
     * html、htm超文本文件、text等文本文件(预览的文本文件类型)
     */
    public static String textArray[][] = {{".htm", "text/html"},
            {".html", "text/html"}, {".c", "text/plain"},
            {".cpp", "text/plain"}, {".conf", "text/plain"},
            {".ini", "text/plain"}, {".dat", "text/plain"},
            {".data", "text/plain"}, {".java", "text/plain"},
            {".js", "application/x-javascript"}, {".log", "text/plain"},
            {".prop", "text/plain"}, {".rc", "text/plain"},
            {".sh", "text/plain"}, {".txt", "text/plain"},};

    // 声明图片后缀名数组
    public static String imgeArray[][] = {{".bmp", "0"}, {".dib", "1"},
            {".gif", "2"}, {".jfif", "3"}, {".jpe", "4"},
            {".jpeg", "5"}, {".jpg", "6"}, {".png", "7"},
            {".tif", "8"}, {".tiff", "9"}, {".ico", "10"}};
    // 声明audio后缀名数组
    public static String audioArray[][] = {{".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"}, {".ogg", "audio/ogg"},
            {".tta", "audio/tta"}, {".mpga", "audio/mpeg"},
            {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"},
            {".m3u", "audio/x-mpegurl"}, {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"},
            {".ape", "audio/ape"},};
    // 声明vedio后缀名数组
    public static String vedioArray[][] = {{".asx", "video/x-ms-asx"},
            {".asf", "video/x-ms-asf"}, {".avi", "video/x-msvideo"},
            {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"},
            {".mpa", "video/mpa"}, {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"}, {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"}, {".flv", "video/flv"},
            {".mp4", "audio/mp4"}, {".rmvb", "audio/rmvb"},
            {".rm", "audio/rm"}, {".mkv", "audio/mkv"},
            {".3gp", "audio/3gp"}, {".dat", "audio/dat"},
            {".wmv", "audio/x-ms-wmv"}, {".wvx", "audio/x-ms-wvx"},};

    /**
     * 判断文件是否为对应后缀名的文件或图片<br>
     * <br>
     *
     * @param pInput    文件名<br>
     * @param pImgeFlag 判断具体文件类型<br>
     * @return 检查后的结果<br>
     * @throws Exception
     */
    public static boolean isContainsFile(String pInput, String fileArray[][],
                                         String pImgeFlag) {
        // 文件名称为空的场合
        if (TextUtils.isEmpty(pInput)) {
            // 返回不和合法
            return false;
        }
        // 获得文件后缀名
        String tmpName = "";
        int index = pInput.lastIndexOf(".");
        if (index >= 0) {
            tmpName = pInput.substring(index, pInput.length());
        }

        // 遍历名称数组
        for (int i = 0; i < fileArray.length; i++) {
            // 判断单个类型文件的场合
            if (!TextUtils.isEmpty(pImgeFlag)
                    && fileArray[i][0].equals(tmpName.toLowerCase())
                    && fileArray[i][1].equals(pImgeFlag)) {
                return true;
            }
            // 判断符合全部类型的场合
            if (TextUtils.isEmpty(pImgeFlag)
                    && fileArray[i][0].equals(tmpName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    /**
     * android获取一个用于打开PPT文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    /**
     * android获取一个用于打开Excel文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getExcelFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    /**
     * android获取一个用于打开Word文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    /***
     * android获取一个用于打开CHM文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    /***
     * android获取一个用于打开文本文件的intent
     *
     * @param param
     * @param paramBoolean
     * @return
     */
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(Uri.encode(param));
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    /***
     * android获取一个用于打开PDF文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /***
     * android获取一个用于打开图片的intent
     *
     * @param param
     * @return
     */
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    /***
     * android获取一个用于打开音频文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getAudioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    /***
     * android获取一个用于打开视频、影像文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getVedioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    /***
     * android获取一个用于打开任何文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getAnyFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    /***
     * android获取安装apk 的intent
     *
     * @return
     */
    public static Intent getApkFileIntent(File apkFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apkFile),
                "application/vnd.android.package-archive");
        return intent;
    }

    // 打开照相机页面
    public static String getPhotoFromCamera(Context context, int requestCode) {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileManager.getImageDir(),
                System.currentTimeMillis() + "penderie_camera.jpg");
        Uri targetFileUri = null;
        if (file != null) {
            targetFileUri = Uri.fromFile(file);
        }
        // 在SDCard 创建文件失败。
        if (targetFileUri == null) {
            return null;
        }
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, targetFileUri);
        ((Activity) context).startActivityForResult(intent, requestCode);
        return file.getPath();
    }

    /**
     * 选择 图片
     */
    public static void getPhotoFromLibrary(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 选择 文件
     */
    public static void getFileFromLibrary(Context context, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            ((Activity) context).startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    requestCode);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public static void getVideoFromLibrary(Context context, int requestCode) {
        Intent intent = new Intent();
        intent.setType("video/*"); // 选择视频 （mp4 3gp 是android支持的视频格式）
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    // 调用图片剪辑程序
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("noFaceDetection", true);// 取消人脸识别功能
        intent.putExtra("return-data", true);
        return intent;
    }

    public static void getCropBigImageUri(Context context, Uri uri, int outputX,
                                          int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

}
