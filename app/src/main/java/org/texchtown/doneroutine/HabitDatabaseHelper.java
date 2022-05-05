package org.texchtown.doneroutine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HabitDatabaseHelper extends SQLiteOpenHelper {
    //TAG
    private static final String TAG = "HabitDatabaseHelper";
    //database create var
    private static final String DATABASE_NAME = "habit.db";
    private static final int DATABASE_VERSION = 1;
    //new habit table & column name
    private static final String TABLE_HABIT = "tb_habit";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_STATUS = "build_or_quit";
    private static final String COLUMN_GOAL = "goal";
    private static final String COLUMN_MON = "mon";
    private static final String COLUMN_TUE = "tue";
    private static final String COLUMN_WED = "wed";
    private static final String COLUMN_THU = "thu";
    private static final String COLUMN_FRI = "fri";
    private static final String COLUMN_SAT = "sat";
    private static final String COLUMN_SUN = "sun";
    private static final String COLUMN_GROUP = "h_group";
    private static final String COLUMN_COLOR = "color";
    private static final String COLUMN_NOTIFY = "notification";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_CREATE_DATE = "create_date";
    private static final String COLUMN_STRIKE = "strike";
    //keep track of habit table & column name
    private static final String TABLE_DATE = "tb_date";
    private static final String COLUMN_CLICKED_DATE = "clicked_date";
    private static final String COLUMN_FINISHED_NAME = "finished_habit_name";
    private static final String COLUMN_FINISHED_STATUS = "finished_habit_status";

    //query
//    private static final String CREATE_HABIT_QUERY = "create table tb_habit (_id integer primary key autoincrement, name text, build_or_quit boolean" +
//            ", goal integer, mon boolean, tue boolean, wed boolean, thu boolean, fri boolean, sat boolean, sun boolean, h_group text" +
//            ", color integer, notification boolean, count integer, create_date text, strike integer);";
//    private static final String CREATE_DATE_QUERY = "create table tb_date (clicked_date text, finished_habit_name text, finished_habit_status boolean);";
//    private static final String UPDATE_HABIT_QUERY = "drop table tb_habit";
//    private static final String UPDATE_DATE_QUERY = "drop table tb_date";
//    private static final String READ_TODAY_QUERY = "select name, build_or_quit, goal, h_group, color, count from tb_habit";
//    private static final String CHECK_HABIT_STATUS = "select finished_habit_name from tb_date where finished_habit_status=true";


    HabitDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table tb_habit (_id integer primary key autoincrement, name text, build_or_quit boolean" +
                ", goal integer, mon boolean, tue boolean, wed boolean, thu boolean, fri boolean, sat boolean, sun boolean, h_group text" +
                ", color integer, notification boolean, count integer, create_date text, strike integer);");
        sqLiteDatabase.execSQL("create table tb_date (clicked_date text, finished_habit_name text, finished_habit_status boolean);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table tb_habit");
        sqLiteDatabase.execSQL("drop table tb_date");
        onCreate(sqLiteDatabase);
    }

    void addHabit(String name, Boolean status, Integer goal, Boolean mon, Boolean tue, Boolean wed, Boolean thu, Boolean fri
            , Boolean sat, Boolean sun, String group, int color, Boolean notification) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        //use content value objet to pass data groups that we want to save
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_GOAL, goal);
        contentValues.put(COLUMN_MON, mon);
        contentValues.put(COLUMN_TUE, tue);
        contentValues.put(COLUMN_WED, wed);
        contentValues.put(COLUMN_THU, thu);
        contentValues.put(COLUMN_FRI, fri);
        contentValues.put(COLUMN_SAT, sat);
        contentValues.put(COLUMN_SUN, sun);
        contentValues.put(COLUMN_GROUP, group);
        contentValues.put(COLUMN_COLOR, color);
        contentValues.put(COLUMN_NOTIFY, notification);
        contentValues.put(COLUMN_COUNT, 0);
        contentValues.put(COLUMN_CREATE_DATE, todayDate());
        contentValues.put(COLUMN_STRIKE, 0);

        //pass content value to sql database object
        long result = sqLiteDatabase.insert(TABLE_HABIT, null, contentValues);

        if(result == -1){
            Log.d(TAG, "addHabit: save habit data into db failed");
        }
    }

    void addTodayHabit(String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //use content value objet to pass data groups that we want to save
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CLICKED_DATE, todayDate());
        contentValues.put(COLUMN_FINISHED_NAME, name);
        contentValues.put(COLUMN_FINISHED_STATUS, false);
        //pass content value to sql database object
        long result = sqLiteDatabase.insert(TABLE_DATE, null, contentValues);
        if(result == -1){
            Log.d(TAG, "updateDate: save date data into db failed");
        }
    }

    ArrayList<String> checkTodayHabit(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select finished_habit_name from tb_date where finished_habit_status=true" + " and clicked_date=" + todayDate(), null);
        ArrayList<String> checkedHabit = new ArrayList<>();
        if(cursor.getCount() == 0) {
            Log.d(TAG, "checkTodayHabit: cursor with no data");
        } else {
            while(cursor.moveToNext()) {
                checkedHabit.add(cursor.getString(0));
            }
        }
        return checkedHabit;
    }

    void updateTodayHabit(String name, Boolean clickStatus) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FINISHED_STATUS, clickStatus);

        long result = sqLiteDatabase.update(TABLE_DATE, contentValues, "clicked_date=? and finished_habit_name=?"
                , new String[]{todayDate(), name});
        if(result == -1){
            Log.d(TAG, "addHabit: update data failed");
        }
    }


    Cursor readTodayData() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        String todayQuery = "";
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY:
                todayQuery = " where mon=true;";
                break;
            case Calendar.TUESDAY:
                todayQuery = " where tue=true;";
                break;
            case Calendar.WEDNESDAY:
                todayQuery = " where wed=true;";
                break;
            case Calendar.THURSDAY:
                todayQuery = " where thu=true;";
                break;
            case Calendar.FRIDAY:
                todayQuery = " where fri=true;";
                break;
            case Calendar.SATURDAY:
                todayQuery = " where sat=true;";
                break;
            case Calendar.SUNDAY:
                todayQuery = " where sun=true;";
                break;
            default:
                Log.d(TAG, "readTodayData: error at reading day of week");
                break;
        }
        if(sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery("select name, build_or_quit, goal, h_group, color" +
                    ", count from tb_habit" + todayQuery, null);
        }

        return cursor;
    }

    void updateCount(String name, int count) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COUNT, count);

        long result = sqLiteDatabase.update(TABLE_HABIT, contentValues, "name=?", new String[]{name});
        if(result == -1){
            Log.d(TAG, "addHabit: update data failed");
        }
    }
    String todayDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(today);
        Log.d(TAG, "todayDate(current date): "+currentDate);
        return currentDate;
    }

    Cursor readGroupDetailData(String group) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select name, count, goal, color from tb_habit where h_group=?"
                , new String[]{group});

        return cursor;
    }

    ArrayList<String> readAllGroup() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select h_group from tb_habit", null);
        ArrayList<String> group_name = new ArrayList<>();
        if(cursor.getCount() == 0) {
            Log.d(TAG, "readAllGroup: cursor with no data");
        } else {
            while(cursor.moveToNext()) {
                if(!group_name.contains(cursor.getString(0))){
                    group_name.add(cursor.getString(0));
                }
            }
        }
        return group_name;
    }

    Cursor readHabitDetailByName(String name){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select goal, count, strike, color from tb_habit where name=?"
                , new String[]{name});

        return cursor;
    }

    Cursor readDateByName(String name){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select clicked_date from tb_date " +
                        "where finished_habit_name=? and finished_habit_status=1", new String[]{name});

        return cursor;
    }
}
