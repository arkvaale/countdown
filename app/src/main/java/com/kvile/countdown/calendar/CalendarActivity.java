package com.kvile.countdown.calendar;

import static com.kvile.countdown.common.CountdownDataApplication.STORE_FILE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.kvile.countdown.MainActivity;
import com.kvile.countdown.R;
import com.kvile.countdown.common.Countdown;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;

public class CalendarActivity extends MainActivity {

    private final static String UNNAMED_COUNTDOWN = "Unnamed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int editPosition = intent.getIntExtra(EDIT_COUNTDOWN_POSITION, -1);
        setContentView(R.layout.activity_calendar);
        EditText editText = findViewById(R.id.countdownName);
        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        if (editPosition >= 0) {
            Countdown countdownToEdit = adapter.getCountdowns().get(editPosition);
            editText.setText(countdownToEdit.getName());
            Calendar calendar = countdownToEdit.getCalendar();
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
            String name = StringUtils.isEmpty(editText.getText()) ? UNNAMED_COUNTDOWN : editText.getText().toString();
            save(name, cal, editPosition);
        });
    }

    protected void save(String name, Calendar cal, int editPosition) {
        if (editPosition >= 0) {
            adapter.editCountdown(new Countdown(name, cal), editPosition);
        } else {
            adapter.addCountdown(new Countdown(name, cal));
        }
        countdownDataApplication.setList(STORE_FILE_NAME, adapter.getCountdowns());
        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        CalendarActivity.this.startActivity(intent);
    }

}