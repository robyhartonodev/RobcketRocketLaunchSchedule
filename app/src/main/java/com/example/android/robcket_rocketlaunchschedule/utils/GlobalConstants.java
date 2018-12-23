package com.example.android.robcket_rocketlaunchschedule.utils;

/**
 * This class contains global variables for Keys, Tag, Preferences, Action ID, etc.
 */
public final class GlobalConstants {

    // Value of notification switch
    public static Boolean notificationSwitchPref;
    /**
     * Id launch service provider (lsp)
     * 44   : National Aeronautics and Space Administration (NASA)
     * 121  : SpaceX
     * 31   : Indian Space Research Organization(ISRO)
     * 115  : Arianespace
     * 37   : Japan Aerospace Exploration Agency (JAXA)
     * 63   : Russian Federal Space Agency (ROSCOSMOS)
     * 88   : China Aerospace Science and Technology Corporation(CASC)
     * 124  : United Launch Alliance(ULA)
     * 147  : Rocket Lab Ltd
     * WARNING: DO NOT CHANGE VALUE OF THE STRINGS
     */
    // Value of filter NASA
    public static String filterAgencyNASA = "FILTER_AGENCY_NASA_PREF_KEY";
    // Value of filter SpaceX
    public static String filterAgencySpaceX = "FILTER_AGENCY_SPACEX_PREF_KEY";
    // Value of filter ULA
    public static String filterAgencyULA = "FILTER_AGENCY_ULA_PREF_KEY";
    // Value of filter ROSCOSMOS
    public static String filterAgencyROSCOSMOS = "FILTER_AGENCY_ROSCOSMOS_PREF_KEY";
    // Value of filter JAXA
    public static String filterAgencyJAXA = "FILTER_AGENCY_JAXA_PREF_KEY";
    // Value of filter Arianespace
    public static String filterAgencyArianespace = "FILTER_AGENCY_ARIANESPACE_PREF_KEY";
    // Value of filter CASC
    public static String filterAgencyCASC = "FILTER_AGENCY_CASC_PREF_KEY";
    // Value of filter ISRO
    public static String filterAgencyISRO = "FILTER_AGENCY_ISRO_PREF_KEY";
    // Value of filter RocketLabLtd
    public static String filterAgencyRocketLabLtd = "FILTER_AGENCY_ROCKETLABLTB_PREF_KEY";

    // Value of filter NASA
    public static Boolean filterAgencyNASABoolean;
    // Value of filter SpaceX
    public static Boolean filterAgencySpaceXBoolean;
    // Value of filter ULA
    public static Boolean filterAgencyULABoolean;
    // Value of filter ROSCOSMOS
    public static Boolean filterAgencyROSCOSMOSBoolean;
    // Value of filter JAXA
    public static Boolean filterAgencyJAXABoolean;
    // Value of filter Arianespace
    public static Boolean filterAgencyArianespaceBoolean;
    // Value of filter CASC
    public static Boolean filterAgencyCASCBoolean;
    // Value of filter ISRO
    public static Boolean filterAgencyISROBoolean;
    // Value of filter RocketLabLtd
    public static Boolean filterAgencyRocketLabLtdBoolean;

    /**
     * Filter variables for location (actually pad)
     * 5,6,7,8    : Jiuquan, People's Republic of China
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
     * 1,2,3,4   : Woomera, Australia
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
    // Id for all locations
    public static String allLocationIdString = "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,24,25,26,27,28,29,31,32,33,37";

    // Value of filter Jiuquan
    public static String filterLocationJiuquan = "FILTER_LOCATION_JIUQUAN_PREF_KEY";
    // Value of filter Taiyuan
    public static String filterLocationTaiyuan = "FILTER_LOCATION_TAIYUAN_PREF_KEY";
    // Value of filter Kourou
    public static String filterLocationKourou = "FILTER_LOCATION_KOUROU_PREF_KEY";
    // Value of filter Hammaguir
    public static String filterLocationHammaguir = "FILTER_LOCATION_HAMMAGUIR_PREF_KEY";
    // Value of filter Sriharikota
    public static String filterLocationSriharikota = "FILTER_LOCATION_SRIHARIKOTA_PREF_KEY";
    // Value of filter Semnan
    public static String filterLocationSemnan = "FILTER_LOCATION_SEMNAN_PREF_KEY";
    // Value of filter Kenya
    public static String filterLocationKenya = "FILTER_LOCATION_KENYA_PREF_KEY";
    // Value of filter Kagoshima
    public static String filterLocationKagoshima = "FILTER_LOCATION_KAGOSHIMA_PREF_KEY";
    // Value of filter Tanegashima
    public static String filterLocationTanegashima = "FILTER_LOCATION_TANEGASHIMA_PREF_KEY";
    // Value of filter Baikonur Cosmodrome
    public static String filterLocationBaikonur = "FILTER_LOCATION_BAIKONUR_PREF_KEY";
    // Value of filter Plesetsk Cosmodrome
    public static String filterLocationPlesetsk = "FILTER_LOCATION_PLESETSK_PREF_KEY";
    // Value of filter Kapustin Yar
    public static String filterLocationKapustin = "FILTER_LOCATION_KAPUSTIN_PREF_KEY";
    // Value of filter Svobodney Cosmodrome
    public static String filterLocationSvobodney = "FILTER_LOCATION_SVOBODNEY_PREF_KEY";
    // Value of filter Dombarovskiy
    public static String filterLocationDombarovskiy = "FILTER_LOCATION_DOMBAROVSKY_PREF_KEY";
    // Value of filter Sea Launch
    public static String filterLocationSea = "FILTER_LOCATION_SEALAUNCH_PREF_KEY";
    // Value of filter Cape canaveral
    public static String filterLocationCape = "FILTER_LOCATION_CAPE_PREF_KEY";
    // Value of filter Kennedy Space Center
    public static String filterLocationKennedy = "FILTER_LOCATION_KENNEDY_PREF_KEY";
    // Value of filter Vandenberg AFB
    public static String filterLocationVandenberg = "FILTER_LOCATION_VANDENBERG_PREF_KEY";
    // Value of filter Wallops
    public static String filterLocationWallops = "FILTER_LOCATION_WALLOPS_PREF_KEY";
    // Value of filter Woomera
    public static String filterLocationWoomera = "FILTER_LOCATION_WOOMERA_PREF_KEY";
    // Value of filter Kiatorete Spit
    public static String filterLocationKiatorete = "FILTER_LOCATION_KIATORETE_PREF_KEY";
    // Value of filter Xichang Satellite Launch Center
    public static String filterLocationXichang = "FILTER_LOCATION_XICHANG_PREF_KEY";
    // Value of filter Negev, State of Israel
    public static String filterLocationNegev = "FILTER_LOCATION_NEGEV_PREF_KEY";
    // Value of filter Palmachim Airbase
    public static String filterLocationPalmachim = "FILTER_LOCATION_PALMACHIM_PREF_KEY";
    // Value of filter Kauai
    public static String filterLocationKauai = "FILTER_LOCATION_KAUAI_PREF_KEY";
    // Value of filter Ohae Satellite Launching station
    public static String filterLocationOhae = "FILTER_LOCATION_OHAE_PREF_KEY";
    // Value of filter Naro Space Center
    public static String filterLocationNaro = "FILTER_LOCATION_NARO_PREF_KEY";
    // Value of filter Kodiak Launch Complex
    public static String filterLocationKodiak= "FILTER_LOCATION_KODIAK_PREF_KEY";
    // Value of filter Wenchang Satellite Launch Center
    public static String filterLocationWenchang= "FILTER_LOCATION_WENCHANG_PREF_KEY";
    // Value of filter Unknown Location
    public static String filterLocationUnknown = "FILTER_LOCATION_UNKNOWN_PREF_KEY";



    private GlobalConstants(){

    }

}
