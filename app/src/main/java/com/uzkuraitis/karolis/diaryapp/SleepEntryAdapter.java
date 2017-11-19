package com.uzkuraitis.karolis.diaryapp;
import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.uzkuraitis.karolis.diaryapp.DiaryEntriesActivity;
import com.uzkuraitis.karolis.diaryapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SleepEntryAdapter extends BaseAdapter{
    private SleepEntry [] m_entries;
    private static LayoutInflater inflater=null;
    public SleepEntryAdapter(Context mainActivity, SleepEntry[] entries) {
        // TODO Auto-generated constructor stub
        m_entries = entries;
        Context context = mainActivity;
        inflater = ( LayoutInflater ) context.
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(R.layout.sleep_data_list, null);
        TextView dateTV = rowView.findViewById(R.id.dateTextView);
        SleepEntry entr = m_entries[position];
        dateTV.setText(entr.year+"-"+(entr.month+1));

        GraphView graph = rowView.findViewById(R.id.graph);
        List<DataPoint> dataPoints = new ArrayList<>();
        for (Map.Entry<Integer, Float> item : entr.entries.entrySet())
        {
            dataPoints.add(new DataPoint(item.getKey(), item.getValue()));
        }

        DataPoint[] sleepEntryArr = new DataPoint[dataPoints.size()];
        sleepEntryArr = dataPoints.toArray(sleepEntryArr);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(sleepEntryArr);
        //graph.removeAllSeries();
        series.setThickness(8);
        //series.setAnimated(true);
        //series.setDrawAsPath(true);
        series.setDrawDataPoints(true);
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1);
        graph.getViewport().setMaxX(31);
        graph.getViewport().setMaxXAxisSize(1);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);


        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "You Clicked "+m_entries[position].month, Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}