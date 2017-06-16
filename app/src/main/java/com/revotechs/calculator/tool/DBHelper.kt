package com.revotechs.calculator.tool

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Project Calculator
 * Created on 30.05.2017
 * @author CriticalGnome
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, "CalcDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("Calculator", "--- onCreate database ---")
        db.execSQL("create table history (id integer primary key autoincrement, date text, expression text, result text, comment text, locked integer);")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
