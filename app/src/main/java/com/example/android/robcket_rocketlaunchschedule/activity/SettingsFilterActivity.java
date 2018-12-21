package com.example.android.robcket_rocketlaunchschedule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.fragment.SettingsFilterFragment;

public class SettingsFilterActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * id launch service provider (lsp)
     * 44   : National Aeronautics and Space Administration (NASA)
     * 121  : SpaceX
     * 31   : Indian Space Research Organization(ISRO)
     * 115  : Arianespace
     * 37   : Japan Aerospace Exploration Agency (JAXA)
     * 63   : Russian Federal Space Agency (ROSCOSMOS)
     * 88   : China Aerospace Science and Technology Corporation(CASC)
     * 124  : United Launch Alliance(ULA)
     * 147  : Rocket Lab Ltd
     */

    public static final String
            KEY_PREF_NOTIFICATION_SWITCH = "notification_switch";
    public static final String
            KEY_PREF_FILTER_NASA_CHECKBOX = "check_box_preference_nasa";
    public static final String
            KEY_PREF_FILTER_SPACEX_CHECKBOX = "check_box_preference_spacex";
    public static final String
            KEY_PREF_FILTER_JAXA_CHECKBOX = "check_box_preference_jaxa";
    public static final String
            KEY_PREF_FILTER_ROCKETLABLTD_CHECKBOX = "check_box_preference_rocketlabltd";
    public static final String
            KEY_PREF_FILTER_ULA_CHECKBOX = "check_box_preference_ula";
    public static final String
            KEY_PREF_FILTER_ARIANESPACE_CHECKBOX = "check_box_preference_arianespace";
    public static final String
            KEY_PREF_FILTER_CASC_CHECKBOX = "check_box_preference_casc";
    public static final String
            KEY_PREF_FILTER_ISRO_CHECKBOX = "check_box_preference_isro";
    public static final String
            KEY_PREF_FILTER_ROSCOSMOS_CHECKBOX = "check_box_preference_roscosmos";

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
