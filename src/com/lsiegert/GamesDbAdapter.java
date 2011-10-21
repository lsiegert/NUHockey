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
import android.text.format.DateFormat;

public class GamesDbAdapter {
	public static final String ID = "_id";
	public static final String LOCATION = "location";
	public static final String OPPSCORE = "oppscore";
	public static final String OPPONENT = "opponent";
	public static final String SEASON = "season";
	public static final String NUSCORE = "nuscore";
	public static final String DATE = "date";
	public static final String UPDATED ="updated";
	
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
    
    public long createGame(int id, String date, String season, String opponent, int nuscore, int oppscore, String location) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ID, id);
        initialValues.put(DATE, date);
        initialValues.put(SEASON, season);
        initialValues.put(OPPONENT, opponent);
        initialValues.put(NUSCORE, nuscore);
        initialValues.put(OPPSCORE, oppscore);
        initialValues.put(LOCATION, location);

        return mDb.insert("games", null, initialValues);
    }
    
    public boolean updateGame(int id, String date, String season, String opponent, int nuscore, int oppscore, String location) {
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(DATE, date);
        values.put(SEASON, season);
        values.put(OPPONENT, opponent);
        values.put(NUSCORE, nuscore);
        values.put(OPPSCORE, oppscore);
        values.put(LOCATION, location);
        
        return mDb.update("games", values, ID + "=" + id, null) > 0;
    }
    
    public boolean deleteGame(int id) {
    	return mDb.delete("games", ID + "=" + id, null) > 0;
    }
    
    public Date getLastUpdated() throws ParseException{
    	Cursor c = mDb.query("updated", new String[] { UPDATED }, null, null, null, null, null);
    	if (c != null) {
            c.moveToFirst();
            String s = c.getString(0);
            return df.parse(s);
        } else {
        	return new Date(0);
        }
    }
    
    public boolean saveLastUpdated(Date date){
    	ContentValues values = new ContentValues();
    	values.put(ID, 1);
    	values.put(UPDATED, df.format(date));

    	return mDb.replace("updates", UPDATED, values) > 0;
    }
    
	private static class DatabaseHelper extends SQLiteOpenHelper {
		 private static final String DATABASE_NAME = "gamedata";
		 private static final int DATABASE_VERSION = 1;
		    
		private static final String GAMES_TABLE_CREATE =
	        "create table games(" +
	        	ID + " integer primary key, " +
	        	DATE + " text not null," +
	        	SEASON + " text not null" +
	        	OPPONENT + " text not null" +
	        	NUSCORE + " integer" +
	        	OPPSCORE + " integer" +
	        	LOCATION + " text not null" +
	        ");";
		
		private static final String UPDATED_TABLE_CREATE =
	        "create table updated(" +
	        	ID + " integer primary key autoincrement, " +
	        	UPDATED + " text not null," +
	        ");";
		
		
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(GAMES_TABLE_CREATE);
			db.execSQL(UPDATED_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS games");
			db.execSQL("DROP TABLE IF EXISTS updated");
            onCreate(db);
		}
		
	}
}
