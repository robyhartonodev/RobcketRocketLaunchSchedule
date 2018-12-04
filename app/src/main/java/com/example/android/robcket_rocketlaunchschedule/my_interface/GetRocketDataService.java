package com.example.android.robcket_rocketlaunchschedule.my_interface;

import com.example.android.robcket_rocketlaunchschedule.model.RocketList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetRocketDataService {

    @GET("rocket")
    Call<RocketList> getRocketData();
}
