package edu.berkeley.cs160.stackunderflow.photofocus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentActivity extends BaseActivity {
	
	private int photoID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_comment);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoId");
	        //comments = extras.getStringArrayList("comments");
	    }
	}
	
	public void submitComment(View v) {
		
		TextView t = (TextView) findViewById(R.id.new_comment);
		EditText e = (EditText) findViewById(R.id.commentText);
		t.setText("You: " + e.getText().toString());
		e.setText("");
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
		
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
}
