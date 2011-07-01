package com.lsiegert;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RSSActivity extends ListActivity {
	private List<Message> messages;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.rss);
		loadFeed();
    }
    
    private void loadFeed(){
    	try{
	    	BaseFeedParser parser = new BaseFeedParser();
	    	messages = parser.parse();
	    	List<String> titles = new ArrayList<String>(messages.size());
	    	for (Message msg : messages){
	    		titles.add(msg.getTitle());
	    	}
	    	ArrayAdapter<String> adapter = 
	    		new ArrayAdapter<String>(this, R.layout.rss_row, titles);
	    	this.setListAdapter(adapter);
    	} catch (Throwable t){
    		Log.e("NUHockey",t.getMessage(),t);
    	}
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Message m = messages.get(position);
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(m.getLink().toString()));
    	startActivity(browserIntent);
    }
}