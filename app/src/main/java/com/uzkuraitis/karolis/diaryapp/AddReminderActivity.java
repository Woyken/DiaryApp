package com.uzkuraitis.karolis.diaryapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity {
    long origId = -1;
    Date origDateTime;
    Calendar newDateTime = new GregorianCalendar(0,0,0,0,0,0);
    EditText dateEdit;
    EditText messageEdit;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dateEdit = findViewById(R.id.DateReminder);
        messageEdit = findViewById(R.id.MessageReminder);

        Intent intent = getIntent();
        origId = intent.getLongExtra("id", -1);
        long gotTime = intent.getLongExtra("milis", -1);
        messageEdit.setText(intent.getStringExtra("message"));

        if(gotTime >= 0)
        {
            origDateTime = new Date(gotTime);
            newDateTime.setTimeInMillis(gotTime);
            updateEditText();
        }
        else
        {
            View deleteBtn = findViewById(R.id.deleteBtn);
            deleteBtn.setVisibility(View.GONE);
            newDateTime = Calendar.getInstance();
        }

        final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newDateTime.set(Calendar.MINUTE, minute);
                updateEditText();
            }
        };

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDateTime.set(Calendar.YEAR, year);
                newDateTime.set(Calendar.MONTH, monthOfYear);
                newDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                new TimePickerDialog(AddReminderActivity.this, timePickerListener, newDateTime.get(Calendar.HOUR_OF_DAY), newDateTime.get(Calendar.MINUTE), true).show();
                updateEditText();
            }
        };

        dateEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddReminderActivity.this, datePickerListener, newDateTime.get(Calendar.YEAR), newDateTime.get(Calendar.MONTH), newDateTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateEditText()
    {
        dateEdit.setText(dateFormatter.format(new Date(newDateTime.getTimeInMillis())));
    }

    public void doneButton(View v)
    {
        Date parsed;
        try {
            parsed = dateFormatter.parse(dateEdit.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(this, R.string.dateFormatInvalid, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", origId);
        returnIntent.putExtra("resultDate", parsed.getTime());
        returnIntent.putExtra("resultString", messageEdit.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    public void deleteButton(View v)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", origId);
        returnIntent.putExtra("resultDate", -1L);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
