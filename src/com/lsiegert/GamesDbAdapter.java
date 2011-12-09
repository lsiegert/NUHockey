package com.lsiegert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GamesDbAdapter {
	private static final String TAG = "NUHockey";

	public static final String ID = "_id";
	public static final String LOCATION = "location";
	public static final String OPPSCORE = "oppscore";
	public static final String OPPONENT = "opponent";
	public static final String SEASON = "season";
	public static final String NUSCORE = "nuscore";
	public static final String OVERTIME = "overtime";
	public static final String DATE = "date";
	public static final String UPDATED = "updated";
	public static final String ATTENDED = "attended";

	public static final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private final Context mCtx;
	private SQLiteOpenHelper mDbHelper;
	private SQLiteDatabase mDb;

	public GamesDbAdapter(Context ctx) {
		mCtx = ctx;
	}

	public GamesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long createGame(int id, String date, String season, String opponent,
			int nuscore, int oppscore, String location, boolean overtime) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(ID, id);
		initialValues.put(DATE, date);
		initialValues.put(SEASON, season);
		initialValues.put(OPPONENT, opponent);
		initialValues.put(NUSCORE, nuscore);
		initialValues.put(OPPSCORE, oppscore);
		initialValues.put(LOCATION, location);
		initialValues.put(OVERTIME, overtime);

		return mDb.insert("games", null, initialValues);
	}

	public boolean updateGame(int id, String date, String season,
			String opponent, int nuscore, int oppscore, String location, boolean overtime) {
		ContentValues values = new ContentValues();
		values.put(ID, id);
		values.put(DATE, date);
		values.put(SEASON, season);
		values.put(OPPONENT, opponent);
		values.put(NUSCORE, nuscore);
		values.put(OPPSCORE, oppscore);
		values.put(LOCATION, location);
		values.put(OVERTIME, overtime);

		return mDb.update("games", values, ID + "=" + id, null) > 0;
	}

	public boolean deleteGame(int id) {
		return mDb.delete("games", ID + "=" + id, null) > 0;
	}

	public Date getLastUpdated() throws ParseException {
		Log.d(TAG, "getting last updated date");
		Cursor c = mDb.query("updated", new String[] { UPDATED }, null, null,
				null, null, null);
		if (c != null) {
			boolean hasFirstRow = c.moveToFirst();
			if (hasFirstRow) {
				String s = c.getString(0);
				return df.parse(s);
			}
		}
		c.close();
		return new Date(0);
	}

	public boolean saveLastUpdated(Date date) {
		ContentValues values = new ContentValues();
		values.put(ID, 1);
		values.put(UPDATED, df.format(date));

		return mDb.replace("updated", UPDATED, values) > 0;
	}

	public boolean toggleAttended(int gameId, boolean isChecked) {
		if (isChecked) {
			ContentValues values = new ContentValues();
			values.put(ATTENDED, gameId);
			return mDb.insert("attended", ATTENDED, values) >= 0;
		} else {
			return mDb.delete("attended", ATTENDED + "=" + gameId, null) == 1;
		}
	}

	public Cursor getAllPlayers() {
		return null;
	}

	public Cursor getAllGames() {
		return mDb.query("games", null, null, null, null, null, "date");
	}

	public Cursor getAllSeasons() {
		String query = "select distinct season from games order by season desc";
		return mDb.rawQuery(query, null);
	}
	
	public Cursor getAllOpponents() {
		String query = "select distinct opponent from games order by opponent";
		return mDb.rawQuery(query, null);
	}

	// location should be either home, away, or neutral
	public Cursor getGamesByLocation(String location) {
		return mDb.query("games", null, "location = ?",
				new String[] { location }, null, null, "date");
	}

	// Get all games for the given season
	public Cursor getGamesBySeason(String season) {
		String query = "select games._id, games.date, games.season, games.opponent, "
				+ "games.nuscore, games.oppscore, games.location, games.overtime, attended.attended"
				+ " from games left outer join attended on games._id=attended.attended"
				+ " where season = ? order by date";
		return mDb.rawQuery(query, new String[] { season });
	}

	// Get all games for the given opponent
	public Cursor getGamesByOpponent(String opponent) {
		String query = "select games._id, games.date, games.season, games.opponent, "
				+ "games.nuscore, games.oppscore, games.location, games.overtime, attended.attended"
				+ " from games left outer join attended on games._id=attended.attended"
				+ " where opponent = ? order by date";
		return mDb.rawQuery(query, new String[] { opponent });
	}

	// W-L-T record across all games in the given season
	public String getRecordBySeason(String season) {
		String wins = "select games._id from games where nuscore > oppscore and season=?";
		String losses = "select games._id from games where nuscore < oppscore and season=?";
		String ties = "select games._id from games where nuscore != -1 and nuscore = oppscore and season=?";
		String[] seasons = new String[] { season };
		int w = mDb.rawQuery(wins, seasons).getCount();
		int l = mDb.rawQuery(losses, seasons).getCount();
		int t = mDb.rawQuery(ties, seasons).getCount();
		return w + "-" + l + "-" + t;
	}

	
	// W-L-T record across all  games against the given opponent
	public String getRecordByOpponent(String opponent) {
		String wins = "select games._id from games where nuscore > oppscore and opponent=?";
		String losses = "select games._id from games where nuscore < oppscore and opponent=?";
		String ties = "select games._id from games where nuscore != -1 and nuscore = oppscore and opponent=?";
		String[] opponents = new String[] { opponent };
		int w = mDb.rawQuery(wins, opponents).getCount();
		int l = mDb.rawQuery(losses, opponents).getCount();
		int t = mDb.rawQuery(ties, opponents).getCount();
		return w + "-" + l + "-" + t;
	}

	// W-L-T record across all games the user has attended
	public String getRecord() {
		String wins = "select games._id from games, attended where nuscore > oppscore and games._id=attended.attended";
		String losses = "select games._id from games, attended where nuscore < oppscore and games._id=attended.attended";
		String ties = "select games._id from games, attended where nuscore = oppscore and games._id=attended.attended";
		int w = mDb.rawQuery(wins, null).getCount();
		int l = mDb.rawQuery(losses, null).getCount();
		int t = mDb.rawQuery(ties, null).getCount();
		return w + "-" + l + "-" + t;
	}

	// W-L-T record across games the user has attended, by location
	public String getRecordByLocation(String location) {
		String wins = "select games._id from games, attended where nuscore > oppscore and games._id=attended.attended and location=?";
		String losses = "select games._id from games, attended where nuscore < oppscore and games._id=attended.attended and location=?";
		String ties = "select games._id from games, attended where nuscore = oppscore and games._id=attended.attended and location=?";
		String[] args = new String[] { location };
		int w = mDb.rawQuery(wins, args).getCount();
		int l = mDb.rawQuery(losses, args).getCount();
		int t = mDb.rawQuery(ties, args).getCount();
		return w + "-" + l + "-" + t;
	}

	// W-L-T record across overtime games the user has attended
	public String getOvertimeRecord() {
		String wins = "select games._id from games, attended where nuscore > oppscore and games._id=attended.attended and overtime=?";
		String losses = "select games._id from games, attended where nuscore < oppscore and games._id=attended.attended and overtime=?";
		String ties = "select games._id from games, attended where nuscore = oppscore and games._id=attended.attended and overtime=?";
		String[] args = new String[] { "1" };
		int w = mDb.rawQuery(wins, args).getCount();
		int l = mDb.rawQuery(losses, args).getCount();
		int t = mDb.rawQuery(ties, args).getCount();
		return w + "-" + l + "-" + t;
	}

	public int getNumOfTeams() {
		String query = "select distinct opponent from games, attended where games._id=attended.attended";
		return mDb.rawQuery(query, null).getCount();
	}

	public int getNumOfGoals() {
		String query = "select nuscore from games, attended where games._id=attended.attended";
		Cursor allGoals = mDb.rawQuery(query, null);
		allGoals.moveToFirst();
		int totalGoals = 0;
		while (!allGoals.isAfterLast()) {
			totalGoals = totalGoals + allGoals.getInt(0);
			allGoals.moveToNext();
		}
		allGoals.close();
		return totalGoals;
	}

	public int getNumOfShutouts() {
		String query = "select games._id from games, attended where games._id=attended.attended and oppscore=0";
		return mDb.rawQuery(query, null).getCount();
	}

	public int getNumOfWinsAtagganis() {
		String query = "select games._id from games, attended where games._id=attended.attended and "
				+ "nuscore>oppscore and opponent='boston university' and location='away'";
		return mDb.rawQuery(query, null).getCount();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "gamedata";
		private static final int DATABASE_VERSION = 3;

		private static final String GAMES_TABLE_CREATE = "create table games("
				+ ID + " integer primary key, " + DATE + " text not null,"
				+ SEASON + " text not null," + OPPONENT + " text not null,"
				+ NUSCORE + " integer," + OPPSCORE + " integer," + LOCATION
				+ " text not null," + OVERTIME + " boolean" + ");";

		private static final String UPDATED_TABLE_CREATE = "create table updated("
				+ ID
				+ " integer primary key autoincrement, "
				+ UPDATED
				+ " text not null" + ");";

		private static final String ATTENDED_TABLE_CREATE = "create table if not exists attended("
				+ ID
				+ " integer primary key autoincrement, "
				+ ATTENDED
				+ " integer not null" + ");";

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(GAMES_TABLE_CREATE);
			db.execSQL(UPDATED_TABLE_CREATE);
			db.execSQL(ATTENDED_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS games");
			db.execSQL("DROP TABLE IF EXISTS updated");
			onCreate(db);
		}

	}
}
