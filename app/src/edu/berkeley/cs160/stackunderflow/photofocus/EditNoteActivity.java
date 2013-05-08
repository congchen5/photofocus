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
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditNoteActivity extends BaseActivity {
	private int photoID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoId");
	        //comments = extras.getStringArrayList("comments");
	    }
	    
	    // get the notes
	 	String httpGetAddOn = "/notes?photo_id=" + photoID;
		getNotes request2 = new getNotes(this, photoID);
		request2.execute(httpGetAddOn);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_note, menu);
		return true;
	}
	
	public void updateNote(View v) {
		Log.d("updateNote", "called");
		EditText e = (EditText) findViewById(R.id.editNote);
		sendPostNote(e.getText().toString(), photoID + "");
		e.setText("");
		
		Intent intent = new Intent(this, MyPhotosActivity.class);
		intent.putExtra("photoId", photoID);
		startActivity(intent);
	}
	
	public void cancelNoteUpdate(View v) {
		Log.d("cancelNoteUpdate", "called");
		finish();
	}
	
	private class getNotes extends AsyncTask<String, Object, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private EditNoteActivity thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
    	
    	public getNotes(EditNoteActivity thisAct, int curUserId){
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
	    		
	    		if (s != null) {
					JSONArray ja = new JSONArray(s);
					
					if (ja.length() > 1) {
						Log.d("ERROR!", "SHOULD ONLY HAVE ONE NOTE PER PHOTO");
					}
					
					JSONObject c = ja.getJSONObject(0);
	
		            String body = c.getString("body");
		            int user_id = Integer.parseInt(c.getString("user_id"));
		            
		            TextView textOverlay = (TextView) findViewById(R.id.editNote);
		            textOverlay.setText(body);
	    		}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
	
	private void sendPostNote(String body, String photo_id) {
	    postNote postNoteAsyncTask = new postNote(this, 1);
	    postNoteAsyncTask.execute(body, photo_id);
	}
	
	private class postNote extends AsyncTask<String, Void, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private EditNoteActivity thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
		
    	public postNote(EditNoteActivity thisAct, int curUserId){
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
	    	Log.d("postComments", "called");
	    	
	    	String paramBody = params[0];
	    	String paramPhotoId = params[1];
	    	
	    	System.out.println("*** doInBackground *** paramBody: " + paramBody + " paramPhotoId: " + paramPhotoId);
	    	
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(EC2_URL + "/notes");
            

            BasicNameValuePair bodyBasicNameValuePair = new BasicNameValuePair("body", paramBody);
            BasicNameValuePair photoIdBasicNameValuePair = new BasicNameValuePair("photo_id", paramPhotoId);
            
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(bodyBasicNameValuePair);
            nameValuePairList.add(photoIdBasicNameValuePair);
            
            try {
                // UrlEncodedFormEntity is an entity composed of a list of url-encoded pairs. 
                //This is typically useful while sending an HTTP POST request. 
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);

                // setEntity() hands the entity (here it is urlEncodedFormEntity) to the request.
                httpPost.setEntity(urlEncodedFormEntity);
                httpPost.setHeader("auth_token", AUTH_TOKEN);

                try {
                    // HttpResponse is an interface just like HttpPost.
                    //Therefore we can't initialize them
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    // According to the JAVA API, InputStream constructor do nothing. 
                    //So we can't initialize InputStream although it is not an interface
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String bufferedStrChunk = null;

                    while((bufferedStrChunk = bufferedReader.readLine()) != null){
                        stringBuilder.append(bufferedStrChunk);
                    }

                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("First Exception caz of HttpResponese :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second Exception caz of HttpResponse :" + ioe);
                    ioe.printStackTrace();
                }
            } catch (UnsupportedEncodingException uee) {
                System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
                uee.printStackTrace();
            }
            return null;
    	}
    	
        @Override
        protected void onPostExecute(String result) {
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
        	super.onPostExecute(result);
        }    
    }

}
