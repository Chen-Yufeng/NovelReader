package com.ifchan.reader.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by daily on 11/14/17.
 */

public class BookshelfDataBaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "Bookshelf.db";
    private static int VERSION = 1;
    private static BookshelfDataBaseHelper sDataBaseHelper = null;
    private final String TAG = "@vir DBHelper";

    public static final String CREATE_Bookshelf = "create table Bookshelf(" +
            "id integer primary key, bookid text, title text, author text, shortIntro text, cover" +
            " text, coverPath text, site text, latelyFollower text, " +
            "retentionRatio text, majorCate text, lastGetChapter integer)";

    public BookshelfDataBaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static BookshelfDataBaseHelper getInstance(Context context) {
        if (sDataBaseHelper == null) {
            sDataBaseHelper = new BookshelfDataBaseHelper(context);
        }
        return sDataBaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Bookshelf);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addToBookshelf(SQLiteDatabase db, int position, String id, String title, String
            author, String shortIntro, String cover, String coverPath, String site,
                               int latelyFollower, String retentionRatio, String majorCate,
                               String lastGetChapter) {
        db.execSQL("insert into Bookshelf (id, bookid, title, author, shortIntro, cover, " +
                        "coverPath, site, latelyFollower, retentionRatio, majorCate, lastGetChapter) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                , new String[]{Integer.toString(position), id, title, author, shortIntro, cover,
                        coverPath, site, Integer.toString
                        (latelyFollower), retentionRatio, majorCate, lastGetChapter});
    }

    public void refresh(String position,String cover,String latelyFollower,String retentionRatio,
                        SQLiteDatabase
            db) {
        db.execSQL("update Bookshelf set cover = ? where id = ?",new String[]{cover,position});
        db.execSQL("update Bookshelf set latelyFollower = ? where id = ?",new String[]{latelyFollower,position});
        db.execSQL("update Bookshelf set retentionRatio = ? where id = ?",new String[]{retentionRatio,position});

    }

    public void renewLastGetChapter(int id, String lastGetChapter, SQLiteDatabase db) {
        db.execSQL("update Bookshelf set lastGetChapter = ? where id = ?",new
                String[]{Integer.toString(id), lastGetChapter});
    }

    public void delFromBookshelf(int position, SQLiteDatabase db) {
        db.execSQL("delete from Bookshelf where id = ?", new String[]{Integer.toString(position)});
        //后续要优化（触发器？？？）
        db.execSQL("update Bookshelf set id=id-1 where id > ?", new String[]{Integer.toString
                (position)});
    }

    public void removeAllColumns(SQLiteDatabase db) {
        db.delete("Bookshelf", null, null);
    }

}