package com.uzkuraitis.karolis.diaryapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ReminderEntryAdapter extends BaseAdapter{
    ReminderEntry [] m_entries;
    Context context;
    private static LayoutInflater inflater=null;
    public ReminderEntryAdapter(Context mainActivity, ReminderEntry[] entries) {
        // TODO Auto-generated constructor stub
        m_entries = entries;
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return m_entries.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private OnRowClickedListener onRowClickedListener;
    public interface OnRowClickedListener {
        void onRowClicked(ReminderEntry entry);
    }

    public void setOnDatePickedListener(OnRowClickedListener listener) {
        onRowClickedListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(R.layout.reminder_item_list, null);
        TextView dateTV = rowView.findViewById(R.id.dateTextView);
        TextView timeTV = rowView.findViewById(R.id.timeTextView);
        TextView contentTV = rowView.findViewById(R.id.contentTextView);
        ReminderEntry entry = m_entries[position];
        contentTV.setText(entry.content);
        dateTV.setText(String.format("%d-%d-%d", entry.calendar.get(Calendar.YEAR), entry.calendar.get(Calendar.MONTH), entry.calendar.get(Calendar.DAY_OF_MONTH)));
        timeTV.setText(String.format("%d:%d", entry.calendar.get(Calendar.HOUR_OF_DAY), entry.calendar.get(Calendar.MINUTE)));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != onRowClickedListener)
                    onRowClickedListener.onRowClicked(m_entries[position]);
                //Toast.makeText(context, "You Clicked "+m_entries[position].content, Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

} 