package com.example.android.robcket_rocketlaunchschedule.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.android.robcket_rocketlaunchschedule.adapter.LaunchNextAdapter;
import com.example.android.robcket_rocketlaunchschedule.model.Launch;
import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;
import com.example.android.robcket_rocketlaunchschedule.my_interface.GetLaunchDataService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private WelcomeHelper welcomeHelper;
    private LaunchNextAdapter launchNextAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout launchSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_tutorial) {
            welcomeHelper.forceShow();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to generate List of notice using RecyclerView with custom adapter
     */
    private void generateLaunchList() {
        // Create handle for the RetrofitInstance interface
        GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);

        // Call the method with parameter in the interface to get the notice data
        Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();

        launchNextCall.enqueue(new Callback<LaunchNextList>() {
            @Override
            public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
                recyclerView = findViewById(R.id.recycler_view_notice_list);
                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches());

                // Setup layout manager
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(launchNextAdapter);
            }

            @Override
            public void onFailure(Call<LaunchNextList> call, Throwable t) {
                ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();

                recyclerView = findViewById(R.id.recycler_view_notice_list);
                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList);

                // Setup layout manager
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(launchNextAdapter);

                // Show Toast
                Toast.makeText(MainActivity.this,
                        "Unable to load the list.\nPlease check your connection"
                        , Toast.LENGTH_LONG).show();
            }
        });
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
                .withTextColor(getResources().getColor(R.color.material_drawer_dark_primary_text))
                .addProfiles(
                        new ProfileDrawerItem().withName("Robcket")
                                .withEmail("Rocket Launcher Schedule App")
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
        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
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
                        switch (position){
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
                .build();
    }

    /**
     * Sets up a SwipeRefreshLayout.OnRefreshListener and Refresh signal colors that is invoked when the user
     * performs a swipe-to-refresh gesture.
     */
    private void setRocketSwipeRefreshLayout() {
        launchSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // Your code to refresh the list here.
                        // Make sure you call swipeContainer.setRefreshing(false)
                        // once the network request has completed successfully.
                        // Create handle for the RetrofitInstance interface
                        // Create handle for the RetrofitInstance interface
                        GetLaunchDataService launchNextService = RetrofitInstance.getRetrofitInstance().create(GetLaunchDataService.class);

                        // Call the method with parameter in the interface to get the notice data
                        Call<LaunchNextList> launchNextCall = launchNextService.getLaunchNextListData();

                        launchNextCall.enqueue(new Callback<LaunchNextList>() {
                            @Override
                            public void onResponse(Call<LaunchNextList> call, Response<LaunchNextList> response) {
                                // Clear the rocketList to avoid duplication, if the rocket list has already populated
                                launchNextAdapter.clear();

                                recyclerView = findViewById(R.id.recycler_view_notice_list);
                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, response.body().getLaunches());

                                // Setup layout manager
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(launchNextAdapter);

                                // Remove the refresh signal after finished
                                launchSwipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(Call<LaunchNextList> call, Throwable t) {
                                // Clear the rocketList to avoid duplication, if the rocket list has already populated
                                launchNextAdapter.clear();

                                ArrayList<Launch> launchNextFailureList = new ArrayList<Launch>();

                                recyclerView = findViewById(R.id.recycler_view_notice_list);
                                launchNextAdapter = new LaunchNextAdapter(MainActivity.this, launchNextFailureList);

                                // Setup layout manager
                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(launchNextAdapter);

                                // Show Toast
                                Toast.makeText(MainActivity.this,
                                        "Unable to load the list.\nPlease check your connection"
                                        , Toast.LENGTH_LONG).show();

                                // Remove the refresh signal after finished
                                launchSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }
        );

        // Scheme colors for animation
        launchSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.secondaryLightColor)
        );

    }

}

