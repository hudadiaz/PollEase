package com.zaidhuda.pollease.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zaidhuda.pollease.constants.DBConstUser;
import com.zaidhuda.pollease.objects.User;

/**
 * Created by Zaid on 22/12/2015.
 */
public class UserDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {DBConstUser.COLUMN_IDENTIFIER, DBConstUser.COLUMN_ID, DBConstUser.COLUMN_TOKEN};

    public UserDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBConstUser.COLUMN_ID, user.getId());
        values.put(DBConstUser.COLUMN_IDENTIFIER, user.getIdentifier());
        values.put(DBConstUser.COLUMN_TOKEN, user.getToken());
        database.insert(DBConstUser.TABLE_NAME, null, values);
    }

    public User getUser() {
        User user;
        Cursor cursor = database.query(DBConstUser.TABLE_NAME,
                allColumns, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            System.out.println("No user");
            return null;
        }
        cursor.moveToFirst();
        user = cursorToUser(cursor);
        cursor.close();
        return user;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User(cursor.getString(0));
        user.setId(cursor.getString(1));
        user.setToken(cursor.getString(2));
        return user;
    }
}
