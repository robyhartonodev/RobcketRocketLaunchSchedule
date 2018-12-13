package com.example.android.robcket_rocketlaunchschedule.my_interface;

import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetLaunchDataService {

    @GET("launch/next/10")
    Call<LaunchNextList> getLaunchNextListData();
}
