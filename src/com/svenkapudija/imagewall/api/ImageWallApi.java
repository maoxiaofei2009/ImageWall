package com.svenkapudija.imagewall.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.models.LatLonGeoPoint;
import com.svenkapudija.imagewall.models.Tag;

public class ImageWallApi {

	private static final String API_BASE_URL = "http://www.domain.com"; 
	
	private AsyncHttpClient httpClient;
	
	public ImageWallApi() {
		httpClient = new AsyncHttpClient();
	}
	
	public void getImages(final ImagesListener listener, Date ... lastImageTimestamp) {
		httpClient.get(API_BASE_URL + "/images", new AsyncHttpResponseHandler() {
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
	
	public void getImages(Tag tag, final ImagesListener listener) {
		httpClient.get(API_BASE_URL + "/images?tag=" + encode(tag.getValue()), new AsyncHttpResponseHandler() {
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
	
	public void getTags(LatLonGeoPoint geoPoint, final TagsListener listener) {
		httpClient.get(API_BASE_URL + "/tags?location=" + encode(geoPoint.getLat() + "," + geoPoint.getLon()), new AsyncHttpResponseHandler() {
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
	
	public void getImage(int id, final ImageListener listener) {
		httpClient.get(API_BASE_URL + "/images/" + id, new AsyncHttpResponseHandler() {
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
	
	public void uploadImage(Bitmap image, final ImageWallListener listener) {
		httpClient.post(API_BASE_URL + "/images", new AsyncHttpResponseHandler() {
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
	
	private String encode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException ignorable) {}
		
		return string;
	}
	
	public interface ImageListener {
		public void onSuccess(Image image);
		public void onFailure();
	}
	
	public interface TagsListener {
		public void onSuccess(List<Tag> tags);
		public void onFailure();
	}
	
	public interface ImagesListener {
		public void onSuccess(List<Image> images);
		public void onFailure();
	}
	
	public interface ImageWallListener {
		public void onSuccess();
		public void onFailure();
	}

}
