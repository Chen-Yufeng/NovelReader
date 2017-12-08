package com.ifchan.reader.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by daily on 11/14/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "History.db";
    private static int VERSION = 1;
    private static DataBaseHelper sDataBaseHelper = null;
    private final String TAG = "@vir DBHelper";

    public static final String CREATE_BOOK = "create table History(" +
            "id integer primary key,history text)";

    public DataBaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static DataBaseHelper getInstance(Context context) {
        if (sDataBaseHelper == null) {
            sDataBaseHelper = new DataBaseHelper(context);
        }
        return sDataBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addHistory(int position, String history, SQLiteDatabase db) {
        db.execSQL("insert into History (id, history) values(?, ?)"
                , new String[]{Integer.toString(position), history});
    }

    public void delHistory(int position, SQLiteDatabase db) {
        db.execSQL("delete from History where id = ?", new String[]{Integer.toString(position)});
        //后续要优化（触发器？？？）
        db.execSQL("update History set id=id-1 where id > ?", new String[]{Integer.toString
                (position)});
    }

    public void updateHistory(int position, String history, SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL("update History set history = ? where id = ?", new String[]{history,
                Integer.toString(position)});
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public void removeAllColumns(SQLiteDatabase db) {
        db.delete("History", null, null);
    }

}