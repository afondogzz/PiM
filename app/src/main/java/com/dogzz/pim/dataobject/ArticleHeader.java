package com.dogzz.pim.dataobject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.dogzz.pim.asynctask.DownloadTask;

import java.io.IOException;

import static com.dogzz.pim.persistence.DBHelper.*;

/**
 * Data Object for storing article headers
 * Created by afon on 06.07.2016.
 */
public class ArticleHeader {
    private String articleUrl = "";
    private String articleImageUrl = "";
    private String title = "";
    private String subTitle = "";
    private long loadDate;
    private boolean isRead = false;
    private boolean isOffline = false;
    private String fileName = "";
    private int type;// 0 for articles and 1 for news

    private SQLiteDatabase db;
    private String[] columns = null;
    private String selection = null;
    private String[] selectionArgs = null;
    private String downloadResult;

    public ArticleHeader() {}

    public ArticleHeader(SQLiteDatabase db) {
        this.db = db;
    }

    public void synchronizeWithDB() {
        Cursor c = getRecordFromDB();
        if (c == null || c.getCount() == 0) {
            saveRecordToDB();
        } else {
            updateRecordAccordingToDB(c);
        }
    }

    private Cursor getRecordFromDB() {
        columns = new String[] {COLUMN_READ, COLUMN_OFFLINE, COLUMN_URL, COLUMN_LOAD_DATE, COLUMN_FILENAME};
        selection = COLUMN_URL.concat(" = ? AND ").concat(COLUMN_TYPE).concat(" = ?");
        selectionArgs = new String[] { articleUrl , String.valueOf(type)};
        return db.query(DB_TABLE, columns, selection, selectionArgs, null, null,
                null);
    }

    private void saveRecordToDB() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, getTitle());
        cv.put(COLUMN_SUB_TITLE, getSubTitle());
        cv.put(COLUMN_LOAD_DATE, getLoadDate());
        cv.put(COLUMN_READ, isRead() ? 1:0);
        cv.put(COLUMN_OFFLINE, isOffline() ? 1:0);
        cv.put(COLUMN_FILENAME, getFileName());
        cv.put(COLUMN_URL, getArticleUrl());
        cv.put(COLUMN_IMAGE_URL, getArticleImageUrl());
        cv.put(COLUMN_TYPE, getType());
        db.insert(DB_TABLE, null, cv);
    }

    private void updateRecordAccordingToDB(Cursor cursor) {
        if (cursor.moveToFirst()) {
            setRead(cursor.getInt(cursor.getColumnIndex(COLUMN_READ)) == 1);
            setOffline(cursor.getInt(cursor.getColumnIndex(COLUMN_OFFLINE)) == 1);
            setLoadDate(cursor.getLong(cursor.getColumnIndex(COLUMN_LOAD_DATE)));
            setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)));
        }

    }

    public void markArticleAsRead(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_READ, 1);
        selection = COLUMN_URL.concat(" = ? AND ").concat(COLUMN_TYPE).concat(" = ?");
        selectionArgs = new String[] { articleUrl , String.valueOf(type)};
        db.update(DB_TABLE, cv, selection, selectionArgs);
        setRead(true);
    }

    public void markArticleAsSaved(SQLiteDatabase db, String fileName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_OFFLINE, 1);
        cv.put(COLUMN_FILENAME, fileName);
        selection = COLUMN_URL.concat(" = ? AND ").concat(COLUMN_TYPE).concat(" = ?");
        selectionArgs = new String[] { articleUrl , String.valueOf(type)};
        db.update(DB_TABLE, cv, selection, selectionArgs);
        setFileName(fileName);
        setOffline(true);
    }

    public void markArticleAsNotSaved(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_OFFLINE, 0);
        cv.put(COLUMN_FILENAME, "");
        selection = COLUMN_URL.concat(" = ? AND ").concat(COLUMN_TYPE).concat(" = ?");
        selectionArgs = new String[] { articleUrl , String.valueOf(type)};
        db.update(DB_TABLE, cv, selection, selectionArgs);
        setFileName("");
        setOffline(false);
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleImageUrl() {
        return articleImageUrl;
    }

    public void setArticleImageUrl(String articleImageUrl) {
        this.articleImageUrl = articleImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public long getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(long loadDate) {
        this.loadDate = loadDate;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String tempFileName) {
        this.fileName = tempFileName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}


