package com.svenkapudija.imagewall.caching;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class AndroidFileUtils {

	private String applicationName;
	private File internalMemoryRoot;
	private File sdCardAppRoot;
	
	public AndroidFileUtils(Context context) {
		applicationName = getApplicationName(context);
		internalMemoryRoot = context.getFilesDir();
		sdCardAppRoot = context.getExternalFilesDir(null);
	}

	private String getApplicationName(Context context) {
		final PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo(context.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		
		return (String) (ai != null ? pm.getApplicationLabel(ai) : "unknown_app");
	}
	
	public File getFile(StorageOption option, String directory, String fileName) {
		File destFile = new File(generatePath(option, directory, fileName));
		if(destFile.exists() == false)
			return null;
		
		return destFile;
	}
	
	public String[] getFileNames(StorageOption option, String directory) {
		File mainDirectory = new File(generatePath(option, directory, null));
		if(mainDirectory.exists() == false)
			return null;
		
		if(mainDirectory.isDirectory() == false)
			return null;
		
		return mainDirectory.list();
	}
	
	public int getCount(StorageOption option, String directory) {
		File mainDirectory = new File(generatePath(option, directory, null));
		if(mainDirectory.exists() == false)
			return 0;
		
		if(mainDirectory.isDirectory() == false)
			return 0;
		
		return mainDirectory.listFiles().length;
	}
	
	public Bitmap getBitmap(StorageOption option, String directory, String fileName) {
		File destFile = new File(generatePath(option, directory, fileName));
		if(destFile.exists() == false)
			return null;
		
		return decodeFile(destFile);
	}
	
	public Bitmap getBitmap(StorageOption option, String directory, int index) {
		File mainDirectory = new File(generatePath(option, directory, null));
		if(mainDirectory.exists() == false)
			return null;
		
		if(mainDirectory.isDirectory() == false)
			return null;
		
		if(index >= mainDirectory.listFiles().length)
			return null;
		
		return decodeFile(mainDirectory.listFiles()[index]);
	}
	
	public List<Bitmap> getBitmaps(StorageOption option, String directory) {
		List<Bitmap> images = new ArrayList<Bitmap>();
		
		File mainDirectory = new File(generatePath(option, directory, null));
		if(mainDirectory.exists() == false)
			return null;
		
		if(mainDirectory.isDirectory() == false)
			return null;
		
		for (File child : mainDirectory.listFiles()) {
			Bitmap image = decodeFile(child);
			if(image != null) {
				images.add(image);
			}
		}
		
		return images;
	}
	
	private Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE = 400;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	public void write(Bitmap image, StorageOption option, String directory, String fileName) throws IOException {
		write(convertToByteArray(image), option, directory, fileName);
	}

	public void write(File sourceFile, StorageOption option, String directory, String fileName) throws IOException {
		write(convertToByteArray(sourceFile), option, directory, fileName);
	}
	
	private void write(byte[] bytes, StorageOption option, String directory, String fileName) throws IOException {
		String path = generatePath(option, directory, fileName);
		
		File destFile = new File(path);
		new File(destFile.getParent()).mkdirs();
		
		writeByteArrayToFile(destFile, bytes);
	}
	
	private byte[] convertToByteArray(Bitmap image) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, bytes);
		return bytes.toByteArray();
	}
	
	private byte[] convertToByteArray(File file) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream bos = null;
		
		try {
			is = new FileInputStream(file);
			bos = new ByteArrayOutputStream();

			byte[] b = new byte[1024];

			int bytesRead = -1;
			while ((bytesRead = is.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			
			if (bos != null) {
				bos.close();
			}
		}

		return bos != null ? bos.toByteArray() : null;
	}
	
    private static void writeByteArrayToFile(File file, byte[] data) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            out.write(data);
            out.close();
        } finally {
        	if (out != null) {
            	out.close();
            }
        }
    }

	protected String generatePath(StorageOption option, String directory, String fileName) {
		String path = "";
		if(option == null || option == StorageOption.SD_CARD_APP_ROOT) {
			path += sdCardAppRoot.getAbsolutePath();
		} else if(option == StorageOption.SD_CARD_ROOT) {
			path += Environment.getExternalStorageDirectory().getAbsolutePath();
			path += "/" + applicationName;
		} else {
			path += internalMemoryRoot.getAbsolutePath();
		}
		
		if(directory != null && directory.trim().length() > 0) {
			path += "/" + directory;
		}
		
		if(fileName != null && fileName.trim().length() > 0) {
			path += "/" + fileName;
		}
		
		return path;
	}
	
	public boolean exists(StorageOption option, String directory, String fileName) {
		File destFile = new File(generatePath(option, directory, fileName));
		return destFile.exists();
	}
	
	public enum StorageOption {

		SD_CARD_ROOT,
		SD_CARD_APP_ROOT,
		INTERNAL_MEMORY
		
	}

}
