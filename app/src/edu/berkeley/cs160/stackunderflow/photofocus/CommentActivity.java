package edu.berkeley.cs160.stackunderflow.photofocus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class CommentActivity extends BaseActivity {
	
	private int photoID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_comment);
	    
	    Bundle extras = getIntent().getExtras();
	    if (extras != null) {
	        photoID = extras.getInt("photoID");
	        //comments = extras.getStringArrayList("comments");
	    }
	}
	
	public void submitComment(View v) {
		EditText commentEdit = (EditText)findViewById(R.id.commentText);
		String body = commentEdit.getText().toString();
		postHttpString serverTask = new postHttpString();
		serverTask.execute("/comments?body="+body+"photoid="+photoID);
	}
}
