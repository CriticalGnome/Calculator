package com.revotechs.calculator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.revotechs.calculator.R
import com.revotechs.calculator.entitiy.HistoryItem
import java.util.Locale

/**
 * Project Calculator
 * Created on 29.05.2017
 * @author CriticalGnome
 */
class RecyclerViewAdapter(private val historyItems: List<HistoryItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val holder: RecyclerView.ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_view, parent, false)
        holder = HistoryItemHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = historyItems[position]
        val historyItemHolder = holder as HistoryItemHolder
        historyItemHolder.idTextView.text = String.format(item.id.toString(), Locale.ENGLISH)
        historyItemHolder.dateTextView.text = item.date
        historyItemHolder.expressionTextView.text = item.expression
        historyItemHolder.resultTextView.text = item.result
        if (item.comment != null) {
            historyItemHolder.commentView.text = item.comment
            historyItemHolder.commentView.visibility = View.VISIBLE
        }
        if (item.locked) {
            historyItemHolder.lockImage.visibility = View.VISIBLE
        } else {
            historyItemHolder.lockImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return historyItems.size
    }

    private inner class HistoryItemHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var idTextView: TextView = itemView.findViewById(R.id.id_text_view) as TextView
        internal var dateTextView: TextView = itemView.findViewById(R.id.date_text_view) as TextView
        internal var expressionTextView: TextView = itemView.findViewById(R.id.expression_text_view) as TextView
        internal var resultTextView: TextView = itemView.findViewById(R.id.result_text_view) as TextView
        internal var commentView: TextView = itemView.findViewById(R.id.comment_text_view) as TextView
        internal var lockImage: ImageView = itemView.findViewById(R.id.lock_image) as ImageView

    }
}
