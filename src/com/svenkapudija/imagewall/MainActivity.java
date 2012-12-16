package com.svenkapudija.imagewall;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.svenkapudija.imagechooser.AlertDialogImageChooser;
import com.svenkapudija.imagechooser.ImageChooser;
import com.svenkapudija.imagechooser.ImageChooserListener;
import com.svenkapudija.imagechooser.StorageOption;
import com.svenkapudija.imagechooser.settings.AlertDialogImageChooserSettings;
import com.svenkapudija.imagewall.adapters.TimelineAdapter;
import com.svenkapudija.imagewall.api.ImageWallApi.ImagesListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Image;
import com.svenkapudija.imagewall.models.Tag;

public class MainActivity extends ImageWallActivity {
	
	public static final String EXTRA_ACTION_REFRESH = "action_refresh";
	public static final String EXTRA_ACTION_SEARCH_TAG = "action_search";
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
		
		initImageChooser();
		
		initListItemsFromDb();
		adapter = new TimelineAdapter(this, listItems);
		initListView();
		
		newImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chooser.show();
			}
		});
		
		// Fetch new images if necessary
		if(listItems.size() == 0 || actionFetchNewImages()) {
			final ProgressDialog dialog = ProgressDialog.show(this, null, "Loading images...", true, false);
			fetchImagesFromNetwork(new FetchImagesListener() {
				
				@Override
				public void onSuccess() {
					dialog.cancel();
				}
				
				@Override
				public void onFailure() {
					dialog.cancel();
				}
			});
		}
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			String searchImagesWithTag = b.getString(EXTRA_ACTION_SEARCH_TAG, null);
			if(searchImagesWithTag != null) {
				getApi().getImages(new Tag(searchImagesWithTag), new ImagesListener() {
					
					@Override
					public void onSuccess(Collection<Image> images) {
						
					}
					
					@Override
					public void onFailure() {
						
					}
				});
			}
		}
		
	}
	
	private void initListItemsFromDb() {
		List<Image> images = new ArrayList<Image>();
		try {
			images = getHelper().getImagesDao().queryBuilder().orderBy("dateCreated", false).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		listItems = images;
	}

	private void initListView() {
		listView.setAdapter(adapter);
		
		listView.setReleaseLabel("Let go!");
		listView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				fetchImagesFromNetwork();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				position--; // Possible bug due PullToRefresh? [header counts as first row]
				
				Intent i = new Intent(MainActivity.this, ImageActivity.class);
				i.putExtra(ImageActivity.EXTRA_IMAGE, listItems.get(position));
				startActivity(i);
			}
		});
	}

	private void initImageChooser() {
		chooser = new AlertDialogImageChooser(this, CHOOSER_IMAGE_REQUEST_CODE, new AlertDialogImageChooserSettings(true));
		chooser.saveImageTo(StorageOption.SD_CARD_APP_ROOT, "imageToUpload", "myImage");
	}

	private boolean actionFetchNewImages() {
		boolean fetchNewImages = false;
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			fetchNewImages = b.getBoolean(EXTRA_ACTION_REFRESH, false);
		}
		
		return fetchNewImages;
	}

	private void fetchImagesFromNetwork(final FetchImagesListener ... listeners) {
		getApi().getImages(new ImagesListener() {
			
			@Override
			public void onSuccess(Collection<Image> images) {
				RuntimeExceptionDao<Image, Integer> imagesDao = getHelper().getImagesDao();
				for(Image image : images) {
					boolean newRowCreated = imagesDao.createOrUpdate(image).isCreated();
					if(newRowCreated) {
						// Add it the to list
						listItems.add(0, image);
						adapter.notifyDataSetChanged();
					}
				}
				
				listView.onRefreshComplete();
				
				for(FetchImagesListener listener : listeners) {
					listener.onSuccess();
				}
			}
			
			@Override
			public void onFailure() {
				listView.onRefreshComplete();
				
				for(FetchImagesListener listener : listeners) {
					listener.onFailure();
				}
			}
		});
	}
	
	private interface FetchImagesListener {
		public void onSuccess();
		public void onFailure();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CHOOSER_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
	        chooser.onActivityResult(data, new ImageChooserListener() {

	            @Override
	            public void onResult(final Bitmap image, final File ... savedImages) {
	            	image.recycle();
	            	
	            	Intent i = new Intent(MainActivity.this, NewImageActivity.class);
    				startActivity(i);
	            }

	            @Override
	            public void onError(String message) {
	            	Toast.makeText(MainActivity.this, "Couldn't load the image. Please try again.", Toast.LENGTH_LONG).show();
	            }
	        });
	    }
	}

}
