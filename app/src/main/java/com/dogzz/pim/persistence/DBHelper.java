/*
* @Author: dogzz
* @Created: 7/14/2016
*/

package com.dogzz.pim.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "pimdb";
    public static final int DB_VERSION = 1;
    public static final String DB_TABLE = "headers";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SUB_TITLE = "subTitle";
    public static final String COLUMN_LOAD_DATE = "loadDate";
    public static final String COLUMN_READ = "isRead";
    public static final String COLUMN_OFFLINE = "isOffline";
    public static final String COLUMN_FILENAME = "fileName";
    public static final String COLUMN_URL = "articleUrl";
    public static final String COLUMN_IMAGE_URL = "articleImageUrl";
    public static final String COLUMN_TYPE = "type";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_TITLE + " integer, " +
                    COLUMN_SUB_TITLE + " text, " +
                    COLUMN_LOAD_DATE + " integer, " +
                    COLUMN_READ + " integer, " +
                    COLUMN_OFFLINE + " integer, " +
                    COLUMN_FILENAME + " text, " +
                    COLUMN_URL + " text, " +
                    COLUMN_IMAGE_URL + " text, " +
                    COLUMN_TYPE + " integer" +
                    ");";;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
