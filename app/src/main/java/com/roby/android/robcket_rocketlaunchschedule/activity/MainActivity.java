package com.roby.android.robcket_rocketlaunchschedule.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.roby.android.robcket_rocketlaunchschedule.adapter.LaunchNextAdapter;
import com.roby.android.robcket_rocketlaunchschedule.model.Launch;
import com.roby.android.robcket_rocketlaunchschedule.model.LaunchNextList;
import com.roby.android.robcket_rocketlaunchschedule.model.Mission;
import com.roby.android.robcket_rocketlaunchschedule.my_interface.GetLaunchDataService;
import com.roby.android.robcket_rocketlaunchschedule.receiver.AlarmReceiver;
import com.roby.android.robcket_rocketlaunchschedule.utils.GlobalConstants;
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

import com.roby.android.robcket_rocketlaunchschedule.R;
import com.roby.android.robcket_rocketlaunchschedule.network.RetrofitInstance;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    private TextView nextLaunchLabelTextView;
    private TextView emptyDataTextView;
    private Drawer result;
    private LottieAnimationView loadingListAnimationView;
    private LottieAnimationView notFoundAnimationView;
    private LottieAnimationView networkLostAnimationView;

    private String nextLaunchTimerString;

    private ArrayList<Launch> finalLaunchNextList = new ArrayList<>();
    private ArrayList<Mission> missionListSorted = new ArrayList<>();

    private SharedPreferences filterSettingsSharedPreferences;


    // Notification related variables

    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Show Splash Screen at the start of the app
        setTheme(R.style.AppTheme_NoActionBar);

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

        // Set Next Launch Timer TextView
        nextLaunchTimerTextView = findViewById(R.id.textview_next_launch_timer);
        nextLaunchLabelTextView = findViewById(R.id.textview_next_launch);

        // Set Empty Textview for data not found
        emptyDataTextView = findViewById(R.id.textview_no_data);

        // Set Animation View
        loadingListAnimationView = findViewById(R.id.animation_view);
        networkLostAnimationView = findViewById(R.id.animation_view_network_lost);
        notFoundAnimationView = findViewById(R.id.animation_view_not_found);

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

        // Set the Notification Function
        // setNextLaunchNotification();

        // Create Notification Channel for Oreo (API >= 27)
        createNotificationChannel();

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
        } else if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutMeActivity.class);
            startActivity(aboutIntent);
            return true;
        } else if (id == R.id.action_licenses) {
            Intent licenseIntent = new Intent(this, LicenseActivity.class);
            startActivity(licenseIntent);
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

                    // Populate the launch list
                    if (response.body() != null) {
                        finalLaunchNextList.addAll(response.body().getLaunches());
                    }

                    // Remove unconfirmed launches if checked in filter
                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.unconfirmedSwitchPref, false)) {
                        List<Launch> confirmedLaunches = new ArrayList<>();

                        for (int i = 0; i < finalLaunchNextList.size(); i++) {
                            // Confirmed launches has tbddate = 0
                            if (finalLaunchNextList.get(i).getTbddate() == 0) {
                                confirmedLaunches.add(finalLaunchNextList.get(i));
                            }
                        }
                        // Remove all elements in the list
                        finalLaunchNextList.clear();

                        // Add all elements in confirmed launches list in the empty final launch next list
                        finalLaunchNextList.addAll(confirmedLaunches);
                    }

                    // Filter the launch by location
                    filterByLocation();

                    // Populate the mission list
                    for (int i = 0; i < finalLaunchNextList.size(); i++) {
                        if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                            missionListSorted.add(new Mission("TBD", "No Information available"));
                        }
                        missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                    }

                    // If finalLaunchNextList is empty then show textview empty data
                    if (!finalLaunchNextList.isEmpty()) {
                        // Set next launch string variable with the next launch time from json response
                        nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();

                        // Set the Countdown Timer
                        setCountDownTimer();

                        // Set the Notification Function
                        setNextLaunchNotification();

                        recyclerView = findViewById(R.id.recycler_view_notice_list);
                        launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                        // Setup layout manager
                        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                        LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(launchNextAdapter);

                        // Hide empty textview if data is available
                        emptyDataTextView.setVisibility(View.INVISIBLE);
                    } else {
                        // Show empty textview if data is unavailable
                        emptyDataTextView.setVisibility(View.VISIBLE);

                        // Show AnimationView for not found result
                        notFoundAnimationView.setVisibility(View.VISIBLE);
                    }

                    // Hide AnimationView for loading after finished loading
                    loadingListAnimationView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<LaunchNextList> call, Throwable t) {
                    ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();
                    ArrayList<Mission> launchNextMissionFailureList = new ArrayList<>();

                    //TODO handle notification without internet connection

                    recyclerView = findViewById(R.id.recycler_view_notice_list);
                    launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList, launchNextMissionFailureList);

                    // Setup layout manager
                    int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                    LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                    recyclerView.setLayoutManager(layoutManager);

                    // Handles RecyclerView stutter scrolling
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setItemViewCacheSize(20);
                    recyclerView.setDrawingCacheEnabled(true);

                    recyclerView.setAdapter(launchNextAdapter);

                    // Show Toast
                    Toast.makeText(MainActivity.this,
                            "Unable to load the list.\nPlease check your connection"
                            , Toast.LENGTH_LONG).show();

                    // Show Animation View Network not found
                    networkLostAnimationView.setVisibility(View.VISIBLE);

                    // Hide AnimationView Loading
                    loadingListAnimationView.setVisibility(View.GONE);

                    // Hide AnimationView Not found
                    notFoundAnimationView.setVisibility(View.GONE);
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

                            // Remove unconfirmed launches if checked in filter
                            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.unconfirmedSwitchPref, false)) {
                                List<Launch> confirmedLaunches = new ArrayList<>();

                                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                    // Confirmed launches has tbddate = 0
                                    if (finalLaunchNextList.get(i).getTbddate() == 0) {
                                        confirmedLaunches.add(finalLaunchNextList.get(i));
                                    }
                                }
                                // Remove all elements in the list
                                finalLaunchNextList.clear();

                                // Add all elements in confirmed launches list in the empty final launch next list
                                finalLaunchNextList.addAll(confirmedLaunches);
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

                            // Filter the launch by location
                            filterByLocation();

                            for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                                    missionListSorted.add(new Mission("TBD", "No Information available"));
                                }
                                missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                            }

                            //Toast.makeText(MainActivity.this, String.valueOf(missionListSorted.size()), Toast.LENGTH_LONG).show();

                            // If finalLaunchNextList is empty then show textview empty data
                            if (!finalLaunchNextList.isEmpty()) {
                                // Set next launch string variable with the next launch time from json response
                                nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();

                                // Set the Countdown Timer
                                setCountDownTimer();

                                // Set the Notification Function
                                setNextLaunchNotification();

                                recyclerView = findViewById(R.id.recycler_view_notice_list);
                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                                // Setup layout manager
                                int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                recyclerView.setLayoutManager(layoutManager);

                                // Handles RecyclerView stutter scrolling
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setItemViewCacheSize(20);
                                recyclerView.setDrawingCacheEnabled(true);

                                recyclerView.setAdapter(launchNextAdapter);
                                // Hide empty textview if data is available
                                emptyDataTextView.setVisibility(View.INVISIBLE);
                            } else {
                                // Show empty textview if data is unavailable
                                emptyDataTextView.setVisibility(View.VISIBLE);

                                // Show AnimationView for result not found
                                notFoundAnimationView.setVisibility(View.VISIBLE);
                            }

                            // Hide AnimationView for loading after finished loading
                            loadingListAnimationView.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // Show Toast
                            Toast.makeText(MainActivity.this,
                                    "Unable to load the list.\nPlease check your connection"
                                    , Toast.LENGTH_LONG).show();

                            // Show AnimationView Network not found
                            networkLostAnimationView.setVisibility(View.VISIBLE);

                            // Hide AnimationView Loading
                            loadingListAnimationView.setVisibility(View.GONE);

                            // Hide AnimationView Not found
                            notFoundAnimationView.setVisibility(View.GONE);
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
                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.notificationSwitchPref, false))
                .withOnCheckedChangeListener(onCheckedChangeListener)
                .withSelectable(false);

        SwitchDrawerItem itemUnconfirmed = new SwitchDrawerItem()
                .withIdentifier(123)
                .withName("Hide unconfirmed launches")
                .withDescriptionTextColor(getResources().getColor(R.color.material_drawer_header_selection_subtext))
                .withIcon(FontAwesome.Icon.faw_check_circle)
                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.unconfirmedSwitchPref, false))
                .withOnCheckedChangeListener(onCheckedChangeListener)
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


        //Location Filter List
        ExpandableDrawerItem itemFilterLocation = new ExpandableDrawerItem()
                .withIdentifier(3)
                .withName("Filter by locations")
                .withIcon(FontAwesome.Icon.faw_globe_americas)
                .withSelectable(false)
                .withSubItems(
                        new SwitchDrawerItem().withName("Jiuquan, China").withLevel(2).withIdentifier(13)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationJiuquan, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Taiyuan, China").withLevel(2).withIdentifier(14)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTaiyuan, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kourou, French Guiana").withLevel(2).withIdentifier(15)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKourou, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Hammaguir, Algeria").withLevel(2).withIdentifier(16)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationHammaguir, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Sriharikota, India").withLevel(2).withIdentifier(17)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSriharikota, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Semnan, Iran").withLevel(2).withIdentifier(18)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSemnan, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kenya").withLevel(2).withIdentifier(19)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKenya, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kagoshima, Japan").withLevel(2).withIdentifier(20)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKagoshima, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Tanegashima, Japan").withLevel(2).withIdentifier(21)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTanegashima, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Baikonur Cosmodrome, Kazakhstan").withLevel(2).withIdentifier(22)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationBaikonur, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Plesetsk Cosmodrome, Russia").withLevel(2).withIdentifier(23)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPlesetsk, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kapustin Yar, Russia").withLevel(2).withIdentifier(24)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKapustin, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Svobodney Cosmodrome, Russia").withLevel(2).withIdentifier(25)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSvobodney, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Dombarovskiy, Russia").withLevel(2).withIdentifier(26)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationDombarovskiy, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Sea Launch").withLevel(2).withIdentifier(27)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSea, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Cape Canaveral, USA").withLevel(2).withIdentifier(28)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationCape, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kennedy Space Center, USA").withLevel(2).withIdentifier(29)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKennedy, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Vandenberg AFB, USA").withLevel(2).withIdentifier(30)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationVandenberg, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Wallops Island, USA").withLevel(2).withIdentifier(31)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWallops, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Woomera, Australia").withLevel(2).withIdentifier(32)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWoomera, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kiatorete Spit, New Zealand").withLevel(2).withIdentifier(33)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKiatorete, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Xichang, China").withLevel(2).withIdentifier(34)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationXichang, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Negev, Israel").withLevel(2).withIdentifier(35)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNegev, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Palmachim Airbase, Israel").withLevel(2).withIdentifier(36)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPalmachim, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kauai, USA").withLevel(2).withIdentifier(37)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKauai, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Ohae, Korea").withLevel(2).withIdentifier(38)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationOhae, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Naro Space Center, South Korea").withLevel(2).withIdentifier(39)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNaro, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Kodiak, USA").withLevel(2).withIdentifier(40)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKodiak, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Wenchang, China").withLevel(2).withIdentifier(41)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWenchang, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener),
                        new SwitchDrawerItem().withName("Unknown Location").withLevel(2).withIdentifier(42)
                                .withChecked(filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationUnknown, false))
                                .withSelectable(false).withOnCheckedChangeListener(onCheckedChangeListener)

                );

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
                        itemUnconfirmed,
                        new DividerDrawerItem(),
                        itemFilterAgency,
                        new DividerDrawerItem(),
                        itemFilterLocation
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
                    // Notification
                    case 1:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.notificationSwitchPref, isChecked).apply();
                        break;
                    // Confirm Launch
                    case 123:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.unconfirmedSwitchPref, isChecked).apply();
                        break;
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
                    // Jiuquan
                    case 13:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationJiuquan, isChecked).apply();
                        break;
                    // Taiyuan
                    case 14:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationTaiyuan, isChecked).apply();
                        break;
                    // Kourou
                    case 15:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKourou, isChecked).apply();
                        break;
                    // Hammaguir
                    case 16:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationHammaguir, isChecked).apply();
                        break;
                    // Sriharikota
                    case 17:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationSriharikota, isChecked).apply();
                        break;
                    // Semnan
                    case 18:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationSemnan, isChecked).apply();
                        break;
                    // Kenya
                    case 19:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKenya, isChecked).apply();
                        break;
                    // Kagoshima
                    case 20:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKagoshima, isChecked).apply();
                        break;
                    // Tanegashima
                    case 21:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationTanegashima, isChecked).apply();
                        break;
                    // Baikonur
                    case 22:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationBaikonur, isChecked).apply();
                        break;
                    // Plesetsk
                    case 23:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationPlesetsk, isChecked).apply();
                        break;
                    // Kapustin
                    case 24:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKapustin, isChecked).apply();
                        break;
                    // Svobodney
                    case 25:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationSvobodney, isChecked).apply();
                        break;
                    // Dombarovskiy
                    case 26:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationDombarovskiy, isChecked).apply();
                        break;
                    // Sea Launch
                    case 27:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationSea, isChecked).apply();
                        break;
                    // Cape
                    case 28:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationCape, isChecked).apply();
                        break;
                    // Kennedy
                    case 29:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKennedy, isChecked).apply();
                        break;
                    // Vandenberg
                    case 30:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationVandenberg, isChecked).apply();
                        break;
                    // Wallops
                    case 31:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationWallops, isChecked).apply();
                        break;
                    // Woomera
                    case 32:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationWoomera, isChecked).apply();
                        break;
                    // Kiatorete
                    case 33:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKiatorete, isChecked).apply();
                        break;
                    // Xichang
                    case 34:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationXichang, isChecked).apply();
                        break;
                    // Negev
                    case 35:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationNegev, isChecked).apply();
                        break;
                    // Palmachim
                    case 36:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationPalmachim, isChecked).apply();
                        break;
                    // Kauai
                    case 37:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKauai, isChecked).apply();
                        break;
                    // Ohae
                    case 38:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationOhae, isChecked).apply();
                        break;
                    // Naro
                    case 39:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationNaro, isChecked).apply();
                        break;
                    // Kodiak
                    case 40:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationKodiak, isChecked).apply();
                        break;
                    // Wenchang
                    case 41:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationWenchang, isChecked).apply();
                        break;
                    // Unknown
                    case 42:
                        filterSettingsSharedPreferences.edit().putBoolean(GlobalConstants.filterLocationUnknown, isChecked).apply();
                        break;
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

                                    // Populate the launch list
                                    if (response.body() != null) {
                                        finalLaunchNextList.addAll(response.body().getLaunches());
                                    }

                                    // Remove unconfirmed launches if checked in filter
                                    if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.unconfirmedSwitchPref, false)) {
                                        List<Launch> confirmedLaunches = new ArrayList<>();

                                        for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                            // Confirmed launches has tbddate = 0
                                            if (finalLaunchNextList.get(i).getTbddate() == 0) {
                                                confirmedLaunches.add(finalLaunchNextList.get(i));
                                            }
                                        }
                                        // Remove all elements in the list
                                        finalLaunchNextList.clear();

                                        // Add all elements in confirmed launches list in the empty final launch next list
                                        finalLaunchNextList.addAll(confirmedLaunches);
                                    }

                                    // Filter the launch by location
                                    filterByLocation();

                                    for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                        if (finalLaunchNextList.get(i).getMissions().isEmpty()) {
                                            missionListSorted.add(new Mission("TBD", "No Information available"));
                                        }
                                        missionListSorted.addAll(finalLaunchNextList.get(i).getMissions());
                                    }

                                    // If finalLaunchNextList is empty then show textview empty data
                                    if (!finalLaunchNextList.isEmpty()) {
                                        // Set next launch string variable with the next launch time from json response
                                        nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();

                                        // Set the Countdown Timer
                                        setCountDownTimer();

                                        // Set the Notification Function
                                        setNextLaunchNotification();

                                        recyclerView = findViewById(R.id.recycler_view_notice_list);
                                        launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                                        // Setup layout manager
                                        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                        LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                        recyclerView.setLayoutManager(layoutManager);

                                        // Handles RecyclerView stutter scrolling
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setItemViewCacheSize(20);
                                        recyclerView.setDrawingCacheEnabled(true);

                                        recyclerView.setAdapter(launchNextAdapter);
                                        // Hide empty textview if data is available
                                        emptyDataTextView.setVisibility(View.INVISIBLE);
                                    } else {
                                        // Show empty textview if data is unavailable
                                        emptyDataTextView.setVisibility(View.VISIBLE);

                                        // Show AnimationView for result not found
                                        notFoundAnimationView.setVisibility(View.VISIBLE);
                                    }

                                    // Remove loading refresh button after finished refresh
                                    launchSwipeRefreshLayout.setRefreshing(false);

                                    // Hide AnimationView for loading after finished loading
                                    loadingListAnimationView.setVisibility(View.INVISIBLE);
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

                                    // Handles RecyclerView stutter scrolling
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setItemViewCacheSize(20);
                                    recyclerView.setDrawingCacheEnabled(true);

                                    recyclerView.setAdapter(launchNextAdapter);

                                    // Show Toast
                                    Toast.makeText(MainActivity.this,
                                            "Unable to load the list.\nPlease check your connection"
                                            , Toast.LENGTH_LONG).show();

                                    // Remove refresh button when done
                                    launchSwipeRefreshLayout.setRefreshing(false);

                                    // Show AnimationView Network not found
                                    networkLostAnimationView.setVisibility(View.VISIBLE);

                                    // Hide AnimationView Loading
                                    loadingListAnimationView.setVisibility(View.GONE);

                                    // Hide AnimationView Not found
                                    notFoundAnimationView.setVisibility(View.GONE);
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

                                            // Remove unconfirmed launches if checked in filter
                                            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.unconfirmedSwitchPref, false)) {
                                                List<Launch> confirmedLaunches = new ArrayList<>();

                                                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                                                    // Confirmed launches has tbddate = 0
                                                    if (finalLaunchNextList.get(i).getTbddate() == 0) {
                                                        confirmedLaunches.add(finalLaunchNextList.get(i));
                                                    }
                                                }
                                                // Remove all elements in the list
                                                finalLaunchNextList.clear();

                                                // Add all elements in confirmed launches list in the empty final launch next list
                                                finalLaunchNextList.addAll(confirmedLaunches);
                                            }

                                            // Filter the launch by location
                                            filterByLocation();

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

                                            // Debug Toast
                                            // Toast.makeText(MainActivity.this, String.valueOf(missionListSorted.size()), Toast.LENGTH_LONG).show();

                                            // If finalLaunchNextList is empty then show textview empty data
                                            if (!finalLaunchNextList.isEmpty()) {
                                                // Set next launch string variable with the next launch time from json response
                                                nextLaunchTimerString = finalLaunchNextList.get(0).getWindowstart();

                                                // Set the Countdown Timer
                                                setCountDownTimer();

                                                // Set the Notification Function
                                                setNextLaunchNotification();

                                                recyclerView = findViewById(R.id.recycler_view_notice_list);
                                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, finalLaunchNextList, missionListSorted);

                                                // Setup layout manager
                                                int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
                                                LinearLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, gridColumnCount);
                                                recyclerView.setLayoutManager(layoutManager);

                                                // Handles RecyclerView stutter scrolling
                                                recyclerView.setHasFixedSize(true);
                                                recyclerView.setItemViewCacheSize(20);
                                                recyclerView.setDrawingCacheEnabled(true);

                                                recyclerView.setAdapter(launchNextAdapter);
                                                // Hide empty textview if data is available
                                                emptyDataTextView.setVisibility(View.INVISIBLE);
                                            } else {
                                                // Show empty textview if data is unavailable
                                                emptyDataTextView.setVisibility(View.VISIBLE);

                                                // Show AnimationView for result not found
                                                notFoundAnimationView.setVisibility(View.VISIBLE);
                                            }

                                            // Remove refresh button when done
                                            launchSwipeRefreshLayout.setRefreshing(false);

                                            // Hide AnimationView for loading after finished loading
                                            loadingListAnimationView.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // Remove refresh button when done
                                            launchSwipeRefreshLayout.setRefreshing(false);

                                            // Show Toast
                                            Toast.makeText(MainActivity.this,
                                                    "Unable to load the list.\nPlease check your connection"
                                                    , Toast.LENGTH_LONG).show();

                                            // Show Animation Network not found
                                            networkLostAnimationView.setVisibility(View.VISIBLE);

                                            // Hide AnimationView Loading
                                            loadingListAnimationView.setVisibility(View.GONE);

                                            // Hide AnimationView Not found
                                            notFoundAnimationView.setVisibility(View.GONE);
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
        // Set Timer TextView to visible
        nextLaunchLabelTextView.setVisibility(View.VISIBLE);
        nextLaunchTimerTextView.setVisibility(View.VISIBLE);

        // Get the Date of next Launch
        Date nextLaunchTime = convertJSONStringToDate(nextLaunchTimerString);

        // Get the current time
        Date currentTime = Calendar.getInstance().getTime();

        // Debug Toast for nextLaunchTime
        // Toast.makeText(MainActivity.this, currentTime.toString(), Toast.LENGTH_LONG).show();

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

                // You can compute the millisUntilFinished on hours/minutes/seconds
                // Hide days, hours and minutes text if zero
                if (days == 0) {
                    // Set launch timer TextView
                    nextLaunchTimerTextView.setText(hours + " H : " + minutes + " M : " + seconds + " S ");
                } else if (days == 0 && hours == 0) {
                    // Set launch timer TextView
                    nextLaunchTimerTextView.setText(minutes + " M : " + seconds + " S ");
                } else if (days == 0 && hours == 0 && minutes == 0) {
                    // Set launch timer TextView
                    nextLaunchTimerTextView.setText(seconds + " S ");
                } else {
                    // Set launch timer TextView
                    nextLaunchTimerTextView.setText(days + " D : " + hours + " H : " + minutes + " M : " + seconds + " S ");
                }
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

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "Robket - Next Space Launch Schedule",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    ("Robket is an app to see next rocket launches in the world");

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


    /**
     * This method sets the next launches notification
     */
    private void setNextLaunchNotification() {
        // Get the Date of next Launch
        Date nextLaunchTime = convertJSONStringToDate(nextLaunchTimerString);

        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService
                (ALARM_SERVICE);

        // Set the alarm to start at launch time
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.setTime(nextLaunchTime);

//        firingCal.setTimeInMillis(System.currentTimeMillis());
//        firingCal.set(Calendar.HOUR_OF_DAY, 22);
//        firingCal.set(Calendar.MINUTE, 8);
//        firingCal.set(Calendar.DAY_OF_MONTH, 26);
//        firingCal.set(Calendar.MONTH, 12);
//        firingCal.set(Calendar.YEAR, 2018);
//        firingCal.set(2018, 11, 26, 22, 13,0);

        long intendedTime = firingCal.getTimeInMillis();
        long currentTime = currentCal.getTimeInMillis();

        if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.notificationSwitchPref, false) && intendedTime >= currentTime) {
            // If the Switch is turned on, set the alarm based on next launch time
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC, intendedTime, notifyPendingIntent);
            }
        } else {
            // Cancel notification if the Switch is turned off.
            mNotificationManager.cancelAll();

            if (alarmManager != null) {
                alarmManager.cancel(notifyPendingIntent);
            }
        }
    }

    /**
     * This method handles filter by location inside generateLaunchList method
     */
    private void filterByLocation() {
        // If at least a filter by location is checked then do the filter by location
        if ((filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationJiuquan, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTaiyuan, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKourou, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationHammaguir, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSriharikota, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSemnan, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKenya, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKagoshima, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTanegashima, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationBaikonur, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPlesetsk, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKapustin, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSvobodney, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSea, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationCape, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKennedy, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationVandenberg, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWallops, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWoomera, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKiatorete, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationXichang, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNegev, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPalmachim, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKauai, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationOhae, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNaro, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKodiak, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWenchang, false) ||
                filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationUnknown, false)
        )) {
            // Create an empty ArrayList Launch
            List<Launch> tempFinalLaunchList = new ArrayList<>();

            // Jiuquan
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationJiuquan, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Jiuquan, People's Republic of China"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }
            // Taiyuan
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTaiyuan, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Taiyuan, People's Republic of China"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }
            // Kourou
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKourou, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kourou, French Guiana"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Hammaguir
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationHammaguir, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Hammaguir, Algeria"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Sriharikota
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSriharikota, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Sriharikota, Republic of India"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Semnan
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSemnan, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Semnan Space Center, Islamic Republic of Iran"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kenya
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKenya, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kenya"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kagoshima
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKagoshima, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kagoshima, Japan"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Tanegashima
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationTanegashima, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Tanegashima, Japan"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Baikonur
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationBaikonur, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Baikonur Cosmodrome, Republic of Kazakhstan"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Plestsk
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPlesetsk, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Plesetsk Cosmodrome, Russian Federation"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kapustin
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKapustin, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kapustin Yar, Russian Federation"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Svobodney
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSvobodney, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Svobodney Cosmodrome, Russian Federation"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Dombarovskiy
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationDombarovskiy, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Dombarovskiy, Russian Federation"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            //Sea Launch
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationSea, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Sea Launch"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Cape
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationCape, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Cape Canaveral, FL, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kennedy
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKennedy, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kennedy Space Center, FL, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Vandenberg
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationVandenberg, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Vandenberg AFB, CA, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Wallops
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWallops, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Wallops Island, Virginia, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Woomera
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWoomera, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Woomera, Australia"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            //Kiatorete
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKiatorete, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kiatorete Spit, New Zealand"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Xichang
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationXichang, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Xichang Satellite Launch Center, People's Republic of China"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Negev
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNegev, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Negev, State of Israel"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Palmachim
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationPalmachim, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Palmachim Airbase, State of Israel"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kauai
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKauai, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kauai, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Ohae
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationOhae, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Ohae Satellite Launching station, Democratic People's Republic of Korea"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Naro
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationNaro, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Naro Space Center, South Korea"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Kodiak
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationKodiak, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Kodiak Launch Complex, Alaska, USA"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Wenchang
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationWenchang, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Wenchang Satellite Launch Center, People's Republic of China"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Unknown
            if (filterSettingsSharedPreferences.getBoolean(GlobalConstants.filterLocationUnknown, false))
                for (int i = 0; i < finalLaunchNextList.size(); i++) {
                    if (finalLaunchNextList.get(i).getLocation().getName().equals("Unknown Location"))
                        tempFinalLaunchList.add(finalLaunchNextList.get(i));
                }

            // Empty List
            finalLaunchNextList.clear();

            // Add all elements from tempFinalLaunchList to finalLaunchList
            finalLaunchNextList.addAll(tempFinalLaunchList);
        }
    }

}

