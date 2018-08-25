package io.github.dansager.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateInfo;
import io.github.dansager.travelplanner.data_structures.Trip;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    CreateTrip pop = new CreateTrip();

    public static List<Trip> tripList;
    public static List<Trip> upcomingTrips;
    public static List<Trip> currentTrips;
    public static List<Trip> previousTrips;
    public static RecyclerView recyclerView;
    public static DateInfo today;
    public static SectionedRecyclerViewAdapter sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeSelector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTrips();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create_new_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.createDialogWindow(MainActivity.this);
            }
        });
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_stats) {
            //CLEARS THE LIST AND SAVES IT SHAREDPREFENCE
            tripList.clear();
            upcomingTrips.clear();
            currentTrips.clear();
            previousTrips.clear();
            sectionAdapter.notifyDataSetChanged();

            SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
            SharedPreferences.Editor prefEditor = settings.edit();
            Gson gson = new Gson();
            String json = settings.getString("Trips", "");
            json = gson.toJson(tripList);
            prefEditor.putString("Trips", json);
            prefEditor.commit();
            //CLEARS THE LIST AND SAVES IT SHAREDPREFENCE

            Toast.makeText(this, "WIP",Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void themeSelector() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String color = pref.getString("pref_app_color","Default");
        if (color.equals("Teal")) {
            setTheme(R.style.AppTheme);
        } else if (color.equals("Red")) {
            setTheme(R.style.RedStyle);
        } else if (color.equals("Orange")) {
            setTheme(R.style.OrangeStyle);
        } else if (color.equals("Yellow")) {
            setTheme(R.style.YellowStyle);
        } else if (color.equals("Green")) {
            setTheme(R.style.GreenStyle);
        } else if (color.equals("Blue")) {
            setTheme(R.style.BlueStyle);
        } else if (color.equals("Purple")) {
            setTheme(R.style.PurpleStyle);
        }
    }

    public void displayTrips () {
        SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
        Gson gson = new Gson();
        String json = settings.getString("Trips", "");

        Type type = new TypeToken<List<Trip>>(){}.getType();
        tripList = gson.fromJson(json, type);

        if (tripList == null) {
            tripList = new ArrayList<Trip>();
        }

        upcomingTrips = new ArrayList<Trip>();
        currentTrips = new ArrayList<Trip>();
        previousTrips = new ArrayList<Trip>();

        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String formattedMonth = monthFormat.format(c);
        String formattedDay = dayFormat.format(c);
        String formattedYear = yearFormat.format(c);
        int currentMonth = Integer.parseInt(formattedMonth);
        int currentDay = Integer.parseInt(formattedDay);
        int currentYear = Integer.parseInt(formattedYear);
        today = new DateInfo(currentMonth,currentDay,currentYear);

        for (Trip t : tripList) {
            if (today.beforeDate(t.getStartDate())) {
                upcomingTrips.add(t);
            } else if (t.getEndDate().beforeDate(today)) {
                previousTrips.add(t);
            } else {
                currentTrips.add(t);
            }
        }

        sectionAdapter = new SectionedRecyclerViewAdapter();

        sectionAdapter.addSection(new TripSection(this,"Upcoming",upcomingTrips));
        sectionAdapter.addSection(new TripSection(this,"Current",currentTrips));
        sectionAdapter.addSection(new TripSection(this,"Previous",previousTrips));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionAdapter);
    }

    public static void updateAdapter (List<Trip> updatedListTrip) {
        tripList.clear();
        tripList.addAll(updatedListTrip);

        upcomingTrips.clear();
        currentTrips.clear();
        previousTrips.clear();

        for (Trip t : tripList) {
            if (today.beforeDate(t.getStartDate())) {
                upcomingTrips.add(t);
            } else if (t.getEndDate().beforeDate(today)) {
                previousTrips.add(t);
            } else {
                currentTrips.add(t);
            }
        }

        sectionAdapter.notifyDataSetChanged();
    }
}