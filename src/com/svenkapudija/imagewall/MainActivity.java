package com.svenkapudija.imagewall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.svenkapudija.android.fileutils.AndroidFileUtils;
import com.svenkapudija.imagechooser.AlertDialogImageChooser;
import com.svenkapudija.imagechooser.ImageChooser;
import com.svenkapudija.imagechooser.ImageChooserListener;
import com.svenkapudija.imagechooser.StorageOption;
import com.svenkapudija.imagechooser.settings.AlertDialogImageChooserSettings;
import com.svenkapudija.imageresizer.ImageResizer;
import com.svenkapudija.imageresizer.operations.DimensionUnit;
import com.svenkapudija.imagewall.adapters.TimelineAdapter;
import com.svenkapudija.imagewall.api.ImageWallApi.ImagesListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
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
		chooser.saveImageTo(StorageOption.SD_CARD_APP_ROOT, "images", new Date().getTime() + "test");
		
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
					public void onSuccess(List<Image> images) {
						
						
						listView.onRefreshComplete();
					}
					
					@Override
					public void onFailure() {
						
					}
				});
			}
		});
		
		AndroidFileUtils utils = new AndroidFileUtils(this);
		
		int totalImages = utils.getCount(AndroidFileUtils.StorageOption.SD_CARD_APP_ROOT, "images");
		for(int i = 0; i < totalImages; i++) {
			adapter.add(new Image(null, "Ovo je testiranje", null));
		}
	}
	
	private static final int LIST_ROW_IMAGE_WIDTH_DP = 275;
	private static final int LIST_ROW_IMAGE_HEIGHT_DP = 90;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CHOOSER_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
	        chooser.onActivityResult(data, new ImageChooserListener() {

	            @Override
	            public void onResult(final Bitmap image, final File ... savedImages) {
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
	            			Bitmap cropped = ImageResizer.resize(savedImages[0], true, LIST_ROW_IMAGE_WIDTH_DP, LIST_ROW_IMAGE_HEIGHT_DP, DimensionUnit.DP, MainActivity.this);
	    	            	image.recycle();
	    	            	
	            			return cropped;
	            		}
	            		
	            		@Override
	            		protected void onPostExecute(Bitmap result) {
	            			super.onPostExecute(result);
	            			
	            			if(dialog != null) {
	            				dialog.cancel();
	            			}
	            			
	            			adapter.add(new Image(result, "Ovo je testiranje", null));
	            		}
	            		
					}.execute();
	            	
//					Intent i = new Intent(MainActivity.this, NewImageActivity.class);
//					startActivity(i);
	            }

	            @Override
	            public void onError(String message) {
	            	
	            }
	        });
	    }
	}

}
