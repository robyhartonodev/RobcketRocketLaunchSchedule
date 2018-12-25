package com.example.android.robcket_rocketlaunchschedule.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.adapter.LaunchNextAdapter;
import com.example.android.robcket_rocketlaunchschedule.model.Launch;
import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;
import com.example.android.robcket_rocketlaunchschedule.model.Mission;
import com.example.android.robcket_rocketlaunchschedule.my_interface.GetLaunchDataService;
import com.example.android.robcket_rocketlaunchschedule.utils.GlobalConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.network.RetrofitInstance;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.stephentuso.welcome.WelcomeHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function9;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private WelcomeHelper welcomeHelper;
    private LaunchNextAdapter launchNextAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout launchSwipeRefreshLayout;
    private TextView nextLaunchTimerTextView;
    private Drawer result;

    private String nextLaunchTimerString;

    private ArrayList<Launch> finalLaunchNextList = new ArrayList<>();
    private ArrayList<Mission> missionListSorted = new ArrayList<>();

    private SharedPreferences filterSettingsSharedPreferences;
    // private SharedPreferences.Editor preferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });

        // Shared Preferences setup
        filterSettingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // filterSettingsSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        // applySettingsFilter();

        // Set Next Launch Timer TextView
        nextLaunchTimerTextView = findViewById(R.id.textview_next_launch_timer);

        // SwipeRefreshLayout
        launchSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        // Set Refresh Listener to launchSwipeRefreshLayout
        setRocketSwipeRefreshLayout();

        // Welcome / OnBoard Screen Setup
        welcomeHelper = new WelcomeHelper(this, OnBoardActivity.class);
        welcomeHelper.show(savedInstanceState);

        // Set the Rocket List
        generateLaunchList();

        // Set the Navigation Drawer
        setNavigationDrawer(toolbar);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        welcomeHelper.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
            // Recreate activity, override blank screen animation
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            result.openDrawer();
            return true;
        } else if (id == R.id.action_tutorial) {
            // Show the OnBoarding / Tutorial screen
            welcomeHelper.forceShow();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to generate List of notice using RecyclerView with custom adapter
     */
    private void generateLaunchList() {
        // Clear the launch list first to avoid duplication
        finalLaunchNextList.clear();
        missionListSorted.clear();

        // Check if all filters unchecked
        if (!(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencySpaceX, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyNASA, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyISRO, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyArianespace, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyJAXA, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyROSCOSMOS, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyCASC, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyULA, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyRocketLabLtd, false)
        )) {
            // Create handle for the RetrofitInstance interface
            GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);

            // Call the method with parameter in the interface to get the notice data
            Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();

            launchNextCall.enqueue(new Callback<LaunchNextList>() {
                @Override
                public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
                    // Set next launch string variable with the next launch time from json response
                    nextLaunchTimerString = response.body().getLaunches().get(0).getWindowstart();

                    // Set the Countdown Timer
                    setCountDownTimer();

                    // Populate the launch list
                    finalLaunchNextList.addAll(response.body().getLaunches());

                    for (int i = 0; i < finalLaunchNextList.size(); i++) {
                        if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                            missionListSorted.add(new Mission("TBD", "No Information available"));
                        }
                        missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                    }

                    recyclerView = findViewById(R.id.recycler_view_notice_list);
                    launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches(), missionListSorted);

                    // Setup layout manager
                    int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                    LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(launchNextAdapter);
                }

                @Override
                public void onFailure(Call<LaunchNextList> call, Throwable t) {
                    ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();
                    ArrayList<Mission> launchNextMissionFailureList = new ArrayList<>();

                    recyclerView = findViewById(R.id.recycler_view_notice_list);
                    launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList, launchNextMissionFailureList);

                    // Setup layout manager
                    int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                    LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(launchNextAdapter);

                    // Show Toast
                    Toast.makeText(MainActivity.this,
                            "Unable to load the list.\nPlease check your connection"
                            , Toast.LENGTH_LONG).show();
                }
            });
        } // end if
        else {

            GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitRxInstance().create(GetLaunchDataService.class);

            // Observable agencies
            Observable<LaunchNextList> obs1 = launchNextService.getLaunchNextListDataWithAgency("121");     // SpaceX
            Observable<LaunchNextList> obs2 = launchNextService.getLaunchNextListDataWithAgency("31");      // ISRO
            Observable<LaunchNextList> obs3 = launchNextService.getLaunchNextListDataWithAgency("44");      // NASA
            Observable<LaunchNextList> obs4 = launchNextService.getLaunchNextListDataWithAgency("115");     // Arianespace
            Observable<LaunchNextList> obs5 = launchNextService.getLaunchNextListDataWithAgency("37");      // JAXA
            Observable<LaunchNextList> obs6 = launchNextService.getLaunchNextListDataWithAgency("63");      // ROSCOSMOS
            Observable<LaunchNextList> obs7 = launchNextService.getLaunchNextListDataWithAgency("88");      // CASC
            Observable<LaunchNextList> obs8 = launchNextService.getLaunchNextListDataWithAgency("124");     // ULA
            Observable<LaunchNextList> obs9 = launchNextService.getLaunchNextListDataWithAgency("147");     // Rocketlab Ltd

            // Create observable
            Observable<List<LaunchNextList>> result =
                    Observable.zip(
                            obs1.subscribeOn(Schedulers.io()),
                            obs2.subscribeOn(Schedulers.io()),
                            obs3.subscribeOn(Schedulers.io()),
                            obs4.subscribeOn(Schedulers.io()),
                            obs5.subscribeOn(Schedulers.io()),
                            obs6.subscribeOn(Schedulers.io()),
                            obs7.subscribeOn(Schedulers.io()),
                            obs8.subscribeOn(Schedulers.io()),
                            obs9.subscribeOn(Schedulers.io()),
                            new Function9<LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, List<LaunchNextList>>() {
                                @Override
                                public List<LaunchNextList> apply(LaunchNextList launchNextList, LaunchNextList launchNextList2, LaunchNextList launchNextList3, LaunchNextList launchNextList4, LaunchNextList launchNextList5, LaunchNextList launchNextList6, LaunchNextList launchNextList7, LaunchNextList launchNextList8, LaunchNextList launchNextList9) throws Exception {
                                    List<LaunchNextList> list = new ArrayList<>();
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencySpaceX, false))
                                        list.add(launchNextList);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyISRO, false))
                                        list.add(launchNextList2);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyNASA, false))
                                        list.add(launchNextList3);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyArianespace, false))
                                        list.add(launchNextList4);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyJAXA, false))
                                        list.add(launchNextList5);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyROSCOSMOS, false))
                                        list.add(launchNextList6);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyCASC, false))
                                        list.add(launchNextList7);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyULA, false))
                                        list.add(launchNextList8);
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyRocketLabLtd, false))
                                        list.add(launchNextList9);
                                    return list;
                                }
                            });

            result.observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new Observer<List<LaunchNextList>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<LaunchNextList> launchNextLists) {
                            // Debug Toast
                            // Toast.makeText(MainActivity.this, String.valueOf(launchNextLists.size()), Toast.LENGTH_LONG).show();

                            for (int i = 0; i < launchNextLists.size(); i++) {
                                finalLaunchNextList.addAll(launchNextLists.get(i).getLaunches());
                            }

                            // Sort the final launch next list based on date
                            Collections.sort(finalLaunchNextList, new Comparator<Launch>() {
                                @Override
                                public int compare(Launch r1, Launch r2) {
                                    // Get Date from string datetime window start of each launch
                                    Date launchDate1 = convertJSONStringToDate(r1.getWindowstart());
                                    Date launchDate2 = convertJSONStringToDate(r2.getWindowstart());

                                    return launchDate1.compareTo(launchDate2);
                                }
                            });

                            for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                                    missionListSorted.add(new Mission("TBD", "No Information available"));
                                }
                                missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                            }

                            Toast.makeText(MainActivity.this, String.valueOf(missionListSorted.size()), Toast.LENGTH_LONG).show();

                            // Set next launch string variable with the next launch time from json response
                            nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();


                            // Set the Countdown Timer
                            setCountDownTimer();

                            recyclerView = findViewById(R.id.recycler_view_notice_list);
                            launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                            // Setup layout manager
                            int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                            LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(launchNextAdapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // Show Toast
                            Toast.makeText(MainActivity.this,
                                    "Unable to load the list.\nPlease check your connection"
                                    , Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } // end else
    }

    /**
     * Set the Navigation Drawer
     *
     * @param toolbar toolbar of the activity in onCreate method
     */
    private void setNavigationDrawer(Toolbar toolbar) {
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.secondaryColor)
                .withSelectionListEnabledForSingleProfile(false)
                //.withHeightDp(56)
                .withTextColor(getResources().getColor(R.color.material_drawer_dark_primary_text))
                .addProfiles(
                        new ProfileDrawerItem().withName("Settings & Filter")
                                //.withEmail("Rocket Launcher Schedule App")
                                .withIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                )
                .withProfileImagesVisible(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        SwitchDrawerItem itemNotification = new SwitchDrawerItem()
                .withIdentifier(1)
                .withName("Notification")
                .withDescription("Turn notification on or off")
                .withDescriptionTextColor(getResources().getColor(R.color.material_drawer_header_selection_subtext))
                .withIcon(FontAwesome.Icon.faw_bell)
                .withSelectable(false);

        // Agency Filter List
        ExpandableDrawerItem itemFilterAgency = new ExpandableDrawerItem()
                .withIdentifier(2)
                .withName("Filter by agencies")
                .withIcon(FontAwesome.Icon.faw_place_of_worship)
                .withSelectable(false)
                .withSubItems(
                        new SwitchDrawerItem().withName("NASA").withLevel(2).withIdentifier(4)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyNASA, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("SpaceX").withLevel(2).withIdentifier(5)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencySpaceX, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("ISRO").withLevel(2).withIdentifier(6)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyISRO, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Arianespace").withLevel(2).withIdentifier(7)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyArianespace, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("JAXA").withLevel(2).withIdentifier(8)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyJAXA, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("ROSCOSMOS").withLevel(2).withIdentifier(9)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyROSCOSMOS, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("CASC").withLevel(2).withIdentifier(10)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyCASC, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("ULA").withLevel(2).withIdentifier(11)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyULA, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Rocket Lab Ltd").withLevel(2).withIdentifier(12)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyRocketLabLtd, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener)
                );


        // Location Filter List
//        ExpandableDrawerItem itemFilterLocation = new ExpandableDrawerItem()
//                .withIdentifier(3)
//                .withName("Filter by locations")
//                .withIcon(FontAwesome.Icon.faw_globe_americas)
//                .withSelectable(false)
//                .withSubItems(
//                        new SwitchDrawerItem().withName("Jiuquan, China").withLevel(2).withIdentifier(13)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationJiuquan, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Taiyuan, China").withLevel(2).withIdentifier(14)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTaiyuan, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kourou, French Guiana").withLevel(2).withIdentifier(15)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKourou, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Hammaguir, Algeria").withLevel(2).withIdentifier(16)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationHammaguir, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Sriharikota, India").withLevel(2).withIdentifier(17)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSriharikota, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Semnan, Iran").withLevel(2).withIdentifier(18)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSemnan, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kenya").withLevel(2).withIdentifier(19)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKenya, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kagoshima, Japan").withLevel(2).withIdentifier(20)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKagoshima, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Tanegashima, Japan").withLevel(2).withIdentifier(21)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTanegashima, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Baikonur Cosmodrome, Kazakhstan").withLevel(2).withIdentifier(22)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationBaikonur, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Plesetsk Cosmodrome, Russia").withLevel(2).withIdentifier(23)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPlesetsk, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kapustin Yar, Russia").withLevel(2).withIdentifier(24)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKapustin, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Svobodney Cosmodrome, Russia").withLevel(2).withIdentifier(25)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSvobodney, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Dombarovskiy, Russia").withLevel(2).withIdentifier(26)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationDombarovskiy, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Sea Launch").withLevel(2).withIdentifier(27)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSea, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Cape Canaveral, USA").withLevel(2).withIdentifier(28)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationCape, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kennedy Space Center, USA").withLevel(2).withIdentifier(29)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKennedy, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Vandenberg AFB, USA").withLevel(2).withIdentifier(30)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationVandenberg, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Wallops Island, USA").withLevel(2).withIdentifier(31)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWallops, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Woomera, Australia").withLevel(2).withIdentifier(32)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWoomera, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kiatorete Spit, New Zealand").withLevel(2).withIdentifier(33)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKiatorete, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Xichang, China").withLevel(2).withIdentifier(34)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationXichang, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Negev, Israel").withLevel(2).withIdentifier(35)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNegev, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Palmachim Airbase, Israel").withLevel(2).withIdentifier(36)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPalmachim, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kauai, USA").withLevel(2).withIdentifier(37)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKauai, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Ohae, Korea").withLevel(2).withIdentifier(38)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationOhae, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Naro Space Center, South Korea").withLevel(2).withIdentifier(39)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNaro, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Kodiak, USA").withLevel(2).withIdentifier(40)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKodiak, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Wenchang, China").withLevel(2).withIdentifier(41)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWenchang, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Unknown Location").withLevel(2).withIdentifier(42)
//                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationUnknown, false))
//                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener)
//
//                );

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withFullscreen(true)
                .withSelectedItem(-1)
                .withDrawerGravity(Gravity.END)
                //.withDrawerWidthPx(matchParentWidth)
                //.withToolbar(toolbar)
                .addDrawerItems(
                        itemNotification,
                        new DividerDrawerItem(),
                        itemFilterAgency
                        //new DividerDrawerItem(),
                        //itemFilterLocation
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                        }
                        return true;
                    }
                })
                //.withSavedInstance()
                .build();


        //Get the DrawerLayout from the Drawer
        DrawerLayout drawerLayout = result.getDrawerLayout();

        //Lock the Drawer
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (Build.VERSION.SDK_INT >= 19) {
            result.getDrawerLayout().setFitsSystemWindows(false);
        }
    }

    /**
     * This method handles check behaviour for switch- and toggleitems in the drawer
     */
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {


        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {

                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);

                // ID Identifier for items
                int id = (int) drawerItem.getIdentifier();

                /**
                 * saves value of check into the shared preference in global constants class
                 */
                switch (id) {
                    // NASA
                    case 4:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyNASA, isChecked).apply();
                        break;
                    // SpaceX
                    case 5:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencySpaceX, isChecked).apply();
                        break;
                    // ISRO
                    case 6:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyISRO, isChecked).apply();
                        break;
                    // Arianespace
                    case 7:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyArianespace, isChecked).apply();
                        break;
                    // JAXA
                    case 8:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyJAXA, isChecked).apply();
                        break;
                    // ROSCOSMOS
                    case 9:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyROSCOSMOS, isChecked).apply();
                        break;
                    // CASC
                    case 10:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyCASC, isChecked).apply();
                        break;
                    // ULA
                    case 11:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyULA, isChecked).apply();
                        break;
                    // RocketLab Ltd
                    case 12:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterAgencyRocketLabLtd, isChecked).apply();
                        break;
//                    // Jiuquan
//                    case 13:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationJiuquan, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Taiyuan
//                    case 14:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationTaiyuan, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kourou
//                    case 15:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKourou, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Hammaguir
//                    case 16:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationHammaguir, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Sriharikota
//                    case 17:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationSriharikota, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Semnan
//                    case 18:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationSemnan, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kenya
//                    case 19:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKenya, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kagoshima
//                    case 20:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKagoshima, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Tanegashima
//                    case 21:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationTanegashima, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Baikonur
//                    case 22:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationBaikonur, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Plesetsk
//                    case 23:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationPlesetsk, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kapustin
//                    case 24:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKapustin, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Svobodney
//                    case 25:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationSvobodney, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Dombarovskiy
//                    case 26:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationDombarovskiy, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Sea Launch
//                    case 27:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationSea, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Cape
//                    case 28:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationCape, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kennedy
//                    case 29:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKennedy, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Vandenberg
//                    case 30:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationVandenberg, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Wallops
//                    case 31:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationWallops, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Woomera
//                    case 32:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationWoomera, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kiatorete
//                    case 33:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKiatorete, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Xichang
//                    case 34:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationXichang, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Negev
//                    case 35:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationNegev, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Palmachim
//                    case 36:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationPalmachim, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kauai
//                    case 37:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKauai, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Ohae
//                    case 38:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationOhae, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Naro
//                    case 39:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationNaro, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Kodiak
//                    case 40:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationKodiak, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Wenchang
//                    case 41:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationWenchang, isChecked);
//                        preferencesEditor.apply();
//                        break;
//                    // Unknown
//                    case 42:
//                        preferencesEditor.putBoolean(GlobalConstants.filterLocationUnknown, isChecked);
//                        preferencesEditor.apply();
//                        break;
                    default:
                        break;
                } // end switch case

                // Debug Toast
                // Toast.makeText(MainActivity.this, "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked, Toast.LENGTH_SHORT).show();

            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    /**
     * Sets up a SwipeRefreshLayout.OnRefreshListener and Refresh signal colors that is invoked when the user
     * performs a swipe-to-refresh gesture.
     */
    private void setRocketSwipeRefreshLayout() {
        launchSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Clear the launch list first to avoid duplication
                        finalLaunchNextList.clear();
                        missionListSorted.clear();

                        // Check if all filters unchecked
                        if (!(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencySpaceX, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyNASA, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyISRO, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyArianespace, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyJAXA, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyROSCOSMOS, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyCASC, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyULA, false) ||
                                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyRocketLabLtd, false)
                        )) {
                            // Create handle for the RetrofitInstance interface
                            GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);

                            // Call the method with parameter in the interface to get the notice data
                            Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();

                            launchNextCall.enqueue(new Callback<LaunchNextList>() {
                                @Override
                                public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
                                    // Set next launch string variable with the next launch time from json response
                                    nextLaunchTimerString = response.body().getLaunches().get(0).getWindowstart();

                                    // Set the Countdown Timer
                                    setCountDownTimer();

                                    // Populate the launch list
                                    finalLaunchNextList.addAll(response.body().getLaunches());

                                    for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                        if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                                            missionListSorted.add(new Mission("TBD", "No Information available"));
                                        }
                                        missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                                    }

                                    recyclerView = findViewById(R.id.recycler_view_notice_list);
                                    launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches(), missionListSorted);

                                    // Setup layout manager
                                    int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                    LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(launchNextAdapter);

                                    // Remove refresh button when done
                                    launchSwipeRefreshLayout.setRefreshing(false);
                                }

                                @Override
                                public void onFailure(Call<LaunchNextList> call, Throwable t) {
                                    ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();
                                    ArrayList<Mission> launchNextMissionFailureList = new ArrayList<>();

                                    recyclerView = findViewById(R.id.recycler_view_notice_list);
                                    launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList, launchNextMissionFailureList);

                                    // Setup layout manager
                                    int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                    LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(launchNextAdapter);

                                    // Show Toast
                                    Toast.makeText(MainActivity.this,
                                            "Unable to load the list.\nPlease check your connection"
                                            , Toast.LENGTH_LONG).show();

                                    // Remove refresh button when done
                                    launchSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } // end if
                        else {
                            GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitRxInstance().create(GetLaunchDataService.class);

                            // Observable agencies
                            Observable<LaunchNextList> obs1 = launchNextService.getLaunchNextListDataWithAgency("121");     // SpaceX
                            Observable<LaunchNextList> obs2 = launchNextService.getLaunchNextListDataWithAgency("31");      // ISRO
                            Observable<LaunchNextList> obs3 = launchNextService.getLaunchNextListDataWithAgency("44");      // NASA
                            Observable<LaunchNextList> obs4 = launchNextService.getLaunchNextListDataWithAgency("115");     // Arianespace
                            Observable<LaunchNextList> obs5 = launchNextService.getLaunchNextListDataWithAgency("37");      // JAXA
                            Observable<LaunchNextList> obs6 = launchNextService.getLaunchNextListDataWithAgency("63");      // ROSCOSMOS
                            Observable<LaunchNextList> obs7 = launchNextService.getLaunchNextListDataWithAgency("88");      // CASC
                            Observable<LaunchNextList> obs8 = launchNextService.getLaunchNextListDataWithAgency("124");     // ULA
                            Observable<LaunchNextList> obs9 = launchNextService.getLaunchNextListDataWithAgency("147");     // Rocketlab Ltd

                            // Create observable
                            Observable<List<LaunchNextList>> result =
                                    Observable.zip(
                                            obs1.subscribeOn(Schedulers.io()),
                                            obs2.subscribeOn(Schedulers.io()),
                                            obs3.subscribeOn(Schedulers.io()),
                                            obs4.subscribeOn(Schedulers.io()),
                                            obs5.subscribeOn(Schedulers.io()),
                                            obs6.subscribeOn(Schedulers.io()),
                                            obs7.subscribeOn(Schedulers.io()),
                                            obs8.subscribeOn(Schedulers.io()),
                                            obs9.subscribeOn(Schedulers.io()),
                                            new Function9<LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, LaunchNextList, List<LaunchNextList>>() {
                                                @Override
                                                public List<LaunchNextList> apply(LaunchNextList launchNextList, LaunchNextList launchNextList2, LaunchNextList launchNextList3, LaunchNextList launchNextList4, LaunchNextList launchNextList5, LaunchNextList launchNextList6, LaunchNextList launchNextList7, LaunchNextList launchNextList8, LaunchNextList launchNextList9) throws Exception {
                                                    List<LaunchNextList> list = new ArrayList<>();
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencySpaceX, false))
                                                        list.add(launchNextList);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyNASA, false))
                                                        list.add(launchNextList2);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyISRO, false))
                                                        list.add(launchNextList3);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyArianespace, false))
                                                        list.add(launchNextList4);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyJAXA, false))
                                                        list.add(launchNextList5);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyROSCOSMOS, false))
                                                        list.add(launchNextList6);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyCASC, false))
                                                        list.add(launchNextList7);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyULA, false))
                                                        list.add(launchNextList8);
                                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterAgencyRocketLabLtd, false))
                                                        list.add(launchNextList9);
                                                    return list;
                                                }
                                            });

                            result.observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new Observer<List<LaunchNextList>>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onNext(List<LaunchNextList> launchNextLists) {
                                            // Debug Toast
                                            // Toast.makeText(MainActivity.this, String.valueOf(launchNextLists.size()), Toast.LENGTH_LONG).show();

                                            for (int i = 0; i < launchNextLists.size(); i++) {
                                                finalLaunchNextList.addAll(launchNextLists.get(i).getLaunches());
                                            }

                                            // Sort the final launch next list based on date
                                            Collections.sort(finalLaunchNextList, new Comparator<Launch>() {
                                                @Override
                                                public int compare(Launch r1, Launch r2) {
                                                    // Get Date from string datetime window start of each launch
                                                    Date launchDate1 = convertJSONStringToDate(r1.getWindowstart());
                                                    Date launchDate2 = convertJSONStringToDate(r2.getWindowstart());

                                                    return launchDate1.compareTo(launchDate2);
                                                }
                                            });

                                            for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                                if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                                                    missionListSorted.add(new Mission("TBD", "No Information available"));
                                                }
                                                missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                                            }

                                            Toast.makeText(MainActivity.this, String.valueOf(missionListSorted.size()), Toast.LENGTH_LONG).show();

                                            // Set next launch string variable with the next launch time from json response
                                            nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();


                                            // Set the Countdown Timer
                                            setCountDownTimer();

                                            recyclerView = findViewById(R.id.recycler_view_notice_list);
                                            launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                                            // Setup layout manager
                                            int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                            LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(launchNextAdapter);

                                            // Remove refresh button when done
                                            launchSwipeRefreshLayout.setRefreshing(false);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // Remove refresh button when done
                                            launchSwipeRefreshLayout.setRefreshing(false);

                                            // Show Toast
                                            Toast.makeText(MainActivity.this,
                                                    "Unable to load the list.\nPlease check your connection"
                                                    , Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    });
                        } // end else
                    }
                }
        );

        // Scheme colors for animation
        launchSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.secondaryLightColor)
        );

    }

    /**
     * This method sets the countdown timer for the launch
     */
    private void setCountDownTimer() {
        // Get the Date of next Launch
        Date nextLaunchTime = convertJSONStringToDate(nextLaunchTimerString);

        // Get the current time
        Date currentTime = Calendar.getInstance().getTime();

        // Get the difference between current time and next launch time
        long diffInMs = nextLaunchTime.getTime() - currentTime.getTime();

        // Debug Toast show difference
        // Toast.makeText(MainActivity.this, String.valueOf(diffInMs), Toast.LENGTH_LONG).show();

        //1000 = 1 second interval
        CountDownTimer cdt = new CountDownTimer(diffInMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                nextLaunchTimerTextView.setText(days + " D " + hours + " H " + minutes + " M " + seconds + " S "); //You can compute the millisUntilFinished on hours/minutes/seconds
            }

            @Override
            public void onFinish() {
                nextLaunchTimerTextView.setText("LAUNCH IS ON");
            }
        };

        // Start Timer
        cdt.start();
    }

    /**
     * This method convert JSON Time Response to Date
     *
     * @param ourDate String of json time response, example: December 13, 2018 04:00:00 UTC
     * @return Date type object of converted json string time response
     */
    private Date convertJSONStringToDate(String ourDate) {
        Date dateResult;
        // Example Date String Response: "December 13, 2018 04:00:00 UTC"
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateResult = formatter.parse(ourDate);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            dateResult = Calendar.getInstance().getTime();
        }
        return dateResult;
    }


}

