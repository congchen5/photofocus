package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

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
	    
	    ImageView image = (ImageView) findViewById(R.id.picture);
	    image.setImageResource(R.drawable.campanile);
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
	 
}
