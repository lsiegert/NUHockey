package com.lsiegert;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadGameUpdatesTask extends AsyncTask<Void, Void, Void> {
	private Context context;
	private ProgressDialog dialog;
	private static final String TAG = "NUHockey";

	public DownloadGameUpdatesTask(Context c) {
		context = c;
	}

	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "", "Fetching game updates...", true);
	}

	@Override
	protected Void doInBackground(Void... params){
		try {
			checkForGameUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		dialog.dismiss();
	}

	private void checkForGameUpdates() throws Exception {
		// If fetching, let's tell the user we are doing something. Show a waiting thing.

		GamesDbAdapter gamesDba = new GamesDbAdapter(context);

		try {
			Log.d(TAG, "starting game updates");
			gamesDba.open();

			// First, let's look and see how long ago we last checked
			// If it was more than 3 days ago, let's fetch updates. Otherwise, finish.

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
				int nuscore = parseOrInvalid(game.get("nuscore"));
				int oppscore = parseOrInvalid(game.get("oppscore"));
				String location = game.get("location");
				boolean overtime = Boolean.parseBoolean(game.get("overtime"));

				if (op.equalsIgnoreCase("NEW")) {
					Log.d(TAG, "Creating game: " + id);
					gamesDba.createGame(id, date, season, opponent, nuscore, oppscore, location, overtime);
				} else if (op.equalsIgnoreCase("UPDATED")) {
					Log.d(TAG, "Updating game: " + id);
					gamesDba.updateGame(id, date, season, opponent, nuscore, oppscore, location, overtime);
				} else if (op.equalsIgnoreCase("DELETED")) {
					Log.d(TAG, "Deleting game: " + id);
					gamesDba.deleteGame(id);
				}
			}

			// When finished, this was the last time we updated. Save.
			gamesDba.saveLastUpdated(now);
		} finally {
			gamesDba.close();
			dialog.dismiss();
		}
	}

	private int parseOrInvalid(String s) {
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return -1;
		}
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
		Log.d(TAG, "Making the request to " + uri.toString());
		HttpResponse response = client.execute(request);
		Log.d(TAG, "Response: " + response.getStatusLine().toString());

		InputStream content = response.getEntity().getContent();
		in = new BufferedReader(new InputStreamReader(content));

		String line = "";

		// We are going to get some sort of lines. For each line, create a Map.
		while ((line = in.readLine()) != null) {
			Log.d(TAG, "Parsing line: " + line);
			Map<String,String> m = new HashMap<String,String>();
			String[] parts = line.split(",");
			if (parts.length == 9) {
				m.put("operation", parts[0]);
				m.put("id", parts[1]);
				m.put("date", parts[2]);
				m.put("season", parts[3]);
				m.put("opponent", parts[4]);
				m.put("nuscore", parts[5]);
				m.put("oppscore", parts[6]);
				m.put("location", parts[7]);
				m.put("overtime", parts[8]);
				
				updates.add(m);
			} else {
				Log.e(TAG, "Line doesn't have 9 parts");
			}
		}
		// Return a list of Maps that can be used by the methods of gamesDba
		return updates;
	}
}
