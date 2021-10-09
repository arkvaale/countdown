package com.kvile.countdown;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ComponentActivity {

    protected static final String STORE_FILE_NAME = "countdowns";
    protected static final String EDIT_COUNTDOWN_POSITION = "editPosition";
    protected CountdownsAdapter adapter;
    private View previousClickedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.addCountdownButton).setOnClickListener(v -> openCalendar(null));

        initializeRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        adapter = new CountdownsAdapter(this, getList());
        setRecyclerView();
    }

    private void openCalendar(Integer position) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        intent.putExtra(EDIT_COUNTDOWN_POSITION, position);
        MainActivity.this.startActivity(intent);
    }

    private void setRecyclerView() {
        RecyclerView rvCountdowns = this.findViewById(R.id.countdowns);
        if (rvCountdowns != null) {
            rvCountdowns.setLayoutManager(new LinearLayoutManager(this));
            rvCountdowns.setHasFixedSize(true);
            rvCountdowns.setAdapter(adapter);
            rvCountdowns.requestFocus();
            adapter.setOnItemClickListener((position, v) -> {
                if (previousClickedView != null && v != previousClickedView) {
                    LinearLayout previousActionsLayout = previousClickedView.findViewById(R.id.actions);
                    previousActionsLayout.setVisibility(View.GONE);
                }
                previousClickedView = v;
                LinearLayout actionsLayout = v.findViewById(R.id.actions);
                if (actionsLayout.getVisibility() == View.GONE) {
                    actionsLayout.setVisibility(View.VISIBLE);
                } else {
                    actionsLayout.setVisibility(View.GONE);
                }
                Button editButton = v.findViewById(R.id.editButton);
                editButton.setOnClickListener(b -> openCalendar(position));
                Button deleteButton = v.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(b -> deleteCountdown(position));
            });
        }
    }

    private void deleteCountdown(int position) {
        adapter.deleteCountdown(position);
        adapter.notifyItemRemoved(position);
        setList(STORE_FILE_NAME, adapter.getCountdowns());
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(key, json);
    }

    public void set(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public List<Countdown> getList() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        List<Countdown> arrayItems = new ArrayList<>();
        String serializedObject = sharedPreferences.getString(STORE_FILE_NAME, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Countdown>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        arrayItems.sort(Comparator.comparingLong(Countdown::getDaysRemaining));
        return arrayItems;
    }

}