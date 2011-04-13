package com.lsiegert;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.lsiegert/databases/";
 
    private static String DB_NAME = "NUHockey.sqlite";
 
    public SQLiteDatabase myDb; 
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDatabase() throws IOException{
 
    	boolean dbExists = checkDatabase();
    	SQLiteDatabase db_Read = null;
 
    	if(dbExists){
    		//do nothing - database already exists
    	}else{
    		db_Read = this.getReadableDatabase(); 
    		db_Read.close();
 
        	try {
    			copyDatabase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
 
    }
 
    /**
     * Check if the database already exists to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDatabase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	} catch(SQLiteException e){
    		//database does't exist yet.
    	}
 
    	if(checkDB != null){
    		checkDB.close();
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * */
    private void copyDatabase() throws IOException{
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
    	String outFileName = DB_PATH + DB_NAME;
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
 
    public void openDatabase() throws SQLException{
        String myPath = DB_PATH + DB_NAME;
    	myDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
    @Override
	public synchronized void close() {
    	    if(myDb != null)
    		    myDb.close();
    	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		checkDatabase();
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
 
	public Cursor getAllPlayers() {
		return myDb.query("Players", null, null, null, null, null, "number");
	}
	
	public Cursor getAllGames() {
		return myDb.query("Games", null, null, null, null, null, "date");
	}
	
	public Cursor getAllSeasons() {
		String query = "select distinct season from Games order by season desc";
		return myDb.rawQuery(query, null);
	}
	
	// location should be either home, away, or neutral
	public Cursor getGamesByLocation(String location) {
		return myDb.query("Games", null, "location = ?", new String[]{location}, null, null, "date");
	}
	
	// Get all games for the given season
	public Cursor getGamesBySeason(String season) {
		return myDb.query("Games", null, "season = ?", new String[]{season}, null, null, "date");
	}
	
	// W-L-T record across games the user has attended
	public String getRecord() {
		String wins = "select _id from Games where nuscore > oppscore and attended=1";
		String losses = "select _id from Games where nuscore < oppscore and attended=1";
		String ties = "select _id from Games where nuscore = oppscore and attended=1";
		int w = myDb.rawQuery(wins, null).getCount();
		int l = myDb.rawQuery(losses, null).getCount();
		int t = myDb.rawQuery(ties, null).getCount();
		return w + "-" + l + "-" + t;
	}
	
	// total number of goals in games the user has attended
	public int getTotalGoalsSeen() {
		Cursor goals = myDb.rawQuery("select sum(nuscore) from Games where attended=1", null);
		return goals.getInt(0);
	}
}