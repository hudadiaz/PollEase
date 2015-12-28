package com.zaidhuda.pollease.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zaidhuda.pollease.constants.DBConstPoll;
import com.zaidhuda.pollease.objects.Poll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Zaid on 22/12/2015.
 */
public class PollDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {DBConstPoll.COLUMN_ID, DBConstPoll.COLUMN_QUESTION,
            DBConstPoll.COLUMN_PASSWORD, DBConstPoll.COLUMN_URL, DBConstPoll.COLUMN_TIME_ADDED};

    public PollDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createPoll(Poll poll) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(DBConstPoll.COLUMN_ID, poll.getId());
        values.put(DBConstPoll.COLUMN_QUESTION, poll.getQuestion());
        values.put(DBConstPoll.COLUMN_PASSWORD, poll.getPassword());
        values.put(DBConstPoll.COLUMN_URL, poll.getUrl());
        values.put(DBConstPoll.COLUMN_TIME_ADDED, Calendar.getInstance().getTimeInMillis());
        database.insert(DBConstPoll.TABLE_NAME, null, values);
    }

    public Poll getPoll(int id) {
        Cursor cursor = database.query(DBConstPoll.TABLE_NAME,
                allColumns, "id = " + String.valueOf(id), null, null, null, DBConstPoll.COLUMN_TIME_ADDED);
        cursor.moveToFirst();
        Poll poll = cursorToPoll(cursor);
        cursor.close();
        return poll;
    }

    public void updatePoll(Poll poll) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(DBConstPoll.COLUMN_PASSWORD, poll.getPassword());
        database.update(DBConstPoll.TABLE_NAME, values, DBConstPoll.COLUMN_ID + "=" + poll.getId(), null);
    }

    public void deletePoll(Poll poll) throws SQLException {
        database.delete(DBConstPoll.TABLE_NAME, DBConstPoll.COLUMN_ID + "=" + poll.getId(), null);
    }

    public List<Poll> getAllPolls() {
        List<Poll> polls = new ArrayList<>();
        Cursor cursor = database.query(DBConstPoll.TABLE_NAME,
                allColumns, null, null, null, null, DBConstPoll.COLUMN_TIME_ADDED);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Poll poll = cursorToPoll(cursor);
            polls.add(poll);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return polls;
    }

    private Poll cursorToPoll(Cursor cursor) {
        Poll poll = new Poll(cursor.getInt(0));
        poll.setQuestion(cursor.getString(1));
        poll.setPassword(cursor.getString(2));
        poll.setUrl(cursor.getString(3));
        return poll;
    }
}
