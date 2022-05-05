package org.texchtown.doneroutine;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    private static final String TAG = "MainActivity";

    // widget
    Toolbar toolbar;
    ActionBar actionBar;
    BottomNavigationView bottomNavigationView;
    Dialog settingDialog, createDialog;
    //var


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //find view by id
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        settingDialog = new Dialog(this);
        createDialog = new Dialog(this);

        //set toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        //set status bar
        transparentStatusBar();

        //set listener for bottom navigation
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_nav_today:
                onFragmentChanged(0);
                break;
            case R.id.main_nav_total:
                onFragmentChanged(1);
                break;
            case R.id.main_nav_list:
                onFragmentChanged(2);
                break;
        }
        return true;
    }

    public void onFragmentChanged(int Index) {
        Fragment todayFragment = new TodayFragment();
        Fragment totalFragment = new TotalFragment();
//        Fragment listFragment = new ListFragment();

        switch (Index) {
            case 0:
                //replace container with today fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_main, todayFragment).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_main, totalFragment).commit();
                break;
            case 2:
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.container_main, listFragment).commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemSelected = item.getItemId();
        switch(itemSelected) {
            case R.id.main_toolbar_setting:
                Log.d(TAG, "onOptionsItemSelected: setting clicked");
                openSetting();
                break;
            case R.id.main_toolbar_add:
                Log.d(TAG, "onOptionsItemSelected: add clicked");
                openAddHabit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSetting() {
        SettingDialog settingDialog = new SettingDialog();
        settingDialog.show(getSupportFragmentManager(), "setting");
    }

    public void openAddHabit() {
        HabitDialog habitDialog = new HabitDialog();
        habitDialog.show(getSupportFragmentManager(), "create new habit");
    }

    public void transparentStatusBar() {
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    public void moveToHabitSpecific(String name) {
        Intent intent = new Intent(MainActivity.this, SubActivity.class);
        intent.putExtra("swiped element", name);
        startActivity(intent);
    }

    public void moveToGroup(String group) {
        //move to fragment 2 to show habits inside a perticular group
    }
}