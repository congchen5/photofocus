package edu.berkeley.cs160.stackunderflow.photofocus;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class getAddressFromLatLng extends AsyncTask<String, Object, LatLng>{
	public String srchText;
	public MapPhotoActivity  thisAct;
	public LatLng searchLatLng;
	
	public getAddressFromLatLng(String searchText, MapPhotoActivity thisMapPhoto){
		this.srchText = searchText;
		this.thisAct = thisMapPhoto;
	}
	@Override
	protected LatLng doInBackground(String... arg0) {
		Log.d("getAddrLatLng", "called");
		double searchLat =0;
		double searchLng =0;
		HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + srchText+ "&sensor=false");
		Log.d("getAddrLatLng", "Http Get made: " + httpGet);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder strBuilder = new StringBuilder();
		
		try{
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1){
				strBuilder.append((char) b);
			}
			Log.d("getAddrLatLng", "while finished");
			Log.d("getAddrLatLng", "string: " + strBuilder.toString());
		}catch (ClientProtocolException e){
				e.printStackTrace();
		}catch (IOException e){
				e.printStackTrace();
		}
		JSONObject jsonOb = new JSONObject();
		try{
			jsonOb = new JSONObject(strBuilder.toString());
			searchLng = ((JSONArray) jsonOb.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
			Log.d ("getAddrLatLng" ,"long: " + searchLng);
			searchLat = ((JSONArray) jsonOb.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
			Log.d ("getAddrLatLng" ,"lat: " + searchLat);

		}catch(JSONException e){
			e.printStackTrace();
		}
		searchLatLng = new LatLng(searchLat, searchLng);
		return searchLatLng;
	}
	@Override
	protected void onPostExecute(LatLng latLngObj){
		super.onPostExecute(latLngObj);
		Log.d("getAddrLatLng", "ONpostExecute called");
		thisAct.searchLatLng = (LatLng) latLngObj;
		thisAct.cameraPosition = new CameraPosition (searchLatLng, 14,0,0);
		thisAct.updateMap();
	}
	
}

