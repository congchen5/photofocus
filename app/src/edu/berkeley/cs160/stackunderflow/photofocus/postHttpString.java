package edu.berkeley.cs160.stackunderflow.photofocus;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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


public class postHttpString extends AsyncTask<String, Object, String> {
	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
	private final static String EC2_URL= "http://ec2-107-22-151-251.compute-1.amazonaws.com:5000";
	private MapPhotoActivity thisActivity;
	private int currentPhotoId;
	private String body;
	
	public postHttpString(MapPhotoActivity thisAct, int curPhotoId, String body){
		thisActivity = thisAct;
		this.currentPhotoId = curPhotoId;
		this.body = body;
	}
	public postHttpString(MapPhotoActivity thisAct){
		thisActivity = thisAct;
	}
	public postHttpString(){
		
	}
	protected String doInBackground(String ...params){
		Log.d("getHttpString", "called");
		String thisURL = EC2_URL + params[0];
		
		currentPhotoId = Integer.parseInt(params[1]);
		body = params[2];
		Log.d("getHttpString", "params[0]: " + params[0] + " / params[1]" + params[1] + " / params[2]" + params[2]);
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
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			
			OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
			
			wr.write("body="+body+"&photo_id="+currentPhotoId);
			wr.flush();
			
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			
			wr.close();
			reader.close();
			
			Log.d("getHttpString", "post url connection made");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	/*InputStream in= null;
	try{
		in = new BufferedInputStream(urlConnection.getInputStream());
		Log.d("getHttpPhoto", "got input stream");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	byte[] contents = new byte[1024];
	int bytesRead =0;
	String strContents = null;
	try {
		while((bytesRead = in.read(contents)) != -1) { // <- NULL POINTER this line
			strContents= new String(contents, 0, bytesRead);
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	Log.d("getHttpPhoto", "about to return");
	Log.d("getHttpPhoto", "info: "+ strContents);
	return strContents;*/
	
	return null;
	
	}
	
	@Override
	protected void onPostExecute(String response){
		//Log.d("onPostExecute", "response: "+ response );
		super.onPostExecute(response);
		Log.d("onPostExecute", "called");	
	}
}
	

