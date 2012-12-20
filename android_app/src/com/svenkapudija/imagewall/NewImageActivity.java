package com.svenkapudija.imagewall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.svenkapudija.imagewall.api.ImageWallApi.TagsListener;
import com.svenkapudija.imagewall.api.ImageWallApi.UploadImageListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Location;
import com.svenkapudija.imagewall.models.Tag;

public class NewImageActivity extends ImageWallActivity {

	/**
	 * How much to wait to get some location?
	 */
	private static final int WAIT_FOR_LOCATION_IN_SECONDS = 10;
	
	private Location location;
	private ProgressDialog waitingLocationDialog;
	
	private Button send;
	private Button nearestTagsButton;
	private EditText imageDescription;
	private EditText tagValue;
	private CheckBox useLocation;
	
	@Override
	public void initUI() {
		send = (Button) findViewById(R.id.button_send);
		nearestTagsButton = (Button) findViewById(R.id.button_nearestTags);
		imageDescription = (EditText) findViewById(R.id.editText_imageDescription);
		tagValue = (EditText) findViewById(R.id.editText_tagValue);
		useLocation = (CheckBox) findViewById(R.id.checkBox_location);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_image);
		
		if (isLocationProviderAvailable()) {
			LocationLibrary.forceLocationUpdate(this);
			initNearestTags();
		} else {
			useLocation.setChecked(false);
			useLocation.setVisibility(View.GONE);
		}
        			
		send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(useLocation.isChecked() && location == null) {
					waitForLocationTask();
				} else {
					startImageUpload();
				}
			}
		});
	}
	
	/**
	 * Show dialog and wait for location...if fails, show the {@link AlertDialog} and eventually
	 * try again.
	 */
	private void waitForLocationTask() {
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				waitingLocationDialog = ProgressDialog.show(NewImageActivity.this, null, getString(R.string.getting_your_location), true, false);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(WAIT_FOR_LOCATION_IN_SECONDS * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				if(waitingLocationDialog.isShowing()) {
					waitingLocationDialog.cancel();
					showTryAgainDialog();
				}
			}

			private void showTryAgainDialog() {
				AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
				builder.setMessage(getString(R.string.your_location_it_currently_unavailable));
				builder.setPositiveButton(getString(R.string.send), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startImageUpload();
					}
				});
				builder.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						LocationLibrary.forceLocationUpdate(NewImageActivity.this);
						waitForLocationTask();
					}
				});
				
				builder.create().show();
			}
		}.execute();
	}
	
	private List<String> nearestTags;
	
	/**
	 * Start fetching nearest tags from REST API.
	 */
	private void initNearestTags() {
		nearestTagsButton.setVisibility(View.VISIBLE);
		nearestTagsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showNearestTagsDialog();
			}
		});

		fetchTags(new TagsListener() {
			
			@Override
			public void onSuccess(Collection<Tag> tagsCollection) {
				nearestTags = new ArrayList<String>();
				nearestTags.clear();
				for(Tag tag : tagsCollection) {
					nearestTags.add(tag.getValue());
				}
			}
			
			@Override
			public void onFailure() {}
			
		});
	}

	private void showNearestTagsDialog() {
		if(nearestTags == null) { // If previous call to retrieve tags wasn't fast enough, try again...
			final ProgressDialog dialog = ProgressDialog.show(this, null, getString(R.string.getting_nearest_tags), true, false);
			
			fetchTags(new TagsListener() {
				
				@Override
				public void onSuccess(Collection<Tag> tagsCollection) {
					dialog.cancel();
					
					nearestTags = new ArrayList<String>();
					nearestTags.clear();
					for(Tag tag : tagsCollection) {
						nearestTags.add(tag.getValue());
					}
					
					showNearestTagsDialog();
				}
				
				@Override
				public void onFailure() {
					dialog.cancel();
					
				}
				
			});
		} else if(nearestTags.size() == 0) { // Tags are fetched, but there are none
			Toast.makeText(NewImageActivity.this, getString(R.string.couldnt_find_any_tag_around_your_location), Toast.LENGTH_LONG).show();
		} else { // Finally, show some tags
			AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
			
			final String[] items = new String[nearestTags.size()];
			nearestTags.toArray(items);
			
			builder.setTitle(getString(R.string.nearest_tags));
			builder.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					tagValue.setText(items[which]);
				}
			});
			
			builder.create().show();
		}
	}
	
	private void fetchTags(TagsListener listener) {
		LocationInfo locationInfo = new LocationInfo(this);
		locationInfo.refresh(this);
		Location myCurrentPosition = new Location(locationInfo.lastLat, locationInfo.lastLong);
		
		getApi().getTags(myCurrentPosition, listener);
	}

	/**
	 * @return	<code>true</code> if either gps or network provided are available.
	 */
	private boolean isLocationProviderAvailable() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        return gps || network;
	}
	
	/**
	 * Start the image upload process and onFinish return to calling activity.
	 */
	private void startImageUpload() {
		final ProgressDialog apiProgress = ProgressDialog.show(NewImageActivity.this, null, getString(R.string.uploading_your_image), true, false);
		
		String description = imageDescription.getText().toString();
		String tag = tagValue.getText().toString();
		
		getApi().uploadImage(getFileUtils().getImageToUpload(), description, tag, useLocation.isChecked() ? location : null, new UploadImageListener() {
			
			@Override
			public void onSuccess() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				openMainActivity(true);
			}

			@Override
			public void onFailure() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
				builder.setMessage(getString(R.string.error_image_couldnt_be_uploaded));
				builder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openMainActivity(false);
					}
				});
			}
			
			private void openMainActivity(boolean refresh) {
				Toast.makeText(NewImageActivity.this, getString(R.string.image_has_been_uploaded), Toast.LENGTH_LONG).show();
				
				if(refresh) {
					setResult(RESULT_OK);
				}
				finish();
			}
			
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
		registerReceiver(lftBroadcastReceiver, lftIntentFilter);
	}

	@Override
	public void onPause() {
		super.onResume();

		unregisterReceiver(lftBroadcastReceiver);
	}

	/**
	 * Receiver to listen for location changes.
	 */
	private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
			location = new Location(locationInfo.lastLat, locationInfo.lastLong);
			
			if(waitingLocationDialog != null) {
				waitingLocationDialog.cancel();
				
				startImageUpload();
			}
		}
	};
    
}
