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

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends BaseActivity {
	
	private int photoID;
	private CommentAdapter c_adapter;
	private ArrayList<Comment> comments;
	private Context context;
	private ListView listComments;
	
	private String[] nameLookup = {"Kate", "Brian", "Ben", "Cong"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_comment);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoId");
	        //comments = extras.getStringArrayList("comments");
	    }
	    
	    listComments = (ListView) findViewById(R.id.commentList);
	    comments = new ArrayList<Comment>();
		
	    this.c_adapter = new CommentAdapter(this, R.layout.comment_list_item, comments);
	    listComments.setAdapter(c_adapter);
        
        context = this;
        updateComments();
	}
	
	// Gets the newest comments from the server
	public void updateComments() {
		c_adapter.clear();
        // Get the list of comments for this photo
	 	String httpGetAddOn = "/comments?photo_id=" + photoID;
		getComments request = new getComments(this, 1);
		request.execute(httpGetAddOn);
	}
	
	private class getComments extends AsyncTask<String, Object, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private CommentActivity thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
    	
    	public getComments(CommentActivity thisAct, int curUserId){
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
	    	Log.d("getComments", "called");
	    	String thisURL = EC2_URL + params[0];
	    	Log.d("getComments", "params[0]: " + params[0] );
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
	    		Log.d("getComments", "url connection made");
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	InputStream in= null;
	    	try{
	    		in = new BufferedInputStream(urlConnection.getInputStream());
	    		Log.d("getComments", "got input stream");
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
	
		            String body = c.getString("body");
		            int user_id = Integer.parseInt(c.getString("user_id"));
		            String user_name = nameLookup[user_id - 1];
					
		            c_adapter.add(new Comment(user_id, user_name, body));
				}
				c_adapter.notifyDataSetChanged();
				
	    		super.onPostExecute(s);
	    		Log.d("onPostExecute", "called");
	    		Log.d("null? ", (s == null) + "");
	    		Log.d("jsonarray value: ", s);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
	public void submitComment(View v) {
		sendPostComment("cool picture", photoID + "");
		updateComments();
		
//		TextView t = (TextView) findViewById(R.id.new_comment);
//		EditText e = (EditText) findViewById(R.id.commentText);
//		t.setText("You: " + e.getText().toString());
//		e.setText("");
//		InputMethodManager imm = (InputMethodManager)getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
//		
//		EditText commentEdit = (EditText)findViewById(R.id.commentText);
//		String body = commentEdit.getText().toString();
//		postHttpString serverTask = new postHttpString();
//
//		String[] args = new String[]{"/comments", ""+ photoID + "", body};
//		
//		serverTask.execute(args);
//		
//		finish();
	}
	
	private void sendPostComment(String body, String photo_id) {
	    postComments postCommentsAsyncTask = new postComments(this, 1);
	    postCommentsAsyncTask.execute(body, photo_id);
	}
		
	private class postComments extends AsyncTask<String, Void, String> {
    	public final static String AUTH_TOKEN = "cc0a0942c97e1c1e7c4eb4f2af8c70b1375557d9";	//kate's auth token
    	private final static String EC2_URL = MapPhotoActivity.EC2_URL;
    	private CommentActivity thisActivity;
    	private int currentUserID;
		private ProgressDialog dialog;
		
    	public postComments(CommentActivity thisAct, int curUserId){
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
            HttpPost httpPost = new HttpPost(EC2_URL);

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
            super.onPostExecute(result);

            if(result.equals("working")){
                Toast.makeText(getApplicationContext(), "HTTP POST is working...", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "Invalid POST req...", Toast.LENGTH_LONG).show();
            }
        }    
    }

	private class CommentAdapter extends ArrayAdapter<Comment> {
		private List<Comment> comments;
		private LayoutInflater vi;
		public CommentAdapter(Context context, int textViewResourceId, List<Comment> comments) {
			super(context, textViewResourceId, comments);
			vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.comments = comments;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				v = vi.inflate(
						R.layout.comment_list_item, null);
			}
			Comment c = comments.get(position);
			if (c != null) {
				TextView un = (TextView) v.findViewById(R.id.user_name);
				TextView ub = (TextView) v.findViewById(R.id.user_body);
				if (un != null) {
					un.setText(c.getName());
				}
				if (ub != null) {
					ub.setText(c.getBody());
				}
			}
			return v;
		}
	}
}
