package edu.berkeley.cs160.stackunderflow.photofocus;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(230, 230));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
    		R.drawable.berkeley_campanile, R.drawable.berkeley_law,
    		R.drawable.berkeley_marina, R.drawable.berkeley_pool,
    		R.drawable.campanile, R.drawable.bell_tower_night,
    		R.drawable.berkeley_sather_gate, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,
    		R.drawable.berkeley_walk, R.drawable.berkeley_walk,

    };
}