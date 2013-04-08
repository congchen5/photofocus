package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class PhotoDetailsActivity extends Activity{
	
	private String photoID;
	private ArrayList comments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_photo_details);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getString("photoID");
	        comments = extras.getStringArrayList("comments");
	    }
	    
	    ImageView image = (ImageView) findViewById(R.id.picture);
	    image.setImageResource(R.drawable.campanile);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	
	public void submitComment() {
		//http requests to server
	}
	 
}
