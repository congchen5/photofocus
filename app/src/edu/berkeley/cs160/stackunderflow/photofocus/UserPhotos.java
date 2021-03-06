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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class UserPhotos extends BaseActivity {
	
	private ArrayList<Integer> photoIds = new ArrayList<Integer>();
	private ArrayList<Bitmap> imgs = new ArrayList<Bitmap>();
	private ImageAdapter imgAdapter;
	private int listCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_photos);
		
	    GridView gridview = (GridView) findViewById(R.id.user_gallery);
	    imgAdapter = new ImageAdapter(this, imgs);
	    gridview.setAdapter(imgAdapter);
	    
	    // Make async request, user is hardcoded in
	 	String httpGetAddOn = "/photos?user_id=1";
		getPhotoMetaData request = new getPhotoMetaData(this, 1);
		request.execute(httpGetAddOn);
		
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//	            Toast.makeText(UserPhotos.this, "" + position, Toast.LENGTH_SHORT).show();
            	
	            Intent myIntent = new Intent(UserPhotos.this, PhotoDetailsActivity.class);
            	UserPhotos.this.startActivity(myIntent);
	        }
	    });
	}
	
	private class getPhotoMetaData extends AsyncTask<String, Object, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private UserPhotos thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
    	
    	public getPhotoMetaData(UserPhotos thisAct, int curUserId){
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
	    	
	    	InputStream in= null;
	    	try{
	    		in = new BufferedInputStream(urlConnection.getInputStream());
	    		Log.d("getPhoto", "got input stream");
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
				JSONArray ja = new JSONArray(s);
				
				for (int i = 0; i < ja.length(); i++) {
					JSONObject c = ja.getJSONObject(i);
	
		            String photo_id = c.getString("photo_id");
		            photoIds.add(Integer.parseInt(photo_id));
				}
				
	    		super.onPostExecute(s);
	    		Log.d("onPostExecute", "called");
	    		Log.d("null? ", (ja == null) + "");
	    		Log.d("jsonarray value: ", s);
	    		Log.d("photoIds: ", photoIds.toString());
	    		
	    		listCount = photoIds.size();
	    		
	            for (int i = 0; i < photoIds.size(); i++) {
	            	
	            	// Make get request for image
	    			String httpGetAddOn = "/photos/" + photoIds.get(i) + "?show=image";
	    			getPhoto request = new getPhoto(thisActivity, photoIds.get(i));
	    			request.execute(httpGetAddOn);
	            }
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
	
	private class getPhoto extends AsyncTask<String, Object, Bitmap> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private UserPhotos thisActivity;
    	private int currentPhotoId;
		private ProgressDialog dialog;
    	
    	public getPhoto(UserPhotos thisAct, int curPhotoId){
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
    	    
    	    imgs.add(bmp);
    	    
    	    --listCount;
    	    if (listCount == 0) {
    	    	Log.d("debug", "notifyDataSetChanged called");
    	    	Log.d("size", "" + imgs.size());
    	    	imgAdapter.notifyDataSetChanged();
    	    }
    	}
    }
	
	private class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private ArrayList<Bitmap> images;

	    public ImageAdapter(Context c, ArrayList<Bitmap> i) {
	        mContext = c;
	        images = i;
	    }

	    public int getCount() {
	        return images.size();
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(230, 230));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(2, 2, 2, 2);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageBitmap(images.get(position));
	        
	    	OnPictureClickListener l = new OnPictureClickListener(photoIds.get(position));
	    	imageView.setOnClickListener(l);
	        return imageView;
	    }
	    
    	public class OnPictureClickListener implements OnClickListener {
    		private int pictureId;
    		public OnPictureClickListener(int pictureId) {
    			this.pictureId = pictureId;
    		}
    		
			@Override
			public void onClick(View v) {
				Intent intnt = new Intent(UserPhotos.this, MyPhotosActivity.class);
				intnt.putExtra("photoId", pictureId);
				startActivity(intnt);
			}
    	}
	}
}
