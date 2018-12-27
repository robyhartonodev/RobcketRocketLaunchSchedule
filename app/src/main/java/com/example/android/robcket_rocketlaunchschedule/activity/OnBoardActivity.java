package com.example.android.robcket_rocketlaunchschedule.activity;

import android.content.Context;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import androidx.core.content.ContextCompat;

public class OnBoardActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {

        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.secondaryDarkColor)
                .page(new TitlePage(R.drawable.ic_iconfinder_space_exploration,
                        "ROBKET - NEXT SPACE LAUNCH")
                )
                .page(new BasicPage(R.drawable.ic_iconfinder_space_shuttle,
                        "DETAIL",
                        "Get detailed information about the launch")
                        .background(R.color.secondaryColor)
                )
                .page(new BasicPage(R.drawable.ic_iconfinder_satellite,
                        "NOTIFICATION",
                        "Get notification for the upcoming rocket launches in the world")
                        .background(R.color.secondaryColor)
                )
                .page(new BasicPage(R.drawable.ic_iconfinder_astronaut,
                        "FILTER",
                       "Follow your desired rocket launch by agencies like NASA, SpaceX, etc.")
                        .background(R.color.secondaryLightColor)
                )
                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }
}
