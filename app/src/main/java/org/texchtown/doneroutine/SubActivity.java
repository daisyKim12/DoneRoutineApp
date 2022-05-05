package org.texchtown.doneroutine;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;

public class SubActivity extends AppCompatActivity {
    //TAG
    private static final String TAG = "SubActivity";
    //widget
    LinearLayout ll_all, ll_bottom;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView tv_name, tv_goal, tv_count, tv_strike, tv_month_year;
    Button btn_prv, btn_nxt;
    RecyclerView rv_calendar;
    //var
    LocalDate selectedDate;
    String todayYearMonth;
    String name;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        transparentStatusBar();

        Intent intent = getIntent();
        name = intent.getStringExtra("swiped element");

        //set toolbar
        toolbar = findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        //set up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        //bind widget
        initWidget();
        //set habit details
        setWidgetData(name);

        //set on click listener
        btn_prv.setOnClickListener(listener);
        btn_nxt.setOnClickListener(listener);

        //set calendar
        selectedDate = LocalDate.now();
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        todayYearMonth = yearMonthFormat.format(currentDate);

        setCalendar();
    }

    private void setCalendar() {
        tv_month_year.setText(monthYearFromDate(selectedDate));
        ArrayList<String> dayInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(this, dayInMonth
                , todayYearMonth, color, name);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        rv_calendar.setAdapter(calendarAdapter);
        rv_calendar.setLayoutManager(layoutManager);

    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++){
            if(i <=dayOfWeek || i> daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf((i - dayOfWeek)));
            }
        }
        return daysInMonthArray;
    }


    private String monthYearFromDate(LocalDate date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private void setWidgetData(String name){
        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(this);
        Cursor cursor = habitDatabaseHelper.readHabitDetailByName(name);
        if(cursor.getCount() == 0) {
            Log.d(TAG, "setWidgetData: cursor with no data");
        } else {
            while(cursor.moveToNext()) {
                tv_name.setText(name.toUpperCase());
                tv_goal.setText("GOAL: " + Integer.toString(cursor.getInt(0)));
                tv_count.setText("TODAY: " + Integer.toString(cursor.getInt(1)));
                tv_strike.setText("STRIKE: " + Integer.toString(cursor.getInt(2)));

                color = cursor.getInt(3);
                appBarLayout.setBackgroundColor(color);
                toolbar.setBackgroundColor(color);
                ll_all.setBackgroundColor(color);
            }
        }
    }

    public void transparentStatusBar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SubActivity.this, MainActivity.class);
        startActivity(intent);
    }

    View.OnClickListener listener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.btn_previous_month:
                    selectedDate = selectedDate.minusMonths(1);
                    setCalendar();
                    break;
                case R.id.btn_next_month:
                    selectedDate = selectedDate.plusMonths(1);
                    setCalendar();
                    break;
            }
        }
    };

    void initWidget() {
        ll_all = findViewById(R.id.linear_layout);
        ll_bottom = findViewById(R.id.ll_calendar);
        appBarLayout = findViewById(R.id.appbar_layout);
        tv_name = findViewById(R.id.tv_name);
        tv_goal = findViewById(R.id.tv_goal);
        tv_count = findViewById(R.id.tv_count);
        tv_strike = findViewById(R.id.tv_strike);
        btn_prv = findViewById(R.id.btn_previous_month);
        btn_nxt = findViewById(R.id.btn_next_month);
        tv_month_year = findViewById(R.id.tv_month_year);
        rv_calendar = findViewById(R.id.rv_calendar);
    }

}


