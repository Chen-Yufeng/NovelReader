package com.ifchan.reader.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.ifchan.reader.helper.BookshelfDataBaseHelper;
import com.ifchan.reader.helper.DataBaseHelper;

import java.io.File;

/**
 * Created by daily on 12/14/17.
 */

public class CacheUtil {
    private static DataBaseHelper sDataBaseHelper;
    private static BookshelfDataBaseHelper sBookshelfDataBaseHelper;
    private static final String TEMP_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/Reader/temp";

    public static void clearImageCache() {
        File dir1 = new File(TEMP_PATH + "/cover");
        if (dir1.isDirectory()) {
            String[] children = dir1.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir1, children[i]).delete();
            }
        }
    }

    public static void clearRichTextCache() {
        File dir2 = new File(TEMP_PATH + "/richtext");
        if (dir2.isDirectory()) {
            String[] children = dir2.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir2, children[i]).delete();
            }
        }
    }

    public static void clearSearchHistory(Context context) {
        sDataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase db = sDataBaseHelper.getWritableDatabase();
        sDataBaseHelper.removeAllColumns(db);
    }

    public static void clearBookDateBase(Context context) {
        sBookshelfDataBaseHelper = BookshelfDataBaseHelper.getInstance(context);
        SQLiteDatabase db = sBookshelfDataBaseHelper.getWritableDatabase();
        sBookshelfDataBaseHelper.removeAllColumns(db);
    }
}
