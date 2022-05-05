package org.texchtown.doneroutine;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayViewHolder> {
    //TAG
    private static final String TAG = "TodayAdapter";

    //var
    private Context context;
    private ArrayList<String> habit_name, habit_detail;
    private ArrayList<Integer> habit_count, habit_color;

    public TodayAdapter(Context context, ArrayList<String> habit_name, ArrayList<String> habit_detail
            , ArrayList<Integer> habit_count, ArrayList<Integer> habit_color) {
        this.context = context;
        this.habit_name = habit_name;
        this.habit_detail = habit_detail;
        this.habit_count = habit_count;
        this.habit_color = habit_color;
    }

    @NonNull
    @Override
    public TodayAdapter.TodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_habit, parent, false);
        return new TodayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayAdapter.TodayViewHolder holder, int position) {
        String name = habit_name.get(position);
        String detail = habit_detail.get(position);
        int count = habit_count.get(position);
        int color = habit_color.get(position);
        MainActivity mainActivity = (MainActivity)context;

        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(context);
        ArrayList<String> checkedHabit = habitDatabaseHelper.checkTodayHabit();
        if(checkedHabit.contains(name)) {
            holder.cardView_bg.setBackgroundColor(color);
        } else {
            holder.cardView_bg.setBackgroundColor(Color.parseColor("#808080"));
        }
        holder.tv_name.setText(name);
        holder.tv_detail.setText(detail);
        holder.tv_count.setText(Integer.toString(count));
        holder.cardView_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedHabit.contains(name)) {
                    //change color and check today status to true
                    holder.cardView_bg.setBackgroundColor(Color.parseColor("#808080"));
                    habitDatabaseHelper.updateCount(name, count-1);
                    habitDatabaseHelper.updateTodayHabit(name, false);
                    mainActivity.onFragmentChanged(0);
                } else {
                    holder.cardView_bg.setBackgroundColor(color);
                    habitDatabaseHelper.updateCount(name, count+1);
                    habitDatabaseHelper.updateTodayHabit(name, true);
                    mainActivity.onFragmentChanged(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return habit_name.size();
    }

    public class TodayViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout cardView_bg;
        TextView tv_name, tv_detail, tv_count;

        public TodayViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView_bg = itemView.findViewById(R.id.habit_layout);
            tv_name = itemView.findViewById(R.id.habit_name);
            tv_detail = itemView.findViewById(R.id.habit_detail);
            tv_count = itemView.findViewById(R.id.habit_count);
        }
    }
}
