package com.svenkapudija.imagewall.caching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.svenkapudija.android.fileutils.AndroidFileUtils;

public class ImageWallFileUtils extends AndroidFileUtils {

	private static final String IMAGES_DIRECTORY = "images";	
	private StorageOption storageOption = StorageOption.SD_CARD_APP_ROOT;
	private String imageNameFormat = "%d.jpg";
	
	public ImageWallFileUtils(Context context) {
		super(context);
	}
	
	public List<Integer> getImagesIds() {
		String[] fileNames = getFileNames(storageOption, IMAGES_DIRECTORY);
		List<Integer> ids = new ArrayList<Integer>();
		
		if(fileNames == null) {
			return ids;
		}
		
		for(String fileName : fileNames) {
			int id = 0;
			try {
				String trimmedExtension = fileName.substring(0, fileName.lastIndexOf('.'));
				id = Integer.parseInt(trimmedExtension);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
			if(id != 0) {
				ids.add(id);
			}
		}
		
		return ids;
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
