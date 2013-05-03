package edu.berkeley.cs160.stackunderflow.photofocus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoDetailsActivity extends BaseActivity{
	
	private int photoID;
	private final int EMPTY_STATE = 0;
	private final int MAP_STATE = 1;
	private final int DESCRIPTION_STATE = 2;
	private int descriptionState = EMPTY_STATE;
	//private ArrayList comments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_photo_details);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoId");
	        //comments = extras.getStringArrayList("comments");
	    }

	    // add some sample static markers
	 	String httpGetAddOn = "/photos/" + photoID + "?show=image";
		getPhoto request = new getPhoto(this, photoID);
		request.execute(httpGetAddOn);
		
		// For debugging purposes
//	    Toast.makeText(this, "ImageId received: " + photoID, Toast.LENGTH_SHORT).show();
	}
	
	public void openComments(View v) {
		Intent intent = new Intent(this, CommentActivity.class);
		intent.putExtra("photoId", photoID);
		startActivity(intent);
	}
	
	public void submitComment() {
		//http requests to server
	}
	
	public void toggleDescription(View v) {
		View view = findViewById(R.id.descriptionOverlay);
		ImageView map = (ImageView) findViewById(R.id.map);
		
		if (descriptionState == EMPTY_STATE) {
			map.setVisibility(View.VISIBLE);
			descriptionState = MAP_STATE;
		}
		else if (descriptionState == MAP_STATE) {
			view.setVisibility(View.VISIBLE);
			descriptionState = DESCRIPTION_STATE;
		}
		else if (descriptionState == DESCRIPTION_STATE) {
			view.setVisibility(View.INVISIBLE);
			map.setVisibility(View.INVISIBLE);
			descriptionState = EMPTY_STATE;
		}
		else {
			//NOT A VALID STATE
			Log.e("PhotoDetailsActivity", "Not a valid description state");
		}
	}
	
	private class getPhoto extends AsyncTask<String, Object, Bitmap> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private PhotoDetailsActivity thisActivity;
    	private int currentPhotoId;
		private ProgressDialog dialog;
    	
    	public getPhoto(PhotoDetailsActivity thisAct, int curPhotoId){
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
}
