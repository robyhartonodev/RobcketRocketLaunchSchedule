package com.example.android.robcket_rocketlaunchschedule.my_interface;

import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetLaunchDataService {

    @GET("launch/next/10")
    Call<LaunchNextList> getLaunchNextListData();

    /**
     * id launch service provider (lsp)
     * 44   : National Aeronautics and Space Administration (NASA)
     * 121  : SpaceX
     * 31   : Indian Space Research Organization(ISRO)
     * 115  : Arianespace
     * 37   : Japan Aerospace Exploration Agency (JAXA)
     * 63   : Russian Federal Space Agency (ROSCOSMOS)
     * 88   : China Aerospace Science and Technology Corporation(CASC)
     * 124  : United Launch Alliance(ULA)
     * 147  : Rocket Lab Ltd
     */

    /**
     * id location
     * 1    : Jiuquan, People's Republic of China
     * 2    : Taiyuan, People's Republic of China
     * 3    : Kourou, French Guiana
     * 4    : Hammaguir, Algeria
     * 5    : Sriharikota, Republic of India
     * 6    : Semnan Space Center, Islamic Republic of Iran
     * 7    : Kenya
     * 8    : Kagoshima, Japan
     * 9    : Tanegashima, Japan
     * 10   : Baikonur Cosmodrome, Republic of Kazakhstan
     * 11   : Plesetsk Cosmodrome, Russian Federation
     * 12   : Kapustin Yar, Russian Federation
     * 13   : Svobodney Cosmodrome, Russian Federation
     * 14   : Dombarovskiy, Russian Federation
     * 15   : Sea Launch
     * 16   : Cape Canaveral, FL, USA
     * 17   : Kennedy Space Center, FL, USA
     * 18   : Vandenberg AFB, CA, USA
     * 19   : Wallops Island, Virginia, USA
     * 20   : Woomera, Australia
     * 24   : Kiatorete Spit, New Zealand
     * 25   : Xichang Satellite Launch Center, People's Republic of China
     * 26   : Negev, State of Israel
     * 27   : Palmachim Airbase, State of Israel
     * 28   : Kauai, USA
     * 29   : Ohae Satellite Launching station, Democratic People's Republic of Korea
     * 31   : Naro Space Center, South Korea
     * 32   : Kodiak Launch Complex, Alaska, USA
     * 33   : Wenchang Satellite Launch Center, People's Republic of China
     * 37   : Unknown Location
     */

    @GET("launch/next/10")
    Observable<LaunchNextList> getLaunchNextListDataWithAgency(@Query("lsp") String lsp);

}
