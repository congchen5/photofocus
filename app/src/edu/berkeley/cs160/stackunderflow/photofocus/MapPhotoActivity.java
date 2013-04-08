package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.HashMap;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.berkeley.cs160.stackunderflow.photofocus.MyLocation;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

public class MapPhotoActivity extends BaseActivity implements LocationListener,
		OnMarkerClickListener {
	private GoogleMap myMap;
	private LocationManager lManager;
	private final static float MIN_DISTANCE = 100;
	private final static int MIN_TIME = 2000;
	private final static int PIC_WIDTH = 100;
	private final static int PIC_HEIGHT = 100;
	private final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";
	private final static String EC2_URL = "http://ec2-107-22-151-251.compute-1.amazonaws.com:5000";
	public double currentLat;
	public double currentLong;
	public LatLng latlonglocation;

	/*
	 * photos will be stored with their data in the following HashMaps with
	 * their photoid as their key
	 */
	public HashMap<Integer, Bitmap> markerPhotos = new HashMap<Integer, Bitmap>();
	public HashMap<Integer, Double> markerLatitude = new HashMap<Integer, Double>();
	public HashMap<Integer, Double> markerLongitude = new HashMap<Integer, Double>();
	public HashMap<String, Integer> markerIdtoPhotoId = new HashMap<String, Integer>();

	public int currentPhotoId = 1;

	private CameraPosition cameraPosition;
	Location currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_photo);
		Log.d("oncreate", "calling getMyLocation");
		getMyLocation();
		setUpMapIfNeeded();
		// SEED THE HASH MAPS-- TEMPORARY
		// kate's house
		markerLatitude.put(1, 37.868024);
		markerLongitude.put(1, -122.253406);
		// campanile
		markerLatitude.put(2, 37.871858);
		markerLongitude.put(2, -122.258083);
		// ihouse
		markerLatitude.put(3, 37.869788);
		markerLongitude.put(3, -122.251456);
		// lawrence lab
		markerLatitude.put(4, 37.880160);
		markerLongitude.put(4, -122.251561);
		// rose garden
		markerLatitude.put(5, 37.886348);
		markerLongitude.put(5, -122.263012);
		// li ka shing
		markerLatitude.put(6, 37.873114);
		markerLongitude.put(6, -122.265261);

		// run through each of the get request,
		// onPostExecute will store the photo in the HashMap

		for (currentPhotoId = 2; currentPhotoId < 7; currentPhotoId++) {

			// add some sample static markers
			String httpGetAddOn = "/photos/" + currentPhotoId + "?show=image";

			getHttpPhoto request = new getHttpPhoto(this, currentPhotoId);
			try {
				request.execute(httpGetAddOn);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
		Log.d("debug", "HashMap Markers" + markerIdtoPhotoId.toString());
	}

	public void getMyLocation() {
		Log.d("getMyLocation", "called");
		MyLocation location = new MyLocation();
		MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {

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

	private void setUpMapIfNeeded() {
		if (myMap == null) {
			myMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.photo_map)).getMap();
			// CameraUpdate cameraUpdate =
			// CameraUpdateFactory.newCameraPosition(cameraPosition);
			// myMap.moveCamera(cameraUpdate);

			myMap.setMyLocationEnabled(true);
			myMap.setOnMarkerClickListener(this);
		}
	}

	private void updateMap() {
		CameraUpdate cameraUpdate = CameraUpdateFactory
				.newCameraPosition(cameraPosition);
		myMap.moveCamera(cameraUpdate);
	}

	// adds a marker to the map
	// TODO
	public void addMarker(LatLng latlngloc, String title, String snippet,
			Bitmap bmp, int currentPhotoId) {
		// rescale the Bitmap
		Bitmap rescaledBmp = Bitmap.createScaledBitmap(bmp, PIC_WIDTH,
				PIC_HEIGHT, false);
		Log.d("addMarker", "called");
		Marker newMarker = myMap.addMarker(new MarkerOptions()
				.position(latlngloc).title(title).snippet(snippet)

				.icon(BitmapDescriptorFactory.fromBitmap(rescaledBmp))

		);
		markerIdtoPhotoId.put(newMarker.getId(), currentPhotoId);
		Log.d("debug", "HashMap Markers" + markerIdtoPhotoId.toString());

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

	public GoogleMap getMyMap() {
		return myMap;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// get the photoID
		String markerId = marker.getId();
		if (marker == null){
			Log.d("onMarkerClick", "marker is null");
		}
		Log.d("onMarkerClick","markerId: " + marker.getId() );
		Log.d("onMarkerClick, what's returned: ",""+ markerIdtoPhotoId.get(markerId));
		int photoID = markerIdtoPhotoId.get(markerId);
		// BRIAN
		 Intent i = new Intent(this, PhotoDetailsActivity.class);
		 i.putExtra("photoId", photoID);
		 startActivity(i);
		return true;
	}

}
