/*
* @Author: dogzz
* @Created: 7/20/2016
*/

package com.dogzz.pim.persistence;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.dogzz.pim.datahandlers.HeadersList.DAY_IN_SECONDS;
import static com.dogzz.pim.persistence.DBHelper.*;

public class HistoryManager {

    private Activity mainActivity;
    private String selection = null;
    private String[] selectionArgs = null;

    public HistoryManager(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void clearOld(String interval) {
        try {
            long currentDate = System.currentTimeMillis() / 1000;
            long minDate = currentDate - Long.valueOf(interval) * DAY_IN_SECONDS;
            DBHelper mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
            SQLiteDatabase mDB = mDBHelper.getWritableDatabase();
            selection = COLUMN_LOAD_DATE.concat(" <= ? AND ").concat(COLUMN_OFFLINE).concat(" != 1");
            selectionArgs = new String[] { String.valueOf(minDate) };
            mDB.delete(DB_TABLE, selection, selectionArgs);
            if (mDBHelper!=null) mDBHelper.close();
        } catch (Exception e) {
            Log.e("History", "Error during clearing history " + e.getMessage());
        }
    }

    public void clearAll() {
        clearOld("0");
    }
}
