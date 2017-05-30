package com.revotechs.calculator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.revotechs.calculator.R;
import com.revotechs.calculator.adapters.RecyclerViewAdapter;
import com.revotechs.calculator.dao.HistoryDao;
import com.revotechs.calculator.tools.HistoryItem;
import com.revotechs.calculator.tools.RecyclerItemClickListener;

import java.util.List;

import static android.support.v7.recyclerview.R.attr.layoutManager;

public class HistoryActivity extends AppCompatActivity {

    private HistoryDao historyDao = HistoryDao.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final RecyclerView historyView = (RecyclerView) findViewById(R.id.history_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyView.setLayoutManager(manager);
        final List<HistoryItem> items = historyDao.getAll(historyView.getContext());
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        historyView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
        historyView.addItemDecoration(itemDecoration);
        historyView.addOnItemTouchListener(new RecyclerItemClickListener(historyView.getContext(), historyView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String expression = ((TextView) view.findViewById(R.id.expression_text_view)).getText().toString();
                Intent intent = new Intent();
                intent.putExtra("expression", expression);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
            }

            @Override
            public void onLongItemClick(final View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(historyView.getContext());
                builder
                        .setMessage(R.string.confirm_delete)
                        .setCancelable(false)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HistoryItem item = items.get(position);
                                historyDao.delete(item.getId(), historyView.getContext());
                                adapter.notifyItemRemoved(position);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }));

    }
}
