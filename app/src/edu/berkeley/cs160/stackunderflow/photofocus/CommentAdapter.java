package edu.berkeley.cs160.stackunderflow.photofocus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<String> {
	  private final Context context;
	  private final String[] values;
	
	  public CommentAdapter(Context context, String[] values) {
	    super(context, R.layout.listitem_comment, values);
	    this.context = context;
	    this.values = values;
	  }
	
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.listitem_comment, parent, false);
			TextView textView = (TextView) rowView.findViewById(R.id.comment);
			textView.setText(values[position]);
			// Change the icon for Windows and iPhone
		
	  return rowView;
	  }
} 