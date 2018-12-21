package com.example.android.robcket_rocketlaunchschedule.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.adapter.LaunchNextAdapter;
import com.example.android.robcket_rocketlaunchschedule.model.Launch;
import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;
import com.example.android.robcket_rocketlaunchschedule.model.Mission;
import com.example.android.robcket_rocketlaunchschedule.my_interface.GetLaunchDataService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.network.RetrofitInstance;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
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
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private WelcomeHelper welcomeHelper;
    private LaunchNextAdapter launchNextAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout launchSwipeRefreshLayout;
    private TextView nextLaunchTimerTextView;
    private Drawer result;

    private String nextLaunchTimerString;

    private ArrayList<Launch> finalLaunchNextList = new ArrayList<>();


    /**
     * Filter Variable
     */
    // Value of notification switch
    private Boolean notificationSwitchPref;
    // Value of filter NASA
    private Boolean filterAgencyNASA;
    // Value of filter SpaceX
    private Boolean filterAgencySpaceX;
    // Value of filter ULA
    private Boolean filterAgencyULA;
    // Value of filter ROSCOSMOS
    private Boolean filterAgencyROSCOSMOS;
    // Value of filter JAXA
    private Boolean filterAgencyJAXA;
    // Value of filter Arianespace
    private Boolean filterAgencyArianespace;
    // Value of filter CASC
    private Boolean filterAgencyCASC;
    // Value of filter ISRO
    private Boolean filterAgencyISRO;
    // Value of filter RocketLabLtd
    private Boolean filterAgencyRocketLabLtd;


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


        finalLaunchNextList = new ArrayList<>();

        // Applies shared preferences values in SettingsFilter
        applySettingsFilter();

        // Set Next Launch Timer TextView
        nextLaunchTimerTextView = findViewById(R.id.textview_next_launch_timer);

        // SwipeRefreshLayout
        launchSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        // Set Refresh Listener to launchSwipeRefreshLayout
        // setRocketSwipeRefreshLayout();

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

        GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitRxInstance().create(GetLaunchDataService.class);

        // Observable agencies
        Observable<LaunchNextList> obs1 = launchNextService.getLaunchNextListDataWithAgency("121");     // SpaceX
        Observable<LaunchNextList> obs2 = launchNextService.getLaunchNextListDataWithAgency("31");      // ISRO
        Observable<LaunchNextList> obs3 = launchNextService.getLaunchNextListDataWithAgency("44");      // NASA

        Observable<List<LaunchNextList>> result =
                Observable.zip(
                        obs1.subscribeOn(Schedulers.io()),
                        obs2.subscribeOn(Schedulers.io()),
                        obs3.subscribeOn(Schedulers.io()),
                        new Function3<LaunchNextList, LaunchNextList, LaunchNextList, List<LaunchNextList>>() {
                            @Override
                            public List<LaunchNextList> apply(LaunchNextList type1, LaunchNextList type2, LaunchNextList type3) {
                                List<LaunchNextList> list = new ArrayList();
                                list.add(type1);
                                list.add(type2);
                                list.add(type3);
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

                        //Create ArrayList for Mission List
                        ArrayList<Mission> missionListSorted = new ArrayList<>();

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
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(launchNextAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


//        // Create handle for the RetrofitInstance interface
//        GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);
//
//        // Call the method with parameter in the interface to get the notice data
//        Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();
//
//        launchNextCall.enqueue(new Callback<LaunchNextList>() {
//            @Override
//            public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
//                // Set next launch string variable with the next launch time from json response
//                nextLaunchTimerString = response.body().getLaunches().get(0).getWindowstart();
//
//                // Set the Countdown Timer
//                setCountDownTimer();
//
//                recyclerView = findViewById(R.id.recycler_view_notice_list);
//                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches());
//
//                // Setup layout manager
//                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//                recyclerView.setLayoutManager(layoutManager);
//                recyclerView.setAdapter(launchNextAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<LaunchNextList> call, Throwable t) {
//                ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();
//
//                recyclerView = findViewById(R.id.recycler_view_notice_list);
//                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList);
//
//                // Setup layout manager
//                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//                recyclerView.setLayoutManager(layoutManager);
//                recyclerView.setAdapter(launchNextAdapter);
//
//                // Show Toast
//                Toast.makeText(MainActivity.this,
//                        "Unable to load the list.\nPlease check your connection"
//                        , Toast.LENGTH_LONG).show();
//            }
//        });
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
        PrimaryDrawerItem itemHome = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.drawer_item_home)
                .withIcon(FontAwesome.Icon.faw_home);
        PrimaryDrawerItem itemRocket = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.drawer_item_rocket)
                .withIcon(FontAwesome.Icon.faw_space_shuttle);
        PrimaryDrawerItem itemFilter = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.drawer_item_filter)
                .withIcon(FontAwesome.Icon.faw_filter);
        SecondaryDrawerItem itemSettings = new SecondaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.drawer_item_settings)
                .withIcon(FontAwesome.Icon.faw_cogs);
        SecondaryDrawerItem itemHelp = new SecondaryDrawerItem()
                .withIdentifier(5)
                .withName(R.string.drawer_item_help)
                .withIcon(FontAwesome.Icon.faw_question);
        SecondaryDrawerItem itemAbout = new SecondaryDrawerItem()
                .withIdentifier(6)
                .withName(R.string.drawer_item_about)
                .withIcon(FontAwesome.Icon.faw_user);


        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withFullscreen(true)
                .withDrawerGravity(Gravity.END)
                //.withDrawerWidthPx(matchParentWidth)
                //.withToolbar(toolbar)
                .addDrawerItems(
                        itemHome,
                        itemRocket,
                        itemFilter,
                        new DividerDrawerItem(),
                        itemSettings,
                        itemHelp,
                        itemAbout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position) {
                            case 1:
                                Intent homeIntent = new Intent(view.getContext(), MainActivity.class);
                                startActivity(homeIntent);
                                break;
                            case 2:
                                Intent rocketIntent = new Intent(view.getContext(), RocketActivity.class);
                                startActivity(rocketIntent);
                                break;
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
     * Sets up a SwipeRefreshLayout.OnRefreshListener and Refresh signal colors that is invoked when the user
     * performs a swipe-to-refresh gesture.
     */
//    private void setRocketSwipeRefreshLayout() {
//        launchSwipeRefreshLayout.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        // Your code to refresh the list here.
//                        // Make sure you call swipeContainer.setRefreshing(false)
//                        // once the network request has completed successfully.
//                        // Create handle for the RetrofitInstance interface
//                        GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);
//
//                        // Call the method with parameter in the interface to get the notice data
//                        Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();
//
//                        launchNextCall.enqueue(new Callback<LaunchNextList>() {
//                            @Override
//                            public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
//                                // Clear the rocketList to avoid duplication, if the rocket list has already populated
//                                launchNextAdapter.clear();
//
//                                // Set next launch string variable with the next launch time from json response
//                                nextLaunchTimerString = response.body().getLaunches().get(0).getWindowstart();
//
//                                // Set the Countdown Timer
//                                setCountDownTimer();
//
//                                recyclerView = findViewById(R.id.recycler_view_notice_list);
//                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches());
//
//                                // Setup layout manager
//                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//                                recyclerView.setLayoutManager(layoutManager);
//                                recyclerView.setAdapter(launchNextAdapter);
//
//                                // Remove the refresh signal after finished
//                                launchSwipeRefreshLayout.setRefreshing(false);
//                            }
//
//                            @Override
//                            public void onFailure(Call<LaunchNextList> call, Throwable t) {
//                                // Clear the rocketList to avoid duplication, if the rocket list has already populated
//                                launchNextAdapter.clear();
//
//                                ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();
//
//                                recyclerView = findViewById(R.id.recycler_view_notice_list);
//                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList);
//
//                                // Setup layout manager
//                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
//                                recyclerView.setLayoutManager(layoutManager);
//                                recyclerView.setAdapter(launchNextAdapter);
//
//                                // Show Toast
//                                Toast.makeText(MainActivity.this,
//                                        "Unable to load the list.\nPlease check your connection"
//                                        , Toast.LENGTH_LONG).show();
//
//                                // Remove the refresh signal after finished
//                                launchSwipeRefreshLayout.setRefreshing(false);
//                            }
//                        });
//                    }
//                }
//        );
//
//        // Scheme colors for animation
//        launchSwipeRefreshLayout.setColorSchemeColors(
//                getResources().getColor(R.color.secondaryLightColor)
//        );
//
//    }

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

    /**
     * This methods applies values inside of SettingsFilter in MainActivity
     */
    private void applySettingsFilter() {
        // Save the default values in shared preferences for Settings & Filter
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Gets default value of shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Value of notification switch
        notificationSwitchPref = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_NOTIFICATION_SWITCH, false);

        // Value of filter NASA
        filterAgencyNASA = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_NASA_CHECKBOX, false);

        // Value of filter SpaceX
        filterAgencySpaceX = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_SPACEX_CHECKBOX, false);

        // Value of filter ULA
        filterAgencyULA = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_ULA_CHECKBOX, false);

        // Value of filter ROSCOSMOS
        filterAgencyROSCOSMOS = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_ROSCOSMOS_CHECKBOX, false);

        // Value of filter JAXA
        filterAgencyJAXA = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_JAXA_CHECKBOX, false);

        // Value of filter Arianespace
        filterAgencyArianespace = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_ARIANESPACE_CHECKBOX, false);

        // Value of filter CASC
        filterAgencyCASC = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_CASC_CHECKBOX, false);

        // Value of filter ISRO
        filterAgencyISRO = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_ISRO_CHECKBOX, false);

        // Value of filter RocketLabLtd
        filterAgencyRocketLabLtd = sharedPreferences.getBoolean(SettingsFilterActivity.KEY_PREF_FILTER_ROCKETLABLTD_CHECKBOX, false);

        // Debug Toast
        // Toast.makeText(this, filterAgencyAll.toString(), Toast.LENGTH_LONG).show();
    }



}

