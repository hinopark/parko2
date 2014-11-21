package com.plaxto.hino.mobile.park.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_HISTORY_VISIT	= "HistoryVisit";
	public static final String TABLE_HISTORY_REDEEM	= "HistoryRedeem";
	public static final String KEY_ID 			= "_id";
	public static final String KEY_DATE			= "_date";
	public static final String KEY_STORE		= "_store";
	public static final String KEY_TOT_SHOP		= "_totalShop";
	public static final String KEY_TOT_POINT	= "_totalPoint";
	public static final String KEY_PRODUCT		= "_product";
	public static final String KEY_PRO_IMAGE	= "_productImage";
	public static final String KEY_PRO_POINT	= "_productPoint";
	public static final String KEY_PRO_QUANTITY	= "_productQty";
	
	
	private static final String TAG = "TrackingSQLiteAdapter";
	private static final String SCRIPT_CREATE_HISTORY_VISIT_TABLE =
			"create table " + TABLE_HISTORY_VISIT + " ("
			+ KEY_ID      	+ " integer primary key autoincrement, "
			+ KEY_DATE		+ " date, "
			+ KEY_STORE 	+ " text, "
			+ KEY_TOT_SHOP 	+ " text, "
			+ KEY_TOT_POINT + " text);";
	
	private static final String SCRIPT_CREATE_HISTORY_REDEEM_TABLE =
			"create table " 	+ TABLE_HISTORY_REDEEM + " ("
			+ KEY_ID      		+ " integer primary key autoincrement, "
			+ KEY_DATE			+ " date, "
			+ KEY_PRODUCT		+ " text, "
			+ KEY_PRO_IMAGE 	+ " text, "
			+ KEY_PRO_QUANTITY 	+ " integer, "
			+ KEY_PRO_POINT 	+ " integer, "
			+ KEY_TOT_POINT 	+ " integer, "
			+ KEY_STORE 		+ " text);";
	
	
	public DatabaseOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {              
        db.execSQL(SCRIPT_CREATE_HISTORY_VISIT_TABLE);	
        db.execSQL(SCRIPT_CREATE_HISTORY_REDEEM_TABLE);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion 
                + " to "
                + newVersion + ", which will destroy all old data");
          db.execSQL("DROP TABLE IF EXISTS" + TABLE_HISTORY_VISIT);
          db.execSQL("DROP TABLE IF EXISTS" + TABLE_HISTORY_REDEEM);
          onCreate(db);
	}
}
