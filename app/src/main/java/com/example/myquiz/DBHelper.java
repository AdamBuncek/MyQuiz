package com.example.myquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myquiz.QuizDB.*;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "QUIZGAMEDB.db";
    private static final int DATABASE_VERSION = 5;

    private SQLiteDatabase db;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_TABLE = "CREATE TABLE " + PlayerTable.TABLE_NAME + "(" + PlayerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlayerTable.COLUMN_NAME + " TEXT, " + PlayerTable.COLUMN_SCORE + " INTEGER" + ")";

        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PlayerTable.TABLE_NAME);
        onCreate(db);
    }

    public void insert(Player player){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PlayerTable.COLUMN_NAME, player.getName());
        cv.put(PlayerTable.COLUMN_SCORE, player.getHighScore());
        db.insert(PlayerTable.TABLE_NAME, null, cv);
    }

    public ArrayList<String> getPlayerList()
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + PlayerTable.TABLE_NAME + " ORDER BY " + PlayerTable.COLUMN_SCORE + " DESC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndex(PlayerTable.COLUMN_NAME));
            int score = res.getInt(res.getColumnIndex(PlayerTable.COLUMN_SCORE));
            arrayList.add("Player: " + name + " score: " + score);
            Log.d("players", "Player: " + name + " score: " + score);
            res.moveToNext();
        }
        return arrayList;
    }

    public void RemoveAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ PlayerTable.TABLE_NAME);
    }
}
