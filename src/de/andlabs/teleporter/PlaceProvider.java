package de.andlabs.teleporter;

import java.io.File;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;


public class PlaceProvider extends ContentProvider implements OnSharedPreferenceChangeListener {
	
	private static final int ORIGIN = 0;
    private static final int PLACES = 1;
    private static final int PLACE = 2;
    private static final UriMatcher sUriMatcher;
	private static final String TAG = "PlaceProvider";
	private static DatabaseHelper db;
    private static final String DATABASE_NAME = "myPlaces.db";


	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		    db.execSQL("CREATE TABLE myplaces ("
                        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name TEXT,"
                        + "city TEXT,"
                        + "address TEXT,"
                        + "icon INTEGER,"
                        + "tlp_id INTEGER"
                        + ");");
		    db.execSQL("INSERT INTO myplaces values(1, 'Shackspace', 'Stuttgart', 'Äusserer Nordbahnhof 12', "+R.drawable.shackspace+", 23);");
		    db.execSQL("INSERT INTO myplaces values(2, 'Droidcamp', 'Stuttgart', 'Nobelstrasse 10', "+R.drawable.droidcamp+", 42);");
		    db.execSQL("INSERT INTO myplaces values(3, 'c-base', 'Berlin', 'Rungestr 20', "+R.drawable.cbase+", 42);");
		    Log.d(TAG, "created DB");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}



	private static final String SQL = 
	    "SELECT _id, " +
	            "'"+Place.CONTENT_URI+"/%3$s/' || _id " +
	            	 "AS "+SearchManager.SUGGEST_COLUMN_INTENT_DATA+", "+
	            "name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", " +
            	"%1$s AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+", " +
            	"icon AS "+SearchManager.SUGGEST_COLUMN_ICON_1+", " +
            	"'"+R.drawable.icon+"' " +
            		 "AS "+SearchManager.SUGGEST_COLUMN_ICON_2+", " +
            	"%2$s || ', ' || %1$s " +
            		 "AS "+SearchManager.SUGGEST_COLUMN_QUERY+", "+
            	"'"+SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT+"' " +
            		 "AS "+SearchManager.SUGGEST_COLUMN_SHORTCUT_ID+" "+
            	"FROM %3$s " +
            	"WHERE name LIKE '%%1$s%%%%' ";
   
	private String sql;
	
	
	@Override
	public boolean onCreate() {
	    Log.d(TAG, "onCreate");
		
	    SharedPreferences autocompletion = getContext().getSharedPreferences("autocompletion", getContext().MODE_PRIVATE);
	    autocompletion.registerOnSharedPreferenceChangeListener(this);
	    onSharedPreferenceChanged(autocompletion, null);
	    
		return true;
	}


    @Override
    public void onSharedPreferenceChanged(SharedPreferences autocompletion, String key) {
        Log.d(TAG, "onSharedPreferenceChanged");
        
        // prepare database queries
        if (db != null) db.close();
        db = new DatabaseHelper(getContext());
        
        StringBuilder builder = new StringBuilder();
        String dir = Environment.getExternalStorageDirectory().getPath()+"/teleporter/";
        builder.append(String.format(SQL, "city", "address", "myplaces"));
        for (String file : autocompletion.getAll().keySet()) {
            if (autocompletion.getBoolean(file, false)) {
                String path = dir+file;
                if (!new File(path).exists()) {
                    Log.d(TAG, file+" doesn't exist");
                    continue;
                }
                String name = file.split("\\.")[0];
                db.getWritableDatabase().execSQL("ATTACH DATABASE '"+path+"' AS '"+name.replace("-", "_")+"';");
                builder.append("UNION ALL ");
                builder.append(String.format(SQL, "'"+name.substring(name.indexOf("_")+1)+"'", "name", name.replace("-", "_")+".places"));
                Log.d(TAG, name);
            }
        }
        builder.append(" LIMIT 42");
        sql = builder.toString();
        Log.d(TAG, sql);
    }
	
	
	@Override
	public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case PLACES:
        	return Place.CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query: "+uri);
		String query;
		Cursor cursor = null;
        switch (sUriMatcher.match(uri)) {
        case PLACE:
        	Log.d(TAG, "matched PLACE: "+uri+" "+uri.getPathSegments().get(1));
        	cursor = db.getReadableDatabase().query(uri.getPathSegments().get(1), projection, "_id="+uri.getLastPathSegment(), selectionArgs, null, null, null);
        	break;
		case ORIGIN:
			Log.d(TAG, "matched ORIGIN: "+uri);
			cursor = db.getReadableDatabase().rawQuery(String.format(sql, ""), null);
			break;
		case PLACES:
			Log.d(TAG, "matched PLACES: "+uri);
			query = uri.getPathSegments().size() == 2 ? uri.getLastPathSegment(): "";
			cursor = db.getReadableDatabase().rawQuery(String.format(sql, query), null);
//					"SELECT _id, " +
//					    "name AS "+SearchManager.SUGGEST_COLUMN_TEXT_1+", " +
//					    "'"+mCity+"' AS "+SearchManager.SUGGEST_COLUMN_TEXT_2+", " +
//					    "icon AS "+SearchManager.SUGGEST_COLUMN_ICON_1+", " +
//					    "'"+R.drawable.icon+"' AS "+SearchManager.SUGGEST_COLUMN_ICON_2+", " +
//					    "name ||', "+mCity+"' AS "+SearchManager.SUGGEST_COLUMN_QUERY+", "+
//					    "'"+SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT+"' AS "+SearchManager.SUGGEST_COLUMN_SHORTCUT_ID+" "+
//					"FROM city.places " +
//					(uri.getPathSegments().size() == 2 ? "WHERE name LIKE '"+uri.getLastPathSegment()+"%' " : "") +
//					"LIMIT 42", null);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return cursor;
		
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
	        String[] selectionArgs) {
	    // TODO Auto-generated method stub
	    return 0;
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI("de.andlabs.teleporter", "places/*/#", PLACE);
		sUriMatcher.addURI("de.andlabs.teleporter", SearchManager.SUGGEST_URI_PATH_QUERY, PLACES);
		sUriMatcher.addURI("de.andlabs.teleporter", SearchManager.SUGGEST_URI_PATH_QUERY+"/*", PLACES);
		sUriMatcher.addURI("de.andlabs.teleporter", "origin/"+SearchManager.SUGGEST_URI_PATH_QUERY, ORIGIN);
		
	}
}
