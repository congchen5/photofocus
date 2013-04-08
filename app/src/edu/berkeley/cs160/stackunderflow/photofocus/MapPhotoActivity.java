package edu.berkeley.cs160.stackunderflow.photofocus;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.berkeley.cs160.stackunderflow.photofocus.MyLocation;
import edu.berkeley.cs160.stackunderflow.photofocus.MyLocation.LocationResult;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;

public class MapPhotoActivity extends BaseActivity implements LocationListener {
	private GoogleMap myMap;
	private LocationManager lManager;
	private final static float MIN_DISTANCE = 100;
	private final static int MIN_TIME = 2000;
	public double currentLat;
	public double currentLong;
	public LatLng latlonglocation;
	private CameraPosition cameraPosition;
	Location currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_photo);
		Log.d("oncreate", "calling getMyLocation");
		getMyLocation();
		
		if (myMap == null) {
			myMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.photo_map)).getMap();
			// CameraUpdate cameraUpdate =
			// CameraUpdateFactory.newCameraPosition(cameraPosition);
			// myMap.moveCamera(cameraUpdate);

			myMap.setMyLocationEnabled(true);
		}
		
		// add some sample static markers
		LatLng lawSchool = new LatLng(37.869551, -122.253224);
		LatLng katesHouse = new LatLng(37.868024, -122.253406);
		addMarker(lawSchool, "The Law School",
				"This is where law students study.");
		addMarker(katesHouse, "Kate's House", "It's really messy.");

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
					cameraPosition = new CameraPosition(latlonglocation, 14, 0,
							0);
					updateMap();

				}
			};
		}; // ends LocationResult
		Log.d("getMyLocation", "locationResult");
		location.getLocation(this, locationResult);

	}// ends getMyLocation method

	private void updateMap() {
		CameraUpdate cameraUpdate = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		myMap.moveCamera(cameraUpdate);
	}

	// adds a marker to the map
	// TODO
	public void addMarker(LatLng latlngloc, String title, String snippet) {

		Marker newMarker = myMap.addMarker(new MarkerOptions()
				.position(latlngloc).title(title).snippet(snippet)
		// .icon(BitmapDescriptorFactory.fromResource(R.drawable.berkeley_law_2))
				);
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
