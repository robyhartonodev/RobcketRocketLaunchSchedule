package com.roby.android.robcket_rocketlaunchschedule.my_interface;

import com.roby.android.robcket_rocketlaunchschedule.model.LaunchNextList;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetLaunchDataService {

    @GET("launch/next/30")
    Call<LaunchNextList> getLaunchNextListData();

    @GET("launch/next/30")
    Observable<LaunchNextList> getLaunchNextListDataWithAgency(@Query("lsp") String lsp);

}
