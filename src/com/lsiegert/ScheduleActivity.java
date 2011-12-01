package com.lsiegert;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class ScheduleActivity extends ListActivity {
	private GamesDbAdapter dbHelper = null;
	private ListView listView = null;
	private static final String TAG = "NUHockey";
	private SeparatedListAdapter adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listView = getListView();
		listView.setItemsCanFocus(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		if (dbHelper == null) {
			dbHelper = new GamesDbAdapter(this);
		}

		dbHelper.open();

		Cursor seasons = getAllSeasons();
		startManagingCursor(seasons);
		String season = seasons.getString(0);
		showGamesBySeason(season);
	}

	private Cursor getAllSeasons() {
		Cursor seasons = dbHelper.getAllSeasons();
		startManagingCursor(seasons);
		seasons.moveToFirst();
		return seasons;
	}

	private void showGamesBySeason(String season) {
		adapter = new SeparatedListAdapter(this);
		String record = dbHelper.getRecordBySeason(season);
		Cursor games = dbHelper.getGamesBySeason(season);
		startManagingCursor(games);
		ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, R.layout.schedule_row, games, null, null, dbHelper);
		adapter.addSection(season + " (" + record + ")", scheduleAdapter);
		setListAdapter(adapter);
	}
	
	private void showGamesByOpponent() {
		adapter = new SeparatedListAdapter(this);
		Cursor opponents = dbHelper.getAllOpponents();
		startManagingCursor(opponents);

		opponents.moveToFirst();
		while (!opponents.isAfterLast()) {
			String opponent = opponents.getString(0);
			String record = dbHelper.getRecordByOpponent(opponent);
			Cursor games = dbHelper.getGamesByOpponent(opponent);
			startManagingCursor(games);
			ScheduleAdapter scheduleAdapter = new ScheduleAdapter(this, R.layout.schedule_row, games, null, null, dbHelper);
			adapter.addSection(opponent + " (" + record + ")", scheduleAdapter);
			opponents.moveToNext();
		}

		setListAdapter(adapter);
	}

   	@Override
   	public boolean onCreateOptionsMenu(Menu menu) {
   	    MenuInflater inflater = getMenuInflater();
   	    inflater.inflate(R.menu.menu, menu);
   	    return true;
   	}
   	
   	@Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   	    // Handle item selection
   	    switch (item.getItemId()) {
   	    case R.id.info:
   	    	showInfo();
   	        return true;
   	    case R.id.changeseason:
   	    	changeSeason();
   	    	return true;
   	    case R.id.sort:
   	    	sort();
   	    	return true;
   	    case R.id.update:
   	    	try {
				checkForUpdates();
			} catch (Exception e) {
				Log.d(TAG, "checking for updates failed");
				e.printStackTrace();
			}
   	        return true;
   	    default:
   	        return super.onOptionsItemSelected(item);
   	    }
   	}

   private void showInfo() {
	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   builder.setMessage(R.string.info_text)
	          .setCancelable(true);
	   AlertDialog alert = builder.create();
	   alert.show();
	}
   
   private void sort() {
	   final CharSequence[] items = {"Season", "Opponent"};
	   
	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   builder.setTitle("View games by");
	   builder.setItems(items, new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int item) {
	    	   String s = items[item].toString();
	    	   if (s.equalsIgnoreCase("Season")) {
	    		   showGamesBySeason("2011-12");
	    	   } else {
	    		   showGamesByOpponent();
	    	   }
	    	   listView.requestLayout();
	       }
	   });
	   AlertDialog alert = builder.create();
	   alert.show();
   }
   
   private void changeSeason() {
	   final CharSequence[] items = {"2011-12", "2010-11", "2009-10", "2008-09", "2007-08", "2006-07", "2005-06", "2004-05"};
	   
	   AlertDialog.Builder builder = new AlertDialog.Builder(this);
	   builder.setTitle("Choose a season");
	   builder.setItems(items, new DialogInterface.OnClickListener() {
	       public void onClick(DialogInterface dialog, int item) {
	           showGamesBySeason(items[item].toString());
	           listView.requestLayout();
	       }
	   });
	   AlertDialog alert = builder.create();
	   alert.show();
   }


   private void checkForUpdates() {
	   new DownloadGameUpdatesTask(ScheduleActivity.this).execute();
   }
}