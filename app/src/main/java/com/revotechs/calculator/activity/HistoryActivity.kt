package com.revotechs.calculator.activity

import android.R.color.white
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_TEXT
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import com.revotechs.calculator.R
import com.revotechs.calculator.adapter.HistoryAdapter
import com.revotechs.calculator.dao.HistoryDao
import com.revotechs.calculator.databinding.ActivityHistoryBinding
import com.revotechs.calculator.tool.RecyclerItemClickListener

/**
 * Project Calculator
 * Created on 26.05.2017
 * @author CriticalGnome
 */
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private val adapter = HistoryAdapter(emptyList())
    private val historyDao = HistoryDao()
    private var searchString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = if (searchString.isEmpty()) {
            historyDao.getAll(binding.historyView.context)
        } else {
            historyDao.search(searchString, binding.historyView.context)
        }
        adapter.updateItems(items)

        binding.historyView.adapter = adapter
        binding.historyView.addItemDecoration(
            DividerItemDecoration(binding.historyView.context, VERTICAL)
        )

        binding.historyView.addOnItemTouchListener(
            RecyclerItemClickListener(
                binding.historyView.context,
                binding.historyView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(
                                    EXTRA_EXPRESSION_NAME,
                                    items[position].expression
                                )
                            })
                        finish()
                    }

                    override fun onLongItemClick(view: View, position: Int) {
                        AlertDialog.Builder(this@HistoryActivity)
                            .setTitle(R.string.history_item_alert_title)
                            .setItems(resources.getStringArray(R.array.history_item_alert_menu)) { _, which ->
                                when (which) {
                                    MENU_COMMENT -> {
                                        val input = EditText(this@HistoryActivity).apply { inputType = TYPE_CLASS_TEXT }
                                        AlertDialog.Builder(this@HistoryActivity)
                                            .setCancelable(false)
                                            .setTitle(R.string.comment_title)
                                            .setView(input)
                                            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                            .setPositiveButton(R.string.OK) { _, _ ->
                                                val item = items[position]
                                                var comment: String? = input.text.toString()
                                                if (comment?.isEmpty() == true) comment = null
                                                item.comment = comment
                                                historyDao.update(item, binding.historyView.context)
                                                adapter.notifyItemChanged(position)
                                            }.show()
                                        val comment = items[position].comment
                                        if (comment != null) {
                                            input.setText(comment)
                                            input.setSelection(0, comment.length)
                                        }
                                    }

                                    MENU_DELETE -> {
                                        AlertDialog.Builder(this@HistoryActivity)
                                            .setMessage(R.string.confirm_delete)
                                            .setCancelable(false)
                                            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                            .setPositiveButton(R.string.OK) { _, _ ->
                                                items[position].id?.let { historyDao.delete(it, binding.historyView.context) }
                                                adapter.updateItems(items.filterIndexed { index, _ -> index != position })
                                            }.show()
                                    }

                                    MENU_LOCK -> {
                                        with(items[position]) {
                                            locked = locked.not()
                                            historyDao.update(this, this@HistoryActivity)
                                            adapter.notifyItemChanged(position)
                                        }
                                    }
                                }
                            }.show()
                    }
                })
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                val context = this@HistoryActivity
                val input = EditText(context)
                input.inputType = TYPE_CLASS_TEXT
                AlertDialog.Builder(context)
                    .setTitle(R.string.search)
                    .setView(input)
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .setPositiveButton(R.string.OK) { _, _ ->
                        searchString = input.text.toString()
                        var items = historyDao.search(searchString, binding.historyView.context)
                        if (items.isNotEmpty()) {
                            adapter.updateItems(items)
                            binding.searchView.text = String.format("%s%s", getString(R.string.search_title), searchString)
                            binding.searchView.visibility = View.VISIBLE
                        } else {
                            binding.searchView.setBackgroundColor(ContextCompat.getColor(binding.historyView.context, R.color.searchStringEmpty))
                            binding.searchView.setTextColor(ContextCompat.getColor(binding.historyView.context, white))
                        }
                        binding.searchView.setOnClickListener {
                            searchString = ""
                            items = historyDao.getAll(binding.historyView.context)
                            adapter.updateItems(items)
                            binding.searchView.visibility = View.GONE
                        }
                    }.show()
                true
            }
            R.id.clear -> {
                val context = this@HistoryActivity
                AlertDialog.Builder(context)
                    .setMessage(R.string.clear_history_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.OK) { _, _ ->
                        historyDao.clearHistory(context)
                        adapter.updateItems(historyDao.getAll(context))
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val EXTRA_EXPRESSION_NAME = "expression"
        private const val MENU_COMMENT = 0
        private const val MENU_DELETE = 1
        private const val MENU_LOCK = 2
    }
}
