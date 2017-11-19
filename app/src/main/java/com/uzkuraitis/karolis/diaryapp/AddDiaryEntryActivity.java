package com.uzkuraitis.karolis.diaryapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class AddDiaryEntryActivity extends AppCompatActivity {
    long origId = -1;
    Date origDateTime;
    Calendar newDateTime = new GregorianCalendar(0,0,0,0,0,0);
    EditText timeEdit;
    EditText messageEdit;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        timeEdit = findViewById(R.id.TimeEdit);
        messageEdit = findViewById(R.id.ContentEdit);

        Intent intent = getIntent();
        origId = intent.getLongExtra("id", -1);
        long gotTime = intent.getLongExtra("dateTime", -1);
        if(gotTime < 0)
        {
            finish();
            return;
        }

        messageEdit.setText(intent.getStringExtra("content"));

        origDateTime = new Date(gotTime);
        newDateTime.setTimeInMillis(gotTime);
        updateEditText();

        final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newDateTime.set(Calendar.MINUTE, minute);
                updateEditText();
            }
        };

        timeEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddDiaryEntryActivity.this, timePickerListener, newDateTime.get(Calendar.HOUR_OF_DAY), newDateTime.get(Calendar.MINUTE), true).show();
            }
        });
    }

    private void updateEditText()
    {
        timeEdit.setText(dateFormatter.format(new Date(newDateTime.getTimeInMillis())));
    }

    public void doneButton(View v)
    {
        Date parsed;
        try {
            parsed = dateFormatter.parse(timeEdit.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(this, R.string.timeFormatInvalid, Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar n = new GregorianCalendar(0,0,0,0,0,0);
        n.setTimeInMillis(origDateTime.getTime());
        Calendar temp = new GregorianCalendar(0,0,0,0,0,0);
        temp.setTimeInMillis(parsed.getTime());
        n.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
        n.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", origId);
        returnIntent.putExtra("resultDateTime", n.getTimeInMillis());
        returnIntent.putExtra("resultString", messageEdit.getText().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    public void deleteButton(View v)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", origId);
        returnIntent.putExtra("resultDateTime", -1L);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
