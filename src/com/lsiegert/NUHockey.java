package com.lsiegert;

import java.util.List;
import java.util.Map;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class NUHockey extends TabActivity{
	private static final String TAG = "NUHockey";
	
       /** Called when the activity is first created. */
       @Override
       public void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
               setContentView(R.layout.main);

               // Check here for any updates
               try {
				checkForUpdates();
               } catch (Exception e) {
				Log.e(TAG, "Checking for updates failed: " + e.getMessage());
               }

               Resources res = getResources();
               TabHost tabHost = getTabHost();
               TabHost.TabSpec spec;
               Intent intent;

//               intent = new Intent().setClass(this, RosterActivity.class);
//
//               spec = tabHost.newTabSpec("roster").setIndicator("Roster",
//                               res.getDrawable(R.drawable.ic_tab_roster))
//                               .setContent(intent);
//               tabHost.addTab(spec);

               intent = new Intent().setClass(this, RSSActivity.class);

               spec = tabHost.newTabSpec("rss").setIndicator("RSS",
                               res.getDrawable(R.drawable.ic_tab_rss))
                               .setContent(intent);
               tabHost.addTab(spec);

               intent = new Intent().setClass(this, ScheduleActivity.class);

               spec = tabHost.newTabSpec("schedule").setIndicator("Schedule",
                               res.getDrawable(R.drawable.ic_tab_schedule))
                               .setContent(intent);
               tabHost.addTab(spec);

               intent = new Intent().setClass(this, StatsActivity.class);

               spec = tabHost.newTabSpec("stats").setIndicator("Your Stats",
                               res.getDrawable(R.drawable.ic_tab_stats))
                               .setContent(intent);
               tabHost.addTab(spec);

               tabHost.setCurrentTab(1);
       }
   

   private void checkForUpdates() {

       // First we check for any game updates
	   new DownloadGameUpdatesTask(NUHockey.this).execute();

       // Then check for roster updates
       // checkForRosterUpdates();
   }

   private void checkForRosterUpdates() {

       // RosterDbAdapter rosterDba = new RosterDbAdapter(this);
       // rosterDba.open();

       // When was the last time we checked? If more than 3 days, fetch updates. Otherwise finish.

       // Tell the user we are doing something with a wait thing.

       // Get the listof updates from somewhere

       // Remove all data from the roster database. For each update, put it into the database.

       // When finished, save last updated time.

       // rosterDba.close();
   }

   private List<Map<String,String>> fetchRosterUpdates() {

       // Make a http request for the html page

       // Parse the html and pull out the roster info

       // For each roster entry, return a Map.
	   return null;
   }
}