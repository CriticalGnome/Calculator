package com.revotechs.calculator.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.revotechs.calculator.R;
import com.revotechs.calculator.adapters.RecyclerViewAdapter;
import com.revotechs.calculator.tools.HistoryItem;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyView;
    private RecyclerViewAdapter adapter;
    public static ArrayList<HistoryItem> historyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

//        historyView = (RecyclerView) findViewById(R.id.historyView);
//        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        historyView.setLayoutManager(manager);
//        adapter = new RecyclerViewAdapter(historyItems);

    }
}
