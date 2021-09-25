package com.kvile.countdown;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ComponentActivity {

    private static final String STORE_FILE_NAME = "countdowns";
    private CountdownsAdapter adapter;
    private View previousClickedView;
    private int currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeContentView(R.layout.activity_main);

        setAddCountdownButtonOnClickListener();

        adapter = new CountdownsAdapter(this, getList());
        setRecyclerView();
    }

    private void openCalendar(Integer position) {
        changeContentView(R.layout.calendar);
        EditText editText = findViewById(R.id.countdownName);
        DatePicker datePicker = findViewById(R.id.datePicker);
        if (position != null) {
            Countdown countdownToEdit = adapter.getCountdowns().get(position);
            editText.setText(countdownToEdit.getName());
            Calendar calendar = countdownToEdit.getCalendar();
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
            String name = StringUtils.isEmpty(editText.getText()) ? "Unnamed" : editText.getText().toString();
            save(name, cal, position);
        });
    }

    private void save(String name, Calendar cal, Integer position) {
        if (position != null) {
            adapter.editCountdown(new Countdown(name, cal), position);
        } else {
            adapter.addCountdown(new Countdown(name, cal));
        }
        changeContentView(R.layout.activity_main);
        setRecyclerView();
        setAddCountdownButtonOnClickListener();
        setList(STORE_FILE_NAME, adapter.getCountdowns());
    }

    private void setRecyclerView() {
        RecyclerView rvCountdowns = this.findViewById(R.id.countdowns);
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
            editButton.setOnClickListener(b -> editCountdown(position));
            Button deleteButton = v.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(b -> deleteCountdown(position));
        });
    }

    private void editCountdown(int position) {
        openCalendar(position);
    }

    private void deleteCountdown(int position) {
        adapter.deleteCountdown(position);
        adapter.notifyItemRemoved(position);
        setList(STORE_FILE_NAME, adapter.getCountdowns());
    }

    private void setAddCountdownButtonOnClickListener() {
        Button addCountdownButton = findViewById(R.id.addCountdownButton);
        addCountdownButton.setOnClickListener(v -> openCalendar(null));
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

    private void changeContentView(int id) {
        setContentView(id);
        currentView = id;
    }

    @Override
    public void onBackPressed() {
        if (currentView == R.layout.calendar) {
            changeContentView(R.layout.activity_main);
            setRecyclerView();
            setAddCountdownButtonOnClickListener();
        } else {
            super.onBackPressed();
        }
    }
}