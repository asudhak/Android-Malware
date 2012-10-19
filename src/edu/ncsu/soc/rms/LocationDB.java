package edu.ncsu.soc.rms;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LocationDB extends ContentProvider {

	public static final Uri CONTENT_URI = Uri
			.parse("content://edu.ncsu.soc.rms/alerts");

	// Create the constants used to differentiate between the different URI
	// requests.
	private static final int ALERTS = 1;
	private static final int ALERT_ID = 2;

	private static final UriMatcher uriMatcher;
	// Allocate the UriMatcher object, where a URI ending in 'alerts' will
	// correspond to a request for all alerts, and 'alerts' with a trailing
	// '/[rowID]' will represent a single alert row.
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("edu.ncsu.soc.rms", "alerts", ALERTS);
		uriMatcher.addURI("edu.ncsu.soc.rms", "alerts/#", ALERT_ID);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();

		alertsDatabaseHelper dbHelper;
		dbHelper = new alertsDatabaseHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
		locDB = dbHelper.getWritableDatabase();
		return (locDB == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALERTS:
			return "vnd.android.cursor.dir/vnd.ncsu.alert";
		case ALERT_ID:
			return "vnd.android.cursor.item/vnd.ncsu.alert";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(LOCS_TABLE);

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case ALERT_ID:
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}

		// If no sort order is specified sort by date / time
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_LOCATION;
		} else {
			orderBy = sort;
		}

		// Apply the query to the underlying database.
		Cursor c = qb.query(locDB, projection, selection, selectionArgs,
				null, null, orderBy);

		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);

		// Return a cursor to the query result.
		return c;
	}

	@Override
	public Uri insert(Uri _uri, ContentValues _initialValues) {
		// Insert the new row, will return the row number if successful.
		long rowID = locDB.insert(LOCS_TABLE, "alert", _initialValues);

		// Return a URI to the newly inserted row on success.
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	
	
	public static void simplydelete(int id, SQLiteDatabase locDB)
	{
		locDB.execSQL("DELETE FROM LOCS_TABLE where _id = " + id);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;

		switch (uriMatcher.match(uri)) {
		case ALERTS:
			count = locDB.delete(LOCS_TABLE, where, whereArgs);
			break;

		case ALERT_ID:
			String segment = uri.getPathSegments().get(1);
			count = locDB.delete(LOCS_TABLE,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case ALERTS:
			count = locDB.update(LOCS_TABLE, values, where, whereArgs);
			break;

		case ALERT_ID:
			String segment = uri.getPathSegments().get(1);
			count = locDB.update(LOCS_TABLE, values,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/** The underlying database */
	public SQLiteDatabase locDB;

	private static final String TAG = "LocDB";
	private static final String DATABASE_NAME = "locs.db";
	private static final int DATABASE_VERSION = 1;
	private static final String LOCS_TABLE = "locs";

	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_LOCATION = "location";
	public static final String KEY_PLACE_LAT = "latitude";
	public static final String KEY_PLACE_LNG = "longitude";
	public static final String KEY_PROFILE = "profile";
	public static final String KEY_DIA = "diameter";
	public static final String KEY_EMAIL="email";

	// Column indexes
//	public static final int LOCATION_COLUMN = 1;
	public static final int LATITUDE_COLUMN = 2;
	public static final int LONGITUDE_COLUMN = 3;
	public static final int PROFILE_COLUMN = 4;
	public static final int DIA_COLUMN = 1;
	public static final int LOCATION_COLUMN=5;
	public static final int EMAIL_COLUMN=6;
	

	// Helper class for opening, creating, and managing database version control
	private static class alertsDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE = "create table "
				+ LOCS_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, " + 
				KEY_LOCATION+ " TEXT NOT NULL, " 
				+ "email"+ " INTEGER, "
				+ KEY_PLACE_LAT + " INTEGER, "
				+ KEY_PLACE_LNG + " INTEGER, " 
				+ KEY_PROFILE+ " TEXT NOT NULL, "
				+ KEY_DIA + " INTEGER" + " );";

		/** Helper class for managing the Earthquake database */
		public alertsDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DATABASE_CREATE);
		}
		
		

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + LOCS_TABLE);
			onCreate(db);
		}
	}
}