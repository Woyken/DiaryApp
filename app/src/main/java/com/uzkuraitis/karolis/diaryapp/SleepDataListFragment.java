package com.uzkuraitis.karolis.diaryapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by karol on 11/19/2017.
 */

public class SleepDataListFragment extends Fragment
{
    ListView lv;
    FloatingActionButton fab;
    public static List<SleepEntry> sleepDataEntries = new ArrayList<>();

    public SleepDataListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(null != container)
            container.removeAllViews();
        View rootView = inflater.inflate(R.layout.activity_sleep_data, container, false);
        lv = rootView.findViewById(R.id.listView1);
        fab = rootView.findViewById(R.id.floatingActionBtnAddSleepData);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.SleepDataActivityTitle));
        MySQLHelper db = new MySQLHelper(getActivity());
        sleepDataEntries = db.getAllSleepEntries();
        db.close();
        SleepEntry[] sleepEntryArr = new SleepEntry[sleepDataEntries.size()];
        sleepEntryArr = sleepDataEntries.toArray(sleepEntryArr);
        lv.setAdapter(new SleepEntryAdapter(getActivity(), sleepEntryArr));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSleepDataButton(v);
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    public float getSleepAmountForDay(int year, int month, int day)
    {
        for (SleepEntry entr : sleepDataEntries)
        {
            if(year == entr.year && month == entr.month)
            {
                for (Map.Entry<Integer, Float> entrS : entr.entries.entrySet())
                {
                    if(day == entrS.getKey())
                        return entrS.getValue();
                }
            }
        }
        return 0;
    }

    private void fillEntries(int year, int month, int day, float amount){
        boolean found = false;
        SleepEntry monthEntry = null;
        for (SleepEntry entr : sleepDataEntries)
        {
            if(year == entr.year && month == entr.month)
            {
                found = true;
                monthEntry = entr;
            }
        }
        if(!found)
        {
            monthEntry = new SleepEntry();
            monthEntry.year = year;
            monthEntry.month = month;
            monthEntry.entries = new TreeMap<>();
        }
        monthEntry.entries.put(day, amount);
        MySQLHelper db = new MySQLHelper(getActivity());
        int updatedRows = db.updateSleepEntry(year, month, day, amount);
        if(updatedRows < 1)
            db.addSleepEntry(year, month, day, amount);
        db.close();

        SleepEntry[] sleepEntryArr = new SleepEntry[sleepDataEntries.size()];
        sleepEntryArr = sleepDataEntries.toArray(sleepEntryArr);
        lv.setAdapter(new SleepEntryAdapter(getActivity(), sleepEntryArr));
    }



    public void addSleepDataButton(View v)
    {

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "timePicker");
        datePickerFragment.setOnDatePickedListener(new DatePickerFragment.OnDatePickedListener() {
            @Override
            public void onDatePicked(final int year, final int month, final int day)
            {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                float amount = getSleepAmountForDay(year, month, day);
                timePickerFragment.defaultToHours = (int)Math.floor(amount);
                timePickerFragment.defaultToMinutes = (int)((amount - timePickerFragment.defaultToHours)*60);

                timePickerFragment.show(getFragmentManager(), "timePicker");
                timePickerFragment.setOnTimePickedListener(new TimePickerFragment.OnTimePickedListener() {
                    @Override
                    public void onTimePicked(int hourOfDay, int minute)
                    {
                        fillEntries(year, month, day, hourOfDay + minute / 60);
                    }
                });
            }
        });


    }
}
