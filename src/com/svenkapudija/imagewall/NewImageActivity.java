package com.svenkapudija.imagewall;


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
import android.widget.TextView;
import android.widget.Toast;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.svenkapudija.imagewall.api.ImageWallApi.UploadImageListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Location;

public class NewImageActivity extends ImageWallActivity {

	public static final String EXTRA_NEAREST_TAGS = "nearest_tags";
	
	private static final int WAIT_FOR_LOCATION_IN_SECONDS = 10;
	
	private Location location;
	private ProgressDialog waitingLocationDialog;
	
	private Button send;
	private Button nearestTagsButton;
	private TextView nearestTagTextView;
	private EditText imageDescription;
	private EditText tagValue;
	private CheckBox useLocation;
	
	@Override
	public void initUI() {
		send = (Button) findViewById(R.id.button_send);
		nearestTagsButton = (Button) findViewById(R.id.button_nearestTags);
		nearestTagTextView = (TextView) findViewById(R.id.textView_nearestTag);
		imageDescription = (EditText) findViewById(R.id.editText_imageDescription);
		tagValue = (EditText) findViewById(R.id.editText_tagValue);
		useLocation = (CheckBox) findViewById(R.id.checkBox_location);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_image);
		
		initNearestTags();
		
		if (isLocationAvailable()) {
			LocationLibrary.forceLocationUpdate(this);
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
	
	private void waitForLocationTask() {
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				waitingLocationDialog = ProgressDialog.show(NewImageActivity.this, null, "Getting your location...", true, false);
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
				
				if(waitingLocationDialog != null) {
					waitingLocationDialog.cancel();
				}
				
				showTryAgainDialog();
			}

			private void showTryAgainDialog() {
				AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
				builder.setMessage("Your location it currently unavailable. Do you want to try again or send image without location?");
				builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startImageUpload();
					}
				});
				builder.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
					
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
	
	private void initNearestTags() {
		final List<String> nearestTags = getIntent().getStringArrayListExtra(EXTRA_NEAREST_TAGS);
		if(nearestTags != null) {
			nearestTagsButton.setText(nearestTags.get(0));
			nearestTagsButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final String[] items = new String[nearestTags.size()];
					nearestTags.toArray(items);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
					builder.setTitle("Nearest tags");
					builder.setItems(items, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							tagValue.setText(items[which]);
						}
					});
				}
			});
		} else {
			nearestTagTextView.setVisibility(View.GONE);
			nearestTagsButton.setVisibility(View.GONE);
		}
	}

	private boolean isLocationAvailable() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        return gps || network;
	}
	
	private void startImageUpload() {
		final ProgressDialog apiProgress = ProgressDialog.show(NewImageActivity.this, null, "Uploading your image...", true, false);
		
		String description = imageDescription.getText().toString();
		String tag = tagValue.getText().toString();
		
		getApi().uploadImage(getFileUtils().getImageToUpload(), description, tag, useLocation.isChecked() ? location : null, new UploadImageListener() {
			
			@Override
			public void onSuccess() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				openMainActivity();
			}

			@Override
			public void onFailure() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(NewImageActivity.this);
				builder.setMessage("Error: image couldn't be sent.");
				builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						openMainActivity();
					}
				});
			}
			
			private void openMainActivity() {
				Toast.makeText(NewImageActivity.this, "Image has been sent successfully", Toast.LENGTH_LONG).show();
				
				Intent i = new Intent(NewImageActivity.this, MainActivity.class);
				i.putExtra(MainActivity.EXTRA_ACTION_REFRESH, true);
				startActivity(i);
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
