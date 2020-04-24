package com.amber.svoice.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAssist extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private static final String TABLE_NAME = "data";
    private static final String COLUMN_1 = "_id";
    public static final String COLUMN_2 = "_code";
    public static final String COLUMN_3 = "_name";

    public SQLiteAssist(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + COLUMN_1 + " integer primary key autoincrement," + COLUMN_2 + " varchar(64) unique," + COLUMN_3 + " varchar(64))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table " + TABLE_NAME + "");
        onCreate(db);
    }

    public Cursor query(String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = this.getReadableDatabase();
        if (database.isOpen()) {
            Cursor cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            return cursor;
        }
        return null;
    }

    public long insert(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db.isOpen()) {
                return db.insert(TABLE_NAME, null, contentValues);
            }
        } finally {
            db.close();
        }
        return 0;
    }

    public int delete(String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db.isOpen()) {
                return db.delete(TABLE_NAME, selection, selectionArgs);
            }
        } finally {
            db.close();
        }
        return 0;
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db.isOpen()) {
                return db.update(TABLE_NAME, values, selection, selectionArgs);
            }
        } finally {
            db.close();
        }
        return 0;
    }

    public String getName(String code) {
        String value = null;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select " + COLUMN_3 + " from " + TABLE_NAME + " where " + COLUMN_2 + "=?", new String[]{code});
        if (cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndex(COLUMN_3));
        }
        cursor.close();
        return value;
    }
}
