package com.lsiegert;

import java.io.IOException;

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

		DatabaseHelper myDbHelper = new DatabaseHelper(this);

		try {
			myDbHelper.createDatabase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");	 
		}
		
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, RosterActivity.class);

		spec = tabHost.newTabSpec("roster").setIndicator("Roster",
				res.getDrawable(R.drawable.ic_tab_roster))
				.setContent(intent);
		tabHost.addTab(spec);

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
}