package edu.berkeley.cs160.stackunderflow.photofocus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPhotosActivity extends BaseActivity{
	
	private int photoID;
	private final int EMPTY_STATE = 0;
	private final int MAP_STATE = 1;
	private final int DESCRIPTION_STATE = 2;
	private int descriptionState = EMPTY_STATE;
	//private ArrayList comments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_my_photos);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoId");
	        //comments = extras.getStringArrayList("comments");
	    }
	    
//	    ImageView image = (ImageView) findViewById(R.id.picture);
//	    image.setImageResource(R.drawable.berkeley_pool);
	    
	    // get the photo
	 	String httpGetAddOn = "/photos/" + photoID + "?show=image";
		getPhoto request = new getPhoto(this, photoID);
		request.execute(httpGetAddOn);
		
	    // get the notes
	 	httpGetAddOn = "/notes?photo_id=" + photoID;
		getNotes request2 = new getNotes(this, photoID);
		request2.execute(httpGetAddOn);
	    
	}
	
	public void updateNote(View v) {
		Intent intent = new Intent(this, EditNoteActivity.class);
		intent.putExtra("photoId", photoID);
		startActivity(intent);
		
//		TextView notes = (TextView) findViewById(R.id.descriptionOverlay);
//		EditText e = (EditText) findViewById(R.id.noteText);
//		notes.setText(notes.getText() + "\n" + e.getText());
//		e.setText("");
//		e.clearFocus();
//		
//		InputMethodManager imm = (InputMethodManager)getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
	}
	
	public void openComments() {
		Intent intent = new Intent(this, CommentActivity.class);
		intent.putExtra("photoId", photoID);
		startActivity(intent);
	}
	
	public void submitComment() {
		//http requests to server
	}
	
	private class getPhoto extends AsyncTask<String, Object, Bitmap> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private MyPhotosActivity thisActivity;
    	private int currentPhotoId;
		private ProgressDialog dialog;
    	
    	public getPhoto(MyPhotosActivity thisAct, int curPhotoId){
    		thisActivity = thisAct;
    		currentPhotoId = curPhotoId;
    	}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(thisActivity);
	        this.dialog.setMessage("Loading...");
	        this.dialog.show();
	    }
		
    	protected Bitmap doInBackground(String ...params){
	    	Log.d("getPhotos", "called");
	    	String thisURL = EC2_URL + params[0];
	    	Log.d("getPhotos", "params[0]: " + params[0] );
	    	URL url = null;
	    	try {
	    		url = new URL(thisURL);
	    	} catch (MalformedURLException e) {
	    		e.printStackTrace();
	    	}
	    	HttpURLConnection urlConnection = null;
	    	try {
	
	    		urlConnection = (HttpURLConnection) url.openConnection();
	    		urlConnection.setRequestProperty("auth_token", AUTH_TOKEN);
	    		urlConnection.setRequestMethod("GET");
	    		Log.d("getPhoto", "url connection made");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	Bitmap bmp = null;
	    	InputStream in= null;
	    	try{
	    		in = new BufferedInputStream(urlConnection.getInputStream());
	    		Log.d("getPhoto", "got input stream");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	bmp = BitmapFactory.decodeStream(in);
	    	if (bmp ==null){
	    		Log.d("getPhoto", "bmp is null");
	    	}else{
	    		Log.d("getPhoto", "bmp is not null");
	    	}
	    	return bmp;
    	}
    	
    	@Override
    	protected void onPostExecute(Bitmap bmp) {
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
			
    		super.onPostExecute(bmp);
    		Log.d("onPostExecute", "called");
    		Log.d("got the picture for picture id: " + currentPhotoId, "");

    		ImageView imageView = (ImageView) findViewById(R.id.picture);
    		imageView.setId(currentPhotoId);
    	    imageView.setImageBitmap(bmp);
    	}
    }
	
	private class getNotes extends AsyncTask<String, Object, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private MyPhotosActivity thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
    	
    	public getNotes(MyPhotosActivity thisAct, int curUserId){
    		thisActivity = thisAct;
    		currentUserID = curUserId;
    	}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(thisActivity);
	        this.dialog.setMessage("Loading...");
	        this.dialog.show();
	    }
		
    	protected String doInBackground(String ...params) {
	    	Log.d("getNotes", "called");
	    	String thisURL = EC2_URL + params[0];
	    	Log.d("getNotes", "params[0]: " + params[0] );
	    	URL url = null;
	    	try {
	    		url = new URL(thisURL);
	    	} catch (MalformedURLException e) {
	    		e.printStackTrace();
	    	}
	    	HttpURLConnection urlConnection = null;
	    	try {
	
	    		urlConnection = (HttpURLConnection) url.openConnection();
	    		urlConnection.setRequestProperty("auth_token", AUTH_TOKEN);
	    		urlConnection.setRequestMethod("GET");
	    		Log.d("getNotes", "url connection made");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	InputStream in= null;
	    	try{
	    		in = new BufferedInputStream(urlConnection.getInputStream());
	    		Log.d("getNotes", "got input stream");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	    	StringBuilder sb = new StringBuilder();

	    	String line = null;
	    	try {
				while ((line = reader.readLine()) != null)
				{
				    sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	String result = sb.toString();
	    	return result;
    	}
    	
    	@Override
    	protected void onPostExecute(String s) {
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
			try {
				
		  		super.onPostExecute(s);
	    		Log.d("onPostExecute", "called");
	    		Log.d("null? ", (s == null) + "");
	    		Log.d("jsonarray value: ", s);
	    		
				JSONArray ja = new JSONArray(s);
				
				if (ja.length() > 1) {
					Log.d("ERROR!", "SHOULD ONLY HAVE ONE NOTE PER PHOTO");
				}
				
				JSONObject c = ja.getJSONObject(0);

	            String body = c.getString("body");
	            int user_id = Integer.parseInt(c.getString("user_id"));
	            String user_name = nameLookup[user_id - 1];
				
	            ImageView profile = (ImageView) findViewById(R.id.profile_pic);
	            setProfilePicture(profile, user_id);
	            
	            TextView textOverlay = (TextView) findViewById(R.id.textOverlay);
	            textOverlay.setText(user_name + "\n" + body);
	  
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
}
