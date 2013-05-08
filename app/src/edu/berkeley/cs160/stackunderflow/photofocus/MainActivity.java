package edu.berkeley.cs160.stackunderflow.photofocus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity {
	
	private int numDisplay = 7;
	private LinearLayout nearbyPictures;
//	private List<Picture> nearbyPictures;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        nearbyPictures = (LinearLayout) findViewById(R.id.gallery);
        
        for (int i = 1; i < numDisplay; i++) {
        	
			// add some sample static markers
			String httpGetAddOn = "/photos/" + i + "?show=image";
			
			getPhoto request = new getPhoto(this, i);
			request.execute(httpGetAddOn);	
        }
        
        final Button button1 = (Button) findViewById(R.id.button_start_photo_session);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MainActivity.this, PhotoSession.class);
//            	myIntent.putExtra("key", value); //Optional parameters
            	MainActivity.this.startActivity(myIntent);
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.button_find_nearby_photos);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MainActivity.this, MapPhotoActivity.class);
//            	myIntent.putExtra("key", value); //Optional parameters
            	MainActivity.this.startActivity(myIntent);
            }
        });
    }
    
    private class getPhoto extends AsyncTask<String, Object, Bitmap> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private MainActivity thisActivity;
    	private int currentPhotoId;
    	
    	public getPhoto(MainActivity thisAct, int curPhotoId){
    		thisActivity = thisAct;
    		currentPhotoId = curPhotoId;
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
    	protected void onPostExecute(Bitmap bmp){
    		super.onPostExecute(bmp);
    		Log.d("onPostExecute", "called");
    		Log.d("got the picture for picture id: " + currentPhotoId, "");
    		
    		ImageView imageView = new ImageView(getApplicationContext());
    		imageView.setId(currentPhotoId);
    	    imageView.setLayoutParams(new LayoutParams(800, LayoutParams.MATCH_PARENT));
    	    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	    imageView.setPadding(25, 10, 25, 10);
    	    imageView.setImageBitmap(bmp);
    		
    	    if (bmp != null) {
    	    	nearbyPictures.addView(imageView);
    	    	
    	    	OnPictureClickListener l = new OnPictureClickListener(currentPhotoId);
    	    	imageView.setOnClickListener(l);
    	    }
    	}
    	
    	public class OnPictureClickListener implements OnClickListener {
    		private int pictureId;
    		public OnPictureClickListener(int pictureId) {
    			this.pictureId = pictureId;
    		}
    		
			@Override
			public void onClick(View v) {
				Intent intnt = new Intent(MainActivity.this, PhotoDetailsActivity.class);
				intnt.putExtra("photoId", pictureId);
				startActivity(intnt);
			}
    	}
    }
}
