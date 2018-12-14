package com.example.android.robcket_rocketlaunchschedule.utils;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Helper class to remove underline on hyperlink textview
 */
public class URLSpanNoUnderline extends URLSpan {

    public URLSpanNoUnderline(String url) {
        super(url);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
