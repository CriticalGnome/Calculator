package com.revotechs.calculator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.revotechs.calculator.R;
import com.revotechs.calculator.tools.HistoryItem;

import java.util.List;

/**
 * Project Calculator
 * Created on 29.05.2017
 *
 * @author CriticalGnome
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HistoryItem> historyItems;

    public RecyclerViewAdapter(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_view, parent, false);
        holder = new HistoryItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        HistoryItemHolder historyItemHolder = (HistoryItemHolder) holder;
        historyItemHolder.dateTextView.setText(item.getDate().toString());
        historyItemHolder.expressionTextView.setText(item.getExpression());
        historyItemHolder.resultTextView.setText(item.getResult());

    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    private class HistoryItemHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView expressionTextView;
        TextView resultTextView;

        HistoryItemHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            expressionTextView = (TextView) itemView.findViewById(R.id.expression_text_view);
            resultTextView = (TextView) itemView.findViewById(R.id.result_text_view);
        }
    }
}
