package com.example.android.robcket_rocketlaunchschedule.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.adapter.RocketAdapter;
import com.example.android.robcket_rocketlaunchschedule.model.Rocket;
import com.example.android.robcket_rocketlaunchschedule.model.RocketList;
import com.example.android.robcket_rocketlaunchschedule.my_interface.GetRocketDataService;
import com.example.android.robcket_rocketlaunchschedule.network.RetrofitInstance;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.stephentuso.welcome.WelcomeHelper;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private WelcomeHelper welcomeHelper;
    private RocketAdapter rocketAdapter;
    private RecyclerView recyclerView;

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

        // Welcome / OnBoard Screen Setup
        welcomeHelper = new WelcomeHelper(this, OnBoardActivity.class);
        welcomeHelper.show(savedInstanceState);

        // Create handle for the RetrofitInstance interface
        GetRocketDataService rocketService = RetrofitInstance.getRetrofitInstance().create(GetRocketDataService.class);

        // Call the method with parameter in the interface to get the notice data
        Call<RocketList> rocketCall = rocketService.getRocketData();

        rocketCall.enqueue(new Callback<RocketList>() {
            @Override
            public void onResponse(Call<RocketList> call, Response<RocketList> response) {
                generateRocketList(response.body().getRockets());
            }

            @Override
            public void onFailure(Call<RocketList> call, Throwable t) {

            }
        });

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_settings);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .build();
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
    private void generateRocketList(ArrayList<Rocket> rocketList) {
        recyclerView = findViewById(R.id.recycler_view_notice_list);
        rocketAdapter = new RocketAdapter(this, rocketList);

        // Setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rocketAdapter);
    }

}
