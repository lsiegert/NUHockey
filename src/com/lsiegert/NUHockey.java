package com.lsiegert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class NUHockey extends TabActivity {
       /** Called when the activity is first created. */
       @Override
       public void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
               setContentView(R.layout.main);

               // Check here for any updates
               try {
				checkForUpdates();
               } catch (Exception e) {
				e.printStackTrace();
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

   private void checkForUpdates() throws Exception {

       // First we check for any game updates
       checkForGameUpdates();

       // Then check for roster updates
       // checkForRosterUpdates();
   }

   private void checkForGameUpdates() throws Exception {

       GamesDbAdapter gamesDba = new GamesDbAdapter(this);
       gamesDba.open();

       // First, let's look and see how long ago we last checked
       // If it was more than 3 days ago, let's fetch updates. Otherwise, finish.
       

       // If fetching, let's tell the user we are doing something. Show a waiting thing.
       ProgressDialog dialog = ProgressDialog.show(NUHockey.this, "", 
               "Fetching game updates...", true);

       // Let's get the list of updates from our server
       List<Map<String,String>> updates = fetchGameUpdates(gamesDba.getLastUpdated());;

       // Once we have them, remember the exact time.
       Date now = new Date();

       // For each update, determine what the hell it wants us to do and apply it to the gamesDba
       for (int i = 0; i < updates.size(); i++) {
    	   Map<String,String> game = updates.get(i);
    	   String op = game.get("operation");
    	   int id = Integer.parseInt(game.get("id"));
    	   String date = game.get("date");
    	   String opponent = game.get("opponent");
    	   String season = game.get("season");
    	   int nuscore = Integer.parseInt(game.get("nuscore"));
    	   int oppscore = Integer.parseInt(game.get("oppscore"));
    	   String location = game.get("location");
    	   
    	   if (op=="new") {
    		   gamesDba.createGame(id, date, season, opponent, nuscore, oppscore, location);
    	   } else if (op=="update") {
    		   // update game
    		   gamesDba.updateGame(id, date, season, opponent, nuscore, oppscore, location);
    	   } else if (op=="deleted") {
    		   gamesDba.deleteGame(id);
    	   }
       }

       // When finished, this was the last time we updated. Save.
       gamesDba.saveLastUpdated(now);
       gamesDba.close();
       dialog.dismiss();
   }

   private List<Map<String,String>> fetchGameUpdates(Date lastChecked) throws Exception {
	   BufferedReader in = null;
	   SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	   List<Map<String,String>> updates = new ArrayList<Map<String,String>>();
	   String date = df.format(lastChecked);
	   
       // Make some sort of http request
	   HttpClient client = new DefaultHttpClient();
	   HttpGet request = new HttpGet();
	   URI uri = new URI("http://morning-planet-4215.heroku.com/gameupdates/" + date + ".txt");
	   request.setURI(uri);
	   HttpResponse response = client.execute(request);
	   
	   InputStream content = response.getEntity().getContent();
	   in = new BufferedReader(new InputStreamReader(content));
	   
	   String line = "";
	   
       // We are going to get some sort of lines. For each line, create a Map.
	   while ((line = in.readLine()) != null) {
		   Map<String,String> m = new HashMap<String,String>();
		   String[] parts = line.split(",");
		   m.put("operation", parts[0]);
		   m.put("id", parts[1]);
		   m.put("date", parts[2]);
		   m.put("opponent", parts[3]);
		   m.put("season", parts[4]);
		   m.put("nuscore", parts[5]);
		   m.put("oppscore", parts[6]);
		   m.put("location", parts[7]);
		   updates.add(m);
	   }
       // Return a list of Maps that can be used by the methods of gamesDba
	   return updates;
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