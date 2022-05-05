package org.texchtown.doneroutine;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class TotalFragment extends Fragment {
    //TAG
    private static final String TAG = "GroupFragment";
    //widget
    RecyclerView recyclerView;

    //var
    HabitDatabaseHelper habitDatabaseHelper;
    ArrayList<String> group_name, group_detail, group_percentage;
    ArrayList<Integer> group_color;
    TotalAdapter totalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total, container, false);
        recyclerView = view.findViewById(R.id.recycler);

        //getting group data from db to bind it to a adapter
        habitDatabaseHelper = new HabitDatabaseHelper(getActivity());
        group_name = new ArrayList<>();
        group_detail = new ArrayList<>();
        group_percentage = new ArrayList<>();
        group_color = new ArrayList<>();
        storeAllGroupInArray();
        storeGroupDataInArray();

        //initialize totalAdapter and set Adapter for recycler view
        totalAdapter = new TotalAdapter(getActivity(), group_name, group_detail, group_percentage, group_color);
        recyclerView.setAdapter(totalAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        return view;
    }

    private void storeAllGroupInArray() {
        group_name = habitDatabaseHelper.readAllGroup();
    }

    void storeGroupDataInArray() {
        for (String group : group_name) {
            String habit_detail = "";
            int total_count = 0, total_goal = 1;
            Cursor cursor = habitDatabaseHelper.readGroupDetailData(group);
            if (cursor.getCount() == 0) {
                Log.d(TAG, "storeGroupDataInArray: cursor with no data");
            } else {
                while (cursor.moveToNext()) {
                    habit_detail = habit_detail + cursor.getString(0) + " " + Integer.toString(cursor.getInt(1))
                            + "/" + Integer.toString(cursor.getInt(2)) + "\n";
                    total_count = total_count + cursor.getInt(1);
                    total_goal = total_goal + cursor.getInt(2);
                    if(!group_color.contains(cursor.getInt(3))){
                        group_color.add(cursor.getInt(3));
                    }
                }
            }
            group_detail.add(habit_detail);
            group_percentage.add(Integer.toString(total_count / total_goal * 100) + "%");
        }
    }
}
