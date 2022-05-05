package org.texchtown.doneroutine;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

public class SettingDialog extends DialogFragment {
    //TAG
    private static final String TAG = "SettingDialog";

    //widget
    Button btn_save;
    RadioGroup rg_sort;
    SwitchCompat swt_mode, swt_ntf;

    //val
    Dialog settingDialog;
    SharedPreferences sharedPreferences;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        settingDialog = new Dialog(getActivity());
        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        settingDialog.setContentView(R.layout.dialog_setting);
        rg_sort = settingDialog.findViewById(R.id.rg_sort);
        swt_mode = settingDialog.findViewById(R.id.swt_setting_bg);
        swt_ntf = settingDialog.findViewById(R.id.swt_setting_ntf);

        //show current setting data
        sharedPreferences = getActivity().getSharedPreferences("SettingPrefs", Context.MODE_PRIVATE);
        //read data from shared preference and initialize widgets
        readSettingData();

        btn_save = settingDialog.findViewById(R.id.setting_save_btn);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save setting change
                saveSettingData();
                settingDialog.dismiss();
            }
        });

        return settingDialog;
    }

    public void readSettingData() {
        int sort = sharedPreferences.getInt("sort", -1);
        Boolean mode = sharedPreferences.getBoolean("mode", false);
        Boolean notification = sharedPreferences.getBoolean("notification", false);

        rg_sort.check(sort);
        swt_mode.setChecked(mode);
        swt_ntf.setChecked(notification);
    }

    public void saveSettingData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int sort = rg_sort.getCheckedRadioButtonId();
        Boolean mode = swt_mode.isChecked();
        Boolean notification = swt_ntf.isChecked();
        editor.putInt("sort", sort);
        editor.putBoolean("mode", mode);
        editor.putBoolean("notification", notification);
        editor.commit();

        checkSettingData(sort, mode, notification);
    }

    private void checkSettingData(int sort, Boolean mode, Boolean ntf) {
        Log.d(TAG, "checkSettingData(sort): " + sort);
        Log.d(TAG, "checkSettingData(mode): " + mode);
        Log.d(TAG, "checkSettingData(ntf): " + ntf);
    }
}
