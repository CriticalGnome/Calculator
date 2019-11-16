package com.revotechs.calculator.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.revotechs.calculator.entitiy.HistoryItem
import com.revotechs.calculator.tool.DBHelper
import java.util.*

/**
 * Project Calculator
 * Created on 30.05.2017
 * @author CriticalGnome
 */
class HistoryDao {

    fun create(item: HistoryItem, context: Context): Long {
        val dbHelper = DBHelper(context)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put(FIELD_DATE, item.date)
        cv.put(FIELD_EXPRESSION, item.expression)
        cv.put(FIELD_RESULT, item.result)
        cv.put(FIELD_COMMENT, item.comment)
        if (item.locked) {
            cv.put(FIELD_LOCKED, 1)
        } else {
            cv.put(FIELD_LOCKED, 0)
        }
        val id = db.insert(TABLE_HISTORY, null, cv)
        item.id = id
        Log.d(HISTORY_DAO, item.toString())
        dbHelper.close()
        return id
    }

//    fun getOne(id: Long, context: Context): HistoryItem {
//        val dbHelper = DBHelper(context)
//        val db = dbHelper.readableDatabase
//        var item = HistoryItem(0L, "", "", "", "", false)
//        val cursor = db.query(TABLE_HISTORY, null, "$FIELD_ID = ?", arrayOf(id.toString()), null, null, null)
//        if (cursor.moveToFirst()) {
//            do {
//                item = constructItem(cursor)
//            } while (cursor.moveToNext())
//        } else {
//            Log.d(HISTORY_DAO, NO_DATA)
//        }
//        Log.d(HISTORY_DAO, item.toString())
//        cursor.close()
//        dbHelper.close()
//        return item
//    }

    fun getAll(context: Context): List<HistoryItem> {
        val dbHelper = DBHelper(context)
        val db = dbHelper.readableDatabase
        val items = ArrayList<HistoryItem>()
        val cursor = db.query(TABLE_HISTORY, null, null, null, null, null, "$FIELD_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                items.add(constructItem(cursor))
            } while (cursor.moveToNext())
        } else {
            Log.d(HISTORY_DAO, NO_DATA)
        }
        Log.d(HISTORY_DAO, items.toString())
        cursor.close()
        dbHelper.close()
        return items
    }

    fun update(item: HistoryItem, context: Context) {
        val dbHelper = DBHelper(context)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put(FIELD_DATE, item.date)
        cv.put(FIELD_EXPRESSION, item.expression)
        cv.put(FIELD_RESULT, item.result)
        cv.put(FIELD_COMMENT, item.comment)
        if (item.locked) {
            cv.put(FIELD_LOCKED, 1)
        } else {
            cv.put(FIELD_LOCKED, 0)
        }
        db.update(TABLE_HISTORY, cv, "$FIELD_ID = ${item.id}", null)
        Log.d(HISTORY_DAO, "$FIELD_ID = ${item.id} updated")
    }

    fun delete(id: Long, context: Context) {
        val dbHelper = DBHelper(context)
        val db = dbHelper.writableDatabase
        db.delete(TABLE_HISTORY, "$FIELD_ID = $id", null)
        Log.d(HISTORY_DAO, "$FIELD_ID = $id deleted")
    }

    fun search(search: String, context: Context): List<HistoryItem> {
        val dbHelper = DBHelper(context)
        val db = dbHelper.readableDatabase
        val items = ArrayList<HistoryItem>()
        val cursor = db.query(
                TABLE_HISTORY,
                null,
                "$FIELD_DATE LIKE '%$search%' or $FIELD_EXPRESSION LIKE '%$search%' or $FIELD_RESULT LIKE '%$search%' or $FIELD_COMMENT LIKE '%$search%'",
                null,
                null,
                null,
                "$FIELD_ID DESC")
        if (cursor.moveToFirst()) {
            do {
                items.add(constructItem(cursor))
            } while (cursor.moveToNext())
            Log.d(HISTORY_DAO, items.toString())
        } else {
            Log.d(HISTORY_DAO, NO_DATA)
        }
        cursor.close()
        dbHelper.close()
        return items
    }

    fun clearHistory(context: Context) {
        val dbHelper = DBHelper(context)
        val db = dbHelper.writableDatabase
        db.delete(TABLE_HISTORY, "$FIELD_LOCKED = 0", null)
    }

    private fun constructItem(cursor: Cursor): HistoryItem {
        return HistoryItem(
                cursor.getLong(cursor.getColumnIndex(FIELD_ID)),
                cursor.getString(cursor.getColumnIndex(FIELD_DATE)),
                cursor.getString(cursor.getColumnIndex(FIELD_EXPRESSION)),
                cursor.getString(cursor.getColumnIndex(FIELD_RESULT)),
                cursor.getString(cursor.getColumnIndex(FIELD_COMMENT)),
                cursor.getInt(cursor.getColumnIndex(FIELD_LOCKED)) == 1)
    }

    companion object {

        private const val HISTORY_DAO = "historyDAO"
        private const val TABLE_HISTORY = "history"
        private const val FIELD_ID = "id"
        private const val FIELD_DATE = "date"
        private const val FIELD_EXPRESSION = "expression"
        private const val FIELD_RESULT = "result"
        private const val FIELD_COMMENT = "comment"
        private const val FIELD_LOCKED = "locked"
        private const val NO_DATA = "No data"

    }
}
