package edu.berkeley.cs160.stackunderflow.photofocus;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPhotosActivity extends BaseActivity{
	
	private int photoID;
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
	    
	    ImageView image = (ImageView) findViewById(R.id.picture);
	    image.setImageResource(R.drawable.berkeley_pool);
	}
	
	public void updateNotes(View v) {
		TextView notes = (TextView) findViewById(R.id.pictureOverlay);
		EditText e = (EditText) findViewById(R.id.noteText);
		notes.setText(notes.getText() + "\n" + e.getText());
		e.setText("");
		e.clearFocus();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
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
