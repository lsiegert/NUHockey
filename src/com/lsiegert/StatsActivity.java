package com.lsiegert;

import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class StatsActivity extends ListActivity {
	private GamesDbAdapter dbHelper = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getListView();
        
        if (dbHelper == null) {
        	dbHelper = new GamesDbAdapter(this);
        }

		dbHelper.open();
    }

	public final static String ITEM_TITLE = "title";  
    public final static String ITEM_CAPTION = "caption";  
  
    public Map<String,?> createItem(String title, String caption) {  
        Map<String,String> item = new HashMap<String,String>();  
        item.put(ITEM_TITLE, title);  
        item.put(ITEM_CAPTION, caption);  
        return item;  
    }  
    
    public void onResume() {
    	super.onResume();
    	String record = dbHelper.getRecord();
    	String homeRecord = dbHelper.getRecordByLocation("home");
    	String awayRecord = dbHelper.getRecordByLocation("away");
    	String neutralRecord = dbHelper.getRecordByLocation("neutral");
    	int winsAtAgganis = dbHelper.getNumOfWinsAtagganis();
    	int numTeams = dbHelper.getNumOfTeams();
    	int goals = dbHelper.getNumOfGoals();
    	int shutouts = dbHelper.getNumOfShutouts();
    
        // create our list and custom adapter  
        SeparatedListAdapter adapter = new SeparatedListAdapter(this);
        adapter.addSection("Team's Record", new ArrayAdapter<String>(this,  
            R.layout.list_item, new String[] { "Overall: " + record,
        									   "Home: " + homeRecord,
        									   "Away: " + awayRecord,
        									   "Neutral: " + neutralRecord}));
        
        adapter.addSection("Statistics", new ArrayAdapter<String>(this,  
                R.layout.list_item, new String[] { "Goals: " + goals,
            									   "Shutouts: " + shutouts,
            									   "Opponents: " + numTeams,
            									   "Wins at agganis: " + winsAtAgganis})); 
  
        setListAdapter(adapter);
    }
}