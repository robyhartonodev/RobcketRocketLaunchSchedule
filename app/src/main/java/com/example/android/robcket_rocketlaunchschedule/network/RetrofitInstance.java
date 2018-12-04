package com.example.android.robcket_rocketlaunchschedule.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static final String ROCKET_BASE_URL = "https://api.myjson.com/";

    /**
     * Create an instance of Retrofit object
     * */
    public static Retrofit getRetrofitInstanceRocket() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(ROCKET_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
