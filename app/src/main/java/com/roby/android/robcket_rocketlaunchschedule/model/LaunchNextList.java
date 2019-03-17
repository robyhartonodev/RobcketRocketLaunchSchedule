package com.roby.android.robcket_rocketlaunchschedule.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LaunchNextList {

    @SerializedName("launches")
    @Expose
    private ArrayList<Launch> launches = null;
    @SerializedName("total")
    @Expose
    private int total;
    @SerializedName("offset")
    @Expose
    private int offset;
    @SerializedName("count")
    @Expose
    private int count;

    public ArrayList<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(ArrayList<Launch> launches) {
        this.launches = launches;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
