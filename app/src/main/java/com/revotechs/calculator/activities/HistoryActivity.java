package com.revotechs.calculator.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.revotechs.calculator.R;
import com.revotechs.calculator.adapters.RecyclerViewAdapter;
import com.revotechs.calculator.dao.HistoryDao;
import com.revotechs.calculator.entities.HistoryItem;
import com.revotechs.calculator.tools.RecyclerItemClickListener;

import java.util.List;

import static android.R.color.white;

public class HistoryActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String EXTRA_EXPRESSION_NAME = "expression";
    private static final int MENU_COMMENT = 0;
    private static final int MENU_DELETE = 1;
    private static final int MENU_LOCK = 2;
    private RecyclerView historyView;
    private TextView searchView;
    private RecyclerViewAdapter adapter;
    private RecyclerView.ItemDecoration itemDecoration;
    private List<HistoryItem> items;
    private String[] listMenuItems;
    private HistoryDao historyDao = HistoryDao.getInstance();
    private int screenWidth;
    private float xCoord;
    private String searchString = "";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        historyView = (RecyclerView) findViewById(R.id.history_view);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        historyView.setLayoutManager(manager);

        if (searchString.isEmpty()) {
            items = historyDao.getAll(historyView.getContext());
        } else {
            items = historyDao.search(searchString, historyView.getContext());
        }

        adapter = new RecyclerViewAdapter(items);
        historyView.setAdapter(adapter);

        itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
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

                AlertDialog.Builder menuBuilder = new AlertDialog.Builder(view.getContext());
                menuBuilder.setTitle(R.string.history_item_alert_title);
                menuBuilder.setItems(listMenuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MENU_COMMENT:
                                AlertDialog.Builder commentBuilder = new AlertDialog.Builder(historyView.getContext());
                                final EditText input = new EditText(historyView.getContext());
                                input.setInputType(InputType.TYPE_CLASS_TEXT);
                                commentBuilder
                                        .setCancelable(false)
                                        .setTitle(R.string.comment_title)
                                        .setView(input)
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
                                                String comment = input.getText().toString();
                                                if (comment.isEmpty()) {
                                                    comment = null;
                                                }
                                                item.setComment(comment);
                                                historyDao.update(item, historyView.getContext());
                                                adapter.notifyItemChanged(position);
                                            }
                                        });
                                AlertDialog alertComment = commentBuilder.create();
                                alertComment.show();
                                String comment = items.get(position).getComment();
                                if (comment != null) {
                                    input.setText(comment);
                                    input.setSelection(0, comment.length());
                                }
                                break;
                            case MENU_DELETE:
                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(historyView.getContext());
                                deleteBuilder
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
                                AlertDialog alertDelete = deleteBuilder.create();
                                alertDelete.show();
                                break;
                            case MENU_LOCK:
                                HistoryItem item = items.get(position);
                                if (item.isLocked()) {
                                    item.setLocked(false);
                                } else {
                                    item.setLocked(true);
                                }
                                historyDao.update(item, historyView.getContext());
                                adapter.notifyItemChanged(position);
                                break;
                        }
                    }
                });
                AlertDialog alert = menuBuilder.create();
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

    public void onClickSearch(MenuItem item) {
        final Context context = HistoryActivity.this;
        AlertDialog.Builder searchBuilder = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        searchBuilder
                .setTitle(R.string.search)
                .setView(input)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchString = input.getText().toString();
                        items = historyDao.search(searchString, historyView.getContext());
                        adapter = new RecyclerViewAdapter(items);
                        historyView.setAdapter(adapter);
                        itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
                        historyView.addItemDecoration(itemDecoration);
                        adapter.notifyDataSetChanged();
                        searchView = (TextView) findViewById(R.id.search_view);
                        searchView.setText(String.format("%s%s", getString(R.string.search_title), searchString));
                        searchView.setVisibility(View.VISIBLE);
                        if (items.isEmpty()) {
                            searchView.setBackgroundColor(ContextCompat.getColor(historyView.getContext(), R.color.searchStringEmpty));
                            searchView.setTextColor(ContextCompat.getColor(historyView.getContext(), white));
                        }
                        searchView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                searchString = "";
                                items = historyDao.getAll(historyView.getContext());
                                adapter = new RecyclerViewAdapter(items);
                                historyView.setAdapter(adapter);
                                itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
                                historyView.addItemDecoration(itemDecoration);
                                adapter.notifyDataSetChanged();
                                searchView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
        AlertDialog alertSearch = searchBuilder.create();
        alertSearch.show();
    }

    public void onClickClear(MenuItem item) {
        final Context context = HistoryActivity.this;
        AlertDialog.Builder clearBuilder = new AlertDialog.Builder(context);
        clearBuilder
                .setMessage(R.string.clear_history_message)
                .setCancelable(false)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        historyDao.crearHistory(context);
                        items = historyDao.getAll(context);
                        adapter = new RecyclerViewAdapter(items);
                        historyView.setAdapter(adapter);
                        itemDecoration = new DividerItemDecoration(historyView.getContext(), DividerItemDecoration.VERTICAL);
                        historyView.addItemDecoration(itemDecoration);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertClear = clearBuilder.create();
        alertClear.show();
    }
}
