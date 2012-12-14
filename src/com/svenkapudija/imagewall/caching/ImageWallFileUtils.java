package com.svenkapudija.imagewall.caching;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

import com.svenkapudija.android.fileutils.AndroidFileUtils;

public class ImageWallFileUtils extends AndroidFileUtils {

	private static final String IMAGES_DIRECTORY = "images";	
	private StorageOption storageOption = StorageOption.SD_CARD_APP_ROOT;
	private String imageNameFormat = "image_%d.jpg";
	
	public ImageWallFileUtils(Context context) {
		super(context);
	}
	
	public Bitmap getImage(int imageId) {
		return getBitmap(storageOption, IMAGES_DIRECTORY, String.format(imageNameFormat, imageId));
	}

	public void writeImage(Bitmap image, int imageId) throws IOException {
		write(image, storageOption, IMAGES_DIRECTORY, String.format(imageNameFormat, imageId));
	}
	
	public boolean existsImage(int imageId) {
		return exists(storageOption, IMAGES_DIRECTORY, String.format(imageNameFormat, imageId));
	}
}
