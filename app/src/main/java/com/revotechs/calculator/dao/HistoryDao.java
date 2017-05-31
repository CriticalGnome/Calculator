package com.revotechs.calculator.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.revotechs.calculator.tools.DBHelper;
import com.revotechs.calculator.tools.HistoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Calculator
 *
 * Created on 30.05.2017
 * @author CriticalGnome
 */

public class HistoryDao {

    private static final String TABLE_HISTORY = "history";
    private static final String FIELD_ID = "id";
    private static final String FIELD_COMMENT = "comment";
    private static final String FIELD_DATE = "date";
    private static final String FIELD_EXPRESSION = "expression";
    private static final String FIELD_RESULT = "result";
    private static final String ORDERING_DESC = "DESC";
    private static final String HISTORY_DAO = "historyDAO";
    private static final String NO_DATA = "No data";
    private static final String UPDATED = " updated";
    private static final String DELETED = " deleted";
    private static final String EQUAL_SIGN = " = ";
    private static volatile HistoryDao instance;
    private DBHelper dbHelper;
    private ContentValues cv;
    private SQLiteDatabase db;

    private HistoryDao() {}

    public static HistoryDao getInstance() {
        if (instance == null) {
            synchronized (HistoryDao.class) {
                if (instance == null) {
                    instance = new HistoryDao();
                }
            }
        }
        return instance;
    }

    public Long create(HistoryItem item, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        cv.put(FIELD_DATE, item.getDate());
        cv.put(FIELD_EXPRESSION, item.getExpression());
        cv.put(FIELD_RESULT, item.getResult());
        cv.put(FIELD_COMMENT, item.getComment());
        Long id = db.insert(TABLE_HISTORY, null, cv);
        item.setId(id);
        Log.d(HISTORY_DAO, item.toString());
        dbHelper.close();
        return id;
    }

    public HistoryItem getOne(Long id, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        HistoryItem item = new HistoryItem();
        Cursor cursor = db.query(TABLE_HISTORY, null, FIELD_ID + " = ?", new String[] {id.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(FIELD_ID);
            int dateColIndex = cursor.getColumnIndex(FIELD_DATE);
            int expressionColIndex = cursor.getColumnIndex(FIELD_EXPRESSION);
            int resultColIndex = cursor.getColumnIndex(FIELD_RESULT);
            int commentColIndex = cursor.getColumnIndex(FIELD_COMMENT);
            do {
                item.setId(cursor.getLong(idColIndex));
                item.setDate(cursor.getString(dateColIndex));
                item.setExpression(cursor.getString(expressionColIndex));
                item.setResult(cursor.getString(resultColIndex));
                item.setComment(cursor.getString(commentColIndex));
            } while (cursor.moveToNext());
        } else {
            Log.d(HISTORY_DAO, NO_DATA);
        }
        Log.d(HISTORY_DAO, item.toString());
        cursor.close();
        dbHelper.close();
        return item;
    }

    public List<HistoryItem> getAll(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        List<HistoryItem> items = new ArrayList<>();
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null, FIELD_ID + " " + ORDERING_DESC);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex(FIELD_ID);
            int dateColIndex = cursor.getColumnIndex(FIELD_DATE);
            int expressionColIndex = cursor.getColumnIndex(FIELD_EXPRESSION);
            int resultColIndex = cursor.getColumnIndex(FIELD_RESULT);
            int commentColIndex = cursor.getColumnIndex(FIELD_COMMENT);
            do {
                HistoryItem item = new HistoryItem();
                item.setId(cursor.getLong(idColIndex));
                item.setDate(cursor.getString(dateColIndex));
                item.setExpression(cursor.getString(expressionColIndex));
                item.setResult(cursor.getString(resultColIndex));
                item.setComment(cursor.getString(commentColIndex));
                items.add(item);
            } while (cursor.moveToNext());
        } else {
            Log.d(HISTORY_DAO, NO_DATA);
        }
        Log.d(HISTORY_DAO, items.toString());
        cursor.close();
        dbHelper.close();
        return items;
    }

    public void update(HistoryItem item, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        cv.put(FIELD_DATE, item.getDate());
        cv.put(FIELD_EXPRESSION, item.getExpression());
        cv.put(FIELD_RESULT, item.getResult());
        cv.put(FIELD_COMMENT, item.getComment());
        String where = FIELD_ID + EQUAL_SIGN + item.getId();
        db.update(TABLE_HISTORY, cv, where, null);
        Log.d(HISTORY_DAO, FIELD_ID + EQUAL_SIGN +  item.getId() + UPDATED);
    }

    public void delete(Long id, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_HISTORY, FIELD_ID + EQUAL_SIGN + id, null);
        Log.d(HISTORY_DAO, FIELD_ID + EQUAL_SIGN +  id + DELETED);
    }

}
