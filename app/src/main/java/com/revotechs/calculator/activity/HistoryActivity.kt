package com.revotechs.calculator.activity

import android.R.color.white
import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.revotechs.calculator.R
import com.revotechs.calculator.adapter.RecyclerViewAdapter
import com.revotechs.calculator.dao.HistoryDao
import com.revotechs.calculator.entitiy.HistoryItem
import com.revotechs.calculator.tool.RecyclerItemClickListener
import kotlinx.android.synthetic.main.activity_history.*

/**
 * Project Calculator
 * Created on 26.05.2017
 * @author CriticalGnome
 */
class HistoryActivity : AppCompatActivity(), View.OnTouchListener {

    private val historyDao = HistoryDao()
    private var screenWidth: Int = 0
    private var xCoordinate: Float = 0.toFloat()
    private var searchString = ""

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        history_view.layoutManager = manager

        val items: ArrayList<HistoryItem> = if (searchString.isEmpty()) {
            historyDao.getAll(history_view.context) as ArrayList<HistoryItem>
        } else {
            historyDao.search(searchString, history_view.context) as ArrayList<HistoryItem>
        }
        val adapter = RecyclerViewAdapter(items)
        history_view.adapter = adapter

        val itemDecoration = DividerItemDecoration(history_view.context, DividerItemDecoration.VERTICAL)
        history_view.addItemDecoration(itemDecoration)

        history_view.setOnTouchListener(this)
        history_view.addOnItemTouchListener(RecyclerItemClickListener(history_view.context, history_view, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent()
                intent.putExtra(EXTRA_EXPRESSION_NAME, items[position].expression)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun onLongItemClick(view: View, position: Int) {

                val res = resources
                val listMenuItems = res.getStringArray(R.array.history_item_alert_menu)

                val menuBuilder = AlertDialog.Builder(view.context)
                menuBuilder.setTitle(R.string.history_item_alert_title)
                menuBuilder.setItems(listMenuItems) { _, which ->
                    when (which) {
                        MENU_COMMENT -> {
                            val commentBuilder = AlertDialog.Builder(history_view.context)
                            val input = EditText(history_view.context)
                            input.inputType = InputType.TYPE_CLASS_TEXT
                            commentBuilder
                                    .setCancelable(false)
                                    .setTitle(R.string.comment_title)
                                    .setView(input)
                                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                    .setPositiveButton(R.string.OK) { _, _ ->
                                        val item = items[position]
                                        var comment: String? = input.text.toString()
                                        if (comment!!.isEmpty()) {
                                            comment = null
                                        }
                                        item.comment = comment
                                        historyDao.update(item, history_view.context)
                                        adapter.notifyItemChanged(position)
                                    }
                            val alertComment = commentBuilder.create()
                            alertComment.show()
                            val comment = items[position].comment
                            if (comment != null) {
                                input.setText(comment)
                                input.setSelection(0, comment.length)
                            }
                        }
                        MENU_DELETE -> {
                            val deleteBuilder = AlertDialog.Builder(history_view.context)
                            deleteBuilder
                                    .setMessage(R.string.confirm_delete)
                                    .setCancelable(false)
                                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                    .setPositiveButton(R.string.OK) { _, _ ->
                                        val item = items[position]
                                        historyDao.delete(item.id!!, history_view.context)
                                        items.removeAt(position)
                                        adapter.notifyItemRemoved(position)
                                    }
                            val alertDelete = deleteBuilder.create()
                            alertDelete.show()
                        }
                        MENU_LOCK -> {
                            val item = items[position]
                            item.locked = !item.locked
                            historyDao.update(item, history_view.context)
                            adapter.notifyItemChanged(position)
                        }
                    }
                }
                val alert = menuBuilder.create()
                alert.show()
            }
        }))
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> xCoordinate = event.x
            MotionEvent.ACTION_UP ->

                if (event.x - xCoordinate > screenWidth / 2) {
                    val i = Intent(v.context, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
        }
        return false
    }

    fun onClickSearch(item: MenuItem) {
        val context = this@HistoryActivity
        val searchBuilder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        var items: ArrayList<HistoryItem>
        searchBuilder
                .setTitle(R.string.search)
                .setView(input)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                .setPositiveButton(R.string.OK) { _, _ ->
                    searchString = input.text.toString()
                    items = historyDao.search(searchString, history_view.context) as ArrayList<HistoryItem>
                    var adapter = RecyclerViewAdapter(items)
                    history_view.adapter = adapter
                    var itemDecoration = DividerItemDecoration(history_view.context, DividerItemDecoration.VERTICAL)
                    history_view.addItemDecoration(itemDecoration)
                    adapter.notifyDataSetChanged()
                    search_view.text = String.format("%s%s", getString(R.string.search_title), searchString)
                    search_view.visibility = View.VISIBLE
                    if (items.isEmpty()) {
                        search_view.setBackgroundColor(ContextCompat.getColor(history_view.context, R.color.searchStringEmpty))
                        search_view.setTextColor(ContextCompat.getColor(history_view.context, white))
                    }
                    search_view.setOnClickListener {
                        searchString = ""
                        items = historyDao.getAll(history_view.context) as ArrayList<HistoryItem>
                        adapter = RecyclerViewAdapter(items)
                        history_view.adapter = adapter
                        itemDecoration = DividerItemDecoration(history_view.context, DividerItemDecoration.VERTICAL)
                        history_view.addItemDecoration(itemDecoration)
                        adapter.notifyDataSetChanged()
                        search_view.visibility = View.GONE
                    }
                }
        val alertSearch = searchBuilder.create()
        alertSearch.show()
    }

    fun onClickClear(item: MenuItem) {
        val context = this@HistoryActivity
        val clearBuilder = AlertDialog.Builder(context)
        clearBuilder
                .setMessage(R.string.clear_history_message)
                .setCancelable(false)
                .setPositiveButton(R.string.OK) { _, _ ->
                    historyDao.clearHistory(context)
                    val items = historyDao.getAll(context) as ArrayList<HistoryItem>
                    val adapter = RecyclerViewAdapter(items)
                    history_view.adapter = adapter
                    val itemDecoration = DividerItemDecoration(history_view.context, DividerItemDecoration.VERTICAL)
                    history_view.addItemDecoration(itemDecoration)
                    adapter.notifyDataSetChanged()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        val alertClear = clearBuilder.create()
        alertClear.show()
    }

    companion object {

        private const val EXTRA_EXPRESSION_NAME = "expression"
        private const val MENU_COMMENT = 0
        private const val MENU_DELETE = 1
        private const val MENU_LOCK = 2
    }
}
