/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package com.aditya.ctrl.thedailypromo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "login";
	private static final String TABLE_USER = "user";
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "username";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_LOGIN_TABLE = "CREATE TABLE "
				+ TABLE_USER + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_NAME + " TEXT" + ")";
		db.execSQL(CREATE_LOGIN_TABLE);

		Log.d(TAG, "Database tables created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		onCreate(db);
	}

	public void addUser(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		db.insert(TABLE_USER, null, values);

		Log.d(TAG, "new user added");
		db.close();

	}

	public String getUserDetails() {
		String userdata = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			userdata = (cursor.getString(1));
		}
		cursor.close();
		db.close();

		return userdata;
	}

	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from sqlite");
	}

}
