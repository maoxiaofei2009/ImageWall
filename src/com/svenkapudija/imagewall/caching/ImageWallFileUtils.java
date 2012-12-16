package com.svenkapudija.imagewall.caching;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;

import com.svenkapudija.android.fileutils.AndroidFileUtils;

public class ImageWallFileUtils extends AndroidFileUtils {

	private static final String IMAGE_TO_UPLOAD_DIRECTORY = "imageToUpload";
	private static final String IMAGES_DIRECTORY = "original";	
	private static final String THUMBNAILS_DIRECTORY = "thumbnail";
	
	private StorageOption storageOption = StorageOption.SD_CARD_APP_ROOT;
	
	public ImageWallFileUtils(Context context) {
		super(context);
	}
	
	public Bitmap getThumbnail(String imageName) {
		return getBitmap(storageOption, THUMBNAILS_DIRECTORY, imageName);
	}
	
	public Bitmap getImage(String imageName) {
		return getBitmap(storageOption, IMAGES_DIRECTORY, imageName);
	}
	
	public Bitmap getImageToUpload() {
		return getBitmap(storageOption, IMAGE_TO_UPLOAD_DIRECTORY, "myImage.jpg");
	}
	
	public Bitmap getImageToUploadThumbnail() {
		return getBitmap(storageOption, IMAGE_TO_UPLOAD_DIRECTORY, "myImage_thumbnail.jpg");
	}

	public void writeImageToUploadThumbnail(Bitmap image) throws IOException {
		write(image, storageOption, IMAGE_TO_UPLOAD_DIRECTORY, "myImage_thumbnail.jpg");
	}
	
	public String getImagePath(String imageName) {
		return generatePath(storageOption, IMAGES_DIRECTORY, imageName);
	}
	
	public void writeImage(Bitmap image, String imageName) throws IOException {
		write(image, storageOption, IMAGES_DIRECTORY, imageName);
	}
	
	public void writeThumbnail(Bitmap image, String imageName) throws IOException {
		write(image, storageOption, THUMBNAILS_DIRECTORY, imageName);
	}
	
	public boolean existsImage(String imageName) {
		return exists(storageOption, IMAGES_DIRECTORY, imageName);
	}
	
	public boolean existsThumbnail(String imageName) {
		return exists(storageOption, THUMBNAILS_DIRECTORY, imageName);
	}
}
