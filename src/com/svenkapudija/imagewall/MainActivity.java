package com.svenkapudija.imagewall;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.svenkapudija.imagechooser.AlertDialogImageChooser;
import com.svenkapudija.imagechooser.ImageChooser;
import com.svenkapudija.imagechooser.ImageChooserListener;
import com.svenkapudija.imagechooser.StorageOption;
import com.svenkapudija.imagechooser.settings.AlertDialogImageChooserSettings;
import com.svenkapudija.imageresizer.ImageResizer;
import com.svenkapudija.imagewall.adapters.TimelineAdapter;
import com.svenkapudija.imagewall.api.ImageWallApi.ImagesListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.caching.ImageWallFileUtils;
import com.svenkapudija.imagewall.models.Image;

public class MainActivity extends ImageWallActivity {
	
	private static final String TAG = MainActivity.class.getName();
	
	private static final int CHOOSER_IMAGE_REQUEST_CODE = 1000;

	private ImageChooser chooser;
	private List<Image> listItems;
	private ArrayAdapter<Image> adapter;
	
	private ImageButton newImage;
	private PullToRefreshListView listView;
	
	@Override
	public void initUI() {
		newImage = (ImageButton) findViewById(R.id.imageButton_newImage);
		listView = (PullToRefreshListView) findViewById(R.id.listView);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		chooser = new AlertDialogImageChooser(this, CHOOSER_IMAGE_REQUEST_CODE, new AlertDialogImageChooserSettings(true));
		chooser.saveImageTo(StorageOption.SD_CARD_APP_ROOT, "imagesToUpload", Long.toString(new Date().getTime()));
		
		newImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chooser.show();
			}
		});
		
		listItems = new ArrayList<Image>();
		adapter = new TimelineAdapter(this, listItems);
		listView.setAdapter(adapter);
		
		listView.setReleaseLabel("Let go!");
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getApi().getImages(new ImagesListener() {
					
					@Override
					public void onSuccess(Collection<Image> images) {
						for(Image image : images) {
							Log.e(TAG, image.toString());
						}
						
						listView.onRefreshComplete();
					}
					
					@Override
					public void onFailure() {
						listView.onRefreshComplete();
					}
				});
			}
		});
		
		ImageWallFileUtils utils = new ImageWallFileUtils(this);
		List<Integer> ids = utils.getImagesIds();
		for(int id : ids) {
			for(int i = 0; i < 10; i++) {
				adapter.add(new Image(id, "Ovo je testiranje", null));
			}
		}
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent i = new Intent(MainActivity.this, ImageActivity.class);
				startActivity(i);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CHOOSER_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
	        chooser.onActivityResult(data, new ImageChooserListener() {

	            @Override
	            public void onResult(final Bitmap image, final File ... savedImages) {
	            	resizeAndSave(image, savedImages);
	            }

				private void resizeAndSave(final Bitmap image, final File... savedImages) {
					new AsyncTask<Void, Void, Bitmap>() {
	            		
	            		private ProgressDialog dialog;
	            		
	            		@Override
						protected void onCancelled() {
							super.onCancelled();
							
							dialog.cancel();
						}

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							
							dialog = ProgressDialog.show(MainActivity.this, null, "Resizing image...");
						}

						@Override
	            		protected Bitmap doInBackground(Void... params) {
	            			Bitmap cropped = ImageResizer.resize(savedImages[0], true, 1280, 800);
	    	            	image.recycle();
	    	            	
	            			return cropped;
	            		}
	            		
	            		@Override
	            		protected void onPostExecute(Bitmap result) {
	            			super.onPostExecute(result);
	            			
	            			result.recycle();
	            			
	            			if(dialog != null) {
	            				dialog.cancel();
	            			}
	            			
	            			Intent i = new Intent(MainActivity.this, NewImageActivity.class);
	        				startActivity(i);
	            			//adapter.add(new Image(0, "Ovo je testiranje", null));
	            		}
	            		
					}.execute();
				}

	            @Override
	            public void onError(String message) {
	            	
	            }
	        });
	    }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
