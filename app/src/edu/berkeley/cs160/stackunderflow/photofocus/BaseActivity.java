package edu.berkeley.cs160.stackunderflow.photofocus;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

public abstract class BaseActivity extends Activity {
	public String[] nameLookup = {"Kate", "Ben", "Cong", "Brian"};

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
    		Intent i2 = new Intent(BaseActivity.this, UserPhotos.class);
    		BaseActivity.this.startActivity(i2);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void setProfilePicture(ImageView imageReference, int userId) {
    	if (userId == 1)
    		imageReference.setImageDrawable(getResources().getDrawable(R.drawable.kate_profile));
    	else if (userId == 2)
    		imageReference.setImageDrawable(getResources().getDrawable(R.drawable.ben_profile));
    	else if (userId == 3)
    		imageReference.setImageDrawable(getResources().getDrawable(R.drawable.cong_profile));
    	else if (userId == 4)
    		imageReference.setImageDrawable(getResources().getDrawable(R.drawable.brian_profile));
    }
    
}
