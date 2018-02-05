package com.technophile.parsingsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Blob;
import java.util.ArrayList;

/**
 * Created by Technophile on 2/4/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Movies";
    private static final String COLUMN_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private SQLiteDatabase sqLiteDatabase;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_NAME + " BLOB );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }



    public void insertData(ArrayList<MoviesDataModel> data) {
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, new Gson().toJson(data).getBytes());
        sqLiteDatabase.insert(TABLE_NAME, null, cv);
        sqLiteDatabase.close();
    }

    public ArrayList<MoviesDataModel> getDataFromDB(){
        sqLiteDatabase = this.getWritableDatabase();
        TypeToken<ArrayList<MoviesDataModel>> token = new TypeToken<ArrayList<MoviesDataModel>>(){};
        Cursor cursor= sqLiteDatabase.query(TABLE_NAME,new String[]{COLUMN_NAME},null,null,null,null,null);
        String cursorData=null;
        System.out.println("cursor size = " + cursor.getCount());
        System.out.println("cursor size = " + cursor.getBlob(1));

        while (cursor.moveToNext()){
                System.out.println("cursorData = " +new String(cursor.getBlob(1)));
                System.out.println("cursorData = " + cursor.getString(0));
            cursorData=cursor.getString(1);
        }

        System.out.println("cursorData = " + cursorData);
        cursor.close();
        sqLiteDatabase.close();
        return new Gson().fromJson(cursorData,token.getType());
    }
}
