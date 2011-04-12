package com.lsiegert;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.TextView;

public class StatsActivity extends Activity {
    private DatabaseHelper myDbHelper;
	private TextView textview;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	myDbHelper = new DatabaseHelper(this);
    	
    	textview = new TextView(this);
    	setContentView(textview);
    	
		try {
			myDbHelper.openDatabase();
		} catch(SQLException sqle){
			throw sqle;
		}
    }
    
    public void onResume() {
    	super.onResume();
    	String record = myDbHelper.getRecord();
				
        textview.setText(record);
    }
}