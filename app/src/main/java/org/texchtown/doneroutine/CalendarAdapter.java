package org.texchtown.doneroutine;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    //TAG
    private static final String TAG = "CalendarAdapter";
    //var
    Context context;
    ArrayList<String> dayInMonth;
    String yearMonth;
    String name;
    int color;
    ArrayList<Integer> dayInMonthInteger;
    ArrayList<Integer> dateCheckedInteger;

    public CalendarAdapter(Context context, ArrayList<String> dayInMonth, String yearMonth, int color, String name) {
        this.context = context;
        this.dayInMonth = dayInMonth;
        this.yearMonth = yearMonth;
        this.color = color;
        this.name = name;

        dayInMonthInteger = new ArrayList<>();
        dateCheckedInteger = new ArrayList<>();

        for(String date: dayInMonth) {
            if(date == ""){
                dayInMonthInteger.add(-1);
            } else {
                dayInMonthInteger.add(Integer.parseInt(date));
            }
        }

        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(context);
        Cursor cursor = habitDatabaseHelper.readDateByName(name);
        if(cursor.getCount() == 0) {
            Log.d(TAG, "habitIsDone: cursor with no data");
        } else {
            while(cursor.moveToNext()){
                Boolean test = (Integer.parseInt(cursor.getString(0).substring(0,6)) == Integer.parseInt(yearMonth));

                if(Integer.parseInt(cursor.getString(0).substring(0,6)) == Integer.parseInt(yearMonth)) {
                    String date = cursor.getString(0).substring(6);
                    dateCheckedInteger.add(Integer.parseInt(date));
                }
            }
        }
    }

    @NonNull
    @Override
    public CalendarAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cardview_calendar, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.CalendarViewHolder holder, int position) {
        holder.tv_day.setText(dayInMonth.get(position));
        if(habitIsDone(position)) {
            holder.cl_calendar.setBackgroundColor(color);
            Log.d(TAG, "onBindViewHolder: background color set");
        }
    }

    private boolean habitIsDone(int position) {
        if(dateCheckedInteger.contains(dayInMonthInteger.get(position))){
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return dayInMonth.size();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl_calendar;
        TextView tv_day;
//        ImageView iv_check;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cl_calendar = itemView.findViewById(R.id.cl_calender);
            tv_day = itemView.findViewById(R.id.tv_day);
//            iv_check = itemView.findViewById(R.id.iv_check);
        }
    }
}
