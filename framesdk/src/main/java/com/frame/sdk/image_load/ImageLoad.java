package com.frame.sdk.image_load;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.frame.sdk.async.AsyncPool;
import com.frame.sdk.async.ExecuteTask;
import com.frame.sdk.async.TaskListener;
import com.frame.sdk.file.FileManager;
import com.frame.sdk.http.HttpUtil;
import com.frame.sdk.util.ImageUtil;
import com.frame.sdk.util.LogUtils;
import com.frame.sdk.util.ScreenUtil;

public class ImageLoad {

	public static void loadImage(final String imgUrl, TaskListener taskListener) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
		loadImage(imgUrl, taskListener, new Options());
	}

	public static void loadImage(final String imgUrl, TaskListener taskListener, final Options options) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
		ExecuteTask task = new ExecuteTask(taskListener) {
			@Override
			public Object onDo() {
				final String imgName = getImageName(imgUrl);
				Bitmap bitmap = null;
				if (options.isLoadFromMemory) {
					bitmap = ImageCache.getBitmapFromMemory(imgName);
				}
				if (bitmap == null) {
					if (options.isLoadFromFile)
						bitmap = ImageCache.getBitmapFromFile(imgName);
				}
				if (bitmap != null) {
					if (options.isRoundCorner)
						return ImageUtil.toRoundCorner(bitmap);
					return bitmap;
				}
				bitmap = HttpUtil.downloadImage(imgUrl);
				if (options.isSaveToMemory)
					ImageCache.saveBitmapToMemory(imgName, bitmap);
				if (options.isSaveToFile)
					ImageCache.saveBitmapToFile(imgName, bitmap);
				if (options.isRoundCorner) {
					return ImageUtil.toRoundCorner(bitmap);
				} else
					return bitmap;
			}
		};
		task.setPriority(ExecuteTask.LOW_PRIORITY);
		AsyncPool.getInstance().addTask(task);
	}

	public static void loadImage(final ImageView imageView, final String imgUrl) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
		loadImage(imageView, imgUrl, new Options());
	}

	public static void loadImage(final ImageView imageView, final String imgUrl, final Options options) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
		final String imgName = getImageName(imgUrl);
		LogUtils.i("imgURL==="+imgUrl);
		LogUtils.i("imgName==="+imgName);

//		imageView.setImageBitmap(null);
		Bitmap bitmap = null;
		final Drawable bgDrawable = imageView.getBackground();

		if (options.isLoadFromMemory) {
			bitmap = ImageCache.getBitmapFromMemory(imgName);
		}
		if (bitmap == null && options.isLoadFromFile)
			bitmap = ImageCache.getBitmapFromFile(imgName);
		if (bitmap != null) {
			if (options.isRoundCorner)
				showImage(imageView, ImageUtil.toRoundCorner(bitmap), options);
			else
				showImage(imageView, bitmap, options);
			return;
		}

		if (options.loadingBmp != null) {
			imageView.setImageBitmap(options.loadingBmp);
		}

		if (options.bgAnimId > 0) {
			imageView.setBackgroundResource(options.bgAnimId);
			AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
			if (anim != null)
				anim.start();
		}

		if (options.bgView != null) {
			options.bgView.setVisibility(View.VISIBLE);
			AnimationDrawable anim = (AnimationDrawable) options.bgView.getBackground();
			if (anim != null)
				anim.start();
		}

		TaskListener taskListener = new TaskListener() {
			@Override
			public void onComplete(Object result) {
				if (options.bgAnimId > 0) {
					AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
					if (anim != null)
						anim.stop();
					imageView.setBackgroundDrawable(bgDrawable);

				}
				if (options.bgView != null) {
					options.bgView.setVisibility(View.GONE);
					AnimationDrawable anim = (AnimationDrawable) options.bgView.getBackground();
					if (anim != null)
						anim.stop();
				}
				Bitmap bitmap = (Bitmap) result;
				showImage(imageView, bitmap, options);
			}
		};
		ExecuteTask task = new ExecuteTask(taskListener) {
			@Override
			public Object onDo() {
				Bitmap bmp = HttpUtil.downloadImage(imgUrl);
				if (bmp == null) {
					if (options.isRoundCorner) {
						return ImageUtil.toRoundCorner(options.defaultBmp);
					} else {
						return options.defaultBmp;
					}
				}
				if (options.isSaveToMemory)
					ImageCache.saveBitmapToMemory(imgName, bmp);
				if (options.isSaveToFile)
					ImageCache.saveBitmapToFile(imgName, bmp);
				if (options.isRoundCorner) {
					return ImageUtil.toRoundCorner(bmp);
				} else
					return bmp;
			}
		};
		task.setPriority(ExecuteTask.LOW_PRIORITY);
		AsyncPool.getInstance().addTask(task);
	}

	/**
	 * 加载大图
	 */
	public static void loadBigImage(final ImageView imageView, final String imgUrl) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
		loadBigImage(imageView, imgUrl, ScreenUtil.getScreenWidth(imageView.getContext()), ScreenUtil.getScreenHeight(imageView.getContext()), new Options());
	}

	/**
	 * 加载大图
	 */
	public static void loadBigImage(final ImageView imageView, final String imgUrl, final int width, final int height, final Options options) {
		if(TextUtils.isEmpty(imgUrl)){
			return;
		}
//		imageView.setImageBitmap(null);
		final Drawable bgDrawable = imageView.getBackground();

		if (options.loadingBmp != null) {
			imageView.setImageBitmap(options.loadingBmp);
		}
		if (options.bgAnimId > 0) {
			imageView.setBackgroundResource(options.bgAnimId);
			AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
			if (anim != null)
				anim.start();
		}

		if (options.bgView != null) {
			options.bgView.setVisibility(View.VISIBLE);
			AnimationDrawable anim = (AnimationDrawable) options.bgView.getBackground();
			if (anim != null)
				anim.start();
		}

		TaskListener taskListener = new TaskListener() {
			@Override
			public void onComplete(Object result) {
				if (options.bgAnimId > 0) {
					AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
					if (anim != null)
						anim.stop();
					imageView.setBackgroundDrawable(bgDrawable);
				}
				if (options.bgView != null) {
					options.bgView.setVisibility(View.GONE);
					AnimationDrawable anim = (AnimationDrawable) options.bgView.getBackground();
					if (anim != null)
						anim.stop();
				}
				Bitmap bitmap = (Bitmap) result;
				showImage(imageView, bitmap, options);
			}
		};
		ExecuteTask task = new ExecuteTask(taskListener) {
			@Override
			public Object onDo() {
				String imgName = getImageName(imgUrl);
				Bitmap bitmap = null;
				if (options.isLoadFromFile)
					bitmap = ImageUtil.getBitmap(FileManager.getImagePath(imgName), width, height);
				if (bitmap != null) {
					if (options.isRoundCorner)
						bitmap = ImageUtil.toRoundCorner(bitmap);
					return bitmap;
				}

				if (HttpUtil.downloadFile(imgUrl, FileManager.getImagePath(imgName)) == false)
					return null;
				bitmap = ImageUtil.getBitmap(FileManager.getImagePath(imgName), width, height);
				if (bitmap == null) {
					bitmap = options.defaultBmp;
				}
				if (bitmap != null) {
					if (options.isRoundCorner)
						bitmap = ImageUtil.toRoundCorner(bitmap);
				}
				return bitmap;
			}
		};
		task.setPriority(ExecuteTask.LOW_PRIORITY);
		AsyncPool.getInstance().addTask(task);
	}

	public static void showImage(ImageView imageView, Bitmap bmp, final Options options) {
		if (options.isFixCenter) {
			float sx = 1.0f * options.desWidth / bmp.getWidth();
			float sy = 1.0f * options.desHeight / bmp.getHeight();

			if (sx > sy) {
				bmp = ImageUtil.scaleBitmap(bmp, sx, sx);
			} else {
				bmp = ImageUtil.scaleBitmap(bmp, sy, sy);
			}
			int offX = (bmp.getWidth() - options.desWidth) / 2;
			int offY = (bmp.getHeight() - options.desHeight) / 2;
			bmp = ImageUtil.clipBitmap(bmp, new Rect(offX, offY, offX + options.desWidth, offY + options.desHeight));
		}
		imageView.setImageBitmap(bmp);
		if (options.anim != null) {
			imageView.setAnimation(options.anim);
			imageView.startAnimation(options.anim);
		}
		if (options.bgDrawable != null)
			imageView.setBackgroundDrawable(options.bgDrawable);
	}

	public static String getRoundImageName(String imgUrl) {
		String imgName = getImageName(imgUrl);
		if (ImageUtil.isImage(imgName)) {
			int index = imgName.lastIndexOf(".");
			imgName = imgName.substring(0, index) + "_r" + imgName.substring(index);
		} else
			imgName = imgName + "_r";
		return imgName;
	}

	public static String getImageName(String imgUrl) {
		int index = imgUrl.lastIndexOf("/");
		if (index >= 0)
			return imgUrl.substring(index + 1);
		return imgUrl;
	}

	public static class Options {
		public Animation anim;// 图片显示的动画
		public Drawable bgDrawable;// 背景图片
		public Bitmap defaultBmp;// 图片不存在时的默认图片
		public Bitmap loadingBmp;// 图片网络加载中显示的图片
		public boolean isLoadFromMemory = true;// 是否从内存加载
		public boolean isLoadFromFile = true;// 是否从文件加载
		public boolean isSaveToMemory = true;// 是否保存至内存
		public boolean isSaveToFile = true;// 是否保存至文件
		public boolean isRoundCorner = false;// 是否显示为圆角图片
		public int bgAnimId;// 背景是否是一个动画，加载中会播放这个动画
		public View bgView;// 用来播放加载中动画的view,把加载中的动画设为这个view的背景
		public boolean isFixCenter;// 是否是把bitmap适应ImageView显示
		public int desWidth;// bitmap目标的宽度
		public int desHeight;// bitmap目标的高度
	}
}
