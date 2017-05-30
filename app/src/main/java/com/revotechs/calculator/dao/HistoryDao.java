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
 * <p>
 * Created on 30.05.2017
 *
 * @author CriticalGnome
 */

public class HistoryDao {

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
        cv.put("date", item.getDate());
        cv.put("expression", item.getExpression());
        cv.put("result", item.getResult());
        Long id = db.insert("history", null, cv);
        item.setId(id);
        Log.d("HistoryDAO", item.toString());
        dbHelper.close();
        return id;
    }

    public HistoryItem getOne(Long id, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        HistoryItem item = new HistoryItem();
        Cursor cursor = db.query("history", null, "id = ?", new String[] {id.toString()}, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("date");
            int expressionColIndex = cursor.getColumnIndex("expression");
            int resultColIndex = cursor.getColumnIndex("result");
            do {
                item.setId(cursor.getLong(idColIndex));
                item.setDate(cursor.getString(dateColIndex));
                item.setExpression(cursor.getString(expressionColIndex));
                item.setResult(cursor.getString(resultColIndex));
            } while (cursor.moveToNext());
        } else {
            Log.d("historyDAO", "No data");
        }
        Log.d("HistoryDAO", item.toString());
        cursor.close();
        dbHelper.close();
        return item;
    }

    public List<HistoryItem> getAll(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        List<HistoryItem> items = new ArrayList<>();
        Cursor cursor = db.query("history", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("date");
            int expressionColIndex = cursor.getColumnIndex("expression");
            int resultColIndex = cursor.getColumnIndex("result");
            do {
                HistoryItem item = new HistoryItem();
                item.setId(cursor.getLong(idColIndex));
                item.setDate(cursor.getString(dateColIndex));
                item.setExpression(cursor.getString(expressionColIndex));
                item.setResult(cursor.getString(resultColIndex));
                items.add(item);
            } while (cursor.moveToNext());
        } else {
            Log.d("historyDAO", "No data");
        }
        Log.d("HistoryDAO", items.toString());
        cursor.close();
        dbHelper.close();
        return items;
    }

    public void update(HistoryItem item, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        cv = new ContentValues();
        cv.put("date", item.getDate());
        cv.put("expression", item.getExpression());
        cv.put("result", item.getResult());
        String where = "id = " + item.getId();
        db.update("history", cv, where, null);
    }

    public void delete(Long id, Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        db.delete("history", "id = " + id, null);
        Log.d("HistoryDAO", "item id=" + id + " deleted");
    }

}
