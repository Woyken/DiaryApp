package com.uzkuraitis.karolis.diaryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

public class DiaryEntriesActivity extends AppCompatActivity {
    private final int REQUEST_CODE_ADD_DIARY_ENTRY = 0;
    private final int REQUEST_CODE_UPDATE_DIARY_ENTRY = 1;
    ListView lv;
    Calendar datePassed;
    public static List<DiaryEntry> diaryEntries = new ArrayList<>();

    public DiaryEntry getEntryById(long id)
    {
        for (DiaryEntry entry : diaryEntries)
        {
            if(entry.id == id)
                return entry;
        }
        return null;
    }

    private void updateEntries()
    {
        MySQLHelper db = new MySQLHelper(this);
        diaryEntries = db.getAllDiaryEntries();
        db.close();

        Iterator<DiaryEntry> i = diaryEntries.iterator();
        while (i.hasNext()) {
            DiaryEntry entr = i.next(); // must be called before you can call i.remove()
            if(entr.date.getTimeInMillis() < datePassed.getTimeInMillis() || entr.date.getTimeInMillis() >= datePassed.getTimeInMillis() + 24*60*60*1000)
            {
                i.remove();
            }
        }

        lv = findViewById(R.id.listView1);
        DiaryEntry[] diaryEntryArr = new DiaryEntry[diaryEntries.size()];
        diaryEntryArr = diaryEntries.toArray(diaryEntryArr);
        DiaryEntryAdapter diaryEntryListAdapter = new DiaryEntryAdapter(this, diaryEntryArr);
        diaryEntryListAdapter.setOnDatePickedListener(new DiaryEntryAdapter.OnRowClickedListener() {
            @Override
            public void onRowClicked(DiaryEntry entry) {
                editDiaryEntry(entry);
            }
        });
        lv.setAdapter(diaryEntryListAdapter);
    }

    private void addEntry(Calendar calendar, String content){
        DiaryEntry d = new DiaryEntry();
        d.content = content;
        d.date = calendar;
        diaryEntries.add(d);
        MySQLHelper db = new MySQLHelper(this);
        db.addDiaryEntry(d);
        db.close();
        //updateEntries();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entries);
        setTitle(getString(R.string.DiaryEntriesActivityTitle));

        Intent intent = getIntent();
        int[] datePassedArray = intent.getIntArrayExtra(MainCalendarActivity.EXTRA_MESSAGE);
        datePassed = new GregorianCalendar(datePassedArray[0], datePassedArray[1], datePassedArray[2], 0, 0, 0);
        setTitle(datePassedArray[0]+"-"+datePassedArray[1]+"-"+datePassedArray[2]);

        updateEntries();
    }

    private void inputText(final int hourOfDay, final int minute)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.reminderEnterMessage);

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Calendar date = datePassed;
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                addEntry(datePassed, value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        alert.show();
    }

    public void addDiaryEntryButton(View v)
    {
        Intent intent = new Intent(this, AddDiaryEntryActivity.class);
        intent.putExtra("dateTime", datePassed.getTimeInMillis());
        startActivityForResult(intent, REQUEST_CODE_ADD_DIARY_ENTRY);
    }

    private void editDiaryEntry(final DiaryEntry entry)
    {
        Intent intent = new Intent(this, AddDiaryEntryActivity.class);
        intent.putExtra("id", entry.id);
        intent.putExtra("dateTime", entry.date.getTimeInMillis());
        intent.putExtra("content", entry.content);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DIARY_ENTRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_ADD_DIARY_ENTRY)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Long timeInMilis = data.getLongExtra("resultDateTime", 0);
                String content = data.getStringExtra("resultString");
                Calendar n = new GregorianCalendar(0,0,0,0,0,0);
                n.setTimeInMillis(timeInMilis);
                addEntry(n, content);
            }
        }
        else if (requestCode == REQUEST_CODE_UPDATE_DIARY_ENTRY)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                long id = data.getLongExtra("id", -1);
                if(id == -1)
                {
                    //Update without id?
                    return;
                }
                Long timeInMilis = data.getLongExtra("resultDateTime", 0);
                MySQLHelper db = new MySQLHelper(this);
                if(timeInMilis < 0)
                {
                    //Delete reminder
                    db.deleteDiaryEntry(id);
                    db.close();
                    updateEntries();
                    return;
                }
                String message = data.getStringExtra("resultString");
                DiaryEntry entr = getEntryById(id);
                if(null != entr)
                {
                    entr.date.setTimeInMillis(timeInMilis);
                    entr.content = message;
                    db.updateDiaryEntry(entr);
                }
                db.close();

                updateEntries();
            }
        }
    }
}
