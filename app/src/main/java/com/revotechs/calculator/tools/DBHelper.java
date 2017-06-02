package com.revotechs.calculator.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Project Calculator
 * <p>
 * Created on 30.05.2017
 *
 * @author CriticalGnome
 */

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "CalcDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("Calculator", "--- onCreate database ---");
        db.execSQL("create table history (id integer primary key autoincrement, date text, expression text, result text, comment text, locked integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
