package com.uzkuraitis.karolis.diaryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by karol on 11/15/2017.
 */

public class MySQLHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "DiaryAppDB";

    public MySQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String TABLE_REMINDER_ENTRY = "reminder_entry";
    private static final String REMINDER_ENTRY_KEY_ID = "id";
    private static final String REMINDER_ENTRY_KEY_CALENDAR = "calendar";
    private static final String REMINDER_ENTRY_KEY_CONTENT = "content";
    private static final String[] REMINDER_ENTRY_COLUMNS = {REMINDER_ENTRY_KEY_ID, REMINDER_ENTRY_KEY_CALENDAR, REMINDER_ENTRY_KEY_CONTENT};

    private static final String TABLE_DIARY_ENTRY = "diary_entry";
    private static final String DIARY_ENTRY_KEY_ID = "id";
    private static final String DIARY_ENTRY_KEY_CALENDAR = "calendar";
    private static final String DIARY_ENTRY_KEY_CONTENT = "content";
    private static final String[] DIARY_ENTRY_COLUMNS = {DIARY_ENTRY_KEY_ID, DIARY_ENTRY_KEY_CALENDAR, DIARY_ENTRY_KEY_CONTENT};

    private static final String TABLE_SLEEP_ENTRY = "sleep_entry";
    private static final String SLEEP_ENTRY_KEY_CALENDAR = "calendar";
    private static final String SLEEP_ENTRY_KEY_VALUE = "value";
    private static final String[] SLEEP_ENTRY_COLUMNS = {SLEEP_ENTRY_KEY_CALENDAR, SLEEP_ENTRY_KEY_VALUE};

    /*
    private static final String TABLE_SLEEP_ENTRY = "sleep_entry_monthly";
    private static final String SLEEP_ENTRY_KEY_YEAR = "year";
    private static final String SLEEP_ENTRY_KEY_MONTH = "month";
    private static final String SLEEP_ENTRY_KEY_CONTENT = "content";
    private static final String[] SLEEP_ENTRY_COLUMNS = {SLEEP_ENTRY_KEY_YEAR, SLEEP_ENTRY_KEY_MONTH, SLEEP_ENTRY_KEY_CONTENT};*/

    /*private static final String TABLE_SLEEP_ENTRY_MDATA = "sleep_entry";
    private static final String SLEEP_ENTRY_MDATA_KEY_ID = "id";
    private static final String SLEEP_ENTRY_MDATA_KEY_DAY = "day";
    private static final String SLEEP_ENTRY_MDATA_KEY_VALUE = "value";
    private static final String[] SLEEP_ENTRY_MDATA_COLUMNS = {SLEEP_ENTRY_MDATA_KEY_ID, SLEEP_ENTRY_MDATA_KEY_DAY, SLEEP_ENTRY_MDATA_KEY_VALUE};*/

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String[] sqlCommands =
                {
                        "CREATE TABLE IF NOT EXISTS " + TABLE_REMINDER_ENTRY + " ( " +
                                REMINDER_ENTRY_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                REMINDER_ENTRY_KEY_CALENDAR + " INTEGER, "+
                                REMINDER_ENTRY_KEY_CONTENT + " TEXT " +
                                ");",

                        "CREATE TABLE IF NOT EXISTS " + TABLE_DIARY_ENTRY + " ( " +
                                DIARY_ENTRY_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                DIARY_ENTRY_KEY_CALENDAR + " INTEGER, "+
                                DIARY_ENTRY_KEY_CONTENT + " TEXT " +
                                ");",

                        "CREATE TABLE IF NOT EXISTS " + TABLE_SLEEP_ENTRY + " ( " +
                                SLEEP_ENTRY_KEY_CALENDAR + " INTEGER PRIMARY KEY, " +
                                SLEEP_ENTRY_KEY_VALUE + " REAL " +
                                ");"
                };
        for(String sql : sqlCommands)
        {
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] sqlCommands =
                {
                        "DROP TABLE IF EXISTS "+ TABLE_REMINDER_ENTRY,

                        "DROP TABLE IF EXISTS " + TABLE_DIARY_ENTRY,

                        "DROP TABLE IF EXISTS " + TABLE_SLEEP_ENTRY
                };

        for(String sql : sqlCommands)
        {
            db.execSQL(sql);
        }

        this.onCreate(db);
    }
    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) + get all + delete all
     */

    /**********************************************************************************************
     *
     **********************************************************************************************/

    public void addReminderEntry(ReminderEntry reminderEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(REMINDER_ENTRY_KEY_CALENDAR, reminderEntry.calendar.getTimeInMillis());
        values.put(REMINDER_ENTRY_KEY_CONTENT, reminderEntry.content);

        db.insert(TABLE_REMINDER_ENTRY,
                null,
                values);

        db.close();
    }

    public ReminderEntry getReminderEntry(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_REMINDER_ENTRY, // a. table
                        REMINDER_ENTRY_COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        ReminderEntry reminderEntry = new ReminderEntry();
        reminderEntry.id = Long.parseLong(cursor.getString(0));
        reminderEntry.calendar = new GregorianCalendar(0,0,0,0,0,0);
        reminderEntry.calendar.setTimeInMillis(Long.parseLong(cursor.getString(1)));
        reminderEntry.content = cursor.getString(2);

        if(!cursor.isClosed())
            cursor.close();
        db.close();

        return reminderEntry;
    }

    public List<ReminderEntry> getAllReminderEntries()
    {
        List<ReminderEntry> reminderEntriesList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_REMINDER_ENTRY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ReminderEntry reminderEntry = null;
        if (cursor.moveToFirst()) {
            do {
                reminderEntry = new ReminderEntry();
                reminderEntry.id = Long.parseLong(cursor.getString(0));
                reminderEntry.calendar = new GregorianCalendar(0,0,0,0,0,0);
                reminderEntry.calendar.setTimeInMillis(Long.parseLong(cursor.getString(1)));
                reminderEntry.content = cursor.getString(2);

                reminderEntriesList.add(reminderEntry);
            } while (cursor.moveToNext());
        }
        if(!cursor.isClosed())
            cursor.close();
        db.close();

        return reminderEntriesList;
    }

    public int updateReminderEntry(ReminderEntry reminderEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(REMINDER_ENTRY_KEY_CALENDAR, reminderEntry.calendar.getTimeInMillis());
        values.put(REMINDER_ENTRY_KEY_CONTENT, reminderEntry.content);

        int i = db.update(TABLE_REMINDER_ENTRY,
                values,
                REMINDER_ENTRY_KEY_ID +" = ?",
                new String[] { String.valueOf(reminderEntry.id) });

        db.close();

        return i;

    }

    public void deleteReminderEntry(ReminderEntry reminderEntry)
    {
        deleteReminderEntry(reminderEntry.id);
    }

    public void deleteReminderEntry(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REMINDER_ENTRY,
                REMINDER_ENTRY_KEY_ID +" = ?",
                new String[] { String.valueOf(id) });

        db.close();
    }

    /**********************************************************************************************
     *
     **********************************************************************************************/

    public void addDiaryEntry(DiaryEntry diaryEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DIARY_ENTRY_KEY_CALENDAR, diaryEntry.date.getTimeInMillis());
        values.put(DIARY_ENTRY_KEY_CONTENT, diaryEntry.content);

        db.insert(TABLE_DIARY_ENTRY,
                null,
                values);

        db.close();
    }

    public DiaryEntry getDiaryEntry(long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_DIARY_ENTRY, // a. table
                        DIARY_ENTRY_COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.id = Long.parseLong(cursor.getString(0));
        diaryEntry.date = new GregorianCalendar(0,0,0,0,0,0);
        diaryEntry.date.setTimeInMillis(Long.parseLong(cursor.getString(1)));
        diaryEntry.content = cursor.getString(2);

        if(!cursor.isClosed())
            cursor.close();
        db.close();

        return diaryEntry;
    }

    public List<DiaryEntry> getAllDiaryEntries()
    {
        List<DiaryEntry> diaryEntriesList = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_DIARY_ENTRY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        DiaryEntry diaryEntry = null;
        if (cursor.moveToFirst()) {
            do {
                diaryEntry = new DiaryEntry();
                diaryEntry.id = Long.parseLong(cursor.getString(0));
                diaryEntry.date = new GregorianCalendar(0,0,0,0,0,0);
                diaryEntry.date.setTimeInMillis(Long.parseLong(cursor.getString(1)));
                diaryEntry.content = cursor.getString(2);

                diaryEntriesList.add(diaryEntry);
            } while (cursor.moveToNext());
        }
        if(!cursor.isClosed())
            cursor.close();
        db.close();

        return diaryEntriesList;
    }

    public int updateDiaryEntry(DiaryEntry diaryEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DIARY_ENTRY_KEY_CALENDAR, diaryEntry.date.getTimeInMillis());
        values.put(DIARY_ENTRY_KEY_CONTENT, diaryEntry.content);

        int i = db.update(TABLE_DIARY_ENTRY,
                values,
                DIARY_ENTRY_KEY_ID +" = ?",
                new String[] { String.valueOf(diaryEntry.id) });

        db.close();

        return i;

    }

    public void deleteDiaryEntry(DiaryEntry diaryEntry)
    {
        deleteDiaryEntry(diaryEntry.id);
    }

    public void deleteDiaryEntry(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DIARY_ENTRY,
                DIARY_ENTRY_KEY_ID +" = ?",
                new String[] { String.valueOf(id) });

        db.close();
    }

    /**********************************************************************************************
     *
     **********************************************************************************************/

    public void addSleepEntry(SleepEntry sleepEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;
        for (Map.Entry<Integer, Float> entrS : sleepEntry.entries.entrySet())
        {
            values= new ContentValues();
            Date date = new Date();
            Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
            calendar.setTime(date);
            calendar.set(sleepEntry.year, sleepEntry.month, entrS.getKey());
            values.put(SLEEP_ENTRY_KEY_CALENDAR, calendar.getTimeInMillis());
            values.put(SLEEP_ENTRY_KEY_VALUE, entrS.getValue());

            db.insert(TABLE_SLEEP_ENTRY,
                    null,
                    values);
        }

        db.close();
    }

    public void addSleepEntry(int year, int month, Integer day, Float amount)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
        calendar.set(year, month, day);
        values.put(SLEEP_ENTRY_KEY_CALENDAR, calendar.getTimeInMillis());
        values.put(SLEEP_ENTRY_KEY_VALUE, amount);

        long test = db.insert(TABLE_SLEEP_ENTRY,
                null,
                values);

        db.close();
    }

    public SleepEntry getSleepEntry(int year, int month)
    {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
        calendar.setTime(date);
        calendar.set(year, month, 0);

        Calendar calendarNextMonth = calendar;
        calendarNextMonth.add(Calendar.MONTH, 1);

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =
                db.query(TABLE_SLEEP_ENTRY, // a. table
                        SLEEP_ENTRY_COLUMNS, // b. column names
                        SLEEP_ENTRY_KEY_CALENDAR + " >= ? AND " + SLEEP_ENTRY_KEY_CALENDAR + " < ?", // c. selections
                        new String[] { String.valueOf(calendar.getTimeInMillis()), String.valueOf(calendarNextMonth.getTimeInMillis()) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        SleepEntry sleepEntry = null;

        if (cursor != null)
        {
            if (cursor.moveToFirst())
            {
                sleepEntry = new SleepEntry();
                Map<Integer, Float> entriesMap = new HashMap<>();
                do
                {
                    calendar = new GregorianCalendar(0,0,0,0,0,0);
                    calendar.setTimeInMillis(Long.parseLong(cursor.getString(0)));
                    sleepEntry.year = calendar.get(Calendar.YEAR);
                    sleepEntry.month = calendar.get(Calendar.MONTH);
                    entriesMap.put(calendar.get(Calendar.DAY_OF_MONTH), Float.parseFloat(cursor.getString(1)));

                } while (cursor.moveToNext());
                sleepEntry.entries = entriesMap;
            }
            if(!cursor.isClosed())
                cursor.close();
        }

        db.close();

        return sleepEntry;
    }

    public List<SleepEntry> getAllSleepEntries()
    {
        List<SleepEntry> sleepEntriesList = new LinkedList<>();
        Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);

        String query = "SELECT  * FROM " + TABLE_SLEEP_ENTRY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        SleepEntry sleepEntry = null;
        Calendar oldCalendar = null;
        if (cursor.moveToFirst())
        {
            do
            {
                calendar.setTimeInMillis(Long.parseLong(cursor.getString(0)));
                int oldYear = -1;
                int oldMonth = -1;
                if(null != oldCalendar)
                {
                    oldYear = oldCalendar.get(Calendar.YEAR);
                    oldMonth = oldCalendar.get(Calendar.MONTH);
                }

                int newYear = calendar.get(Calendar.YEAR);
                int newMonth = calendar.get(Calendar.MONTH);
                if(null == sleepEntry || oldYear != newYear || oldMonth != newMonth)
                {
                    if(null != sleepEntry)
                        sleepEntriesList.add(sleepEntry);
                    sleepEntry = new SleepEntry();
                    sleepEntry.year = newYear;
                    sleepEntry.month = newMonth;
                    sleepEntry.entries = new TreeMap<>();
                }
                sleepEntry.entries.put(calendar.get(Calendar.DAY_OF_MONTH), Float.parseFloat(cursor.getString(1)));
                oldCalendar = ((Calendar) calendar.clone());
            } while (cursor.moveToNext());
            if(null != sleepEntry)
                sleepEntriesList.add(sleepEntry);
        }
        if(!cursor.isClosed())
            cursor.close();
        db.close();

        return sleepEntriesList;
    }

    public int updateSleepEntry(int year, int month, Integer day, Float amount)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
        calendar.set(year, month, day);
        ContentValues values = new ContentValues();

        values.put(SLEEP_ENTRY_KEY_VALUE, amount);

        int i = db.update(TABLE_SLEEP_ENTRY,
                values,
                SLEEP_ENTRY_KEY_CALENDAR + " = ?",
                new String[] { String.valueOf(calendar.getTimeInMillis()) });

        db.close();

        return i;
    }

    public int updateSleepEntry(SleepEntry sleepEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values;
        int i = 0;
        for (Map.Entry<Integer, Float> entrS : sleepEntry.entries.entrySet())
        {
            values = new ContentValues();
            Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
            calendar.set(sleepEntry.year, sleepEntry.month, entrS.getKey());
            values.put(SLEEP_ENTRY_KEY_VALUE, entrS.getValue());
            i += db.update(TABLE_SLEEP_ENTRY,
                    values,
                    SLEEP_ENTRY_KEY_CALENDAR + " = ?",
                    new String[] { String.valueOf(calendar.getTimeInMillis()) });
        }
        db.close();

        return i;
    }

    public void deleteSleepEntry(SleepEntry sleepEntry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar calendar = new GregorianCalendar(0,0,0,0,0,0);
        calendar.set(sleepEntry.year, sleepEntry.month, 0);

        Calendar calendarNextMonth = calendar;
        calendarNextMonth.add(Calendar.MONTH, 1);

        db.delete(TABLE_SLEEP_ENTRY,
                SLEEP_ENTRY_KEY_CALENDAR + " >= ? AND " + SLEEP_ENTRY_KEY_CALENDAR + " < ?",
                new String[] { String.valueOf(calendar.getTimeInMillis()), String.valueOf(calendarNextMonth.getTimeInMillis()) });

        db.close();
    }

}
