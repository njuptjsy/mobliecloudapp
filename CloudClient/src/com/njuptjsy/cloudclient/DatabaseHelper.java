package com.njuptjsy.cloudclient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * study on SQLite database
 * and prepare to use SQLite store file information in cloud
 * */
public class DatabaseHelper extends SQLiteOpenHelper{

	private Context context;
	private static final String CREATE_FILE_IN_CLOUD_TABLE = "create table FileInCloud (id integer primary key autoincrement, "
			+ "bucketName text, "
			+ "fileName text)"; 
	
	private static final String CREATE_BUCKET_IN_CLOUD_TABLE = "create table BucketInCloud (id integer primary key autoincrement, "
			+ "bucketName text)";
	
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {//when call function getReadableDatabase() or getWritableDatabase() indirectly call this function
		db.execSQL(CREATE_FILE_IN_CLOUD_TABLE);
		db.execSQL(CREATE_BUCKET_IN_CLOUD_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//execute when the version in constructor is update 
//		switch(oldVersion){each case without break arms to execute each case where across version update
//			case 1:
//			case 2:		
//	}		
	}
	
}
