package com.online.nutrition.dietdiary.data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.online.nutrition.dietdiary.database.DDSQLiteOpenHelper;


/**
 * 
 * DataHelper Class
 * 
 * Class for communicating with SQLite database inside Android device
 * 
 * @author Aare Puussaar (aare.puussaar#gmail.com)
 *
 */
/**
 * @author aare
 *
 */
/**
 * @author aare
 *
 */
public class DataHelper {
	 private static final String t = "DataHelper";

	    private static final String DATABASE_NAME = "diary.db";
	    private static final int DATABASE_VERSION = 3;
	    private static final String DIARY_TABLE_NAME = "diary";
	
	    

	    /**
	     * This class helps open, create, and upgrade the database file.
	     */
	    private static class DatabaseHelper extends DDSQLiteOpenHelper {

	        DatabaseHelper(String databaseName) {
	            super("/sdcard/dietdiary/metadata", databaseName, null, DATABASE_VERSION);
	        }


	        @Override
	        public void onCreate(SQLiteDatabase db) {
	            db.execSQL("CREATE TABLE IF NOT EXISTS " + DIARY_TABLE_NAME + " ("+
	            	" id INTEGER PRIMARY KEY," +
	            	" comment TEXT," +
	            	" cb1 INTEGER," +
	            	" cb2 INTEGER," +
	            	" cb3 INTEGER," +
	            	" cb4 INTEGER," +
	            	" cb5 INTEGER," +
	            	" timestamp TEXT," +
	            	" uploaded INTEGER," +
	            	" deleted INTEGER);");
	        }


	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            //Log.w(t, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	            db.execSQL("DROP TABLE IF EXISTS diary");
	            onCreate(db);
	        }
	    }
	

	    private DatabaseHelper mDbHelper;
	    private HashMap<String, List<String>> data;
	    
	    /**
	     * Datahelper constructor
	     * 
	     */
	    public DataHelper() {
	        mDbHelper = new DatabaseHelper(DATABASE_NAME);
	    }
	    
	    /**
	     * Insert method
	     * 
	     * @param values - values to be inserted
	     * @return true if something was inserted
	     * @throws SQLException if inserting faild
	     */
	    public boolean insert(ContentValues values) {	       
	        //insert into database
	        SQLiteDatabase db = mDbHelper.getWritableDatabase();
	        long rowId = db.insert(DIARY_TABLE_NAME, null, values);
	        if (rowId > 0) {
	            return true;
	        }

	        throw new SQLException("Failed to insert row into database");
	    }
	    
	    //make update query
	    /**
	     * Update
	     * 
	     * method for updating rows in the database.
	     * 
	     * @param values -a map from column names to new column values. null is a valid value that will be translated to NULL.
	     * @param where - the optional WHERE clause to apply when updating. Passing null will update all rows.
	     * @param whereArgs - WHERE arguments.
	     * @return the number of rows affected
	     */
	    public int update(ContentValues values, String where, String[] whereArgs) {
	    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
	        int count;
	        count = db.update(DIARY_TABLE_NAME, values, where, whereArgs);
	        
	        return count;
	    }
	   
	    
	    /**
	     * 
	     * Method for deleting row in database
	     * 
	     * @param where - the optional WHERE clause to apply when deleting. Passing null will delete all rows.
	     * @param whereArgs -WHERE arguments.
	     * @return the number of rows affected
	     */
	    public int delete(String where, String[] whereArgs) {
	    	SQLiteDatabase db = mDbHelper.getWritableDatabase();
	        int count;
	        count = db.delete(DIARY_TABLE_NAME, where, whereArgs);
	        return count;
	    }
	    

	    /**
	     * Query the given table, returning a Cursor over the result set.
	     * 
	     * @param projection - A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	     * @param selection - A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	     * @param selectionArgs - You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	     * @param groupBy - A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	     * @param having - A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	     * @param sortOrder - How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	     * @param limit - Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
	     * @return - A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.
	     */
	    public Cursor query(String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
	        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        qb.setTables(DIARY_TABLE_NAME);

	        // Get the database and run the query
	        SQLiteDatabase db = mDbHelper.getReadableDatabase();
	        Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
	        //Return cursor
	        return c;
	    }
	    
	    
	    /**
	     * Performs raw query
	     * 
	     * @param query - raw query string
	     * @return c - returns cursor object
	     */
	    public Cursor rawQuery(String query){
	    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        qb.setTables(DIARY_TABLE_NAME);

	        // Get the database and run the query
	        SQLiteDatabase db = mDbHelper.getReadableDatabase();
	        Cursor c = db.rawQuery(query,null);
	        
	        //Return cursor
	        return c;
	    }
	    
	    /**
	     * Get image names for history from database
	     * 
	     * @return list of image names from
	     */
	    public HashMap<String, List<String>> getImageNames(){
	    	List<String> images = new ArrayList<String>();
	    	List<String> isUploaded = new ArrayList<String>();
	    	List<String> dates = new ArrayList<String>();
	    	Cursor cursor = query(new String[] { "timestamp","uploaded", "datetime(timestamp,'unixepoch','localtime') AS date" }, "deleted IS NOT "+1, null, null, null, "timestamp desc", null);
	    	if (cursor.moveToFirst()) {
				do {
					images.add(cursor.getString(0));
					isUploaded.add(Integer.toString(cursor.getInt(1)));
					dates.add(cursor.getString(2));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			data = new HashMap<String, List<String>>();
			data.put("images", images);
			data.put("isUploaded", isUploaded);
			data.put("dates", dates);
			//Log.d(t,data.toString());
			return data;
	    }
	    
	    
	    /**
	     * 
	     * Get newest image values for upload
	     * 
	     * @return newest image name and values from database
	     */
	    public ContentValues getUploadImage(){
	    	ContentValues values = new ContentValues();
			Cursor cursor = query(new String[] { "comment", "cb1", "cb2", "cb3", "cb4", "cb5", "timestamp"}, "uploaded=" + 0,null, null, null, "timestamp desc", "1");
			if (cursor.moveToFirst()) {
				do {
					values.put("comment", cursor.getString(0));
					values.put("cb1", cursor.getInt(1));
					values.put("cb2", cursor.getInt(2));
					values.put("cb3", cursor.getInt(3));
					values.put("cb4", cursor.getInt(4));
					values.put("cb5", cursor.getInt(5));
					values.put("timestamp", cursor.getString(6));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return values;
	    }
	    
	    /**
	     * 
	     * Get stats from database order by date
	     * 
	     * @return all the stats
	     */
	    public HashMap<String, List<String>> getStats(){
	    	List<String> dates = new ArrayList<String>();
	    	List<String> meals = new ArrayList<String>();
	    	List<String> drinks = new ArrayList<String>();
	    	List<String> snacks = new ArrayList<String>();
	    	List<String> breakfastTime = new ArrayList<String>();
	    	List<String> lunchTime = new ArrayList<String>();
	    	List<String> dinnerTime = new ArrayList<String>();
	    	List<String> lastMealTime = new ArrayList<String>();
			Cursor cursor = rawQuery("SELECT date(di.timestamp,'unixepoch','localtime') AS date," +
					"COUNT(CASE WHEN di.cb5 != 1  THEN 1 ELSE null END) AS meals, " +
					"SUM(di.cb5) AS drinks," +
					"SUM(di.cb4) AS snacks," +
					"(SELECT datetime(d.timestamp,'unixepoch','localtime') FROM "+DIARY_TABLE_NAME+" AS d WHERE d.cb1 = 1 AND date(d.timestamp,'unixepoch','localtime') = date(di.timestamp,'unixepoch','localtime') ORDER BY d.timestamp DESC LIMIT 1) AS breakfastTime, " +
					"(SELECT datetime(d.timestamp,'unixepoch','localtime') FROM "+DIARY_TABLE_NAME+" AS d WHERE d.cb2 = 1 AND date(d.timestamp,'unixepoch','localtime') = date(di.timestamp,'unixepoch','localtime') ORDER BY d.timestamp DESC LIMIT 1) AS lunchTime," +
					"(SELECT datetime(d.timestamp,'unixepoch','localtime') FROM "+DIARY_TABLE_NAME+" AS d WHERE d.cb3 = 1 AND date(d.timestamp,'unixepoch','localtime') = date(di.timestamp,'unixepoch','localtime') ORDER BY d.timestamp DESC LIMIT 1) AS dinnerTime," +
					"(SELECT datetime(d.timestamp,'unixepoch','localtime') FROM "+DIARY_TABLE_NAME+" AS d WHERE (d.cb5 != 1 OR d.cb1 = 1 OR d.cb2 = 1 OR d.cb3 = 1 OR d.cb4 = 1 ) " +
					"AND date(d.timestamp,'unixepoch','localtime') = date(di.timestamp,'unixepoch','localtime')  ORDER BY d.timestamp DESC LIMIT 1) AS lastMealTime" +
					" FROM "+DIARY_TABLE_NAME+" AS di " +
					"GROUP BY date " +
					"ORDER BY date DESC;");
			if (cursor.moveToFirst()) {
				do {
					dates.add(cursor.getString(0));
					meals.add(cursor.getString(1));
					drinks.add(cursor.getString(2));
					snacks.add(cursor.getString(3));
					breakfastTime.add(cursor.getString(4));
					lunchTime.add(cursor.getString(5));
					dinnerTime.add(cursor.getString(6));
					lastMealTime.add(cursor.getString(7));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			data = new HashMap<String, List<String>>();
			data.put("dates", dates);
			data.put("meals", meals);
			data.put("drinks", drinks);
			data.put("snacks", snacks);
			data.put("breakfastTime", breakfastTime);
			data.put("lunchTime", lunchTime);
			data.put("dinnerTime", dinnerTime);
			data.put("lastMealTime", lastMealTime);
			return data;
	    }
	    
	    
	    /**
	     * Gets all images that are not uploaded yet
	     * @return images shorted by time desc
	     */
	    public HashMap<String, List<String>> getUploadImages(){
	    	List<String> comments = new ArrayList<String>();
	    	List<String> cb1 = new ArrayList<String>();
	    	List<String> cb2 = new ArrayList<String>();
	    	List<String> cb3 = new ArrayList<String>();
	    	List<String> cb4 = new ArrayList<String>();
	    	List<String> cb5 = new ArrayList<String>();
	    	List<String> timestamp = new ArrayList<String>();
			Cursor cursor = query(new String[] { "comment", "cb1", "cb2", "cb3","cb4", "cb5", "timestamp"}, "uploaded=" + 0,null, null, null, "timestamp desc", null);
			if (cursor.moveToFirst()) {
				do {
					comments.add(cursor.getString(0));
					cb1.add(Integer.toString(cursor.getInt(1)));
					cb2.add(Integer.toString(cursor.getInt(2)));
					cb3.add(Integer.toString(cursor.getInt(3)));
					cb4.add(Integer.toString(cursor.getInt(4)));
					cb5.add(Integer.toString(cursor.getInt(5)));
					timestamp.add(Integer.toString(cursor.getInt(6)));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			data = new HashMap<String, List<String>>();
			data.put("comments", comments);
			data.put("cb1", cb1);
			data.put("cb2", cb2);
			data.put("cb3", cb3);
			data.put("cb4", cb4);
			data.put("cb5", cb5);
			data.put("timestamp", timestamp);
			return data;	    	
	    }
	    
	    /**
	     * Sets image deleted in database
	     * 
	     * @param name - imagename
	     * @return true if image is set deleted
	     */
	    public boolean setDeleted(String name){
	    	ContentValues values = new ContentValues();
	    	values.put("deleted",1);
	    	update(values,"timestamp="+name, null);
	    	return true;
	    }
	    
	    /**
	     * Delete imagerow from database
	     * 
	     * @param name - imagename
	     * @return true if row is deleted
	     */
	    public boolean deleteImage(String name){
	    	delete("timestamp="+name, null);
	    	return true;
	    }
	    
	    /**
	     * Set image uploaded 1 in database
	     * @param name - imagename
	     * @return true if success
	     */
	    public boolean setUploaded(String name){
	    	ContentValues values = new ContentValues();
	    	values.put("uploaded",1);
	    	update(values, "timestamp="+name, null);
	    	return true;
	    }
	    
	    
	    /**
	     * Gets deleted image names from database
	     *
	     * @return list of imagenames
	     */
	    public List<String> getDeletedImages(){
	    	List<String> results = new ArrayList<String>();
	    	Cursor cursor = query(new String[] {"timestamp"}, "deleted=" + 1,null, null, null, "timestamp desc", null);
			if (cursor.moveToFirst()) {
				do {
					results.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			return results;
	    	
	    }
	    
	    public HashMap<String, List<String>> getImagesInfo(){
	    	List<String> comments = new ArrayList<String>();
	    	List<String> cb1 = new ArrayList<String>();
	    	List<String> cb2 = new ArrayList<String>();
	    	List<String> cb3 = new ArrayList<String>();
	    	List<String> cb4 = new ArrayList<String>();
	    	List<String> cb5 = new ArrayList<String>();
	    	List<String> timestamp = new ArrayList<String>();
			Cursor cursor = query(new String[] { "comment", "cb1", "cb2", "cb3","cb4", "cb5", "timestamp"}, null, null, null, null, "timestamp desc", null);
			if (cursor.moveToFirst()) {
				do {
					comments.add(cursor.getString(0));
					cb1.add(Integer.toString(cursor.getInt(1)));
					cb2.add(Integer.toString(cursor.getInt(2)));
					cb3.add(Integer.toString(cursor.getInt(3)));
					cb4.add(Integer.toString(cursor.getInt(4)));
					cb5.add(Integer.toString(cursor.getInt(5)));
					timestamp.add(Integer.toString(cursor.getInt(6)));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			data = new HashMap<String, List<String>>();
			data.put("comments", comments);
			data.put("cb1", cb1);
			data.put("cb2", cb2);
			data.put("cb3", cb3);
			data.put("cb4", cb4);
			data.put("cb5", cb5);
			data.put("timestamp", timestamp);
			return data;	    	
	    }
	    
	    

	    
	    /**
	     * Close database connection
	     */
	    public void closeConnection(){
			this.mDbHelper.close();
		}
	    
	    
	    
}
