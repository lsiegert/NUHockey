package com.lsiegert;

import java.util.ArrayList;
import java.util.List;

import com.lsiegert.BaseFeedParser;
import com.lsiegert.Message;
import com.lsiegert.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

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
}