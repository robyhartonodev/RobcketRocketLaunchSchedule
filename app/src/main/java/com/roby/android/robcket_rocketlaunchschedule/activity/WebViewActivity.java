package com.roby.android.robcket_rocketlaunchschedule.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.roby.android.robcket_rocketlaunchschedule.R;

public class WebViewActivity extends AppCompatActivity {


    private WebView mWebView;

    private String ROCKET_WIKI_URL_EXTRA = "LAUNCH_ROCKET_WIKI_URL";
    private String AGENCY_WIKI_URL_EXTRA = "LAUNCH_AGENCY_WIKI_URL";
    private String ROCKET_NAME_EXTRA = "LAUNCH_ROCKET_NAME";
    private String AGENCY_NAME_EXTRA = "LAUNCH_AGENCY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mWebView = findViewById(R.id.webView1);

        // Set WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Set Javascript on in the webview
        mWebView.getSettings().setJavaScriptEnabled(true);

        //Load the WebView based on clicked text view on LaunchDetailActivity
        if (getIntent().hasExtra(ROCKET_WIKI_URL_EXTRA)) {
            // Load the Url
            mWebView.loadUrl(getIntent().getStringExtra(ROCKET_WIKI_URL_EXTRA));

            // Set title of toolbar with launch name
            // getSupportActionBar().setTitle(getIntent().getStringExtra(ROCKET_NAME_EXTRA));
        } else if (getIntent().hasExtra(AGENCY_WIKI_URL_EXTRA)) {
            // Load the Url
            mWebView.loadUrl(getIntent().getStringExtra(AGENCY_WIKI_URL_EXTRA));

            // Set title of toolbar with launch name
            // getSupportActionBar().setTitle(getIntent().getStringExtra(AGENCY_NAME_EXTRA));
        }

    }
}
