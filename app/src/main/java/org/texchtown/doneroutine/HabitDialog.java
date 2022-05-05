package org.texchtown.doneroutine;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class HabitDialog extends DialogFragment {
    //TAG
    private static final String TAG = "HabitDialog";
    //widget
    EditText et_name, et_goal, et_group;
    TextView tv_color;
    RadioGroup rg_status, rg_color;
    RadioButton rb_build, rb_quit;
    CheckBox cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat, cb_sun;
    RadioButton rb_color_0, rb_color_1, rb_color_2, rb_color_3, rb_color_4
            , rb_color_5, rb_color_6, rb_color_7;
    SwitchCompat swt_ntf;
    Button btn_save, btn_cancel;

    //val
    Dialog createDialog;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        createDialog = new Dialog(getActivity());
        createDialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
        createDialog.setContentView(R.layout.dialog_add_habit);

        //bind widget to view
        bindWidget();

        //add text watcher onto edit text and button
        et_name.addTextChangedListener(editTextWatcher);
        et_goal.addTextChangedListener(editTextWatcher);
        et_group.addTextChangedListener(editTextWatcher);
        rg_color.setOnCheckedChangeListener(radioButtonWatcher);
        rg_status.setOnCheckedChangeListener(radioButtonWatcher);
        checkboxListener();

        //set on click listener
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save data into Habit table
                saveNewHabitData();
                //save data into Date table
                updateCurrentHabit();

                //add habit to recycler view
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.onFragmentChanged(0);
                //dismiss dialog
                createDialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog.dismiss();
            }
        });
        return createDialog;
    }

    private ArrayList<Integer> getCheckedDays() {
        ArrayList<Integer> selectedDays = new ArrayList<>();
        CheckBox[] days = {cb_sun, cb_mon, cb_tue, cb_wed, cb_thu, cb_fri, cb_sat};
        for(int i = 0; i < 7; i++) {
            if(days[i].isChecked()) {
                selectedDays.add(i+1);
            }
        }
        return selectedDays;
    }

    private void enableSaveButton() {
        //charSequence is text input by three editText view
        //so need to get input directly from the view
        String habitNameInput = et_name.getText().toString().trim();
        String habitGoalInput = et_goal.getText().toString().trim();
        String habitGroupInput = et_group.getText().toString().trim();
        //find with button is checked
        int StatusId = rg_status.getCheckedRadioButtonId();
        int ColorId = rg_color.getCheckedRadioButtonId();
        ArrayList<Integer> DaysId = getCheckedDays();

        Boolean editTextFilled, buttonChecked;

        editTextFilled = !habitNameInput.isEmpty() && !habitGoalInput.isEmpty()
                && !habitGroupInput.isEmpty();
        if(disableColorButton()){
            buttonChecked = (StatusId != -1) && !DaysId.isEmpty();
        } else {
            buttonChecked = (StatusId != -1) && (ColorId != -1) && !DaysId.isEmpty();
        }
        btn_save.setEnabled( buttonChecked && editTextFilled);
    }

    private void saveNewHabitData() {
        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(getActivity());
        //get data from the view
        String name, group ;
        int goal, color;
        Boolean mon, tue, wed, thu, fri, sat, sun, status, notification;
        //get edit text data
        name = et_name.getText().toString().trim();
        goal = Integer.parseInt(et_goal.getText().toString().trim());
        group = et_group.getText().toString().trim();
        //get build quit data (build == true, quit == false)
        if(rg_status.getCheckedRadioButtonId() == R.id.radio_build) {
            status = true;
        }else {
            status = false;
        }

        //set group color if group color already exists
        color = -1;
        if(disableColorButton()){
            Cursor cursor = habitDatabaseHelper.readGroupDetailData(et_group.getText().toString());
            while(cursor.moveToNext()) { color = cursor.getInt(3); }
        } else {
            //get color data
            switch (rg_color.getCheckedRadioButtonId()) {
                case R.id.color_0:
                    color = Color.parseColor("#F44336");
                    break;
                case R.id.color_1:
                    color = Color.parseColor("#E91E63");
                    break;
                case R.id.color_2:
                    color = Color.parseColor("#9C27B0");
                    break;
                case R.id.color_3:
                    color = Color.parseColor("#3F51B5");
                    break;
                case R.id.color_4:
                    color = Color.parseColor("#03A9F4");
                    break;
                case R.id.color_5:
                    color = Color.parseColor("#009688");
                    break;
                case R.id.color_6:
                    color = Color.parseColor("#8BC34A");
                    break;
                case R.id.color_7:
                    color = Color.parseColor("#FFC107");
                    break;
                default:
                    color = -1;
                    Log.d(TAG, "saveNewHabitData: color data saving failed");
                    break;
            }
        }
        //get days data
        mon = tue = wed = thu = fri = sat = sun = false;
        ArrayList<Integer> DaysId = getCheckedDays();
        for(Integer chosenDay: DaysId) {
            switch (chosenDay) {
                case 1: sun = true;
                    break;
                case 2: mon = true;
                    break;
                case 3: tue = true;
                    break;
                case 4: wed = true;
                    break;
                case 5: thu = true;
                    break;
                case 6: fri = true;
                    break;
                case 7: sat = true;
                    break;
            }
        }
        //get notification data
        notification = swt_ntf.isChecked();

        //pass data to data base helper
        habitDatabaseHelper.addHabit(name, status, goal, mon, tue, wed, thu, fri, sat, sun
                , group, color, notification);
    }

    private void updateCurrentHabit() {
        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(getActivity());
        String name = et_name.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        ArrayList<Integer> DaysId = getCheckedDays();
        if(DaysId.contains(currentDay)) {
            habitDatabaseHelper.addTodayHabit(name);
        }
    }

    private Boolean disableColorButton() {
        HabitDatabaseHelper habitDatabaseHelper = new HabitDatabaseHelper(getActivity());
        ArrayList<String> currentGroups = habitDatabaseHelper.readAllGroup();
        if(currentGroups.contains(et_group.getText().toString())) {
            tv_color.setText("group color exists");
            tv_color.setTextColor(Color.parseColor("red"));
            for(int i = 0; i < rg_color.getChildCount(); i++) {
                rg_color.getChildAt(i).setEnabled(false);
            }
            return true;
        } else {
            tv_color.setText("group color");
            tv_color.setTextColor(Color.parseColor("#B1B1B1"));
            for(int i = 0; i < rg_color.getChildCount(); i++) {
                rg_color.getChildAt(i).setEnabled(true);
            }
            return false;
        }
    }

    public void bindWidget() {
        tv_color = createDialog.findViewById(R.id.tv_color);

        et_name = createDialog.findViewById(R.id.edit_name);
        et_goal = createDialog.findViewById(R.id.edit_goal);
        et_group = createDialog.findViewById(R.id.edit_group);

        rg_status = createDialog.findViewById(R.id.radio_group_status);
        rg_color = createDialog.findViewById(R.id.radio_group_color);

        rb_build = createDialog.findViewById(R.id.radio_build);
        rb_quit = createDialog.findViewById(R.id.radio_quit);

        cb_mon = createDialog.findViewById(R.id.check_mon);
        cb_tue = createDialog.findViewById(R.id.check_tue);
        cb_wed = createDialog.findViewById(R.id.check_wed);
        cb_thu = createDialog.findViewById(R.id.check_thu);
        cb_fri = createDialog.findViewById(R.id.check_fri);
        cb_sat = createDialog.findViewById(R.id.check_sat);
        cb_sun = createDialog.findViewById(R.id.check_sun);

        rb_color_0 = createDialog.findViewById(R.id.color_0);
        rb_color_1 = createDialog.findViewById(R.id.color_1);
        rb_color_2 = createDialog.findViewById(R.id.color_2);
        rb_color_3 = createDialog.findViewById(R.id.color_3);
        rb_color_4 = createDialog.findViewById(R.id.color_4);
        rb_color_5 = createDialog.findViewById(R.id.color_5);
        rb_color_6 = createDialog.findViewById(R.id.color_6);
        rb_color_7 = createDialog.findViewById(R.id.color_7);

        swt_ntf = createDialog.findViewById(R.id.swt_ntf);

        btn_save = createDialog.findViewById(R.id.create_save_btn);
        btn_cancel = createDialog.findViewById(R.id.create_cancel_btn);
    }

    private TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            enableSaveButton();
        }
        @Override
        public void afterTextChanged(Editable editable) {
            //disable color radio group if entered group already exists
            disableColorButton();
        }
    };

    private View.OnClickListener checkboxWatcher = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            enableSaveButton();
        }
    };

    private RadioGroup.OnCheckedChangeListener radioButtonWatcher = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            enableSaveButton();
        }
    };

    private void checkboxListener() {
        cb_mon.setOnClickListener(checkboxWatcher);
        cb_tue.setOnClickListener(checkboxWatcher);
        cb_wed.setOnClickListener(checkboxWatcher);
        cb_thu.setOnClickListener(checkboxWatcher);
        cb_fri.setOnClickListener(checkboxWatcher);
        cb_sat.setOnClickListener(checkboxWatcher);
        cb_sun.setOnClickListener(checkboxWatcher);
    }
}
