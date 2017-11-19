package com.uzkuraitis.karolis.diaryapp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

public class DiaryCalendarFragment extends Fragment
{
    CalendarView calendarView;
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public DiaryCalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.DiaryEntriesActivityTitle));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //Toast.makeText(getApplicationContext(), ""+dayOfMonth, 0).show();// TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), DiaryEntriesActivity.class);
                int[] i = {year, month, dayOfMonth};
                intent.putExtra(EXTRA_MESSAGE, i);
                startActivity(intent);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(null != container)
            container.removeAllViews();
        View rootView = inflater.inflate(R.layout.content_main_calendar, container, false);
        calendarView = rootView.findViewById(R.id.calendarView);
        return rootView;
    }

}
