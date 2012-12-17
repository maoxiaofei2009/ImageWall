package com.svenkapudija.imagewall;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.svenkapudija.imagewall.models.LatLonGeoPoint;
import com.svenkapudija.imagewall.models.Location;
import com.svenkapudija.imagewall.overlays.LocationOverlay;

public class MapsActivity extends MapActivity {

	/**
	 * {@link Location} provided from calling activity.
	 */
	public static final String EXTRA_LOCATION = "extra_location";
	
	private MapView mapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		
		Bundle b = getIntent().getExtras();
		if(b != null) {
			Location location = (Location) b.getParcelable(EXTRA_LOCATION);
			LatLonGeoPoint geoPoint = addLocationToMap(location);
			
			MapController controller = mapView.getController();
			controller.animateTo(geoPoint);
			controller.setZoom(16);
		}
		
	}

	/**
	 * Add pinpoint to map.
	 * 
	 * @param location
	 * @return	{@link Location} converted to {@link LatLonGeoPoint}
	 */
	private LatLonGeoPoint addLocationToMap(Location location) {
		LatLonGeoPoint locationGeoPoint = new LatLonGeoPoint(location.getLat(), location.getLon());
		LocationOverlay locationOverlay = new LocationOverlay(getResources().getDrawable(R.drawable.map_marker));
		locationOverlay.addOverlay(new OverlayItem(locationGeoPoint, null, null));
		mapView.getOverlays().add(locationOverlay);
		
		return locationGeoPoint;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
