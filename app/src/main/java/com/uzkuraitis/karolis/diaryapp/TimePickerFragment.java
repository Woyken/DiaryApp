package com.uzkuraitis.karolis.diaryapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    int defaultToHours = -1;
    int defaultToMinutes = -1;

    private OnTimePickedListener onTimePickedListener;
    public interface OnTimePickedListener {
        void onTimePicked(int hourOfDay, int minute);
    }

    public void setOnTimePickedListener(OnTimePickedListener listener) {
        onTimePickedListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = 0;
        int minute = 0;
        if(defaultToHours == -1)
        {
            hour = c.get(Calendar.HOUR_OF_DAY);
        }
        if(defaultToMinutes == -1)
        {
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(onTimePickedListener != null)
            onTimePickedListener.onTimePicked(hourOfDay, minute);
        // Do something with the time chosen by the user
    }
}