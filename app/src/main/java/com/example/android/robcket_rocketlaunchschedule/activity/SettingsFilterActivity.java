package com.example.android.robcket_rocketlaunchschedule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.fragment.SettingsFilterFragment;

public class SettingsFilterActivity extends AppCompatActivity {

    public static final String
            KEY_PREF_NOTIFICATION_SWITCH = "notification_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFilterFragment())
                .commit();

    }
}
