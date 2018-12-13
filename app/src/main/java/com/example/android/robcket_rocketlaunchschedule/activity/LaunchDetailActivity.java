package com.example.android.robcket_rocketlaunchschedule.activity;

import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchDetailActivity extends AppCompatActivity {

    // Variables for toolbar
    private ImageView mLaunchRocketImageView;
    private TextView mLaunchTitleTextView;

    // Variables for Details
    private TextView mLaunchDateTextView;

    // Variables for Missions
    private TextView mLaunchMissionNameTextView;
    private TextView mLaunchMissionSummaryTextView;

    // Variables for putExtra Intents
    private String ROCKET_IMAGE_EXTRA = "LAUNCH_IMAGE_URL";
    private String LAUNCH_TITLE_EXTRA = "LAUNCH_TITLE";
    private String MISSION_NAME_EXTRA = "LAUNCH_MISSION_NAME";
    private String MISSION_SUMMARY_EXTRA = "LAUNCH_MISSION_SUMMARY";
    private String LAUNCH_DATE_EXTRA = "LAUNCH_DATE";
    private String LAUNCH_WINDOW_START_EXTRA = "LAUNCH_WINDOW_START";
    private String LAUNCH_WINDOW_END_EXTRA = "LAUNCH_WINDOW_END";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set ViewPager
        // setViewPager();

        // Display back button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Set toolbar collapsing image
        mLaunchRocketImageView = findViewById(R.id.launch_rocket_detail_image_view);
        Picasso.with(this)
                .load(getIntent().getStringExtra(ROCKET_IMAGE_EXTRA))
                .placeholder(R.drawable.ic_rocket)
                .into(mLaunchRocketImageView);

        // Set title of toolbar with launch name
        getSupportActionBar().setTitle(getIntent().getStringExtra(LAUNCH_TITLE_EXTRA));

        // Set Details CardView information
        setDetailInformation();

        // Set Mission CardView information
        setMissionInformation();
    }

    /**
     * This Method sets all information in Mission CardView
     */
    private void setMissionInformation(){
        // Set Mission Title
        mLaunchMissionNameTextView = findViewById(R.id.textview_launch_mission_name);
        mLaunchMissionNameTextView.setText(getIntent().getStringExtra(MISSION_NAME_EXTRA));

        // Set Mission Summary
        mLaunchMissionSummaryTextView = findViewById(R.id.textview_launch_mission_details);
        mLaunchMissionSummaryTextView.setText(getIntent().getStringExtra(MISSION_SUMMARY_EXTRA));
    }

    /**
     * This method sets all information in Details CardView
     */
    private void setDetailInformation(){
        // Set Launch Date
        mLaunchDateTextView = findViewById(R.id.textview_date_value);
        mLaunchDateTextView.setText(getIntent().getStringExtra(LAUNCH_WINDOW_START_EXTRA));

        // Set Launch Window
    }
}
