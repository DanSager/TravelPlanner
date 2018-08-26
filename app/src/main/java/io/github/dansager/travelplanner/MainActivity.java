package io.github.dansager.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.Trip;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {

    CreateTrip pop = new CreateTrip();

    public static List<Trip> tripList;
    public static List<Trip> upcomingTrips;
    public static List<Trip> currentTrips;
    public static List<Trip> previousTrips;
    public static RecyclerView recyclerView;
    public static SectionedRecyclerViewAdapter sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
//            //CLEARS THE LIST AND SAVES IT SHAREDPREFENCE
//            tripList.clear();
//            upcomingTrips.clear();
//            currentTrips.clear();
//            previousTrips.clear();
//            sectionAdapter.notifyDataSetChanged();
//
//            SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
//            SharedPreferences.Editor prefEditor = settings.edit();
//            Gson gson = new Gson();
//            String json = settings.getString("Trips", "");
//            json = gson.toJson(tripList);
//            prefEditor.putString("Trips", json);
//            prefEditor.commit();
//            //CLEARS THE LIST AND SAVES IT SHAREDPREFENCE

            Toast.makeText(this, "WIP",Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayTrips () {
        SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
        final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = builder.create();

        String json = settings.getString("Trips", "");

        Type type = new TypeToken<List<Trip>>(){}.getType();
         tripList = gson.fromJson(json, type);

        if (tripList == null) {
            tripList = new ArrayList<Trip>();
        }

        upcomingTrips = new ArrayList<Trip>();
        currentTrips = new ArrayList<Trip>();
        previousTrips = new ArrayList<Trip>();

        for (Trip t : tripList) {
            if (t.getStartDate().isBeforeNow() && t.getEndDate().isAfterNow()) {
                currentTrips.add(t);
            } else if (t.getStartDate().isAfterNow()) {
                upcomingTrips.add(t);
            } else if (t.getEndDate().isBeforeNow()) {
                previousTrips.add(t);
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
            if (t.getStartDate().isBeforeNow() && t.getEndDate().isAfterNow()) {
                currentTrips.add(t);
            } else if (t.getStartDate().isAfterNow()) {
                upcomingTrips.add(t);
            } else if (t.getEndDate().isBeforeNow()) {
                previousTrips.add(t);
            }
        }

        sectionAdapter.notifyDataSetChanged();
    }
}