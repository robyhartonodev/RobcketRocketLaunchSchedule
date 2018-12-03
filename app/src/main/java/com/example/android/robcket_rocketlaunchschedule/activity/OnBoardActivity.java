package com.example.android.robcket_rocketlaunchschedule.activity;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.TitlePage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

public class OnBoardActivity extends WelcomeActivity {
    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimary)
                .page(new TitlePage(R.drawable.ic_notification,
                        "Title")
                )
                .page(new BasicPage(R.drawable.ic_calendar,
                        "Header",
                        "More text.")
                        .background(R.color.colorAccent)
                )
                .page(new BasicPage(R.drawable.ic_rocket,
                        "Lorem ipsum",
                       "dolor sit amet.")
                )
                .swipeToDismiss(true)
                .build();
    }
}
