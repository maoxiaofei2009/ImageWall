package com.svenkapudija.imagewall;

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

import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;
import com.svenkapudija.imagewall.api.ImageWallApi.ImageWallListener;
import com.svenkapudija.imagewall.base.ImageWallActivity;
import com.svenkapudija.imagewall.models.Location;

public class NewImageActivity extends ImageWallActivity {

	private static final String TAG = NewImageActivity.class.getName();
	
	private static final int WAIT_FOR_LOCATION_IN_SECONDS = 10;
	
	private Location location;
	private ProgressDialog waitingLocationDialog;
	
	private Button send;
	private EditText imageDescription;
	private EditText tagValue;
	private CheckBox useLocation;
	
	@Override
	public void initUI() {
		send = (Button) findViewById(R.id.button_send);
		imageDescription = (EditText) findViewById(R.id.editText_imageDescription);
		tagValue = (EditText) findViewById(R.id.editText_tagValue);
		useLocation = (CheckBox) findViewById(R.id.checkBox_location);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_image);
		
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
		});
	}

	private boolean isLocationAvailable() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        return gps || network;
	}
	
	private void startImageUpload() {
		String description = imageDescription.getText().toString();
		String tag = tagValue.getText().toString();
		
		final ProgressDialog apiProgress = ProgressDialog.show(NewImageActivity.this, null, "Uploading your image...", true, false);
		
		getApi().uploadImage(null, description, tag, useLocation.isChecked() ? location : null, new ImageWallListener() {
			
			@Override
			public void onSuccess() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				
			}
			
			@Override
			public void onFailure() {
				if(apiProgress != null) {
					apiProgress.cancel();
				}
				
				
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
