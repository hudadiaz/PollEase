package com.zaidhuda.pollease.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zaidhuda.pollease.Objects.DBConstPoll;
import com.zaidhuda.pollease.Objects.DBConstUser;

/**
 * Created by Zaid on 22/12/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pollease.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement

    private static final String CREATE_TABLE_USER = "create table "
            + DBConstUser.TABLE_NAME + "("
            + DBConstUser.COLUMN_ID + " text not null, "
            + DBConstUser.COLUMN_IDENTIFIER + " text not null, "
            + DBConstUser.COLUMN_TOKEN + " text not null);";

    private static final String CREATE_TABLE_POLLS = "create table "
            + DBConstPoll.TABLE_NAME + "("
            + DBConstPoll.COLUMN_ID + " integer primary key, "
            + DBConstPoll.COLUMN_URL + " text not null, "
            + DBConstPoll.COLUMN_QUESTION + " text not null, "
            + DBConstPoll.COLUMN_PASSWORD + " text)";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_USER);
        database.execSQL(CREATE_TABLE_POLLS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + DBConstUser.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DBConstPoll.TABLE_NAME);
        onCreate(database);
    }
}
