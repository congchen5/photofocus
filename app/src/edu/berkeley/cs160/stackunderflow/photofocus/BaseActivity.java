package edu.berkeley.cs160.stackunderflow.photofocus;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        getActionBar().setHomeButtonEnabled(true);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case android.R.id.home:
    		Intent i1 = new Intent(BaseActivity.this, MainActivity.class);
    		BaseActivity.this.startActivity(i1);
    		return true;
    	case R.id.menu_my_photos:
    		Intent i2 = new Intent(BaseActivity.this, MyPhotosActivity.class);
    		BaseActivity.this.startActivity(i2);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
}
