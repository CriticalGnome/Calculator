package com.revotechs.calculator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.revotechs.calculator.R;
import com.revotechs.calculator.entities.HistoryItem;

import java.util.List;
import java.util.Locale;

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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item_view, parent, false);
        holder = new HistoryItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final HistoryItem item = historyItems.get(position);
        HistoryItemHolder historyItemHolder = (HistoryItemHolder) holder;
        historyItemHolder.idTextView.setText(String.format(item.getId().toString(), Locale.ENGLISH));
        historyItemHolder.dateTextView.setText(item.getDate());
        historyItemHolder.expressionTextView.setText(item.getExpression());
        historyItemHolder.resultTextView.setText(item.getResult());
        if (item.getComment() != null) {
            historyItemHolder.commentView.setText(item.getComment());
            historyItemHolder.commentView.setVisibility(View.VISIBLE);
        }
        if (item.isLocked()) {
            historyItemHolder.lockImage.setVisibility(View.VISIBLE);
        } else {
            historyItemHolder.lockImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    private class HistoryItemHolder extends RecyclerView.ViewHolder {

        TextView idTextView;
        TextView dateTextView;
        TextView expressionTextView;
        TextView resultTextView;
        TextView commentView;
        ImageView lockImage;

        HistoryItemHolder(View itemView) {
            super(itemView);
            idTextView = (TextView) itemView.findViewById(R.id.id_text_view);
            dateTextView = (TextView) itemView.findViewById(R.id.date_text_view);
            expressionTextView = (TextView) itemView.findViewById(R.id.expression_text_view);
            resultTextView = (TextView) itemView.findViewById(R.id.result_text_view);
            commentView = (TextView) itemView.findViewById(R.id.comment_text_view);
            lockImage = (ImageView) itemView.findViewById(R.id.lock_image);

        }
    }
}
