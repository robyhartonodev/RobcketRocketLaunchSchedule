package com.example.android.robcket_rocketlaunchschedule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.fragment.SettingsFilterFragment;

public class SettingsFilterActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String
            KEY_PREF_NOTIFICATION_SWITCH = "notification_switch";
    public static final String
            KEY_PREF_FILTER_ALL_CHECKBOX = "check_box_preference_all";
    public static final String
            KEY_PREF_FILTER_NASA_CHECKBOX = "check_box_preference_nasa";
    public static final String
            KEY_PREF_FILTER_SPACEX_CHECKBOX = "check_box_preference_spacex";
    public static final String
            KEY_PREF_FILTER_ULA_CHECKBOX = "check_box_preference_ula";
    public static final String
            KEY_PREF_FILTER_KSC_CHECKBOX = "check_box_preference_ksc";
    public static final String
            KEY_PREF_FILTER_VANDENBERG_CHECKBOX = "check_box_preference_vandenberg";
    public static final String
            KEY_PREF_FILTER_ARIANESPACE_CHECKBOX = "check_box_preference_arianespace";
    public static final String
            KEY_PREF_FILTER_CASC_CHECKBOX = "check_box_preference_casc";
    public static final String
            KEY_PREF_FILTER_ISRO_CHECKBOX = "check_box_preference_isro";
    public static final String
            KEY_PREF_FILTER_PLESTSK_CHECKBOX = "check_box_preference_plestsk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFilterFragment())
                .commit();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(KEY_PREF_FILTER_ARIANESPACE_CHECKBOX)){
            
        }
    }
}
