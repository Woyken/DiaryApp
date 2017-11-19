package com.uzkuraitis.karolis.diaryapp;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.uzkuraitis.karolis.diaryapp.DiaryEntriesActivity;
import com.uzkuraitis.karolis.diaryapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DiaryEntryAdapter extends BaseAdapter{
    DiaryEntry [] m_entries;
    Context context;
    private static LayoutInflater inflater=null;
    public DiaryEntryAdapter(DiaryEntriesActivity mainActivity, DiaryEntry[] entries) {
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
        void onRowClicked(DiaryEntry entry);
    }

    public void setOnDatePickedListener(OnRowClickedListener listener) {
        onRowClickedListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(R.layout.diary_item_list, null);
        TextView contentTV = rowView.findViewById(R.id.contentTextView);
        TextView dateTV = rowView.findViewById(R.id.dateTextView);
        contentTV.setText(m_entries[position].content);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String str = formatter.format(new Date(m_entries[position].date.getTimeInMillis()));
        dateTV.setText(str);

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