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
		
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		Cursor seasons = dbHelper.getAllSeasons();
		startManagingCursor(seasons);
		
		seasons.moveToFirst();
		while (!seasons.isAfterLast()) {
			String season = seasons.getString(0);
			Cursor games = dbHelper.getGamesBySeason(season);
			startManagingCursor(games);
			ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, R.layout.schedule_row, games, null, null, dbHelper);
			adapter.addSection(season, scheduleAdapter);
			seasons.moveToNext();
		}
		
		setListAdapter(adapter);
    }
}