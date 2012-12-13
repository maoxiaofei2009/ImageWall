package com.svenkapudija.imagewall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.svenkapudija.imagewall.api.ImageWallApi.ImagesListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Image;

public class MainActivity extends ImageWallActivity {

	private static final int CHOOSER_IMAGE_REQUEST_CODE = 1000;

	private ImageChooser chooser;
	
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
		chooser.saveImageTo(StorageOption.SD_CARD_APP_ROOT, "images", "myImage");
		
		newImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chooser.show();
			}
		});
		
		List<Image> items = new ArrayList<Image>();
		for(int i = 0; i < 20; i++) {
			items.add(new Image(null, null, null));
		}
		
		ArrayAdapter<Image> adapter = new TimelineAdapter(this, items);
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CHOOSER_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
	        chooser.onActivityResult(data, new ImageChooserListener() {

	            @Override
	            public void onResult(Bitmap image, File ... savedImages) {
					Intent i = new Intent(MainActivity.this, NewImageActivity.class);
					startActivity(i);
	            }

	            @Override
	            public void onError(String message) {
	            	
	            }
	        });
	    }
	}

}
