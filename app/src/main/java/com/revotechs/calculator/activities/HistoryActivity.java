package com.revotechs.calculator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.revotechs.calculator.R;
import com.revotechs.calculator.adapters.RecyclerViewAdapter;
import com.revotechs.calculator.dao.HistoryDao;
import com.revotechs.calculator.tools.HistoryItem;
import com.revotechs.calculator.tools.RecyclerItemClickListener;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements View.OnTouchListener {

    //TODO Поиск по записям

    public static final String EXTRA_EXPRESSION_NAME = "expression";
    private String[] listMenuItems;
    private HistoryDao historyDao = HistoryDao.getInstance();
    private int screenWidth;
    private float xCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        final RecyclerView historyView = (RecyclerView) findViewById(R.id.history_view);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyView.setLayoutManager(manager);
        final List<HistoryItem> items = historyDao.getAll(historyView.getContext());
        final RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        historyView.setAdapter(adapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
        historyView.addItemDecoration(itemDecoration);
        historyView.setOnTouchListener(this);
        historyView.addOnItemTouchListener(new RecyclerItemClickListener(historyView.getContext(), historyView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_EXPRESSION_NAME, items.get(position).getExpression());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onLongItemClick(final View view, final int position) {

                Resources res = getResources();
                listMenuItems = res.getStringArray(R.array.history_item_alert_menu);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.history_item_alert_title);
                builder.setItems(listMenuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), listMenuItems[which], Toast.LENGTH_SHORT).show();
                        switch (which) {
                            case 0:
                                //TODO Добавление комментаря к записи
                                break;
                            case 1:
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
                                                items.remove(position);
                                                adapter.notifyItemRemoved(position);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        }));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xCoord = event.getX();
                break;
            case MotionEvent.ACTION_UP:

                if (event.getX() - xCoord > screenWidth / 2) {
                    Intent i = new Intent(v.getContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }
        }
        return false;
    }
}
