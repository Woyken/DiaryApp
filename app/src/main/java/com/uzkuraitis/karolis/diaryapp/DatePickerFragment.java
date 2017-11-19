package com.uzkuraitis.karolis.diaryapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnDatePickedListener onDatePickedListener;
    public interface OnDatePickedListener {
        void onDatePicked(int year, int month, int day);
    }

    public void setOnDatePickedListener(OnDatePickedListener listener) {
        onDatePickedListener = listener;
    }
    private Calendar defaultDate = null;
    public void setDefaultDate(int year, int month, int day)
    {
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        defaultDate = c;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year, month, day;
        if(null != defaultDate)
        {
            year = defaultDate.get(Calendar.YEAR);
            month = defaultDate.get(Calendar.MONTH);
            day = defaultDate.get(Calendar.DAY_OF_MONTH);
        }
        else
        {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(onDatePickedListener != null)
            onDatePickedListener.onDatePicked(year, month, day);
        // Do something with the date chosen by the user
    }
}