package com.kvile.countdown;

import static com.kvile.countdown.common.CountdownDataApplication.STORE_FILE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.ComponentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kvile.countdown.calendar.CalendarActivity;
import com.kvile.countdown.common.CountdownDataApplication;
import com.kvile.countdown.common.CountdownsAdapter;

public class MainActivity extends ComponentActivity {

    protected static final String EDIT_COUNTDOWN_POSITION = "editPosition";
    protected final CountdownDataApplication countdownDataApplication = new CountdownDataApplication(this);
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
        adapter = new CountdownsAdapter(this, countdownDataApplication.getList());
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
        countdownDataApplication.setList(STORE_FILE_NAME, adapter.getCountdowns());
    }

}