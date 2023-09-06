package com.revotechs.calculator.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revotechs.calculator.databinding.HistoryItemViewBinding
import com.revotechs.calculator.entitiy.HistoryItem
import java.util.Locale

/**
 * Project Calculator
 * Created on 29.05.2017
 * @author CriticalGnome
 */
class HistoryAdapter(
    private var historyItems: List<HistoryItem>
) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    fun updateItems(historyItems: List<HistoryItem>) {
        DiffUtil.calculateDiff(HistoryDiffCallback(this.historyItems, historyItems)).also {
            this.historyItems = historyItems
            it.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder =
        HistoryHolder(
            HistoryItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) =
        holder.bind(historyItems[position])

    override fun getItemCount() = historyItems.size

    private class HistoryDiffCallback(
        private val ol: List<HistoryItem>,
        private val nl: List<HistoryItem>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = ol.size
        override fun getNewListSize() = nl.size
        override fun areItemsTheSame(o: Int, n: Int) = ol[o].id == nl[n].id
        override fun areContentsTheSame(o: Int, n: Int) = ol[o] == nl[n]
    }

    inner class HistoryHolder(
        private val binding: HistoryItemViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryItem) = with(binding) {
            idTextView.text = String.format(item.id.toString(), Locale.ENGLISH)
            dateTextView.text = item.date
            expressionTextView.text = item.expression
            resultTextView.text = item.result
            item.comment?.let { commentTextView.text = it }
            commentTextView.isVisible = item.comment.isNullOrEmpty().not()
            lockImage.isVisible = item.locked
        }
    }
}
