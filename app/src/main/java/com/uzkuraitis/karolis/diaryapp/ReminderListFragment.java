package com.uzkuraitis.karolis.diaryapp;

/**
 * Created by karol on 11/19/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


public class ReminderListFragment extends Fragment
{
    private int REQUEST_CODE_ADD_REMINDER = 0;
    private int REQUEST_CODE_UPDATE_REMINDER = 1;

    public ReminderListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(null != container)
            container.removeAllViews();
        View rootView = inflater.inflate(R.layout.activity_reminders, container, false);
        lv = rootView.findViewById(R.id.listView1);
        fab = rootView.findViewById(R.id.floatingActionBtnAddReminder);
        return rootView;
    }

    ListView lv;
    FloatingActionButton fab;
    public static List<ReminderEntry> reminderEntries = new ArrayList<>();

    public ReminderEntry getReminderEntryById(long id)
    {
        for (ReminderEntry entry : reminderEntries)
        {
            if(entry.id == id)
                return entry;
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.RemindersActivityTitle));
        updateRemindersList();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminderButton(v);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

        public void updateRemindersList()
    {
        MySQLHelper db = new MySQLHelper(getActivity());
        reminderEntries = db.getAllReminderEntries();
        Iterator<ReminderEntry> i = reminderEntries.iterator();
        while (i.hasNext()) {
            ReminderEntry entr = i.next();
            if(entr.calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
            {
                db.deleteReminderEntry(entr);
                i.remove();
            }
        }

        db.close();
        ReminderEntry[] diaryEntryArr = new ReminderEntry[reminderEntries.size()];
        diaryEntryArr = reminderEntries.toArray(diaryEntryArr);
        ReminderEntryAdapter adapter = new ReminderEntryAdapter(getActivity(), diaryEntryArr);
        adapter.setOnDatePickedListener(new ReminderEntryAdapter.OnRowClickedListener() {
            @Override
            public void onRowClicked(ReminderEntry entry) {
                Toast.makeText(getActivity(), "Edit this reminder:", Toast.LENGTH_LONG).show();
                editReminder(entry);
            }
        });
        lv.setAdapter(adapter);
    }

    private void editReminder(final ReminderEntry entry)
    {
        Intent intent = new Intent(getActivity(), AddReminderActivity.class);
        intent.putExtra("id", entry.id);
        intent.putExtra("milis", entry.calendar.getTimeInMillis());
        intent.putExtra("message", entry.content);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_REMINDER);
    }

    private void fillEntries(int year, int month, int day, int hourOfDay, int minute, String message)
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, hourOfDay, minute, 0);


        if(calendar.before(Calendar.getInstance()))
        {
            Toast.makeText(getActivity(), R.string.warnReminderInPast, Toast.LENGTH_SHORT).show();
        }
        else
        {
            MySQLHelper db = new MySQLHelper(getActivity());
            ReminderEntry d = new ReminderEntry();
            d.content = message;
            d.calendar = calendar;
            db.addReminderEntry(d);
            db.close();
            updateRemindersList();

            for (ReminderEntry entry: reminderEntries)
            {
                if(entry.calendar.getTimeInMillis() == calendar.getTimeInMillis())
                {
                    NotificationScheduler.setReminder(getActivity(), ReminderAlarmReceiver.class, entry.id, entry.calendar.getTimeInMillis(), getString(R.string.ReminderTitle), message);
                    Toast.makeText(getActivity(), String.format(getString(R.string.reminderAfterToastText), (calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000), Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE_ADD_REMINDER)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Long timeInMilis = data.getLongExtra("resultDate", 0);
                String message = data.getStringExtra("resultString");
                Calendar n = new GregorianCalendar(0,0,0,0,0,0);
                n.setTimeInMillis(timeInMilis);
                fillEntries(n.get(Calendar.YEAR),n.get(Calendar.MONTH),n.get(Calendar.DAY_OF_MONTH),n.get(Calendar.HOUR_OF_DAY), n.get(Calendar.MINUTE),message);
            }
        }
        else if (requestCode == REQUEST_CODE_UPDATE_REMINDER)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                long id = data.getLongExtra("id", -1);
                if(id == -1)
                {
                    //Update without id?
                    return;
                }
                Long timeInMilis = data.getLongExtra("resultDate", 0);
                MySQLHelper db = new MySQLHelper(getActivity());
                if(timeInMilis < 0)
                {
                    //Delete reminder
                    db.deleteReminderEntry(id);
                    NotificationScheduler.cancelReminder(getActivity(), ReminderAlarmReceiver.class, (int)id);
                    db.close();
                    updateRemindersList();
                    return;
                }
                String message = data.getStringExtra("resultString");
                ReminderEntry entr = getReminderEntryById(id);
                if(null != entr)
                {
                    entr.calendar.setTimeInMillis(timeInMilis);
                    entr.content = message;
                    db.updateReminderEntry(entr);
                    NotificationScheduler.cancelReminder(getActivity(), ReminderAlarmReceiver.class, (int)id);
                    NotificationScheduler.setReminder(getActivity(), ReminderAlarmReceiver.class, entr.id, entr.calendar.getTimeInMillis(), getString(R.string.ReminderTitle), message);
                }

                updateRemindersList();
            }
        }
    }

    public void addReminderButton(View v)
    {
        Intent intent = new Intent(getActivity(), AddReminderActivity.class);
        intent.putExtra("id", -1L);
        startActivityForResult(intent, REQUEST_CODE_ADD_REMINDER);

    }
}

