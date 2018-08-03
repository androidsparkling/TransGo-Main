package com.ByteCrunchers.TransGo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    private CheckBox checkBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addListenerOnChkIos();

    }

    public void addListenerOnChkIos() {

        checkBox = (CheckBox) findViewById(R.id.translationCheckBox);

        checkBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    startService(new Intent(SettingsActivity.this, ClipboardMonitorService.class));
                    Toast.makeText(SettingsActivity.this,
                            "Translation service started.", Toast.LENGTH_LONG).show();
                }
                else if (!((CheckBox) v).isChecked()) {
                    stopService(new Intent(SettingsActivity.this, ClipboardMonitorService.class));
                    Toast.makeText(SettingsActivity.this,
                            "Translation service stopped.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }



}