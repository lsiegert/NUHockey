package com.lsiegert;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class ScheduleActivity extends ListActivity {
	private DatabaseHelper dbHelper = null;
	private ListView listView = null;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = getListView();
        listView.setItemsCanFocus(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        if (dbHelper == null) {
        	dbHelper = new DatabaseHelper(this);
        }

		dbHelper.openDatabase();
		
		Cursor games = dbHelper.getAllGames();
		
		startManagingCursor(games);
		ScheduleAdapter adapter = new ScheduleAdapter(this, R.layout.schedule_row, games, null, null, dbHelper);

		setListAdapter(adapter);
    }
	
	
}