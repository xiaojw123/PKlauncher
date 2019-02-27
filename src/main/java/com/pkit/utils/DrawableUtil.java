package com.pkit.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

public class DrawableUtil {
	private static ImageLoader loader;
	private static final int CACHE_AGE = 24 * 60 * 60;

	public static void displayDrawable(String uri, int loadingImgRes, ImageView view, int radius) {
		initLoader(view.getContext());
		@SuppressWarnings("deprecation")
		File bmpFile = loader.getDiskCache().get(uri);
		if (bmpFile == null || !bmpFile.exists()) {
			DisplayImageOptions options = buildOption(0, loadingImgRes);
			loader.displayImage(uri, view, options);
		} else {
			String fileUri = "file://" + bmpFile.getAbsolutePath();
			loader.displayImage(fileUri, view);
		}
	}

	public static void displayDrawable(String uri, int loadingImgRes, ImageView view, int radius, ImageLoadingListener listener) {
		initLoader(view.getContext());
		DisplayImageOptions options = buildOption(0, loadingImgRes);
		loader.displayImage(uri, view, options, listener);
	}

	private static void initLoader(Context context) {
		if (loader != null) {
			return;
		}
		loader = ImageLoader.getInstance();
		if (!loader.isInited()) {
			DiskCache cache = new LimitedAgeDiscCache(getCacheDir(), null, new Md5FileNameGenerator(), CACHE_AGE);
			ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
			builder.threadPriority(Thread.NORM_PRIORITY);
			builder.denyCacheImageMultipleSizesInMemory();
			builder.diskCacheFileNameGenerator(new Md5FileNameGenerator());
			builder.diskCache(cache);
			builder.threadPoolSize(2);
			builder.tasksProcessingOrder(QueueProcessingType.LIFO);
			ImageLoaderConfiguration config = builder.build();
			loader.init(config);
		}
	}

	public static DisplayImageOptions buildOption(int cornerRadiusPixels, int imgRes) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		if (imgRes != -1) {
			builder.showImageOnLoading(imgRes);
			builder.showImageOnFail(imgRes);
			builder.showImageForEmptyUri(imgRes);
		}
		builder.cacheInMemory(false);
		builder.cacheOnDisk(true);
		builder.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels));
		DisplayImageOptions options = builder.build();
		return options;
	}

	private static File getCacheDir() {
		File storyageDir = Environment.getExternalStorageDirectory();
		File cacheDir = new File(storyageDir, "PKLauncher_Img");
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		return cacheDir;
	}
}
