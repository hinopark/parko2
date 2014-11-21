package com.plaxto.hino.mobile.park.db;

/*
 * SQLAdapter to handle Create/Select/Drop/Insert/Update Database/Table
 * Call it (function) of your activity. 
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteAdapter {

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "HinoParking";

	private DatabaseOpenHelper sqLiteHelper;
	static SQLiteDatabase sqLiteDatabase;
	
	public SQLiteAdapter(Context context){
		sqLiteHelper = new DatabaseOpenHelper(context, DB_NAME, null, DB_VERSION);
	}
	
	public void openToRead() throws android.database.SQLException {
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
	}
	
	public void openToWrite() throws android.database.SQLException {
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();	
	}
	
	public void close(){
		sqLiteDatabase.close();
	}
	
	public void insertToHistoryVisit(String date, String store, String totalShop, String totalPoint){
		openToWrite();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseOpenHelper.KEY_DATE, date);
		contentValues.put(DatabaseOpenHelper.KEY_STORE, store);
		contentValues.put(DatabaseOpenHelper.KEY_TOT_SHOP, totalShop);
		contentValues.put(DatabaseOpenHelper.KEY_TOT_POINT, totalPoint);
		sqLiteDatabase.insert(DatabaseOpenHelper.TABLE_HISTORY_VISIT, null, contentValues);
		close();
	}
	
	public void insertToHistoryRedeem(String date, String imageUrl, String product, int qtyProduct, int productPoint, String totalPoint, String store){
		openToWrite();
		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseOpenHelper.KEY_DATE, date);
		contentValues.put(DatabaseOpenHelper.KEY_PRODUCT, product);
		contentValues.put(DatabaseOpenHelper.KEY_PRO_IMAGE, imageUrl);
		contentValues.put(DatabaseOpenHelper.KEY_PRO_QUANTITY, qtyProduct);
		contentValues.put(DatabaseOpenHelper.KEY_PRO_POINT, productPoint);
		contentValues.put(DatabaseOpenHelper.KEY_TOT_POINT, totalPoint);
		contentValues.put(DatabaseOpenHelper.KEY_STORE, store);
		sqLiteDatabase.insert(DatabaseOpenHelper.TABLE_HISTORY_REDEEM, null, contentValues);
		close();
	}
	
	public Cursor queueAllHistoryVisit(){
		String[] columns = new String[]{DatabaseOpenHelper.KEY_ID, 
				DatabaseOpenHelper.KEY_DATE,
				DatabaseOpenHelper.KEY_STORE, 
				DatabaseOpenHelper.KEY_TOT_SHOP, 
				DatabaseOpenHelper.KEY_TOT_POINT};
		Cursor cursor = sqLiteDatabase.query(DatabaseOpenHelper.TABLE_HISTORY_VISIT, 
				columns, 
				null, null, null, null, 
				DatabaseOpenHelper.KEY_ID + " DESC");
		return cursor;
	}
	
	public Cursor queueAllHistoryRedeem(){
		String[] columns = new String[]{DatabaseOpenHelper.KEY_ID, 
				DatabaseOpenHelper.KEY_DATE,
				DatabaseOpenHelper.KEY_PRODUCT,
				DatabaseOpenHelper.KEY_PRO_IMAGE, 
				DatabaseOpenHelper.KEY_PRO_QUANTITY, 
				DatabaseOpenHelper.KEY_PRO_POINT, 
				DatabaseOpenHelper.KEY_TOT_POINT, 
				DatabaseOpenHelper.KEY_STORE};
		Cursor cursor = sqLiteDatabase.query(DatabaseOpenHelper.TABLE_HISTORY_REDEEM, 
				columns, 
				null, null, null, null, 
				DatabaseOpenHelper.KEY_ID + " DESC");
		return cursor;
	}
}