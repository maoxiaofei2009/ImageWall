package com.svenkapudija.imagewall.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.models.Location;
import com.svenkapudija.imagewall.models.Tag;

/**
 * Wrapper for ImageWall REST API.
 */
public class ImageWallApi {

	private static final String API_BASE_URL = "http://team36.host25.com/api"; 
	
	private AsyncHttpClient httpClient;
	
	public ImageWallApi() {
		httpClient = new AsyncHttpClient();
		httpClient.addHeader("Accept", "application/json");
		httpClient.setTimeout(30000);
	}

	/**
	 * Get all images.
	 * 
	 * @param listener
	 * @param lastImageTimestamp
	 */
	public void getImages(final ImagesListener listener, Date ... lastImageTimestamp) {
		httpClient.get(API_BASE_URL + "/images", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Collection<Image> images = jsonToImagesCollection(result);
				listener.onSuccess(images);
			}

			@Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		});
	}
	
	/**
	 * Get all images with specific tag.
	 * 
	 * @param tag
	 * @param listener
	 * @param lastImageTimestamp
	 */
	public void getImages(Tag tag, final ImagesListener listener, Date ... lastImageTimestamp) {
		RequestParams params = new RequestParams();
		params.put("tag", tag.getValue());
		
		httpClient.get(API_BASE_URL + "/images", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Collection<Image> images = jsonToImagesCollection(result);
				listener.onSuccess(images);
			}
			
			@Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		});
	}
	
	/**
	 * Parse JSON to {@link Image} objects.
	 * 
	 * @param result
	 * @return
	 */
	private Collection<Image> jsonToImagesCollection(String result) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Collection<Image> images = new ArrayList<Image>();
		
		try {
			Type collectionType = new TypeToken<Collection<Image>>(){}.getType();
			images = gsonBuilder.create().fromJson(result, collectionType);
		} catch(JsonSyntaxException ignorable) {}
		
		return images;
	}
	
	/**
	 * Get all tags near some location.
	 * 
	 * @param geoPoint
	 * @param listener
	 */
	public void getTags(Location geoPoint, final TagsListener listener) {
		RequestParams params = new RequestParams();
		params.put("lat", Double.toString(geoPoint.getLat()));
		params.put("lon", Double.toString(geoPoint.getLon()));
		params.put("radius", Integer.toString(100));
		
		httpClient.get(API_BASE_URL + "/tags", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Collection<Tag> tags = new ArrayList<Tag>();
				
				try {
					Type collectionType = new TypeToken<Collection<Tag>>(){}.getType();
					tags = new Gson().fromJson(result, collectionType);
				} catch(JsonSyntaxException ignorable) {}
				
				listener.onSuccess(tags);
			}
			
			@Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		});
	}
	
	/**
	 * Get specific image by it's ID.
	 * 
	 * @param id
	 * @param listener
	 */
	public void getImage(int id, final ImageListener listener) {
		httpClient.get(API_BASE_URL + "/images/id/" + id, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				listener.onSuccess(null);
			}
			
			@Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		});
	}
	
	/**
	 * Get actual bitmap (file).
	 * 
	 * @param type
	 * @param imageName
	 * @param listener
	 */
	public void getBitmap(ImageSizeType type, String imageName, final BitmapListener listener) {
		String[] allowedContentTypes = new String[] { "image/jpeg" };
		httpClient.get(API_BASE_URL + "/files/images/" + type.getUrl() + "/" + imageName, new BinaryHttpResponseHandler(allowedContentTypes) {
		    @Override
		    public void onSuccess(byte[] fileData) {
		    	Bitmap image = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
		    	listener.onSuccess(image);
		    }
		    
		    @Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		    
		});
	}
	
	/**
	 * Upload image with optional description, tag and location.
	 * 
	 * @param image
	 * @param description
	 * @param tagValue
	 * @param location
	 * @param listener
	 */
	public void uploadImage(Bitmap image, String description, String tagValue, Location location, final UploadImageListener listener) {
		RequestParams params = new RequestParams();
		if (description != null) {
			params.put("description", description);
		}
		
		if (tagValue != null) {
			params.put("tag", tagValue);
		}
		
		if (location != null) {
			params.put("lat", Double.toString(location.getLat()));
			params.put("lon", Double.toString(location.getLon()));
		}
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bitmapByteArray = stream.toByteArray();
		
		params.put("image", new ByteArrayInputStream(bitmapByteArray), "myImage.jpg");
		
		httpClient.post(API_BASE_URL + "/images", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				listener.onSuccess();
			}
			
			@Override
			public void onFailure(Throwable t, String message) {
				listener.onFailure();
			}
		});
	}
	
	public enum ImageSizeType {
		
		ORIGINAL("original"),
		WEB_DEFAULT("default"),
		THUMBNAIL_EMBEDDED("thumbnail/embedded"),
		THUMBNAIL_SQUARE("thumbnail/square"),
		THUMBNAIL_ANDROID("thumbnail/android");
		
		private String url;
	
		ImageSizeType(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
		
	}
	
	public interface BitmapListener {
		public void onSuccess(Bitmap image);
		public void onFailure();
	}
	
	public interface ImageListener {
		public void onSuccess(Image image);
		public void onFailure();
	}
	
	public interface TagsListener {
		public void onSuccess(Collection<Tag> tags);
		public void onFailure();
	}
	
	public interface ImagesListener {
		public void onSuccess(Collection<Image> images);
		public void onFailure();
	}
	
	public interface UploadImageListener {
		public void onSuccess();
		public void onFailure();
	}

}
