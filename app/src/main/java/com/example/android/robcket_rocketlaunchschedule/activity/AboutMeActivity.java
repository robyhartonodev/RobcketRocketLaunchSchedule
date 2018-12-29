package com.example.android.robcket_rocketlaunchschedule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture)
                .setCover(R.mipmap.profile_cover)
                .setName("Roby Hartono")
                .setSubTitle("Mobile Developer")
                .setBrief("I'm warmed of mobile technologies. Like basketball, nature and video games.")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                //TODO add google play store link
                .addGooglePlayStoreLink("8002078663318221363")
                .addGitHubLink("robyhartonodev")
                .addFacebookLink("roby.hartono.5")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        addContentView(view, new FrameLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
