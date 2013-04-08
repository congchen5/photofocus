package edu.berkeley.cs160.stackunderflow.photofocus;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends BaseActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button button1 = (Button) findViewById(R.id.button_start_photo_session);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MainActivity.this, PhotoSession.class);
//            	myIntent.putExtra("key", value); //Optional parameters
            	MainActivity.this.startActivity(myIntent);
            }
        });
        
        final Button button2 = (Button) findViewById(R.id.button_find_a_spot_near_me);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent(MainActivity.this, MapPhotoActivity.class);
//            	myIntent.putExtra("key", value); //Optional parameters
            	MainActivity.this.startActivity(myIntent);
            }
        });
    }
}
