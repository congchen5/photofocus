package edu.berkeley.cs160.stackunderflow.photofocus;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

public abstract class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
}
