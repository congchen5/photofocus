package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.ArrayList;
import java.util.Timer;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import edu.berkeley.cs160.stackunderflow.photofocus.MyLocation;
import edu.berkeley.cs160.stackunderflow.photofocus.MyLocation.LocationResult;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PhotoSession extends BaseActivity implements LocationListener {
	
	private GoogleMap myMap;
	public double currentLat;
	public double currentLong;
	private Location currentLocation;
	public LatLng latlonglocation;
	private CameraPosition cameraPosition;
	
	private LocationManager manager;
	public double lat;
	public double lon;
	private ArrayList<LatLng> path;
	
	//Handling Location Business
	LocationManager locationManager;
	LocationResult locationResult;
	LocationListener locationlistener;
	Timer timer;
	boolean gps_on = false;
	boolean network_on = false;
	
    private static final int DIALOG_PHOTO_SESSION = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_session);
		
		path = new ArrayList<LatLng>();
		Log.d("onCreate()", "calling getMyLocation");
		getMyLocation();
//		checkLocation();
		
		if (myMap == null) {
			myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.photo_session_map)).getMap();

			myMap.setMyLocationEnabled(true);
		}
		
        final Button button = (Button) findViewById(R.id.button_end_photo_session);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(PhotoSession.this, PhotoSessionComplete.class);
//            	myIntent.putExtra("key", value); //Optional parameters
            	PhotoSession.this.startActivity(myIntent);
            }
        });
	}
	
	public void checkLocation() {
		Log.d("checkLocation: ", " is called");
		//initialize location manager
		manager =  (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//check if GPS is enabled
		//if not, notify user with a toast
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	Toast.makeText(this, "GPS is disabled.", Toast.LENGTH_SHORT).show();
	    } else {

	    	//get a location provider from location manager
	    	//empty criteria searches through all providers and returns the best one
	    	String providerName = manager.getBestProvider(new Criteria(), true);
	    	Location location = manager.getLastKnownLocation(providerName);
	    	Log.d("right before ", " updatedList");
//	    	TextView tv = (TextView)findViewById(R.id.locationResults);
	    	if (location != null) {
//		    	Toast.makeText(this, location.getLatitude() + " latitude, " + location.getLongitude() + " longitude", Toast.LENGTH_SHORT).show();
		    	Log.d("location info: ", location.getLatitude() + " latitude, " + location.getLongitude() + " longitude");
		    	currentLat = location.getLatitude();
		    	currentLong = location.getLongitude();
		    	
				latlonglocation = new LatLng(currentLat, currentLong);
				path.add(latlonglocation);
				
				cameraPosition = new CameraPosition(latlonglocation, 14, 0, 0);
				updateMap();
				
	    	} else {
//	    		Toast.makeText(this, "Last known location not found. Waiting for updated location...", Toast.LENGTH_SHORT).show();
	    	}
	    	//sign up to be notified of location updates every 15 seconds - for production code this should be at least a minute
	    	manager.requestLocationUpdates(providerName, 15000, 1, this);
	    }
	}

	@Override
	public void onLocationChanged(Location location) {
    	if (location != null) {
//	    	Toast.makeText(this, location.getLatitude() + " latitude, " + location.getLongitude() + " longitude", Toast.LENGTH_SHORT).show();
	    	Log.d("location info: ", location.getLatitude() + " latitude, " + location.getLongitude() + " longitude");
	    	currentLat = location.getLatitude();
	    	currentLong = location.getLongitude();
	    	
			latlonglocation = new LatLng(currentLat, currentLong);
			path.add(latlonglocation);
			cameraPosition = new CameraPosition(latlonglocation, 14, 0, 0);
			updateMap();
    	} else {
//    		Toast.makeText(this, "Last known location not found. Waiting for updated location...", Toast.LENGTH_SHORT).show();
    	}
	}
	public void getMyLocation() {
		Log.d("getMyLocation", "called");
		MyLocation location = new MyLocation();
		edu.berkeley.cs160.stackunderflow.photofocus.MyLocation.LocationResult locationResult = new edu.berkeley.cs160.stackunderflow.photofocus.MyLocation.LocationResult() {

			@Override
			public void gotLocation(Location loc) {
				currentLocation = loc;
				Log.d("getMyLocation", "currentLocation:" + loc.toString());
				if (loc != null) {
					currentLat = loc.getLatitude();
					Log.d("currentlat:", "" + currentLat);
					currentLong = loc.getLongitude();
					Log.d("currentLong", currentLong + "");
					latlonglocation = new LatLng(currentLat, currentLong);
					
					Log.d("INSIDE GOTLOCATION current Lat", "" + latlonglocation.latitude);
					Log.d("INSIDE GOTLOCATION current Long", "" + latlonglocation.longitude);
					
					cameraPosition = new CameraPosition(latlonglocation, 14, 0, 0);
					updateMap();
				}
			};
		}; // ends LocationResult
		Log.d("getMyLocation", "locationResult");
		location.getLocation(this, locationResult);
	}
	
	private void updateMap() {
		CameraUpdate cameraUpdate = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		myMap.moveCamera(cameraUpdate);
	}

	@Override
	public void onProviderDisabled(String arg0) {}

	@Override
	public void onProviderEnabled(String arg0) {}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lon;
	}
}
