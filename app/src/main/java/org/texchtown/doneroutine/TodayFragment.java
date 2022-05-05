package org.texchtown.doneroutine;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;


public class TodayFragment extends Fragment {
    //TAG
    private static final String TAG = "TodayFragment";
    //widget
    RecyclerView recyclerView;

    //var
    HabitDatabaseHelper habitDatabaseHelper;
    ArrayList<String> habit_name, habit_detail;
    ArrayList<Integer> habit_count, habit_color;
    TodayAdapter todayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        recyclerView = view.findViewById(R.id.recycler);

        //getting habit data from db to bind it to a adapter
        habitDatabaseHelper = new HabitDatabaseHelper(getActivity());
        habit_name = new ArrayList<>();
        habit_detail = new ArrayList<>();
        habit_count = new ArrayList<>();
        habit_color = new ArrayList<>();
        storeDataInArray();
        checkDataInArray();

        //write date db once a day;
        initializeDataDatabase();

        //initialize todayAdapter and set Adapter for recycler view
        todayAdapter = new TodayAdapter(getActivity(), habit_name, habit_detail, habit_count, habit_color);
        recyclerView.setAdapter(todayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set swipe action
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(cardViewSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    void storeDataInArray() {
        Cursor cursor = habitDatabaseHelper.readTodayData();
        if(cursor.getCount() == 0) {
            Log.d(TAG, "storeDataInArray: cursor with no data");
        }else{
            while(cursor.moveToNext()) {
                //add status, goal, h_group to make detail text
                String detail = "";
                if(cursor.getInt(1) == 1) {
                    detail = "<" + cursor.getString(3) + "> lets start " + cursor.getString(0)
                            + " for " + cursor.getInt(2) + "days";
                } else {
                    detail = "<" + cursor.getString(3) + "> lets quit " + cursor.getString(0)
                            + " for " + cursor.getInt(2) + "days";
                }

                habit_name.add(cursor.getString(0));
                habit_detail.add(detail);
                habit_count.add(cursor.getInt(5));
                habit_color.add(cursor.getInt(4));
            }
        }
    }

    void checkDataInArray() {
        for(String name: habit_name) {
            Log.d(TAG, "storeDataInArray: "+name);
        }
        for(String detail: habit_detail) {
            Log.d(TAG, "checkDataInArray: "+detail);
        }
        for(int count: habit_count) {
            Log.d(TAG, "checkDataInArray: "+Integer.toString(count));
        }
        for(int color: habit_color) {
            Log.d(TAG, "checkDataInArray: "+Integer.toString(color));
        }
    }

    void initializeDataDatabase() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences setting = getActivity().getSharedPreferences("PREFS", 0);
        int lastDay = setting.getInt("day", 0);

        if(lastDay != currentDay) {
            SharedPreferences.Editor editor = setting.edit();
            editor.putInt("day", currentDay);
            editor.commit();
            //write date data once a day
            for(String habit: habit_name) {
                habitDatabaseHelper.addTodayHabit(habit);
            }
        }
    }

    ItemTouchHelper.SimpleCallback cardViewSwipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction == ItemTouchHelper.LEFT) {
                //get the row of element in a recycler view with is swiped
                int position = viewHolder.getAdapterPosition();
                String name = habit_name.get(position);
                //go to the sub class with selected element data
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.moveToHabitSpecific(name);
            } else {
                Log.d(TAG, "onSwiped: swipe direction error");
            }
        }
    };

}