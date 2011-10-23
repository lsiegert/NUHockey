package com.lsiegert;

import com.lsiegert.R;
import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ListView;

public class RosterActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roster);
        
        ListView list = (ListView) findViewById(R.id.RosterListView);
        
        GamesDbAdapter myDbHelper = new GamesDbAdapter(this);

		try {
			myDbHelper.open();
		} catch(SQLException sqle){
			throw sqle;
		}
		
        Cursor players = myDbHelper.getAllPlayers();
        
        RosterAdapter adapter = new RosterAdapter(this, players);
        
        list.setAdapter(adapter);
    }
}