package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class MyPhotosActivity extends BaseActivity{
	
	private int photoID;
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
	    
	    ImageView image = (ImageView) findViewById(R.id.picture);
	    image.setImageResource(R.drawable.campanile);
	}
	
	public void openComments() {
		Intent intent = new Intent(this, CommentActivity.class);
		intent.putExtra("photoId", photoID);
		startActivity(intent);
	}
	
	public void submitComment() {
		//http requests to server
	}
	 
}
