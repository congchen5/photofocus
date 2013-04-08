package edu.berkeley.cs160.stackunderflow.photofocus;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


public class getHttpPhoto extends AsyncTask<String, Object, Bitmap> {
	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
	private final static String EC2_URL= "http://ec2-107-22-151-251.compute-1.amazonaws.com:5000";
	private MapPhotoActivity thisActivity;
	private int currentPhotoId;
	
	public getHttpPhoto(MapPhotoActivity thisAct, int curPhotoId){
		thisActivity = thisAct;
		currentPhotoId = curPhotoId;
	}
	protected Bitmap doInBackground(String ...params){
	Log.d("getHttpPhoto", "called");
	String thisURL = EC2_URL + params[0];
	Log.d("getHttpPhoto", "params[0]: " + params[0] );
	URL url = null;
	try {
		url = new URL(thisURL);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	HttpURLConnection urlConnection = null;
	try {

		urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("auth_token", AUTH_TOKEN);
		urlConnection.setRequestMethod("GET");
		Log.d("getHttpPhoto", "url connection made");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	Bitmap bmp = null;
	InputStream in= null;
	try{
		in = new BufferedInputStream(urlConnection.getInputStream());
		Log.d("getHttpPhoto", "got input stream");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	/*byte[] contents = new byte[1024];
	int bytesRead =0;
	String strContents = null;
	try {
		while((bytesRead = in.read(contents))!= -1){
			strContents= new String(contents, 0, bytesRead);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Log.d("getHttpPhoto", "about to return");
	Log.d("getHttpPhoto", "info: "+ strContents);
	return strContents;
	*/
	
	bmp = BitmapFactory.decodeStream(in);
	if (bmp ==null){
		Log.d("getHttpPhoto", "bmp is null");
	}else{
		Log.d("getHttpPhoto", "bmp is not null");
	}
	
	return bmp;
	
	/*protected Bitmap doInBackground(String...params){
		Log.d("getHttpPhoto", "called");
		Bitmap bmp = null;
		DefaultHttpClient client = new DefaultHttpClient();
		String url = EC2_URL + params[0];
		URI uri =null;
		try{
			uri = new URI(null, android.net.Uri.encode(url), null);
		}catch (URISyntaxException e){
			e.printStackTrace();
		}
		if (uri == null){
			Log.d("uri", "uri is null");
		}
		HttpGet get = new HttpGet(url);
		get.addHeader("auth_token", AUTH_TOKEN);
		HttpResponse getResponse = null;
		try {
			getResponse = client.execute(get);
			Log.d("getHttpPhoto", "getResponse execute called");
			
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		String entityContents ="";
		HttpEntity responseEntity = getResponse.getEntity();
		BufferedHttpEntity httpEntity = null;
		try{
			httpEntity = new BufferedHttpEntity(responseEntity);
			Log.d("getHttpPhoto" ,"httpEntity successful");
		}catch (IOException e1){
			e1.printStackTrace();
		}
		
		InputStream imageStream = null;
		try{
			imageStream = httpEntity.getContent();
			Log.d("getHttpPhoto", "image stream accquired");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		if (imageStream == null){
			Log.d("getHttpPhoto", "image stream is null");
		}
		bmp = BitmapFactory.decodeStream(imageStream);
		
		
		return bmp;
	} */
	}
	
	@Override
	protected void onPostExecute(Bitmap bmp){
		//Log.d("onPostExecute", "response: "+ response );
		super.onPostExecute(bmp);
		Log.d("onPostExecute", "called");
		thisActivity.markerPhotos.put(currentPhotoId, bmp);
		Log.d("onPostExecute", "currentphotoId: "+ currentPhotoId);
		if (thisActivity.markerLatitude.get(currentPhotoId) ==null){
			Log.d("onPostExecute", "Latitude is null");
		}else{
			Log.d("onPostExecute", "Latitude is: " + thisActivity.markerLatitude.get(currentPhotoId));
		}
		if (thisActivity.markerLongitude.get(currentPhotoId)==null){
			Log.d("onPostExecute", "Longitude is null");
		}else{
			Log.d("onPostExecute", "Longitude is: " + thisActivity.markerLongitude.get(currentPhotoId));
		}
		LatLng locationMarker = new LatLng(thisActivity.markerLatitude.get(currentPhotoId), 
				thisActivity.markerLongitude.get(currentPhotoId));
		thisActivity.addMarker(locationMarker, null,null, bmp);
	}
	
	
}
	
