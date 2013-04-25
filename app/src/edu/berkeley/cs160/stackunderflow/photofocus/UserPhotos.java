package edu.berkeley.cs160.stackunderflow.photofocus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class UserPhotos extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_photos);
		
	    GridView gridview = (GridView) findViewById(R.id.user_gallery);
	    gridview.setAdapter(new ImageAdapter(this));
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//	            Toast.makeText(UserPhotos.this, "" + position, Toast.LENGTH_SHORT).show();
            	
	            Intent myIntent = new Intent(UserPhotos.this, PhotoDetailsActivity.class);
            	UserPhotos.this.startActivity(myIntent);
	        }
	    });
	}

}
